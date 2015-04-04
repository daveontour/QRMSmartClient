package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.ModelQuantImpactType;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskConsequence;
import au.com.quaysystems.qrm.dto.ModelRiskProject;
import au.com.quaysystems.qrm.server.montecarlo.MonteCarloProcessor;
import au.com.quaysystems.qrm.server.montecarlo.MonteEngineOutput;

@SuppressWarnings("serial")
@WebServlet (value = "/calcRiskContingency", asyncSupported = false)
public class ServletCalcRiskContingency extends QRMRPCServlet {

	@SuppressWarnings("unchecked")
	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		request.getSession().setAttribute("riskContingencyPreMitCostResult", null);
		request.getSession().setAttribute("riskContingencyPostMitCostResult", null);


		try {
			JSONObject riskJS = (JSONObject) parser.parse(stringMap.get("DATA"));

			ModelRisk risk = getRisk(riskID, userID,projectID, sess);
			ModelRiskProject proj = (ModelRiskProject)sess.get(ModelRiskProject.class, projectID );
			Double contingencyPercentile = getDoubleJS(riskJS.get("contingencyPercentile"))/100;

			MonteCarloProcessor engine = new MonteCarloProcessor();
			HashMap<Long, ModelQuantImpactType> type = new HashMap<Long, ModelQuantImpactType>();

			List<ModelQuantImpactType> impactTypes= sess.createSQLQuery("SELECT 0 AS GENERATION, quantimpacttype.* FROM quantimpacttype").addEntity(ModelQuantImpactType.class).list();
			for (ModelQuantImpactType t : impactTypes) {
				type.put(t.typeID, t);
			}
			risk.setProbConsequenceNodes(new ArrayList<ModelRiskConsequence>(getRiskConsequences(risk.getInternalID(), sess)));	

			// If using the provided Contingency, then the value is the result 
			if (!risk.useCalculatedContingency){

				ArrayList<Double> result = new ArrayList<Double>(QRMConstants.NUMCONTINGENCY_ITERATIONS);
				for (int i=0;i<QRMConstants.NUMCONTINGENCY_ITERATIONS;i++){
					result.add(risk.estimatedContingencey);
				}
				request.getSession().setAttribute("riskContingencyPreMitCostResult", result);
				request.getSession().setAttribute("riskContingencyPostMitCostResult", result);

				outputJSON(risk, response);
				return;
			}

			// Return if there are no consequences defined
			if (risk.getProbConsequenceNodes() == null || risk.getProbConsequenceNodes().size() == 0){
				outputJSON(risk,response);
				return;
			}

			boolean costTypeFound = false;
			for (ModelRiskConsequence node : risk.getProbConsequenceNodes()){
				if (node.getQuantImpactType().isCostCategroy()){
					costTypeFound = true;
					break;
				}
			}

			// There may have been consequences, but none were defined as cost based, so no need to proceed
			if (!costTypeFound){

				request.getSession().setAttribute("riskContingencyPreMitCostResult", null);
				request.getSession().setAttribute("riskContingencyPostMitCostResult", null);

				outputJSON(risk,response);
				return;
			}

			// Will use the MonteCarlo Engine, which expects a List of risks
			List<ModelRisk> risks = new ArrayList<ModelRisk>();
			risks.add(risk);

			int index = Math.min(QRMConstants.NUMCONTINGENCY_ITERATIONS-1, new Double(QRMConstants.NUMCONTINGENCY_ITERATIONS*contingencyPercentile).intValue());
			risk.contingencyPercentile = contingencyPercentile * 100;


			MonteEngineOutput out = null;
			try {
				out = engine.runMulti(risks, QRMConstants.NUMCONTINGENCY_ITERATIONS , risk.getBeginExposure(), risk.getEndExposure(),proj.parentID, true, true,impactTypes,sess);
			} catch (Exception e1) {
				log.error("QRM Stack Trace", e1);
			}
			ArrayList<Double> preMitCostResult = out.getPreCostSummaryResult();
			ArrayList<Double> postMitCostResult = out.getPostCostSummaryResult();

			risk.preMitContingency = new Double(preMitCostResult.get(index).intValue());
			if(proj.singlePhase){
				risk.postMitContingency = new Double(preMitCostResult.get(index).intValue());
			} else {
				risk.postMitContingency = new Double(postMitCostResult.get(index).intValue());
			}


			try {
				out = engine.runMulti(risks, QRMConstants.NUMCONTINGENCY_ITERATIONS , risk.getBeginExposure(), risk.getEndExposure(),proj.parentID, false, false,impactTypes,sess);
			} catch (Exception e1) {
				log.error("QRM Stack Trace", e1);
			}

			ArrayList<Double> preMitCostResultWeighted = out.getPreCostSummaryResult();
			ArrayList<Double> postMitCostResultWeighted = out.getPostCostSummaryResult();

			risk.preMitContingencyWeighted = new Double(preMitCostResultWeighted.get(index).intValue());
			if(proj.singlePhase){
				risk.postMitContingencyWeighted = new Double(preMitCostResultWeighted.get(index).intValue());
			} else {
				risk.postMitContingencyWeighted = new Double(postMitCostResultWeighted.get(index).intValue());
			}

			request.getSession().setAttribute("riskContingencyPreMitCostResult", preMitCostResultWeighted);
			request.getSession().setAttribute("riskContingencyPostMitCostResult", postMitCostResultWeighted);

			outputJSON(risk,response);
		} catch (Exception e){
			log.error("QRM Stack Trace", e);

			request.getSession().setAttribute("riskContingencyPreMitCostResult", null);
			request.getSession().setAttribute("riskContingencyPostMitCostResult", null);

			outputJSON(false,response);
			return;
		}

	}
}
