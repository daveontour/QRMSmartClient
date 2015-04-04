package au.com.quaysystems.qrm.util.probability;

import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;

public class NormalDistribution implements IRandNumGenDist {

	public String ID = "au.com.quaysystems.qrm.util.probability.Normal";
	public Double sigma = null;
	public Double mu = null;
	protected final Random rand = new Random();
	private Logger log = Logger.getLogger("au.com.quaysystems.qrm");

	public NormalDistribution() {
		// Introduce slight delay so that two calls produce different patterns
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			log.error("QRM Stack Trace", e);
		}
		rand.setSeed(System.currentTimeMillis());
	}
	public void setParams(final ArrayList<Double> paramsValue) {
		
		if (paramsValue.get(0) == null || paramsValue.get(1) == null ) return;
		
		this.sigma = paramsValue.get(1);
		this.mu = paramsValue.get(0);

		if (this.sigma == null || mu == null) {
			rand.setSeed(System.currentTimeMillis());
			return;
		}
		if (sigma < 0 || mu <= 0) {
			this.sigma = 1.0;
			rand.setSeed(System.currentTimeMillis());
		}
	}
	
	public Object[] getDisplayData(){

		ArrayList<String> label = new ArrayList<String>();
		ArrayList<Double> prob = new ArrayList<Double>();

		double low = this.mu - this.sigma * 4;
		double high = this.mu + this.sigma * 4;
		double step = (high - low) / 41;

		int i = 0;
		for (double idx = low; idx <= high; idx = idx + step) {
			label.add(i, "");
			prob.add(i,getPDF(this.mu, this.sigma, idx) / 10);
			i++;
		}
		
		label.add(0, Double.toString((this.mu-(4*this.sigma))));
		label.add(10,Double.toString((this.mu-(2*this.sigma))));
		label.add(20, Double.toString(this.mu));
		label.add(30, Double.toString((this.mu+(2*this.sigma))));
		label.add(40,Double.toString((this.mu+(4*this.sigma))));

		return new Object[]{label.toArray(new String[]{}), prob.toArray(new Double[]{})};
	}

	public double getNext() {
		return sigma * rand.nextGaussian() + mu;
	}
	public static double getPDF(final double mean, final double s,	final double value) {
		return (1 / (s * Math.sqrt(Math.PI * 2)))
				* Math.exp(-((value - mean) * (value - mean) / (2 * s * s)));
	}

	@Override
	public String toString() {
		if (sigma == null || mu == null) {
			return "Normal(No Values Set)";
		} else {
			return "Normal(Mean=" + mu + ", Variance=" + sigma + ")";
		}
	}

	public String getID() {
		return ID;
	}

	@Override
	public double getPercentileValue(double percentitle) {
		// TODO FIX This so it is correct
		return 0;
	}
}