package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import au.com.quaysystems.qrm.dto.ModelMultiLevel;
import au.com.quaysystems.qrm.dto.ModelMultiLevelNoGen;

@SuppressWarnings("serial")
@WebServlet (value = "/saveProjectCategories", asyncSupported = false)
public class ServletSaveProjectCategories extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		// Delete Categories
		try {
			JSONArray deleteJS = (JSONArray) parser.parse(stringMap.get("DATADELETE"));
			Transaction tx = sess.beginTransaction();
			for (Object obj : deleteJS) {
				Long id = (Long) obj;
				ModelMultiLevelNoGen cat = (ModelMultiLevelNoGen) sess.load(ModelMultiLevelNoGen.class, id);
				sess.delete(cat);
			}
			tx.commit();

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

		JSONArray catJS = null;
		try {
			catJS = (JSONArray) parser.parse(stringMap.get("DATA"));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			log.error("QRM Stack Trace", e1);
		}

		// Update Exiting
		try {
			Transaction tx = sess.beginTransaction();
			for (Object obj : catJS) {

				JSONObject x = (JSONObject) obj;
				Long primID = (Long) x.get("internalID");
				Long gen = (Long) x.get("generation");

				// Exisiting Primary
				if ((gen >= 0) && (primID > 0)) {
					ModelMultiLevelNoGen cat = (ModelMultiLevelNoGen) sess.load(ModelMultiLevelNoGen.class, (Long) x.get("internalID"));
					cat.setDescription((String) x.get("description"));
					sess.update(cat);
				}
				// New Primary
				if ((gen >= 0) && (primID < 0)) {
					ModelMultiLevel cat = new ModelMultiLevel();
					cat.setDescription((String) x.get("description"));
					cat.setContextID(projectID);
					cat.setParentID(1L);
					primID = (Long) sess.save(cat);
					tx.commit();
					tx.begin();
				}

				JSONArray catJSSEC = null;
				try {
					catJSSEC = (JSONArray) x.get("sec");
				} catch (Exception e1) {
					log.error("QRM Stack Trace", e1);
				}

				// Update Exiting Secondary or Insert new Secondary
				if (catJSSEC != null) {
					try {
						for (Object obj2 : catJSSEC) {
							JSONObject x2 = (JSONObject) obj2;
							if (((Long) x2.get("generation") >= 0)	&& ((Long) x2.get("internalID") > 0)) {
								ModelMultiLevelNoGen cat2 = (ModelMultiLevelNoGen) sess.load(ModelMultiLevelNoGen.class, (Long) x2.get("internalID"));
								cat2.setDescription((String) x2.get("description"));
								sess.update(cat2);
							}
							if (((Long) x2.get("generation") >= 0)	&& ((Long) x2.get("internalID") < 0)) {
								ModelMultiLevel cat = new ModelMultiLevel();
								cat.setDescription((String) x2.get("description"));
								cat.setContextID(projectID);
								cat.setParentID(primID);
								sess.save(cat);
							}
						}
					} catch (Exception e) {
						log.error("QRM Stack Trace", e);
					}
				}

			}
			tx.commit();

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}


	}
	
}
