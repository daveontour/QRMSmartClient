/*
 * 
 */
package au.com.quaysystems.qrm.dto;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelRiskSubjRank.
 */
public class ModelRiskSubjRank implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3285531966562939016L;

	/** The rank. */
	protected Long rank;

	/** The risk id. */
	protected Long riskId;

	/**
	 * Instantiates a new model risk subj rank.
	 */
	public ModelRiskSubjRank() {
	}

	/**
	 * Gets the rank.
	 * 
	 * @return the rank
	 */
	public final Long getRank() {
		return rank;
	}

	/**
	 * Sets the rank.
	 * 
	 * @param rank
	 *            the rank to set
	 */
	public final void setRank(final Long rank) {
		this.rank = rank;
	}

	/**
	 * Gets the risk id.
	 * 
	 * @return the riskId
	 */
	public final Long getRiskId() {
		return riskId;
	}

	/**
	 * Sets the risk id.
	 * 
	 * @param riskId
	 *            the riskId to set
	 */
	public final void setRiskId(final Long riskId) {
		this.riskId = riskId;
	}

}
