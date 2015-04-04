package au.com.quaysystems.qrm.server.montecarlo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.ModelQuantImpactType;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskConsequence;
import au.com.quaysystems.qrm.util.probability.DiscreteDistribution;
import au.com.quaysystems.qrm.util.probability.IRandNumGenDist;
import au.com.quaysystems.qrm.util.probability.NormalDistribution;
import au.com.quaysystems.qrm.util.probability.SimpleDistribution;
import au.com.quaysystems.qrm.util.probability.TriGenDistribution;
import au.com.quaysystems.qrm.util.probability.TriangularDistribution;
import au.com.quaysystems.qrm.util.probability.TruncNormalDistribution;
import au.com.quaysystems.qrm.util.probability.UniformDistribution;

public class MonteCarloEngineRiskSingle {

	private ArrayList<RiskLiteImpactNode> preConsequenceNodeList = new ArrayList<RiskLiteImpactNode>();
	private ArrayList<RiskLiteImpactNode> postConsequenceNodeList = new ArrayList<RiskLiteImpactNode>();
	private final HashMap<Long, Double> preTypeResult = new HashMap<Long, Double>();
	private final HashMap<Long, Double> postTypeResult = new HashMap<Long, Double>();
	private Date itStartDate = null;
	private Date itEndDate = null;
	private Double preProb = 1.0;
	private Double postProb = 1.0;
	private final Random rand = new Random();
	private boolean validPre = true;
	private boolean validPost = true;
	private ModelRisk risk;
	private boolean bSingle = false;
	private Logger log = Logger.getLogger("au.com.quaysystems.qrm");


	public final boolean isActiveNode(){
		return(preConsequenceNodeList.size() >0) || (postConsequenceNodeList.size() >0);
	}

	public MonteCarloEngineRiskSingle(final ModelRisk risk,  final List<ModelQuantImpactType> impactTypes, final boolean consActive) {
		this.risk = risk;
		this.rand.setSeed(System.currentTimeMillis());

		// The consequence nodes for the risk is created
		if (risk.getProbConsequenceNodes() != null) {
			for (ModelRiskConsequence impact : risk.getProbConsequenceNodes()) {
				if (impact.isQuantifiable()) {
					this.preConsequenceNodeList.add(new RiskLiteImpactNode(impact, QRMConstants.PRE,consActive));
					this.postConsequenceNodeList.add(new RiskLiteImpactNode(impact, QRMConstants.POST,consActive));
				}
			}
		}

		// For each type of impact, a result space is created
		for (ModelQuantImpactType type : impactTypes) {
			this.preTypeResult.put(type.getInternalID(), new Double(0.0));
			this.postTypeResult.put(type.getInternalID(), new Double(0.0));
		}
	}
	
	/**
	 * Set100 percent prob.
	 */
	public final void set100PercentProb() {
		this.preProb = 1.0;
		this.postProb = 1.0;

		this.validPre = true;
		this.validPost = true;
	}
	
	/**
	 * Inits the nodes.
	 */
	public final void initNodes() {

		if (this.validPre) {
			for (RiskLiteImpactNode node : this.preConsequenceNodeList) {
				node.initRun();
			}
		}

		if (this.validPost) {
			for (RiskLiteImpactNode node : this.postConsequenceNodeList) {
				node.initRun();
			}
		}
	}
	
	/**
	 * Inits the run.
	 * 
	 * @param impactTypes the impact types
	 */
	public final void initRun(final List<ModelQuantImpactType> impactTypes) {

		// Clear out previous result
		this.preTypeResult.clear();
		this.postTypeResult.clear();

		for (ModelQuantImpactType type : impactTypes) {
			this.preTypeResult.put(type.getInternalID(), 0.0);
			this.postTypeResult.put(type.getInternalID(), 0.0);
		}
	}
	
	/**
	 * Execute run.
	 */
	public final void executeRun() {

		// Determine if the consequence will be active for this run
		boolean preActive = false;
		boolean postActive = false;

		if (this.rand.nextDouble() <= this.preProb) {
			preActive = true;
		}
		if (this.rand.nextDouble() <= this.postProb) {
			postActive = true;
		} 

		if (this.validPre) {
			for (RiskLiteImpactNode node : this.preConsequenceNodeList) {
				node.runIteration(preActive);
				try {
					Long resultType = node.node.getQuantImpactType().getInternalID();
					Double val = this.preTypeResult.get(resultType)+node.getResult();

					this.preTypeResult.put(node.node.getQuantImpactType().getInternalID(), val);

				} catch (RuntimeException e) {
					log.error("QRM Stack Trace", e);
				}
			}
		}

		if (this.validPost) {
			for (RiskLiteImpactNode node : this.postConsequenceNodeList) {
				node.runIteration(postActive);
				try {
					Long resultType = node.node.getQuantImpactType().getInternalID();
					Double val = this.postTypeResult.get(resultType)+node.getResult();

					this.postTypeResult.put(resultType, val);

				} catch (RuntimeException e) {
					log.error("QRM Stack Trace", e);
				}
			}
		}
	}
	
	public final Double getRunCostResult(final int mode) {

		ArrayList<RiskLiteImpactNode> nodes;

		if (mode == QRMConstants.PRE) {
			nodes = this.preConsequenceNodeList;
		} else {
			nodes = this.postConsequenceNodeList;
		}

		double total = 0.0;

		for (RiskLiteImpactNode node : nodes) {
			try {
				if (node.getRiskConsequenceNode().getQuantImpactType().isCostCategroy()) {
					total = total + node.getResult();
				}
			} catch (RuntimeException e) {
				// Will catch instances where ther is no category/quant assigned
				// which we can ignore.
			}
		}

		return total;
	}
	
