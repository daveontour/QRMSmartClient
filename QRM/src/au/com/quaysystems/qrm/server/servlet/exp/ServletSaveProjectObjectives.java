package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import au.com.quaysystems.qrm.dto.ModelObjective;
import au.com.quaysystems.qrm.dto.ModelObjectiveNoGen;
import au.com.quaysystems.qrm.server.PersistenceUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/saveProjectObjectives", asyncSupported = false)
public class ServletSaveProjectObjectives extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		// Delete Objectives
		try {
			Transaction tx = sess.beginTransaction();
			for (Object obj : (JSONArray) parser.parse(stringMap.get("DATADELETE"))) {
				sess.delete(sess.load(ModelObjectiveNoGen.class, (Long) obj));
			}
			tx.commit();
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

		JSONArray objJS = null;
		try {
			objJS = (JSONArray) parser.parse(stringMap.get("DATA"));
		} catch (ParseException e1) {
			log.error("QRM Stack Trace", e1);
		}

		// Update Exiting
		if (objJS != null){
			try {
				Transaction tx = sess.beginTransaction();
				for (Object obj : objJS) {
					JSONObject x = (JSONObject) obj;
					if (((Long) x.get("generation") < 0) || ((Long) x.get("objectiveID") < 0)) {
						continue;
					}
					ModelObjectiveNoGen noGenObj = (ModelObjectiveNoGen)sess.load(ModelObjectiveNoGen.class, (Long) x.get("objectiveID"));
					
					ModelObjective objective = new ModelObjective();
					
					objective.setInternalID(noGenObj.getInternalID());
					objective.setProjectID(noGenObj.getProjectID());
					objective.projectTitle = noGenObj.projectTitle;
					
					objective.setDescription((String) x.get("objective"));
					objective.setParentID((Long) x.get("parentID"));
					sess.update(objective);
				}
				tx.commit();

			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}


			// Creating new {objectives
			// New Objective come through with an objectiveID < 0

			HashMap<Long, Long> idMap = new HashMap<Long, Long>();
			Connection conn = PersistenceUtil.getConnection((String) request.getSession().getAttribute("session.url"));

			try {
				CallableStatement cstmt = conn.prepareCall("{call insertObjective(?,?,?,?)}");
				for (Object obj : objJS) {
					JSONObject x = (JSONObject) obj;
					if (((Long) x.get("generation") < 0) || ((Long) x.get("objectiveID") >= 0)) {
						continue;
					}

					Long parentID = (Long) x.get("parentID");
					if (parentID < 0) {
						parentID = idMap.get(x.get("parentID"));
					}
					if (parentID == null) {
						continue;
					}
					cstmt.setLong(1, projectID);
					cstmt.setString(2, (String) x.get("objective"));
					cstmt.setLong(3, parentID);
					cstmt.registerOutParameter(4, Types.NUMERIC);

					cstmt.execute();

					idMap.put((Long) x.get("objectiveID"), cstmt.getLong(4));
				}
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			} finally {
				closeAll(null, null, conn);
			}
		}
		// JDBC was used, so reset the Session factory so the caches get synced
		PersistenceUtil.removeSF((String) request.getSession().getAttribute("session.url"));

	}
	
}
