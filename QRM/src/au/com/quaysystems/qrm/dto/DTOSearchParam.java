package au.com.quaysystems.qrm.dto;

public class DTOSearchParam {

	public boolean desc;
	public boolean summaryRisks = false;
	public boolean processFilter = false;
	public boolean rolled = false;
	
	
	public boolean activeStatus = false;
	public boolean pendingStatus = false;
	public boolean inactiveStatus = false;
	public boolean treatedStatus = false;
	public boolean untreatedStatus = false;

	public long ownerID = 0;
	public long managerID= 0;
	public long categoryID = 0;

	public boolean tolEx = false;
	public boolean tolHigh = false;
	public boolean tolSig = false;
	public boolean tolMod = false;
	public boolean tolLow = false;

	public Long parentRiskID = null; 
	
	public int treatedProb;
	public int treatedImpact;
	public int untreatedProb;
	public int untreatedImpact;
	public int treated = 0;
	public boolean processTreated = false;
	public boolean incSummaryRisks = true;
	
	public Long userID;
	public Long projectID;
	public Long riskID;

	
	

}
