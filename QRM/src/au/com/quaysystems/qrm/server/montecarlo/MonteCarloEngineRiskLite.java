package au.com.quaysystems.qrm.server.montecarlo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.apache.log4j.Logger;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.ModelQuantImpactType;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskConsequence;
import au.com.quaysystems.qrm.dto.ModelRiskProject;
import au.com.quaysystems.qrm.util.probability.DiscreteDistribution;
import au.com.quaysystems.qrm.util.probability.IRandNumGenDist;
import au.com.quaysystems.qrm.util.probability.NormalDistribution;
import au.com.quaysystems.qrm.util.probability.SimpleDistribution;
import au.com.quaysystems.qrm.util.probability.TriGenDistribution;
import au.com.quaysystems.qrm.util.probability.TriangularDistribution;
import au.com.quaysystems.qrm.util.probability.TruncNormalDistribution;
import au.com.quaysystems.qrm.util.probability.UniformDistribution;

public class MonteCarloEngineRiskLite  {

	public final ArrayList<RiskLiteImpactNode> preNodeList = new ArrayList<RiskLiteImpactNode>();
	public final ArrayList<RiskLiteImpactNode> postNodeList = new ArrayList<RiskLiteImpactNode>();
	public final ArrayList<Boolean> preActiveResult = new ArrayList<Boolean>();
	public final ArrayList<Boolean> postActiveResult = new ArrayList<Boolean>();
	public final HashMap<Long, ArrayList<Double>> preTypeResult = new HashMap<Long, ArrayList<Double>>();
	public final HashMap<Long, ArrayList<Double>> postTypeResult = new HashMap<Long, ArrayList<Double>>();
	private Logger log = Logger.getLogger("au.com.quaysystems.qrm");


	private Liklihood preLikelihood;
	private Liklihood postLikelihood;
	private Date startDate = null;
	private Date endDate = null;
	private int numIterations = 1000;
	private Double preProb;
	private Double postProb;
	private final Random rand = new Random();
	private boolean validData = true;
	private boolean validPre = true;
	private boolean validPost = true;
	private ModelRiskProject project;

	public MonteCarloEngineRiskLite(final ModelRisk risk, final ModelRiskProject project) {

		this.project = project;

		this.rand.setSeed(System.currentTimeMillis());

		try {
			for (ModelRiskConsequence impact : risk.getProbConsequenceNodes()) {
				this.preNodeList.add(new RiskLiteImpactNode(impact, QRMConstants.PRE));
				this.postNodeList.add(new RiskLiteImpactNode(impact, QRMConstants.POST));
				this.startDate = (risk.getBeginExposure());
				this.endDate = (risk.getEndExposure());
			}
		} catch (RuntimeException e) {
			this.validData = false;
			return;
		}

		try {
			this.preLikelihood = new Liklihood(risk.getLikeAlpha(), risk.getLikeProb(), risk.getLikeT(), risk.getLikeType(), risk.getLikePostAlpha(), risk.getLikePostProb(), risk.getLikePostT(), risk.getLikePostType());
			this.preProb = this.calcRiskProb(this.preLikelihood, QRMConstants.PRE);
		} catch (RuntimeException e) {
			this.validPre = false;
		}

		try {
			this.postLikelihood = new Liklihood(risk.getLikeAlpha(), risk.getLikeProb(), risk.getLikeT(), risk.getLikeType(), risk.getLikePostAlpha(), risk.getLikePostProb(), risk.getLikePostT(), risk.getLikePostType());
			this.postProb = this.calcRiskProb(this.postLikelihood, QRMConstants.POST);
		} catch (RuntimeException e) {
			this.validPost = false;

		}

		for (ModelQuantImpactType type : project.getImpactTypes()) {
			this.preTypeResult.put(type.getInternalID(),
					new ArrayList<Double>());
			this.postTypeResult.put(type.getInternalID(),
					new ArrayList<Double>());
		}

	}

	public final void set100PercentProb() {
		this.preProb = 1.0;
		this.postProb = 1.0;

		this.validPre = true;
		this.validPost = true;
	}

	public final void setNumIterations(final int num) {
		this.numIterations = Math.min(num, QRMConstants.ITERATIONSMAX);
	}

	public final int getNumIterations() {
		return this.numIterations;
	}