	public final Double getTypeRunResult(final Long type, final int mode) {

		try {
			if (mode == QRMConstants.PRE) {
				return this.preTypeResult.get(type);
			} else if (mode == QRMConstants.POST) {
				return this.postTypeResult.get(type);
			}
		} catch (RuntimeException e) {
			return null;
		}

		return null;
	}
	
	public final Double getNodeRunResult(final ModelRiskConsequence node, final int mode) {

		ArrayList<RiskLiteImpactNode> nodes;

		if (mode == QRMConstants.PRE) {
			nodes = this.preConsequenceNodeList;
		} else {
			nodes = this.postConsequenceNodeList;
		}

		for (RiskLiteImpactNode nodeA : nodes) {
			if (nodeA.getRiskConsequenceNode().equals(node)) {
				return nodeA.getResult();
			}
		}
		return null;
	}
	
	public final void setDates(final Date startDate, final Date endDate) {
		this.itStartDate = startDate;
		this.itEndDate = endDate;
	}
	
	public final void calcProb(final boolean riskActive) {
		
		if (riskActive){
			preProb = 1.0;
			postProb = 1.0;
			return;
		}
		
		if (this.bSingle) {
			this.itStartDate = (this.risk.getBeginExposure());
			this.itEndDate = (this.risk.getEndExposure());
		}
		try {
			preProb = MonteUtils.calcPreRiskProb(new Liklihood(risk.getLikeAlpha(), risk.getLikeProb(), risk.getLikeT(), risk.getLikeType(), risk.getLikePostAlpha(), risk.getLikePostProb(), risk.getLikePostT(), risk.getLikePostType()), this.itStartDate, this.itEndDate, this.risk);
		} catch (Exception e) {
			validPre = false;
		}
		try {
			postProb = MonteUtils.calcPostRiskProb(new Liklihood(risk.getLikeAlpha(), risk.getLikeProb(), risk.getLikeT(), risk.getLikeType(), risk.getLikePostAlpha(), risk.getLikePostProb(), risk.getLikePostT(), risk.getLikePostType()), this.itStartDate, this.itEndDate, this.risk);
		} catch (Exception e) {
			validPost = false;
		}
	}
	
	public final void setSingleRiskAnalysis() {
		this.bSingle = true;
	}

	private class RiskLiteImpactNode {

		private ModelRiskConsequence node;
		private Double result = 0.0;
		private IRandNumGenDist generator;
		private Double prob;
		private int mode;
		private final Random rand = new Random();
		private ArrayList<Double> temp = new ArrayList<Double>();

		public RiskLiteImpactNode(final ModelRiskConsequence node, final int mode, final boolean consActive) {
			this.mode = mode;
			this.node = node;

			if (mode == QRMConstants.PRE) {
				this.prob = node.getRiskConsequenceProb() / 100;
				try {
					this.generator = (IRandNumGenDist)Class.forName(node.getCostDistributionType()).newInstance();
				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
				}
			} else {
				this.prob = node.getPostRiskConsequenceProb() / 100;
				try {
					this.generator = (IRandNumGenDist)Class.forName(node.getPostCostDistributionType()).newInstance();
				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
				}
			}
			
			if (consActive) {
				this.prob = 1.0;
			}

			this.rand.setSeed(System.currentTimeMillis());
			for (int i = 0; i < 8; i++){
				this.temp.add(0.0);
			}
		}

		/**
		 * Inits the run.
		 */
		public void initRun() {

			// Not supporting Simple Model just yet

			if (this.mode == QRMConstants.PRE) {
				try {
					generator.setParams(setParamArray(this.node.getCostDistributionParams()));
				} catch (Exception e) {
					generator.setParams(temp);
				}
			} else {

				try {
					generator.setParams(setParamArray(this.node.getPostCostDistributionParams()));
				} catch (Exception e) {
					generator.setParams(temp);
				}
			}
		}
		
		private ArrayList<Double> setParamArray(ArrayList<Double> valArr){
			
	    	ArrayList<Double> params = new ArrayList<Double>();

	    	if (generator instanceof TruncNormalDistribution){
		    	params.add(valArr.get(0));
		    	params.add(valArr.get(1));
		    	params.add(valArr.get(5));
		    	params.add(valArr.get(6));
		    } else if (generator instanceof NormalDistribution){
		    	params.add(valArr.get(0));
		    	params.add(valArr.get(1));
		    }

	    	if (generator instanceof TriGenDistribution){
		    	params.add(valArr.get(2));
		    	params.add(valArr.get(3));
		    	params.add(valArr.get(4));
		    	params.add(valArr.get(5));
		    	params.add(valArr.get(6));
		    } else if (generator instanceof TriangularDistribution){
		    	params.add(valArr.get(2));
		    	params.add(valArr.get(3));
		    	params.add(valArr.get(4));
		    }
	    	
	    	if (generator instanceof UniformDistribution){
		    	params.add(valArr.get(5));
		    	params.add(valArr.get(6));
		    }
	    	if (generator instanceof SimpleDistribution){
		    	params.add(valArr.get(7));
		    }
	    	if (generator instanceof DiscreteDistribution){
	    		for (Double val : valArr){
	    			params.add(val);
	    		}
		    }
			return params;
		}

		public ModelRiskConsequence getRiskConsequenceNode() {
			return this.node;
		}

		public void runIteration(final boolean active) {
			if ((this.rand.nextDouble() <= this.prob) && active) {
				result = generator.getNext();
			} else {
				result = 0.0;
			}
		}
		public Double getResult() {
			return result;
		}
	}
}