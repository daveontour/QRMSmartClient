package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.dto.ModelRisk;

@SuppressWarnings("serial")
@WebServlet (value = "/removeContributingRisk", asyncSupported = false)
public class ServletRemoveContributingRisk extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		ModelRisk risk = (ModelRisk) sess.getNamedQuery("getRisk")
				.setLong("userID", userID)
				.setLong("riskID", riskID)
				.setLong("projectID", projectID)
				.uniqueResult();
		
		if (risk.forceDownParent){
			try {
				response.getWriter().println("Cannot Remove Contributing Risks of a Propogated Risk");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}

		try {
			JSONArray risksJS = (JSONArray) parser.parse(stringMap.get("RISKS"));

			for (Object obj : risksJS) {
				JSONObject objJS = (JSONObject) obj;
				removeContributingRisk(riskID, (Long)objJS.get("riskID"), request);
			}
			response.getWriter().println("Risks Removed");
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
	}	
}
