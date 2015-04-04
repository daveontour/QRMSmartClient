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

//@NamedNativeQueries( {
//	@NamedNativeQuery(name = "getRiskConsequences", query = "SELECT riskconsequence.*  FROM riskconsequence   WHERE riskconsequence.riskID = :riskID", resultClass = ModelRiskConsequence.class)
//})
@Entity
@Table(name = "riskconsequence")
public class ModelRiskConsequence implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 401181837159386520L;

	/** The cost distribution params array. */
	@Transient
	public ArrayList<Double> costDistributionParamsArray;

	/** The post cost distribution params array. */
	@Transient
	public ArrayList<Double> postCostDistributionParamsArray;

	/** The date updated. */
	@Column(updatable = false, insertable = false)
	public Date dateUpdated;

	/** The internal id. */
	@Id
	@GeneratedValue
	public Long internalID;

	/** The cost distribution params. */
	public String costDistributionParams;

	/** The cost distribution type. */
	public String costDistributionType;

	/** The description. */
	public String description;

	/** The post cost distribution params. */
	public String postCostDistributionParams;

	/** The post cost distribution type. */
	public String postCostDistributionType;

	/** The post risk consequence prob. */
	public Double postRiskConsequenceProb;

	/** The quantifiable. */
	public boolean quantifiable = true;

	/** The risk consequence prob. */
	public Double riskConsequenceProb;

	/** The risk id. */
	public long riskID;

	/** The quant type. */
	public long quantType;

	// @ManyToOne(fetch = FetchType.EAGER)
	// @JoinColumn(name="QUANTTYPE")
	/** The quant impact type. */
	@Transient
	public ModelQuantImpactType quantImpactType;

	/**
	 * Instantiates a new model risk consequence.
	 */
	public ModelRiskConsequence() {
	}

	/**
	 * Compile.
	 */
	public final void compile() {

		String[] paramsStr = costDistributionParams.split(":");
		try {
			for (String str : paramsStr) {
				getCostDistributionParams().add(Double.parseDouble(str));
			}
		} catch (NumberFormatException e) {

		}

		String[] paramsPostStr = postCostDistributionParams.split(":");
		try {
			for (String str : paramsPostStr) {
				getPostCostDistributionParams().add(Double.parseDouble(str));
			}
		} catch (NumberFormatException e) {

		}
	}

	/**
	 * Decompile.
	 */
	public final void decompile() {
		try {
			String str2 = "";
			for (Double d : getPostCostDistributionParams()) {
				str2 = str2.concat(d.toString() + ":");
			}
			postCostDistributionParams = str2;
		} catch (RuntimeException e) {
			postCostDistributionParams = "";
		}

		try {
			String str2 = "";
			for (Double d : getCostDistributionParams()) {
				str2 = str2.concat(d.toString() + ":");
			}
			costDistributionParams = str2;
		} catch (RuntimeException e) {
			costDistributionParams = "";
		}

	}

	/**
	 * Gets the cost distribution params.
	 * 
	 * @return the costDistributionParams
	 */
	public final ArrayList<Double> getCostDistributionParams() {
		if (costDistributionParamsArray == null) {
			costDistributionParamsArray = new ArrayList<Double>();
		}
		return costDistributionParamsArray;
	}

	/**
	 * Sets the cost distribution params array.
	 * 
	 * @param costDistributionParams
	 *            the costDistributionParams to set
	 */
	public final void setCostDistributionParamsArray(
			final ArrayList<Double> costDistributionParams) {
		this.costDistributionParamsArray = costDistributionParams;
	}

	/**
	 * Gets the cost distribution type.
	 * 
	 * @return the costDistributionType
	 */
	public final String getCostDistributionType() {
		return costDistributionType;
	}

	/**
	 * Sets the cost distribution type.
	 * 
	 * @param costDistributionType
	 *            the costDistributionType to set
	 */
	public final void setCostDistributionType(final String costDistributionType) {
		this.costDistributionType = costDistributionType;
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
	 * @param dateUpdated
	 *            the dateUpdated to set
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
	 * @param description
	 *            the description to set
	 */
	public final void setDescription(final String description) {
		this.description = description;
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
	 * @param internalID
	 *            the internalID to set
	 */
	public final void setInternalID(final Long internalID) {
		this.internalID = internalID;
	}


	/**
	 * Gets the post cost distribution params.
	 * 
	 * @return the postCostDistributionParams
	 */
	public final ArrayList<Double> getPostCostDistributionParams() {
		if (postCostDistributionParamsArray == null) {
			postCostDistributionParamsArray = new ArrayList<Double>();
		}
		return postCostDistributionParamsArray;
	}

	/**
	 * Sets the post cost distribution params array.
	 * 
	 * @param postCostDistributionParams
	 *            the postCostDistributionParams to set
	 */
	public final void setPostCostDistributionParamsArray(
			final ArrayList<Double> postCostDistributionParams) {
		this.postCostDistributionParamsArray = postCostDistributionParams;
	}

	/**
	 * Gets the post cost distribution type.
	 * 
	 * @return the postCostDistributionType
	 */
	public final String getPostCostDistributionType() {
		return postCostDistributionType;
	}

	/**
	 * Sets the post cost distribution type.
	 * 
	 * @param postCostDistributionType
	 *            the postCostDistributionType to set
	 */
	public final void setPostCostDistributionType(
			final String postCostDistributionType) {
		this.postCostDistributionType = postCostDistributionType;
	}

	/**
	 * Gets the post risk consequence prob.
	 * 
	 * @return the postRiskConsequenceProb
	 */
	public final Double getPostRiskConsequenceProb() {
		return postRiskConsequenceProb;
	}

	/**
	 * Sets the post risk consequence prob.
	 * 
	 * @param postRiskConsequenceProb
	 *            the postRiskConsequenceProb to set
	 */
	public final void setPostRiskConsequenceProb(
			final Double postRiskConsequenceProb) {
		this.postRiskConsequenceProb = postRiskConsequenceProb;
	}

	/**
	 * Checks if is quantifiable.
	 * 
	 * @return the quantifiable
	 */
	public final boolean isQuantifiable() {
		return quantifiable;
	}

	/**
	 * Sets the quantifiable.
	 * 
	 * @param quantifiable
	 *            the quantifiable to set
	 */
	public final void setQuantifiable(final boolean quantifiable) {
		this.quantifiable = quantifiable;
	}

	/**
	 * Gets the quant impact type.
	 * 
	 * @return the quantImpactType
	 */
	public final ModelQuantImpactType getQuantImpactType() {
		return quantImpactType;
	}

	/**
	 * Sets the quant impact type.
	 * 
	 * @param quantImpactType
	 *            the quantImpactType to set
	 */
	public final void setQuantImpactType(
			final ModelQuantImpactType quantImpactType) {
		this.quantImpactType = quantImpactType;
	}

	/**
	 * Gets the risk consequence prob.
	 * 
	 * @return the riskConsequenceProb
	 */
	public final Double getRiskConsequenceProb() {
		return riskConsequenceProb;
	}

	/**
	 * Sets the risk consequence prob.
	 * 
	 * @param riskConsequenceProb
	 *            the riskConsequenceProb to set
	 */
	public final void setRiskConsequenceProb(final Double riskConsequenceProb) {
		this.riskConsequenceProb = riskConsequenceProb;
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
	 * @param riskID
	 *            the riskID to set
	 */
	public final void setRiskID(final long riskID) {
		this.riskID = riskID;
	}

}
