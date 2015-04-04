package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.SimpleTimeZone;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.json.simple.parser.JSONParser;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.DTOAnalysisConfigElements;
import au.com.quaysystems.qrm.dto.DTOObjectiveImpacted;
import au.com.quaysystems.qrm.dto.IModelRiskLite;
import au.com.quaysystems.qrm.dto.IModelRiskLiteBasic;
import au.com.quaysystems.qrm.dto.ModelAuditComment;
import au.com.quaysystems.qrm.dto.ModelDataObjectAllocation;
import au.com.quaysystems.qrm.dto.ModelImportTemplate;
import au.com.quaysystems.qrm.dto.ModelIncident;
import au.com.quaysystems.qrm.dto.ModelIncidentConsequence;
import au.com.quaysystems.qrm.dto.ModelIncidentUpdate;
import au.com.quaysystems.qrm.dto.ModelJobQueue;
import au.com.quaysystems.qrm.dto.ModelMitigationStep;
import au.com.quaysystems.qrm.dto.ModelMultiLevel;
import au.com.quaysystems.qrm.dto.ModelObjective;
import au.com.quaysystems.qrm.dto.ModelPerson;
import au.com.quaysystems.qrm.dto.ModelQRMReport;
import au.com.quaysystems.qrm.dto.ModelQuantImpactType;
import au.com.quaysystems.qrm.dto.ModelRepository;
import au.com.quaysystems.qrm.dto.ModelReview;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskConsequence;
import au.com.quaysystems.qrm.dto.ModelRiskControl;
import au.com.quaysystems.qrm.dto.ModelRiskLite;
import au.com.quaysystems.qrm.dto.ModelRiskLiteBasic;
import au.com.quaysystems.qrm.dto.ModelRiskProject;
import au.com.quaysystems.qrm.dto.ModelToleranceDescriptors;
import au.com.quaysystems.qrm.dto.ModelToleranceMatrix;
import au.com.quaysystems.qrm.dto.ModelUpdateComment;
import au.com.quaysystems.qrm.dto.QRMExportProject;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.RESTTransportContainer;
import au.com.quaysystems.qrm.server.servlet.SessionControl;
import au.com.quaysystems.qrm.server.servlet.SessionEntry;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;

public abstract class QRMRPCServlet extends HttpServlet {

	public static long numCalls = 0;
	protected static final Long oneMB = 1048576L;
	private final static long serialVersionUID = -7408422743586821213L;
	protected static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	public static int SESSIONTIMEOUT = 20;
	protected static HashMap<String, String[]> orgURLMap = new HashMap<String, String[]>();
	protected boolean disableNewSessions = false;
	protected int limitSessions = 20;
	protected XStream xsXML = new XStream();
	protected SimpleDateFormat formatUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected XStream xsJSON = new XStream(new JsonHierarchicalStreamDriver() {
		@Override
		public HierarchicalStreamWriter createWriter(final Writer writer) {
			return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
		}
	});
	protected XStream xsJSONRiskLite = new XStream(new JsonHierarchicalStreamDriver() {
		@Override
		public HierarchicalStreamWriter createWriter(final Writer writer) {
			return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
		}
	}
			);
	protected XStream xsJSON2 = new XStream(new JsonHierarchicalStreamDriver() {
		@Override
		public HierarchicalStreamWriter createWriter(final Writer writer) {
			return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
		}
	});
	protected JSONParser parser = new JSONParser();

	protected Properties configProp = new Properties();
	protected static Logger log = Logger.getLogger("au.com.quaysystems.qrm");
	protected String servlet;

	public boolean qrmTracing;
	protected ServletConfig sc;