	private double calcRiskProb(final Liklihood like, final int mode) {
		double prob0 = 0;

		if (mode == QRMConstants.PRE) {

			if (like.getType().equals("PROB")) { //$NON-NLS-1$
				return this.preLikelihood.getProb() / 100;
			}

			try {
				double period = like.getT();
				double days = (this.endDate.getTime() - this.startDate.getTime())	/ (1000 * 60 * 60 * 24);

				if ((days == 0) || (period == 0)) {
					return 0;
				}

				double t = days / period;
				double alphat = like.getAlpha() * t;
				double kfact = QRMConstants.fact(0);
				prob0 = Math.exp(-alphat) * ((Math.pow(alphat, 0) / kfact));
			} catch (RuntimeException e) {
				prob0 = 0.0;
			}
		}
		if (mode == QRMConstants.POST) {

			if (like.getPostType().equals("PROB")) { //$NON-NLS-1$
				return this.postLikelihood.getPostProb() / 100;
			}

			try {
				double period = like.getPostT();
				double days = (this.endDate.getTime() - this.startDate.getTime()) / QRMConstants.DAYSMSEC;

				if ((days == 0) || (period == 0)) {
					return 0;
				}

				double t = days / period;
				double alphat = like.getPostAlpha() * t;
				prob0 = Math.exp(-alphat) * ((Math.pow(alphat, 0) / QRMConstants.fact(0)));
			} catch (RuntimeException e) {
				prob0 = 0.0;
			}
		}

		return 1 - prob0;

	}

	public final void initRun() {

		for (ModelQuantImpactType type : this.project.getImpactTypes()) {
			this.preTypeResult.get(type.getInternalID()).clear();
			this.postTypeResult.get(type.getInternalID()).clear();
		}

		if (!this.validData) {
			return;
		}

		if (this.validPre) {
			for (RiskLiteImpactNode node : this.preNodeList) {
				node.initRun();
			}
		}

		if (this.validPost) {
			for (RiskLiteImpactNode node : this.postNodeList) {
				node.initRun();
			}
		}
	}

	public final void executeRun() {

		if (!this.validData) {
			return;
		}

		for (int i = 1; i <= this.numIterations; i++) {

			if (this.validPre) {
				boolean preActive = (this.rand.nextDouble() <= this.preProb);
				this.preActiveResult.add(new Boolean(preActive));

				for (RiskLiteImpactNode node : this.preNodeList) {
					node.runIteration(preActive);
				}
			}

			if (this.validPost) {
				boolean postActive = (this.rand.nextDouble() <= this.postProb);

				this.postActiveResult.add(new Boolean(postActive));
				for (RiskLiteImpactNode node : this.postNodeList) {
					node.runIteration(postActive);
				}
			}
		}
	}

	public final ArrayList<Double> getRunCostResult(final int mode) {

		ArrayList<RiskLiteImpactNode> nodes;
		ArrayList<Double> res = new ArrayList<Double>();

		if (mode == QRMConstants.PRE) {
			nodes = this.preNodeList;
		} else {
			nodes = this.postNodeList;
		}

		for (int i = 0; i < this.numIterations; i++) {

			double total = 0.0;

			for (RiskLiteImpactNode node : nodes) {
				try {
					if (node.getRiskConsequenceNode().getQuantImpactType().isCostCategroy()) {
						total = total + node.getResultNum(i);
					}
				} catch (RuntimeException e) {
					// Will catch instances where there is no category/quant
					// assigned which we can ignore.

				}
			}

			res.add(total);
		}
		return res;
	}

	public final double getRiskActiveProb(final int mode) {

		ArrayList<Boolean> nodes;

		if (mode == QRMConstants.PRE) {
			nodes = this.preActiveResult;
		} else {
			nodes = this.postActiveResult;
		}

		int i = 0;
		for (Boolean runActive : nodes) {
			if (runActive.booleanValue()) {
				i++;
			}
		}
		return i / (double) nodes.size();
	}

	public final ArrayList<Double> getTypeRunResult(final Long type, final int mode) {

		if (mode == QRMConstants.PRE) {
			if (this.preTypeResult.get(type).size() > 0) {
				return this.preTypeResult.get(type);
			}
			return null;
		}

		if (this.postTypeResult.get(type).size() > 0) {
			return this.postTypeResult.get(type);
		}
		return null;
	}

	public final ArrayList<Double> getNodeRunResult(final ModelRiskConsequence node,
			final int mode) {

		ArrayList<RiskLiteImpactNode> nodes;

		if (mode == QRMConstants.PRE) {
			nodes = this.preNodeList;
		} else {
			nodes = this.postNodeList;
		}

		for (RiskLiteImpactNode nodeA : nodes) {
			if (nodeA.getRiskConsequenceNode().equals(node)) {
				return nodeA.getResults();
			}
		}
		return null;
	}

	private class RiskLiteImpactNode {

		private ModelRiskConsequence node;
		private final ArrayList<Double> results = new ArrayList<Double>();
		private IRandNumGenDist generator;
		private Double prob;
		private int mode;
		private final Random rand = new Random();

		public RiskLiteImpactNode(final ModelRiskConsequence node, final int mode) {
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

			this.rand.setSeed(System.currentTimeMillis());

		}

		public double getResultNum(final int i) {
			return this.results.get(i);
		}

