/*
 * 
 */
package au.com.quaysystems.qrm.dto;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The Class ModelEmailData.
 */
@Entity
@Table(name="emailrecord")
public class ModelEmailRecord  {

	/** The id. */
	@Id
	@GeneratedValue
	long id;
	
	@Temporal(TemporalType.DATE)
	public Date emaildate;
	
	public boolean successful;
	public String errorMsg;
	public String messageObject;
	
	public ModelEmailRecord(long id, Date emaildate, boolean successful,
			String errorMsg, String messageObject) {
		this.id = id;
		this.emaildate = emaildate;
		this.successful = successful;
		this.errorMsg = errorMsg;
		this.messageObject = messageObject;
	}
}
