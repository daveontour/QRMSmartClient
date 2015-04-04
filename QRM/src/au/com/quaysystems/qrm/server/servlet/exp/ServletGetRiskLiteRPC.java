package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelRiskLite;
import au.com.quaysystems.qrm.dto.ModelRiskLiteRelationship;

@SuppressWarnings("serial")
@WebServlet (value = "/getRiskLiteRPC", asyncSupported = false)
public class ServletGetRiskLiteRPC extends QRMRPCServlet {
	
	public static final Long uniqueOffset = 50000000L;

	@SuppressWarnings("unchecked")
	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		boolean family = false;
		long tempIndex= 20;

//		if (stringMap.get("UNALLOCATED") != null) {
//			if (stringMap.get("UNALLOCATED").equalsIgnoreCase("true")) {
//				unallocated = true;
//			}
//		}
		if (stringMap.get("FAMILY") != null) {
			if (stringMap.get("FAMILY").equalsIgnoreCase("true")) {
				family = true;
			}
		}
		
		boolean desc = Boolean.parseBoolean(stringMap.get(	"DESCENDANTS"));
		 
		ArrayList<ModelRiskLite> risksParent = new ArrayList<ModelRiskLite>();
		ArrayList<ModelRiskLiteRelationship> risksFamily = new ArrayList<ModelRiskLiteRelationship>();
		ArrayList<ModelRiskLite> allRisks = new ArrayList<ModelRiskLite>();

		
		if (family) {
			List<ModelRiskLite> rawrisks = getAllProjectRisksLite(userID,	projectID, 	desc, sess);

			for (ModelRiskLite risk : rawrisks) {
				risk.tempIndex = tempIndex++;
				if (risk.forceDownParent || risk.summaryRisk ){
					risksParent.add(risk);
				} 
				if (!risk.summaryRisk && !risk.forceDownParent && !risk.forceDownChild) {
					risk.origParentSummaryRisk = -1L;
					risk.relationshipID = -1L;
					risk.externalID = null;
					risk.tempIndex = tempIndex++;
					
					risk.riskID = risk.riskID+uniqueOffset+risk.tempIndex;
					
					allRisks.add(risk);
				}
			}
			
			for (ModelRiskLite riskParent : risksParent) {
				
				ModelRiskLiteRelationship risk = new ModelRiskLiteRelationship();
				
				try {
					PropertyUtils.copyProperties(risk, riskParent);
				} catch (Exception e) {
					e.printStackTrace();
				} 
				risk.relationshipID = -1L;
				risk.riskID = riskParent.riskID;
				
				risksFamily.add(risk);
				
				String 	sql = "SELECT riskrisk.internalID AS relationshipID, riskdetail.*, 0 AS contextRank, riskrisk.* FROM riskdetail "+
						      "JOIN riskrisk ON riskrisk.childID = riskdetail.riskID WHERE riskrisk.parentID = "+risk.riskID;
				
				for (ModelRiskLiteRelationship modelRisk : (ArrayList<ModelRiskLiteRelationship>) sess.createSQLQuery(sql).addEntity(ModelRiskLiteRelationship.class).list()){
					ModelRiskLiteRelationship childRisk = new ModelRiskLiteRelationship();
					try {
						PropertyUtils.copyProperties(childRisk, modelRisk);
						childRisk.riskID = modelRisk.riskID;
						childRisk.relationshipID = modelRisk.relationshipID;
					} catch (Exception e) {
						e.printStackTrace();
					} 
					
					childRisk.parentSummaryRisk = risk.riskID;
					childRisk.origParentSummaryRisk = risk.riskID;
					childRisk.tempIndex = tempIndex++;
					
					childRisk.riskID = childRisk.riskID+uniqueOffset+childRisk.tempIndex;
					risksFamily.add(childRisk);
				}
			}			

			Object[] arr = new Object[2];
			arr[0] = risksFamily;
			arr[1] = allRisks;


			outputJSONRiskLite(arr,response);


		} else {
			List<ModelRiskLite> risks = getAllProjectRisksLite(
					userID, projectID, 
					Boolean.parseBoolean(stringMap.get("DESCENDANTS")), 
					sess);

			if (stringMap.get("ULTRALITE") != null ){
				outputJSONRiskLite(new ArrayList<ModelRiskLite>(risks),response);
			} else {
				outputJSONB(new ArrayList<ModelRiskLite>(risks),response);
			}
		}
	}
	
	

}