		public void initRun() {
			this.results.clear();
			
			
			String[] valArr = null;
			
			if (this.mode == QRMConstants.PRE) {
				valArr = this.node.costDistributionParams.split(":");
			} else {
				valArr = this.node.postCostDistributionParams.split(":");
			}
	    	ArrayList<Double> params = new ArrayList<Double>();

	    	if (generator instanceof TruncNormalDistribution){
		    	params.add(Double.parseDouble(valArr[0]));
		    	params.add(Double.parseDouble(valArr[1]));
		    	params.add(Double.parseDouble(valArr[5]));
		    	params.add(Double.parseDouble(valArr[6]));
		    } else if (generator instanceof NormalDistribution){
		    	params.add(Double.parseDouble(valArr[0]));
		    	params.add(Double.parseDouble(valArr[1]));
		    }

	    	if (generator instanceof TriGenDistribution){
		    	params.add(Double.parseDouble(valArr[2]));
		    	params.add(Double.parseDouble(valArr[3]));
		    	params.add(Double.parseDouble(valArr[4]));
		    	params.add(Double.parseDouble(valArr[5]));
		    	params.add(Double.parseDouble(valArr[6]));
		    } else if (generator instanceof TriangularDistribution){
		    	params.add(Double.parseDouble(valArr[2]));
		    	params.add(Double.parseDouble(valArr[3]));
		    	params.add(Double.parseDouble(valArr[4]));
		    }
	    	
	    	if (generator instanceof UniformDistribution){
		    	params.add(Double.parseDouble(valArr[5]));
		    	params.add(Double.parseDouble(valArr[6]));
		    }
	    	if (generator instanceof SimpleDistribution){
		    	params.add(Double.parseDouble(valArr[7]));
		    }
	    	if (generator instanceof DiscreteDistribution){
	    		for (String val : valArr){
	    			params.add(Double.parseDouble(val));
	    		}
		    }
	    	

			this.generator.setParams(params);

		}

		public ModelRiskConsequence getRiskConsequenceNode() {
			return this.node;
		}

		public void runIteration(final boolean active) {
			if (!active) {
				this.results.add(0.0);
				if (this.mode == QRMConstants.PRE) {
					if (MonteCarloEngineRiskLite.this.preTypeResult
							.containsKey(this.node.getQuantImpactType()
									.getInternalID())) {
						MonteCarloEngineRiskLite.this.preTypeResult.get(
								this.node.getQuantImpactType().getInternalID())
								.add(0.0);
					}
				}
				if (this.mode == QRMConstants.POST) {
					if (MonteCarloEngineRiskLite.this.postTypeResult.containsKey(this.node.getQuantImpactType()
									.getInternalID())) {
						MonteCarloEngineRiskLite.this.postTypeResult.get(this.node.getQuantImpactType().getInternalID())
								.add(0.0);
					}
				}
				return;
			}

			if (this.rand.nextDouble() <= this.prob) {

				double x = this.generator.getNext();
				this.results.add(x);
				if (this.mode == QRMConstants.PRE) {
					if (MonteCarloEngineRiskLite.this.preTypeResult
							.containsKey(this.node.getQuantImpactType().getInternalID())) {
						MonteCarloEngineRiskLite.this.preTypeResult.get(
								this.node.getQuantImpactType().getInternalID()).add(x);
					}
				}
				if (this.mode == QRMConstants.POST) {
					if (MonteCarloEngineRiskLite.this.postTypeResult
							.containsKey(this.node.getQuantImpactType().getInternalID())) {
						MonteCarloEngineRiskLite.this.postTypeResult.get(
								this.node.getQuantImpactType().getInternalID()).add(x);
					}
				}
			} else {
				this.results.add(0.0);
				if (this.mode == QRMConstants.PRE) {

					if (MonteCarloEngineRiskLite.this.preTypeResult
							.containsKey(this.node.getQuantImpactType().getInternalID())) {
						MonteCarloEngineRiskLite.this.preTypeResult.get(
								this.node.getQuantImpactType().getInternalID()).add(0.0);
					}
				}
				if (this.mode == QRMConstants.POST) {
					if (MonteCarloEngineRiskLite.this.postTypeResult
							.containsKey(this.node.getQuantImpactType().getInternalID())) {
						MonteCarloEngineRiskLite.this.postTypeResult.get(
								this.node.getQuantImpactType().getInternalID()).add(0.0);
					}
				}
			}
		}

		public ArrayList<Double> getResults() {
			return this.results;
		}
	}

	public final void setUpdatedPreLikelihood(final Liklihood like) {
		this.preLikelihood = like;
		this.preProb = this.calcRiskProb(like, QRMConstants.PRE);
	}

	public final void setUpdatedPostLikelihood(final Liklihood like) {
		this.postLikelihood = like;
		this.postProb = this.calcRiskProb(like, QRMConstants.POST);
	}
}
