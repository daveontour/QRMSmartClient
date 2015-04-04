package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.birt.core.archive.FileArchiveWriter;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IPDFRenderOption;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IRunTask;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.hibernate.Session;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.DTOSearchParam;
import au.com.quaysystems.qrm.dto.ModelJobQueue;
import au.com.quaysystems.qrm.dto.ModelQRMReport;
import au.com.quaysystems.qrm.dto.ModelRepository;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.server.BirtEngineFactory;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.QRMAsyncMessage;
import au.com.quaysystems.qrm.server.QRMTXManager;
import au.com.quaysystems.qrm.server.report.IReportProcessorVisitor;
import au.com.quaysystems.qrm.server.report.ReportProcessorData;

public class ReportProcessor {
	private QRMTXManager txmgr = new QRMTXManager();
	private static Properties configProp = new Properties();
	private Logger log = Logger.getLogger("au.com.quaysystems.qrm");

	public ReportProcessor(Properties props) {
		configProp = props;
	}
	public void deliver(HashMap<String, Object> dataMap) {

		ReportProcessorData job = (ReportProcessorData) dataMap.get("job");
		Session sess = PersistenceUtil.getSession(job.jdbcURL);

		try  {
			dispatchReportJob(job, sess);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sess.close();
		}
	}

	final public void dispatchReportJob( ReportProcessorData jobData, Session sess)  {

		HashMap<Object, Object> taskParamMap = jobData.taskParamMap;  // 

		ModelQRMReport rep = (ModelQRMReport) sess.get(ModelQRMReport.class, jobData.reportID);

		Session sess2 = PersistenceUtil.getSimpleControlSession();
		@SuppressWarnings("unchecked")
		List<ModelRepository> reps = (List<ModelRepository>)sess2.createSQLQuery("SELECT * FROM repositories WHERE url = '"+jobData.jdbcURL+"'").addEntity(ModelRepository.class).list();
		sess2.close();

		taskParamMap.put("repID", reps.get(0).repID);
		taskParamMap.put("userID", jobData.userID);
		taskParamMap.put("jobID", jobData.jobID);
		taskParamMap.put("JDBCURL", jobData.jdbcURL);
		taskParamMap.put("JOBFORMAT", jobData.format);
		taskParamMap.put("USER", jobData.userID);
		taskParamMap.put("JOBID", jobData.jobID);
		if (rep != null){
			taskParamMap.put("REPDETAILCONFIGWINDOW", rep.detailConfigWindow);
			taskParamMap.put("REPCOREFILE", false);
			taskParamMap.put("REPFILENAME", rep.getReportFileName());
			taskParamMap.put("REPBODYTEXT", rep.getBodyText());
		}

		ModelJobQueue jobRec = null;
		Long reportSessionID = null;

		jobRec = (ModelJobQueue) sess.get(ModelJobQueue.class, jobData.jobID);
		jobRec.setReadyToExecute(false);
		jobRec.setProcessing(true);
		sess.beginTransaction();
		sess.save(jobRec);
		sess.getTransaction().commit();
		try {
			ServletUserMessageManager.notifyReportStatus(jobData.userID);
		} catch (IOException e) {
			e.printStackTrace();
		}


		// special handling for export of risks
		if (jobData.reportID == QRMConstants.EXPORTRISKSXML ){
			reportSessionID = getReportSessionID(jobData.userID,jobData.reportID,PersistenceUtil.getConnection(jobData.jdbcURL));
			exportRisks(jobData, taskParamMap,sess, jobRec, reportSessionID);
			return;
		}

		reportSessionID = getReportSessionID(jobData.userID,rep.getInternalID(),PersistenceUtil.getConnection(jobData.jdbcURL));

		if(taskParamMap.containsKey("projectRisk")){
			addProjectRiskIDS(taskParamMap,jobData, reportSessionID,  PersistenceUtil.getConnection(jobData.jdbcURL));
		}

		if(taskParamMap.containsKey("registerReport")){
			addRegisterRiskIDS(taskParamMap,jobData, reportSessionID,  PersistenceUtil.getConnection(jobData.jdbcURL));
		}


		if (taskParamMap.containsKey("riskids")) {
			processRiskIDs(taskParamMap, reportSessionID, false, PersistenceUtil.getConnection(jobData.jdbcURL));
		}
		if (taskParamMap.containsKey("elementIDs")) {
			processElementIDs(taskParamMap, reportSessionID, PersistenceUtil.getConnection(jobData.jdbcURL));
		}

		taskParamMap.put("sessionID", reportSessionID);

		if (rep.visitor != null){
			try {
				((IReportProcessorVisitor)Class.forName(rep.visitor).newInstance()).process(jobData, taskParamMap,  sess, reportSessionID, new QRMTXManager() );
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}
		}

		for (Object key :taskParamMap.keySet()){
			if (taskParamMap.get(key) instanceof Long){
				taskParamMap.put(key, ((Long)taskParamMap.get(key)).intValue());
			}
		}

		jobData.taskParamMap = taskParamMap;
		jobData.readyToProcess = true;

		sess.beginTransaction();
		sess.saveOrUpdate(jobData);
		sess.getTransaction().commit();

		// Process the job

		byte[] bytes;
		if (rep.bodyText == null){
			bytes = null;
		} else {
			bytes = rep.bodyText.getBytes();
		}

		try {
			runTask(jobData.userID, taskParamMap, jobData.jobID, jobData.format, rep.reportFileName, rep.coreFile, bytes);
		} catch (EngineException e) {
			e.printStackTrace();
		}

		// Let the user know that the report status has been updated
		try {
			ServletUserMessageManager.notifyReportStatus(jobData.userID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	final private void exportRisks(ReportProcessorData job, HashMap<Object, Object> taskParamMap, Session sess, ModelJobQueue jobRec, Long reportSessionID) {

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Connection conn = PersistenceUtil.getConnection(job.jdbcURL);

		try {
			jobRec.setUserID(job.userID);
			jobRec.setState("Processing");
			jobRec.setProcessing(true);
			jobRec.setJobJdbcURL(job.jdbcURL);
			jobRec.setJobDescription(job.description);
			if (jobRec.getJobDescription() == null){
				jobRec.setJobDescription("Risk Export");
			}
			jobRec.setJobType("REPORT");
			jobRec.setReadyToExecute(false);
			jobRec.setProcessing(true);
			if (job.reportID == QRMConstants.EXPORTRISKSEXCEL){
				jobRec.setReportFormat("xls");
				jobRec.downloadOnly = true;
				jobRec.setJobDescription("Risk Export (Excel)");
			}
			if (job.reportID == QRMConstants.EXPORTRISKSXML){
				jobRec.setReportFormat("xml");
				jobRec.downloadOnly = true;
				jobRec.setJobDescription("Risk Export (XML)");
			}
			sess.beginTransaction();
			sess.save(jobRec);
			sess.getTransaction().commit();
			sess.refresh(jobRec);

			ServletUserMessageManager.notifyReportStatus(job.userID);
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}


		ArrayList<ModelRisk> risks = new ArrayList<ModelRisk>();
		long rootProjectID = PersistenceUtil.getRootProjectID(job.jdbcURL);
		if (job.projectID == null || job.projectID == 0){
			job.projectID = (Long)taskParamMap.get("projectID");
		}
		for (ModelRisk risk : txmgr.getAllProjectRisks(job.userID, job.projectID, (Boolean)taskParamMap.get("descendants"), sess)){
			risks.add(txmgr.getRisk(risk.riskID, 1L, rootProjectID, sess));
		}

		if (job.reportID == QRMConstants.EXPORTRISKSXML){
			ServletExportMSFormats.exportAsXML(os, new ArrayList<ModelRisk>(risks), null);
		}

		byte b[] = os.toByteArray();
		int sizeInBytes = b.length;
		ByteArrayInputStream is2 = new ByteArrayInputStream(b);


		try {
			conn.setAutoCommit(true);
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO jobresult (jobID, resultStr) VALUES (?,?)");
			stmt.setLong(1, job.jobID);
			stmt.setBinaryStream(2,is2, sizeInBytes);
			stmt.execute();
			conn.close();
		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
			jobRec.setState("Failed");
			jobRec.setProcessing(false);
			jobRec.setReadyToExecute(false);
			jobRec.setReadyToCollect(false);
			jobRec.setFailed(true);
			jobRec.setExecutedDate(new Date());
			job.failed = true;

			sess.beginTransaction();
			sess.save(jobRec);
			sess.getTransaction().commit();

		}

		if (job.emailAttachment){
			try {
				job.processed = true;
				sess.beginTransaction();
				sess.saveOrUpdate(job);
				sess.getTransaction().commit();
				new QRMAsyncMessage(QRMConstants.EMAIL_MSG, job, true).send();
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}
		}

		jobRec.setProcessing(false);
		jobRec.setReadyToExecute(false);
		jobRec.setReadyToCollect(true);
		jobRec.setExecutedDate(new Date());
		jobRec.downloadOnly = true;

		sess.beginTransaction();
		sess.save(jobRec);
		sess.getTransaction().commit();

		try {
			setReportSessionComplete(reportSessionID, PersistenceUtil.getConnection(job.jdbcURL));
			ServletUserMessageManager.notifyReportStatus(job.userID);
		} catch (IOException e) {
			log.error("QRM Stack Trace", e);
		}
	}
	final private Long getReportSessionID(final Long userID, final Long reportID, final Connection conn) {
		try {
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO reportsession (user_id, reportName) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);

			pstmt.setLong(1, userID);
			pstmt.setString(2, reportID.toString());
			pstmt.executeUpdate();
			ResultSet keys = pstmt.getGeneratedKeys();

			keys.next();
			return keys.getLong(1);

		} catch (SQLException e) {
			return null;
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("QRM Stack Trace", e);
			}
		}
	}
	final private void processElementIDs(final HashMap<Object, Object> taskParamMap, final Long reportSessionID, final Connection conn) {
		int rank = 0;
		for (String idStr : ((String) taskParamMap.get("elementIDs")).split(",")) {
			setReportSessionData(reportSessionID, "elementID", Long.parseLong(idStr), null, rank++, conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		}
	}
	final private void processRiskIDs(final HashMap<Object, Object> taskParamMap, final Long reportSessionID, final boolean bMat, final Connection conn) {
		int rank = 0;
		for (String idStr : ((String) taskParamMap.get("riskids")).split(",")) {
			setReportSessionData(reportSessionID, "RISK",  Long.parseLong(idStr), null, rank++, conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		}
	}

	final private void addProjectRiskIDS(HashMap<Object, Object> taskParamMap, ReportProcessorData jobData, Long reportSessionID, Connection conn) {

		Long projectID; 
		try {
			projectID = (Long)taskParamMap.get("projectID");
		} catch (Exception e1) {
			projectID = new Long((Integer)taskParamMap.get("projectID"));
		}
		Boolean desc = (Boolean)taskParamMap.get("descendants");
		int rank = 0;

		for (Long riskID: getProjectRiskIDs(conn, projectID, desc, jobData.userID)) {			
			setReportSessionData(reportSessionID, "RISK",  riskID, null, rank++, conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		}

	}

	private void addRegisterRiskIDS(HashMap<Object, Object> taskParamMap,
			ReportProcessorData jobData, Long reportSessionID,
			Connection conn) {


		DTOSearchParam searchParam = SearchStringHelper.parseSearch(taskParamMap);
		String sql = SearchStringHelper.getSQLSearchString(searchParam);

		ArrayList<Long> risks = new ArrayList<Long>();

		try  {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()){
				risks.add(rs.getLong("riskID"));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} 

		int rank = 0;
		for (Long riskID: risks) {			
			setReportSessionData(reportSessionID, "RISK",  riskID, null, rank++, conn);
		}
		try {
			conn.close();
		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		}

	}


	final private ArrayList<Long> getProjectRiskIDs(Connection conn, Long projectID, Boolean desc, Long userID) {



		String sql = "SELECT risk.*,   p1.projectCode AS fromProjCode, p2.projectCode AS toProjCode  FROM risk "+
				"LEFT OUTER JOIN riskproject AS p1 ON p1.projectID = risk.projectID  " +
				"LEFT OUTER JOIN riskproject AS p2 ON p2.projectID = risk.promotedProjectID  ";

		if (desc){
			sql = sql + " WHERE (risk.projectID IN (SELECT subprojectID  FROM subprojects WHERE projectID = "+projectID+"  UNION SELECT "+projectID+") OR (risk.promotedProjectID IN (SELECT superprojectID  FROM superprojects WHERE projectID = "+projectID+"  UNION SELECT "+projectID+") AND risk.projectID IN (SELECT subprojectID  FROM subprojects WHERE projectID = "+projectID+"  UNION SELECT "+projectID+") ) OR risk.promotedProjectID = "+projectID+" )"+
					" AND checkRiskSecurityView(risk.riskID, "+userID+", risk.securityLevel, risk.projectID)";
		} else {
			sql = sql + " WHERE  (risk.promotedProjectID IN (SELECT superprojectID  FROM superprojects WHERE projectID = "+projectID+" AND risk.projectID IN (SELECT subprojectID  FROM subprojects WHERE projectID = "+projectID+") )  OR risk.promotedProjectID = "+projectID+" OR risk.projectID = "+projectID+") AND checkRiskSecurityView(risk.riskID, "+userID+", risk.securityLevel, risk.projectID)";
		}

		ArrayList<Long> risksIDs = new ArrayList<Long>();

		try  {
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()){
				risksIDs.add(rs.getLong("riskID"));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} 
		return  risksIDs;
	}


	final private void setReportSessionComplete(final Long sessionID, final Connection conn) {
		try {

			PreparedStatement pstmt = conn.prepareStatement("DELETE FROM reportsession WHERE sessionID = ?");
			pstmt.setLong(1, sessionID);
			pstmt.executeUpdate();

			conn.close();
		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		}
	}
	final public void setReportSessionData(final long var_reportSessionID,
			final String var_dataElement, final long var_dataID,
			final String var_dataString, final int var_rank, final Connection conn) {

		try {

			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO reportsessiondata (sessionID ,dataElement ,dataID,dataString, rank) VALUES (?,?,?,?,? )");

			pstmt.setLong(1, var_reportSessionID);
			pstmt.setString(2, var_dataElement);
			pstmt.setLong(3, var_dataID);
			pstmt.setString(4, var_dataString);
			pstmt.setInt(5, var_rank);

			pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		}
	}

	@SuppressWarnings("unchecked")
	private boolean runTask(Long userID, HashMap<Object, Object> taskParamMap, Long jobID, String jobFormat,  String reportFileName, boolean repCoreFile, byte[] repBody) throws EngineException {

		try {

			Connection conn = PersistenceUtil.getConnection((String ) taskParamMap.get("JDBCURL"));
			conn.setAutoCommit(true);

			InputStream is = null;

			if (repCoreFile) {
				try {
					String file = configProp.getProperty("REPORT_PATH")+ reportFileName+ ".rptdesign";
					file = file.replace("\\", "/");
					is = new FileInputStream(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return false;
				}
			} else {
				is = new ByteArrayInputStream(repBody);
			}

			IReportEngine engine = BirtEngineFactory.getInstance(configProp);

			IRunTask task = engine.createRunTask(engine.openReportDesign(is));
			task.setParameterValues(taskParamMap);
			task.getAppContext().put("OdaJDBCDriverPassInConnection", conn);
			//			task.setProgressMonitor(new IProgressMonitor(){
			//				@Override
			//				public void onProgress(int arg0, int arg1) {
			//					log.info("BIRT RUN PROGRESS MESSAGE "+arg0);
			//
			//			}});

			FileArchiveWriter jobArchive = new FileArchiveWriter(configProp.getProperty("REPORT_TEMP_DIR")+"ReportJob"+jobID);

			try {
				task.run(jobArchive);
				conn.close();
			} catch (Exception e3) {
				e3.printStackTrace();
				try (Connection conn2 = PersistenceUtil.getConnection((String ) taskParamMap.get("JDBCURL"))){
					conn2.setAutoCommit(true);
					PreparedStatement stmt3 = conn2.prepareStatement("UPDATE jobqueue SET state = 'Failed', processing = 0, readyToExecute = 0, readyToCollect = 0, failed = 1, executedDate = NOW()  WHERE jobID = ?");
					stmt3.setLong(1, jobID);
					stmt3.executeUpdate();
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				return false;
			}

		} catch (Exception e2) {
			e2.printStackTrace();
		}

		// Save the result 
		try (Connection conn = PersistenceUtil.getConnection((String ) taskParamMap.get("JDBCURL"))){

			conn.setAutoCommit(true);

			PreparedStatement stmt2 = conn.prepareStatement("UPDATE reportdata SET readyToProcess = 0, processed = 1 WHERE jobID = ?");
			stmt2.setLong(1, jobID);
			stmt2.executeUpdate();

			PreparedStatement stmt3 = conn.prepareStatement("UPDATE jobqueue SET state = 'Completed', processing = 0, readyToExecute = 0, readyToCollect = 1, executedDate = NOW()  WHERE jobID = ?");
			stmt3.setLong(1, jobID);
			stmt3.executeUpdate();

			PreparedStatement stmt4 = conn.prepareStatement("DELETE FROM reportsession WHERE sessionID = ?");
			stmt4.setLong(1, new Long((Integer)taskParamMap.get("sessionID")));
			stmt4.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean renderTask( Long jobID, HttpServletResponse response,HttpServletRequest request, ServletConfig ctx,boolean download, String format)  {

		if (download){
			response.setHeader("Content-Disposition", "attachment; filename=Report Job ID "+jobID);
		}

		IRenderOption options = new RenderOption();		

		switch (format){
		case "html":
			response.setContentType("text/html");
			options.setOutputFormat("html");
			options.setEmitterID("org.eclipse.birt.report.engine.emitter.html");
			break;
		case "doc":
			response.setContentType("application/msword");
			options.setOutputFormat("doc");
			options.setEmitterID("org.eclipse.birt.report.engine.emitter.word");
			response.setHeader("Content-Disposition", "attachment; filename=Report Job ID"+jobID+".doc");
			break;
		case "docx":
			response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
			options.setOutputFormat("docx");
			options.setEmitterID("org.eclipse.birt.report.engine.emitter.docx");
			response.setHeader("Content-Disposition", "attachment; filename=Report Job ID"+jobID+".docx");
			break;
		case "xls":
			response.setContentType("application/vnd.ms-excel");
			options.setOutputFormat("xls");
			options.setEmitterID("org.eclipse.birt.report.engine.emitter.prototype.excel");
			response.setHeader("Content-Disposition", "attachment; filename=Report Job ID"+jobID+".xls");
			break;
		case "xlsx":
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			options.setOutputFormat("xlsx");
			options.setEmitterID("uk.co.spudsoft.birt.emitters.excel.XlsxEmitter");
			response.setHeader("Content-Disposition", "attachment; filename=Report Job ID"+jobID+".xlsx");
			break;
		case "ppt":
			response.setContentType("application/vnd.ms-powerpoint");
			options.setOutputFormat("ppt");
			options.setEmitterID("org.eclipse.birt.report.engine.emitter.ppt");
			response.setHeader("Content-Disposition", "attachment; filename=Report Job ID"+jobID+".ppt");
			break;
		case "pptx":
			response.setContentType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
			options.setOutputFormat("pptx");
			options.setEmitterID("org.eclipse.birt.report.engine.emitter.pptx" );
			response.setHeader("Content-Disposition", "attachment; filename=Report Job ID"+jobID+".pptx");
			break;
		case "pdf":
		default:
			response.setContentType("application/pdf");
			options.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_PDF);
			options.setEmitterID("org.eclipse.birt.report.engine.emitter.pdf" );
			options.setOption(IPDFRenderOption.PAGE_OVERFLOW, IPDFRenderOption. OUTPUT_TO_MULTIPLE_PAGES);
			if (download){
				response.setHeader("Content-Disposition", "attachment; filename=Report Job ID"+jobID+".pdf");
			}

		}

		try (OutputStream out = response.getOutputStream()) {

			options.setOutputStream(out);

			IReportEngine engine = BirtEngineFactory.getInstance(configProp);					
			IReportDocument iReportDocument = engine.openReportDocument(configProp.getProperty("REPORT_TEMP_DIR")+"ReportJob"+jobID);
			IRenderTask task = engine.createRenderTask(iReportDocument);

			task.setRenderOption(options);

			//			task.setProgressMonitor(new IProgressMonitor(){
			//				@Override
			//				public void onProgress(int arg0, int arg1) {
			//					log.info("BIRT RENDER PROGRESS MESSAGE "+arg0);
			//				}});			
			try {
				task.render();
			} catch (Exception e3) {
				e3.printStackTrace();
				ctx.getServletContext().getRequestDispatcher("/ReportError.jsp").forward(request,response);
				return false;
			} finally {
				iReportDocument.close();
			}			
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return true;
	}

}
