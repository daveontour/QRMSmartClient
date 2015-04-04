package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.File;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.dto.ModelJobQueue;
import au.com.quaysystems.qrm.server.report.ReportProcessorData;

@SuppressWarnings("serial")
@WebServlet (value = "/deleteUserJobs", asyncSupported = false)
public class ServletDeleteUserJobs extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		try {
			for (Object x : (JSONArray) parser.parse(request.getParameter("DATA"))) {

				Transaction tx = sess.beginTransaction();
				ModelJobQueue job = (ModelJobQueue) sess.get(ModelJobQueue.class, getLongJS(((JSONObject)x).get("jobID")));
				sess.delete(job);
				tx.commit();

				sess.beginTransaction();
				ReportProcessorData jobData = (ReportProcessorData) sess.get(ReportProcessorData.class, getLongJS(((JSONObject)x).get("jobID")));
				if (jobData != null){
					sess.delete(jobData);
				}
				sess.getTransaction().commit();

				sess.createSQLQuery("DELETE from jobdata WHERE jobID = " + job.getJobID()).executeUpdate();
				
				File fileTemp = new File(configProp.getProperty("REPORT_TEMP_DIR")+"ReportJob"+job.getJobID());
				if (fileTemp.exists()){
				    fileTemp.delete();
				}
			}
		} catch (Exception e) {
			log.error(e);
		} 
	}
}
