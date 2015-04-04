package au.com.quaysystems.qrm.server.report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import au.com.quaysystems.qrm.dto.DTOObjectiveImpacted;
import au.com.quaysystems.qrm.dto.ModelAuditComment;
import au.com.quaysystems.qrm.dto.ModelMitigationStep;
import au.com.quaysystems.qrm.dto.ModelObjective;
import au.com.quaysystems.qrm.dto.ModelRiskConsequence;
import au.com.quaysystems.qrm.dto.ModelRiskControl;
import au.com.quaysystems.qrm.dto.ModelRiskLite;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.QRMTXManager;

public class RiskReportExcelVisitor extends IReportProcessorVisitor {

	@SuppressWarnings("unchecked")
	@Override
	public void process(ReportProcessorData job, HashMap<Object, Object> taskParamMap, Session sess, Long reportSessionID,QRMTXManager txmgr) {

		try(Connection conn = PersistenceUtil.getConnection(job.jdbcURL)) {

			conn.setAutoCommit(true);

			HashMap<Long, String> objMap = new HashMap<>();
			for (ModelObjective model : (List<ModelObjective>)sess.getNamedQuery("getAllObjectives").list()){
				objMap.put(model.objectiveID, model.objective);
			}

			List<ModelRiskLite> risks = txmgr.getAllProjectRisksLite((Long)taskParamMap.get("userID"), (Long)taskParamMap.get("projectID"), (Boolean)taskParamMap.get("descendants"), sess);
			for (ModelRiskLite risk:risks){

				String plan = risk.mitPlanSummary;
				if (plan == null){
					plan = "-";
				}
				String mit= "MITIGATION PLAN SUMMARY\n"+ plan + "\n\nMITIGATION PLAN DETAILS\n";
				String response = "";
				for (ModelMitigationStep step : (List<ModelMitigationStep>)sess.getNamedQuery("getRiskMitigationsteps").setLong("riskID", risk.riskID).list()){

					if (step.response){
						response = response.concat(step.description+"\n\n");
					} else {
						mit = mit.concat(step.description+"\n\n");
					}

				}
				setInnerReportSessionData(reportSessionID, "MITSTEP", risk.riskID, mit, conn);
				setInnerReportSessionData(reportSessionID, "RESPONSE", risk.riskID, response, conn);


				try {
					List<ModelRiskConsequence> consList = sess.createCriteria(ModelRiskConsequence.class).add(Restrictions.eq( "riskID", risk.riskID )).list();
					String cons = ""; 

					for (ModelRiskConsequence s : consList) {
						cons = cons.concat(s.description+"\n");
					}

					setInnerReportSessionData(reportSessionID, "CONSEQUENCE", risk.riskID, cons, conn);

				} catch (RuntimeException e) {
					log.error("QRM Stack Trace", e);
				}

				try {

					String sql = "SELECT internalID,attachmentFileName, auditcomments.dateEntered, schedReviewDate,projectID, riskID,enteredByID, comment, commenturl, identification, evaluation, mitigation, review, approval, schedReview, schedReviewID,  stakeholders.name FROM auditcomments, stakeholders WHERE auditcomments.enteredByID = stakeholders.stakeholderID AND riskID="+ risk.riskID;
					Statement st = conn.createStatement();

					ResultSet rs = st.executeQuery(sql);
					String comm = null;
					while (rs.next()) {
						ModelAuditComment comment = new ModelAuditComment();
						comment.setComment(rs.getString("comment"));
						comment.setDateEntered(rs.getDate("dateEntered"));
						comment.setDateEntered(new Date(comment.getDateEntered().getTime()));
						comment.personName = rs.getString("name");

						if (comm == null) {
							comm = comment.personName+" ("+comment.dateEntered+") "+comment.comment+"\n\n";
						} else {
							comm = comm.concat(comment.personName+" ("+comment.dateEntered+") "+comment.comment+"\n\n");
						}
					}

					rs.close();

					setInnerReportSessionData(reportSessionID, "COMMENT", risk.riskID, comm, conn);


					String object = "";

					for (DTOObjectiveImpacted obj :(List<DTOObjectiveImpacted>)sess.getNamedQuery("getRiskObjectives").setLong("riskID",risk.riskID).list()){
						object = object.concat(objMap.get(obj.objectiveID)+"\n");
					}
					setInnerReportSessionData(reportSessionID, "OBJECTIVE", risk.riskID, object, conn);


				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
				}


				String contrls = "";
				for (ModelRiskControl ctl : new ArrayList<ModelRiskControl>(sess.getNamedQuery("getRiskControls").setLong("riskid", risk.riskID).list())){
					contrls = contrls.concat(ctl.control+"\n");
				}
				setInnerReportSessionData(reportSessionID, "CONTROL", risk.riskID, contrls, conn);

			}

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} 
	}
}
