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

import au.com.quaysystems.qrm.dto.ModelAuditComment;

@SuppressWarnings("serial")
@WebServlet (value = "/getRiskComments", asyncSupported = false)
public class ServletGetRiskComments extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		ArrayList<ModelAuditComment> comments = new ArrayList<ModelAuditComment>();

		try {

			String sql = "SELECT internalID,attachmentFileName, auditcomments.dateEntered, schedReviewDate,projectID, riskID,enteredByID, comment, commenturl, identification, evaluation, mitigation, review, approval, schedReview, schedReviewID,  stakeholders.name FROM auditcomments, stakeholders WHERE auditcomments.enteredByID = stakeholders.stakeholderID AND riskID="+ stringMap.get("RISKID");
			Connection conn = getSessionConnection(request);
			Statement st = conn.createStatement();

			try {
				ResultSet rs = st.executeQuery(sql);
				while (rs.next()) {
					ModelAuditComment comment = new ModelAuditComment();
					comment.setComment(rs.getString("comment"));
					comment.setDateEntered(rs.getDate("dateEntered"));
					comment.setDateEntered(new Date(comment.getDateEntered().getTime()));

					boolean isReview = rs.getBoolean("review");
					boolean isApproval = rs.getBoolean("approval");
					boolean isIdentification = rs.getBoolean("identification");
					boolean isEvaluation = rs.getBoolean("evaluation");
					boolean isMitigation = rs.getBoolean("mitigation");
					boolean isSchedReview = rs.getBoolean("schedReview");

					if (isReview && isIdentification) {
						comment.type = "Identication Review";
					}
					if (isReview && isEvaluation) {
						comment.type = "Evaluation Review";
					}
					if (isReview && isMitigation) {
						comment.type = "Mitigation Review";
					}

					if (isApproval && isIdentification) {
						comment.type = "Identication Approval";
					}
					if (isApproval && isEvaluation) {
						comment.type = "Evaluation Approval";
					}
					if (isApproval && isMitigation) {
						comment.type = "Mitigation Approval";
					}

					comment.identification = isIdentification;
					comment.evaluation = isEvaluation;
					comment.mitigation = isMitigation;

					comment.review = isReview;
					comment.approval = isApproval;
					comment.schedReview = isSchedReview;

					if (isSchedReview) {
						comment.type = "Scheduled Review";
					}

					if (comment.type == null) {
						comment.type = "General Comment";
					}

					comment.url = rs.getString("commenturl");

					String filename = rs.getString("attachmentFileName");
					if ((filename != null) && (filename.length() > 0)) {
						comment.attachmentURL = "./QRMComment?getCommentAttachment=true&ATTACHMENTID="+ rs.getLong("internalID");
					}

					comment.setPersonName(rs.getString("name"));
					comments.add(comment);
				}
				rs.close();
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
				outputJSONB(false,response);
				return;
			} finally {
				closeAll(null, st, conn);
			}

			outputJSON(comments,response);

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			outputJSONB(false,response);
		}


	}
}
