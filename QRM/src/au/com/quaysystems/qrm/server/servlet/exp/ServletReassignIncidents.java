package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONArray;

import au.com.quaysystems.qrm.dto.ModelIncident;

@SuppressWarnings("serial")
@WebServlet (value = "/reassignIncidents", asyncSupported = false)
public class ServletReassignIncidents extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		try {
			for (Object objJS:(JSONArray)parser.parse(stringMap.get("XFERINCIDENTS"))){
				response.getWriter().println((reassignIncident(getLongJS(objJS),Long.parseLong(stringMap.get("NEWPROJECTID")), Long.parseLong(stringMap.get("MOVEORPROMOTE")),sess, request)));
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}


	}
	protected final String reassignIncident(Long incidentID, Long newProject,  Long moveOrpromote, Session sess, HttpServletRequest request) {

		ModelIncident incident = getIncident(incidentID, sess);


		if (moveOrpromote == 0){
			try {

				try {
					sess.beginTransaction();
					ModelIncident in = (ModelIncident) sess.get(ModelIncident.class, incidentID);
					in.projectID = newProject;
					sess.update(in);
					sess.getTransaction().commit();
				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
					return "Failed to reassign Incident";
				}

				return "Incident Reassigned";
			} catch (Exception e1) {
				log.error("QRM Stack Trace", e1);
			}
		} else {

			try {
				Connection conn = getSessionConnection(request);

				try {
					String sql = "SELECT * from superprojects WHERE superprojectID = ? AND projectID = ?";
					PreparedStatement stmt = conn.prepareStatement(sql);
					stmt.setLong(1, newProject);
					stmt.setLong(2, incident.projectID);
					ResultSet rs = stmt.executeQuery();					
					if (!rs.first()){
						closeAll(rs, stmt, conn);
						return "The selected project is not a parent of the existing project";
					}
				} catch (Exception e1) {
					conn.close();
					return "Error promoting incident";
				} 

				try {
					String sql = "UPDATE incident SET promotedProjectID = ? WHERE incidentID = ?";
					PreparedStatement stmt = conn.prepareStatement(sql);
					stmt.setLong(1, newProject);
					stmt.setLong(2, incidentID);
					int rows = stmt.executeUpdate();			
					if (rows != 1){
						closeAll(null, stmt, conn);
						return "Error promoting incident(2) ";
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					conn.close();
					return "Error promoting incident(2) ";
				}

				closeAll(null, null, conn);
				return "Incident Promoted";
			} catch (SQLException e1) {
				log.error("QRM Stack Trace", e1);
			}

		}

		return null;
	}

}
