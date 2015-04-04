package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.dto.ModelScheduledJob;
import au.com.quaysystems.qrm.server.JobController;

@SuppressWarnings("serial")
@WebServlet (value = "/scheduleJobDelete", asyncSupported = false)
public class ServletScheduleJobDelete extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		try {
			for (Object x : (JSONArray) parser.parse(request.getParameter("DATA"))) {

				Long internalID = getLongJS(((JSONObject)x).get("internalID"));

				sess.beginTransaction();
				ModelScheduledJob job = (ModelScheduledJob)sess.get(ModelScheduledJob.class, internalID);
				sess.delete(job);
				sess.getTransaction().commit();

				// Unschedule the Job
				JobController.cancelJob(internalID);
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} 

	}
}
