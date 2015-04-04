package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.dto.ModelScheduledJob;
import au.com.quaysystems.qrm.server.JobController;
import au.com.quaysystems.qrm.server.JobScheduler;
import au.com.quaysystems.qrm.server.report.ReportProcessorData;

@SuppressWarnings("serial")
@WebServlet (value = "/scheduleJobUpdate", asyncSupported = false)
public class ServletScheduleJobUpdate extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		/*
		 * -1 as in internal ID indicates a new Job
		 */
		try {

			JSONObject obj = (JSONObject) parser.parse(request.getParameter("DATA"));
			ModelScheduledJob job = new  ModelScheduledJob();
			if (obj.get("update") != null){
				try {
					job.internalID = getLongJS(obj.get("internalID"));
				} catch (Exception e1) {
					job.internalID = null;
				}
			}
			if (job.internalID != null){
				job = (ModelScheduledJob) sess.get(ModelScheduledJob.class, job.internalID);
			}
			try {
				ReportProcessorData jobData =(ReportProcessorData) sess.get(ReportProcessorData.class, getLongJS(obj.get("jobID")));
				job.taskParamMapStr = jobData.taskParamMapStr;
				job.taskParamMap = jobData.taskParamMap;

			} catch (Exception e2) {
				// Do nothing occurs on an update
//				e2.printStackTrace();
			}


			try {
				job.projectID = getLongJS(obj.get("projectID"));
			} catch (Exception e1) {
				job.projectID = null;
			}
			
			job.taskParamMap.put("projectID", job.projectID);
			job.taskParamMap.put("contextID", job.projectID);
			
			job.taskParamMap.put("contextName", obj.get("projectName"));
			job.taskParamMap.put("project", obj.get("projectName"));
			
			if (job.taskParamMap.get("project") == null){
				job.taskParamMap.put("contextName", obj.get("-"));
			}


			job.descendants = (Boolean)obj.get("descendants");
			job.taskParamMap.put("descendants", job.descendants);

			if (job.descendants == null) job.descendants = false;
			try {
				job.reportID = getLongJS(obj.get("reportID"));
			} catch (Exception e1) {
				return;
			}

			job.description = (String)obj.get("description");
			job.repository = getRepositoryID((String) request.getSession().getAttribute("session.url"));
			job.userID = userID;

			Long min = (Long) obj.get("Min");
			Long hour = (Long) obj.get("Hour");


			String time = ":";
			if(hour < 10){
				time = "0"+hour+time;
			} else {
				time = hour+time;
			}

			if(min < 10){
				time = time+"0"+min;
			} else {
				time = time+min;
			}

			job.timeStr = time;
			job.Mon = (Boolean)obj.get("Mon");
			job.Tue = (Boolean)obj.get("Tue");
			job.Wed = (Boolean)obj.get("Wed");
			job.Thu = (Boolean)obj.get("Thu");
			job.Fri = (Boolean)obj.get("Fri");
			job.Sat = (Boolean)obj.get("Sat");
			job.Sun = (Boolean)obj.get("Sun");
			job.email = (Boolean)obj.get("email");

			String additionalUsers = (String)obj.get("additionalUsers");
			if (additionalUsers != null){
				job.additionalUsers = additionalUsers.replace('\n', ',').replaceAll(",,", ",").toLowerCase();
			}

			if (job.email == null){
				job.email = false;
			}

			//Save the job request
			try {
				sess.beginTransaction();
				sess.saveOrUpdate(job);
				sess.getTransaction().commit();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error("QRM Stack Trace", e);
			}

			// Un schedule the job and then reschedule to account for any changes.
			JobController.cancelJob(job.internalID);
			// Schedule the Job
			JobController.addJobAndScedule( new JobScheduler(job));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("QRM Stack Trace", e);
		}

	}
}
