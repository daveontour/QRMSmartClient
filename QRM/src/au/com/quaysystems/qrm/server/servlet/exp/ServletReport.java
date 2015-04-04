package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.ModelJobQueue;
import au.com.quaysystems.qrm.dto.ModelQRMReport;
import au.com.quaysystems.qrm.server.QRMAsyncMessage;
import au.com.quaysystems.qrm.server.report.ReportProcessorData;

@WebServlet(urlPatterns = {"/report"})
public class ServletReport extends QRMRPCServlet {

	private static final long serialVersionUID = -9132125136556025801L;
	private ReportProcessor rp;
	private ServletConfig ctx;

	public ServletReport() {
		super();
	}

	@Override
	public void init(final ServletConfig sc) {
		super.init(sc);
		this.ctx = sc;
		rp = new ReportProcessor(configProp);
	}

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {



		String action = stringMap.get("action");
		if (action == null){
			action = "NO_ACTION";
		}
		

		Long jobID;
		
		switch (action.toLowerCase()){
		case "submit":
		case "submitandexecute":			
			log.info(" submit ");
			jobID = prepareReportRequest( request,  response,  sess, userID,  stringMap, objMap);
			try {
				response.getWriter().print(jobID);
			} catch (IOException e) {
				e.printStackTrace();
			}
			executeJob(jobID, sess);
			break;			
		case "execute":
			executeJob(Long.parseLong(stringMap.get("jobID")), sess);
			break;
		case "submitexecuterender":			
			log.info(" submitandexecute ");
			jobID = prepareReportRequest( request,  response,  sess, userID,  stringMap, objMap);
			executeJob(jobID, sess);
			rp.renderTask(jobID,response,request,ctx,false,"pdf");
			break;
		case "status":
			log.info("Report Servlet Request - status ");
			break;
		case "download":			
			rp.renderTask( Long.parseLong(stringMap.get("jobID")),response,request,ctx,true,stringMap.get("format") );
			break;
		case "view":			
			rp.renderTask( Long.parseLong(stringMap.get("jobID")),response,request,ctx,false,"pdf");
			break;
		case "retrieve":
			log.info("Report Servlet Request - retrieve ");
			break;
		default:
			log.info("Report Servlet Request - malformed ");
		}

	}
	
	protected final Long prepareReportRequest(final HttpServletRequest request, final HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap,HashMap<Object,Object> objMap) {

		// Parse the input 
		
		HashMap<Object, Object> taskParamMap = new HashMap<Object, Object>();

		JSONArray keys = null;
		JSONArray vals = null;
		String riskIDs = null;


		try {
			JSONObject input = (JSONObject) parser.parse(request.getParameter("DATA"));
			keys = (JSONArray)input.get("keyArray");
			vals = (JSONArray)input.get("valArray");
		} catch (Exception e2) {
			e2.printStackTrace();
			return null;
		}

		for (int i=0; i < keys.size(); i++){
			String key = (String)keys.get(i);
			Object val = vals.get(i);
			if(key.startsWith("riskID")){
				if (riskIDs == null){
					riskIDs = val.toString();
				} else {
					riskIDs = riskIDs+","+val;
				}
			} else {
				taskParamMap.put(key,val);
			}
		}

		if (riskIDs != null){
			taskParamMap.put("riskids",riskIDs);
		}

		Long repID = getLongJS(taskParamMap.get("reportID"));


		if (repID == QRMConstants.EXPORTRISKSEXCEL ){
			taskParamMap.put("QRMREPORTDESC", "Risk Export (Excel)");
		} else if (repID == QRMConstants.EXPORTRISKSXML){
			taskParamMap.put("QRMREPORTDESC", "Risk Export (XML)");			
		} else {
			taskParamMap.put("QRMREPORTDESC", ((ModelQRMReport) sess.get(ModelQRMReport.class, repID)).reportDescription);
		}
		
		return prepareReportJob(taskParamMap, sess, request, userID, repID);

	}

	
	protected final Long prepareReportJob(final HashMap<Object, Object> taskParamMap, Session sess, HttpServletRequest request, Long userID, Long repID) {

		Long jobID;

		try {

			// This is the record of the job
			ModelJobQueue jobRec = new ModelJobQueue();
			jobRec.setUserID(userID);
			jobRec.setJobJdbcURL((String) (request.getSession().getAttribute("session.url")));
			jobRec.setJobDescription((String)taskParamMap.get("QRMREPORTDESC"));
			jobRec.setJobType("REPORT");
			jobRec.setReadyToExecute(true);
			jobRec.setReportFormat((String)taskParamMap.get("format"));
			jobRec.setQueuedDate(new Date());

			sess.beginTransaction();
			jobID = (Long) sess.save(jobRec);
			sess.refresh(jobRec);
			sess.getTransaction().commit();
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			return null;
		}

		ReportProcessorData jobData = new ReportProcessorData();

		try {
			//This is the data of the job
			jobData.taskParamMap = taskParamMap;
			jobData.userID = userID;
			jobData.jdbcURL = (String)request.getSession().getAttribute("session.url");
			jobData.schedJob = false;
			jobData.sendStatusUpdates = true;
			jobData.format = (String)taskParamMap.get("format");
			jobData.jobID = jobID;
			jobData.reportID = repID;

			sess.beginTransaction();
			sess.saveOrUpdate(jobData);
			sess.getTransaction().commit();

		} catch (Exception e) {
			log.error(e);
			return null;
		} 

		return jobID;
	}
	
