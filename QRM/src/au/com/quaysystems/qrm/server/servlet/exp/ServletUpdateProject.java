package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.dto.ModelRiskProject;

@SuppressWarnings("serial")
@WebServlet (value = "/updateProject", asyncSupported = false)
public class ServletUpdateProject extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		ModelRiskProject project = null;
		try {
			JSONObject projectJS = (JSONObject) parser.parse(stringMap.get("DATA"));
			JSONObject projectJS2 = (JSONObject) parser.parse(stringMap.get("DATA2"));

			Transaction tx = sess.beginTransaction();

			ModelRiskProject checkproject = (ModelRiskProject) sess.getNamedQuery("checkRiskProjectCode").setLong(
					"projectID", projectID).setString(
							"projectCode",	(String) projectJS.get("projectCode"))	.uniqueResult();
			if (checkproject != null) {
				(response).setStatus(401);
				(response).getWriter().println(
						"Proect Code already in use. Changes not saved");
				return;
			}

			project = getRiskProject(projectID,sess);

			project.setProjectTitle((String) projectJS.get("projectTitle"));
			project.setProjectDescription((String) projectJS.get("projectDescription"));

			project.setProjectRiskManagerID(getLongJS(projectJS.get("projectRiskManagerID")));
			project.setProjectEndDate(df.parse(((String) projectJS.get("projectEndDate"))));
			project.setProjectStartDate(df.parse(((String) projectJS.get("projectStartDate"))));
			project.setProjectCode((String) projectJS.get("projectCode"));
			project.setMinimumSecurityLevel(getLongJS(projectJS.get("minimumSecurityLevel")).intValue());
			project.setUseAdvancedConsequences((Boolean) projectJS.get("useAdvancedConsequences"));
			project.setUseAdvancedLiklihood((Boolean) projectJS.get("useAdvancedLiklihood"));
			project.setSinglePhase((Boolean) projectJS.get("singlePhase"));

			Long mask = 3L;

			if (projectJS2.get("riskMitigation") != null && (Boolean)(projectJS2.get("riskMitigation"))) mask+=4;
			if (projectJS2.get("riskResponse") != null && (Boolean)(projectJS2.get("riskResponse"))) mask+=8;
			if (projectJS2.get("riskConsequences") != null && (Boolean)(projectJS2.get("riskConsequences"))) mask+=16;
			if (projectJS2.get("riskControls") != null && (Boolean)(projectJS2.get("riskControls"))) mask+=32;
			if (projectJS2.get("riskObjectives") != null && (Boolean)(projectJS2.get("riskObjectives"))) mask+=64;
			if (projectJS2.get("riskAttachment") != null && (Boolean)(projectJS2.get("riskAttachment"))) mask+=128;
			if (projectJS2.get("riskComments") != null && (Boolean)(projectJS2.get("riskComments"))) mask+=256;
			if (projectJS2.get("riskAudit") != null && (Boolean)(projectJS2.get("riskAudit"))) mask+=512;


			project.setTabsToUse(mask);

			sess.update(project);
			tx.commit();

		} catch (Exception e1) {
			log.error("QRM Stack Trace", e1);
			outputJSON(false, response);
		}

		outputJSON(getRiskProject(projectID, sess), response);


	}
}
