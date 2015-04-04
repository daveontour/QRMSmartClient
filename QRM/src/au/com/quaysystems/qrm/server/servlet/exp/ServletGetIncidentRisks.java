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
@WebServlet (value = "/getIncidentRisks", asyncSupported = false)
public class ServletGetIncidentRisks extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		String sql = "SELECT incidentrisk.*, risk.riskProjectCode, risk.title, risk.currentTolerance  FROM incidentrisk "
				+ " LEFT OUTER JOIN risk ON incidentrisk.riskID = risk.riskID "
				+ " WHERE incidentID = "
				+ request.getParameter("INCIDENTID");

		ArrayList<DTORiskReviewComment> incidentRisks = new ArrayList<DTORiskReviewComment>();

		try {
			Connection conn = getSessionConnection(request);
			Statement st = conn.createStatement();

			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {

				String title = rs.getString("title");
				Long riskid = rs.getLong("riskID");
				int tol = rs.getInt("currentTolerance");
				String code = rs.getString("riskProjectCode");

				incidentRisks.add(new DTORiskReviewComment(title, null, tol, code, riskid));
			}
			closeAll(rs, st, conn);
			outputJSON(incidentRisks,response);

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

	}
}
