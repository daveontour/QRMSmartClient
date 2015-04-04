package au.com.quaysystems.qrm.server;

import java.util.Date;
import java.util.HashMap;

import org.hibernate.event.internal.DefaultPostLoadEventListener;
import org.hibernate.event.spi.PostLoadEvent;

import au.com.quaysystems.qrm.dto.ModelEmailRecord;
import au.com.quaysystems.qrm.dto.ModelIncident;
import au.com.quaysystems.qrm.dto.ModelIncidentUpdate;
import au.com.quaysystems.qrm.dto.ModelJobQueue;
import au.com.quaysystems.qrm.dto.ModelJobQueueMgr;
import au.com.quaysystems.qrm.dto.ModelMitigationStep;
import au.com.quaysystems.qrm.dto.ModelReview;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskConsequence;
import au.com.quaysystems.qrm.dto.ModelRiskLite;
import au.com.quaysystems.qrm.dto.ModelRiskLiteExt;
import au.com.quaysystems.qrm.dto.ModelRiskProject;
import au.com.quaysystems.qrm.dto.ModelScheduledJob;
import au.com.quaysystems.qrm.dto.ModelWelcomeData;
import au.com.quaysystems.qrm.server.report.ReportProcessorData;

import com.thoughtworks.xstream.XStream;

