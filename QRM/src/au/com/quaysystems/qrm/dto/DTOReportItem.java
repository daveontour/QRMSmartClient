/*
 * 
 */
package au.com.quaysystems.qrm.dto;

public class DTOReportItem {
	
	public String title;
	public Long reportID;
	public Long internalID;
	public String type;
	public Boolean baseValues = true;
	public Boolean excelOnly = false;
	public Boolean submit = true;
	public Boolean addAllRiskIDS = false;
	public Boolean projectRisk = false;
	public Boolean detailConfigWindow = false;
	public Boolean addSelectedRiskIDs = false;
	public Boolean checkTableSelection = false;
	
	public DTOReportItem(String title,  Long reportID, String type) {
		this.title = title;
		this.reportID = reportID;
		this.internalID = reportID;
		this.type = type;
	}
}
