package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import au.com.quaysystems.qrm.dto.ModelScheduledJob;

@SuppressWarnings("serial")
@WebServlet (value = "/getUserScheduledJobs", asyncSupported = false)
public class ServletGetUserScheduledJobs extends QRMRPCServlet {

	@SuppressWarnings("unchecked")
	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		try {
			List<ModelScheduledJob> jobs = (List<ModelScheduledJob>)sess
					.createCriteria(ModelScheduledJob.class)
					.add(Restrictions.eq("userID",userID ))
					.list();

			for (ModelScheduledJob job: jobs){
				if (job.additionalUsers != null) {
					job.additionalUsers = job.additionalUsers.replace(',', '\n');
				}
			}

			outputJSON(jobs,response);
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			log.error("QRM Stack Trace", e);

		}
	}
}
