package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import au.com.quaysystems.qrm.server.RESTTransportContainer;

@SuppressWarnings("serial")
@WebServlet (value = "/updateFamilyRPC", asyncSupported = false)
public class ServletUpdateFamilyRPC extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		
		ArrayList<HashMap<String, Long >> list  = new ArrayList<HashMap<String, Long >>(); 
		
		try {
			for (Object obj:(JSONArray)parser.parse(stringMap.get("CHANGES"))){
				JSONObject objJS = (JSONObject)obj;
				
				HashMap<String, Long> item = new HashMap<String, Long>(); 
				item.put("riskID", getLongJS(objJS.get("riskID")));
				item.put("newParentSummaryRisk", getLongJS(objJS.get("newParentSummaryRisk")));
				item.put("parentSummaryRisk", getLongJS(objJS.get("parentSummaryRisk")));
				item.put("origParentSummaryRisk", getLongJS(objJS.get("origParentSummaryRisk")));
				item.put("relationshipID", getLongJS(objJS.get("relationshipID")));
				item.put("tempIndex", getLongJS(objJS.get("tempIndex")));
				
				list.add(item);
				}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		for (HashMap<String, Long> item : list){
			Long childID = item.get("riskID");
			Long newParentSummaryRisk = item.get("newParentSummaryRisk");
			Long parentSummaryRisk = item.get("parentSummaryRisk");
//			Long origParentSummaryRisk = item.get("origParentSummaryRisk");
			Long relationshipID = item.get("relationshipID");
			Long tempIndex = item.get("tempIndex");
			
			//The client requires a unique ID for tree elements, so a fudge is used to create a uniques riskID which is recoverable
			childID =childID-ServletGetRiskLiteRPC.uniqueOffset-tempIndex;
			
			if (relationshipID > 0 ){
				this.removeContributingRisk(relationshipID, request);
			}
			if (newParentSummaryRisk == null){
				this.associateContributingRisk(parentSummaryRisk, childID, request);				
			}
			if (newParentSummaryRisk > 0){
				this.associateContributingRisk(newParentSummaryRisk, childID, request);
			}
		}



		outputJSON(new RESTTransportContainer(0),response);


	}
}