/**
 * 
 * This class defines all the special processing that has to be done on different classes 
 * when they have been loaded
 */
	@SuppressWarnings("serial") class PostLoadEventListener extends	DefaultPostLoadEventListener {
		
		@SuppressWarnings("unchecked")
		public void onPostLoad(PostLoadEvent event) {
			
			
			 if (event.getEntity() instanceof ModelRisk){
				 ModelRisk risk = (ModelRisk)event.getEntity();
				 risk.setCurrentProbImpact();
					try {
						risk.setDateEvalApp(new Date(risk.getDateEvalApp().getTime()));
					} catch (Exception e1) {}
					try {
						risk.setDateEvalRev(new Date(risk.getDateEvalRev().getTime()));
					} catch (Exception e1) {}
					try {
						risk.setDateIDApp(new Date(risk.getDateIDApp().getTime()));
					} catch (Exception e1) {}
					try {
						risk.setDateIDRev(new Date(risk.getDateIDRev().getTime()));
					} catch (Exception e1) {}
					try {
						risk.setDateMitApp(new Date(risk.getDateMitApp().getTime()));
					} catch (Exception e1) {}
					try {
						risk.setDateMitRev(new Date(risk.getDateMitRev().getTime()));
					} catch (Exception e1) {}
					try {
						risk.setDateMitPrep(new Date(risk.getDateMitPrep().getTime()));
					} catch (Exception e1) {}

					try {
						risk.setBeginExposure(new Date(risk.getBeginExposure().getTime()));
					} catch (Exception e1) {}
					try {
						risk.setEndExposure(new Date(risk.getEndExposure().getTime()));
					} catch (Exception e1) {}

					try {
						risk.setDateEntered(new Date(risk.getDateEntered().getTime()));
					} catch (Exception e1) {}
					try {
						risk.setDateUpdated(new Date(risk.getDateUpdated().getTime()));
					} catch (Exception e1) {}
			} else if (event.getEntity() instanceof ModelRiskLite){
				 ModelRiskLite risk = (ModelRiskLite)event.getEntity();
				 risk.setCurrentProbImpact();
			} else if (event.getEntity() instanceof ModelRiskLiteExt){
				 ModelRiskLiteExt risk = (ModelRiskLiteExt)event.getEntity();
				 risk.setCurrentProbImpact();
				 if (risk.promotedProjectID != null){
					 risk.promotionCode = risk.fromProjCode+"->"+risk.toProjCode;
				 } else {
					 risk.promotionCode = "-";
				 }
//				 if (risk.parentRiskProjectCode == null){
//					 risk.parentRiskProjectCode = "-";
//				 }
			} else if (event.getEntity() instanceof ModelRiskProject){
				ModelRiskProject proj = (ModelRiskProject)event.getEntity();
				try {
					proj.setProjectStartDate(new Date(proj.getProjectStartDate().getTime()));
					proj.setProjectEndDate(new Date(proj.getProjectEndDate().getTime()));
				} catch (Exception e) {
					proj.setRiskManager("-");
				}
			} else if (event.getEntity() instanceof ModelMitigationStep){
				ModelMitigationStep step = (ModelMitigationStep)event.getEntity();
				step.setEndDate(new Date(step.getEndDate().getTime()));
			} else if (event.getEntity() instanceof ModelEmailRecord){
				 ModelEmailRecord rec = ( ModelEmailRecord)event.getEntity();
				 rec.emaildate = new Date(rec.emaildate.getTime());
			} else if (event.getEntity() instanceof ModelRiskConsequence){
				ModelRiskConsequence c = (ModelRiskConsequence)event.getEntity();
				c.compile();
			} else if (event.getEntity() instanceof ModelWelcomeData){
				ModelWelcomeData data = (ModelWelcomeData)event.getEntity();
				try {
					data.date = new Date(data.date.getTime());
				} catch (Exception e) {
					data.date = new Date();
				}
			} else if (event.getEntity() instanceof ModelIncidentUpdate){
				ModelIncidentUpdate update = (ModelIncidentUpdate)event.getEntity();
				update.dateEntered = new Date(update.dateEntered.getTime());
			} else if (event.getEntity() instanceof ModelIncident){
				ModelIncident incid = (ModelIncident)event.getEntity();
				incid.setDateIncident(new Date(incid.getDateIncident().getTime()));
			} else if (event.getEntity() instanceof ModelReview){
				ModelReview rev = (ModelReview)event.getEntity();
				rev.setScheduledDate(new Date(rev.getScheduledDate().getTime()));
				try {
					rev.setActualDate(new Date(rev.getActualDate().getTime()));
				} catch (Exception e) {
					rev.setActualDate(null);
				}
			} else if (event.getEntity() instanceof ModelJobQueue){
				ModelJobQueue job = (ModelJobQueue)event.getEntity();
				job.setQueuedDate(new Date(job.getQueuedDate().getTime()));
				try {
					job.setExecutedDate(new Date(job.getExecutedDate().getTime()));
				} catch (Exception e) {
					job.setExecutedDate(null);
				}
			} else if (event.getEntity() instanceof ModelJobQueueMgr){
				ModelJobQueueMgr job = (ModelJobQueueMgr)event.getEntity();
				job.setQueuedDate(new Date(job.getQueuedDate().getTime()));
				try {
					job.setExecutedDate(new Date(job.getExecutedDate().getTime()));
				} catch (Exception e) {
					job.setExecutedDate(null);
				}
			} else if (event.getEntity() instanceof ReportProcessorData){
				ReportProcessorData data = (ReportProcessorData)event.getEntity();
				try {
					XStream xs = new XStream();
					data.job = (ModelScheduledJob)xs.fromXML(data.jobStr);
					data.taskParamMap = (HashMap<Object, Object>)xs.fromXML(data.taskParamMapStr);
					try {
						data.dateEmailSent = (new Date(data.dateEmailSent.getTime()));
					} catch (Exception e) {
						data.dateEmailSent = null;
					}
				} catch (Exception e) {
					
				}
			} else if (event.getEntity() instanceof ModelScheduledJob){
				ModelScheduledJob data = (ModelScheduledJob)event.getEntity();
				try {
					XStream xs = new XStream();
					data.taskParamMap = (HashMap<Object, Object>)xs.fromXML(data.taskParamMapStr);
				} catch (Exception e) {
					//e.printStackTrace();
				}
			} 
			super.onPostLoad(event);
		}
	}