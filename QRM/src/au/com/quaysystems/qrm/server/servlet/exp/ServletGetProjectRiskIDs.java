package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

@SuppressWarnings("serial")
@WebServlet (value = "/getProjectRiskIDs", asyncSupported = false)
public class ServletGetProjectRiskIDs extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


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
			log.error(e1);
		}

		ArrayList<Long> x = getProjectRiskIDsInt(stringMap,userID, request);

		Long[] ids = new Long[x.size()];
		outputJSON(x.toArray(ids),response);
	}
}
