package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.dto.ModelMitigationStep;

@SuppressWarnings("serial")
@WebServlet (value = "/saveRiskMitigation", asyncSupported = false)
public class ServletSaveRiskMitigation extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		// Check if the user has update rights to the risk.
		if (!checkUpdateSecurity(riskID, userID, request)) {
			outputJSON(getRisk(riskID, userID, projectID, sess),response);
			return;
		}

		try {
			JSONArray controlsJS = (JSONArray) parser.parse(stringMap.get("DATA"));

			for (Object obj : controlsJS) {
				try {
					Transaction tx = sess.beginTransaction();
					JSONObject ctr = (JSONObject) obj;
					
					ModelMitigationStep step = null;
					
					 try {
						step  = (ModelMitigationStep)sess.get(ModelMitigationStep.class, (Long) ctr.get("mitstepID"));
					} catch (Exception e3) {
						step = new ModelMitigationStep();
					}
					 
					if (step == null){
						step = new ModelMitigationStep();
					}
					step.setResponse(false);
					
					
					try {
						step.setPersonResponsibleID(Long.parseLong((String) ctr.get("personID")));
					} catch (Exception e2) {
						
					}
					try {
						step.setEstimatedCost(getDoubleJS(ctr.get("estCost")));
					} catch (Exception e1) {
						step.setEstimatedCost(0.0);
					}

					try {
						step.setPercentComplete(getDoubleJS(ctr.get("percentComplete")));
					} catch (Exception e1) {
						step.setPercentComplete(0.0);
					}

					step.setEndDate(df.parse(((String) ctr.get("endDate")).substring(0, 10)));
					step.setProjectID(Long.parseLong(stringMap.get("PROJECTID")));
					step.setStepDescription((String) ctr.get("description"));
					step.setRiskID(Long.parseLong(stringMap.get("RISKID")));

					try {
						step.setInternalID((Long) ctr.get("mitstepID"));
					} catch (Exception e) {
					}
					sess.saveOrUpdate(step);
					tx.commit();
					sess.clear();
				} catch (Exception e) {
					log.error(e);
				}
			}

			sess.flush();

		} catch (Exception e1) {
			log.error("QRM Stack Trace", e1);
			outputJSON(false,response);
		}
		outputJSON(getRisk(riskID, userID,projectID, sess),response);
		notifyUpdate(Long.parseLong(stringMap.get("RISKID")),request);

	}
}
