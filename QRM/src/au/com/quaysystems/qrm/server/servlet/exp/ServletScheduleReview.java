package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.dto.ModelReview;
import au.com.quaysystems.qrm.server.PersistenceUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/scheduleReview", asyncSupported = false)
public class ServletScheduleReview extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		try {
			Long reviewID = null;

			JSONObject data = (JSONObject) parser.parse(request.getParameter("DATA"));

			boolean newReview;
			try {
				try {
					newReview = ((Long) data.get("NewOrOld") == 0) ? true:false;
				} catch (Exception e1) {
					newReview = (((String) data.get("NewOrOld")).indexOf('0') != -1) ? true:false;
				}
			} catch (Exception e1) {
				newReview = true;
			}

			if (newReview) {

				ModelReview rev = new ModelReview();

				rev.setTitle((String) data.get("description"));
				rev.setScheduledDate(df.parse((String) data.get("scheduled")));
				try {
					rev.setProjectID(projectID);
				} catch (Exception e) {
					rev.setProjectID(PersistenceUtil.getRootProjectID((String) request.getSession().getAttribute("session.url")));
				}

				Transaction tx = sess.beginTransaction();
				reviewID = (Long) sess.save(rev);
				tx.commit();

			} else {
				try {
					reviewID = (Long) data.get("existing");
				} catch (Exception e) {
					reviewID = Long.parseLong((String) data.get("existing"));
				}
			}

			JSONArray riskIDS = null;
			try {
				riskIDS = (JSONArray) data.get("riskIDs");
			} catch (Exception e1) {
			}

			if ((riskIDS != null) && (riskIDS.size() > 0)) {
				String sql = "INSERT INTO reviewrisk (reviewID, riskID) VALUES ";
				for (Object riskID2 : riskIDS) {
					sql = sql + "(" + reviewID + "," + riskID2 + "),";
				}
				sql = sql.substring(0, sql.length() - 1);

				Connection conn = getSessionConnection(request);
				Statement st = conn.createStatement();

				try {
					st.executeUpdate(sql);
				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
					outputJSONB(false,response);
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
