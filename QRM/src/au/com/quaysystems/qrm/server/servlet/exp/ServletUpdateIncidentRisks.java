package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.dto.ModelIncident;

@SuppressWarnings("serial")
@WebServlet (value = "/updateIncidentRisks", asyncSupported = false)
public class ServletUpdateIncidentRisks extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		try {
			Long incidentID = null;

			JSONObject data = (JSONObject) parser.parse(request.getParameter("DATA"));

			boolean newIncident;
			try {
				newIncident = ((Long) data.get("NewOrOld") == 0) ? true : false;
			} catch (Exception e1) {
				newIncident = (((String) data.get("NewOrOld")).indexOf('0') != -1) ? true : false;
			}

			if (newIncident) {

				ModelIncident incident = new ModelIncident();

				String title = (String) data.get("title");
				if (title == null || title.length() < 1){
					title = "Enter Incident Title";
				}

				incident.setTitle(title);

				incident.bCauses = incident.bControl = incident.bIdentified = incident.bMitigated = incident.bRated = incident.bReviews = incident.bIssue = false;
				incident.impCost = incident.impEnviron = incident.impReputation = incident.impSafety = incident.impSpec = incident.impTime = false;
				incident.notifyStakeHoldersEntered = incident.notifyStakeHoldersUpdate = incident.stakeHolderNotified = false;

				incident.setTitle(title);
				incident.setDateIncident((df.parse((String) data.get("dateIncident"))));
				incident.setReportedByID(userID);
				incident.setReportedByStr(getPerson(userID, sess).name);

				Transaction tx = sess.beginTransaction();
				incidentID = (Long) sess.save(incident);
				tx.commit();
			} else {
				try {
					incidentID = (Long) data.get("existing");
				} catch (Exception e) {
					incidentID = Long.parseLong((String) data.get("existing"));
				}
			}

			JSONArray riskIDS = (JSONArray) data.get("riskIDs");
			if ((riskIDS != null) && (riskIDS.size() > 0)) {
				Connection conn = getSessionConnection(request);
				String sql = "INSERT INTO incidentrisk (incidentID, riskID) VALUES ";
				boolean okToExecute = false;
				for (Object riskID2 : riskIDS) {
					PreparedStatement ps = conn.prepareStatement("SELECT * FROM incidentrisk WHERE incidentID = ? AND riskID = ?");
					ps.setLong(1,incidentID);
					ps.setLong(2,(Long)riskID2);
					ResultSet rs = ps.executeQuery();
					if (!rs.first()){
						sql = sql + "(" + incidentID + "," + riskID2 + "),";
						okToExecute = true;
					}
				}
				sql = sql.substring(0, sql.length() - 1);

				Statement st = conn.createStatement();

				try {
					if (okToExecute) {
						st.executeUpdate(sql);
					}
				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
					outputJSONB(false, response);
					return;
				} finally {
					closeAll(null, st, conn);
				}
			}

			outputJSONB(true,response);

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			outputJSONB(false,response);
		}
	}
}
