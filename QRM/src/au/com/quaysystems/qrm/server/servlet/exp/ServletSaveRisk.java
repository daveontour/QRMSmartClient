package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskProject;
import au.com.quaysystems.qrm.dto.ModelToleranceMatrix;

@SuppressWarnings("serial")
@WebServlet (value = "/saveRisk", asyncSupported = false)
public class ServletSaveRisk extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		log.info("Risk ID = "+riskID );

		// Check if the user has update rights to the risk.
		if (!checkUpdateSecurity(riskID, userID, request)) {
			outputJSON(getRisk(riskID, userID, projectID, sess),response);
			return;
		}


		ModelRisk updatedRisk = null;
		try {
			JSONObject riskJS = (JSONObject) parser.parse(stringMap.get("DATA"));

			ModelRiskProject project = (ModelRiskProject)sess.get(ModelRiskProject.class, projectID);
			Long pm = project.getProjectRiskManagerID();

			Transaction tx = sess.beginTransaction();
			ModelRisk risk = (ModelRisk) sess.getNamedQuery(	"getRisk").setLong("userID", userID)
					.setLong("riskID", (Long) (riskJS.get("riskID")))
					.setLong("projectID", (Long) (riskJS.get("projectID")))
					.uniqueResult();

			riskID = risk.riskID;
			risk.setTitle((String) riskJS.get("title"));
			risk.setDescription((String) riskJS.get("description"));
			risk.setCause((String) riskJS.get("cause"));
			risk.setMitPlanSummary((String) riskJS.get("mitPlanSummary"));
			risk.setMitPlanSummaryUpdate((String) riskJS.get("mitPlanSummaryUpdate"));
			risk.setImpact((String) riskJS.get("impact"));
			risk.setConsequences((String) riskJS.get("consequences"));
			risk.estimatedContingencey = getDoubleJS(riskJS.get("estimatedContingencey"));
			risk.useCalculatedContingency = (Boolean)riskJS.get("useCalculatedContingency");

			// Security Level may be set to a higher value by the database
			// trigger that ensure that
			// the security level is at least the minimum set for the project

			// Only current risk owner or risk manager can change security level, risk owner or risk manager

			if(userID.longValue() == risk.getManager1ID().longValue()
					|| userID.longValue() == risk.getOwnerID().longValue() || userID.longValue() == pm.longValue()){

				risk.setSecurityLevel(getLongJS(riskJS.get("securityLevel")).intValue());

				try {
					risk.setManager1ID(Long.parseLong((String) riskJS.get("manager1ID")));
				} catch (Exception e) {
					risk.setManager1ID((Long) riskJS.get("manager1ID"));
				}
				try {
					risk.setOwnerID(Long.parseLong((String) riskJS.get("ownerID")));
				} catch (Exception e) {
					risk.setOwnerID((Long) riskJS.get("ownerID"));
				}
			}

			risk.setSummaryRisk((Boolean) riskJS.get("summaryRisk"));

			risk.setImpCost((Boolean) riskJS.get("impCost"));
			risk.setImpEnvironment((Boolean) riskJS.get("impEnvironment"));
			risk.setImpReputation((Boolean) riskJS.get("impReputation"));
			risk.setImpSafety((Boolean) riskJS.get("impSafety"));
			risk.setImpSpec((Boolean) riskJS.get("impSpec"));
			risk.setImpTime((Boolean) riskJS.get("impTime"));

			risk.setTeatmentAvoidance((Boolean) riskJS.get("treatmentAvoidance"));
			risk.setTeatmentReduction((Boolean) riskJS.get("treatmentReduction"));
			risk.setTeatmentRetention((Boolean) riskJS.get("treatmentRetention"));
			risk.setTeatmentTransfer((Boolean) riskJS.get("treatmentTransfer"));



			try {
				try {
					risk.setPrimCatID((Long) riskJS.get("primCatID"));
				} catch (Exception e) {
					risk.setPrimCatID(Long.parseLong((String) riskJS.get("primCatID")));
				}
			} catch (Exception e1) {

			}
			try {
				try {
					risk.setSecCatID((Long) riskJS.get("secCatID"));
				} catch (Exception e) {
					risk.setSecCatID(Long.parseLong((String) riskJS.get("secCatID")));
				}
			} catch (Exception e) {

			}
			risk.setTreated((Boolean) riskJS.get("treated"));
			risk.setEndExposure(df.parse(((String) riskJS.get("endExposure"))));
			risk.setBeginExposure(df.parse(((String) riskJS.get("startExposure"))));

			risk.setTreatedProb(getDoubleJS(riskJS.get("treatedProb")));
			risk.setTreatedImpact(getDoubleJS(riskJS.get("treatedImpact")));

			risk.setInherentProb(getDoubleJS(riskJS.get("inherentProb")));
			risk.setInherentImpact(getDoubleJS(riskJS.get("inherentImpact")));

			risk.setLikeAlpha(getDoubleJS(riskJS.get("likealpha")));
			risk.setLikePostAlpha(getDoubleJS(riskJS.get("likepostAlpha")));

			risk.setLikeType(getLongJS(riskJS.get("liketype")).intValue());
			risk.setLikePostType(getLongJS(riskJS.get("likepostType")).intValue());

			risk.useCalculatedProb = (Boolean) riskJS.get("useCalculatedProb");

			if (!risk.useCalculatedProb){

				// Type "4" is for a fixed probability, which is set according to the position in the matrix
				// It is set here so that Monte Carlo analysis can make use of it.
				risk.setLikeType(4);
				risk.setLikePostType(4);

				ModelToleranceMatrix mat = getProjectMatrix(risk.getProjectID(), sess);
				setDynamicProb2(risk, mat,true);
				setDynamicProb2(risk,mat, false);

			} else {

				risk.setLikePostProb(null);
				risk.setLikeProb(null);


				switch(risk.getLikeType()){
				case 1:
					risk.setLikeT(365.0);
					break;
				case 2:
					risk.setLikeT(30.0);
					break;
				case 3:
					risk.setLikeT(getDoubleJS(riskJS.get("liket")));
					break;
				}

				switch(risk.getLikePostType()){
				case 1:
					risk.setLikePostT(365.0);
					break;
				case 2:
					risk.setLikePostT(30.0);
					break;
				case 3:
					risk.setLikePostT(getDoubleJS(riskJS.get("likepostT")));
					break;
				}
			}

			//			ModelRiskProject proj = txmgr.getRiskProject(risk.projectID, sess);

			sess.update(risk);
			tx.commit();

			sess.flush();

			updatedRisk = getRisk(risk.riskID, userID,risk.getProjectID(), sess);

			// The security level may have been changed by the database trigger,
			// so just make sure that
			// the returned risk has the minimum set.
			updatedRisk.userUpdateSecurity = checkUpdateSecurity(risk.riskID, userID, request);

		} catch (Exception e1) {
			outputJSON(false,response);
			return;
		}

		outputJSON(updatedRisk,response);
		notifyUpdate(riskID, request);


	}
	
}
