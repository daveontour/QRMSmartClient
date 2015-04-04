package au.com.quaysystems.qrm.dto;

import java.util.ArrayList;

public class QRMWSBean {

	private ModelRisk risk;
	
	public ModelRisk getRisk(){
		return risk;
	}
	public ArrayList<ModelRisk> getRisks(){
		return new ArrayList<ModelRisk>();
	}
}
