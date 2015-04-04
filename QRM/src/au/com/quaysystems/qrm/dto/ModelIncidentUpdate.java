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


// TODO: Auto-generated Javadoc
/**
 * The Class ModelIncidentUpdate.
 */
@Entity
@Table(name="incidentupdate")
public class ModelIncidentUpdate  implements Serializable {

 	/** The Constant serialVersionUID. */
	 private static final long serialVersionUID = -3820617460543943375L;
	
	/** The id. */
	@Id
    @GeneratedValue
 	public Long id;
	
	public Long  incidentID;
	public Date  dateEntered;
	public String  description;
	public Long   reportedByID;
	public String  reportedByStr;
	
    public ModelIncidentUpdate(){}
    
}
