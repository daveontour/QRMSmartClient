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
@WebServlet (value = "/saveRiskResponse", asyncSupported = false)
public class ServletSaveRiskResponse extends QRMRPCServlet {

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

			sess.clear();

			for (Object obj : controlsJS) {
				JSONObject ctr = (JSONObject) obj;
				ModelMitigationStep step = new ModelMitigationStep();
				step.setResponse(true);
				step.setPersonResponsibleID(userID);
				step.setProjectID(Long.parseLong(stringMap.get("PROJECTID")));
				step.setStepDescription((String) ctr.get("description"));
				step.setRiskID(Long.parseLong(stringMap.get("RISKID")));
				step.setInternalID((Long) ctr.get("mitstepID"));
				step.setPercentComplete(100.0);

				Transaction tx = sess.beginTransaction();
				sess.update(step);
				tx.commit();
				sess.clear();
			}

		} catch (Exception e1) {
			log.error("QRM Stack Trace", e1);
			outputJSON(false,response);
		}

		outputJSON(getRisk(riskID, userID, projectID, sess),response);
		notifyUpdate(Long.parseLong(stringMap.get("RISKID")), request);

	}
}
