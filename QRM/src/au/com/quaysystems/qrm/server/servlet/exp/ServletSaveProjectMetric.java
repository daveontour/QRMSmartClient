package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.dto.ModelMetric;
import au.com.quaysystems.qrm.dto.ModelMetricProject;

@SuppressWarnings("serial")
@WebServlet (value = "/saveProjectMetric", asyncSupported = false)
public class ServletSaveProjectMetric extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		try {
			JSONArray metricsJS = (JSONArray) parser.parse(stringMap.get("DATA"));
			sess.beginTransaction();
			for (Object obj : metricsJS) {
				JSONObject metJS = (JSONObject) obj;
				Long id = (Long) metJS.get("metricID");

				ModelMetric tm = (ModelMetric) sess.get(ModelMetric.class, id);
				ModelMetricProject pm = (ModelMetricProject) sess
						.createSQLQuery("select * from projectmetric where projectID = :projectID AND metricID = :metID")
						.addEntity(ModelMetricProject.class)
						.setLong("metID",id)
						.setLong("projectID",projectID)
						.uniqueResult();

				if (pm == null) {
					pm = new ModelMetricProject();
				}

				pm.description = tm.description;
				pm.configLimit = tm.configRange;
				pm.configRange = tm.configRange;
				pm.metricID = tm.metricID;
				pm.title = tm.title;
				pm.method = tm.method;

				pm.projectID = projectID;

				pm.grayl = getDoubleJS(metJS.get("grayl"));
				pm.grayu = getDoubleJS(metJS.get("grayu"));
				pm.greenl = getDoubleJS(metJS.get("greenl"));
				pm.greenu = getDoubleJS(metJS.get("greenu"));
				pm.yellowl = getDoubleJS(metJS.get("yellowl"));
				pm.yellowu = getDoubleJS(metJS.get("yellowu"));
				pm.redl = getDoubleJS(metJS.get("redl"));
				pm.redu = getDoubleJS(metJS.get("redu"));
				pm.low = getDoubleJS(metJS.get("low"));
				pm.high = getDoubleJS(metJS.get("high"));

				sess.saveOrUpdate(pm);
			}
			sess.getTransaction().commit();

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
	}
}
