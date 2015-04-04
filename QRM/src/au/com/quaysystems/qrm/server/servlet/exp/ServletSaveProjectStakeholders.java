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

import au.com.quaysystems.qrm.dto.ModelPerson;
import au.com.quaysystems.qrm.server.PersistenceUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/saveProjectStakeholders", asyncSupported = false)
public class ServletSaveProjectStakeholders extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		try {
			Connection conn = PersistenceUtil.getConnection((String) request.getSession().getAttribute("session.url"));
			conn.setAutoCommit(true);
			PreparedStatement st = conn.prepareStatement("DELETE FROM projectowners WHERE projectID = ?");
			st.setLong(1, projectID);
			st.executeUpdate();

			try {
				JSONArray ownerJS = (JSONArray) parser.parse(stringMap.get("OWNERS"));
				PreparedStatement sto = conn.prepareStatement("INSERT INTO projectowners (projectID, stakeholderID) VALUES (?,?)");
				for (Object obj : ownerJS) {
					JSONObject objJS = (JSONObject) obj;
					sto.setLong(1, projectID);
					sto.setLong(2, (Long) objJS.get("stakeholderID"));
					try {
						sto.executeUpdate();
					} catch (Exception e) {
						log.error("QRM Stack Trace", e);
						try {
							// addUnSyncedStakeholderToRepository(objJS);
							// sto.executeUpdate();
						} catch (Exception e1) {
							log.error("QRM Stack Trace", e1);
						}
					}
				}

				st = conn.prepareStatement("DELETE FROM projectriskmanagers WHERE projectID = ?");
				st.setLong(1, projectID);
				st.executeUpdate();
				JSONArray managerJS = (JSONArray) parser.parse(stringMap.get("MANAGERS"));
				PreparedStatement stm = conn.prepareStatement("INSERT INTO projectriskmanagers (projectID, stakeholderID) VALUES (?,?)");
				for (Object obj : managerJS) {
					JSONObject objJS = (JSONObject) obj;
					stm.setLong(1, projectID);
					stm.setLong(2, (Long) objJS.get("stakeholderID"));
					try {
						stm.executeUpdate();
					} catch (Exception e) {
						log.error("QRM Stack Trace", e);
					}
				}

				st = conn.prepareStatement("DELETE FROM projectusers WHERE projectID = ?");
				st.setLong(1, projectID);
				st.executeUpdate();
				JSONArray userJS = (JSONArray) parser.parse(stringMap.get("USERS"));
				PreparedStatement stu = conn.prepareStatement("INSERT INTO projectusers (projectID, stakeholderID) VALUES (?,?)");
				for (Object obj : userJS) {
					JSONObject objJS = (JSONObject) obj;
					stu.setLong(1, projectID);
					stu.setLong(2, (Long) objJS.get("stakeholderID"));
					try {
						stu.executeUpdate();
					} catch (Exception e) {
						log.error("QRM Stack Trace", e);
					}
				}
				closeAll(null, st, conn);
				closeAll(null, sto, conn);
				closeAll(null, stu, conn);
				closeAll(null, stm, conn);
			} catch (Exception e1) {
				log.error("QRM Stack Trace", e1);
			}

			outputJSON(true, response);

			// Remove the session factory so it is recreated next time it is
			// requested so the the Hibernate environment is synced up with the
			// database after the JDBC calls
			PersistenceUtil.removeSF((String) request.getSession().getAttribute("session.url"));

		} catch (Exception e) {
			outputJSONB(sess.createCriteria(ModelPerson.class).list(), response);
		}

	}
	
}
