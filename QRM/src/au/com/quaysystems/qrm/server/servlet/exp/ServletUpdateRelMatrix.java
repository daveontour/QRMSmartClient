package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskProject;

@SuppressWarnings("serial")
@WebServlet (value = "/updateRelMatrix", asyncSupported = false)
public class ServletUpdateRelMatrix extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		try {
			JSONArray array = (JSONArray) parser.parse(stringMap.get("DATA"));

			try {
				Transaction tx = sess.beginTransaction();
				ModelRiskProject proj = getRiskProject(projectID, sess);

				for (Object item : array) {
					JSONObject it = (JSONObject) item;
					ModelRisk risk = getRisk((Long) it.get("riskID"), userID, proj.getInternalID(), sess);
					risk.inherentProb = (Double) (it.get("newUntreatedProb"));
					risk.inherentImpact = (Double) (it.get("newUntreatedImpact"));
					risk.treatedProb = (Double) (it.get("newTreatedProb"));
					risk.treatedImpact = (Double) (it.get("newTreatedImpact"));

					risk.setLikeProb(calcProb(risk, true));
					risk.setLikePostProb(calcProb(risk, false));

					sess.save(risk);
				}

				tx.commit();
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

		outputJSON(true,response);


	}
}
