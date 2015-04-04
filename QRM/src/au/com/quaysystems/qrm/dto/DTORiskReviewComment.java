/*
 * 
 */
package au.com.quaysystems.qrm.dto;

import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class DTORiskReviewComment.
 */
public class DTORiskReviewComment {
	
	/** The title. */
	String title;
	
	/** The comment. */
	String comment;
	
	/** The schedule date. */
	Date scheduleDate;
	
	/** The actual date. */
	Date actualDate;
	
	/** The current tolerance. */
	int currentTolerance;
	
	/** The riskProjectCode. */
	String riskProjectCode;
	
	/** The riskID. */
	Long riskID;


	/**
	 * Instantiates a new dTO risk review comment.
	 * 
	 * @param title the title
	 * @param comment the comment
	 * @param scheduleDate the schedule date
	 * @param actualDate the actual date
	 */
	public DTORiskReviewComment(final String title, final String comment, final Date scheduleDate, final Date actualDate) {
		this.title = title;
		this.comment = comment;
		this.scheduleDate = scheduleDate;
		this.actualDate = actualDate;
	}
	
	/**
	 * Instantiates a new dTO risk review comment.
	 * 
	 * @param title the title
	 * @param comment the comment
	 * @param currentTolerance the current tolerance
	 * @param riskProjectCode the risk project code
	 * @param riskId the risk id
	 */
	public DTORiskReviewComment(final String title, final String comment,
			final int currentTolerance, final String riskProjectCode, final Long riskId) {
		this.title = title;
		this.comment = comment;
		this.currentTolerance = currentTolerance;
		this.riskProjectCode = riskProjectCode;
		riskID = riskId;
	}

}
