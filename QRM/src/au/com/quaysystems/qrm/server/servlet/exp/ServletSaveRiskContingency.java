package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskProject;

@SuppressWarnings("serial")
@WebServlet (value = "/saveRiskContingency", asyncSupported = false)
public class ServletSaveRiskContingency extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		// Check if the user has update rights to the risk.
		if (!checkUpdateSecurity(riskID, userID, request)) {
			outputJSON(false,response);
			return;
		}


		ModelRisk updatedRisk = null;
		try {
			JSONObject riskJS = (JSONObject) parser.parse(stringMap.get("DATA"));

			ModelRiskProject proj = (ModelRiskProject)sess.get(ModelRiskProject.class, projectID );

			double preC = 0;
			double postC = 0;

			preC = getDoubleJS(riskJS.get("preMitContingency"));
			try {
				postC = getDoubleJS(riskJS.get("postMitContingency"));
			} catch (Exception e) {}

			if (proj.singlePhase){
				postC = getDoubleJS(riskJS.get("preMitContingency"));
			}
			double preCW = 0;
			double postCW = 0;

			preCW = getDoubleJS(riskJS.get("preMitContingencyWeighted"));
			try {
				postCW = getDoubleJS(riskJS.get("postMitContingencyWeighted"));
			} catch (Exception e) {}

			if (proj.singlePhase){
				postCW = getDoubleJS(riskJS.get("preMitContingencyWeighted"));
			}

			Connection conn = getSessionConnection(request);
			PreparedStatement ps = conn.prepareStatement("UPDATE risk set preMitContingency = ?, postMitContingency=?,  preMitContingencyWeighted = ?, postMitContingencyWeighted=?, contingencyPercentile=? WHERE riskID=?");
			ps.setDouble(1, preC);
			ps.setDouble(2, postC);
			ps.setDouble(3, preCW);
			ps.setDouble(4, postCW);
			ps.setDouble(5, getDoubleJS(riskJS.get("contingencyPercentile")));
			ps.setLong(6,(Long) (riskJS.get("riskID")));
			ps.executeUpdate();

			closeAll(null,ps,conn);

			sess.flush();
			updatedRisk = getRisk((Long) (riskJS.get("riskID")), userID,projectID, sess);
			updatedRisk.setSecurityLevel(Math.max(updatedRisk.securityLevel,proj.getMinimumSecurityLevel()));

			outputJSON(updatedRisk,response);
			notifyUpdate((Long) (riskJS.get("riskID")), request);

		} catch (Exception e1) {
			log.error("QRM Stack Trace", e1);
			outputJSON(false,response);
			return;
		}

	}
}
