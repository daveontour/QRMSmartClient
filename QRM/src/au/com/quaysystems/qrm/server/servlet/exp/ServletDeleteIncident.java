package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelIncident;

@SuppressWarnings("serial")
@WebServlet (value = "/deleteIncident", asyncSupported = false)
public class ServletDeleteIncident extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		try {
			boolean someFailure = false;
			Long id = Long.parseLong(stringMap.get("INCIDENTID"));
			if (checkDeleteIncidentSecurity(userID, id,sess)){
				sess.beginTransaction();
				ModelIncident incident = (ModelIncident) sess.get(ModelIncident.class, id);
				sess.delete(incident);
				sess.getTransaction().commit();
			} else {
				someFailure = true;
			}

			outputJSONB(someFailure,response);
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

	}
}
