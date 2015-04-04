package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelMitigationStep;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskConsequence;
import au.com.quaysystems.qrm.dto.ModelRiskControl;
import au.com.quaysystems.qrm.dto.ModelUpdateComment;

@SuppressWarnings("serial")
@WebServlet (value = "/getRisk", asyncSupported = false)
public class ServletGetRisk extends QRMRPCServlet {

	@SuppressWarnings("unchecked")
	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		log.info("Risk ID = "+riskID );

		// These session attributes are set when the user calls "calcContingency". By using getRisk
		// the contingency has changed, so the value is nulled out.
		request.getSession().setAttribute("riskContingencyPreMitCostResult", null);
		request.getSession().setAttribute("riskContingencyPostMitCostResult", null);

		try {
			
			ModelRisk risk = (ModelRisk) sess.getNamedQuery("getRisk")
					.setLong("userID", userID)
					.setLong("riskID", riskID)
					.setLong("projectID", projectID)
					.uniqueResult();

			try {
				risk.setMitigationPlan(new ArrayList<ModelMitigationStep>(getRiskMitigationSteps(risk.getInternalID(), sess)));
				List<ModelUpdateComment> updates = (List<ModelUpdateComment>)sess.getNamedQuery("getRiskMitigationStepsUpdate").setLong("riskID", riskID).list();
				//add any updates to the mitigation
				for (ModelMitigationStep step:risk.getMitigationPlan()){
					for(ModelUpdateComment comment:updates){
						step.addUpdate(comment);
					}
				}
				//			risk.setMitigationPlanUpdates(new ArrayList<ModelUpdateComment>(getRiskMitigationStepsUpdates(risk.getInternalID(), sess)));
				risk.setControls(new ArrayList<ModelRiskControl>(sess.getNamedQuery("getRiskControls").setLong("riskid", risk.getInternalID()).list()));
				risk.setObjectivesImpacted(getRiskObjectivesID(risk.getInternalID(), sess));
				risk.setProbConsequenceNodes(new ArrayList<ModelRiskConsequence>(getRiskConsequences(risk.getInternalID(), sess)));
			} catch (RuntimeException e) {
				log.error("QRM Stack Trace", e);
			}
			risk.userUpdateSecurity = checkUpdateSecurity(riskID, userID, request);
			outputJSON(risk,response);
		} catch (Exception e) {
			try {
				response.setStatus(405);
				response.getWriter().println("Error retrieving risk");
				return;
			} catch (IOException e1) {
				log.error("QRM Stack Trace", e1);
			}
		}
	}
}
