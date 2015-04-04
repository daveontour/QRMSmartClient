package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.ModelRisk;

@SuppressWarnings("serial")
@WebServlet (value = "/registerAudit", asyncSupported = false)
public class ServletRegisterAudit extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		if (!checkUpdateSecurity(riskID, userID, request)){
			outputJSON(false,response);
			return;
		}

		int type = 0;
		String comment = null;
		try {

			JSONObject input = (JSONObject) parser.parse(stringMap.get("DATA"));
			comment = (String) input.get("comment");
			type = getLongJS(input.get("type")).intValue();

			Connection conn = getSessionConnection(request);
			conn.setAutoCommit(true);

			String sql = null;
			if (type == QRMConstants.INDENTIFACTIONREVIEW) {
				sql = "INSERT INTO auditcomments (riskID, projectID, enteredByID, comment, identification, review) VALUES (?,?,?,?, true, true)";
			}
			if (type == QRMConstants.INDENTIFACTIONAPPROVAL) {
				sql = "INSERT INTO auditcomments (riskID, projectID, enteredByID, comment, identification, approval) VALUES (?,?,?,?, true, true)";
			}
			if (type == QRMConstants.EVALUATIONREVIEW) {
				sql = "INSERT INTO auditcomments (riskID, projectID, enteredByID, comment, evaluation, review) VALUES (?,?,?,?, true, true)";
			}
			if (type == QRMConstants.EVALUATIONAPPROVAL) {
				sql = "INSERT INTO auditcomments (riskID, projectID, enteredByID, comment, evaluation, approval) VALUES (?,?,?,?, true, true)";
			}
			if (type == QRMConstants.MITREVIEW) {
				sql = "INSERT INTO auditcomments (riskID, projectID, enteredByID, comment, mitigation, review) VALUES (?,?,?,?, true, true)";
			}
			if (type == QRMConstants.MITAPPROVAL) {
				sql = "INSERT INTO auditcomments (riskID, projectID, enteredByID, comment, mitigation, approval) VALUES (?,?,?,?, true, true)";
			}

			PreparedStatement st = conn.prepareStatement(sql);
			st.setLong(1, riskID);
			st.setLong(2, projectID);
			st.setLong(3, userID);
			st.setString(4, comment);

			st.executeUpdate();

			closeAll(null, st, conn);

			Transaction tx = sess.beginTransaction();
			ModelRisk risk = (ModelRisk) sess.getNamedQuery("getRisk")
					.setLong("userID", userID)
					.setLong("riskID", riskID)
					.setLong("projectID", projectID)
					.uniqueResult();

			if (type == QRMConstants.INDENTIFACTIONREVIEW) {
				risk.idIDRev = userID;
				risk.setDateIDRev(new Date());
			}
			if (type == QRMConstants.INDENTIFACTIONAPPROVAL) {
				risk.idIDApp = userID;
				risk.setDateIDApp(new Date());
			}

			if (type == QRMConstants.EVALUATIONREVIEW) {
				risk.idEvalRev = userID;
				risk.setDateEvalRev(new Date());
			}
			if (type == QRMConstants.EVALUATIONAPPROVAL) {
				risk.idEvalApp = userID;
				risk.setDateEvalApp(new Date());
			}

			if (type == QRMConstants.MITREVIEW) {
				risk.idMitRev = userID;
				risk.setDateMitRev(new Date());
			}
			if (type == QRMConstants.MITAPPROVAL) {
				risk.idMitApp = userID;
				risk.setDateMitApp(new Date());
			}

			sess.update(risk);
			tx.commit();

			outputJSON(true,response);

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}


	}
}
