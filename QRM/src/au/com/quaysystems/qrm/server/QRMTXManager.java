package au.com.quaysystems.qrm.server;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.ModelDataObjectAllocation;
import au.com.quaysystems.qrm.dto.ModelImportTemplate;
import au.com.quaysystems.qrm.dto.ModelIncident;
import au.com.quaysystems.qrm.dto.ModelIncidentConsequence;
import au.com.quaysystems.qrm.dto.ModelIncidentUpdate;
import au.com.quaysystems.qrm.dto.ModelJobQueue;
import au.com.quaysystems.qrm.dto.ModelMitigationStep;
import au.com.quaysystems.qrm.dto.ModelMultiLevel;
import au.com.quaysystems.qrm.dto.ModelObjective;
import au.com.quaysystems.qrm.dto.ModelPerson;
import au.com.quaysystems.qrm.dto.ModelQRMReport;
import au.com.quaysystems.qrm.dto.ModelQuantImpactType;
import au.com.quaysystems.qrm.dto.ModelReview;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskConsequence;
import au.com.quaysystems.qrm.dto.ModelRiskControl;
import au.com.quaysystems.qrm.dto.ModelRiskLite;
import au.com.quaysystems.qrm.dto.ModelRiskProject;
import au.com.quaysystems.qrm.dto.ModelToleranceDescriptors;
import au.com.quaysystems.qrm.dto.ModelToleranceMatrix;
import au.com.quaysystems.qrm.dto.ModelUpdateComment;
import au.com.quaysystems.qrm.dto.ModelWelcomeData;


public class QRMTXManager {
	
	private Logger log;
	
	public QRMTXManager(){
		log = Logger.getLogger("au.com.quaysystems.qrm");
	}
	
	@SuppressWarnings("unchecked")
	final public ArrayList<ModelObjective> getProjectObjectives(final Long projectID, final Session sess) {
		return new ArrayList<ModelObjective>(sess.getNamedQuery("getProjectObjectives").setLong("projectID", projectID).list());
	}

	@SuppressWarnings("unchecked")
	final public ArrayList<ModelQuantImpactType> getProjectQuantTypes(final Long projectID, final Session sess) {
		return new ArrayList<ModelQuantImpactType>(sess.getNamedQuery("getProjectQuantTypes").setLong("projectID", projectID).list());
	}

	@SuppressWarnings("unchecked")
	final public List<ModelPerson> getProjectRiskManagers(final Long projectID, final Session sess) {
		return new ArrayList<ModelPerson>(sess.getNamedQuery("getProjectRiskManagers").setLong("projectID", projectID).list());
	}

	@SuppressWarnings("unchecked")
	final public List<ModelPerson> getProjectRiskOwners(final Long projectID,final Session sess) {
		return new ArrayList<ModelPerson>(sess.getNamedQuery("getProjectRiskOwners").setLong("projectID", projectID).list());
	}

	@SuppressWarnings("unchecked")
	final public List<ModelPerson> getProjectUsers(final Long projectID, final Session sess) {
		return new ArrayList<ModelPerson>(sess.getNamedQuery("getProjectRiskUsers").setLong("projectID", projectID).list());
	}

	@SuppressWarnings("unchecked")
	final public ArrayList<ModelQRMReport> getQRMReportsContext(final Session sess) {
		return new ArrayList<ModelQRMReport>(sess.getNamedQuery("getAllReportsContext").list());
	}

	@SuppressWarnings("unchecked")
	final public ArrayList<ModelQRMReport> getQRMReportsRegister(final Session sess) {
		return new ArrayList<ModelQRMReport>(sess.getNamedQuery("getAllReportsRegister").list());
	}
	@SuppressWarnings("unchecked")
	final public ArrayList<ModelQRMReport> getQRMReportsRepository(final Session sess) {
		return new ArrayList<ModelQRMReport>(sess.getNamedQuery("getAllReportsRepository").list());
	}
	@SuppressWarnings("unchecked")
	final public ArrayList<ModelQRMReport> getQRMReportsIncident(final Session sess) {
		return new ArrayList<ModelQRMReport>(sess.getNamedQuery("getAllReportsIncident").list());
	}
	@SuppressWarnings("unchecked")
	final public ArrayList<ModelQRMReport> getQRMReportsReview(final Session sess) {
		return new ArrayList<ModelQRMReport>(sess.getNamedQuery("getAllReportsReview").list());
	}

