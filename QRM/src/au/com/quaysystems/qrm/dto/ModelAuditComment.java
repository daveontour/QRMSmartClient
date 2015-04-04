/*
 * 
 */

package au.com.quaysystems.qrm.dto;

import java.io.Serializable;
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
	@NamedNativeQuery(name = "getRiskComments", query = "select auditcomments.*, S1.name AS personName from auditcomments LEFT OUTER JOIN stakeholders AS S1 ON auditcomments.enteredByID = S1.stakeholderID where riskID = :riskID", resultClass = ModelAuditComment.class)
})
@Entity
@Table(name="auditcomments")
public class ModelAuditComment  implements Serializable  {

	/** The Constant serialVersionUID. */
	@Transient
	private static final long serialVersionUID = -743223196780266087L;
	
	/** The approval. */
	public boolean approval = false;
    
    /** The attachment. */
    public String attachment = "";
    
    /** The comment. */
    public String comment = "";
    
    /** The comment url. */
    public String commentURL = "";
    
    /** The date entered. */
    public Date dateEntered;
    
    /** The date updated. */
    @Transient
    public Date dateUpdated;
    
    /** The sched review date. */
    public Date schedReviewDate;
	
	/** The personName. */
	@Column(updatable=false, insertable=false)
    public String personName = "";
    
    /** The entered by id. */
    public Long enteredByID;
    
    /** The evaluation. */
    public boolean evaluation = false;
    
    /** The identification. */
    public boolean identification = false;
    
    /** The internal id. */
    @Id
    @GeneratedValue
    public Long internalID;
    
    /** The mitigation. */
    public boolean mitigation = false;
    
    /** The project id. */
    public Long projectID;
    
    /** The review. */
    public boolean review = false;
    
    /** The sched review. */
    public boolean schedReview = false;
    
    /** The sched review id. */
    public Long schedReviewID;
    
    /** The risk id. */
    public Long riskID;
    
    /** The type. */
    @Transient
    public String type;
    
    /** The attachment url. */
    @Transient
    public String attachmentURL;
    
    /** The url. */
    @Transient
    public String url;
    
    
    /**
     * Instantiates a new model audit comment.
     */
    public ModelAuditComment(){}
	
	/**
	 * Checks if is approval.
	 * 
	 * @return the approval
	 */
	public final boolean isApproval() {
		return approval;
	}
	
	/**
	 * Sets the approval.
	 * 
	 * @param approval the approval to set
	 */
	public final void setApproval(final boolean approval) {
		this.approval = approval;
	}
	
	/**
	 * Gets the attachment.
	 * 
	 * @return the attachment
	 */
	public final String getAttachment() {
		return attachment;
	}
	
	/**
	 * Sets the attachment.
	 * 
	 * @param attachment the attachment to set
	 */
	public final void setAttachment(final String attachment) {
		this.attachment = attachment;
	}
	
	/**
	 * Gets the comment.
	 * 
	 * @return the comment
	 */
	public final String getComment() {
		return comment;
	}
	
	/**
	 * Sets the comment.
	 * 
	 * @param comment the comment to set
	 */
	public final void setComment(final String comment) {
		this.comment = comment;
	}
	
	/**
	 * Gets the comment url.
	 * 
	 * @return the commentURL
	 */
	public final String getCommentURL() {
		return commentURL;
	}
	
