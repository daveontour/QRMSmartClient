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
import javax.persistence.Transient;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

@NamedNativeQueries( {
	@NamedNativeQuery(name = "getAllCompletedJobs", query = "SELECT * FROM jobqueue WHERE executedDate < NOW() ORDER BY queuedDate DESC", resultClass = ModelJobQueueMgr.class)
})
@Entity
@Table(name="jobqueue")
public class ModelJobQueueMgr implements Serializable  {

	public boolean isEmailSent() {
		return emailSent;
	}

	public void setEmailSent(boolean emailSent) {
		this.emailSent = emailSent;
	}

	public Date getDateemailSent() {
		return dateEmailSent;
	}

	public void setDateemailSent(Date dateemailSent) {
		this.dateEmailSent = dateemailSent;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5523742145639642798L;
	/** The job id. */
	@Id
	@GeneratedValue
	long jobID;
	boolean readyToExecute;
	boolean readyToCollect;
	boolean processing;
	boolean collected;
	boolean failed;
	String state;
	long userID;
	long rootProjectID;
	long projectID;
	Date queuedDate;
	Date executedDate;
	Date collectedDate;
	String jobJdbcURL;
	String jobDescription;
	String jobType;
	String reportFormat;
	
	@Transient
	boolean emailSent;
	@Transient
	Date dateEmailSent;



	/**
	 * Instantiates a new model monte carlo queue.
	 */
	public ModelJobQueueMgr(){}

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
	 * Checks if is ready to execute.
	 * 
	 * @return true, if is ready to execute
	 */
	public final boolean isReadyToExecute() {
		return readyToExecute;
	}

	/**
	 * Sets the ready to execute.
	 * 
	 * @param readyToExecute the new ready to execute
	 */
	public final void setReadyToExecute(final boolean readyToExecute) {
		this.readyToExecute = readyToExecute;
	}

	/**
	 * Checks if is ready to collect.
	 * 
	 * @return true, if is ready to collect
	 */
	public final boolean isReadyToCollect() {
		return readyToCollect;
	}

	/**
	 * Sets the ready to collect.
	 * 
	 * @param readyToCollect the new ready to collect
	 */
	public final void setReadyToCollect(final boolean readyToCollect) {
		this.readyToCollect = readyToCollect;
	}

	/**
	 * Checks if is collected.
	 * 
	 * @return true, if is collected
	 */
	public final boolean isCollected() {
		return collected;
	}

	/**
	 * Sets the collected.
	 * 
	 * @param collected the new collected
	 */
	public final void setCollected(final boolean collected) {
		this.collected = collected;
	}

	/**
	 * Gets the state.
	 * 
	 * @return the state
	 */
	public final String getState() {
		return state;
	}

	/**
	 * Sets the state.
	 * 
	 * @param state the new state
	 */
	public final void setState(final String state) {
		this.state = state;
	}

	/**
	 * Gets the user id.
	 * 
	 * @return the user id
	 */
	public final long getUserID() {
		return userID;
	}

	/**
	 * Sets the user id.
	 * 
	 * @param userID the new user id
	 */
	public final void setUserID(final long userID) {
		this.userID = userID;
	}

	/**
	 * Gets the project id.
	 * 
	 * @return the project id
	 */
	public final long getProjectID() {
		return projectID;
	}

	/**
	 * Sets the project id.
	 * 
	 * @param projectID the new project id
	 */
	public final void setProjectID(final long projectID) {
		this.projectID = projectID;
	}

	/**
	 * Gets the queued date.
	 * 
	 * @return the queued date
	 */
	public final Date getQueuedDate() {
		return queuedDate;
	}

	/**
	 * Sets the queued date.
	 * 
	 * @param queuedDate the new queued date
	 */
	public final void setQueuedDate(final Date queuedDate) {
		this.queuedDate = queuedDate;
	}

	/**
	 * Gets the executed date.
	 * 
	 * @return the executed date
	 */
	public final Date getExecutedDate() {
		return executedDate;
	}

	/**
	 * Sets the executed date.
	 * 
	 * @param executedDate the new executed date
	 */
	public final void setExecutedDate(final Date executedDate) {
		this.executedDate = executedDate;
	}

	/**
	 * Gets the collected date.
	 * 
	 * @return the collected date
	 */
	public final Date getCollectedDate() {
		return collectedDate;
	}

	/**
	 * Sets the collected date.
	 * 
	 * @param collectedDate the new collected date
	 */
	public final void setCollectedDate(final Date collectedDate) {
		this.collectedDate = collectedDate;
	}


	/**
	 * Checks if is processing.
	 * 
	 * @return true, if is processing
	 */
	public final boolean isProcessing() {
		return processing;
	}

	/**
	 * Sets the processing.
	 * 
	 * @param processing the new processing
	 */
	public final void setProcessing(final boolean processing) {
		this.processing = processing;
	}

	public String getJobJdbcURL() {
		return jobJdbcURL;
	}

	public void setJobJdbcURL(final String jobJdbcURL) {
		this.jobJdbcURL = jobJdbcURL;
	}

	public String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(final String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(final String jobType) {
		this.jobType = jobType;
	}

	/**
	 * @return the reportFormat
	 */
	public String getReportFormat() {
		return reportFormat;
	}

	/**
	 * @param reportFormat the reportFormat to set
	 */
	public void setReportFormat(String reportFormat) {
		this.reportFormat = reportFormat;
	}

	/**
	 * @return the failed
	 */
	public boolean isFailed() {
		return failed;
	}

	/**
	 * @param failed the failed to set
	 */
	public void setFailed(boolean failed) {
		this.failed = failed;
	}

	public long getRootProjectID() {
		return rootProjectID;
	}

	public void setRootProjectID(long rootProjectID) {
		this.rootProjectID = rootProjectID;
	}
}
