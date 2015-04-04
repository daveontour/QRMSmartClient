/*
 * 
 */

package au.com.quaysystems.qrm.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

@NamedNativeQueries( {
	@NamedNativeQuery(name = "getRiskMitigationsteps", query = "SELECT mitigationstep.*, S1.name AS personResponsible   FROM mitigationstep  LEFT OUTER JOIN stakeholders AS S1 ON mitigationstep.personID = S1.stakeholderID WHERE mitigationstep.riskID = :riskID ORDER BY dateUpdated ASC", resultClass = ModelMitigationStep.class)
})
@Entity
@Table(name="mitigationstep")
public class ModelMitigationStep  implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6901271069395038460L;
	
	/** The date entered. */
	public Date dateEntered;
	
	/** The date updated. */
	public Date dateUpdated;
	
	/** The end date. */
	public Date endDate;
	
	/** The end date long. */
	@Transient
	public long endDateLong;

	public Double estCost = 0.0;
	
	/** The mitstepID. */
	@Id
	@GeneratedValue
	public Long mitstepID;
	
	/** The percentComplete. */
	public Double percentComplete;
	
	/** The person responsible. */
	@Column(updatable=false, insertable=false)
	@Transient
	public String personResponsible;
	
	/** The personID. */
	public long personID;
	
	/** The projectID. */
	public long projectID;
	
	/** The riskID. */
	public long riskID;
	
	/** The start date. */
	public Date startDate;
	
	/** The description. */
	public String description;
	
	/** The response. */
	public boolean response = false;
	
	/** Indicates whether this is an update to an existing mitigation step */ 
	public boolean mitPlanUpdate = false;
	
	@Transient
	public String updates = "";
	
	/**
	 * Instantiates a new model mitigation step.
	 */
	public ModelMitigationStep(){}

	/**
	 * Gets the date entered.
	 * 
	 * @return the dateEntered
	 */
	public final Date getDateEntered() {
		return dateEntered;
	}
	
	public void addUpdate (ModelUpdateComment uc){
		if (uc.hostType.indexOf("MITIGATION") == -1 || uc.hostID.longValue() != this.mitstepID.longValue()) {
			return;
		}
		SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
		updates = updates.concat("<em>"+df.format(uc.dateUpdated)+"</em> &nbsp;&nbsp;");
		updates = updates.concat(uc.description.concat("<br/>"));
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
	 * Gets the end date.
	 * 
	 * @return the endDate
	 */
	public final Date getEndDate() {
		if (endDate == null && endDateLong != 0){
			endDate = new Date(endDateLong);
		}
		return endDate;
	}
	
	/**
	 * Sets the end date.
	 * 
	 * @param endDate the endDate to set
	 */
	public final void setEndDate(final Date endDate) {
		this.endDate = endDate;
		this.endDateLong = endDate.getTime();
	}
	
	/**
	 * Gets the estimated cost.
	 * 
	 * @return the estimatedCost
	 */
	public final Double getEstimatedCost() {
		return estCost;
	}
	
	/**
	 * Sets the estimated cost.
	 * 
	 * @param estimatedCost the estimatedCost to set
	 */
	public final void setEstimatedCost(final Double estimatedCost) {
		if (estimatedCost != null){
			this.estCost = estimatedCost;
		} else {
			this.estCost = 0.0;
		}
	}
	
	/**
	 * Gets the internal id.
	 * 
	 * @return the internalID
	 */
	public final Long getInternalID() {
		return mitstepID;
	}
	
	/**
	 * Sets the internal id.
	 * 
	 * @param internalID the internalID to set
	 */
	public final void setInternalID(final Long internalID) {
		this.mitstepID = internalID;
	}
	
	/**
	 * Gets the percent complete.
	 * 
	 * @return the percentComplete
	 */
	public final Double getPercentComplete() {
		return percentComplete;
	}
	
	/**
	 * Sets the percent complete.
	 * 
	 * @param percentComplete the percentComplete to set
	 */
	public final void setPercentComplete(final Double percentComplete) {
		this.percentComplete = percentComplete;
	}
	
	/**
	 * Gets the person responsible.
	 * 
	 * @return the personResponsible
	 */
	public final String getPersonResponsible() {
		return personResponsible;
	}
	
	/**
	 * Sets the person responsible.
	 * 
	 * @param personResponsible the personResponsible to set
	 */
	public final void setPersonResponsible(final String personResponsible) {
		this.personResponsible = personResponsible;
	}
	
	/**
	 * Gets the person responsible id.
	 * 
	 * @return the personResponsibleID
	 */
	public final Long getPersonResponsibleID() {
		return personID;
	}
	
	/**
	 * Sets the person responsible id.
	 * 
	 * @param personResponsibleID the personResponsibleID to set
	 */
	public final void setPersonResponsibleID(final Long personResponsibleID) {
		this.personID = personResponsibleID;
	}
	
	/**
	 * Gets the project id.
	 * 
	 * @return the projectID
	 */
	public final long getProjectID() {
		return projectID;
	}
	
	/**
	 * Sets the project id.
	 * 
	 * @param projectID the projectID to set
	 */
	public final void setProjectID(final long projectID) {
		this.projectID = projectID;
	}
	
	/**
	 * Gets the risk id.
	 * 
	 * @return the riskID
	 */
	public final long getRiskID() {
		return riskID;
	}
	
	/**
	 * Sets the risk id.
	 * 
	 * @param riskID the riskID to set
	 */
	public final void setRiskID(final long riskID) {
		this.riskID = riskID;
	}
	
	/**
	 * Gets the start date.
	 * 
	 * @return the startDate
	 */
	public final Date getStartDate() {
		return startDate;
	}
	
	/**
	 * Sets the start date.
	 * 
	 * @param startDate the startDate to set
	 */
	public final void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * Gets the step description.
	 * 
	 * @return the stepDescription
	 */
	public final String getStepDescription() {
		return description;
	}
	
	/**
	 * Sets the step description.
	 * 
	 * @param stepDescription the stepDescription to set
	 */
	public final void setStepDescription(final String stepDescription) {
		this.description = stepDescription;
	}

	/**
	 * Checks if is response.
	 * 
	 * @return true, if is response
	 */
	public final boolean isResponse() {
		return response;
	}

	/**
	 * Sets the response.
	 * 
	 * @param response the new response
	 */
	public final void setResponse(final boolean response) {
		this.response = response;
	}
}
