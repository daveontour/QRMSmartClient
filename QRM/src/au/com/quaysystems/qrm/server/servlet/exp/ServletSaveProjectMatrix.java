package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.dto.ModelToleranceDescriptors;
import au.com.quaysystems.qrm.dto.ModelToleranceMatrix;

@SuppressWarnings("serial")
@WebServlet (value = "/saveProjectMatrix", asyncSupported = false)
public class ServletSaveProjectMatrix extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		try {

			JSONObject dataJS = (JSONObject) parser.parse(stringMap.get("DATA"));

			Long matID = (Long) dataJS.get("matrixID");
			Long matProjID = (Long) dataJS.get("projectID");

			ModelToleranceMatrix mat = new ModelToleranceMatrix();
			mat.setProjectID(projectID);
			mat.tolString = getDataJSString(dataJS, "tolString");
			mat.maxImpact = ((Long) dataJS.get("maxImpact")).intValue();
			mat.maxProb = ((Long) dataJS.get("maxProb")).intValue();

			Long[] root = getRootProjectID(sess);
			Long rootID = root[0];


			ArrayList<ModelToleranceDescriptors> actions = getProjectTolActions(projectID,sess);

			Transaction tx = sess.beginTransaction();
			for (ModelToleranceDescriptors action : actions){
				if (action.projectID.longValue() == projectID){
					action.setLongName(getDataJSString(dataJS, "action"+action.tolLevel));
					sess.update(action);
				} else {
					ModelToleranceDescriptors newAction = new ModelToleranceDescriptors();
					newAction.setLongName(getDataJSString(dataJS, "tolAction"+action.tolLevel));
					newAction.setProjectID(projectID);
					newAction.setShortName(action.shortName);
					newAction.setTolLevel(action.tolLevel);
					newAction.setTolAction(action.tolAction);
					sess.saveOrUpdate(newAction);
				}
			}

			tx.commit();

			if ((projectID.longValue() == matProjID.longValue()) && (projectID.longValue() != rootID.longValue())) {
				mat = (ModelToleranceMatrix) sess.get(ModelToleranceMatrix.class, matID);
			}
			if ((projectID.longValue() == matProjID.longValue()) && (projectID.longValue() == rootID.longValue())) {
				mat = (ModelToleranceMatrix) sess.get(ModelToleranceMatrix.class, matID);
				mat.tolString = getDataJSString(dataJS, "tolString");
				mat.maxImpact = ((Long) dataJS.get("maxImpact")).intValue();
				mat.maxProb = ((Long) dataJS.get("maxProb")).intValue();
			}

			mat.prob1 = getDataJSString(dataJS, "prob1");
			mat.prob2 = getDataJSString(dataJS, "prob2");
			mat.prob3 = getDataJSString(dataJS, "prob3");
			mat.prob4 = getDataJSString(dataJS, "prob4");
			mat.prob5 = getDataJSString(dataJS, "prob5");
			mat.prob6 = getDataJSString(dataJS, "prob6");
			mat.prob7 = getDataJSString(dataJS, "prob7");
			mat.prob8 = getDataJSString(dataJS, "prob8");

			mat.probVal1 = getDataJSDouble(dataJS, "probVal1");
			mat.probVal2 = getDataJSDouble(dataJS, "probVal2");
			mat.probVal3 = getDataJSDouble(dataJS, "probVal3");
			mat.probVal4 = getDataJSDouble(dataJS, "probVal4");
			mat.probVal5 = getDataJSDouble(dataJS, "probVal5");
			mat.probVal6 = getDataJSDouble(dataJS, "probVal6");
			mat.probVal7 = getDataJSDouble(dataJS, "probVal7");
			mat.probVal8 = getDataJSDouble(dataJS, "prob8Val");

			mat.impact1 = getDataJSString(dataJS, "impact1");
			mat.impact2 = getDataJSString(dataJS, "impact2");
			mat.impact3 = getDataJSString(dataJS, "impact3");
			mat.impact4 = getDataJSString(dataJS, "impact4");
			mat.impact5 = getDataJSString(dataJS, "impact5");
			mat.impact6 = getDataJSString(dataJS, "impact6");
			mat.impact7 = getDataJSString(dataJS, "impact7");
			mat.impact8 = getDataJSString(dataJS, "impact8");

			tx = sess.beginTransaction();
			sess.saveOrUpdate(mat);
			tx.commit();

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

		outputJSON(getRiskProject(projectID, sess), response);
	}
	
	private String getDataJSString(final JSONObject dataJS, final String key) {
		String str = null;

		try {
			str = (String) dataJS.get(key);
		} catch (Exception e) {
			str = null;
		}

		return str;
	}
	private Double getDataJSDouble(final JSONObject dataJS, final String key) {

		Double val = new Double(0.0);
		try {

			Object obj = dataJS.get(key);

			if (obj instanceof Long) {
				val = ((Long) obj).doubleValue();
			}
			if (obj instanceof String) {
				val = Double.parseDouble(((String) obj));
			}
			if (obj instanceof Double) {
				val = ((Double) obj);
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			val = 0.0;
		}
		return val;
	}
	
}