	public boolean executeJob(Long jobID, Session sess){
		
		if (jobID == null){
			handleError();
			return false;
		}
		
		
		// The PostLoadEventListener converts the taskParamMap from a string to the HashMap
		ReportProcessorData job =(ReportProcessorData) sess.get(ReportProcessorData.class, jobID);
		
		if (job == null){
			handleError();
			return false;
		}
		
		HashMap<String, Object> data = new HashMap<>();
		data.put("job", job);
		
		try {
			QRMAsyncMessage message = new QRMAsyncMessage(QRMConstants.REPORT_MSG, data);
			message.send();
			log.info("Dispatched Report Job");
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
	
		return true;
	}

	private void handleError() {
		// TODO Auto-generated method stub
		
	}
	
//	final private ArrayList<Long> getProjectRiskIDs(HttpServletRequest request, Session sess, HashMap<Object, Object> objMap, HashMap<String, String> stringMap, Long userID) {
//
//
//		String var_projectID = stringMap.get("PROJECTID");
//
//		String sql = "SELECT risk.*,   p1.projectCode AS fromProjCode, p2.projectCode AS toProjCode  FROM risk "+
//				"LEFT OUTER JOIN riskproject AS p1 ON p1.projectID = risk.projectID  " +
//				"LEFT OUTER JOIN riskproject AS p2 ON p2.projectID = risk.promotedProjectID  ";
//				
//		if (Boolean.parseBoolean(stringMap.get("DESCENDANTS"))){
//			sql = sql + " WHERE (risk.projectID IN (SELECT subprojectID  FROM subprojects WHERE projectID = "+var_projectID+"  UNION SELECT "+var_projectID+") OR (risk.promotedProjectID IN (SELECT superprojectID  FROM superprojects WHERE projectID = "+var_projectID+"  UNION SELECT "+var_projectID+") AND risk.projectID IN (SELECT subprojectID  FROM subprojects WHERE projectID = "+var_projectID+"  UNION SELECT "+var_projectID+") ) OR risk.promotedProjectID = "+var_projectID+" )"+
//					" AND checkRiskSecurityView(risk.riskID, "+userID+", risk.securityLevel, risk.projectID)";
//		} else {
//			sql = sql + " WHERE  (risk.promotedProjectID IN (SELECT superprojectID  FROM superprojects WHERE projectID = "+var_projectID+" AND risk.projectID IN (SELECT subprojectID  FROM subprojects WHERE projectID = "+var_projectID+") )  OR risk.promotedProjectID = "+var_projectID+" OR risk.projectID = "+var_projectID+") AND checkRiskSecurityView(risk.riskID, "+userID+", risk.securityLevel, risk.projectID)";
//		}
//
//		ArrayList<Long> risksIDs = new ArrayList<Long>();
//
//		try (Connection conn = PersistenceUtil.getConnection((String) request.getSession().getAttribute("session.url"))) {
//			PreparedStatement stmt = conn.prepareStatement(sql);
//			ResultSet rs = stmt.executeQuery();
//			while (rs.next()){
//				risksIDs.add(rs.getLong("riskID"));
//			}
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		} 
//		return  risksIDs;
//	}
}
