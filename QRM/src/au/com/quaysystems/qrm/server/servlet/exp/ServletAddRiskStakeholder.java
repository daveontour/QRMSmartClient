package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

@SuppressWarnings("serial")
@WebServlet (value = "/addRiskStakeholder", asyncSupported = false)
public class ServletAddRiskStakeholder extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		
		try (Connection conn = getSessionConnection(request)){
			conn.setAutoCommit(true);

			PreparedStatement ps0 = conn.prepareStatement("SELECT * FROM allriskstakeholders  WHERE riskID =? AND stakeholderID= ?");
			ps0.setLong(1, Long.parseLong(stringMap.get("RISKID")));
			ps0.setLong(2, Long.parseLong(stringMap.get("STAKEHOLDERID")));
			ResultSet rs = ps0.executeQuery();
			if (rs.first()){
				try {
					response.getWriter().println("User is already identified as a stakeholder");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					rs.close();
				}
				return;
			}

			
			PreparedStatement ps = conn.prepareStatement("INSERT INTO riskstakeholder (riskID, stakeholderID, description) VALUES (?,?,?)");
			ps.setLong(1, Long.parseLong(stringMap.get("RISKID")));
			ps.setLong(2, Long.parseLong(stringMap.get("STAKEHOLDERID")));
			ps.setString(3, stringMap.get("ROLE"));
			ps.executeUpdate();
			try {
				response.getWriter().println("User added as a contributing stakeholder");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				rs.close();
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
	}	
}
