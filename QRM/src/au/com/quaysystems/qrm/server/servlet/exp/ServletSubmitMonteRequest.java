package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.ModelJobMonteData;
import au.com.quaysystems.qrm.dto.ModelJobQueue;
import au.com.quaysystems.qrm.server.QRMAsyncMessage;

@SuppressWarnings("serial")
@WebServlet (value = "/submiteMonteRequest", asyncSupported = false)
public class ServletSubmitMonteRequest extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		try {

			Long iterations = QRMConstants.DEFAULTITERATIONS;
			JSONObject input = (JSONObject) parser.parse(request.getParameter("DATA"));

			try {
				iterations = (Long) input.get("iterations");
			} catch (Exception e1) {
				iterations = Long.parseLong((String) input.get("iterations"));
			}

			ModelJobQueue job = new ModelJobQueue();
			job.setRootProjectID(getRootProjectID(sess)[0]);
			job.setUserID(userID);
			job.setProjectID(projectID);
			job.setState("Queued");
			job.setJobJdbcURL((String) (request.getSession().getAttribute("session.url")));
			job.setJobDescription("Monte Carlo Analysis");
			job.setJobType("MONTE");
			job.setReportFormat((String) input.get("reportFormatType"));
			job.setReadyToExecute(false);


			Transaction tx = sess.beginTransaction();
			Long jobID = (Long) sess.save(job);
			tx.commit();

			ModelJobMonteData monteJob = new ModelJobMonteData();
			monteJob.setJobID(jobID);
			monteJob.setProjectID(projectID);
			monteJob.setStartDate(df.parse(((String) input.get("start")).substring(0, 9)));
			monteJob.setEndDate(df.parse(((String) input.get("end")).substring(0, 9)));
			monteJob.setForceConsequencesActive((Boolean) input.get("forceConsequencesActive"));
			monteJob.setForceRiskActive((Boolean) input.get("forceRiskActive"));
			monteJob.setSimType(((Long) input.get("simType")).intValue());
			monteJob.setNumIterations(iterations.intValue());
			monteJob.setProcessed(false);


			String str = "INSERT INTO jobdata (jobID, riskID) VALUES ";
			for (Object x : (JSONArray) input.get("riskIDs")) {
				str = str + "(" + job.getJobID() + "," + x + "),";
			}
			str = str.substring(0, str.length() - 1);
			sess.createSQLQuery(str).executeUpdate();

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("MonteJob", xsXML.toXML(monteJob));
			data.put("JobQueue",  xsXML.toXML(job));

			tx = sess.beginTransaction();
			sess.save(monteJob);
			tx.commit();


			try {
				QRMAsyncMessage message = new QRMAsyncMessage(QRMConstants.MONTE_MSG, data);
				message.send();
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}

			outputJSON(jobID,response);
		} catch (Exception e1) {
			log.error("QRM Stack Trace", e1);
		}

	}
}
