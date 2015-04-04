package au.com.quaysystems.qrm.dto;

import java.util.ArrayList;

public class QRMExportCategory {

	public QRMExportCategory(){}
	public QRMExportCategory(String desc, Long internalID) {
		this.description = desc;
		this.exportID = internalID;
	}
	public ArrayList<QRMExportCategory> subCats = new ArrayList<QRMExportCategory>(); 
	public String description;
		
	public Long exportID;
	public Long assignID;
}