	@SuppressWarnings("unchecked")
	final public ArrayList<ModelQRMReport> getQRMReportsRisk(final Session sess) {
		return new ArrayList<ModelQRMReport>(sess.getNamedQuery("getAllReportsRisk").list());
	}
	public final ModelPerson getPerson(final Long id, final Session sess) {
		return (ModelPerson) sess.createCriteria(ModelPerson.class).add(Restrictions.eq("stakeholderID", id)).add(Restrictions.eq("active", true)).uniqueResult();
	}
	@SuppressWarnings("unchecked")
	final public List<ModelMitigationStep> getRiskMitigationSteps(final Long riskID, final Session sess) {
		return (List<ModelMitigationStep>)sess.getNamedQuery("getRiskMitigationsteps").setLong("riskID", riskID).list();
	}
	@SuppressWarnings("unchecked")
	private List<ModelUpdateComment> getRiskMitigationStepsUpdates(Long riskID, Session sess) {
		return (List<ModelUpdateComment>)sess.getNamedQuery("getRiskMitigationStepsUpdate").setLong("riskID", riskID).list();
	}
	@SuppressWarnings("unchecked")
	final public ArrayList<ModelDataObjectAllocation> getToleranceAllocations(final Session sess, boolean rolled) {
		if (rolled){
			return (ArrayList<ModelDataObjectAllocation>) sess.getNamedQuery("getRolledAllocations").list();
		} else {
			return (ArrayList<ModelDataObjectAllocation>) sess.getNamedQuery("getAllocations").list();			
		}
	}

	@SuppressWarnings("unchecked")
	public final List<ModelReview> getExistingReviews(final Long projectID,	final Session sess) {
		return (List<ModelReview>) sess.createSQLQuery("SELECT * from review WHERE scheduledDate > NOW()").addEntity(ModelReview.class).list();
	}

	@SuppressWarnings("unchecked")
	public final List<ModelJobQueue> getAllUserJobs(final Long userID, final Session sess) {
		return (List<ModelJobQueue>)sess.getNamedQuery("getUserJobs").setLong("userID", userID).list();
	}
	@SuppressWarnings("unchecked")
	final public List<ModelRiskControl> getRiskControls(final Long riskID, final Session sess) {
		return sess.getNamedQuery("getRiskRiskControls").setLong("riskid", riskID).list();
	}
	@SuppressWarnings("unchecked")
	final public List<ModelWelcomeData> getWelcomeData(final Long projectID, final Session sess) {
		return (List<ModelWelcomeData>) sess.getNamedQuery("getWelcomeData").setLong("projectID", projectID).list();
	}

