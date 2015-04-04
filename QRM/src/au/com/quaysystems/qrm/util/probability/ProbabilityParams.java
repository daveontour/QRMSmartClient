package au.com.quaysystems.qrm.util.probability;

import java.util.ArrayList;

public class ProbabilityParams {

	@SuppressWarnings({ "rawtypes" })
	private ArrayList paramsValue = null;
	@SuppressWarnings({ "rawtypes" })
	private ArrayList paramsObjects = null;
	private String ID = null;

	@SuppressWarnings("rawtypes")
	public final ArrayList getParamsObjects() {
		return this.paramsObjects;
	}

	@SuppressWarnings("rawtypes")
	public final void setParamsObjects(final ArrayList paramsObjects) {
		this.paramsObjects = paramsObjects;
	}

	@SuppressWarnings({ "rawtypes" })
	public final ArrayList getParamsValue() {
		return this.paramsValue;
	}

	@SuppressWarnings( "rawtypes")
	public final void setParamsValue(final ArrayList paramsValue) {
		this.paramsValue = paramsValue;
	}

	public final String getID() {
		return this.ID;
	}

	public final void setID(final String id) {
		this.ID = id;
	}

}
