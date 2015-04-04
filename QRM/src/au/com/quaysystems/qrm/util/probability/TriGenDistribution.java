package au.com.quaysystems.qrm.util.probability;

import java.util.ArrayList;

public class TriGenDistribution extends TriangularDistribution implements IRandNumGenDist {

	public String ID = "au.com.quaysystems.qrm.util.probability.TriGen";
	Double minLimit;
	Double maxLimit;

	public final void setParams(final ArrayList<Double> paramsValue) {

		this.min = paramsValue.get(0);
		this.mostlikely = paramsValue.get(1);
		this.max = paramsValue.get(2);
		this.minLimit = paramsValue.get(3);
		this.maxLimit = paramsValue.get(4);

		this.precision = MAX_PRECISION;

		this.precision = (precision < MAX_PRECISION) ? precision : MAX_PRECISION;
		this.precision = ((max - min) < this.precision) ? (int) (max - min)	: this.precision;
		this.interval = (max - min) / this.precision;

		for (int i = 0; i < this.precision; i++) {
			double val = min + i * this.interval;

			if (val <= mostlikely) {
				addDiscretePair(new ProbabilityPair(val, 2 * (val - min) / ((max - min) * (mostlikely - min))));
			} else {
				addDiscretePair(new ProbabilityPair(val, 2 * (max - val) / ((max - min) * (max - mostlikely))));
			}
		}
	}

	public TriGenDistribution() {
			super();
	}

	public final double getNext() {

		if (this.minLimit >= this.min || this.maxLimit <= this.max) {
			return 0.0;
		}

		double x = Double.NaN;

		do {
			x = super.getNext();
		} while (x < minLimit || x > maxLimit);

		return (x);
	}

	public final String toString() {
		if (min == null || max == null || mostlikely == null
				|| minLimit == null || maxLimit == null) {
			return "Trunc Tri(No Values Set)";
		} else {
			return "Trunc Tri(Min=" + min + ", Most Likley=" + max + ", Max="
					+ max + ", Lower Limit=" + minLimit + ", Upper Limit="
					+ maxLimit + ")";
		}

	}

	public final String getID() {
		return ID;
	}
	public Object[] getDisplayData() {

		ArrayList<String> label = new ArrayList<String>();
		ArrayList<Double> prob = new ArrayList<Double>();
		boolean mostLock = false;
		boolean lowerLock = false;
		boolean upperLock = false;

		double step = (this.max - this.min)/40;

		int i = 0;
		for (double idx = this.min; idx <= this.max; idx = idx+step) {
			if (!mostLock && (idx == this.mostlikely || (((idx - step)< this.mostlikely) && ((idx+step) > this.mostlikely)))){
				label.add(i, Double.toString(this.mostlikely));
				mostLock = true;
			} else 	if (!lowerLock && (idx == this.minLimit || (((idx - step)< this.minLimit) && ((idx+step) > this.minLimit)))){
				label.add(i, Double.toString(this.minLimit));
				lowerLock = true;
			} else if (!upperLock && (idx == this.maxLimit || (((idx - step)< this.maxLimit) && ((idx+step) > this.maxLimit)))){
				label.add(i, Double.toString(this.maxLimit));
				upperLock = true;
			} else if (idx == this.min){
				label.add(i, Double.toString(this.min));
			} else if (idx == this.max || ((idx - step) > (this.max - step)) ){
				label.add(i, Double.toString(this.max));
			} else {
				label.add(i, "");
			}
			
			if (idx < this.minLimit || idx > this.maxLimit){
				prob.add(i, 0.0);
			} else	if (idx < this.mostlikely){
				prob.add(i, 1*((idx - this.min)/(this.mostlikely - this.min)));
			} else 	if (idx == this.mostlikely || (((idx - step)< this.mostlikely) && ((idx+step) > this.mostlikely))){
				prob.add(i, 1.0);
			} else if (idx > this.mostlikely){
				prob.add(i, 1*((this.max - idx)/(this.max - this.mostlikely)));		
			}			
			i++;
		}

		return new Object[]{label.toArray(new String[]{}), prob.toArray(new Double[]{})};
	}
}
