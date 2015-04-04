package au.com.quaysystems.qrm.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CSVUploadInfo {
	
	public ArrayList<String[]> itemsToImport;
	public HashMap<Integer, String> actionColumns;
	public HashMap<String, Integer> actionColumnsReverse;
	public HashMap<String, String> configMap;
	public Integer riskKey; 
	public Boolean matchRisks;
	public Long projectID;
	public Long userID; 
	public HashMap<String, Double> probMap = new HashMap<String, Double>();
	public HashMap<String, Double> impactMap = new HashMap<String, Double>();
	public HashSet<String> probSet = new HashSet<String>();
	public List<String[]> allItems;
	public HashSet<String> impactSet;
	public static HashMap<String, Double> refMap = new HashMap<String, Double>();


	public CSVUploadInfo(
			ArrayList<String[]> itemsToImport,
			HashMap<Integer, String> actionColumns,
			HashMap<String, Integer> actionColumnsReverse,
			HashMap<String, String> configMap, Integer riskKey, Long projectID,
			Long userID, Boolean matchRisks, HashSet<String> probSet, HashSet<String> impactSet) {

		this.itemsToImport = itemsToImport;
		this.actionColumns = actionColumns;
		this.actionColumnsReverse = actionColumnsReverse;
		this.configMap = configMap;
		this.riskKey = riskKey;
		this.projectID = projectID;
		this.userID = userID;
		this.matchRisks = matchRisks;
		this.probSet = probSet;
		this.impactSet = impactSet;
	}
}
