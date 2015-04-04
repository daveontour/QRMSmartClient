package au.com.quaysystems.qrm.server;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.ModelJobQueue;
import au.com.quaysystems.qrm.dto.ModelRepository;
import au.com.quaysystems.qrm.dto.ModelScheduledJob;
import au.com.quaysystems.qrm.server.report.ReportProcessorData;

public class JobController{

	private static HashMap<Long, JobScheduler> jobMap = new HashMap<Long, JobScheduler>();
	private static Logger log = Logger.getLogger("au.com.quaysystems.qrm");

	public static void cancelJob(Long jobID){
		JobScheduler job = jobMap.get(jobID);
		if (job != null){
			job.cancel();
			jobMap.remove(jobID);
		}
	}
	public void scheduleJob(Long jobID){
		JobScheduler job = jobMap.get(jobID);
		if (job != null){
			job.start();
		}		
	}
	public void addJob(JobScheduler job){
		jobMap.put(job.jobID, job);
	}
	public static void addJobAndScedule(JobScheduler job){
		jobMap.put(job.jobID, job);
		job.start();
	}
	public void addJobs(List<JobScheduler> jobs){
		for (JobScheduler job:jobs){
			jobMap.put(job.jobID, job);
		}
	}
	public void addJobsAndScedule(List<JobScheduler> jobs){
		for (JobScheduler job:jobs){
			jobMap.put(job.jobID, job);
			job.start();
		}
	}
	public void cancelAllJobs(){
		for (JobScheduler job:jobMap.values()){
			cancelJob(job.jobID);
		}		
	}
	public static String getURLForJob(Long jobID){

		Session sess = PersistenceUtil.getSimpleControlSession();
		
		try {
			ModelRepository rep = (ModelRepository) sess
			.createCriteria(ModelRepository.class)
			.add(Restrictions.eq("repID",jobMap.get(jobID).getJobDetail().repository ))
			.uniqueResult();
			return rep.url;
		} catch (HibernateException e) {
			return null;
		} finally {
			sess.close();
		}
	}
	public synchronized static void processReportJob(ModelScheduledJob schedJob){
		
		ModelJobQueue jobRec;
		Long jobID = null;

		try {
			jobRec = new ModelJobQueue();
			jobRec.setUserID(schedJob.userID);
			jobRec.setState("Queued");
			jobRec.setJobJdbcURL(getURLForJob(schedJob.internalID));
			jobRec.setReadyToExecute(true);
			jobRec.setQueuedDate(new Date());
			jobRec.setUserID(schedJob.userID);
			jobRec.setJobType("REPORT");
			jobRec.setJobDescription(schedJob.description+" (scheduled)");

			Session sess = PersistenceUtil.getSession(jobRec.getJobJdbcURL());		
			sess.beginTransaction();
			jobID = (Long) sess.save(jobRec);
			sess.refresh(jobRec);
			sess.getTransaction().commit();
			sess.close();
			
		} catch (Exception e) {
			log.error(e);
		}

		ReportProcessorData jobData = new ReportProcessorData();
		jobData.taskParamMap = schedJob.taskParamMap;
		jobData.userID = schedJob.userID;
		jobData.projectID = schedJob.projectID;
		jobData.description = schedJob.description;
		jobData.reportID = schedJob.reportID;
		jobData.jdbcURL = getURLForJob(schedJob.internalID);
		jobData.schedJob = true;
		jobData.sendStatusUpdates = false;
		jobData.format = "PDF";
		jobData.sendEmail = true;
		jobData.emailFormat = "PLAIN";
		jobData.emailAttachment = true;
		jobData.emailContent = "Please find attached your scheduled report from Quay Risk Manager.";
		jobData.emailTitle = "QRM Scheduled Report - "+schedJob.description;
		jobData.job = schedJob;
		jobData.schedJobID = schedJob.internalID;
		jobData.jobID = jobID;
				
		HashMap<String, Object> data = new HashMap<>();
		data.put("job", jobData);
		
		try {
			QRMAsyncMessage message = new QRMAsyncMessage(QRMConstants.REPORT_MSG, data);
			message.send();
			log.info("Dispatched Report Job");
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
	}
}