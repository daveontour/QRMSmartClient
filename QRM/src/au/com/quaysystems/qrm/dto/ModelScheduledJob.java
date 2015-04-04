package au.com.quaysystems.qrm.dto;

import java.io.Serializable;
import java.util.HashMap;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "schedjob")
public class ModelScheduledJob implements Serializable {

	@Id
	@GeneratedValue
	public Long internalID;
	public Boolean Mon;
	public Boolean Tue;
	public Boolean Wed;
	public Boolean Thu;
	public Boolean Fri;
	public Boolean Sat;
	public Boolean Sun;
	public Long userID;
	public Long repository;
	public Long reportID;
	public Long projectID;
	public Boolean descendants;
	public String description;
	public String timeStr;
	public Boolean email;
	public String additionalUsers;
	public String taskParamMapStr;
	
	@Transient
	public String database;
	@Transient
	public String databaseUser;
	@Transient
	public HashMap<Object, Object> taskParamMap = new  HashMap<Object, Object>();
}
