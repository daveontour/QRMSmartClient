package au.com.quaysystems.qrm.server.servlet.exp;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelPerson;
import au.com.quaysystems.qrm.dto.ModelRiskConsequence;
import au.com.quaysystems.qrm.dto.ModelRiskControl;
import au.com.quaysystems.qrm.dto.ModelRiskLite;
import au.com.quaysystems.qrm.dto.ModelUpdateComment;
import au.com.quaysystems.qrm.server.RESTTransportContainer;

@SuppressWarnings("serial")
@WebServlet( urlPatterns = {
		"/getAllUsers",
		"/getRegisterReports",
		"/getRiskReports",
		"/getRiskObjectives",
		"/getIncident", 
		"/getIncidentUpdates", 
		"/getRiskControls",
		"/getConsequence",
		"/getRiskConsequences", 
		"/getProjectObjectives",
		"/getContextReports",
		"/getRiskLiteFetchSorted",
		"/getRiskMitigationUpdate",
		"/updateRanksRPC",
		"/deleteImportTemplate",
		"/cancelImport"}, asyncSupported = false)
public class ServletSimpleMultiFunction extends QRMRPCServlet {

	private final HashMap<String, Method> methodMap = new HashMap<String, Method>();

	@Override
	public void init(final ServletConfig sc) {

		super.init(sc);

		try {
			for (Method m : Class.forName("au.com.quaysystems.qrm.server.servlet.exp.ServletSimpleMultiFunction").getDeclaredMethods()) {
				methodMap.put(m.getName(), m);
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		String[] ops = request.getRequestURI().split("/");
		String op = ops[ops.length-1];

		log.info("Operation = "+op);

		try {
			methodMap.get(op).invoke(this, new Object[]{request,response,sess, userID, stringMap, objMap, projectID, riskID});
		} catch (Exception e) {
			log.error("QRM Error",e);
		} 
	}
	protected void cancelImport(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID, Long riskID){
		try (Connection conn = getSessionConnection(request)){
			conn.createStatement().executeUpdate("DELETE FROM attachment WHERE internalID = "+ stringMap.get("DATA"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	protected final void deleteImportTemplate(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID, Long riskID){
		deleteUserTemplates(stringMap.get("DATA"), sess);
	}
	protected final void updateRanksRPC(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		updateSubjRanks(stringMap.get("RANKS").split("##"),projectID,userID,sess);
		outputJSON(new RESTTransportContainer(0),response);
	}
	protected final void getRiskLiteFetchSorted(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		outputJSONRiskLite(new ArrayList<ModelRiskLite>( getAllProjectRisksLite(userID,	Long.parseLong(stringMap.get("PROJECTID")),	Boolean.parseBoolean(stringMap.get("DESCENDANTS")),	sess)),response);
	}
	protected final void getRiskMitigationUpdate(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		@SuppressWarnings("unchecked")
		List<ModelUpdateComment> risks = sess.createSQLQuery( "SELECT * FROM mitigationstepupdate").addEntity(ModelUpdateComment.class).list();
		outputXML(new RESTTransportContainer(risks.size(), 0, risks.size() - 1, 0, (new ArrayList<ModelUpdateComment>(risks))),response);
	}
	protected final void getRiskObjectives(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		outputJSON(getRiskObjectivesID(riskID,sess),response);
	}
	protected final void getIncident(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		outputJSON(getIncident(Long.parseLong(stringMap.get("INCIDENTID")), sess),response);
	}
	protected final void getIncidentUpdates(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID, Long riskID){
		outputJSON(getIncidentUpdates(Long.parseLong(stringMap.get("INCIDENTID")), sess),response);
	}
	protected final void getRiskControls(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		outputJSON( new ArrayList<ModelRiskControl>(getRiskControls(riskID, sess)),response);
	}
	protected final void getConsequence(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		outputJSON(sess.get(ModelRiskConsequence.class, Long.parseLong(stringMap.get("ID"))),response);
	}
	protected final void getRiskConsequences(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		outputJSON(getRiskConsequences(riskID, sess),response);
	}
	protected final void getProjectObjectives(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		outputXML(new RESTTransportContainer(getProjectObjectives(projectID, sess)),response);
	}
	protected final void getContextReports(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		outputXML(new RESTTransportContainer(getQRMReportsContext(sess)),response);
	}
	protected final void getRiskReports(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		outputXML(new RESTTransportContainer(getQRMReportsRisk(sess)),response);
	}
	protected final void getRegisterReports(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		outputXML(new RESTTransportContainer(getQRMReportsRegister(sess)),response);
	}
	protected final void getAllUsers(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		outputJSON2B(sess.createCriteria(ModelPerson.class).list(),response);
	}
}
