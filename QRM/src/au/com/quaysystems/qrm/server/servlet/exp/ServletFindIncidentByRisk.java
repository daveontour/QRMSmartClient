package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelIncident;

@SuppressWarnings("serial")
@WebServlet (value = "/findIncidentByRisk", asyncSupported = false)
public class ServletFindIncidentByRisk extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		
		String sql = "SELECT DISTINCT *, incidentRisk.riskID, risk.riskProjectCode FROM incident "
				+ " NATURAL JOIN incidentRisk	"
				+ "JOIN risk ON incidentRisk.riskID = risk.riskID WHERE riskProjectCode = '"+stringMap.get("RISKCODE")+"'";
		
		System.out.println(sql);

		outputJSON(sess.createSQLQuery(sql).addEntity(ModelIncident.class).list(),response);
	}
}
