/*
 * 
 */

package au.com.quaysystems.qrm.dto;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Class ModelJobResult.
 */
@Entity
@Table(name="jobresult")
public class ModelJobResult {

	/** The id. */
	@Id
	@GeneratedValue
	long id;

	/** The job id. */
	long jobID;

	/** The result str. */
	String resultStr;



	/**
	 * Instantiates a new model monte carlo result.
	 */
	public ModelJobResult(){}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	public final long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the new id
	 */
	public final void setId(final long id) {
		this.id = id;
	}

	/**
	 * Gets the job id.
	 * 
	 * @return the job id
	 */
	public final long getJobID() {
		return jobID;
	}

	/**
	 * Sets the job id.
	 * 
	 * @param jobID the new job id
	 */
	public final void setJobID(final long jobID) {
		this.jobID = jobID;
	}

	/**
	 * Gets the type id.
	 * 
	 * @return the type id
	 */

	/**
	 * Gets the result str.
	 * 
	 * @return the result str
	 */
	public final String getResultStr() {
		return resultStr;
	}

	/**
	 * Sets the result str.
	 * 
	 * @param resultStr the new result str
	 */
	public final void setResultStr(final String resultStr) {
		this.resultStr = resultStr;
	}

	/**
	 * Gets the result list.
	 * 
	 * @return the result list
	 */
	public final ArrayList<Double> getResultList(){
		ArrayList<Double> resultList = new	ArrayList<Double> ();
		for (String str:resultStr.split("#")){
			resultList.add(Double.parseDouble(str));
		}
		return resultList;
	}
}
