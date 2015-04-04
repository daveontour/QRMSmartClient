package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.ModelQuantImpactType;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskConsequence;
import au.com.quaysystems.qrm.dto.ModelRiskLite;
import au.com.quaysystems.qrm.dto.ModelRiskProject;
import au.com.quaysystems.qrm.server.montecarlo.MonteCarloProcessor;
import au.com.quaysystems.qrm.server.montecarlo.MonteEngineOutput;

@SuppressWarnings("serial")
@WebServlet (value = "/updateProjectContingency", asyncSupported = false)
public class ServletUpdateProjectContingency extends QRMRPCServlet {

	@SuppressWarnings("unchecked")
	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		List<ModelRiskLite> risks =getProjectRisksForUser(userID, Long.parseLong(stringMap.get("PROJECTID")),	(Boolean.parseBoolean(stringMap.get("DESCENDANTS"))) ? 1 : 0,sess);
		ModelRiskProject proj = (ModelRiskProject)sess.get(ModelRiskProject.class, projectID );
		Double contingencyPercentile = Double.parseDouble(stringMap.get("PERCENTILE"))/100;

		MonteCarloProcessor engine = new MonteCarloProcessor();
		HashMap<Long, ModelQuantImpactType> type = new HashMap<Long, ModelQuantImpactType>();

		List<ModelQuantImpactType> impactTypes = (List<ModelQuantImpactType>)sess.createSQLQuery("SELECT 0 AS GENERATION, quantimpacttype.* FROM quantimpacttype").addEntity(ModelQuantImpactType.class).list();
		for (ModelQuantImpactType t : impactTypes) {
			type.put(t.typeID, t);
		}


		for (ModelRiskLite riskLite : risks){

			//Skip any risks with no update rights
			if (!checkUpdateSecurity(riskLite.riskID, userID, request)) {
				continue;
			}
			ModelRisk risk = getRisk(riskLite.riskID, userID, Long.parseLong(stringMap.get("PROJECTID")), sess);
			risk.setProbConsequenceNodes(new ArrayList<ModelRiskConsequence>(getRiskConsequences(risk.getInternalID(), sess)));

			if (risk.getProbConsequenceNodes() == null || risk.getProbConsequenceNodes().size() == 0){
				continue;
			}
			boolean costTypeFound = false;
			for (ModelRiskConsequence node : risk.getProbConsequenceNodes()){
				if (node.getQuantImpactType().isCostCategroy()){
					costTypeFound = true;
					break;
				}
			}
			if (!costTypeFound) continue;
			List<ModelRisk> risksM = new ArrayList<ModelRisk>();
			risksM.add(risk);


			// Create a copy of the inpact type coz runMulti clears it
			List<ModelQuantImpactType> types = new ArrayList<ModelQuantImpactType>();
			for (ModelQuantImpactType t : impactTypes) {
				types.add(t);
			}

			MonteEngineOutput out = null;
			try {
				out = engine.runMulti(risksM, QRMConstants.NUMCONTINGENCY_ITERATIONS , risk.getBeginExposure(), risk.getEndExposure(),proj.parentID, true, true,types,sess);
			} catch (Exception e1) {
				log.error("QRM Stack Trace", e1);
			}

			int index = Math.min(QRMConstants.NUMCONTINGENCY_ITERATIONS-1, new Double(QRMConstants.NUMCONTINGENCY_ITERATIONS*contingencyPercentile).intValue());

			ArrayList<Double> preMitCostResult = out.getPreCostSummaryResult();
			ArrayList<Double> postMitCostResult = out.getPostCostSummaryResult();

			try {
				risk.preMitContingency = new Double(preMitCostResult.get(index).intValue());
				if(proj.singlePhase){
					risk.postMitContingency = new Double(preMitCostResult.get(index).intValue());
				} else {
					risk.postMitContingency = new Double(postMitCostResult.get(index).intValue());
				}
			} catch (Exception e2) {}


			// Create a copy of the impact type coz runMulti clears it
			types.clear();
			for (ModelQuantImpactType t : impactTypes) {
				types.add(t);
			}
			try {
				out = engine.runMulti(risksM, QRMConstants.NUMCONTINGENCY_ITERATIONS , risk.getBeginExposure(), risk.getEndExposure(),proj.parentID, false, false,types,sess);
			} catch (Exception e1) {
				log.error("QRM Stack Trace", e1);
			}

			ArrayList<Double> preMitCostResultWeighted = out.getPreCostSummaryResult();
			ArrayList<Double> postMitCostResultWeighted = out.getPostCostSummaryResult();


			try {
				risk.preMitContingencyWeighted = new Double(preMitCostResultWeighted.get(index).intValue());

				if(proj.singlePhase){
					risk.postMitContingencyWeighted = new Double(preMitCostResultWeighted.get(index).intValue());
				} else {
					risk.postMitContingencyWeighted = new Double(postMitCostResultWeighted.get(index).intValue());
				}
			} catch (Exception e1) {
			}

			try {
				Transaction tx = sess.beginTransaction();
				risk.contingencyPercentile = contingencyPercentile * 100;
				sess.update(risk);
				tx.commit();
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}
		}

	}
}
