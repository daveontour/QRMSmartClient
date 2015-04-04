package au.com.quaysystems.qrm.dto;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@MappedSuperclass
public class IModelRisk extends IModelRiskLite {


	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1334742238396972117L;


	public Long promotedProjectID;
	@Temporal(TemporalType.DATE)
	public Date dateEntered;
	@Temporal(TemporalType.DATE)
	public Date dateUpdated;
	public Long idEvalApp;
	public Long idEvalRev;
	public Long idIDApp;
	public Long idIDRev;
	public Long idMitApp;
	public Long idMitPrep;
	public Long idMitRev;
	@Transient
	public int estMitCost;
	public boolean impCost;
	public boolean impEnvironment;
	public boolean impReputation;
	public boolean impSafety;
	public boolean impSpec;
	public boolean impTime;
	public Double likealpha;
	public Double likepostAlpha;
	public Double likepostProb;
	public Double likepostT;
	public Integer likepostType;
	public Double likeprob;
	public Double liket;
	public Integer liketype;
	public Long manager2ID;
	@Column(name = "manager2Name", updatable = false, insertable = false)
	public String manager2Name;
	public Long manager3ID;
	@Column(name = "manager3Name", updatable = false, insertable = false)
	public String manager3Name;
	@Transient
	public ModelToleranceMatrix matrix;
	@Transient
	public int mean;
	@Transient
	public boolean userUpdateSecurity;
	@Transient
	public ArrayList<Long> objectivesImpacted;
	public boolean treatmentAvoidance;
	public boolean treatmentReduction;
	public boolean treatmentRetention;
	public boolean treatmentTransfer;
	@Transient
	public ArrayList<ModelMitigationStep> mitigationPlan;
	@Transient
	public ArrayList<ModelUpdateComment> mitigationPlanUpdates;
	@Transient
	public ArrayList<Long> childSummaryRisks;
	@Transient
	public ArrayList<ModelRiskConsequence> probConsequenceNodes;
	@Transient
	public ArrayList<ModelRiskControl> controls;
	public Boolean useCalculatedProb;
	

	public IModelRisk() {
	}
	public final Date getDateEntered() {
		return dateEntered;
	}

	public final void setDateEntered(final Date dateEntered) {
		this.dateEntered = dateEntered;
	}

	public final Date getDateUpdated() {
		return dateUpdated;
	}

