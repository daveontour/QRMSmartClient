package au.com.quaysystems.qrm.server.servlet.repmgr;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import au.com.quaysystems.qrm.dto.DTOSessionEntry;
import au.com.quaysystems.qrm.dto.DTOTransactionMap;
import au.com.quaysystems.qrm.dto.ModelJobQueueMgr;
import au.com.quaysystems.qrm.dto.ModelRepository;
import au.com.quaysystems.qrm.dto.ModelScheduledJob;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.report.ReportProcessorData;
import au.com.quaysystems.qrm.server.servlet.SessionControl;
import au.com.quaysystems.qrm.server.servlet.SessionEntry;
import au.com.quaysystems.qrm.server.servlet.exp.ServletUserMessageManager;

@SuppressWarnings("serial")
@WebServlet (value = "/sessionControl", asyncSupported = false)
public class ServletSessionControl extends QRMRPCServletRepMgr {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		
		String op = stringMap.get("OPERATION");
		
		switch (op){
		case "getAllSessions":
			getAllSessions(request, response, sess, riskID, stringMap, objMap, riskID);
			break;
		case "diableSession":
			disableSession(request, response, sess, riskID, stringMap, objMap, riskID);
			break;
		case "clearDefunct":
			clearDefunct(request, response, sess, riskID, stringMap, objMap, riskID);
			break;
		case "deleteJob":
			deleteJob(request, response, sess, riskID, stringMap, objMap, riskID);
			break;
		case "getAllScheduledJobs":
			getAllScheduledJobs(request, response, sess, riskID, stringMap, objMap, riskID);
			break;
		case "sendBroadcastMessage":
			sendBroadcastMessage(request, response, sess, riskID, stringMap, objMap, riskID);
			break;	
		case "deleteJobResult":
			deleteJobResult(request, response, sess, riskID, stringMap, objMap, riskID);
			break;
		case "getCompletedJobResult":
			getCompletedJobResult(request, response, sess, riskID, stringMap, objMap, riskID);
			break;
		case "deleteCompletedJob":
			deleteCompletedJob(request, response, sess, riskID, stringMap, objMap, riskID);
			break;
		case "getTransactionUsage":
			getTransactionUsage(request, response, sess, riskID, stringMap, objMap, riskID);
			break;
		}
		


	}
	
	private final void getAllSessions(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID){
		ArrayList<DTOSessionEntry> sessions = new ArrayList<DTOSessionEntry>();
		for(SessionEntry session : SessionControl.sessionMap.values()){
			sessions.add(new DTOSessionEntry(session));
		}
		outputJSON(sessions, response);
	}

	private final void disableSession(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID){
		String sessionID = stringMap.get("sessionID");
		SessionControl.sessionMap.get(sessionID).sessionEnabled = false;
		getAllSessions( request,  response,  sess,  userID,  stringMap,  objMap,  projectID);
	}
	private final void clearDefunct(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID){

		Set<String> keys = SessionControl.sessionMap.keySet();

		for(String key : keys){
			if (!SessionControl.sessionMap.get(key).sessionEnabled){
				SessionControl.sessionMap.remove(key);
			}
		}
		getAllSessions( request,  response,  sess,  userID,  stringMap,  objMap,  projectID);
	}
	
	protected final void deleteJob(HttpServletRequest request, HttpServletResponse response, Session sess2, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID){

		Session sess = PersistenceUtil.getSimpleControlSession();
		ModelRepository rep = (ModelRepository)sess.createSQLQuery("SELECT * FROM repositories WHERE repID = "+stringMap.get("repository"))
				.addEntity(ModelRepository.class)
				.uniqueResult();
		sess.close();

		try {
			Connection conn = PersistenceUtil.getConnection(rep.url);
			conn.setAutoCommit(true);
			Statement st = conn.createStatement();
			String sql = "DELETE FROM schedjob WHERE internalID = "+stringMap.get("jobID");
			st.executeUpdate(sql);
			st.close();
			conn.close();
		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		}
		getAllSessions( request,  response,  sess,  userID,  stringMap,  objMap,  projectID);
	}
	@SuppressWarnings("unchecked")
	protected final void getAllScheduledJobs(HttpServletRequest request, HttpServletResponse response, Session sess2, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID){

		List<ModelScheduledJob> allJobs =  new ArrayList<ModelScheduledJob> ();

		for (ModelRepository rep:getAllReps()){
			Session sess = PersistenceUtil.getSession(rep.url);

			// Get all the scheduled jobs for the repository
			List<ModelScheduledJob> jobs = (List<ModelScheduledJob>)sess.createSQLQuery("SELECT * FROM schedjob")
					.addEntity(ModelScheduledJob.class).list();

			sess.close();

			for (ModelScheduledJob job : jobs){
				job.database = rep.orgname;
				job.databaseUser = rep.url;
				allJobs.add(job);
			}
		}
		outputJSON(allJobs, response);

	}
	
	@SuppressWarnings({ "unchecked" })
	private List<ModelRepository> getAllReps(){
		Session sess = PersistenceUtil.getSimpleControlSession();
		List<ModelRepository> reps = (List<ModelRepository>)sess.createSQLQuery("SELECT * FROM repositories")
				.addEntity(ModelRepository.class)
				.list();
		for (ModelRepository rep : reps){
			rep.sessions = SessionControl.numRepSessions(rep.url);
		}
		sess.close();
		return reps;
	}
	private final void sendBroadcastMessage(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID){

		try {
//			if (userID != 1){
//				return;
//			}
//
			try {
				ServletUserMessageManager.sendBroadcastMessage(stringMap.get("DATA"));
			} catch (Exception e) {
				log.error(e);
			}
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	private final void deleteJobResult(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID){
		for (ModelRepository rep:getAllReps()){
			try {
				Connection conn = PersistenceUtil.getConnection(rep.url);
				conn.setAutoCommit(true);
				Statement st = conn.createStatement();
				String sql = "SELECT jobID FROM jobqueue WHERE executedDate < DATE_SUB(NOW(), INTERVAL "+stringMap.get("DAYINTERVAL")+" DAY)";

				ResultSet rs = st.executeQuery(sql);
				while (rs.next()){
					long jobID = rs.getLong("jobID");
					deleteReportProcessorData(jobID, sess);
				}
				st.close();

				st = conn.createStatement();
				String sqlUpdate = "DELETE FROM jobqueue WHERE executedDate < DATE_SUB(NOW(), INTERVAL "+stringMap.get("DAYINTERVAL")+" DAY)";
				st.executeUpdate(sqlUpdate);
				st.close();
				conn.close();
			} catch (SQLException e) {
				log.error("QRM Stack Trace", e);
			}
		}
		getCompletedJobResult( request,  response,  sess,  userID,  stringMap,  objMap,  projectID);
	}

	@SuppressWarnings("unchecked")
	private void getCompletedJobResult(HttpServletRequest request, HttpServletResponse response, Session sess2, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID) {

		HashMap<Long, ModelJobQueueMgr> allJobsMap =  new HashMap<Long, ModelJobQueueMgr> ();

		for (ModelRepository rep:getAllReps()){
			Session sess = PersistenceUtil.getSession(rep.url);
			for (ModelJobQueueMgr job :(List<ModelJobQueueMgr>)sess.getNamedQuery("getAllCompletedJobs").list()){
				allJobsMap.put(job.getJobID(), job);
			}
			sess.close();
		}

		outputJSON(new ArrayList<ModelJobQueueMgr>(allJobsMap.values()), response);
	}
	private void getTransactionUsage(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID){

		ArrayList<DTOTransactionMap> map = new ArrayList<DTOTransactionMap>(); 

//		for (String method: QRMServletUserRisk.transactionCouterMap.keySet()){
//			if (QRMServletUserRisk.transactionCouterMap.get(method) > 0){
//				map.add(new DTOTransactionMap(method,QRMServletUserRisk.transactionCouterMap.get(method)));
//			}
//		}

		outputJSON(map, response);
	}
	private void deleteCompletedJob(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID){
		ReportProcessorData job = this.getReportProcessorData(Long.parseLong(stringMap.get("jobID")),sess);

		Connection conn = null;
		if (job == null){
			conn = PersistenceUtil.getConnection(stringMap.get("url"));
		} else {
			conn = PersistenceUtil.getConnection(job.jdbcURL);
		}

		deleteReportProcessorData(Long.parseLong(stringMap.get("jobID")), sess);

		try {
			conn.setAutoCommit(true);
			Statement st = conn.createStatement();
			String sql = "DELETE FROM jobqueue WHERE jobID = "+stringMap.get("jobID");
			st.executeUpdate(sql);
			st.close();
		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("QRM Stack Trace", e);
			}
		}
		getCompletedJobResult( request,  response,  sess,  userID,  stringMap,  objMap,  projectID);
	}
	
	private ReportProcessorData getReportProcessorData(Long jobID,Session sess){
		sess.setCacheMode(CacheMode.IGNORE);
		sess.setFlushMode(FlushMode.ALWAYS);
		ReportProcessorData job = (ReportProcessorData)sess.createCriteria(ReportProcessorData.class).add(Restrictions.eq("jobID", jobID)).uniqueResult();
		sess.close();
		return job;		
	}
	private void deleteReportProcessorData(Long id, Session sess){
		try {
			sess.setCacheMode(CacheMode.IGNORE);
			sess.setFlushMode(FlushMode.ALWAYS);
			sess.beginTransaction();
			ReportProcessorData job = (ReportProcessorData)sess.createCriteria(ReportProcessorData.class).add(Restrictions.eq("jobID", id)).uniqueResult();
			sess.delete(job);
			sess.getTransaction().commit();
			sess.close();
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
	}
	
}
