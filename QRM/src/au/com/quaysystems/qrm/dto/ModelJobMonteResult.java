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
@Table(name="jobmonteresult")
public class ModelJobMonteResult {

	/** The id. */
	@Id
	@GeneratedValue
	long id;

	/** The job id. */
	long jobID;

	/** The type id. */
	long typeID;

	/** The pre mit. */
	boolean preMit;

	/** The result str. */
	String resultStr;

	/** The image. */
	byte[] image;
	
	Double p10;
	Double p20;
	Double p30;
	Double p40;
	Double p50;
	Double p60;
	Double p70;
	Double p80;
	Double p90;
	Double p100;

	/**
	 * Instantiates a new model monte carlo result.
	 */
	public ModelJobMonteResult(){}

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
	public final long getTypeID() {
		return typeID;
	}

	/**
	 * Sets the type id.
	 * 
	 * @param typeID the new type id
	 */
	public final void setTypeID(final long typeID) {
		this.typeID = typeID;
	}

	/**
	 * Checks if is pre mit.
	 * 
	 * @return true, if is pre mit
	 */
	public final boolean isPreMit() {
		return preMit;
	}

	/**
	 * Sets the pre mit.
	 * 
	 * @param preMit the new pre mit
	 */
	public final void setPreMit(final boolean preMit) {
		this.preMit = preMit;
	}

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

	/**
	 * Gets the image.
	 * 
	 * @return the image
	 */
	public final byte[] getImage() {
		return image;
	}

	/**
	 * Sets the image.
	 * 
	 * @param image the new image
	 */
	public final void setImage(final byte[] image) {
		this.image = image;
	}

	public Double getP10() {
		return p10;
	}

	public void setP10(Double p10) {
		this.p10 = p10;
	}

	public Double getP20() {
		return p20;
	}

	public void setP20(Double p20) {
		this.p20 = p20;
	}

	public Double getP30() {
		return p30;
	}

	public void setP30(Double p30) {
		this.p30 = p30;
	}

	public Double getP40() {
		return p40;
	}

	public void setP40(Double p40) {
		this.p40 = p40;
	}

	public Double getP50() {
		return p50;
	}

	public void setP50(Double p50) {
		this.p50 = p50;
	}

	public Double getP60() {
		return p60;
	}

	public void setP60(Double p60) {
		this.p60 = p60;
	}

	public Double getP70() {
		return p70;
	}

	public void setP70(Double p70) {
		this.p70 = p70;
	}

	public Double getP80() {
		return p80;
	}

	public void setP80(Double p80) {
		this.p80 = p80;
	}

	public Double getP90() {
		return p90;
	}

	public void setP90(Double p90) {
		this.p90 = p90;
	}

	public Double getP100() {
		return p100;
	}

	public void setP100(Double p100) {
		this.p100 = p100;
	}
}
