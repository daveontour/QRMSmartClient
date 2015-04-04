package au.com.quaysystems.qrm.dto;

public class DTOSmartClientMenuItem {
	
	String title;
	String ID;
	String icon = "icons/16/icon-pdf.png";
	Boolean enabled;
	Boolean isSeparator;
	Long internalID;
	DTOSmartClientMenuItem[] submenu;
	String click;
	String enableIf;
	String type;

	public DTOSmartClientMenuItem (){
		isSeparator = true;
	}
	
	public DTOSmartClientMenuItem (final String t){
		title = t;
	}
	
	public DTOSmartClientMenuItem (final String t, final String i){
		title = t;
		icon = i;
	}
	
	public DTOSmartClientMenuItem (final String t, final String i, final Boolean e, final Boolean sep){
		title = t;
		icon = i;
		enabled = e;
		isSeparator = sep;
	}
	
	public DTOSmartClientMenuItem (final String id, final String t, final String i, final Boolean e, final Boolean sep, final DTOSmartClientMenuItem[] sub){
		ID = id;
		title = t;
		icon = i;
		enabled = e;
		isSeparator = sep;
		submenu = sub;
	}
	
	public DTOSmartClientMenuItem (final String eif, final String id, final String t, final String i, final Boolean e, final Boolean sep, final DTOSmartClientMenuItem[] sub){
		enableIf = eif;
		ID = id;
		title = t;
		icon = i;
		enabled = e;
		isSeparator = sep;
		submenu = sub;
	}
	
	public DTOSmartClientMenuItem(String reportName, String icon,
			long internalID) {
		this.title = reportName;
		this.icon = icon;
		this.internalID = internalID;
		
	}
	
	public DTOSmartClientMenuItem(String reportName, String icon,
			long internalID, String type) {
		this.title = reportName;
		this.icon = icon;
		this.internalID = internalID;
		this.type = type;
		
	}

	public DTOSmartClientMenuItem(String reportName, String icon, String type) {
		this.title = reportName;
		this.icon = icon;
		this.type = type;
	}

	public DTOSmartClientMenuItem(String reportName, 
			long reportID, Boolean excelOnly) {
		this.internalID = reportID;
		this.title = reportName;
		this.click = "eval('var rep = {checkTableSelection:true, noSelectWindow:true, baseValues:true, reportID:"+ reportID+ ", excelOnly:"+excelOnly+", submit:true}');qoReportEngine.flowController(rep)";
	}

	public DTOSmartClientMenuItem(String reportName, 
			long reportID, boolean addSelectedRiskIDS, boolean projectRisk, boolean configWindow) {
		this.internalID = reportID;
		this.title = reportName;
		this.click = "eval('var rep = {checkTableSelection:true, noSelectWindow:true,baseValues:true, reportID:"+ reportID+ ", excelOnly:false, addSelectedRiskIDS:"+addSelectedRiskIDS+", projectRisk:"+projectRisk+", detailConfigWindow:"+configWindow+",submit:true}');qoReportEngine.flowController(rep)";
	}

	public final void setClick(final String func){
		this.click = func;
	}
	public final void setClick(final Long reportID, Boolean excelOnly){
		this.click = "eval('var rep = {checkTableSelection:true, noSelectWindow:true,baseValues:true, reportID:"+ reportID+ ", excelOnly:"+excelOnly+", submit:true}');qoReportEngine.flowController(rep)";
	}
}
