/*
 * 
 */
package au.com.quaysystems.qrm.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name="repositories")
public class ModelRepository  {

	@Id
    @GeneratedValue
	public Long repID;
	public String rep;
	public Long repmgr;
	public String url;
	public String orgname;
	public Boolean active;
	public Boolean autoAddUsers;
	public Integer sessionlimit;
	public Integer userlimit;
	public String repLogonMessage;
	public Boolean activateOnStartup;
	@Transient
	public Integer sessions;
	
	public ModelRepository(){}
}
