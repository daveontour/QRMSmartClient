package au.com.quaysystems.qrm.util.probability;

import java.util.ArrayList;

public class TriangularDistribution extends RandomDiscreteBase implements IRandNumGenDist {
	
	public String ID = "au.com.quaysystems.qrm.util.probability.Triangular";
	public Double min;
	public Double mostlikely;
	public Double max;
	double interval;
	int precision;
	static final int MAX_PRECISION = 2000;

	public TriangularDistribution() {
		super();
	}
	 
	public void setParams(final ArrayList<Double> paramsValue) {

		this.min = paramsValue.get(0).doubleValue();
		this.mostlikely = paramsValue.get(1).doubleValue();
		this.max = paramsValue.get(2).doubleValue();

		this.precision = MAX_PRECISION;
		double prob = 0;

		this.precision = (precision < MAX_PRECISION) ? precision: MAX_PRECISION;
		this.precision = ((max - min) < this.precision) ? (int) (max - min)	: this.precision;
		this.interval = (max - min) / this.precision;

		for (int i = 0; i < this.precision; i++) {
			double val = min + i * this.interval;

			if (val <= mostlikely) {
				prob = 2 * (val - min) / ((max - min) * (mostlikely - min));
			} else {
				prob = 2 * (max - val) / ((max - min) * (max - mostlikely));
			}
			addDiscretePair(new ProbabilityPair(val, prob));
		}
	}

	public double getNext() {
		return (super.getNext());
	}

	public String toString() {
		if (min == null || max == null || mostlikely == null) {
			return "Triangular(No Values Set)";
		} else {
			return "Triangular(Min=" + min + ", Most Likley=" + mostlikely
					+ ", Max=" + max + ")";
		}
	}

	public String getID() {
		return ID;
	}

	@Override
	public Object[] getDisplayData() {

		ArrayList<String> label = new ArrayList<String>();
		ArrayList<Double> prob = new ArrayList<Double>();
		boolean mostLock = false;

		double step = (this.max - this.min)/40;

		int i = 0;
		for (double idx = this.min; idx <= this.max; idx = idx+step) {
			if (!mostLock && (idx == this.mostlikely || (((idx - step)< this.mostlikely) && ((idx+step) > this.mostlikely)))){
				label.add(i, Double.toString(this.mostlikely));
				mostLock = true;
			} else if (idx == this.min){
				label.add(i, Double.toString(this.min));
			} else if (idx == this.max || ((idx - step) > (this.max - step)) ){
				label.add(i, Double.toString(this.max));
			} else {
				label.add(i, "");
			}

			if (idx < this.mostlikely){
				double interval = this.mostlikely - this.min;
				prob.add(i, 1*((idx - this.min)/interval));
			} else 	if (idx == this.mostlikely || (((idx - step)< this.mostlikely) && ((idx+step) > this.mostlikely))){
				prob.add(i, 1.0);
			} else if (idx > this.mostlikely){
				double interval = this.max - this.mostlikely;
				prob.add(i, 1*((this.max - idx)/interval));		
			}			
			i++;
		}

		return new Object[]{label.toArray(new String[]{}), prob.toArray(new Double[]{})};
	}
}