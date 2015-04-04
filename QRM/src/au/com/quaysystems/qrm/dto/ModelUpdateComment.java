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

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

@NamedNativeQueries( {
	@NamedNativeQuery(name = "getRiskMitigationStepsUpdate", query = "SELECT mitigationstep.riskID, updatecomment.*   FROM mitigationstep  JOIN updatecomment ON mitigationstep.mitStepID = updatecomment.hostID AND updatecomment.hostType='MITIGATION' WHERE mitigationstep.riskID = :riskID", resultClass = ModelUpdateComment.class)
})

@Entity
@Table(name="updatecomment")
public class ModelUpdateComment  implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6901271069395038460L;
	
	/** The mitstepID. */
	@Id
	@GeneratedValue
	public Long internalID;
	public Long hostID;
	public String hostType;
	public long personID;
	public Date dateEntered;
	public Date dateUpdated;
	public String description;
	
	
	/**
	 * Instantiates a new model mitigation step.
	 */
	public ModelUpdateComment(){}


	public ModelUpdateComment(long hostID, String hostType, String description,
			Long personID) {
		
		this.hostID = hostID;
		this.hostType = hostType;
		this.description = description;
		this.personID = personID;
		// TODO Auto-generated constructor stub
	}

}
