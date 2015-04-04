/*
 * 
 */
package au.com.quaysystems.qrm.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Class ModelJobQueue.
 */
@Entity
@Table(name="jobmontedata")
public class ModelJobMonteData  implements Serializable  {

	private static final long serialVersionUID = -2798130549262488812L;

	/** The job id. */
	@Id
	@GeneratedValue
	long id;
	long jobID;
	long projectID;
	long monthlyAnalysis;
	Date startDate;
	Date endDate;
	int numIterations;
	boolean forceConsequencesActive;
	boolean forceRiskActive;
	int simType;
	boolean processed = false;


	/**
	 * Instantiates a new model monte carlo queue.
	 */
	public ModelJobMonteData(){}

	/**
	 * Gets the job id.
	 * 
	 * @return the job id
	 */
	public final Long getJobID() {
		return jobID;
	}

	/**
	 * Sets the job id.
	 * 
	 * @param jobID the new job id
	 */
	public final void setJobID(final long jobID) {
		this.jobID = jobID;
	}


	/**
	 * Gets the start date.
	 * 
	 * @return the start date
	 */
	public final Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the start date.
	 * 
	 * @param startDate the new start date
	 */
	public final void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the end date.
	 * 
	 * @return the end date
	 */
	public final Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end date.
	 * 
	 * @param endDate the new end date
	 */
	public final void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Gets the num iterations.
	 * 
	 * @return the num iterations
	 */
	public final int getNumIterations() {
		return numIterations;
	}

	/**
	 * Sets the num iterations.
	 * 
	 * @param numIterations the new num iterations
	 */
	public final void setNumIterations(final int numIterations) {
		this.numIterations = numIterations;
	}

	/**
	 * Checks if is force consequences active.
	 * 
	 * @return true, if is force consequences active
	 */
	public final boolean isForceConsequencesActive() {
		return forceConsequencesActive;
	}

	/**
	 * Sets the force consequences active.
	 * 
	 * @param forceConsequencesActive the new force consequences active
	 */
	public final void setForceConsequencesActive(final boolean forceConsequencesActive) {
		this.forceConsequencesActive = forceConsequencesActive;
	}

	/**
	 * Checks if is force risk active.
	 * 
	 * @return true, if is force risk active
	 */
	public final boolean isForceRiskActive() {
		return forceRiskActive;
	}

	/**
	 * Sets the force risk active.
	 * 
	 * @param forceRiskActive the new force risk active
	 */
	public final void setForceRiskActive(final boolean forceRiskActive) {
		this.forceRiskActive = forceRiskActive;
	}

	/**
	 * Sets the monthly analysis.
	 * 
	 * @param monthlyAnalysis the new monthly analysis
	 */
	public final void setMonthlyAnalysis(final long monthlyAnalysis) {
		this.monthlyAnalysis = monthlyAnalysis;
	}

	/**
	 * Gets the sim type.
	 * 
	 * @return the sim type
	 */
	public final int getSimType() {
		return simType;
	}

	/**
	 * Sets the sim type.
	 * 
	 * @param simType the new sim type
	 */
	public final void setSimType(final int simType) {
		this.simType = simType;
	}

	/**
	 * Gets the monthly analysis.
	 * 
	 * @return the monthly analysis
	 */
	public final long getMonthlyAnalysis() {
		return monthlyAnalysis;
	}

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public long getProjectID() {
		return projectID;
	}

	public void setProjectID(final long projectID) {
		this.projectID = projectID;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

}
