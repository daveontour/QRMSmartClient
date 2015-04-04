package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelIncident;

@SuppressWarnings("serial")
@WebServlet (value = "/getAllIncidentsSummary", asyncSupported = false)
public class ServletGetAllIncidentsSummary extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		String sql = "SELECT * from incident ";
		
		try {
			Long incidentID = Long.parseLong((String)objMap.get("INCIDENTID"));
			if (incidentID != null){
				sql = sql + " WHERE incidentID  = "+incidentID; 
				outputJSON(sess.createSQLQuery(sql).addEntity(ModelIncident.class).list(),response);
				return;
			}

		} catch (Exception e) {
			//e.printStackTrace();
		}

		boolean activeStatus = (Boolean) objMap.get("STATACTIVE");
		boolean closedStatus = (Boolean) objMap.get("STATCLOSED");
		
		boolean tolEx = (Boolean) objMap.get("TOLEX");
		boolean tolHigh = (Boolean) objMap.get("TOLHIGH");
		boolean tolSig = (Boolean) objMap.get("TOLSIG");
		boolean tolMod = (Boolean) objMap.get("TOLMOD");
		boolean tolLow = (Boolean) objMap.get("TOLLOW");

		boolean impRep = (Boolean) objMap.get("STATIMPREPUTATION");
		boolean impSafety = (Boolean) objMap.get("STATIMPSAFETY");
		boolean impSpec = (Boolean) objMap.get("STATIMPSPEC");
		boolean impTime = (Boolean) objMap.get("STATIMPTIME");
		boolean impCost = (Boolean) objMap.get("STATIMPCOST");
		boolean impEnviron = (Boolean) objMap.get("STATIMPENVIRON");
		
		if (Boolean.parseBoolean(stringMap.get("DESCENDANTS"))){
			sql = sql + " WHERE (incident.projectID IN (SELECT subprojectID  FROM subprojects WHERE projectID = "+projectID+" UNION SELECT "+projectID+") OR (incident.promotedProjectID IN (SELECT superprojectID  FROM superprojects WHERE projectID = "+projectID+" UNION SELECT "+projectID+") AND incident.projectID IN (SELECT subprojectID  FROM subprojects WHERE projectID = "+projectID+"  UNION SELECT "+projectID+") ) OR incident.promotedProjectID = "+projectID+" )";				
		} else {
			sql = sql + " WHERE  (incident.promotedProjectID IN (SELECT superprojectID  FROM superprojects WHERE projectID = "+projectID+" AND incident.projectID IN (SELECT subprojectID  FROM subprojects WHERE projectID = "+projectID+") )  OR incident.promotedProjectID = "+projectID+" OR incident.projectID = "+projectID+")";
		}
		
		// Tolerance clauses
		if (!tolEx){
			sql = sql + " AND incident.severity != 5 ";
		}
		if (!tolHigh){
			sql = sql + " AND incident.severity != 4 ";
		}
		if (!tolSig){
			sql = sql + " AND incident.severity != 3 ";
		}
		if (!tolMod){
			sql = sql + " AND incident.severity != 2 ";
		}
		if (!tolLow){
			sql = sql + " AND incident.severity != 1 ";
		}
		
		if (!impRep){
			sql = sql +" AND impReputation != 1 ";
		}
		if (!impSafety){
			sql = sql +" AND impSafety != 1 ";
		}
		if (!impSpec){
			sql = sql +" AND impSpec != 1 ";
		}
		if (!impTime){
			sql = sql +" AND impTime != 1 ";
		}
		if (!impCost){
			sql = sql +" AND impCost != 1 ";
		}
		if (!impEnviron){
			sql = sql +" AND impEnviron != 1 ";
		}
		if (!activeStatus){
			sql = sql +" AND bActive != 1 ";
		}
		if (!closedStatus){
			sql = sql +" AND bActive = 1 ";
		}

		outputJSON(sess.createSQLQuery(sql).addEntity(ModelIncident.class).list(),response);

	}
}
