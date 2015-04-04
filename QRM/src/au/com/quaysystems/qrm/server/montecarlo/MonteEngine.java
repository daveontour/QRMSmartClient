package au.com.quaysystems.qrm.server.montecarlo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.ModelQuantImpactType;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskConsequence;
import au.com.quaysystems.qrm.dto.ModelRiskProject;

public class MonteEngine {

	HashMap<Long, ArrayList<Double>> preTypeResult = new HashMap<Long, ArrayList<Double>>();
	HashMap<Long, ArrayList<Double>> postTypeResult = new HashMap<Long, ArrayList<Double>>();
	ArrayList<Double> preCostSummaryResult = new ArrayList<Double>();
	ArrayList<Double> postCostSummaryResult = new ArrayList<Double>();

	MonteEngineInput in;
	MonteEngineOutput out;


	public MonteEngine(MonteEngineInput in){
		this.in = in;
	}


	public MonteEngineOutput run(){ 

		ArrayList<MonteCarloEngineRiskSingle> engineRisks = new ArrayList<MonteCarloEngineRiskSingle>();

		try {
			for (ModelRisk risk : in.risks) {

				// if there are no consequence nodes defined, then there is nothing to do
				if (risk.getProbConsequenceNodes().size() <= 0 && (risk.estimatedContingencey != null && !risk.useCalculatedContingency)) {
					continue;
				}

				// 2 is the id for "Unspecified Ontingency type
				if (risk.estimatedContingencey != null && !risk.useCalculatedContingency){
					// Filter out all the cost related consequences, coz the cost is take care of by the 
					// supplied contingency
					ArrayList<ModelRiskConsequence> filteredTypes = new ArrayList<ModelRiskConsequence>();
					for (ModelRiskConsequence con : risk.getProbConsequenceNodes()) {
						if (!in.impactTypeMap.get(con.quantType).isCostCategroy()) {
							filteredTypes.add(con);
						}
					}
					risk.setProbConsequenceNodes(filteredTypes);

					// Add the contingency node
					ModelRiskConsequence contingencyNode = new ModelRiskConsequence();
					contingencyNode.setQuantImpactType(in.impactTypeMap.get(2L));
					contingencyNode.setQuantifiable(true);

					contingencyNode.setPostRiskConsequenceProb(100.0);
					contingencyNode.setRiskConsequenceProb(100.0);

					contingencyNode.costDistributionParams = "0.0;0.0;0.0;0.0;0.0;0.0;0.0;"+risk.estimatedContingencey.toString();
					contingencyNode.postCostDistributionParams = "0.0;0.0;0.0;0.0;0.0;0.0;0.0;"+risk.estimatedContingencey.toString();

					ArrayList<Double> temp = new ArrayList<Double>();
					for (int i = 0; i < 7; i++){
						temp.add(0.0);
					}
					temp.add(risk.estimatedContingencey);
					contingencyNode.setCostDistributionParamsArray(temp);
					contingencyNode.setPostCostDistributionParamsArray(temp);
					contingencyNode.setCostDistributionType("au.com.quaysystems.qrm.util.probability.SimpleDistribution");
					contingencyNode.setPostCostDistributionType("au.com.quaysystems.qrm.util.probability.SimpleDistribution");
					contingencyNode.setDescription("Contingency Cost");
					contingencyNode.setInternalID(-100L);
					contingencyNode.quantType = 2L;

					risk.getProbConsequenceNodes().add(contingencyNode);

				}

				// Create results only for the types found (may over write)
				for (ModelRiskConsequence con : risk.getProbConsequenceNodes()) {
					preTypeResult.put(con.quantType, new ArrayList<Double>());
					postTypeResult.put(con.quantType, new ArrayList<Double>());
					if (!in.impactTypes.contains(con.getQuantImpactType())) {
						in.impactTypes.add(con.getQuantImpactType());
					}
				}

				Date start = risk.getBeginExposure();
				Date end = risk.getEndExposure();

				ModelRiskProject proj = in.projects.get(risk.projectID);

				if (start == null) {
					risk.setBeginExposure(proj.getProjectStartDate());
				}
				if (end == null) {
					risk.setEndExposure(proj.getProjectEndDate());
				}

				if (risk.getBeginExposure().after(risk.getEndExposure())) {
					risk.setBeginExposure(proj.getProjectStartDate());
					risk.setEndExposure(proj.getProjectEndDate());
				}

				MonteCarloEngineRiskSingle riskNode = new MonteCarloEngineRiskSingle(risk, in.impactTypes, in.conActive);
				riskNode.setDates(in.startDate, in.endDate);
				riskNode.calcProb(in.riskActive);
				riskNode.initNodes();
				engineRisks.add(riskNode);
			}

			runEngine(engineRisks, in.its, in.impactTypes);
			sortAndCompile(in.its, in.impactTypes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new MonteEngineOutput(preTypeResult,	postTypeResult,	preCostSummaryResult, postCostSummaryResult);
	}

	private void runEngine(
			final ArrayList<MonteCarloEngineRiskSingle> engineRisks,
			final int its, final List<ModelQuantImpactType> impactTypes) {
		// Create a set of nodes which actually have consequences to analyze

		ArrayList<MonteCarloEngineRiskSingle> activeNodes = new ArrayList<MonteCarloEngineRiskSingle>();
		for (MonteCarloEngineRiskSingle activeNode : engineRisks) {
			if (activeNode.isActiveNode()) {
				activeNodes.add(activeNode);
			}
		}

		for (int i = 0; i < its; i++) {

			// Make sure processing does not soak up too much energy at the
			// expense of others
			if (i % 100 == 0) {
				Thread.yield();
			}

			// Create the result space for it
			for (ModelQuantImpactType type : impactTypes) {

				preTypeResult.get(type.getInternalID()).add(new Double(0.0));
				postTypeResult.get(type.getInternalID()).add(new Double(0.0));
			}

			// Go through each of the nodes and run
			for (MonteCarloEngineRiskSingle riskNode : activeNodes) {

				riskNode.initRun(impactTypes);
				riskNode.executeRun();

				// Collect the results for each type
				for (ModelQuantImpactType type : impactTypes) {

					try {
						double val = preTypeResult.get(type.getInternalID()).get(i)	+ riskNode.getTypeRunResult(type.getInternalID(), QRMConstants.PRE);

						preTypeResult.get(type.getInternalID()).remove(i);
						preTypeResult.get(type.getInternalID()).add(val);

						val = postTypeResult.get(type.getInternalID()).get(i)+ riskNode.getTypeRunResult(type.getInternalID(), QRMConstants.POST);

						postTypeResult.get(type.getInternalID()).remove(i);
						postTypeResult.get(type.getInternalID()).add(val);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void sortAndCompile(final int iterations,
			final List<ModelQuantImpactType> impactTypes) {

		for (int i = 0; i < iterations; i++) {
			preCostSummaryResult.add(0.0);
		}
		for (int i = 0; i < iterations; i++) {
			postCostSummaryResult.add(0.0);
		}

		// For Each quantity type, sort the result
		for (ModelQuantImpactType type : impactTypes) {
			Thread.yield();
			try {

				if (preTypeResult.get(type.getInternalID()) != null) {
					Collections.sort(preTypeResult.get(type.getInternalID()));
				}
				if (postTypeResult.get(type.getInternalID()) != null) {
					Collections.sort(postTypeResult.get(type.getInternalID()));
				}

				// If it is a cost type, then add it to the total cost incurred
				if (type.isCostCategroy()) {
					ArrayList<Double> preCost = preTypeResult.get(type.getInternalID());
					ArrayList<Double> postCost = postTypeResult.get(type.getInternalID());

					int i = 0;
					for (Double d : preCost) {
						preCostSummaryResult.set(i, preCostSummaryResult.get(i++)+ d);
					}
					int j = 0;
					for (Double d : postCost) {
						postCostSummaryResult.set(j, postCostSummaryResult.get(j++)+ d);
					}
					preCost = null;
					postCost = null;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (preCostSummaryResult != null) {
				Collections.sort(preCostSummaryResult);
			}
			if (postCostSummaryResult != null) {
				Collections.sort(postCostSummaryResult);
			}
		}
	}
}