	/**
	 * Sets the comment url.
	 * 
	 * @param commentURL the commentURL to set
	 */
	public final void setCommentURL(final String commentURL) {
		this.commentURL = commentURL;
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
	 * Gets the personName.
	 * 
	 * @return the enteredBy
	 */
	public final String getPersonName() {
		return personName;
	}
	
	/**
	 * Sets the personName.
	 * 
	 * @param enteredBy the enteredBy to set
	 */
	public final void setPersonName(final String enteredBy) {
		this.personName = enteredBy;
	}
	
	/**
	 * Gets the entered by id.
	 * 
	 * @return the enteredByID
	 */
	public final Long getEnteredByID() {
		return enteredByID;
	}
	
	/**
	 * Sets the entered by id.
	 * 
	 * @param enteredByID the enteredByID to set
	 */
	public final void setEnteredByID(final Long enteredByID) {
		this.enteredByID = enteredByID;
	}
	
	/**
	 * Checks if is evaluation.
	 * 
	 * @return the evaluation
	 */
	public final boolean isEvaluation() {
		return evaluation;
	}
	
	/**
	 * Sets the evaluation.
	 * 
	 * @param evaluation the evaluation to set
	 */
	public final void setEvaluation(final boolean evaluation) {
		this.evaluation = evaluation;
	}
	
	/**
	 * Checks if is identification.
	 * 
	 * @return the identification
	 */
	public final boolean isIdentification() {
		return identification;
	}
	
	/**
	 * Sets the identification.
	 * 
	 * @param identification the identification to set
	 */
	public final void setIdentification(final boolean identification) {
		this.identification = identification;
	}
	
	/**
	 * Gets the internal id.
	 * 
	 * @return the internalID
	 */
	public final Long getInternalID() {
		return internalID;
	}
	
	/**
	 * Sets the internal id.
	 * 
	 * @param internalID the internalID to set
	 */
	public final void setInternalID(final Long internalID) {
		this.internalID = internalID;
	}
	
	/**
	 * Checks if is mitigation.
	 * 
	 * @return the mitigation
	 */
	public final boolean isMitigation() {
		return mitigation;
	}
	
	/**
	 * Sets the mitigation.
	 * 
	 * @param mitigation the mitigation to set
	 */
	public final void setMitigation(final boolean mitigation) {
		this.mitigation = mitigation;
	}
	
	/**
	 * Gets the project id.
	 * 
	 * @return the projectID
	 */
	public final Long getProjectID() {
		return projectID;
	}
	
	/**
	 * Sets the project id.
	 * 
	 * @param projectID the projectID to set
	 */
	public final void setProjectID(final Long projectID) {
		this.projectID = projectID;
	}
	
	/**
	 * Checks if is review.
	 * 
	 * @return the review
	 */
	public final boolean isReview() {
		return review;
	}
	
	/**
	 * Sets the review.
	 * 
	 * @param review the review to set
	 */
	public final void setReview(final boolean review) {
		this.review = review;
	}
	
	/**
	 * Gets the risk id.
	 * 
	 * @return the riskID
	 */
	public final Long getRiskID() {
		return riskID;
	}
	
	/**
	 * Sets the risk id.
	 * 
	 * @param riskID the riskID to set
	 */
	public final void setRiskID(final Long riskID) {
		this.riskID = riskID;
	}
	
	/**
	 * Checks if is sched review.
	 * 
	 * @return true, if is sched review
	 */
	public final boolean isSchedReview() {
		return schedReview;
	}
	
	/**
	 * Sets the sched review.
	 * 
	 * @param schedReview the new sched review
	 */
	public final void setSchedReview(final boolean schedReview) {
		this.schedReview = schedReview;
	}
	
	/**
	 * Gets the sched review id.
	 * 
	 * @return the sched review id
	 */
	public final Long getSchedReviewID() {
		return schedReviewID;
	}
	
	/**
	 * Sets the sched review id.
	 * 
	 * @param schedReviewID the new sched review id
	 */
	public final void setSchedReviewID(final Long schedReviewID) {
		this.schedReviewID = schedReviewID;
	}
	
	/**
	 * Gets the sched review date.
	 * 
	 * @return the sched review date
	 */
	public final Date getSchedReviewDate() {
		return schedReviewDate;
	}
	
	/**
	 * Sets the sched review date.
	 * 
	 * @param schedReviewDate the new sched review date
	 */
	public final void setSchedReviewDate(final Date schedReviewDate) {
		this.schedReviewDate = schedReviewDate;
	}
}
