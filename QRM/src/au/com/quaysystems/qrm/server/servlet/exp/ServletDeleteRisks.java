package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONArray;

import au.com.quaysystems.qrm.dto.ModelRiskLiteBasic;
import au.com.quaysystems.qrm.dto.ModelRiskLiteExt;
import au.com.quaysystems.qrm.server.PersistenceUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/deleteRisks", asyncSupported = false)
public class ServletDeleteRisks extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		try {
			boolean someFailure = false;
			for (Object x : (JSONArray) parser.parse(request.getParameter("DATA"))) {

				ModelRiskLiteBasic risk = (ModelRiskLiteBasic) sess.get(ModelRiskLiteBasic.class, (Long) x);

				if (risk == null){
					someFailure = true;
					continue;
				}
				if (checkDeleteSecurity(risk.riskID, userID, sess)){
					if (risk.forceDownParent){
						String 	sql = "SELECT riskdetail.*, riskrisk.*, 1 AS contextRank FROM riskdetail JOIN riskrisk ON riskrisk.childID = riskdetail.riskID  WHERE riskrisk.parentID = "+risk.riskID;
						
						@SuppressWarnings("unchecked")
						List<ModelRiskLiteExt> risks = sess.createSQLQuery(sql).addEntity(ModelRiskLiteExt.class).list();
						Session sess2 = PersistenceUtil.getSession((String) request.getSession().getAttribute("session.url"));
						for (ModelRiskLiteExt childRisk:risks){
							removeContributingRisk(risk.riskID, childRisk.riskID,  request);						
							try {
								ModelRiskLiteBasic childRisk2 = (ModelRiskLiteBasic) sess2.get(ModelRiskLiteBasic.class, childRisk.riskID);								
								sess2.beginTransaction();
								sess2.delete(childRisk2);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								sess2.getTransaction().commit();								
							}
						}
						sess2.close();
					}
					Session sess3 = PersistenceUtil.getSession((String) request.getSession().getAttribute("session.url"));
					ModelRiskLiteBasic risk2 = (ModelRiskLiteBasic) sess3.get(ModelRiskLiteBasic.class, risk.riskID);								
					sess3.beginTransaction();
					sess3.delete(risk2);
					sess3.getTransaction().commit();
					sess3.close();
				} else {
					someFailure = true;
				}
				
			}

			outputJSONB(someFailure,response);
		} catch (Exception e) {
			log.error(e);
		}
	}
}
