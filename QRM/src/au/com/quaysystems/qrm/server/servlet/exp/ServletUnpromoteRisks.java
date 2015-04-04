package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONArray;

import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.server.PersistenceUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/unpromoteRisks", asyncSupported = false)
public class ServletUnpromoteRisks extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		try {
			for (Object objJS:(JSONArray)parser.parse(stringMap.get("DATA"))){
				response.getWriter().println((unpromoteRisk(getLongJS(objJS),sess, userID, request)));
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

	}
	
	protected final String unpromoteRisk(Long riskID,Session sess, Long userID, HttpServletRequest request) {


		ModelRisk risk = (ModelRisk) sess.getNamedQuery(	"getRisk").setLong("userID", userID)
				.setLong("riskID", riskID)
				.setLong("projectID",PersistenceUtil.getRootProjectID((String) request.getSession().getAttribute("session.url")))
				.uniqueResult();
		
		
		if (risk.promotedProjectID == null){
			return "Risk "+risk.riskProjectCode+" - No Promotion To Remove<br/>";
		}

		if (!checkUpdateSecurity(riskID, userID, request)) {
			return "User not Authorised to update risk "+risk.riskProjectCode;
		}
		if (!checkReassignSecurity(riskID, risk.promotedProjectID, userID, request)) {
			return "User not Authorised to remove promotion of risk "+risk.riskProjectCode;
		}		


		try {
			Connection conn = getSessionConnection(request);

			try {
				String sql = "UPDATE risk SET promotedProjectID = NULL WHERE riskID = ?";
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setLong(1, risk.riskID);
				int rows = stmt.executeUpdate();			
				if (rows != 1){
					closeAll(null, stmt, conn);
					return "Error un-promoting risk(2) "+risk.riskProjectCode;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				conn.close();
				return "Error un-promoting risk(2) "+risk.riskProjectCode;
			}

			// Add a comment to note the reassignment

			String sql = "INSERT INTO auditcomments (  riskID, enteredByID, comment) VALUES (?,?,?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setLong(1, riskID);
			stmt.setLong(2, userID);
			stmt.setString(3, "Promotion of Risk Removed");
			stmt.execute();
			stmt.close();
			closeAll(null, null, conn);
			return "Risk "+risk.riskProjectCode+" - Promotion Removed<br/>";
		} catch (SQLException e1) {
			log.error("QRM Stack Trace", e1);
		}
		return null;
	}
}
