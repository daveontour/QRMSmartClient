package au.com.quaysystems.qrm.util.probability;

import java.util.ArrayList;

public class DiscreteDistribution extends RandomDiscreteBase implements IRandNumGenDist {

	public String ID = "au.com.quaysystems.qrm.util.probability.Discrete";
	private ArrayList<Double> vals;
	public DiscreteDistribution() {
		super();
	}
	@Override
	public final void setParams(final ArrayList<Double> paramsValue) {

		this.vals = paramsValue;

		for (int i = 0; i < paramsValue.size() - 1; i = i + 2) {
			addDiscretePair(new ProbabilityPair(paramsValue.get(i), paramsValue
					.get(i + 1)));
		}
	}

	@Override
	public final double getNext() {
		return (super.getNext());
	}

	@Override
	public final String toString() {
		String str = "Discrete Values(";
		if (vals != null) {
			for (int i = 0; i < vals.size() - 1; i = i + 2) {
				str = str + "[" + vals.get(i) + "," + vals.get(i) + " ]";
			}
		}
		return str + ")";
	}

	public final String getID() {
		return ID;
	}

	@Override
	public Object[] getDisplayData() {
		return null;
	}
}
