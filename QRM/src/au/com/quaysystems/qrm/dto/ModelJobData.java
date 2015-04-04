/*
 * 
 */
package au.com.quaysystems.qrm.dto;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Class ModelJobData.
 */
@Entity
@Table(name="jobdata")
public class ModelJobData  {

	/** The id. */
	@Id
	long id;

	/** The job id. */
	long jobID;

	/** The risk id. */
	long riskID;

	/**
	 * Instantiates a new model monte carlo data.
	 */
	public ModelJobData(){}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public final long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the new id
	 */
	public final void setId(final long id) {
		this.id = id;
	}

	/**
	 * Gets the job id.
	 * 
	 * @return the job id
	 */
	public final long getJobID() {
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
	 * Gets the risk id.
	 * 
	 * @return the risk id
	 */
	public final long getRiskID() {
		return riskID;
	}

	/**
	 * Sets the risk id.
	 * 
	 * @param riskID the new risk id
	 */
	public final void setRiskID(final long riskID) {
		this.riskID = riskID;
	}
}
