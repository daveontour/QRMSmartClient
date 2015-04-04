package au.com.quaysystems.qrm.util.probability;

import java.util.ArrayList;

public class NullDistribution implements IRandNumGenDist {
	
	public static final String ID = "au.com.quaysystems.qrm.util.probability.Null";
	double simple = 0;

	public NullDistribution() {
	}

	public final double getNext() {
		return this.simple;
	}

	public final void setParams(final ArrayList<Double> paramsValue) {
		this.simple = 0;
	}

	public final String toString() {
		return "Null Distribution()";
	}

	public final String getID() {
		return NullDistribution.ID;
	}

	@Override
	public Object[] getDisplayData() {
		return null;
	}

	@Override
	public double getPercentileValue(double percentitle) {
		return 0;
	}
}
