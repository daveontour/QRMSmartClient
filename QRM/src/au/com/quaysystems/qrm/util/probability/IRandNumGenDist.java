package au.com.quaysystems.qrm.util.probability;

import java.util.ArrayList;

public interface IRandNumGenDist {	
	void setParams(ArrayList<Double> paramsValue);
	double getNext();
	Object[] getDisplayData();
	String toString();
	String getID();
	double getPercentileValue(double percentitle);
}
