/*
 * 
 */
package au.com.quaysystems.qrm.util.probability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.apache.log4j.Logger;


public class RandomDiscreteBase {

	public static Random rand = new Random();
	ArrayList<ProbabilityNode> ProbArray = new ArrayList<ProbabilityNode>();
	double lowerLimit = 0;
	double upperLimit = 0;
	int indx = 0;
	boolean initialised = false;
	private Logger log = Logger.getLogger("au.com.quaysystems.qrm");

	public RandomDiscreteBase() {
		// Introduce slight delay so that two calls produce different patterns
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			log.error("QRM Stack Trace", e);
		}
		rand.setSeed(System.currentTimeMillis());
	}

	@SuppressWarnings("rawtypes")
	public void setParams(final ArrayList<Double> paramsObjects) {

		Iterator it = paramsObjects.iterator();
		while (it.hasNext()) {
			this.addDiscretePair((ProbabilityPair) it.next());
		}
	}

	public void addDiscretePair(final ProbabilityPair pair) {

		this.ProbArray.add(new ProbabilityNode(this.upperLimit, this.upperLimit + pair.probProportion, pair.value));
		this.upperLimit = this.upperLimit + pair.probProportion;
		this.initialised = true;
	}
	
	public double getPercentileValue(double percentile) {
		double uniformRandom = percentile * this.upperLimit;
		double max = 0.0;

		Iterator<ProbabilityNode> i = this.ProbArray.listIterator();
		while (i.hasNext()) {
			try {
				ProbabilityNode probNode = i.next();
				if (probNode.InRange(uniformRandom)) {
					max = probNode.getValue();
					break;
				}
			} catch (Exception e) {
				break;
			}
		}

		return max;
	}


	public double getNext() {

		double x = Double.NaN;
		double UniformRandom = rand.nextDouble() * (this.upperLimit);

		if (!this.initialised) {
			return (x);
		}

		Iterator<ProbabilityNode> i = this.ProbArray.listIterator();

		while (i.hasNext()) {
			try {
				ProbabilityNode probNode = i.next();
				if (probNode.InRange(UniformRandom)) {
					x = probNode.getValue();
					break;
				}
			} catch (Exception e) {
				break;
			}
		}
		return (x);

	}

	protected class ProbabilityNode {
		double value;
		double lowerProbLimit;
		double upperProbLimit;
		public ProbabilityNode(final double lower, final double upper,	final double value) {
			this.value = value;
			this.setLowerProbLimit(lower);
			this.setUpperProbLimit(upper);
		}

		public final double getLowerProbLimit() {
			return this.lowerProbLimit;
		}

		public final void setLowerProbLimit(final double lowerProbLimit) {
			this.lowerProbLimit = lowerProbLimit;
		}

		public final double getUpperProbLimit() {
			return this.upperProbLimit;
		}

		public final void setUpperProbLimit(final double upperProbLimit) {
			this.upperProbLimit = upperProbLimit;
		}

		public final double getValue() {
			return this.value;
		}

		public final void setValue(final double value) {
			this.value = value;
		}

		public final boolean InRange(final double x) {
			if ((x > this.lowerProbLimit) && (x <= this.upperProbLimit)) {
				return true;
			}
			return false;
		}
	}
}