	@SuppressWarnings("unchecked")
	final public List<ModelRiskLite> getProjectRisksForUser(final Long userID,final Long projectID, final int descendants, final Session sess) {

		if (projectID.longValue() == -100L){
			return new ArrayList<ModelRiskLite>();
		}
		try {

			List<ModelRiskLite> risks = (List<ModelRiskLite>)sess.getNamedQuery("getProjectRisksForUser")
			.setLong("userID", userID)
			.setLong("projectID", projectID)
			.setLong("descendants",descendants)
			.list();

			Collections.sort(risks, new RiskComparator(false));
			return risks;
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	final public List<ModelRiskLite> getAllProjectRisksLite(final Long user_id,	final long projectID, final boolean descendants, final Session sess) {

		try {
			List<ModelRiskLite> risks = sess.getNamedQuery("getAllProjectRiskLite")
			.setLong("var_user_id", user_id)
			.setLong("var_projectID", projectID)
			.setBoolean("var_descendants", descendants)
			.list();

			Collections.sort(risks, new RiskComparator(false));

			return risks;

		} catch (RuntimeException e) {
			log.error("QRM Stack Trace", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	final public List<ModelRisk> getAllProjectRisks(final Long user_id,	final long projectID, final boolean descendants, final Session sess) {

		try {
			List<ModelRisk> risks = sess.getNamedQuery("getAllProjectRisk")
			.setLong("var_user_id", user_id)
			.setLong("var_projectID",projectID)
			.setBoolean("var_descendants",descendants)
			.list();

			for (ModelRisk risk : risks) {
				try {
					risk.setProbConsequenceNodes(new ArrayList<ModelRiskConsequence>(getRiskConsequences(risk.getInternalID(), sess)));
				} catch (RuntimeException e) {
					log.error("QRM Stack Trace", e);
				}
			}

			return risks;

		} catch (RuntimeException e) {
			log.error("QRM Stack Trace", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	final public List<ModelRiskConsequence> getRiskConsequences(final Long riskID, final Session sess) {

		try {
			List<ModelRiskConsequence> stepList = sess.createCriteria(ModelRiskConsequence.class)
			.add(Restrictions.eq( "riskID", riskID )).list();
			
			for (ModelRiskConsequence s : stepList) {
				s.setQuantImpactType((ModelQuantImpactType) sess
						.createSQLQuery("SELECT 0 AS GENERATION, quantimpacttype.* FROM quantimpacttype WHERE typeID = :id")
						.addEntity(ModelQuantImpactType.class)
						.setLong("id", s.quantType)
						.uniqueResult());
			}
			return stepList;
		} catch (RuntimeException e) {
			log.error("QRM Stack Trace", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	final public ArrayList<ModelRiskProject> getAllRiskProjectsForUserLite(final Long user_id, final Session sess) {
		try {
			return new ArrayList<ModelRiskProject>(sess.getNamedQuery("getAllRiskProjectsForUserLite").setLong("user_id", user_id).list());
		} catch (Exception e) {
			return new ArrayList<ModelRiskProject>();
		}
	}

	@SuppressWarnings("unchecked")
	final public ArrayList<ModelRiskProject> getAllMgmtRiskProjectsForUserLite(final Long user_id, final Session sess) {
		try {
			ArrayList<ModelRiskProject> list = new ArrayList<ModelRiskProject>(
					sess.getNamedQuery("getAllMgmtRiskProjectsForUserLite")
					.setLong("userID", user_id)
					.list());

			return list;
		} catch (Exception e) {
			return new ArrayList<ModelRiskProject>();
		}
	}

	public final ModelRiskProject getRiskProject(final Long projectID, final Session sess) {

		try {
			sess.setCacheMode(CacheMode.IGNORE);
			ModelRiskProject proj = (ModelRiskProject) sess.get(ModelRiskProject.class, projectID);
			
			setRiskmanagers(getProjectRiskManagers( projectID, sess), proj, true);
			setRiskowners(getProjectRiskOwners( projectID, sess), proj, true);
			proj.matrix = getProjectMatrix( projectID, sess);
			proj.setRiskCategorys(new ArrayList<ModelMultiLevel>(getProjectCategorys( projectID, sess)));
			proj.setTolActions(getProjectTolActions( projectID, sess));

			return proj;
		} catch (Exception e) {
			return null;
		}
	}

	public final ModelRiskProject getRiskProjectDetails(final Long projectID, final Session sess) {
		try {
			sess.setCacheMode(CacheMode.IGNORE);
			sess.flush();

			ModelRiskProject proj = getRiskProject(projectID, sess);

			setRiskusers(getProjectUsers(projectID, sess),proj, true);
			proj.setImpactTypes(getProjectQuantTypes(projectID, sess));
			proj.setObjectives(getProjectObjectives(projectID, sess));

			return proj;
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	final public List<ModelMultiLevel> getProjectCategorys(	final Long projectID, final Session sess) {

		sess.flush();
		List<ModelMultiLevel> allLevels = sess.getNamedQuery("getProjectCategorys").setLong("projectID", projectID).list();

		ArrayList<ModelMultiLevel> prims = new ArrayList<ModelMultiLevel>();

		try {
			for (ModelMultiLevel ml : allLevels) {
				if (ml.getParentID() == 1) {
					prims.add(ml);
				}
			}
			for (ModelMultiLevel ml : prims) {
				for (ModelMultiLevel ml2 : allLevels) {
					if (ml2.getParentID().longValue() == ml.getInternalID().longValue()
							&& ml2.getInternalID().longValue() != ml.getInternalID().longValue()) {
						ml.getSec().add(ml2);
					}
				}
			}

			return prims;
		} catch (RuntimeException e) {
			log.error("QRM Stack Trace", e);
			return new ArrayList<ModelMultiLevel>();
		}
	}
	@SuppressWarnings("unchecked")
	public final ModelToleranceMatrix getProjectMatrix(final Long projectID,
			final Session sess) {
		try {
			ArrayList<ModelToleranceMatrix> matrixs = new ArrayList<ModelToleranceMatrix>(sess.getNamedQuery("getProjectMats").setLong("projectID", projectID).list());
			ModelToleranceMatrix projectMat = matrixs.get(0);
			ModelToleranceMatrix rootMat = matrixs.get(matrixs.size() - 1);

			if (projectMat.impact1 == null) projectMat.impact1 = rootMat.impact1;
			if (projectMat.impact2 == null) projectMat.impact2 = rootMat.impact2;
			if (projectMat.impact3 == null) projectMat.impact3 = rootMat.impact3;
			if (projectMat.impact4 == null) projectMat.impact4 = rootMat.impact4;
			if (projectMat.impact5 == null) projectMat.impact5 = rootMat.impact5;
			if (projectMat.impact6 == null) projectMat.impact6 = rootMat.impact6;
			if (projectMat.impact7 == null) projectMat.impact7 = rootMat.impact7;
			if (projectMat.impact8 == null) projectMat.impact8 = rootMat.impact8;

			if (projectMat.prob1 == null) projectMat.prob1 = rootMat.prob1; 
			if (projectMat.prob2 == null) projectMat.prob2 = rootMat.prob2; 
			if (projectMat.prob3 == null) projectMat.prob3 = rootMat.prob3; 
			if (projectMat.prob4 == null) projectMat.prob4 = rootMat.prob4; 
			if (projectMat.prob5 == null) projectMat.prob5 = rootMat.prob5; 
			if (projectMat.prob6 == null) projectMat.prob6 = rootMat.prob6; 
			if (projectMat.prob7 == null) projectMat.prob7 = rootMat.prob7; 
			if (projectMat.prob8 == null) projectMat.prob8 = rootMat.prob8;

			if (projectMat.probVal1 == null) projectMat.probVal1 = rootMat.probVal1;
			if (projectMat.probVal2 == null) projectMat.probVal2 = rootMat.probVal2;
			if (projectMat.probVal3 == null) projectMat.probVal3 = rootMat.probVal3;
			if (projectMat.probVal4 == null) projectMat.probVal4 = rootMat.probVal4;
			if (projectMat.probVal5 == null) projectMat.probVal5 = rootMat.probVal5;
			if (projectMat.probVal6 == null) projectMat.probVal6 = rootMat.probVal6;
			if (projectMat.probVal7 == null) projectMat.probVal7 = rootMat.probVal7;
			if (projectMat.probVal8 == null) projectMat.probVal8 = rootMat.probVal8;

			projectMat.setTolString(rootMat.getTolString());
			projectMat.setMaxProb(rootMat.getMaxProb());
			projectMat.setMaxImpact(rootMat.getMaxImpact());

			return projectMat;
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public final ArrayList<ModelToleranceDescriptors> getProjectTolActions(
			final Long projectID, final Session sess) {
		try {
			ArrayList<ModelToleranceDescriptors> actions = new ArrayList<ModelToleranceDescriptors>(
					sess.getNamedQuery("getProjectTolActions")
					.setLong("projectID", projectID)
					.list());
			return new ArrayList<ModelToleranceDescriptors>(actions.subList(0,5));
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			return null;
		}
	}


	@SuppressWarnings("unchecked")
	final public ModelRisk getRisk(final Long riskID, final Long user_id, final long context_id, final Session sess) {

		ModelRisk risk = (ModelRisk) sess.getNamedQuery("getRisk")
		.setLong("userID", user_id)
		.setLong("riskID", riskID)
		.setLong("projectID", context_id)
		.uniqueResult();

		try {
			risk.setMitigationPlan(new ArrayList<ModelMitigationStep>(getRiskMitigationSteps(risk.getInternalID(), sess)));
			List<ModelUpdateComment> updates = getRiskMitigationStepsUpdates(risk.getInternalID(), sess);
			//add any updates to the mitigation
			for (ModelMitigationStep step:risk.getMitigationPlan()){
				for(ModelUpdateComment comment:updates){
					step.addUpdate(comment);
				}
			}
//			risk.setMitigationPlanUpdates(new ArrayList<ModelUpdateComment>(getRiskMitigationStepsUpdates(risk.getInternalID(), sess)));
			risk.setControls(new ArrayList<ModelRiskControl>(sess.getNamedQuery("getRiskControls").setLong("riskid", risk.getInternalID()).list()));
			risk.setObjectivesImpacted(getRiskObjectivesID(risk.getInternalID(), sess));
			risk.setProbConsequenceNodes(new ArrayList<ModelRiskConsequence>(getRiskConsequences(risk.getInternalID(), sess)));
		} catch (RuntimeException e) {
			log.error("QRM Stack Trace", e);
		}
		return risk;
	}



	public final ModelRiskLite getRiskLite(final Long riskID,final Long user_id, final long context_id, final Session sess) {

		try {
			return (ModelRiskLite) sess.getNamedQuery("getRiskLite")
			.setLong("userID", user_id)
			.setLong("riskID", riskID)
			.setLong("projectID", context_id)
			.uniqueResult();
		} catch (Exception e) {
			return null;
		}
	}

	public final ArrayList<Long> getRiskObjectivesID(final Long id,	final Session sess) {

		ArrayList<Long> ids = new ArrayList<Long>();

		for (Object obj : sess.createSQLQuery("SELECT objectiveID FROM objectives_impacted WHERE riskID = :id")
				.setLong("id", id).list()) {
			ids.add(((BigInteger) obj).longValue());
		}
		return ids;
	}

	public final long updateSubjRanks(final String[] riskIDs,
			final long projectID, final long user_id, final Session sess) {

		Transaction tx = sess.beginTransaction();

		try {
			sess.createSQLQuery("DELETE FROM subjrank WHERE projectID = :projectID")
			.setLong("projectID", projectID)
			.executeUpdate();

			long i = 1;
			for (String riskID : riskIDs) {
				sess.createSQLQuery("INSERT INTO subjrank (projectID, riskID, rank) VALUES (:projectID,:riskID,:rank)")
				.setLong("projectID", projectID)
				.setLong("riskID",Long.parseLong(riskID))
				.setLong("rank", i++)
				.executeUpdate();
			}

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			log.error("QRM Stack Trace", e);
		}

		return QRMConstants.QRM_OK;
	}

	public final long updateSummaryAllocations(final Long userID,
			final Long[] childID, final Long[] parentID, final Session sess) {

		if (childID.length != parentID.length) {
			return QRMConstants.QRM_FAULT;
		}

		for (int i = 0; i < childID.length; i++) {
			try {
				sess.createSQLQuery("UPDATE risk SET parentSummaryRisk = :parentID WHERE riskID = :childID")
				.setLong("parentID", parentID[i])
				.setLong("childID",	childID[i])
				.executeUpdate();
			} catch (RuntimeException e) {
				log.error("QRM Stack Trace", e);
				return QRMConstants.QRM_FAULT;
			}
		}
		return QRMConstants.QRM_OK;
	}

	@SuppressWarnings("unchecked")
	public final ModelIncident getIncident(final Long incidentID, final Session sess) {

		ModelIncident incident = (ModelIncident) sess.get(ModelIncident.class, incidentID);
		List<ModelIncidentConsequence> impacts = (List<ModelIncidentConsequence>)sess.createCriteria(ModelIncidentConsequence.class)
		.add(Restrictions.eq("incidentID",incidentID))
		.list();
		incident.setImpacts(new ArrayList<ModelIncidentConsequence>(impacts));

		ArrayList<Long> ids = new ArrayList<Long>();
		for (Object obj : sess.createSQLQuery("SELECT objectiveID FROM incidentobjective WHERE incidentID = :id")
				.setLong("id", incidentID).list()) {
			ids.add(((BigInteger) obj).longValue());
		}
		incident.setObjectivesImpacted(ids);
		
		incident.setUpdates(getIncidentUpdates(incidentID, sess));

		return incident;
	}
	@SuppressWarnings("unchecked")
	public final ArrayList<ModelIncidentUpdate> getIncidentUpdates(final Long incidentID, final Session sess) {
		
		List<ModelIncidentUpdate> updates = (List<ModelIncidentUpdate>)sess.createCriteria(ModelIncidentUpdate.class)
		.add(Restrictions.eq("incidentID",incidentID))
		.list();

		return new ArrayList<ModelIncidentUpdate>(updates);
	}
	@SuppressWarnings("unchecked")
	final public List<ModelImportTemplate> getUserTemplates(final Long userID, final Session sess) {

		try {
			List<ModelImportTemplate> templates = sess.getNamedQuery("getUserTemplates")
			.setLong("var_userid", userID)
			.list();

			return templates;

		} catch (RuntimeException e) {
			log.error("QRM Stack Trace", e);
			return null;
		}
	}

	final public void deleteUserTemplates(final String templateName, final Session sess) {
		
		sess.beginTransaction();
		
		 ModelImportTemplate template = (ModelImportTemplate)sess.createCriteria(ModelImportTemplate.class)
		.add(Restrictions.eq("templateName",templateName)).uniqueResult();
		 
		 sess.delete(template);


		 sess.getTransaction().commit();

	}
	public void addMitigationUpdate(final Long mitStepID, final String update, final Long userID, final Session sess) {
		try {
			sess.createSQLQuery("INSERT INTO `updatecomment`(`hostID`,`hostType`,`description`,`personID`) VALUES(:mitStepID, 'MITIGATION', :value, :userID)")
			.setLong("userID", userID)
			.setLong("mitStepID",	mitStepID)
			.setString("value", update)
			.executeUpdate();
		} catch (RuntimeException e) {
			log.error("QRM Stack Trace", e);
		}
	}

	private final class RiskComparator implements Comparator<ModelRiskLite> {

		private boolean level = false;
		public RiskComparator(final boolean level) {
			this.level = level;
		}
		public int compare(final ModelRiskLite r1, final ModelRiskLite r2) {

			if (this.level == false) {
				if ((r1.subjectiveRank == null) && (r2.subjectiveRank != null)) {
					return 1;
				}
				if ((r1.subjectiveRank != null) && (r2.subjectiveRank == null)) {
					return -1;
				}
				if ((r1.subjectiveRank == null) && (r2.subjectiveRank == null)) {
					return 0 - new Double(Math.floor(r1.currentTolerance)).compareTo(new Double(Math.floor(r2.currentTolerance)));
				}
				if ((r1.subjectiveRank != null)
						&& (r2.subjectiveRank != null)
						&& (r1.subjectiveRank == 0 || r2.subjectiveRank == 0)) {
					return 0 - new Double(Math.floor(r1.currentTolerance)).compareTo(new Double(Math.floor(r2.currentTolerance)));
				}
				return r1.subjectiveRank.compareTo(r2.subjectiveRank);
			}

			if ((r1.subjectiveRank == null)|| (r2.subjectiveRank == null)) {

				if ((r1.subjectiveRank == null)&& (r2.subjectiveRank != null)) {
					return 1;
				}
				if ((r1.subjectiveRank != null)&& (r2.subjectiveRank == null)) {
					return -1;
				}
				if ((r1.subjectiveRank == null)&& (r2.subjectiveRank == null)) {
					return 0 - new Double(Math.floor(r1.currentTolerance))
					.compareTo(new Double(Math.floor(r2.currentTolerance)));
				}
				if ((r1.subjectiveRank != null)
						&& (r2.subjectiveRank != null)
						&& (r1.subjectiveRank == 0 || r2.subjectiveRank == 0)) {
					return 0 - new Double(Math.floor(r1.currentTolerance))
					.compareTo(new Double(Math.floor(r2.currentTolerance)));
				}

				return r1.subjectiveRank.compareTo(r2.subjectiveRank);
			}
			return r1.subjectiveRank.compareTo(r2.subjectiveRank);
		}
	}
	
	private void setRiskmanagers(final List<ModelPerson> name, final ModelRiskProject proj, final boolean lite) {
		if (lite) {
			for (ModelPerson p : name) {
				proj.riskmanagerIDs.add(p.getStakeholderID());
			}
		} else {
			proj.setRiskmanagers(new ArrayList<ModelPerson>(name));
		}
	}
	private void setRiskowners(final List<ModelPerson> name,
			final ModelRiskProject proj, final boolean lite) {
		if (lite) {
			for (ModelPerson p : name) {
				proj.riskownersIDs.add(p.getStakeholderID());
			}
		} else {
			proj.setRiskowners(new ArrayList<ModelPerson>(name));
		}
	}
	private void setRiskusers(final List<ModelPerson> name, final ModelRiskProject proj, final boolean lite) {
		if (lite) {
			for (ModelPerson p : name) {
				proj.riskusersIDs.add(p.getStakeholderID());
			}
		} else {
			proj.setRiskowners(new ArrayList<ModelPerson>(name));
		}
		proj.setRiskusers(new ArrayList<ModelPerson>(name));
	}
}