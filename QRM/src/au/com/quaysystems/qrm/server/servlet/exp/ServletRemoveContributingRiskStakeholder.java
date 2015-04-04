package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@SuppressWarnings("serial")
@WebServlet (value = "/removeContributingRiskStakeholder", asyncSupported = false)
public class ServletRemoveContributingRiskStakeholder extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		
		try (Connection conn = getSessionConnection(request)){
			conn.setAutoCommit(true);
			
			JSONArray stakeholdersJS = (JSONArray) parser.parse(stringMap.get("STAKEHOLDERS"));
			

			for (Object obj : stakeholdersJS) {
				
				JSONObject objJS = (JSONObject) obj;
				
				PreparedStatement ps = conn.prepareStatement("DELETE FROM riskstakeholder WHERE riskID = ? AND stakeholderID = ?");
				ps.setLong(1, riskID);
				ps.setLong(2, (Long.parseLong((String)objJS.get("stakeholderID"))));
				int num = ps.executeUpdate();
				
				if (num > 0){
					response.getWriter().println((String)objJS.get("name")+ " was removed");					
				} else {
					response.getWriter().println((String)objJS.get("name")+ " cannot be removed");										
				}
				response.getWriter().println("<br />");
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
		
	}	
}
