package au.com.quaysystems.qrm.dto;


public class QRMExportObjective {
	
	public QRMExportObjective(){}
	public QRMExportObjective(String objective, Long exportID,
			Long exportParentID) {
		this.objective = objective;
		this.exportParentID = exportParentID;
		this.exportID = exportID;
	}
	public String objective;
	
	public Long exportParentID;
	public Long assignParentID;
	
	public Long exportID;
	public Long assignID;
	
}
