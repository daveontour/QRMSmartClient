package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.DTORiskReviewComment;

@SuppressWarnings("serial")
@WebServlet (value = "/getRiskReviews", asyncSupported = false)
public class ServletGetRiskReviews extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		String sql = "SELECT review.*, reviewrisk.*, auditcomments.*  FROM review"
				+ " LEFT OUTER JOIN reviewrisk    ON review.reviewID   = reviewrisk.reviewID"
				+ " LEFT OUTER JOIN auditcomments ON reviewrisk.riskID = auditcomments.riskID  AND auditcomments.schedReviewID = reviewrisk.reviewID WHERE reviewrisk.riskID ="
				+ riskID;

		ArrayList<DTORiskReviewComment> riskReviews = new ArrayList<DTORiskReviewComment>();

		try {
			Connection conn = getSessionConnection(request);
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				String comment = rs.getString("comment");
				String title = rs.getString("title");
				Date sched = new Date(rs.getDate("scheduledDate").getTime());
				Date actual;
				try {
					actual = new Date(rs.getDate("actualDate").getTime());
				} catch (Exception e) {
					actual = null;
				}

				riskReviews.add( new DTORiskReviewComment(title, comment, sched, actual));
			}
			closeAll(rs, st, conn);
			outputJSON(riskReviews,response);

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
	}
}
