/*
 * 
 */
package au.com.quaysystems.qrm.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Class ModelJobQueue.
 */
@Entity
@Table(name="jobReportData")
public class ModelJobReportData  implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4048549549099171988L;
	/** The job id. */
	@Id
	@GeneratedValue
	long id;
	long jobID;
	String encodedReportData;
	
	public ModelJobReportData(){}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the jobID
	 */
	public long getJobID() {
		return jobID;
	}

	/**
	 * @param jobID the jobID to set
	 */
	public void setJobID(long jobID) {
		this.jobID = jobID;
	}

	/**
	 * @return the encodedReportData
	 */
	public String getEncodedReportData() {
		return encodedReportData;
	}

	/**
	 * @param encodedReportData the encodedReportData to set
	 */
	public void setEncodedReportData(String encodedReportData) {
		this.encodedReportData = encodedReportData;
	}

}
