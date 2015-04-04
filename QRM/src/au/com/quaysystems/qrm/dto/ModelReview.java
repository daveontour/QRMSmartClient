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

// TODO: Auto-generated Javadoc
/**
 * The Class ModelReview.
 */
@Entity
@Table(name = "review")
public class ModelReview implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6026125603486048504L;

	/** The actual date. */
	public Date actualDate;

	/** The projectID. */
	public long projectID;

	/** The generation. */
	@Transient
	public int generation;

	/** The reviewID. */
	@Id
	@GeneratedValue
	public long reviewID;

	/** The mail update complete. */
	public Boolean mailUpdateComplete = false;

	/** The mail update date. */
	public Date mailUpdateDate;

	/** The review comments. */
	public String reviewComments;

	/** The project title. */
	@Transient
	public String projectTitle;

	/** The scheduled date. */
	public Date scheduledDate;

	/** The title. */
	public String title;

	/** The review complete. */
	public boolean reviewComplete = false;

	/**
	 * Instantiates a new model review.
	 */
	public ModelReview() {
	}

	/**
	 * Gets the actual date.
	 * 
	 * @return the actualDate
	 */
	public final Date getActualDate() {
		return actualDate;
	}

	/**
	 * Sets the actual date.
	 * 
	 * @param actualDate
	 *            the actualDate to set
	 */
	public final void setActualDate(final Date actualDate) {
		this.actualDate = actualDate;
	}

	/**
	 * Gets the context id.
	 * 
	 * @return the contextID
	 */
	public final long getContextID() {
		return projectID;
	}

	/**
	 * Sets the context id.
	 * 
	 * @param contextID
	 *            the contextID to set
	 */
	public final void setContextID(final long contextID) {
		this.projectID = contextID;
	}

	/**
	 * Gets the generation.
	 * 
	 * @return the generation
	 */
	public final int getGeneration() {
		return generation;
	}

	/**
	 * Sets the generation.
	 * 
	 * @param generation
	 *            the generation to set
	 */
	public final void setGeneration(final int generation) {
		this.generation = generation;
	}

	/**
	 * Gets the internal id.
	 * 
	 * @return the internalID
	 */
	public final long getInternalID() {
		return reviewID;
	}

	/**
	 * Sets the internal id.
	 * 
	 * @param internalID
	 *            the internalID to set
	 */
	public final void setInternalID(final long internalID) {
		this.reviewID = internalID;
	}

	/**
	 * Checks if is mail update.
	 * 
	 * @return the mailUpdate
	 */
	public final boolean isMailUpdate() {
		return mailUpdateComplete;
	}

	/**
	 * Sets the mail update.
	 * 
	 * @param mailUpdate
	 *            the mailUpdate to set
	 */
	public final void setMailUpdate(final boolean mailUpdate) {
		this.mailUpdateComplete = mailUpdate;
	}

	/**
	 * Gets the mail update date.
	 * 
	 * @return the mailUpdateDate
	 */
	public final Date getMailUpdateDate() {
		return mailUpdateDate;
	}

	/**
	 * Sets the mail update date.
	 * 
	 * @param mailUpdateDate
	 *            the mailUpdateDate to set
	 */
	public final void setMailUpdateDate(final Date mailUpdateDate) {
		this.mailUpdateDate = mailUpdateDate;
	}

	/**
	 * Gets the review comments.
	 * 
	 * @return the reviewComments
	 */
	public final String getReviewComments() {
		return reviewComments;
	}

	/**
	 * Sets the review comments.
	 * 
	 * @param reviewComments
	 *            the reviewComments to set
	 */
	public final void setReviewComments(final String reviewComments) {
		this.reviewComments = reviewComments;
	}

	/**
	 * Gets the risk context.
	 * 
	 * @return the riskContext
	 */
	public final String getRiskContext() {
		return projectTitle;
	}

	/**
	 * Sets the risk context.
	 * 
	 * @param riskContext
	 *            the riskContext to set
	 */
	public final void setRiskContext(final String riskContext) {
		this.projectTitle = riskContext;
	}

	/**
	 * Gets the scheduled date.
	 * 
	 * @return the scheduledDate
	 */
	public final Date getScheduledDate() {
		return scheduledDate;
	}

	/**
	 * Sets the scheduled date.
	 * 
	 * @param scheduledDate
	 *            the scheduledDate to set
	 */
	public final void setScheduledDate(final Date scheduledDate) {
		this.scheduledDate = scheduledDate;
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
	 * @param title
	 *            the title to set
	 */
	public final void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * Gets the projectID.
	 * 
	 * @return the projectID
	 */
	public final long getProjectID() {
		return projectID;
	}

	/**
	 * Sets the projectID.
	 * 
	 * @param projectID
	 *            the new projectID
	 */
	public final void setProjectID(final long projectID) {
		this.projectID = projectID;
	}

	/**
	 * Gets the reviewID.
	 * 
	 * @return the reviewID
	 */
	public final long getReviewID() {
		return reviewID;
	}

	/**
	 * Sets the reviewID.
	 * 
	 * @param reviewID
	 *            the new reviewID
	 */
	public final void setReviewID(final long reviewID) {
		this.reviewID = reviewID;
	}

	/**
	 * Gets the mail update complete.
	 * 
	 * @return the mail update complete
	 */
	public final Boolean getMailUpdateComplete() {
		return mailUpdateComplete;
	}

	/**
	 * Sets the mail update complete.
	 * 
	 * @param mailUpdateComplete
	 *            the new mail update complete
	 */
	public final void setMailUpdateComplete(final Boolean mailUpdateComplete) {
		this.mailUpdateComplete = mailUpdateComplete;
	}

	/**
	 * Gets the project title.
	 * 
	 * @return the project title
	 */
	public final String getProjectTitle() {
		return projectTitle;
	}

	/**
	 * Sets the project title.
	 * 
	 * @param projectTitle
	 *            the new project title
	 */
	public final void setProjectTitle(final String projectTitle) {
		this.projectTitle = projectTitle;
	}

	/**
	 * Checks if is review complete.
	 * 
	 * @return true, if is review complete
	 */
	public final boolean isReviewComplete() {
		return reviewComplete;
	}

	/**
	 * Sets the review complete.
	 * 
	 * @param reviewComplete
	 *            the new review complete
	 */
	public final void setReviewComplete(final boolean reviewComplete) {
		this.reviewComplete = reviewComplete;
	}

}
