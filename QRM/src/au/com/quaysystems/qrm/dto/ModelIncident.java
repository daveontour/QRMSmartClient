/*
 * 
 */
package au.com.quaysystems.qrm.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


// TODO: Auto-generated Javadoc
/**
 * The Class ModelEventIncident.
 */
@Entity
@Table(name = "incident")
public class ModelIncident  implements Serializable  {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3020244444553492083L;
	
	@Column(updatable=false, insertable=false)
	public String  incidentProjectCode;
	public Boolean bActive;
	public Boolean bCauses;
	public Boolean bControl;
	public Boolean bIdentified;
	public Boolean bMitigated;
	public Boolean bRated;
	public Boolean bReviews;
	public String controls;
	public Date dateEntered;
	public Date dateIncident;
	public Date dateStakeHoldersNotified;
	public Date dateUpdated;
	public String description;
	public Boolean impCost;
	public Boolean impEnviron;
	public Boolean impReputation;
	public Boolean impSafety;
	public Boolean impSpec;
	public Boolean impTime;
	public Boolean bIssue;
	@Transient
	public ArrayList<ModelIncidentConsequence> impacts;
	@Transient
	public ArrayList<ModelIncidentUpdate> updates;
	public Long projectID;
	public Long promotedProjectID;
	public int severity;
	/** The incident id. */
	@Id
    @GeneratedValue
	public Long incidentID;
	
	/** The lessons learnt. */
	public String lessonsLearnt;
	
	/** The notify stake holders entered. */
	public Boolean notifyStakeHoldersEntered;
	
	/** The notify stake holders update. */
	public Boolean notifyStakeHoldersUpdate;
	
	/** The objectives impacted. */
	@Transient
	public ArrayList<Long> objectivesImpacted;
	
	/** The context id. */
	public Long contextID;
	
	/** The reported by id. */
	public Long reportedByID;
	
	/** The reported by str. */
	public String reportedByStr;
	
	/** The risks impacted. */
	@Transient
	public ArrayList<Long> risksImpacted;
	
	/** The stake holder notified. */
	public Boolean stakeHolderNotified;
	
	/** The title. */
	public String title;
	
	/**
	 * Instantiates a new model event incident.
	 */
	public  ModelIncident(){}

	/**
	 * Checks if is b causes.
	 * 
	 * @return the bCauses
	 */
	public final boolean isBCauses() {
		return bCauses;
	}
	
	/**
	 * Sets the b causes.
	 * 
	 * @param causes the bCauses to set
	 */
	public final void setBCauses(final boolean causes) {
		bCauses = causes;
	}
	
	/**
	 * Checks if is b control.
	 * 
	 * @return the bControl
	 */
	public final boolean isBControl() {
		return bControl;
	}
	
	/**
	 * Sets the b control.
	 * 
	 * @param control the bControl to set
	 */
	public final void setBControl(final boolean control) {
		bControl = control;
	}
	
	/**
	 * Checks if is b identified.
	 * 
	 * @return the bIdentified
	 */
	public final boolean isBIdentified() {
		return bIdentified;
	}
	
	/**
	 * Sets the b identified.
	 * 
	 * @param identified the bIdentified to set
	 */
	public final void setBIdentified(final boolean identified) {
		bIdentified = identified;
	}
	
	/**
	 * Checks if is b mitigated.
	 * 
	 * @return the bMitigated
	 */
	public final boolean isBMitigated() {
		return bMitigated;
	}
	
	/**
	 * Sets the b mitigated.
	 * 
	 * @param mitigated the bMitigated to set
	 */
	public final void setBMitigated(final boolean mitigated) {
		bMitigated = mitigated;
	}
	
	/**
	 * Checks if is b rated.
	 * 
	 * @return the bRated
	 */
	public final boolean isBRated() {
		return bRated;
	}
	
	/**
	 * Sets the b rated.
	 * 
	 * @param rated the bRated to set
	 */
	public final void setBRated(final boolean rated) {
		bRated = rated;
	}
	
	/**
	 * Checks if is b reviews.
	 * 
	 * @return the bReviews
	 */
	public final boolean isBReviews() {
		return bReviews;
	}
	
	/**
	 * Sets the b reviews.
	 * 
	 * @param reviews the bReviews to set
	 */
	public final void setBReviews(final boolean reviews) {
		bReviews = reviews;
	}
	
	/**
	 * Gets the controls.
	 * 
	 * @return the controls
	 */
	public final String getControls() {
		return controls;
	}
	
	/**
	 * Sets the controls.
	 * 
	 * @param controls the controls to set
	 */
	public final void setControls(final String controls) {
		this.controls = controls;
	}
	
	/**
	 * Gets the date entered.
	 * 
	 * @return the dateEntered
	 */
	public final Date getDateEntered() {
		return dateEntered;
	}
	
	/**
	 * Sets the date entered.
	 * 
	 * @param dateEntered the dateEntered to set
	 */
	public final void setDateEntered(final Date dateEntered) {
		this.dateEntered = dateEntered;
	}
	
