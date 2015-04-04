package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.dto.ModelQuantImpactType;

@SuppressWarnings("serial")
@WebServlet (value = "/saveProjectCons", asyncSupported = false)
public class ServletSaveProjectCons extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,	Session sess, Long userID, HashMap<String, String> stringMap,	HashMap<Object, Object> objMap, Long projectID, Long riskID) {
	
		try {
			for (Object objID : (JSONArray) parser.parse(stringMap.get("DATADELETE"))) {
				sess.beginTransaction();
				ModelQuantImpactType type = (ModelQuantImpactType)sess.getNamedQuery("getQuantType").setLong("typeID", (Long) objID).uniqueResult();
				sess.delete(type);
				sess.getTransaction().commit();
			}
		} catch (Exception e) {
			log.error(e);
		}

		try {
			for (Object obj :  (JSONArray) parser.parse(stringMap.get("DATA"))) {

				JSONObject objJS = (JSONObject) obj;

				if ((Long) objJS.get("generation") < 0) {
					continue;
				}
				
				Long id = (Long) objJS.get("typeID");
				String description  = (String) objJS.get("description");
				String units = (String) objJS.get("units");
				Boolean costCategory = (Boolean) objJS.get("costCategroy");
			
				sess.beginTransaction();
				
				ModelQuantImpactType type = null;
				if (id > 0) {		
					type = (ModelQuantImpactType)sess.getNamedQuery("getQuantType").setLong("typeID", id).uniqueResult();					
				} else {
					type = new ModelQuantImpactType();
					type.setProjectID(projectID);
				}
				
				type.setDescription(description);
				type.setUnits(units);
				type.setCostCategroy(costCategory);
				
				sess.saveOrUpdate(type);
				sess.getTransaction().commit();
			}

		} catch (Exception e) {
			log.error(e);
		}
	}
}
