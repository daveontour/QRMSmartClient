/*
 * 
 */

package au.com.quaysystems.qrm.util.probability;

import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;

public class UniformDistribution implements IRandNumGenDist {
	
	public String ID = "au.com.quaysystems.qrm.util.probability.Uniform";
	Double lower;
	Double upper;
	Random rand = new Random();
	private Logger log = Logger.getLogger("au.com.quaysystems.qrm");

	public UniformDistribution() {
		// Introduce slight delay so that two calls produce different patterns
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			log.error("QRM Stack Trace", e);
		}
		rand.setSeed(System.currentTimeMillis());
	}

	public final void setParams(final ArrayList<Double> paramsValue)
			throws IllegalArgumentException {

		this.lower = paramsValue.get(0);
		this.upper = paramsValue.get(1);

		if (lower >= upper) {
			throw new IllegalArgumentException(
					"upper bound must be > lower bound");
		}
	}

	public final double getNext() {
		return lower + rand.nextDouble() * (upper - lower + 1);
	}

	 
	public final String toString() {
		if (lower == null || upper == null) {
			return "Uniform(No Values Set)";
		} else {
			return "Uniform(Lower=" + lower + ", Upper=" + upper + ")";
		}
	}

	public final String getID() {
		return ID;
	}

	@Override
	public Object[] getDisplayData() {
		
		ArrayList<String> label = new ArrayList<String>();
		ArrayList<Double> prob2 = new ArrayList<Double>();

		if (lower == upper) {
			return null;
		}
		
		double step = (upper - lower) / 20;

		int  i = 0;
		for (double idx = lower; idx <= upper; idx = idx + step) {
			label.add(i,Double.toString(Math.round(idx)));
			prob2.add(i++, 1.0 / 20.0);
		}
		return new Object[]{label.toArray(new String[]{}), prob2.toArray(new Double[]{})};
	}

	@Override
	public double getPercentileValue(double percentile) {
		return lower+(upper-lower)*percentile;
	}
}
