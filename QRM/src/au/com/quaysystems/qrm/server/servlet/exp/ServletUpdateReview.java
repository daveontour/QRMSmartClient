package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.dto.ModelReview;

@SuppressWarnings("serial")
@WebServlet (value = "/updateReview", asyncSupported = false)
public class ServletUpdateReview extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		try {
			JSONObject reviewJS = (JSONObject) parser.parse(request.getParameter("REVIEWDATA"));
			JSONArray riskJS = (JSONArray) parser.parse(request.getParameter("RISKDATA"));

			Transaction tx = sess.beginTransaction();
			ModelReview rev = (ModelReview) sess.get(ModelReview.class, Long.parseLong(stringMap.get("REVIEWID")));

			// Don't update if it has already been marked as completed.
			if (rev.reviewComplete) {
				outputJSON(true, response);
				tx.commit();
				return;
			}

			try {
				rev.setTitle((String) reviewJS.get("title"));
			} catch (Exception e1) {
			}
			try {
				rev.setScheduledDate(df.parse(((String) reviewJS.get("scheduledDate"))));
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}
			try {
				rev.setActualDate(df.parse(((String) reviewJS.get("actualDate"))));
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}
			try {
				rev.setReviewComplete((Boolean) reviewJS.get("reviewComplete"));
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}
			try {
				rev.setReviewComments(((String) reviewJS.get("reviewComments")));
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}

			sess.update(rev);

			tx.commit();

			Connection conn = getSessionConnection(request);
			conn.setAutoCommit(false);

			if (riskJS != null) {
				for (Object obj : riskJS) {
					try {
						JSONObject risk = (JSONObject) obj;

						String sql = "DELETE FROM auditcomments WHERE riskID = ? AND  schedReviewID = ? AND schedReview = true";
						PreparedStatement stmt = conn.prepareStatement(sql);
						stmt.setLong(1, (Long) risk.get("riskID"));
						stmt.setLong(2, rev.reviewID);

						stmt.executeUpdate();

						sql = "INSERT INTO auditcomments (  riskID, enteredByID, comment,  schedReviewID,  schedReview) VALUES (?,?,?,?,true)";
						stmt = conn.prepareStatement(sql);
						stmt.setLong(1, (Long) risk.get("riskID"));
						stmt.setLong(2, userID);
						stmt.setString(3, (String) risk.get("comment"));
						stmt.setLong(4, rev.reviewID);

						stmt.executeUpdate();

						conn.commit();

					} catch (Exception e) {
						log.error("QRM Stack Trace", e);
						closeAll(null, null, conn);
					}
				}
				closeAll(null, null, conn);
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
		outputJSON(true, response);

	}
}