	/**
	 * Gets the date incident.
	 * 
	 * @return the dateIncident
	 */
	public final Date getDateIncident() {
		return dateIncident;
	}
	
	/**
	 * Sets the date incident.
	 * 
	 * @param dateIncident the dateIncident to set
	 */
	public final void setDateIncident(final Date dateIncident) {
		this.dateIncident = dateIncident;
	}
	
	/**
	 * Gets the date stake holders notified.
	 * 
	 * @return the dateStakeHoldersNotified
	 */
	public final Date getDateStakeHoldersNotified() {
		return dateStakeHoldersNotified;
	}
	
	/**
	 * Sets the date stake holders notified.
	 * 
	 * @param dateStakeHoldersNotified the dateStakeHoldersNotified to set
	 */
	public final void setDateStakeHoldersNotified(final Date dateStakeHoldersNotified) {
		this.dateStakeHoldersNotified = dateStakeHoldersNotified;
	}
	
	/**
	 * Gets the date updated.
	 * 
	 * @return the dateUpdated
	 */
	public final Date getDateUpdated() {
		return dateUpdated;
	}
	
	/**
	 * Sets the date updated.
	 * 
	 * @param dateUpdated the dateUpdated to set
	 */
	public final void setDateUpdated(final Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public final String getDescription() {
		return description;
	}
	
	/**
	 * Sets the description.
	 * 
	 * @param description the description to set
	 */
	public final void setDescription(final String description) {
		this.description = description;
	}
	
	/**
	 * Gets the impacts.
	 * 
	 * @return the impacts
	 */
	public final ArrayList<ModelIncidentConsequence> getImpacts() {
		if (impacts == null) {
			impacts = new ArrayList<ModelIncidentConsequence>();
		}
		return impacts;
	}
	
	/**
	 * Sets the impacts.
	 * 
	 * @param impacts the impacts to set
	 */
	public final void setImpacts(final ArrayList<ModelIncidentConsequence> impacts) {
		this.impacts = impacts;
	}
	/**
	 * Gets the impacts.
	 * 
	 * @return the impacts
	 */
	public final ArrayList<ModelIncidentUpdate> getUpdates() {
		if (updates == null) {
			updates = new ArrayList<ModelIncidentUpdate>();
		}
		return updates;
	}
	
	/**
	 * Sets the impacts.
	 * 
	 * @param impacts the impacts to set
	 */
	public final void setUpdates(final ArrayList<ModelIncidentUpdate> updates) {
		this.updates = updates;
	}	
	/**
	 * Checks if is imp cost.
	 * 
	 * @return the impCost
	 */
	public final boolean isImpCost() {
		return impCost;
	}
	
	/**
	 * Sets the imp cost.
	 * 
	 * @param impCost the impCost to set
	 */
	public final void setImpCost(final boolean impCost) {
		this.impCost = impCost;
	}
	
	/**
	 * Checks if is imp environ.
	 * 
	 * @return the impEnviron
	 */
	public final boolean isImpEnviron() {
		return impEnviron;
	}
	
	/**
	 * Sets the imp environ.
	 * 
	 * @param impEnviron the impEnviron to set
	 */
	public final void setImpEnviron(final boolean impEnviron) {
		this.impEnviron = impEnviron;
	}
	
	/**
	 * Checks if is imp reputation.
	 * 
	 * @return the impReputation
	 */
	public final boolean isImpReputation() {
		return impReputation;
	}
	
	/**
	 * Sets the imp reputation.
	 * 
	 * @param impReputation the impReputation to set
	 */
	public final void setImpReputation(final boolean impReputation) {
		this.impReputation = impReputation;
	}
	
	/**
	 * Checks if is imp safety.
	 * 
	 * @return the impSafety
	 */
	public final boolean isImpSafety() {
		return impSafety;
	}
	
	/**
	 * Sets the imp safety.
	 * 
	 * @param impSafety the impSafety to set
	 */
	public final void setImpSafety(final boolean impSafety) {
		this.impSafety = impSafety;
	}
	
	/**
	 * Checks if is imp spec.
	 * 
	 * @return the impSpec
	 */
	public final boolean isImpSpec() {
		return impSpec;
	}
	
	/**
	 * Sets the imp spec.
	 * 
	 * @param impSpec the impSpec to set
	 */
	public final void setImpSpec(final boolean impSpec) {
		this.impSpec = impSpec;
	}
	
	/**
	 * Checks if is imp time.
	 * 
	 * @return the impTime
	 */
	public final boolean isImpTime() {
		return impTime;
	}
	
	/**
	 * Sets the imp time.
	 * 
	 * @param impTime the impTime to set
	 */
	public final void setImpTime(final boolean impTime) {
		this.impTime = impTime;
	}
	
	/**
	 * Gets the incident id.
	 * 
	 * @return the internalID
	 */
	public final Long getIncidentID() {
		return incidentID;
	}
	
	/**
	 * Sets the incident id.
	 * 
	 * @param internalID the internalID to set
	 */
	public final void setIncidentID(final Long internalID) {
		this.incidentID = internalID;
	}
	
	/**
	 * Gets the lessons learnt.
	 * 
	 * @return the lessonsLearnt
	 */
	public final String getLessonsLearnt() {
		return lessonsLearnt;
	}
	
	/**
	 * Sets the lessons learnt.
	 * 
	 * @param lessonsLearnt the lessonsLearnt to set
	 */
	public final void setLessonsLearnt(final String lessonsLearnt) {
		this.lessonsLearnt = lessonsLearnt;
	}
	
	/**
	 * Checks if is notify stake holders entered.
	 * 
	 * @return the notifyStakeHoldersEntered
	 */
	public final boolean isNotifyStakeHoldersEntered() {
		return notifyStakeHoldersEntered;
	}
	
	/**
	 * Sets the notify stake holders entered.
	 * 
	 * @param notifyStakeHoldersEntered the notifyStakeHoldersEntered to set
	 */
	public final void setNotifyStakeHoldersEntered(final boolean notifyStakeHoldersEntered) {
		this.notifyStakeHoldersEntered = notifyStakeHoldersEntered;
	}
	
	/**
	 * Checks if is notify stake holders update.
	 * 
	 * @return the notifyStakeHoldersUpdate
	 */
	public final boolean isNotifyStakeHoldersUpdate() {
		return notifyStakeHoldersUpdate;
	}
	
	/**
	 * Sets the notify stake holders update.
	 * 
	 * @param notifyStakeHoldersUpdate the notifyStakeHoldersUpdate to set
	 */
	public final void setNotifyStakeHoldersUpdate(final boolean notifyStakeHoldersUpdate) {
		this.notifyStakeHoldersUpdate = notifyStakeHoldersUpdate;
	}
	
	/**
	 * Gets the objectives impacted.
	 * 
	 * @return the objectivesImpacted
	 */
	public final ArrayList<Long> getObjectivesImpacted() {
		if (objectivesImpacted == null) {
			objectivesImpacted = new ArrayList<Long>();
		}

		return objectivesImpacted;
	}
	
	/**
	 * Sets the objectives impacted.
	 * 
	 * @param objectivesImpacted the objectivesImpacted to set
	 */
	public final void setObjectivesImpacted(final ArrayList<Long> objectivesImpacted) {
		this.objectivesImpacted = objectivesImpacted;
	}
	
	/**
	 * Gets the project id.
	 * 
	 * @return the projectID
	 */
	public final long getProjectID() {
		return contextID;
	}
	
	/**
	 * Sets the project id.
	 * 
	 * @param projectID the projectID to set
	 */
	public final void setProjectID(final long projectID) {
		this.contextID = projectID;
	}
	
	/**
	 * Gets the reported by id.
	 * 
	 * @return the reportedByID
	 */
	public final Long getReportedByID() {
		return reportedByID;
	}
	
	/**
	 * Sets the reported by id.
	 * 
	 * @param reportedByID the reportedByID to set
	 */
	public final void setReportedByID(final Long reportedByID) {
		this.reportedByID = reportedByID;
	}
	
	/**
	 * Gets the reported by str.
	 * 
	 * @return the reportedByStr
	 */
	public final String getReportedByStr() {
		return reportedByStr;
	}
	
	/**
	 * Sets the reported by str.
	 * 
	 * @param reportedByStr the reportedByStr to set
	 */
	public final void setReportedByStr(final String reportedByStr) {
		this.reportedByStr = reportedByStr;
	}
	
	/**
	 * Gets the risks impacted.
	 * 
	 * @return the risksImpacted
	 */
	public final ArrayList<Long> getRisksImpacted() {
		if (risksImpacted == null) {
			risksImpacted  = new ArrayList<Long>();
		}
		return risksImpacted;
	}
	
	/**
	 * Sets the risks impacted.
	 * 
	 * @param risksImpacted the risksImpacted to set
	 */
	public final void setRisksImpacted(final ArrayList<Long> risksImpacted) {
		this.risksImpacted = (risksImpacted);
	}
	
	/**
	 * Checks if is stake holder notified.
	 * 
	 * @return the stakeHolderNotified
	 */
	public final boolean isStakeHolderNotified() {
		return stakeHolderNotified;
	}
	
	/**
	 * Sets the stake holder notified.
	 * 
	 * @param stakeHolderNotified the stakeHolderNotified to set
	 */
	public final void setStakeHolderNotified(final boolean stakeHolderNotified) {
		this.stakeHolderNotified = stakeHolderNotified;
	}
	
	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	public final String getTitle() {
		return title;
	}
	
	/**
	 * Sets the title.
	 * 
	 * @param title the title to set
	 */
	public final void setTitle(final String title) {
		this.title = title;
	}

	public Boolean getbIssue() {
		return bIssue;
	}

	public void setbIssue(Boolean bIssue) {
		this.bIssue = bIssue;
	}  
}