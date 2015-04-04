package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.dto.ModelRiskLiteBasic;
import au.com.quaysystems.qrm.dto.ModelRiskProject;

@SuppressWarnings("serial")
@WebServlet (value = "/newForceDownRisk", asyncSupported = false)
public class ServletNewForceDownRisk extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		try {
			JSONObject riskJS = (JSONObject) parser.parse(stringMap.get("DATA"));
			
			projectID = getLongJS(riskJS.get("projectID"));
			ModelRiskProject parentProject = getRiskProject(projectID, sess);
			if (parentProject.projectRiskManagerID.longValue() != userID.longValue()){
				outputJSON2B("&nbsp;&nbsp;Risk Not Created<br/><br/>Only the Project Risk Manager can create a Propogated Risk",response);
				return;
			}

			
			HashMap<String, Object> vars  = new HashMap<String, Object>();
			
			String title = (String) riskJS.get("title");

			vars.put("title", "(TOP PROPGATED RISK) "+title);
			vars.put("description", (String) riskJS.get("description"));
			vars.put("cause", (String) riskJS.get("cause"));
			vars.put("consequences", (String) riskJS.get("consequences"));
			vars.put("manager1ID", getLongJS((String) riskJS.get("manager1ID")));
			vars.put("ownerID", getLongJS((String) riskJS.get("ownerID")));
			vars.put("projectID", getLongJS(riskJS.get("projectID")));
			vars.put("endExposure", df.parse(((String) riskJS.get("endExposure")).substring(0, 9)));
			vars.put("startExposure",  df.parse(((String) riskJS.get("startExposure")).substring(0, 9)));

			vars.put("forceDownParent", true);
			vars.put("forceDownChild", false);
			
	
			ModelRiskLiteBasic parentRisk = newRisk(vars, sess, null);
			
			String rtnValue=parentRisk.riskProjectCode+"<br/><br/>Propogated Risks Created:";
			
			try(Connection conn = getSessionConnection(request)){
				Statement st = conn.createStatement();
				
				String sql = "SELECT * FROM subprojects WHERE projectID = "+projectID;
				if (getLongJS(riskJS.get("typePropogation")).longValue() == 0){
					sql = "SELECT parentID AS projectID, projectID AS subprojectID FROM riskproject WHERE parentID = "+projectID;
				}
				ResultSet rs = st.executeQuery(sql);
				vars.put("title", "(PROPOGATED) "+title);
				while (rs.next()){
					long subProjectID = rs.getLong("subprojectID");
					if (projectID.longValue() == subProjectID || subProjectID < 0){
						continue;
					}
					ModelRiskProject subproject = getRiskProject(projectID, sess);
					vars.put("manager1ID", subproject.getManagerInternalID());
					vars.put("ownerID", subproject.getManagerInternalID());

					vars.put("projectID", subProjectID);
					vars.put("forceDownParent", false);
					vars.put("forceDownChild", true);
					ModelRiskLiteBasic forceDownChildRisk = newRisk(vars, sess, parentRisk.riskProjectCode);
					rtnValue= rtnValue+"<br/>&nbsp;&nbsp;&nbsp;&nbsp;"+forceDownChildRisk.riskProjectCode;
					associateContributingRisk(parentRisk.riskID, forceDownChildRisk.riskID, request);
				}				
			}
			
			outputJSON2B(rtnValue,response);

		} catch (Exception e1) {
			e1.printStackTrace();
			outputJSON(false,response);
		}
	}
	
	private ModelRiskLiteBasic newRisk(HashMap<String, Object> vars, Session sess, String forceDownParentCode){
		
		ModelRiskLiteBasic risk = new ModelRiskLiteBasic();

		risk.setTitle((String) vars.get("title"));
		risk.setDescription((String) vars.get("description"));
		risk.setCause((String) vars.get("cause"));
		risk.setConsequences((String) vars.get("consequences"));
		risk.setSummaryRisk(false);
		risk.securityLevel = 0;

		// Insert trigger will figure out the correct matrix ID
		risk.matrixID = 0L;

		risk.manager1ID = (Long) vars.get("manager1ID");
		risk.ownerID = (Long) vars.get("ownerID");
		risk.projectID = (Long)vars.get("projectID");

		risk.endExposure = (Date) vars.get("endExposure");
		risk.startExposure = (Date) vars.get("startExposure");
		
		risk.forceDownChild = (Boolean) vars.get("forceDownChild");
		risk.forceDownParent = (Boolean) vars.get("forceDownParent");

		sess.beginTransaction();
		sess.save(risk);
		sess.getTransaction().commit();

		//Refresh updates the risk after the insert triggers have been fired. The triggers set some data
		sess.refresh(risk);	
		
		if (risk.forceDownChild){
			risk.setRiskProjectCode(forceDownParentCode+"-"+risk.riskProjectCode);
		} else {
			risk.setRiskProjectCode(risk.riskProjectCode+"P");			
		}
		
		sess.beginTransaction();
		sess.update(risk);
		sess.getTransaction().commit();	
		sess.refresh(risk);				

		return risk;
	}
}