	public void doPost(final HttpServletRequest request, final HttpServletResponse response)  {
		doGet(request, response);
	}
	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response)  {

		HttpSession httpSess = ((HttpServletRequest) request).getSession();

		Date nowDate = new Date();
		Long now = new Date().getTime();


		// Record 
		httpSess.setAttribute("LAST_QRM_TRANSACTION", now);
		try {
			ServletUserMessageManager.sessionMemberMap.get(httpSess.getId()).lastRequest = now;
		} catch (Exception e) {
			// Nothing to worry about
		}
		SessionControl.sessionMap.get(httpSess.getId()).lastAccess = nowDate;
		SessionControl.sessionMap.get(httpSess.getId()).numTransactions++;


		HashMap<String, SessionEntry> x1 = SessionControl.sessionMap;

		String x2 = request.getSession().getId();
		SessionEntry x3 = x1.get(x2);

		if (x3 == null){
			System.out.println("Request with Invalid Session");
			return;
		}
		ModelPerson x4 = x3.person;
		Long userID = x4.getStakeholderID();

		//		Long userID = SessionControl.sessionMap.get(request.getSession().getId()).person.getStakeholderID();
		Long projectID;
		Long riskID;
		HashMap<String, String> stringMap = new HashMap<>();
		HashMap<Object, Object> objMap = new HashMap<>();


		log.info(servlet);


		for (Object key : request.getParameterMap().keySet()) {

			stringMap.put((String) key,	request.getParameter(key.toString()));

			if (request.getParameter(key.toString()).equalsIgnoreCase("true")) {
				objMap.put(key, new Boolean(true));
			} else if (request.getParameter(key.toString()).equalsIgnoreCase("false")) {
				objMap.put(key, new Boolean(false));
			} else {
				objMap.put(key,	request.getParameter(key.toString()));
			}

		}

		try {
			projectID = Long.parseLong(stringMap.get("PROJECTID"));
		} catch (Exception e3) {
			projectID = null;
		}

		try {
			riskID = Long.parseLong(stringMap.get("RISKID"));
		} catch (Exception e3) {
			riskID = null;
		}


		response.setHeader("Cache-Control", "no-cache");

		// Invoke the requested method
		Session sess = null;
		try  {

			sess = PersistenceUtil.getSession((String) request.getSession().getAttribute("session.url"));
			sess.setCacheMode(CacheMode.IGNORE);
			sess.setFlushMode(FlushMode.ALWAYS);

			if (stringMap.get("GETSTATS") != null){
				getStats(response);
			} else if (stringMap.get("RESETSTATS") != null){
				resetStats(response);
			} else {
				numCalls++;
				execute( request, response, sess, userID, stringMap, objMap, projectID, riskID);
			}

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} finally {
			try {
				sess.close();
			} catch (Exception e) {
				// Do Nothing. May have been closed earlier
			}
		}
	}

	public void getStats(HttpServletResponse response){
		try {
			response.getWriter().print(numCalls);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void resetStats(HttpServletResponse response){
		try {
			numCalls = 0;
			response.getWriter().print("OK");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	abstract void execute(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID, Long riskID);

	@Override
	public void init(final ServletConfig sc) {

		this.sc = sc;

		String[] servletStr = sc.getServletName().split("\\.");
		servlet = servletStr[servletStr.length-1];
		log.info("Starting "+ servlet);


		formatUTC.setCalendar(Calendar.getInstance(new SimpleTimeZone(0, "UTC")));

		xsXML.alias("response", RESTTransportContainer.class);

		xsXML.alias("record", DTOAnalysisConfigElements.class);
		xsXML.alias("record", ModelRiskProject.class);
		xsXML.alias("record", ModelRiskLite.class);
		xsXML.alias("record", ModelObjective.class);
		xsXML.alias("record", ModelQRMReport.class);
		xsXML.alias("record", ModelReview.class);

		xsXML.omitField(ModelRiskLite.class,"cause");
		xsXML.omitField(ModelRiskLite.class,"consequences");
		xsXML.omitField(ModelRiskLite.class,"extObject");

		xsJSON.alias("person", ModelPerson.class);
		xsJSON.alias("project", ModelRiskProject.class);
		xsJSON.alias("matrix", ModelToleranceMatrix.class);
		xsJSON.alias("risk", ModelRiskLite.class);
		xsJSON.alias("risk", ModelRisk.class);
		xsJSON.alias("mitplanstep", ModelMitigationStep.class);
		xsJSON.alias("riskcontrol", ModelRiskControl.class);
		xsJSON.alias("data", RESTTransportContainer.class);

		xsJSON.omitField(ModelMitigationStep.class, "dateEntered");
		xsJSON.omitField(ModelMitigationStep.class, "dateUpdated");
		xsJSON.omitField(ModelAuditComment.class, "attachment");

		xsJSON.omitField(ModelJobQueue.class, "jobJdbcURL");

		xsJSON.omitField(ModelRiskLite.class,"cause");
		xsJSON.omitField(ModelRiskLite.class,"consequences");
		xsJSON.omitField(ModelRiskLite.class,"extObject");


		// For security reasons
		xsJSON.omitField(ModelRepository.class, "orgID");
		// xsJSON.omitField(ModelRepository.class, "url");

		xsJSON2.omitField(ModelRiskProject.class, "dateEntered");
		xsJSON2.omitField(ModelRiskProject.class, "dateUpdated");
		xsJSON2.omitField(ModelJobQueue.class, "queuedDate");
		xsJSON2.omitField(ModelJobQueue.class, "executedDate");
		xsJSON2.omitField(ModelJobQueue.class, "collectedDate");
		xsJSON2.omitField(ModelJobQueue.class, "startDate");
		xsJSON2.omitField(ModelJobQueue.class, "endDate");
		xsJSON2.omitField(ModelRiskProject.class, "tolernacematrixs");
		xsJSON2.omitField(ModelRiskProject.class, "tabsToUse");
		xsJSON2.omitField(ModelRiskProject.class, "riskIndex");

		xsJSON.setMode(XStream.NO_REFERENCES);
		xsJSON2.setMode(XStream.NO_REFERENCES);

		//Ultra Lite version for relative Matrix, Ranking
		xsJSONRiskLite.omitField(IModelRiskLiteBasic.class, "dateEntered");
		xsJSONRiskLite.omitField(IModelRiskLiteBasic.class, "estimatedContingencey");
		xsJSONRiskLite.omitField(IModelRiskLiteBasic.class, "useCalculatedContingency");
		xsJSONRiskLite.omitField(IModelRiskLiteBasic.class, "postMitContingency");
		xsJSONRiskLite.omitField(IModelRiskLiteBasic.class, "preMitContingency");
		xsJSONRiskLite.omitField(IModelRiskLiteBasic.class, "contingencyPercentile");
		xsJSONRiskLite.omitField(IModelRiskLiteBasic.class, "mitigationCost");
		xsJSONRiskLite.omitField(IModelRiskLiteBasic.class, "active");
		xsJSONRiskLite.omitField(IModelRiskLiteBasic.class, "startExposure");
		xsJSONRiskLite.omitField(IModelRiskLiteBasic.class, "cause");
		xsJSONRiskLite.omitField(IModelRiskLiteBasic.class, "consequences");
		xsJSONRiskLite.omitField(IModelRiskLiteBasic.class, "endExposure");
		xsJSONRiskLite.omitField(IModelRiskLiteBasic.class, "securityLevel");
		xsJSONRiskLite.omitField(IModelRiskLite.class, "preMitContingencyWeighted");
		xsJSONRiskLite.omitField(IModelRiskLite.class, "postMitContingencyWeighted");
		xsJSONRiskLite.omitField(IModelRiskLite.class, "dateEvalRev");
		xsJSONRiskLite.omitField(IModelRiskLite.class, "dateEvalApp");
		xsJSONRiskLite.omitField(IModelRiskLite.class, "dateIDApp");
		xsJSONRiskLite.omitField(IModelRiskLite.class, "dateIDRev");
		xsJSONRiskLite.omitField(IModelRiskLite.class, "dateMitPrep");
		xsJSONRiskLite.omitField(IModelRiskLite.class, "dateMitApp");

		xsJSONRiskLite.setMode(XStream.NO_REFERENCES);


		try {
			super.init(sc);
		} catch (ServletException e) {
			log.error("QRM Stack Trace", e);
		}

		InputStream in;
		try {
			in = new FileInputStream(sc.getServletContext().getRealPath("/QRM.properties"));
			try {
				configProp.load(in);
				configProp.put("REPORT_PATH", sc.getServletContext().getRealPath("/reports").replace("\\", "/")+"\\");

			} catch (IOException e) {
				log.error("QRM Stack Trace", e);
			}
		} catch (FileNotFoundException e) {
			log.error("QRM Stack Trace", e);
		}

		qrmTracing = Boolean.parseBoolean(configProp.getProperty("QRMTRACE"));

		PersistenceUtil.setInput(configProp, true);

	}

	protected Long getLongJS(final Object obj) {
		Long val = null;
		try {
			if (obj instanceof Long) {
				val = ((Long) obj);
			}
			if (obj instanceof String) {
				val = Long.parseLong(((String) obj));
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			val = null;
		}
		return val;
	}
	protected Double getDoubleJS(final Object obj) {
		Double val = 0.0;
		try {
			if (obj instanceof Long) {
				val = ((Long) obj).doubleValue();
			}
			if (obj instanceof String) {
				val = Double.parseDouble(((String) obj));
			}
			if (obj instanceof Double) {
				val = (Double) obj;
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			val = null;
		}
		return val;
	}
	protected final void outputJSON(final Object obj, HttpServletResponse res) {
		try {
			res.getWriter().println(xsJSON.toXML(obj));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("QRM Stack Trace", e);
		}
	}
	protected final void outputXML(final Object obj, HttpServletResponse res) {
		try {
			res.getWriter().println(xsXML.toXML(obj));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("QRM Stack Trace", e);
		}
	}
	protected final void outputJSONB(final Object obj, HttpServletResponse res) {

		try {
			res.getWriter().println("(" + xsJSON.toXML(obj) + ")");
		} catch (IOException e) {
			log.error("QRM Stack Trace", e);
		}
	}
	protected final void outputJSONRiskLite(final ArrayList<ModelRiskLite> risks,  HttpServletResponse res) {

		try {
			res.getWriter().println("(" + xsJSONRiskLite.toXML(risks) + ")");
		} catch (IOException e) {
			log.error("QRM Stack Trace", e);
		}
	}
	protected final void outputJSONRiskLite(final Object[] arr, HttpServletResponse res) {

		try {
			res.getWriter().println("("+ xsJSONRiskLite.toXML(arr) + ")");
		} catch (IOException e) {
			log.error("QRM Stack Trace", e);
		}
	}
	protected final void outputJSON2B(final Object obj, HttpServletResponse res) {
		try {
			res.getWriter().println("(" + xsJSON2.toXML(obj) + ")");
		} catch (IOException e) {
			log.error("QRM Stack Trace", e);
		}
	}
	protected static void closeAll(final ResultSet resultSet,
			final Statement statement, final Connection connection) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
			} // nothing we can do
		}
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
			} // nothing we can do
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
			} // nothing we can do
		}
	}
	protected Connection getSessionConnection(HttpServletRequest request){
		return PersistenceUtil.getConnection(((String) request.getSession().getAttribute("session.url")));
	}
	protected final void setDynamicProb(ModelRiskProject proj, ModelRisk risk, ModelToleranceMatrix mat, boolean preMit, boolean riskUpdate) {
		ProbUtil.setDynamicProb(proj, risk, mat, preMit, riskUpdate);
	}
	protected final void setDynamicProb2(ModelRisk risk, ModelToleranceMatrix mat, boolean preMit) {
		ProbUtil.setDynamicProb2( risk, mat, preMit);
	}


	protected long getRepositoryID(String url){

		Connection conn = null;
		try {
			conn = PersistenceUtil.getQRMLoginCPDS().getConnection();
			String sql = "SELECT repID FROM repositories WHERE url = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, url);
			ResultSet rs = stmt.executeQuery();
			rs.first();
			long repID = rs.getLong("repID");
			closeAll(rs,stmt, conn);

			return (repID);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error("QRM Stack Trace", e);
		} finally {
			closeAll(null,null,conn);
		}
		return -1L;
	}
	protected long getRepositoryMgr(String url){

		Connection conn = null;
		try {
			conn = PersistenceUtil.getQRMLoginCPDS().getConnection();
			String sql = "SELECT repmgr FROM repositories WHERE url = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, url);
			ResultSet rs = stmt.executeQuery();
			rs.first();
			long repID = rs.getLong("repmgr");
			closeAll(rs,stmt, conn);

			return (repID);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error("QRM Stack Trace", e);
		} finally {
			closeAll(null,null,conn);
		}
		return -1L;
	}
	protected double calcProb(ModelRisk risk, boolean preMit){
		return ProbUtil.calcProb(risk, preMit);
	}
	@SuppressWarnings("unchecked")
	protected Long[] getRootProjectID(Session sess){

		ArrayList<ModelRiskProject> projects = new ArrayList<ModelRiskProject>(sess.getNamedQuery("getAllRiskProjects").list());

		Long id = Long.MAX_VALUE;
		Long userID = null;

		for(ModelRiskProject project:projects){
			if (project.projectID < id){
				id = project.projectID;
				userID = project.projectRiskManagerID;
			}
		}

		return new Long[]{id,userID};
	}
	protected double roundTwoDecimals(double d) {
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}

	protected final boolean checkUpdateSecurity(final long riskID, final long userID, HttpServletRequest request) {


		try (Connection conn = getSessionConnection(request);){
			PreparedStatement ps = conn.prepareStatement("SELECT checkRiskSecurity(?,?)");
			ps.setLong(1, riskID);
			ps.setLong(2, userID);

			ResultSet rs = ps.executeQuery();
			rs.first();
			boolean result = rs.getBoolean(1);
			closeAll(rs, ps, conn);
			return result;
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			return false;
		} 
	}
	protected final boolean checkDeleteSecurity(final long riskID, final long userID, Session sess){		
		try {
			ModelRiskLiteBasic risk = (ModelRiskLiteBasic) sess.load(ModelRiskLiteBasic.class, riskID);
			ModelRiskProject proj = getRiskProject(risk.projectID, sess);
			if (risk.forceDownChild){
				return false;
			}
			if (risk.forceDownParent){
				if (userID == proj.getProjectRiskManagerID()){
					return true;
				} else {
					return false;
				}
			}
			return (userID == risk.getOwnerID() || userID == risk.getManager1ID() || userID == proj.getProjectRiskManagerID());
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			return false;
		}		
	}
	protected final boolean checkViewSecurity(final long riskID, final long userID, final long secLevel, final long projectID, HttpServletRequest request) {

		Connection conn = null;

		try {
			conn = getSessionConnection(request);
			PreparedStatement ps = conn.prepareStatement("SELECT checkRiskSecurityView(?,?,?,?)");
			ps.setLong(1, riskID);
			ps.setLong(2, userID);
			ps.setLong(3, secLevel);
			ps.setLong(4, projectID);

			ResultSet rs = ps.executeQuery();
			rs.first();
			boolean result = rs.getBoolean(1);
			closeAll(rs, ps, conn);
			return result;
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			return false;
		} finally {
			closeAll(null, null, conn);
		}
	}
	protected final boolean checkReassignSecurity(Long riskID, Long newProject,	Long userID, HttpServletRequest request) {

		Connection conn = getSessionConnection(request);
		int res = 0;

		try {
			PreparedStatement ps = conn.prepareStatement("select checkReassignSecurity(?,?,?)");
			ps.setLong(1, riskID);
			ps.setLong(2, newProject);
			ps.setLong(3, userID);
			ResultSet rs = ps.executeQuery();
			rs.first();
			res = rs.getInt(1);
			closeAll(rs,ps,null);
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} finally {
			closeAll(null,null,conn);
		}

		if (res > 0) return true;

		try {
			if(getRepositoryMgr((String)request.getSession().getAttribute("session.url")) == userID.longValue()){
				return true;
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			return false;
		} 
		return false;
	}
	protected final boolean checkDeleteIncidentSecurity(Long userID,  Long incidentID, Session sess) {

		ModelIncident incident = (ModelIncident) sess.get(ModelIncident.class, incidentID);

		if (incident.reportedByID.longValue() == userID.longValue()){
			return true;
		}
		if (getRootProjectID(sess)[1].longValue() == userID.longValue()){
			return true;
		}
		return false;
	}
	protected final boolean checkDeleteReviewSecurity(Long userID,  Long reviewID, Session sess) {

		ModelReview review = (ModelReview) sess.get(ModelReview.class, reviewID);
		Long projectID = review.getProjectID();
		ModelRiskProject proj = (ModelRiskProject)sess.get(ModelRiskProject.class, projectID);

		if (getRootProjectID(sess)[1].longValue() == userID.longValue() || userID.longValue() == proj.projectRiskManagerID){
			return true;
		}
		return false;
	}

	protected final void notifyUpdate(Long riskID, HttpServletRequest request){
		try {		
			ServletUserMessageManager.notifyRiskUpdate(riskID,request.getSession().getId());
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
	}

	protected final void associateContributingRisk(Long parentID, Long childID, HttpServletRequest request){

		try (Connection conn = getSessionConnection(request);){
			PreparedStatement ps = conn.prepareStatement("INSERT INTO riskrisk (parentID, childID) VALUES (?,?)");
			ps.setLong(1, parentID);
			ps.setLong(2, childID);

			ps.executeUpdate();
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} 
	}
	protected final void removeContributingRisk(Long parentID, Long childID, HttpServletRequest request){

		try (Connection conn = getSessionConnection(request);){
			PreparedStatement ps = conn.prepareStatement("DELETE FROM riskrisk WHERE parentID = ? AND childID = ?");
			ps.setLong(1, parentID);
			ps.setLong(2, childID);

			ps.executeUpdate();
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} 
	}
	protected final void removeContributingRisk(Long internalID, HttpServletRequest request){

		try (Connection conn = getSessionConnection(request);){
			PreparedStatement ps = conn.prepareStatement("DELETE FROM riskrisk WHERE internalID = ?");
			ps.setLong(1, internalID);

			int num = ps.executeUpdate();
			System.out.println(num+" Relationships Removed");
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} 
	}
	protected final ArrayList<Long> getProjectRiskIDsInt(HashMap<String, String> stringMap, Long userID, HttpServletRequest request) {

		String var_projectID = stringMap.get("PROJECTID");

		String sql = "SELECT risk.*,   p1.projectCode AS fromProjCode, p2.projectCode AS toProjCode, r2.riskProjectCode AS parentRiskProjectCode  FROM risk "+
				"LEFT OUTER JOIN riskproject AS p1 ON p1.projectID = risk.projectID  " +
				"LEFT OUTER JOIN riskproject AS p2 ON p2.projectID = risk.promotedProjectID  "+
				"LEFT OUTER JOIN risk AS r2 ON r2.riskID = risk.parentSummaryRisk ";

		if (Boolean.parseBoolean(stringMap.get("DESCENDANTS"))){
			sql = sql + " WHERE (risk.projectID IN (SELECT subprojectID  FROM subprojects WHERE projectID = "+var_projectID+"  UNION SELECT "+var_projectID+") OR (risk.promotedProjectID IN (SELECT superprojectID  FROM superprojects WHERE projectID = "+var_projectID+"  UNION SELECT "+var_projectID+") AND risk.projectID IN (SELECT subprojectID  FROM subprojects WHERE projectID = "+var_projectID+"  UNION SELECT "+var_projectID+") ) OR risk.promotedProjectID = "+var_projectID+" )"+
					" AND checkRiskSecurityView(risk.riskID, "+userID+", risk.securityLevel, risk.projectID)";
		} else {
			sql = sql + " WHERE  (risk.promotedProjectID IN (SELECT superprojectID  FROM superprojects WHERE projectID = "+var_projectID+" AND risk.projectID IN (SELECT subprojectID  FROM subprojects WHERE projectID = "+var_projectID+") )  OR risk.promotedProjectID = "+var_projectID+" OR risk.projectID = "+var_projectID+") AND checkRiskSecurityView(risk.riskID, "+userID+", risk.securityLevel, risk.projectID)";
		}


		ArrayList<Long> risksIDs = new ArrayList<Long>();
		try {

			Connection conn = getSessionConnection(request);

			try {
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()){
					risksIDs.add(rs.getLong("riskID"));
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				conn.close();

			} finally {
				conn.close();
			}

		} catch (SQLException e1) {
			log.error("QRM Stack Trace", e1);
		}

		return  risksIDs;
	}
	@SuppressWarnings("unchecked")
	protected final ArrayList<ModelObjective> getProjectObjectives(final Long projectID, final Session sess) {
		return new ArrayList<ModelObjective>(sess.getNamedQuery("getProjectObjectives").setLong("projectID", projectID).list());
	}
	@SuppressWarnings("unchecked")
	protected final ArrayList<ModelQuantImpactType> getProjectQuantTypes(final Long projectID, final Session sess) {
		return new ArrayList<ModelQuantImpactType>(sess.getNamedQuery("getProjectQuantTypes").setLong("projectID", projectID).list());
	}
	@SuppressWarnings("unchecked")
	protected final List<ModelPerson> getProjectRiskManagers(final Long projectID, final Session sess) {
		return new ArrayList<ModelPerson>(sess.getNamedQuery("getProjectRiskManagers").setLong("projectID", projectID).list());
	}
	@SuppressWarnings("unchecked")
	protected final List<ModelPerson> getProjectRiskOwners(final Long projectID,final Session sess) {
		return new ArrayList<ModelPerson>(sess.getNamedQuery("getProjectRiskOwners").setLong("projectID", projectID).list());
	}
	@SuppressWarnings("unchecked")
	protected final List<ModelPerson> getProjectUsers(final Long projectID, final Session sess) {
		return new ArrayList<ModelPerson>(sess.getNamedQuery("getProjectRiskUsers").setLong("projectID", projectID).list());
	}

	@SuppressWarnings("unchecked")
	protected final ArrayList<ModelQRMReport> getQRMReportsContext(final Session sess) {
		return new ArrayList<ModelQRMReport>(sess.getNamedQuery("getAllReportsContext").list());
	}
	@SuppressWarnings("unchecked")
	protected final ArrayList<ModelQRMReport> getQRMReportsRegister(final Session sess) {
		return new ArrayList<ModelQRMReport>(sess.getNamedQuery("getAllReportsRegister").list());
	}
	@SuppressWarnings("unchecked")
	protected final ArrayList<ModelQRMReport> getQRMReportsRepository(final Session sess) {
		return new ArrayList<ModelQRMReport>(sess.getNamedQuery("getAllReportsRepository").list());
	}
	@SuppressWarnings("unchecked")
	protected final ArrayList<ModelQRMReport> getQRMReportsIncident(final Session sess) {
		return new ArrayList<ModelQRMReport>(sess.getNamedQuery("getAllReportsIncident").list());
	}
	@SuppressWarnings("unchecked")
	protected final ArrayList<ModelQRMReport> getQRMReportsReview(final Session sess) {
		return new ArrayList<ModelQRMReport>(sess.getNamedQuery("getAllReportsReview").list());
	}

	@SuppressWarnings("unchecked")
	protected final ArrayList<ModelQRMReport> getQRMReportsRisk(final Session sess) {
		return new ArrayList<ModelQRMReport>(sess.getNamedQuery("getAllReportsRisk").list());
	}
	protected final ModelPerson getPerson(final Long id, final Session sess) {
		return (ModelPerson) sess.createCriteria(ModelPerson.class).add(Restrictions.eq("stakeholderID", id)).add(Restrictions.eq("active", true)).uniqueResult();
	}
	@SuppressWarnings("unchecked")
	protected final List<ModelMitigationStep> getRiskMitigationSteps(final Long riskID, final Session sess) {
		return (List<ModelMitigationStep>)sess.getNamedQuery("getRiskMitigationsteps").setLong("riskID", riskID).list();
	}
	@SuppressWarnings("unchecked")
	protected final ArrayList<ModelDataObjectAllocation> getToleranceAllocations(final Session sess, boolean rolled) {
		if (rolled){
			return (ArrayList<ModelDataObjectAllocation>) sess.getNamedQuery("getRolledAllocations").list();
		} else {
			return (ArrayList<ModelDataObjectAllocation>) sess.getNamedQuery("getAllocations").list();			
		}
	}

	@SuppressWarnings("unchecked")
	protected final List<ModelRiskControl> getRiskControls(final Long riskID, final Session sess) {
		return sess.getNamedQuery("getRiskRiskControls").setLong("riskid", riskID).list();
	}
	@SuppressWarnings("unchecked")
	protected final List<ModelRiskLite> getProjectRisksForUser(final Long userID,final Long projectID, final int descendants, final Session sess) {

		if (projectID.longValue() == -100L){
			return new ArrayList<ModelRiskLite>();
		}
		try {

			List<ModelRiskLite> risks = (List<ModelRiskLite>)sess.getNamedQuery("getProjectRisksForUser")
					.setLong("userID", userID)
					.setLong("projectID", projectID)
					.setLong("descendants",descendants)
					.list();

			Collections.sort(risks, new RiskComparator(false));
			return risks;
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	protected final List<ModelRiskLite> getAllProjectRisksLite(final Long user_id,	final long projectID, final boolean descendants, final Session sess) {

		try {
			List<ModelRiskLite> risks = sess.getNamedQuery("getAllProjectRiskLite")
					.setLong("var_user_id", user_id)
					.setLong("var_projectID", projectID)
					.setBoolean("var_descendants", descendants)
					.list();

			Collections.sort(risks, new RiskComparator(false));

			return risks;

		} catch (RuntimeException e) {
			log.error("QRM Stack Trace", e);
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	protected final List<ModelRisk> getAllProjectRisks(final Long user_id,	final long projectID, final boolean descendants, final Session sess) {

		try {
			List<ModelRisk> risks = sess.getNamedQuery("getAllProjectRisk")
					.setLong("var_user_id", user_id)
					.setLong("var_projectID",projectID)
					.setBoolean("var_descendants",descendants)
					.list();

			for (ModelRisk risk : risks) {
				try {
					risk.setProbConsequenceNodes(new ArrayList<ModelRiskConsequence>(getRiskConsequences(risk.getInternalID(), sess)));
				} catch (RuntimeException e) {
					log.error("QRM Stack Trace", e);
				}
			}

			return risks;

		} catch (RuntimeException e) {
			log.error("QRM Stack Trace", e);
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	protected final ModelRisk getRisk(final Long riskID, final Long user_id, final long context_id, final Session sess) {

		ModelRisk risk = (ModelRisk) sess.getNamedQuery("getRisk")
				.setLong("userID", user_id)
				.setLong("riskID", riskID)
				.setLong("projectID", context_id)
				.uniqueResult();

		try {
			risk.setMitigationPlan(new ArrayList<ModelMitigationStep>(getRiskMitigationSteps(risk.getInternalID(), sess)));
			List<ModelUpdateComment> updates = getRiskMitigationStepsUpdates(risk.getInternalID(), sess);
			//			//add any updates to the mitigation
			for (ModelMitigationStep step:risk.getMitigationPlan()){
				for(ModelUpdateComment comment:updates){
					step.addUpdate(comment);
				}
			}
			//			risk.setMitigationPlanUpdates(new ArrayList<ModelUpdateComment>(getRiskMitigationStepsUpdates(risk.getInternalID(), sess)));
			risk.setControls(new ArrayList<ModelRiskControl>(sess.getNamedQuery("getRiskControls").setLong("riskid", risk.getInternalID()).list()));
			risk.setObjectivesImpacted(getRiskObjectivesID(risk.getInternalID(), sess));
			risk.setProbConsequenceNodes(new ArrayList<ModelRiskConsequence>(getRiskConsequences(risk.getInternalID(), sess)));
		} catch (RuntimeException e) {
			log.error("QRM Stack Trace", e);
		}
		return risk;
	}
	@SuppressWarnings("unchecked")
	protected List<ModelUpdateComment> getRiskMitigationStepsUpdates(Long riskID, Session sess) {
		return (List<ModelUpdateComment>)sess.getNamedQuery("getRiskMitigationStepsUpdate").setLong("riskID", riskID).list();
	}
	protected final void installSingleRisk(ModelRisk riskI, QRMExportProject template, Long userID, Long projectID, Session sess, HttpServletResponse response){


		try {
			ModelRiskLiteBasic risk = new ModelRiskLiteBasic();

			risk.setTitle(riskI.getTitle());
			risk.setDescription(riskI.getDescription());
			risk.setCause(riskI.getCause());
			risk.setConsequences(riskI.getConsequences());


			if (riskI.getManager1ID() == null){
				risk.setManager1ID(userID);
			} else {
				if (template != null){
					risk.setManager1ID(template.personMap.get(riskI.getManager1ID()).stakeholderID);
				} else {
					risk.setManager1ID(userID);		
				}
			}
			if (riskI.getOwnerID() == null){
				risk.setOwnerID(userID);
			} else {
				if (template != null){
					risk.setOwnerID(template.personMap.get(riskI.getOwnerID()).stakeholderID);
				} else {
					risk.setOwnerID(userID);
				}
			}
			risk.setSummaryRisk(riskI.summaryRisk);

			risk.estimatedContingencey = riskI.estimatedContingencey;
			if (risk.estimatedContingencey == null){
				risk.estimatedContingencey = 0.0;
			}

			risk.setInherentProb(riskI.getInherentProb());
			risk.setInherentImpact(riskI.getInherentImpact());

			// Insert trigger will figure out the correct matrix ID
			risk.matrixID = 0L;
			if (riskI.projectID == null){
				risk.projectID = projectID;
			} else {
				risk.projectID = riskI.projectID;
			}
			risk.securityLevel = 0;

			sess.save(risk);
			sess.refresh(risk);

			sess.beginTransaction();

			ModelRisk riskRec = getRisk(risk.riskID, 1L, risk.projectID, sess);
			riskRec.setTeatmentAvoidance(riskI.isTeatmentAvoidance());
			riskRec.setTeatmentReduction(riskI.isTeatmentReduction());
			riskRec.setTeatmentRetention(riskI.isTeatmentRetention());
			riskRec.setTeatmentTransfer(riskI.isTeatmentTransfer());

			riskRec.setImpCost(riskI.isImpCost());
			riskRec.setImpEnvironment(riskI.impEnvironment);
			riskRec.setImpReputation(riskI.isImpReputation());
			riskRec.setImpSafety(riskI.isImpSafety());
			riskRec.setImpSpec(riskI.isImpSpec());
			riskRec.setImpTime(riskI.isImpTime());

			try {
				riskRec.setPrimCatID(template.findNewCatID(riskI.primCatID));
				riskRec.setSecCatID(template.findNewCatID(riskI.secCatID));
			} catch (Exception e1) {
				// Do nothing
			}

			sess.save(riskRec);
			sess.getTransaction().commit();

			if (template != null){
				sess.beginTransaction();
				try {
					for (Long objId : riskI.getObjectivesImpacted()){
						sess.save(new DTOObjectiveImpacted(risk.riskID, template.findNewObjectiveParentID(objId)));					
					}
				} catch (Exception e1) {
					// Do Nothing				
				}
				sess.getTransaction().commit();
			}

			sess.beginTransaction();
			for (ModelRiskConsequence con : riskI.probConsequenceNodes){
				try {
					con.quantType = template.findConsID(con.quantType);
					con.setRiskID(risk.riskID);
					con.internalID = null;
					sess.save(con);					
				} catch (Exception e1) {
					// Do Nothing				
				}
			}
			sess.getTransaction().commit();


			try {
				if (riskI.getMitigationPlan() != null){
					for(ModelMitigationStep step:riskI.getMitigationPlan()){

						ModelMitigationStep stepNew = new ModelMitigationStep();
						if (step.personID == 0){
							stepNew.setPersonResponsibleID(userID);
						} else {
							try {
								if (template != null){
									stepNew.setPersonResponsibleID(template.personMap.get(step.getPersonResponsibleID()).stakeholderID);
								} else {
									stepNew.setPersonResponsibleID(userID);
								}
							} catch (Exception e) {
								step.setPersonResponsibleID(userID);
							}
						}
						if (riskI.projectID == null && projectID != null ){
							stepNew.setProjectID(projectID);
						} else {
							stepNew.setProjectID(riskI.projectID);
						}
						if (step.percentComplete == null){
							stepNew.percentComplete = 0.0;
						} else {
							stepNew.percentComplete = step.percentComplete;
						}
						stepNew.setRiskID(risk.riskID);
						if (step.estCost == null) {
							stepNew.setEstimatedCost(0.0);
						} else {
							stepNew.setEstimatedCost(step.estCost);
						}

						stepNew.description = step.description;
						stepNew.response = step.response;
						stepNew.endDate = step.endDate;

						sess.beginTransaction();
						sess.save(stepNew);
						sess.getTransaction().commit();
					}
				}
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
				response.getWriter().println("Import Risk. New RiskID: "+risk.getRiskProjectCode()+" (Problem importing Mitigation and Response Plan)");
				return;
			}
			try {
				response.getWriter().println(risk.getRiskProjectCode()+" ");
			} catch (IllegalStateException e) {
				// DO Nothing
			}

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
	}
	@SuppressWarnings("unchecked")
	protected final List<ModelRiskConsequence> getRiskConsequences(final Long riskID, final Session sess) {

		try {
			List<ModelRiskConsequence> stepList = sess.createCriteria(ModelRiskConsequence.class)
					.add(Restrictions.eq( "riskID", riskID )).list();

			for (ModelRiskConsequence s : stepList) {
				s.setQuantImpactType((ModelQuantImpactType) sess
						.createSQLQuery("SELECT 0 AS GENERATION, quantimpacttype.* FROM quantimpacttype WHERE typeID = :id")
						.addEntity(ModelQuantImpactType.class)
						.setLong("id", s.quantType)
						.uniqueResult());
			}
			return stepList;
		} catch (RuntimeException e) {
			log.error("QRM Stack Trace", e);
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	protected final ArrayList<ModelRiskProject> getAllRiskProjectsForUserLite(final Long user_id, final Session sess) {
		try {
			return new ArrayList<ModelRiskProject>(sess.getNamedQuery("getAllRiskProjectsForUserLite").setLong("user_id", user_id).list());
		} catch (Exception e) {
			return new ArrayList<ModelRiskProject>();
		}
	}
	@SuppressWarnings("unchecked")
	protected final ArrayList<ModelRiskProject> getAllMgmtRiskProjectsForUserLite(final Long user_id, final Session sess) {
		try {
			ArrayList<ModelRiskProject> list = new ArrayList<ModelRiskProject>(
					sess.getNamedQuery("getAllMgmtRiskProjectsForUserLite")
					.setLong("userID", user_id)
					.list());

			return list;
		} catch (Exception e) {
			return new ArrayList<ModelRiskProject>();
		}
	}
	protected final ModelRiskProject getRiskProject(final Long projectID, final Session sess) {

		try {
			sess.setCacheMode(CacheMode.IGNORE);
			ModelRiskProject proj = (ModelRiskProject) sess.get(ModelRiskProject.class, projectID);

			setRiskmanagers(getProjectRiskManagers( projectID, sess), proj, true);
			setRiskowners(getProjectRiskOwners( projectID, sess), proj, true);
			proj.matrix = getProjectMatrix( projectID, sess);
			proj.setRiskCategorys(new ArrayList<ModelMultiLevel>(getProjectCategorys( projectID, sess)));
			proj.setTolActions(getProjectTolActions( projectID, sess));

			return proj;
		} catch (Exception e) {
			return null;
		}
	}
	protected final ModelRiskProject getRiskProjectDetails(final Long projectID, final Session sess) {
		try {
			sess.setCacheMode(CacheMode.IGNORE);
			sess.flush();

			ModelRiskProject proj = getRiskProject(projectID, sess);

			setRiskusers(getProjectUsers(projectID, sess),proj, true);
			proj.setImpactTypes(getProjectQuantTypes(projectID, sess));
			proj.setObjectives(getProjectObjectives(projectID, sess));

			return proj;
		} catch (Exception e) {
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	protected final List<ModelMultiLevel> getProjectCategorys(	final Long projectID, final Session sess) {

		sess.flush();
		List<ModelMultiLevel> allLevels = sess.getNamedQuery("getProjectCategorys").setLong("projectID", projectID).list();

		ArrayList<ModelMultiLevel> prims = new ArrayList<ModelMultiLevel>();

		try {
			for (ModelMultiLevel ml : allLevels) {
				if (ml.getParentID() == 1) {
					prims.add(ml);
				}
			}
			for (ModelMultiLevel ml : prims) {
				for (ModelMultiLevel ml2 : allLevels) {
					if (ml2.getParentID().longValue() == ml.getInternalID().longValue()
							&& ml2.getInternalID().longValue() != ml.getInternalID().longValue()) {
						ml.getSec().add(ml2);
					}
				}
			}

			return prims;
		} catch (RuntimeException e) {
			log.error("QRM Stack Trace", e);
			return new ArrayList<ModelMultiLevel>();
		}
	}
	@SuppressWarnings("unchecked")
	protected final ModelToleranceMatrix getProjectMatrix(final Long projectID,
			final Session sess) {
		try {
			ArrayList<ModelToleranceMatrix> matrixs = new ArrayList<ModelToleranceMatrix>(sess.getNamedQuery("getProjectMats").setLong("projectID", projectID).list());
			ModelToleranceMatrix projectMat = matrixs.get(0);
			ModelToleranceMatrix rootMat = matrixs.get(matrixs.size() - 1);

			if (projectMat.impact1 == null) projectMat.impact1 = rootMat.impact1;
			if (projectMat.impact2 == null) projectMat.impact2 = rootMat.impact2;
			if (projectMat.impact3 == null) projectMat.impact3 = rootMat.impact3;
			if (projectMat.impact4 == null) projectMat.impact4 = rootMat.impact4;
			if (projectMat.impact5 == null) projectMat.impact5 = rootMat.impact5;
			if (projectMat.impact6 == null) projectMat.impact6 = rootMat.impact6;
			if (projectMat.impact7 == null) projectMat.impact7 = rootMat.impact7;
			if (projectMat.impact8 == null) projectMat.impact8 = rootMat.impact8;

			if (projectMat.prob1 == null) projectMat.prob1 = rootMat.prob1; 
			if (projectMat.prob2 == null) projectMat.prob2 = rootMat.prob2; 
			if (projectMat.prob3 == null) projectMat.prob3 = rootMat.prob3; 
			if (projectMat.prob4 == null) projectMat.prob4 = rootMat.prob4; 
			if (projectMat.prob5 == null) projectMat.prob5 = rootMat.prob5; 
			if (projectMat.prob6 == null) projectMat.prob6 = rootMat.prob6; 
			if (projectMat.prob7 == null) projectMat.prob7 = rootMat.prob7; 
			if (projectMat.prob8 == null) projectMat.prob8 = rootMat.prob8;

			if (projectMat.probVal1 == null) projectMat.probVal1 = rootMat.probVal1;
			if (projectMat.probVal2 == null) projectMat.probVal2 = rootMat.probVal2;
			if (projectMat.probVal3 == null) projectMat.probVal3 = rootMat.probVal3;
			if (projectMat.probVal4 == null) projectMat.probVal4 = rootMat.probVal4;
			if (projectMat.probVal5 == null) projectMat.probVal5 = rootMat.probVal5;
			if (projectMat.probVal6 == null) projectMat.probVal6 = rootMat.probVal6;
			if (projectMat.probVal7 == null) projectMat.probVal7 = rootMat.probVal7;
			if (projectMat.probVal8 == null) projectMat.probVal8 = rootMat.probVal8;

			projectMat.setTolString(rootMat.getTolString());
			projectMat.setMaxProb(rootMat.getMaxProb());
			projectMat.setMaxImpact(rootMat.getMaxImpact());

			return projectMat;
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			return null;
		}
	}
	@SuppressWarnings("unchecked")
	protected final ArrayList<ModelToleranceDescriptors> getProjectTolActions(
			final Long projectID, final Session sess) {
		try {
			ArrayList<ModelToleranceDescriptors> actions = new ArrayList<ModelToleranceDescriptors>(
					sess.getNamedQuery("getProjectTolActions")
					.setLong("projectID", projectID)
					.list());
			return new ArrayList<ModelToleranceDescriptors>(actions.subList(0,5));
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			return null;
		}
	}
	protected final ArrayList<Long> getRiskObjectivesID(final Long id,	final Session sess) {

		ArrayList<Long> ids = new ArrayList<Long>();

		for (Object obj : sess.createSQLQuery("SELECT objectiveID FROM objectives_impacted WHERE riskID = :id")
				.setLong("id", id).list()) {
			ids.add(((BigInteger) obj).longValue());
		}
		return ids;
	}
	protected final long updateSubjRanks(final String[] riskIDs,
			final long projectID, final long user_id, final Session sess) {

		sess.beginTransaction();

		try {
			sess.createSQLQuery("DELETE FROM subjrank WHERE projectID = :projectID")
			.setLong("projectID", projectID)
			.executeUpdate();

			long i = 1;
			for (String riskID : riskIDs) {
				sess.createSQLQuery("INSERT INTO subjrank (projectID, riskID, rank) VALUES (:projectID,:riskID,:rank)")
				.setLong("projectID", projectID)
				.setLong("riskID",Long.parseLong(riskID))
				.setLong("rank", i++)
				.executeUpdate();
			}

			sess.getTransaction().commit();
		} catch (Exception e) {
			sess.getTransaction().rollback();
			log.error("QRM Stack Trace", e);
		}



		return QRMConstants.QRM_OK;
	}
	protected final long updateSummaryAllocations(final Long userID,
			final Long[] childID, final Long[] parentID, final Session sess) {

		if (childID.length != parentID.length) {
			return QRMConstants.QRM_FAULT;
		}

		for (int i = 0; i < childID.length; i++) {
			try {
				sess.createSQLQuery("UPDATE risk SET parentSummaryRisk = :parentID WHERE riskID = :childID")
				.setLong("parentID", parentID[i])
				.setLong("childID",	childID[i])
				.executeUpdate();
			} catch (RuntimeException e) {
				log.error("QRM Stack Trace", e);
				return QRMConstants.QRM_FAULT;
			}
		}
		return QRMConstants.QRM_OK;
	}
	@SuppressWarnings("unchecked")
	protected final ModelIncident getIncident(final Long incidentID, final Session sess) {

		try {
			ModelIncident incident = (ModelIncident) sess.get(ModelIncident.class, incidentID);
			List<ModelIncidentConsequence> impacts = (List<ModelIncidentConsequence>)sess.createCriteria(ModelIncidentConsequence.class)
					.add(Restrictions.eq("incidentID",incidentID))
					.list();
			incident.setImpacts(new ArrayList<ModelIncidentConsequence>(impacts));

			ArrayList<Long> ids = new ArrayList<Long>();
			for (Object obj : sess.createSQLQuery("SELECT objectiveID FROM incidentobjective WHERE incidentID = :id")
					.setLong("id", incidentID).list()) {
				ids.add(((BigInteger) obj).longValue());
			}
			incident.setObjectivesImpacted(ids);

			incident.setUpdates(getIncidentUpdates(incidentID, sess));

			return incident;
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	protected final ArrayList<ModelIncidentUpdate> getIncidentUpdates(final Long incidentID, final Session sess) {

		List<ModelIncidentUpdate> updates = (List<ModelIncidentUpdate>)sess.createCriteria(ModelIncidentUpdate.class)
				.add(Restrictions.eq("incidentID",incidentID))
				.list();

		return new ArrayList<ModelIncidentUpdate>(updates);
	}
	protected final void deleteUserTemplates(final String templateName, final Session sess) {

		sess.beginTransaction();

		ModelImportTemplate template = (ModelImportTemplate)sess.createCriteria(ModelImportTemplate.class).add(Restrictions.eq("templateName",templateName)).uniqueResult();

		sess.delete(template);
		sess.getTransaction().commit();

	}
	protected final void setRiskmanagers(final List<ModelPerson> name, final ModelRiskProject proj, final boolean lite) {
		if (lite) {
			for (ModelPerson p : name) {
				proj.riskmanagerIDs.add(p.getStakeholderID());
			}
		} else {
			proj.setRiskmanagers(new ArrayList<ModelPerson>(name));
		}
	}
	protected final void setRiskowners(final List<ModelPerson> name,
			final ModelRiskProject proj, final boolean lite) {
		if (lite) {
			for (ModelPerson p : name) {
				proj.riskownersIDs.add(p.getStakeholderID());
			}
		} else {
			proj.setRiskowners(new ArrayList<ModelPerson>(name));
		}
	}

	private void setRiskusers(final List<ModelPerson> name, final ModelRiskProject proj, final boolean lite) {
		if (lite) {
			for (ModelPerson p : name) {
				proj.riskusersIDs.add(p.getStakeholderID());
			}
		} else {
			proj.setRiskowners(new ArrayList<ModelPerson>(name));
		}
		proj.setRiskusers(new ArrayList<ModelPerson>(name));
	}
	private final class RiskComparator implements Comparator<ModelRiskLite> {

		private boolean level = false;
		protected RiskComparator(final boolean level) {
			this.level = level;
		}
		public int compare(final ModelRiskLite r1, final ModelRiskLite r2) {

			if (this.level == false) {
				if ((r1.subjectiveRank == null) && (r2.subjectiveRank != null)) {
					return 1;
				}
				if ((r1.subjectiveRank != null) && (r2.subjectiveRank == null)) {
					return -1;
				}
				if ((r1.subjectiveRank == null) && (r2.subjectiveRank == null)) {
					return 0 - new Double(Math.floor(r1.currentTolerance)).compareTo(new Double(Math.floor(r2.currentTolerance)));
				}
				if ((r1.subjectiveRank != null)
						&& (r2.subjectiveRank != null)
						&& (r1.subjectiveRank == 0 || r2.subjectiveRank == 0)) {
					return 0 - new Double(Math.floor(r1.currentTolerance)).compareTo(new Double(Math.floor(r2.currentTolerance)));
				}
				return r1.subjectiveRank.compareTo(r2.subjectiveRank);
			}

			if ((r1.subjectiveRank == null)|| (r2.subjectiveRank == null)) {

				if ((r1.subjectiveRank == null)&& (r2.subjectiveRank != null)) {
					return 1;
				}
				if ((r1.subjectiveRank != null)&& (r2.subjectiveRank == null)) {
					return -1;
				}
				if ((r1.subjectiveRank == null)&& (r2.subjectiveRank == null)) {
					return 0 - new Double(Math.floor(r1.currentTolerance))
					.compareTo(new Double(Math.floor(r2.currentTolerance)));
				}
				if ((r1.subjectiveRank != null)
						&& (r2.subjectiveRank != null)
						&& (r1.subjectiveRank == 0 || r2.subjectiveRank == 0)) {
					return 0 - new Double(Math.floor(r1.currentTolerance))
					.compareTo(new Double(Math.floor(r2.currentTolerance)));
				}

				return r1.subjectiveRank.compareTo(r2.subjectiveRank);
			}
			return r1.subjectiveRank.compareTo(r2.subjectiveRank);
		}
	}

}
