/*
 * 
 */
package au.com.quaysystems.qrm.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
	@NamedNativeQuery(name = "getProjectQuantTypes", callable = true, query = "call getProjectQuantTypes(:projectID )", resultClass = ModelQuantImpactType.class),
	@NamedNativeQuery(name = "getAllQuantTypes",resultClass = ModelQuantImpactType.class, query = "SELECT 0 AS GENERATION, quantimpacttype.* FROM quantimpacttype"),
	@NamedNativeQuery(name = "getQuantType",resultClass = ModelQuantImpactType.class, query = "SELECT 0 AS GENERATION, quantimpacttype.* FROM quantimpacttype WHERE typeID = :typeID")
})
@Entity
@Table(name = "quantimpacttype")
public class ModelQuantImpactType implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7793336930614360642L;

	/** The typeID. */
	@Id
	@GeneratedValue
	public Long typeID;

	/** The date entered. */
	@Transient
	public Date dateEntered;

	/** The date updated. */
	@Transient
	public Date dateUpdated;

	/** The lower. */
	@Transient
	public ArrayList<Double> lower;

	/** The rankdesc. */
	@Transient
	public ArrayList<String> rankdesc;

	/** The upper. */
	@Transient
	public ArrayList<Double> upper;

	/** The cost categroy. */
	public boolean costCategroy;

	/** The cost summary. */
	public boolean costSummary;

	/** The project title. */
	@Transient
	public String projectTitle;

	/** The units. */
	public String units;

	/** The description. */
	public String description;

	/** The generation. */
	@Column(updatable = false, insertable = false)
	public int generation;

	/** The nill quant impact. */
	public boolean nillQuantImpact;

	/** The projectID. */
	public Long projectID;

	/** The description1. */
	public String description1 = "";

	/** The description2. */
	public String description2 = "";

	/** The description3. */
	public String description3 = "";

	/** The description4. */
	public String description4 = "";

	/** The description5. */
	public String description5 = "";

	/** The description6. */
	public String description6 = "";

	/** The description7. */
	public String description7 = "";

	/** The description8. */
	public String description8 = "";

	/** The lower1. */
	public Double lower1 = 0.0;

	/** The lower2. */
	public Double lower2 = 0.0;

	/** The lower3. */
	public Double lower3 = 0.0;

	/** The lower4. */
	public Double lower4 = 0.0;

	/** The lower5. */
	public Double lower5 = 0.0;

	/** The lower6. */
	public Double lower6 = 0.0;

	/** The lower7. */
	public Double lower7 = 0.0;

	/** The lower8. */
	public Double lower8 = 0.0;

	/** The upper1. */
	public Double upper1 = 0.0;

	/** The upper2. */
	public Double upper2 = 0.0;

	/** The upper3. */
	public Double upper3 = 0.0;

	/** The upper4. */
	public Double upper4 = 0.0;

	/** The upper5. */
	public Double upper5 = 0.0;

	/** The upper6. */
	public Double upper6 = 0.0;

	/** The upper7. */
	public Double upper7 = 0.0;

	/** The upper8. */
	public Double upper8 = 0.0;

	/**
	 * Instantiates a new model quant impact type.
	 */
	public ModelQuantImpactType() {
	}

	/**
	 * Compile.
	 */
	public final void compile() {
		rankdesc = new ArrayList<String>(Arrays.asList(new String[] {
				description1, description2, description3, description4,
				description5, description6, description7, description8 }));
		lower = new ArrayList<Double>(Arrays.asList(new Double[] { lower1,
				lower2, lower3, lower4, lower5, lower6, lower7, lower8 }));
		upper = new ArrayList<Double>(Arrays.asList(new Double[] { upper1,
				upper2, upper3, upper4, upper5, upper6, upper7, upper8 }));
	}

	/**
	 * Decompile.
	 */
	public final void decompile() {
		if (rankdesc != null && rankdesc.size() > 0) {
			description1 = rankdesc.get(0);
			description2 = rankdesc.get(1);
			description3 = rankdesc.get(2);
			description4 = rankdesc.get(3);
			description5 = rankdesc.get(4);
			description6 = rankdesc.get(5);
			description7 = rankdesc.get(6);
			description8 = rankdesc.get(7);
		}

		if (lower != null && lower.size() > 0) {

			lower1 = lower.get(0);
			lower2 = lower.get(1);
			lower3 = lower.get(2);
			lower4 = lower.get(3);
			lower5 = lower.get(4);
			lower6 = lower.get(5);
			lower7 = lower.get(6);
			lower8 = lower.get(7);
		}

		if (upper != null && upper.size() > 0) {

			upper1 = upper.get(0);
			upper2 = upper.get(1);
			upper3 = upper.get(2);
			upper4 = upper.get(3);
			upper5 = upper.get(4);
			upper6 = upper.get(5);
			upper7 = upper.get(6);
			upper8 = upper.get(7);
		}

	}

	/**
	 * Checks if is cost categroy.
	 * 
	 * @return the costCategroy
	 */
	public final boolean isCostCategroy() {
		return costCategroy;
	}

	/**
	 * Sets the cost categroy.
	 * 
	 * @param costCategroy
	 *            the costCategroy to set
	 */
	public final void setCostCategroy(final boolean costCategroy) {
		this.costCategroy = costCategroy;
	}

	/**
	 * Checks if is cost summary.
	 * 
	 * @return the costSummary
	 */
	public final boolean isCostSummary() {
		return costSummary;
	}

	/**
	 * Sets the cost summary.
	 * 
	 * @param costSummary
	 *            the costSummary to set
	 */
	public final void setCostSummary(final boolean costSummary) {
		this.costSummary = costSummary;
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
	 * @param value
	 *            the new date entered
	 */
	public final void setDateEntered(final Date value) {
		this.dateEntered = value;
	}

	/**
	 * Gets the date updated.
	 * 
	 * @return the date updated
	 */
	public final Date getDateUpdated() {
		return dateUpdated;
	}

	/**
	 * Sets the date updated.
	 * 
	 * @param value
	 *            the new date updated
	 */
	public final void setDateUpdated(final Date value) {
		this.dateUpdated = value;
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
	public final Long getInternalID() {
		return typeID;
	}

	/**
	 * Sets the internal id.
	 * 
	 * @param internalID
	 *            the internalID to set
	 */
	public final void setInternalID(final Long internalID) {
		this.typeID = internalID;
	}

	/**
	 * Gets the lower.
	 * 
	 * @return the lower
	 */
	public final ArrayList<Double> getLower() {
		if (lower == null) {
			lower = new ArrayList<Double>(Arrays.asList(new Double[] { lower1,
					lower2, lower3, lower4, lower5, lower6, lower7, lower8 }));
		}
		return lower;
	}

	/**
	 * Sets the lower.
	 * 
	 * @param lower
	 *            the lower to set
	 */
	public final void setLower(final ArrayList<Double> lower) {
		this.lower = lower;
	}

	/**
	 * Checks if is nill quant impact.
	 * 
	 * @return the nillQuantImpact
	 */
	public final boolean isNillQuantImpact() {
		return nillQuantImpact;
	}

	/**
	 * Sets the nill quant impact.
	 * 
	 * @param nillQuantImpact
	 *            the nillQuantImpact to set
	 */
	public final void setNillQuantImpact(final boolean nillQuantImpact) {
		this.nillQuantImpact = nillQuantImpact;
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
	 * @param projectID
	 *            the projectID to set
	 */
	public final void setProjectID(final Long projectID) {
		this.projectID = projectID;
	}

	/**
	 * Gets the rankdesc.
	 * 
	 * @return the rankdesc
	 */
	public final ArrayList<String> getRankdesc() {
		if (rankdesc == null) {
			rankdesc = new ArrayList<String>(Arrays
					.asList(new String[] { description1, description2,
							description3, description4, description5,
							description6, description7, description8 }));
		}
		return rankdesc;
	}

	/**
	 * Sets the rankdesc.
	 * 
	 * @param rankdesc
	 *            the rankdesc to set
	 */
	public final void setRankdesc(final ArrayList<String> rankdesc) {
		this.rankdesc = rankdesc;
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
	 * Gets the units.
	 * 
	 * @return the units
	 */
	public final String getUnits() {
		return units;
	}

	/**
	 * Sets the units.
	 * 
	 * @param units
	 *            the units to set
	 */
	public final void setUnits(final String units) {
		this.units = units;
	}

	/**
	 * Gets the upper.
	 * 
	 * @return the upper
	 */
	public final ArrayList<Double> getUpper() {
		if (upper == null) {
			upper = new ArrayList<Double>(Arrays.asList(new Double[] { upper1,
					upper2, upper3, upper4, upper5, upper6, upper7, upper8 }));
		}
		return upper;
	}

	/**
	 * Sets the upper.
	 * 
	 * @param upper
	 *            the upper to set
	 */
	public final void setUpper(final ArrayList<Double> upper) {
		this.upper = upper;
	}
}
