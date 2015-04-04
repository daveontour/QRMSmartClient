package au.com.quaysystems.qrm.dto;


public class QRMExportConsequence {

	public QRMExportConsequence(){}
	public QRMExportConsequence(Long id, String description, String units, Boolean costType) {
		this.description = description;
		this.units = units;
		this.costType = costType;
		this.exportID = id;
	}
	public String description;
	public String units;
	public Boolean costType;
	public Long assignID;
	public Long exportID;
	
}
