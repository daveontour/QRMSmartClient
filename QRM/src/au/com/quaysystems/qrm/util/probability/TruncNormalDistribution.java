package au.com.quaysystems.qrm.util.probability;

import java.util.ArrayList;

public class TruncNormalDistribution extends NormalDistribution implements IRandNumGenDist {

	public String ID = "au.com.quaysystems.qrm.util.probability.TruncNormal";
	Double lowerLimit;
	Double upperLimit;
	public TruncNormalDistribution() {
		super();
	}

	public final void setParams(final ArrayList<Double> paramsValue) {


		this.sigma = paramsValue.get(1);
		this.mu = paramsValue.get(0);
		this.lowerLimit = paramsValue.get(2);
		this.upperLimit = paramsValue.get(3);

		if (lowerLimit >= upperLimit) {
			throw new IllegalArgumentException(
					"Lower truncation point must be less than upper truncation point");
		}

		if (this.sigma == null || mu == null) {
			rand.setSeed(System.currentTimeMillis());
			return;
		}
		if (sigma < 0 || mu <= 0) {
			this.sigma = 1.0;
			rand.setSeed(System.currentTimeMillis());
		}
	}

	 
	public final double getNext() {

		double x = Double.NaN;

		do {
			x = super.getNext();
		} while (x < lowerLimit || x > upperLimit);

		return (x);
	}

	public static double getPDF(final double mean, final double s, final double value,
			final double lower, final double upper) {

		if (value < lower || value > upper) {
			return 0;
		}
		return (1 / (s * Math.sqrt(Math.PI * 2)))
				* Math.exp(-((value - mean) * (value - mean) / (2 * s * s)));
	}

	public final String toString() {
		if (mu == null || sigma == null || lowerLimit == null
				|| upperLimit == null) {
			return "Trunc Normal(No Values Set)";
		} else {
			return "Trunc Normal(Mean=" + mu + ", Variance=" + sigma
					+ ", Lower Limit=" + lowerLimit + ", Upper Limit="
					+ upperLimit + ")";
		}
	}

	 
	public final String getID() {
		return ID;
	}
	
	public Object[] getDisplayData(){

		ArrayList<String> label = new ArrayList<String>();
		ArrayList<Double> prob = new ArrayList<Double>();

		double low = this.mu - this.sigma * 4;
		double high = this.mu + this.sigma * 4;
		double step = (high - low) / 41;

		int i = 0;
		for (double idx = low; idx <= high; idx = idx + step) {

			if (i == 0) {
				label.add(i,Double.toString((this.mu - this.sigma * 4)));
			} else if (i==20){
				label.add(i,Double.toString(this.mu));        		
			}else if (i==40){
				label.add(i,Double.toString(this.mu + this.sigma * 4));
			} else if ((idx == this.lowerLimit)|| (idx < this.lowerLimit && idx+step > this.lowerLimit)&& (this.lowerLimit != this.mu - 4 * this.sigma)){
				label.add(i,Double.toString(this.lowerLimit));
			}else if ((idx == this.upperLimit)|| (idx < this.upperLimit && idx+step > this.upperLimit) && (this.upperLimit != this.mu + 4 * this.sigma)){
				label.add(i,Double.toString(this.upperLimit));
			} else {
				label.add(i,"");
			}

			prob.add(i,getPDF(this.mu, this.sigma, idx, this.lowerLimit, this.upperLimit) / 10);
			i++;
		}
			
		return new Object[]{label.toArray(new String[]{}), prob.toArray(new Double[]{})};
	}
}