	public final void setDateUpdated(final Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}
	public final ArrayList<Long> getChildSummaryRisks() {
		if (childSummaryRisks == null) {
			childSummaryRisks = new ArrayList<Long>();
		}
		return childSummaryRisks;
	}
	public final void setChildSummaryRisks(
			final ArrayList<Long> childSummaryRisks) {
		this.childSummaryRisks = childSummaryRisks;
	}
	public final ArrayList<ModelRiskControl> getControls() {
		if (controls == null) {
			controls = new ArrayList<ModelRiskControl>();
		}
		return controls;
	}
	public final void setControls(final ArrayList<ModelRiskControl> controls) {
		this.controls = controls;
	}
	public final Double getCurrentImpact() {
		return (treated) ? treatedImpact : inherentImpact;
	}
	public final void setCurrentImpact(final double currentImpact) {
		this.currentImpact = currentImpact;
	}
	public final Double getCurrentProb() {
		return (treated) ? treatedProb : inherentProb;
	}
	public final void setCurrentProb(final double currentProb) {
		this.currentProb = currentProb;
	}
	public final Integer getCurrentTolerance() {
		return (treated) ? treatedTolerance : inherentTolerance;
	}
	public final void setCurrentTolerance(final Integer currentTolerance) {
		this.currentTolerance = currentTolerance;
	}
	public final int getEstMitCost() {
		return estMitCost;
	}
	public final void setEstMitCost(final int estMitCost) {
		this.estMitCost = estMitCost;
	}
	public final boolean isImpCost() {
		return impCost;
	}
	public final void setImpCost(final boolean impCost) {
		this.impCost = impCost;
	}
	public final boolean isImpEnvironment() {
		return impEnvironment;
	}
	public final void setImpEnvironment(final boolean impEnvironment) {
		this.impEnvironment = impEnvironment;
	}
	public final boolean isImpReputation() {
		return impReputation;
	}
	public final void setImpReputation(final boolean impReputation) {
		this.impReputation = impReputation;
	}
	public final boolean isImpSafety() {
		return impSafety;
	}
	public final void setImpSafety(final boolean impSafety) {
		this.impSafety = impSafety;
	}
	public final boolean isImpSpec() {
		return impSpec;
	}
	public final void setImpSpec(final boolean impSpec) {
		this.impSpec = impSpec;
	}
	public final boolean isImpTime() {
		return impTime;
	}
	public final void setImpTime(final boolean impTime) {
		this.impTime = impTime;
	}
	public final void setInherentImpact(final double inherentImpact) {
		this.inherentImpact = inherentImpact;
	}
	public final void setInherentProb(final double inherentProb) {
		this.inherentProb = inherentProb;
	}
	public final Long getInternalID() {
		return riskID;
	}
	public final void setInternalID(final Long internalID) {
		this.riskID = internalID;
	}
	public final Double getLikeAlpha() {
		return likealpha;
	}
	public final void setLikeAlpha(final Double likeAlpha) {
		this.likealpha = likeAlpha;
	}
	public final Double getLikePostAlpha() {
		return likepostAlpha;
	}
	public final void setLikePostAlpha(final Double likePostAlpha) {
		this.likepostAlpha = likePostAlpha;
	}
	public final Double getLikePostProb() {
		return likepostProb;
	}
	public final void setLikePostProb(final Double likePostProb) {
		this.likepostProb = likePostProb;
	}
	public final Double getLikePostT() {
		return likepostT;
	}
	public final void setLikePostT(final Double likePostT) {
		this.likepostT = likePostT;
	}
	public final Integer getLikePostType() {
		return likepostType;
	}
	public final void setLikePostType(final Integer likePostType) {
		this.likepostType = likePostType;
	}
	public final Double getLikeProb() {
		return likeprob;
	}
	public final void setLikeProb(final Double likeProb) {
		this.likeprob = likeProb;
	}
	public final Double getLikeT() {
		return liket;
	}
	public final void setLikeT(final Double likeT) {
		this.liket = likeT;
	}
	public final Integer getLikeType() {
		return liketype;
	}
	public final void setLikeType(final Integer likeType) {
		this.liketype = likeType;
	}
	public final Long getManager2ID() {
		return manager2ID;
	}
	public final void setManager2ID(final Long manager2ID) {
		this.manager2ID = manager2ID;
	}
	public final String getManager2Name() {
		return manager2Name;
	}
	public final void setManager2Name(final String manager2Name) {
		this.manager2Name = manager2Name;
	}
	public final Long getManager3ID() {
		return manager3ID;
	}
	public final void setManager3ID(final Long manager3ID) {
		this.manager3ID = manager3ID;
	}
	public final String getManager3Name() {
		return manager3Name;
	}
	public final void setManager3Name(final String manager3Name) {
		this.manager3Name = manager3Name;
	}
	public final ModelToleranceMatrix getMatrix() {
		return matrix;
	}
	public final void setMatrix(final ModelToleranceMatrix matrix) {
		this.matrix = matrix;
	}
	public final void setMatrixID(final long matrixID) {
		this.matrixID = matrixID;
	}
	public final int getMean() {
		return mean;
	}
	public final void setMean(final int mean) {
		this.mean = mean;
	}
	public final ArrayList<ModelMitigationStep> getMitigationPlan() {
		if (mitigationPlan == null) {
			mitigationPlan = new ArrayList<ModelMitigationStep>();
		}
		return mitigationPlan;
	}
	public ArrayList<ModelUpdateComment> getMitigationPlanUpdates() {
		return mitigationPlanUpdates;
	}
	public void setMitigationPlanUpdates(
			ArrayList<ModelUpdateComment> mitigationPlanUpdates) {
		this.mitigationPlanUpdates = mitigationPlanUpdates;
	}
	public final void setMitigationPlan(
			final ArrayList<ModelMitigationStep> mitigationPlan) {
		this.mitigationPlan = mitigationPlan;
	}
	public final ArrayList<Long> getObjectivesImpacted() {
		if (objectivesImpacted == null) {
			objectivesImpacted = new ArrayList<Long>();
		}
		return objectivesImpacted;
	}
	public final void setObjectivesImpacted(
			final ArrayList<Long> objectivesImpacted) {
		this.objectivesImpacted = objectivesImpacted;
	}
//	public final void setParentSummaryRisk(final long parentSummaryRisk) {
//		this.parentSummaryRisk = parentSummaryRisk;
//	}
	public final void setPrimCatID(final long primCatID) {
		this.primCatID = primCatID;
	}
	public final ArrayList<ModelRiskConsequence> getProbConsequenceNodes() {
		if (probConsequenceNodes == null) {
			probConsequenceNodes = new ArrayList<ModelRiskConsequence>();
		}
		return probConsequenceNodes;
	}
	public final void setProbConsequenceNodes(
			final ArrayList<ModelRiskConsequence> probConsequenceNodes) {
		this.probConsequenceNodes = (probConsequenceNodes);
	}
	public final void setProjectID(final long projectID) {
		this.projectID = projectID;
	}
	public final String getRiskID() {
		return riskProjectCode;
	}
	public final void setRiskID(final String riskID) {
		this.riskProjectCode = riskID;
	}
	public final void setSecCatID(final long secCatID) {
		this.secCatID = secCatID;
	}
	public final boolean isTeatmentAvoidance() {
		return treatmentAvoidance;
	}
	public final void setTeatmentAvoidance(final boolean teatmentAvoidance) {
		this.treatmentAvoidance = teatmentAvoidance;
	}
	public final boolean isTeatmentReduction() {
		return treatmentReduction;
	}
	public final void setTeatmentReduction(final boolean teatmentReduction) {
		this.treatmentReduction = teatmentReduction;
	}
	public final boolean isTeatmentRetention() {
		return treatmentRetention;
	}
	public final void setTeatmentRetention(final boolean teatmentRetention) {
		this.treatmentRetention = teatmentRetention;
	}
	public final boolean isTeatmentTransfer() {
		return treatmentTransfer;
	}
	public final void setTeatmentTransfer(final boolean teatmentTransfer) {
		this.treatmentTransfer = teatmentTransfer;
	}
	public final void setTreatedImpact(final double treatedImpact) {
		this.treatedImpact = treatedImpact;
	}
	public final void setTreatedProb(final double treatedProb) {
		this.treatedProb = treatedProb;
	}
	public final Date getBeginExposure() {
		return startExposure;
	}
	public final void setBeginExposure(final Date beginExposure) {
		this.startExposure = beginExposure;
	}
}
