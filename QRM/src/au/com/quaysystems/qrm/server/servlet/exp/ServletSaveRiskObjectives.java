package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONArray;

import au.com.quaysystems.qrm.dto.DTOObjectiveImpacted;

@SuppressWarnings("serial")
@WebServlet (value = "/saveRiskObjectives", asyncSupported = false)
public class ServletSaveRiskObjectives extends QRMRPCServlet {

	@SuppressWarnings("unchecked")
	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		// Check if the user has update rights to the risk.
		if (!checkUpdateSecurity(riskID, userID, request)) {
			outputJSON(false,response);
			try {
				response.getWriter().println("false");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error("QRM Stack Trace", e);
			}
			return;
		}

		try {
			Transaction tx = sess.beginTransaction();

			List<DTOObjectiveImpacted> objectives = (List<DTOObjectiveImpacted>)sess
					.getNamedQuery(	"getRiskObjectives")
					.setLong("riskID",	riskID)
					.list();

			for (DTOObjectiveImpacted obj : objectives) {
				boolean found = false;
				for (Object obj2 : (JSONArray) parser.parse(request.getParameter("DATA"))) {
					Long objID = getLongJS(obj2);
					if (objID.longValue() == obj.objectiveID.longValue()) {
						found = true;
						break;
					}
				}
				try {
					if (!found) {
						sess.delete(obj);
					}
				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
				}
			}
			tx.commit();

			tx = sess.beginTransaction();
			objectives = sess.getNamedQuery("getRiskObjectives")
					.setLong("riskID", riskID)
					.list();

			for (Object obj2 : (JSONArray) parser.parse(request.getParameter("DATA"))) {
				Long objID = getLongJS(obj2);
				boolean found = false;
				for (DTOObjectiveImpacted obj : objectives) {
					if (objID.longValue() == obj.objectiveID.longValue()) {
						found = true;
						break;
					}
				}
				if (!found) {
					sess.save(new DTOObjectiveImpacted(riskID, objID));
				}
			}
			tx.commit();
		} catch (Exception e1) {
			log.error("QRM Stack Trace", e1);
		}

		try {
			response.getWriter().println("true");
		} catch (IOException e) {
			log.error("QRM Stack Trace", e);
		}
		notifyUpdate(Long.parseLong(stringMap.get("RISKID")),request);

	}
}
