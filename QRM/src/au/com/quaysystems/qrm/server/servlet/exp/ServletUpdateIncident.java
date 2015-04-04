package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import au.com.quaysystems.qrm.dto.ModelIncident;
import au.com.quaysystems.qrm.dto.ModelIncidentConsequence;
import au.com.quaysystems.qrm.dto.ModelIncidentObjective;
import au.com.quaysystems.qrm.dto.ModelIncidentRisk;
import au.com.quaysystems.qrm.dto.ModelIncidentUpdate;

@SuppressWarnings("serial")
@WebServlet (value = "/updateIncident", asyncSupported = false)
public class ServletUpdateIncident extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		try {
			JSONObject incidentJS = (JSONObject) parser.parse(request.getParameter("VALUE"));
			Long id = (Long) incidentJS.get("incidentID");

			if (id == -1) {
				// An ID of -1 indicates a new Risk

				ModelIncident incident = new ModelIncident();

				String title = (String) incidentJS.get("title");
				if (title == null || title.length() < 1){
					title = "Enter Incident Title";
				}

				incident.setTitle(title);
				try {
					incident.setDateIncident((df.parse((String) incidentJS.get("dateIncident"))));
				} catch (java.text.ParseException e) {
					log.error("QRM Stack Trace", e);
				}
				incident.projectID = projectID;
				incident.setReportedByID(userID);
				incident.setReportedByStr(getPerson(userID, sess).name);

				incident.setDescription((String) incidentJS.get("description"));
				incident.severity = getLongJS(incidentJS.get("severity")).intValue();

				incident.bActive = (Boolean) incidentJS.get("bActive");
				incident.bIssue = (Boolean) incidentJS.get("bIssue");

				incident.bCauses = false;
				incident.bControl =  false;
				incident.bIdentified = false;
				incident.bMitigated = false;
				incident.bRated =  false;
				incident.bReviews =  false;

				incident.impCost = (Boolean) incidentJS.get("impCost");
				incident.impTime = (Boolean) incidentJS.get("impTime");
				incident.impSpec = (Boolean) incidentJS.get("impSpec");
				incident.impEnviron = (Boolean) incidentJS.get("impEnviron");
				incident.impSafety = (Boolean) incidentJS.get("impSafety");
				incident.impReputation = (Boolean) incidentJS.get("impReputation");

				incident.notifyStakeHoldersEntered = incident.notifyStakeHoldersUpdate = incident.stakeHolderNotified = false;

				sess.beginTransaction();
				id = (Long) sess.save(incident);
				sess.getTransaction().commit();


			} else {

				sess.beginTransaction();
				ModelIncident incident = (ModelIncident) sess.get(ModelIncident.class, id);
				incident.bCauses = (Boolean) incidentJS.get("bCauses");
				incident.bControl = (Boolean) incidentJS.get("bControl");
				incident.bIdentified = (Boolean) incidentJS.get("bIdentified");
				incident.bMitigated = (Boolean) incidentJS.get("bMitigated");
				incident.bRated = (Boolean) incidentJS.get("bRated");
				incident.bReviews = (Boolean) incidentJS.get("bReviews");
				incident.bActive = (Boolean) incidentJS.get("bActive");
				incident.severity = getLongJS(incidentJS.get("severity")).intValue();

				incident.impCost = (Boolean) incidentJS.get("impCost");
				incident.impTime = (Boolean) incidentJS.get("impTime");
				incident.impSpec = (Boolean) incidentJS.get("impSpec");
				incident.impEnviron = (Boolean) incidentJS.get("impEnviron");
				incident.impSafety = (Boolean) incidentJS.get("impSafety");
				incident.impReputation = (Boolean) incidentJS.get("impReputation");

				incident.setTitle((String) incidentJS.get("title"));
				try {
					incident.setDateIncident((df.parse((String) incidentJS.get("dateIncident"))));
				} catch (java.text.ParseException e) {
					log.error("QRM Stack Trace", e);
				}

				incident.setDescription((String) incidentJS.get("description"));
				incident.setLessonsLearnt((String) incidentJS.get("lessonsLearnt"));
				incident.setControls((String) incidentJS.get("controls"));

				sess.update(incident);
				sess.getTransaction().commit();
			}

			JSONArray jsUpdates = (JSONArray) incidentJS.get("updates");

			if (jsUpdates != null){
				for(Object obj:jsUpdates){

					JSONObject updateJS = (JSONObject) obj;

					Long updateID = getLongJS(updateJS.get("id"));
					String description = (String) updateJS.get("description");

					if ((updateID == null || updateID == -1) && description != null){

						ModelIncidentUpdate update = new ModelIncidentUpdate();
						update.description = description;
						update.incidentID = id;
						update.reportedByID = userID;
						update.reportedByStr = getPerson(userID, sess).name;

						sess.beginTransaction();
						sess.saveOrUpdate(update);
						sess.getTransaction().commit();

					}
				}
			}



			// Clear out existing associations

			try (Connection conn = getSessionConnection(request)){
				conn.setAutoCommit(true);
				conn.createStatement().executeUpdate("DELETE FROM incidentobjective WHERE incidentID = "+id);
				conn.createStatement().executeUpdate("DELETE FROM incidentrisk WHERE incidentID = "+id);
				conn.createStatement().executeUpdate("DELETE FROM incidentconseq WHERE incidentID = "+id);
			} catch (Exception e ){
				e.printStackTrace();
			}

			// Set the objectives impacted

			JSONArray arr = (JSONArray) incidentJS.get("objectivesImpacted");
			if (arr != null){
				try {
					sess.beginTransaction();
					for (Object obj : arr ) {
						Long objectiveID = getLongJS(obj);
						if (objectiveID > 0) {
							sess.save(new ModelIncidentObjective(id, objectiveID));
						}
					}				
				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
				} finally {
					sess.getTransaction().commit();
				}
			}


			// Set the risks impacted

			arr = (JSONArray) incidentJS.get("risksImpacted");
			if (arr != null){
				try {
					sess.beginTransaction();
					for (Object obj : arr) {
						Long rsk = getLongJS(obj);
						if (rsk > 0) {
							sess.save(new ModelIncidentRisk(id, rsk));
						}
					}
				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
				} finally {
					sess.getTransaction().commit();
				}
			}

			arr = (JSONArray) incidentJS.get("impacts");
			if (arr != null){
				try {

					// Set the Consequences of the incident
					sess.beginTransaction();
					for (Object con : (JSONArray) incidentJS.get("impacts")) {

						JSONObject conJS = (JSONObject) con;
						ModelIncidentConsequence consq = new ModelIncidentConsequence();

						try {
							consq.value = (Double) conJS.get("value");
						} catch (Exception e) {
							consq.value = ((Long) conJS.get("value")).doubleValue();
						}

						consq.description = (String) conJS.get("description");
						try {
							consq.typeID = (Long) conJS.get("typeID");
						} catch (Exception e) {
							consq.typeID = Long.parseLong((String) conJS.get("typeID"));
						}
						consq.incidentID = id;
						sess.save(consq);
					}

				} catch (HibernateException e) {
					log.error("QRM Stack Trace", e);
				} finally {
					sess.getTransaction().commit();
				}
			}

			// Return the updated incident
			// place the id into the parameter map in case it has been a
			// newly created incident
			stringMap.put("INCIDENTID", id.toString());

			getIncident(Long.parseLong(stringMap.get("INCIDENTID")),  sess);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			log.error("QRM Stack Trace", e);
		}

	}
}
