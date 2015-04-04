/*
 * 
 */
package au.com.quaysystems.qrm.server.montecarlo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.ModelJobMonteData;
import au.com.quaysystems.qrm.dto.ModelJobMonteResult;
import au.com.quaysystems.qrm.dto.ModelJobQueue;
import au.com.quaysystems.qrm.dto.ModelQuantImpactType;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskConsequence;
import au.com.quaysystems.qrm.dto.ModelRiskProject;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.QRMAsyncMessage;
import au.com.quaysystems.qrm.server.QRMTXManager;
import au.com.quaysystems.qrm.server.report.ReportProcessorData;
import au.com.quaysystems.qrm.server.servlet.exp.ServletUserMessageManager;

import com.thoughtworks.xstream.XStream;

public class MonteCarloProcessor{

	private final QRMTXManager txmgr = new QRMTXManager();
	private final Integer WIDTH = 600;
	private final Integer HEIGHT = 400;
	private XStream xsXML = new XStream();
	private static Logger log = Logger.getLogger("au.com.quaysystems.qrm");


	@SuppressWarnings("unchecked")
	private synchronized final void executeMonteJob(final ModelJobMonteData jobMonte, ModelJobQueue jobRec, final Session sess){

		Long jobID = jobMonte.getJobID();

		Transaction tx = sess.beginTransaction();
		ModelJobQueue jobQ = (ModelJobQueue) sess.get(ModelJobQueue.class, jobID);
		jobQ.setState("Processing");
		jobQ.setProcessing(true);
		jobQ.setReadyToExecute(false);
		sess.save(jobQ);
		tx.commit();
		
		try {
			ServletUserMessageManager.notifyReportStatus(jobRec.getUserID());
		} catch (IOException e) {
			e.printStackTrace();
		}


		try {
			
			HashMap<Long, ModelQuantImpactType> type = new HashMap<Long, ModelQuantImpactType>();

			List<ModelQuantImpactType> impactTypes= sess.createSQLQuery("SELECT 0 AS GENERATION, quantimpacttype.* FROM quantimpacttype").addEntity(ModelQuantImpactType.class).list();
			for (ModelQuantImpactType t : impactTypes) {
				type.put(t.typeID, t);
			}

			List<ModelRisk> risks = sess.getNamedQuery("getRequestRisks").setLong("jobID", jobID).list();
			for (ModelRisk risk : risks) {
				try {
					risk.setProbConsequenceNodes(new ArrayList<ModelRiskConsequence>(txmgr.getRiskConsequences(risk.getInternalID(), sess)));
				} catch (RuntimeException e) {
					log.error(e);
					continue;
				}
			}

			MonteEngineOutput out = null;

			try {
				out = runMulti(risks,
						jobMonte.getNumIterations(),
						jobMonte.getStartDate(),jobMonte.getEndDate(),
						jobMonte.getProjectID(),jobMonte.isForceRiskActive(),
						jobMonte.isForceConsequencesActive(),impactTypes,sess);
			} catch (Exception e) {
				log.error(e);
			}

			tx = sess.beginTransaction();
			jobQ.setState("Compiling Results for the Report (Job ID: "+ jobID + " )");
			sess.save(jobQ);
			tx.commit();
			
			try {
				ServletUserMessageManager.notifyReportStatus(jobRec.getUserID());
			} catch (IOException e) {
				e.printStackTrace();
			}


			HashMap<Long, ArrayList<Double>> preMitTypeResults = out.getPreTypeResult();
			ArrayList<Double> preMitCostResult = out.getPreCostSummaryResult();
			HashMap<Long, ArrayList<Double>> postMitTypeResults = out.getPostTypeResult();
			ArrayList<Double> postMitCostResult = out.getPostCostSummaryResult();

			String preResStrCost = "";
			String postResStrCost = "";

			MonteCanvas canvas = new MonteCanvas();

			if ((preMitCostResult != null)&& (preMitCostResult.size() > 0)) {

				for (Double d : preMitCostResult) {
					preResStrCost = preResStrCost + d + "#";
				}
				preResStrCost = preResStrCost.substring(0, preResStrCost.length() - 1);

				for (Double d : postMitCostResult) {
					postResStrCost = postResStrCost + d + "#";
				}
				postResStrCost = postResStrCost.substring(0, postResStrCost.length() - 1);

				ModelJobMonteResult resPreCost = new ModelJobMonteResult();
				resPreCost.setTypeID(0L);
				resPreCost.setJobID(jobID);
				resPreCost.setResultStr(preResStrCost);
				resPreCost.setPreMit(true);
				canvas.setInput(preMitCostResult,"Total Cost Summary", "Cost", true);
				resPreCost.setImage(canvas.getOutputStream(WIDTH, HEIGHT));

				ModelJobMonteResult resPostCost = new ModelJobMonteResult();
				resPostCost.setTypeID(0L);
				resPostCost.setJobID(jobID);
				resPostCost.setPreMit(false);
				resPostCost.setResultStr(postResStrCost);
				canvas.setInput(postMitCostResult,"Total Cost Summary", "Cost", false);
				resPostCost.setImage(canvas.getOutputStream(WIDTH, HEIGHT));

				sess.save(resPreCost);
				sess.save(resPostCost);
			}

			for (Long typeID : preMitTypeResults.keySet()) {

				String preResStr = "";
				String postResStr = "";

				for (Double d : preMitTypeResults.get(typeID)) {
					preResStr = preResStr + d + "#";
				}
				preResStr = preResStr.substring(0, preResStr.length() - 1);

				for (Double d : postMitTypeResults.get(typeID)) {
					postResStr = postResStr + d + "#";
				}
				postResStr = postResStr.substring(0, postResStr.length() - 1);

				ModelJobMonteResult resPre = new ModelJobMonteResult();
				resPre.setTypeID(typeID);
				resPre.setJobID(jobID);
				resPre.setResultStr(preResStr);
				resPre.setPreMit(true);
				canvas.setInput(preMitTypeResults.get(typeID), type.get(typeID).getDescription(), type.get(typeID).getUnits(), true);
				resPre.setImage(canvas.getOutputStream(WIDTH,HEIGHT));
				if (preMitTypeResults.get(typeID) != null){
					ArrayList<Double> points = preMitTypeResults.get(typeID);
					resPre.setP10(points.get((int)(points.size() * 0.1)));
					resPre.setP20(points.get((int)(points.size() * 0.2)));
					resPre.setP30(points.get((int)(points.size() * 0.3)));
					resPre.setP40(points.get((int)(points.size() * 0.4)));
					resPre.setP50(points.get((int)(points.size() * 0.5)));
					resPre.setP60(points.get((int)(points.size() * 0.6)));
					resPre.setP70(points.get((int)(points.size() * 0.7)));
					resPre.setP80(points.get((int)(points.size() * 0.8)));
					resPre.setP90(points.get((int)(points.size() * 0.9)));
					resPre.setP100(points.get((int)(points.size() -1)));
				}

				ModelJobMonteResult resPost = new ModelJobMonteResult();
				resPost.setTypeID(typeID);
				resPost.setJobID(jobID);
				resPost.setResultStr(postResStr);
				resPost.setPreMit(false);
				canvas.setInput(postMitTypeResults.get(typeID),	type.get(typeID).getDescription(), type.get(typeID).getUnits(), false);
				resPost.setImage(canvas.getOutputStream(WIDTH,	HEIGHT));
				if (preMitTypeResults.get(typeID) != null){
					ArrayList<Double> points = postMitTypeResults.get(typeID);
					resPost.setP10(points.get((int)(points.size() * 0.1)));
					resPost.setP20(points.get((int)(points.size() * 0.2)));
					resPost.setP30(points.get((int)(points.size() * 0.3)));
					resPost.setP40(points.get((int)(points.size() * 0.4)));
					resPost.setP50(points.get((int)(points.size() * 0.5)));
					resPost.setP60(points.get((int)(points.size() * 0.6)));
					resPost.setP70(points.get((int)(points.size() * 0.7)));
					resPost.setP80(points.get((int)(points.size() * 0.8)));
					resPost.setP90(points.get((int)(points.size() * 0.9)));
					resPost.setP100(points.get((int)(points.size() -1)));
				}

				tx = sess.beginTransaction();

				sess.save(resPre);
				sess.save(resPost);
				tx.commit();

			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);

			tx = sess.beginTransaction();
			jobQ.setState("Failed");
			jobQ.setProcessing(false);
			jobQ.setReadyToExecute(false);
			jobQ.setReadyToCollect(false);
			jobQ.setFailed(true);
			jobQ.setExecutedDate(new Date());
			sess.save(jobQ);
			tx.commit();

			return;

		}

		sess.beginTransaction();
		ModelJobMonteData jobData = (ModelJobMonteData)sess.createCriteria(ModelJobMonteData.class).add(Restrictions.eq("jobID", jobID)).uniqueResult();
		jobData.setProcessed(true);
		sess.save(jobData);
		sess.getTransaction().commit();

		// PUSH THE PROCESSING TO THE REPORT PROCESSOR TO PRODUCE THE REPORT

		try {

			log.info("Monte Carlo Processor Sending Job to Report Processor");
			
			SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy");

			HashMap<Object, Object> taskParamMap = new HashMap<Object, Object>();
			taskParamMap.put("reportName", "");
			taskParamMap.put("QRMREPORTID", QRMConstants.MONTECARLOREPORT);
			taskParamMap.put("ReportID",  QRMConstants.MONTECARLOREPORT);
			taskParamMap.put("numIteration", jobMonte.getNumIterations());
			taskParamMap.put("startDate", sdf.format(jobMonte.getStartDate()));
			taskParamMap.put("endDate",sdf.format(jobMonte.getEndDate()));
			taskParamMap.put("forceRisk",jobMonte.isForceRiskActive());
			taskParamMap.put("forceConseq",jobMonte.isForceConsequencesActive());
			taskParamMap.put("QRMREPORTDESC", "Monte Carlo Analysis");
			taskParamMap.put("QRMJDBCURL", jobRec.getJobJdbcURL());
			taskParamMap.put("format", jobRec.getReportFormat());

			ReportProcessorData reportJob = new ReportProcessorData();
			reportJob.taskParamMap = taskParamMap;
			reportJob.jdbcURL = jobRec.getJobJdbcURL();
			reportJob.reportID =  new Long(QRMConstants.MONTECARLOREPORT);
			reportJob.description = "Monte Carlo Analysis";
			reportJob.format = "PDF";
			reportJob.jobID = jobRec.getJobID();
			reportJob.userID = jobRec.getUserID();

			sess.beginTransaction();
			sess.saveOrUpdate(reportJob);
			sess.getTransaction().commit();
			sess.close();

			// Send the message to the report processor
			try {
				new QRMAsyncMessage(reportJob).send();
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
	}

	@SuppressWarnings("unchecked")
	public final MonteEngineOutput runMulti(final List<ModelRisk> risks, final int its,
			final Date startDate, final Date endDate, final Long projectID,
			final boolean riskActive, final boolean conActive,
			final List<ModelQuantImpactType> impactTypes, final Session sess) {

		System.gc();

		HashMap<Long, ModelRiskProject> projects = new HashMap<Long, ModelRiskProject>();

		HashMap<Long, ModelQuantImpactType> impactTypeMap = new HashMap<Long, ModelQuantImpactType>();
		for (ModelQuantImpactType type : impactTypes) {
			impactTypeMap.put(type.typeID, type);
		}
		impactTypes.clear();

		for (ModelRiskProject p : (List<ModelRiskProject>) sess.getNamedQuery("getAllRiskProjectsForUserLite").setLong("user_id", 1L).list()) {
			projects.put(p.projectID, p);
		}

		try {
			MonteEngine engine = new MonteEngine(new MonteEngineInput(risks, impactTypeMap, impactTypes, projects, its, startDate, endDate,  riskActive, conActive));
			MonteEngineOutput out = engine.run();
			return out;
		} catch (Exception e) {
			log.error( e);
			return null;
		} finally {
			System.gc();
		}
	}


	@SuppressWarnings( "rawtypes")
	public synchronized void deliver(final Map dataMap) {

		ModelJobMonteData jobMonte = (ModelJobMonteData) xsXML.fromXML((String)dataMap.get("MonteJob"));
		ModelJobQueue job = (ModelJobQueue) xsXML.fromXML((String)dataMap.get("JobQueue"));

		Session sess = PersistenceUtil.getSession(job.getJobJdbcURL());
		executeMonteJob(jobMonte, job, sess);
		if (sess.isOpen()){
			sess.close();
		}
	}
}
