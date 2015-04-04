/*
 * 
 */
package au.com.quaysystems.qrm.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

@NamedNativeQueries( {
	@NamedNativeQuery(name = "getRiskObjectives", query = "SELECT *  FROM objectives_impacted   WHERE riskID = :riskID", resultClass = DTOObjectiveImpacted.class)
})
@Entity
@Table(name = "objectives_impacted")
public class DTOObjectiveImpacted {

	/** The id. */
	@Id
	@GeneratedValue
	public Long id;

	/** The riskID. */
	public Long riskID;

	/** The objectiveID. */
	public Long objectiveID;

	/**
	 * Instantiates a new dTO objective impacted.
	 */
	public DTOObjectiveImpacted() {
	}

	/**
	 * Instantiates a new dTO objective impacted.
	 * 
	 * @param riskID
	 *            the riskID
	 * @param objectiveID
	 *            the objectiveID
	 */
	public DTOObjectiveImpacted(final Long riskID, final Long objectiveID) {
		this.riskID = riskID;
		this.objectiveID = objectiveID;
	}
}