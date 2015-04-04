package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import au.com.quaysystems.qrm.dto.ModelIncident;

@SuppressWarnings("serial")
@WebServlet (value = "/findIncidentByIncident", asyncSupported = false)
public class ServletFindIncidentByIncident extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		ModelIncident incident = (ModelIncident)sess.createCriteria(ModelIncident.class)
				.add(Restrictions.eq("incidentProjectCode", request.getParameter("INCIDENTID")))
				.uniqueResult();
		
		if (incident != null){
			outputJSONB(incident, response);
		} else {
			outputJSONB(null,response);
		}
	}
}
