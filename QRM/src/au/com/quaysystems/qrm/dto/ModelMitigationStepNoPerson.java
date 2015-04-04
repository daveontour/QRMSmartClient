/*
 * 
 */

package au.com.quaysystems.qrm.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelMitigationStep.
 */
@Entity
@Table(name="mitigationstep")
public class ModelMitigationStepNoPerson  implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6901271069395038460L;
	
	@Id
	@GeneratedValue
	public Long mitstepID;
	public long projectID;
	public long riskID;
	public boolean response = false;
	public ModelMitigationStepNoPerson(){}

}
