package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONArray;

import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.server.PersistenceUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/reassignRisks", asyncSupported = false)
public class ServletReassignRisks extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		try {
			for (Object objJS:(JSONArray)parser.parse(stringMap.get("XFERRISKS"))){
				response.getWriter().println((reassignRisk(getLongJS(objJS),Long.parseLong(stringMap.get("NEWPROJECTID")), Long.parseLong(stringMap.get("MOVEORPROMOTE")),sess,userID, request)));
				response.getWriter().println("<br />");
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

	}
	
	
	protected final String reassignRisk(Long riskID, Long newProject,  Long moveOrpromote, Session sess, Long userID, HttpServletRequest request) {

		ModelRisk risk = (ModelRisk) sess.getNamedQuery(	"getRisk").setLong("userID", userID)
				.setLong("riskID", riskID)
				.setLong("projectID",PersistenceUtil.getRootProjectID((String) request.getSession().getAttribute("session.url")))
				.uniqueResult();

		if (!checkUpdateSecurity(riskID, userID, request)) {
			return "User not Authorised to update risk "+risk.riskProjectCode;
		}
		if (!checkReassignSecurity(riskID, newProject, userID, request)) {
			if (moveOrpromote == 0){
				return "User not Authorised to Move risk "+risk.riskProjectCode+" to the new project";
			} else {
				return "User not Authorised to Promote risk "+risk.riskProjectCode;
			}
		}		

		if (moveOrpromote == 0){
			try {

				Connection conn = getSessionConnection(request);
				
				try {
					CallableStatement pc = conn.prepareCall("call reassignRisk(?,?)");
					pc.setLong(1, riskID);
					pc.setLong(2, newProject);
					pc.execute();
					pc.close();
				} catch (Exception e) {
					log.error(e);
					conn.close();
					return "Failed to reassign Risk "+risk.riskProjectCode;
				}
				
				// Get New Risk_Project_Code

				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery("SELECT riskProjectCode  FROM risk WHERE riskID="+ riskID);
				rs.first();
				String newID = rs.getString("riskProjectCode");

				// Add a comment to note the reassignment

				String sql = "INSERT INTO auditcomments (  riskID, enteredByID, comment) VALUES (?,?,?)";
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setLong(1, riskID);
				stmt.setLong(2, userID);
				stmt.setString(3, "Risk reaasigned from another project. New ID = "+newID+". Old ID = "+risk.riskProjectCode);
				stmt.execute();
				stmt.close();
				closeAll(rs, st, conn);
				return "Risk "+risk.riskProjectCode+" reassigned. New ID = "+newID;
			} catch (SQLException e1) {
				log.error("QRM Stack Trace", e1);
			}
		} else {

			try {
				Connection conn = getSessionConnection(request);

				try {
					String sql = "SELECT * from superprojects WHERE superprojectID = ? AND projectID = ?";
					PreparedStatement stmt = conn.prepareStatement(sql);
					stmt.setLong(1, newProject);
					stmt.setLong(2, risk.projectID);
					ResultSet rs = stmt.executeQuery();					
					if (!rs.first()){
						closeAll(rs, stmt, conn);
						return "The selected project is not a parent of the existing project";
					}
				} catch (Exception e1) {
					conn.close();
					return "Error promoting risk "+risk.riskProjectCode;
				} 

				try {
					String sql = "UPDATE risk SET promotedProjectID = ? WHERE riskID = ?";
					PreparedStatement stmt = conn.prepareStatement(sql);
					stmt.setLong(1, newProject);
					stmt.setLong(2, risk.riskID);
					int rows = stmt.executeUpdate();			
					if (rows != 1){
						closeAll(null, stmt, conn);
						return "Error promoting risk(2) "+risk.riskProjectCode;
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					conn.close();
					return "Error promoting risk(2) "+risk.riskProjectCode;
				}

				// Add a comment to note the reassignment

				String sql = "INSERT INTO auditcomments (  riskID, enteredByID, comment) VALUES (?,?,?)";
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setLong(1, riskID);
				stmt.setLong(2, userID);
				stmt.setString(3, "Promoted risk to project");
				stmt.execute();
				stmt.close();
				closeAll(null, null, conn);
				return "Risk "+risk.riskProjectCode+" Promoted";
			} catch (SQLException e1) {
				log.error("QRM Stack Trace", e1);
			}

		}

		return null;
	}
}
