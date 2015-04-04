package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import au.com.quaysystems.qrm.dto.DTOSearchParam;

public class SearchStringHelper {
	
	public static DTOSearchParam parseSearch( HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID,Long userID){
		
		DTOSearchParam s = new DTOSearchParam();
		
		s.projectID = projectID;
		s.userID = userID;
		s.riskID = riskID;
		
		s.desc = Boolean.parseBoolean(stringMap.get("DESCENDANTS"));
		
 

		if (stringMap.get("PROCESSFILTER") != null){
			s.processFilter = true;
		}
		if (stringMap.get("SUMMARYRISKS") != null){
			s.summaryRisks = true;
		}
		if (stringMap.get("ROLLED") != null) {
			s.rolled = true;
		}
		if (stringMap.get("PARENTRISKID") != null){
			s.parentRiskID = Long.parseLong(stringMap.get("PARENTRISKID"));
		}

		try {
			if (s.processFilter){
				s.activeStatus = (Boolean) objMap.get("STATACTIVE");
				s.pendingStatus = (Boolean) objMap.get("STATPENDING");
				s.inactiveStatus = (Boolean) objMap.get("STATINACTIVE");
				s.treatedStatus = (Boolean) objMap.get("STATTREATED");
				s.untreatedStatus = (Boolean) objMap.get("STATUNTREATED");

				s.ownerID = Long.parseLong((String) objMap.get("OWNERID"));
				s.managerID = Long.parseLong((String) objMap.get("MANAGERID"));
				s.categoryID = Long.parseLong((String) objMap.get("CATID"));

				s.tolEx = (Boolean) objMap.get("TOLEX");
				s.tolHigh = (Boolean) objMap.get("TOLHIGH");
				s.tolSig = (Boolean) objMap.get("TOLSIG");
				s.tolMod = (Boolean) objMap.get("TOLMOD");
				s.tolLow = (Boolean) objMap.get("TOLLOW");
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}



		if (stringMap.get("TREATED") != null) {
			if (stringMap.get("TREATED").equalsIgnoreCase("true")
					|| stringMap.get("TREATED")	.equalsIgnoreCase("false")) {
				s.processTreated = true;

			}
		}

		if (s.processTreated) {
			try {
				s.treated = Boolean.parseBoolean(stringMap.get("TREATED"))?1:0;
			} catch (Exception e2) {
				s.processTreated = false;
			}
		}

		if (stringMap.get("INCSUMMARYRISK") != null) {
			if (stringMap.get("INCSUMMARYRISK")	.equalsIgnoreCase("false")) {
				s.incSummaryRisks = false;
			}
		}

		try {
			s.treatedProb = Integer.parseInt(stringMap.get("TREATEDPROB"));
		} catch (Exception e1) {
			s.treatedProb = -1;
		}
		try {
			s.treatedImpact = Integer.parseInt(stringMap.get(	"TREATEDIMPACT"));
		} catch (Exception e1) {
			s.treatedImpact = -1;
		}
		try {
			s.untreatedProb = Integer.parseInt(stringMap.get(	"UNTREATEDPROB"));
		} catch (Exception e1) {
			s.untreatedProb = -1;
		}
		try {
			s.untreatedImpact = Integer.parseInt(stringMap.get("UNTREATEDIMPACT"));
		} catch (Exception e1) {
			s.untreatedImpact = -1;
		}
		

		
		return s;
		
	}

	public static String getSQLSearchString(DTOSearchParam s){

		String sql = "SELECT riskdetail.*, r1.rank AS contextRank FROM riskdetail "+
				"LEFT OUTER JOIN subjrank AS r1 ON (riskdetail.riskID = r1.riskID  AND  r1.projectID  = "+s.projectID+") ";

		// This change allows "promoted risks" to be included

		if (s.desc){
			sql = sql + " WHERE (riskdetail.projectID IN (SELECT subprojectID  FROM subprojects WHERE projectID = "+s.projectID+" UNION SELECT "+s.projectID+") OR (riskdetail.promotedProjectID IN (SELECT superprojectID  FROM superprojects WHERE projectID = "+s.projectID+" UNION SELECT "+s.projectID+") AND riskdetail.projectID IN (SELECT subprojectID  FROM subprojects WHERE projectID = "+s.projectID+"  UNION SELECT "+s.projectID+") ) OR riskdetail.promotedProjectID = "+s.projectID+" )"+
					" AND checkRiskSecurityView(riskdetail.riskID, "+s.userID+", riskdetail.securityLevel, riskdetail.projectID)";
		} else {
			sql = sql + " WHERE  (riskdetail.promotedProjectID IN (SELECT superprojectID  FROM superprojects WHERE projectID = "+s.projectID+" AND riskdetail.projectID IN (SELECT subprojectID  FROM subprojects WHERE projectID = "+s.projectID+") )  OR riskdetail.promotedProjectID = "+s.projectID+" OR riskdetail.projectID = "+s.projectID+") AND checkRiskSecurityView(riskdetail.riskID, "+s.userID+", riskdetail.securityLevel, riskdetail.projectID)";
		}


		if (s.processFilter) {

			// Clauses for status
			if (!s.inactiveStatus) {
				sql = sql + " AND NOW() < riskdetail.endExposure ";
			}
			if (!s.pendingStatus) {
				sql = sql + " AND NOW() > riskdetail.startExposure ";
			}
			if (!s.activeStatus) {
				sql = sql + " AND (NOW() > riskdetail.endExposure OR NOW() < riskdetail.startExposure) ";
			}

			// Specific risk clause
			if (s.riskID != null){
				sql = sql + " AND riskdetail.riskID = "+s.riskID;
			}

			// Treatment status clause
			if (!s.treatedStatus){
				sql = sql + " AND riskdetail.treated < 1";
			}
			if (!s.untreatedStatus){
				sql = sql + " AND riskdetail.treated > 0";
			}

			// Owner / Manager ID clauses
			if (s.ownerID > 0){
				sql = sql + " AND riskdetail.ownerID = "+s.ownerID;
			}
			if (s.managerID > 0){
				sql = sql + " AND riskdetail.manager1ID = "+s.managerID;
			}

			// Category clause
			if (s.categoryID > 0){
				sql = sql + " AND riskdetail.primCatID = "+s.categoryID;
			}

			// Tolerance clauses
			if (!s.tolEx){
				sql = sql + " AND riskdetail.currentTolerance != 5 ";
			}
			if (!s.tolHigh){
				sql = sql + " AND riskdetail.currentTolerance != 4 ";
			}
			if (!s.tolSig){
				sql = sql + " AND riskdetail.currentTolerance != 3 ";
			}
			if (!s.tolMod){
				sql = sql + " AND riskdetail.currentTolerance != 2 ";
			}
			if (!s.tolLow){
				sql = sql + " AND riskdetail.currentTolerance != 1 ";
			}

		} else if (s.summaryRisks) {
			sql = sql + " AND riskdetail.summaryRisk > 0";
		} else {

			if (!s.incSummaryRisks) {
				sql = sql+" AND riskdetail.summaryRisk < 1 ";
			}
			if (s.processTreated){
				sql = sql+" AND riskdetail.treated = "+s.treated+ " ";
			}
			if (s.treatedProb > 0){
				sql = sql + " AND FLOOR(riskdetail.treatedProb) = "+s.treatedProb;
			}
			if (s.untreatedProb > 0){
				sql = sql + " AND FLOOR(riskdetail.inherentProb) = "+s.untreatedProb;
			}
			if (s.treatedImpact > 0){
				sql = sql + " AND FLOOR(riskdetail.treatedImpact) = "+s.treatedImpact;
			}
			if (s.untreatedImpact > 0){
				sql = sql + " AND FLOOR(riskdetail.inherentImpact) = "+s.untreatedImpact;
			}
		}

		if (s.rolled) {
			sql = sql + " AND riskdetail.summaryRisk > 0";
		}

		if (s.parentRiskID != null) {
			sql = "SELECT riskdetail.*, r1.rank AS contextRank, riskrisk.* FROM riskdetail "+
					"LEFT OUTER JOIN subjrank AS r1 ON (riskdetail.riskID = r1.riskID  AND  r1.projectID  = "+s.projectID+") "+
					" JOIN riskrisk ON riskrisk.childID = riskdetail.riskID "+
					" WHERE riskrisk.parentID = "+s.parentRiskID;

		}

		return sql;
	}

	public static DTOSearchParam parseSearch(HashMap<Object, Object> taskParamMap) {
		
		Long projectID;
		Long userID;

		try {
			projectID = (Long)taskParamMap.get("projectID");
		} catch (Exception e1) {
			projectID = new Long((Integer)taskParamMap.get("projectID"));
		}
		try {
			userID = (Long) taskParamMap.get("userID");
		} catch (Exception e1) {
			userID = new Long((Integer) taskParamMap.get("userID"));
		}
		
		HashMap<String, String> stringMap = new HashMap<String, String>();
		HashMap<Object, Object> objMap = new HashMap<Object, Object>();
		
		for (Object key : taskParamMap.keySet()) {

			String keyStr = (String) key;
			
			try {
				String value = taskParamMap.get(key).toString();
				stringMap.put(keyStr,value);
				
				if (value.equalsIgnoreCase("true")){
					objMap.put(key, new Boolean(true));
				} else if (value.equalsIgnoreCase("false")){
					objMap.put(key, new Boolean(false));
				} else {
					objMap.put(key, taskParamMap.get(key).toString());
				}
				stringMap.put(keyStr, taskParamMap.get(key.toString()).toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
			
		}
		
		return parseSearch( stringMap,	objMap,  projectID,  null, userID);
	}

}
