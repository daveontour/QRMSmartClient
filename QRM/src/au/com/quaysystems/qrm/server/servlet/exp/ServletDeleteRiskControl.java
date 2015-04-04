package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONArray;

import au.com.quaysystems.qrm.dto.ModelRiskControl;

@SuppressWarnings("serial")
@WebServlet (value = "/deleteRiskControl", asyncSupported = false)
public class ServletDeleteRiskControl extends QRMRPCServlet {

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
			for (Object obj : (JSONArray) parser.parse(stringMap.get("DATA"))) {
				ModelRiskControl control = (ModelRiskControl)sess.load(ModelRiskControl.class,(Long)obj);
				sess.beginTransaction();
				sess.delete(control);
				sess.getTransaction().commit();
			}
		} catch(Exception e){
			log.error("QRM Stack Trace", e);
		}
		outputJSON(getRisk(riskID, userID,projectID, sess),response);
		notifyUpdate(Long.parseLong(stringMap.get("RISKID")),request);

	}
}
