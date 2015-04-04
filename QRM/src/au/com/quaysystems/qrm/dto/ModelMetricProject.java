/*
 * 
 */
package au.com.quaysystems.qrm.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelMetricProject.
 */
@Entity
@Table(name="projectmetric")
public class ModelMetricProject  implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7910812924476109469L;
	
	/** The id. */
	@Id
    @GeneratedValue
    public Long id;
	
	/** The metric id. */
	public Long metricID;
	
	/** The project id. */
	public Long projectID;
	
	/** The method. */
	@Transient
	public String method; 
	
	/** The description. */
	@Transient
	public String description;
	
	/** The grayl. */
	public Double grayl;
	
	/** The grayu. */
	public Double grayu;
	
	/** The greenl. */
	public Double greenl;
	
	/** The greenu. */
	public Double greenu;
	
	/** The yellowl. */
	public Double yellowl;
	
	/** The yellowu. */
	public Double yellowu;
	
	/** The redl. */
	public Double redl;
	
	/** The redu. */
	public Double redu; 
	
	/** The low. */
	public Double low;
	
	/** The high. */
	public Double high;
	
	/** The title. */
	@Transient
	public String title;
	
	/** The config limit. */
	public Boolean configLimit;
	
	/** The config range. */
	public Boolean configRange;
	

	 /**
 	 * Instantiates a new model metric project.
 	 */
 	public ModelMetricProject(){}


	/**
	 * Gets the metric id.
	 * 
	 * @return the metric id
	 */
	public final Long getMetricID() {
		return metricID;
	}


	/**
	 * Sets the metric id.
	 * 
	 * @param metricID the new metric id
	 */
	public final void setMetricID(final Long metricID) {
		this.metricID = metricID;
	}


	/**
	 * Gets the project id.
	 * 
	 * @return the project id
	 */
	public final Long getProjectID() {
		return projectID;
	}


	/**
	 * Sets the project id.
	 * 
	 * @param projectID the new project id
	 */
	public final void setProjectID(final Long projectID) {
		this.projectID = projectID;
	}


	/**
	 * Gets the method.
	 * 
	 * @return the method
	 */
	public final String getMethod() {
		return method;
	}


	/**
	 * Sets the method.
	 * 
	 * @param method the new method
	 */
	public final void setMethod(final String method) {
		this.method = method;
	}


	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public final String getDescription() {
		return description;
	}


	/**
	 * Sets the description.
	 * 
	 * @param description the new description
	 */
	public final void setDescription(final String description) {
		this.description = description;
	}


	/**
	 * Gets the grayl.
	 * 
	 * @return the grayl
	 */
	public final Double getGrayl() {
		return grayl;
	}


	/**
	 * Sets the grayl.
	 * 
	 * @param grayl the new grayl
	 */
	public final void setGrayl(final Double grayl) {
		this.grayl = grayl;
	}


	/**
	 * Gets the grayu.
	 * 
	 * @return the grayu
	 */
	public final Double getGrayu() {
		return grayu;
	}


	/**
	 * Sets the grayu.
	 * 
	 * @param grayu the new grayu
	 */
	public final void setGrayu(final Double grayu) {
		this.grayu = grayu;
	}


	/**
	 * Gets the greenl.
	 * 
	 * @return the greenl
	 */
	public final Double getGreenl() {
		return greenl;
	}


	/**
	 * Sets the greenl.
	 * 
	 * @param greenl the new greenl
	 */
	public final void setGreenl(final Double greenl) {
		this.greenl = greenl;
	}


	/**
	 * Gets the greenu.
	 * 
	 * @return the greenu
	 */
	public final Double getGreenu() {
		return greenu;
	}


	/**
	 * Sets the greenu.
	 * 
	 * @param greenu the new greenu
	 */
	public final void setGreenu(final Double greenu) {
		this.greenu = greenu;
	}


	/**
	 * Gets the yellowl.
	 * 
	 * @return the yellowl
	 */
	public final Double getYellowl() {
		return yellowl;
	}


	/**
	 * Sets the yellowl.
	 * 
	 * @param yellowl the new yellowl
	 */
	public final void setYellowl(final Double yellowl) {
		this.yellowl = yellowl;
	}


	/**
	 * Gets the yellowu.
	 * 
	 * @return the yellowu
	 */
	public final Double getYellowu() {
		return yellowu;
	}


	/**
	 * Sets the yellowu.
	 * 
	 * @param yellowu the new yellowu
	 */
	public final void setYellowu(final Double yellowu) {
		this.yellowu = yellowu;
	}


	/**
	 * Gets the redl.
	 * 
	 * @return the redl
	 */
	public final Double getRedl() {
		return redl;
	}


	/**
	 * Sets the redl.
	 * 
	 * @param redl the new redl
	 */
	public final void setRedl(final Double redl) {
		this.redl = redl;
	}


	/**
	 * Gets the redu.
	 * 
	 * @return the redu
	 */
	public final Double getRedu() {
		return redu;
	}


	/**
	 * Sets the redu.
	 * 
	 * @param redu the new redu
	 */
	public final void setRedu(final Double redu) {
		this.redu = redu;
	}


	/**
	 * Gets the low.
	 * 
	 * @return the low
	 */
	public final Double getLow() {
		return low;
	}


	/**
	 * Sets the low.
	 * 
	 * @param low the new low
	 */
	public final void setLow(final Double low) {
		this.low = low;
	}


	/**
	 * Gets the high.
	 * 
	 * @return the high
	 */
	public final Double getHigh() {
		return high;
	}


	/**
	 * Sets the high.
	 * 
	 * @param high the new high
	 */
	public final void setHigh(final Double high) {
		this.high = high;
	}


	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	public final String getTitle() {
		return title;
	}


	/**
	 * Sets the title.
	 * 
	 * @param title the new title
	 */
	public final void setTitle(final String title) {
		this.title = title;
	}
	
	
}
