package au.com.quaysystems.qrm.dto;

import java.util.ArrayList;
import java.util.HashMap;

public class QRMExportProject {

	public Long exportID;
	public Long assignID;

	public String projectTitle;
	public String projectDescription;
	public String projectCode;
	public ArrayList<QRMExportProject> subProjects = new ArrayList<QRMExportProject>();
	public ArrayList<QRMExportConsequence> consequenceTypes = new ArrayList<QRMExportConsequence>();
	public ArrayList<QRMExportObjective> objectives = new ArrayList<QRMExportObjective>();
	public ArrayList<QRMExportCategory> categories = new ArrayList<QRMExportCategory>();
	public ArrayList<ModelRisk> risks = new ArrayList<ModelRisk>();
	public ArrayList<Long> owners = new ArrayList<Long>();
	public ArrayList<Long> mgrs = new ArrayList<Long>();
	public ArrayList<Long> users = new ArrayList<Long>();
	public HashMap<Long, ModelPerson> personMap = new HashMap<Long, ModelPerson>();
	
	public QRMExportProject(){}
	
	public Long findNewObjectiveParentID(Long id){
		Long newParentID = null;
		
		for(QRMExportObjective objective: objectives){
			if (objective.exportID.longValue() == id.longValue()){
				return objective.assignID;
			}
		}
		
		for (QRMExportProject proj : subProjects){
			
			Long parentObjID = proj.findNewObjectiveParentID(id);
			
			if (parentObjID != null){
				return parentObjID; 
			}
		}
		
		return newParentID;
	}
	
	public Long findNewCatID(long exportID){
		
		for (QRMExportCategory cat: categories){
			if (cat.exportID.longValue() == exportID){
				return cat.assignID;
			}
			for (QRMExportCategory cat2: cat.subCats){
				if (cat2.exportID.longValue() == exportID){
					return cat2.assignID;
				}
			}
		}
		
		return null;
	}
	public Long findConsID(long exportID){
		if (exportID <= 2) return exportID;
		for (QRMExportConsequence con: consequenceTypes){
			if (con.exportID.longValue() == exportID){
				return con.assignID;
			}
		}		
		return exportID;
	}
}
