package au.com.quaysystems.qrm.server.montecarlo;

import java.util.ArrayList;
import java.util.HashMap;

public class MonteEngineOutput {
	
	private HashMap<Long, ArrayList<Double>> preTypeResult;
	private HashMap<Long, ArrayList<Double>> postTypeResult;
	private ArrayList<Double> preCostSummaryResult;
	private ArrayList<Double> postCostSummaryResult;
	
	public MonteEngineOutput(HashMap<Long, ArrayList<Double>> preTypeResult,
			HashMap<Long, ArrayList<Double>> postTypeResult,
			ArrayList<Double> preCostSummaryResult,
			ArrayList<Double> postCostSummaryResult) {
		super();
		this.preTypeResult = preTypeResult;
		this.postTypeResult = postTypeResult;
		this.preCostSummaryResult = preCostSummaryResult;
		this.postCostSummaryResult = postCostSummaryResult;
	}

	public final ArrayList<Double> getPreResultType(final Long typeID) {
		return preTypeResult.get(typeID);
	}

	public final ArrayList<Double> getPostResultType(final Long typeID) {
		return postTypeResult.get(typeID);
	}

	public final ArrayList<Double> getPostCostSummaryResult() {
		return postCostSummaryResult;
	}

	public final ArrayList<Double> getPreCostSummaryResult() {
		return preCostSummaryResult;
	}
	public final HashMap<Long, ArrayList<Double>> getPreTypeResult() {
		return preTypeResult;
	}

	public final HashMap<Long, ArrayList<Double>> getPostTypeResult() {
		return postTypeResult;
	}

	public void setPreTypeResult(HashMap<Long, ArrayList<Double>> preTypeResult) {
		this.preTypeResult = preTypeResult;
	}

	public void setPostTypeResult(HashMap<Long, ArrayList<Double>> postTypeResult) {
		this.postTypeResult = postTypeResult;
	}

	public void setPreCostSummaryResult(ArrayList<Double> preCostSummaryResult) {
		this.preCostSummaryResult = preCostSummaryResult;
	}

	public void setPostCostSummaryResult(ArrayList<Double> postCostSummaryResult) {
		this.postCostSummaryResult = postCostSummaryResult;
	}

}
