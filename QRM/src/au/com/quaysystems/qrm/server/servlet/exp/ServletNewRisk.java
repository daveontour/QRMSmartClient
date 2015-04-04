package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.dto.ModelRiskLiteBasic;

@SuppressWarnings("serial")
@WebServlet (value = "/newRisk", asyncSupported = false)
public class ServletNewRisk extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		try {
			JSONObject riskJS = (JSONObject) parser.parse(stringMap.get("DATA"));

			ModelRiskLiteBasic risk = new ModelRiskLiteBasic();

			risk.setTitle((String) riskJS.get("title"));
			risk.setDescription((String) riskJS.get("description"));
			risk.setCause((String) riskJS.get("cause"));
			risk.setConsequences((String) riskJS.get("consequences"));
			risk.setSummaryRisk((Boolean) riskJS.get("summaryRisk"));
			risk.securityLevel = 0;

			// Insert trigger will figure out the correct matrix ID
			risk.matrixID = 0L;

			risk.manager1ID = getLongJS((String) riskJS.get("manager1ID"));
			risk.ownerID = getLongJS((String) riskJS.get("ownerID"));
			risk.projectID = getLongJS(riskJS.get("projectID"));

			risk.endExposure = df.parse(((String) riskJS.get("endExposure")).substring(0, 9));
			risk.startExposure = df.parse(((String) riskJS.get("startExposure")).substring(0, 9));

			sess.beginTransaction();
			sess.save(risk);
			sess.getTransaction().commit();

			//Refresh updates the risk after the insert triggers have been fired. The triggers set some data
			sess.refresh(risk);
			outputJSON2B(risk.riskProjectCode,response);

		} catch (Exception e1) {
			outputJSON(false,response);
		}
	}
}
