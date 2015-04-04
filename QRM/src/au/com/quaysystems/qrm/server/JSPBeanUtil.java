package au.com.quaysystems.qrm.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.JobBean;
import au.com.quaysystems.qrm.dto.ModelJobQueue;
import au.com.quaysystems.qrm.dto.ModelPerson;
import au.com.quaysystems.qrm.dto.ModelQRMReport;
import au.com.quaysystems.qrm.dto.ModelRiskProject;
import au.com.quaysystems.qrm.server.report.ReportProcessorData;

public class JSPBeanUtil {

	private static SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");

	public static JobBean getReportJobBean(String url, String jobIDStr){
		 return getReportJobBean(PersistenceUtil.getSession(url), jobIDStr);		
	}
	
	@SuppressWarnings("unchecked")
	public static JobBean getReportJobBean(Session sess, String jobIDStr){
		
		Long jobID = Long.parseLong(jobIDStr);
		
		ReportProcessorData job = (ReportProcessorData)sess.get(ReportProcessorData.class, jobID);		
		ModelQRMReport rep = (ModelQRMReport) sess.get(ModelQRMReport.class, job.reportID);
		ModelJobQueue jobRec = (ModelJobQueue) sess.get(ModelJobQueue.class, jobID);
		ModelPerson user = (ModelPerson) sess.get(ModelPerson.class, job.userID);
		
		HashMap<Object, Object> taskParamMap = job.taskParamMap;
		
		ModelRiskProject project;
		if (taskParamMap.get("projectID") instanceof Long){
			project = (ModelRiskProject) sess.get(ModelRiskProject.class, (Long)taskParamMap.get("projectID"));			
		} else {
			try {
				project = (ModelRiskProject) sess.get(ModelRiskProject.class, new Long((Integer)taskParamMap.get("projectID")));
			} catch (Exception e) {
				project = null;
			}
		}
		HashMap<Long, ModelQRMReport> projectReportMap = new HashMap<>();
		for(ModelQRMReport report :  (new ArrayList<ModelQRMReport>(sess.getNamedQuery("getAllReportsContext").list()))){
			try {
				projectReportMap.put(report.internalID, report);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		sess.close();
		
		
		 JobBean bean = new JobBean();
		 
		 try {
			bean.reportDescription = rep.reportDescription;
		} catch (Exception e) {
			bean.reportDescription = "-";
		}
		 bean.person = user.name;
		 bean.executionTime = formatTime.format(jobRec.getExecutedDate());
		 bean.executionDate = formatDate.format(jobRec.getExecutedDate());
		 try {
			bean.reportID = rep.internalID;
		} catch (Exception e) {
			bean.reportID = 0L;
		}
		 
		 try {
			bean.projectID = new Long((Integer)taskParamMap.get("projectID"));
		} catch (Exception e) {
			bean.projectID = 0L;
		}
		 try {
			bean.projectName = project.projectTitle;
		} catch (Exception e) {
			bean.projectName = "Not Applicable";
		}
		 bean.descendants = (Boolean)taskParamMap.get("descendants");
	 
		 return bean;
		
	}
}
