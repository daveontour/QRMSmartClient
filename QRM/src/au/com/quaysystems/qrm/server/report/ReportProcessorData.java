package au.com.quaysystems.qrm.server.report;

import java.util.Date;
import java.util.HashMap;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import au.com.quaysystems.qrm.dto.ModelScheduledJob;

@Entity
@Table(name="reportdata")
public class ReportProcessorData {

	public Long reportID;
	@Id
	public Long jobID;
	public String reportType;
	public Long userID;
	public Long projectID;
	public String title;
	public String description;
	public String jdbcURL;

	public boolean sendStatusUpdates = false;
	public boolean processRiskIDs = false;
	public boolean processElementIDs = false;
	public boolean sendEmail = false;
	public boolean schedJob = false;
	public boolean emailAttachment = false;
	public boolean processed = false;
	public boolean readyToProcess = false;
	public String format;
	public String emailFormat;
	public String emailTitle;
	public String emailContent;
	public boolean emailSent = false;
	public Date dateEmailSent;
	public Long schedJobID;
	
	public String jobStr;
	public String taskParamMapStr;

	@Transient
	public ModelScheduledJob job;
	@Transient
	public boolean  failed = false;;
	@Transient
	public HashMap<Object, Object> taskParamMap = new  HashMap<Object, Object>();
}
