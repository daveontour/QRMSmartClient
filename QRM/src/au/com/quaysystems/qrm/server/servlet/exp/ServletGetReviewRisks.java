package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.DTORiskReviewComment;

@SuppressWarnings("serial")
@WebServlet (value = "/getReviewRisks", asyncSupported = false)
public class ServletGetReviewRisks extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		String sql = "SELECT reviewrisk.*, auditcomments.comment, risk.riskProjectCode, risk.title, risk.currentTolerance  FROM reviewrisk "
				+ " LEFT OUTER JOIN risk ON reviewrisk.riskID = risk.riskID "
				+ " LEFT OUTER JOIN auditcomments ON reviewrisk.riskID = auditcomments.riskID  AND auditcomments.schedReviewID = reviewrisk.reviewID where reviewID = "
				+ request.getParameter("REVIEWID");

		ArrayList<DTORiskReviewComment> riskReviews = new ArrayList<DTORiskReviewComment>();

		try {
			Connection conn = getSessionConnection(request);
			Statement st = conn.createStatement();

			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {

				String comment = rs.getString("comment");
				String title = rs.getString("title");
				Long riskid = rs.getLong("riskID");
				int tol = rs.getInt("currentTolerance");
				String code = rs.getString("riskProjectCode");

				riskReviews.add(new DTORiskReviewComment(title, comment, tol, code, riskid));
			}
			closeAll(rs, st, conn);
			outputJSON(riskReviews,response);

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

	}
}
