package au.com.quaysystems.qrm.util.probability;

import java.util.ArrayList;

public class SimpleDistribution implements IRandNumGenDist {

	Double simple;
	public static final String ID = "au.com.quaysystems.qrm.util.probability.Simple";
	public SimpleDistribution() {
	}

	public final double getNext() {
		return this.simple;
	}

	public final void setParams(final ArrayList<Double> paramsValue) {
		this.simple = paramsValue.get(0);
	}
	public final String toString() {
		if (simple == null) {
			return "Simple(Not Value Set)";
		}
		return "Simple(" + simple + ")";
	}
	public final String getID() {
		return ID;
	}

	@Override
	public Object[] getDisplayData() {
		return new Object[]{new String[]{"", "", "", "", "" + this.simple, "", "", "", ""}, new Double[]{0.0, 0.0, 0.0, 0.0, 0.1, 0.0, 0.0, 0.0, 0.0}};
	}

	@Override
	public double getPercentileValue(double percentitle) {
		return simple;
	}
}
