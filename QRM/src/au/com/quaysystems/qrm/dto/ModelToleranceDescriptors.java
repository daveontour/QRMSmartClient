/*
 * 
 */
package au.com.quaysystems.qrm.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;


@NamedNativeQueries( {
	@NamedNativeQuery(name = "getProjectTolActions", callable = true, query = "call getProjectTolActions(:projectID )", resultClass = ModelToleranceDescriptors.class)
})
@Entity
@Table(name="toldescriptors")
public class ModelToleranceDescriptors  implements Serializable {

 	/** The Constant serialVersionUID. */
	 private static final long serialVersionUID = 7272334527358122964L;
    
    /** The descriptorID. */
    @Id
    @GeneratedValue
	public Long descriptorID;
	
	/** The projectID. */
	public Long projectID;
 	
	 /** The long name. */
	 public String longName;
    
    /** The short name. */
    public String shortName;
    
    /** The tol action. */
    public String tolAction;
    
    /** The tol level. */
    public int tolLevel;
    
    
    /**
     * Instantiates a new model tolerance descriptors.
     */
    public ModelToleranceDescriptors(){}
	
	/**
	 * Gets the long name.
	 * 
	 * @return the longName
	 */
	public final String getLongName() {
		return longName;
	}
	
	/**
	 * Sets the long name.
	 * 
	 * @param longName the longName to set
	 */
	public final void setLongName(final String longName) {
		this.longName = longName;
	}
	
	/**
	 * Gets the short name.
	 * 
	 * @return the shortName
	 */
	public final String getShortName() {
		return shortName;
	}
	
	/**
	 * Sets the short name.
	 * 
	 * @param shortName the shortName to set
	 */
	public final void setShortName(final String shortName) {
		this.shortName = shortName;
	}
	
	/**
	 * Gets the tol action.
	 * 
	 * @return the tolAction
	 */
	public final String getTolAction() {
		return tolAction;
	}
	
	/**
	 * Sets the tol action.
	 * 
	 * @param tolAction the tolAction to set
	 */
	public final void setTolAction(final String tolAction) {
		this.tolAction = tolAction;
	}
	
	/**
	 * Gets the tol level.
	 * 
	 * @return the tolLevel
	 */
	public final int getTolLevel() {
		return tolLevel;
	}
	
	/**
	 * Sets the tol level.
	 * 
	 * @param tolLevel the tolLevel to set
	 */
	public final void setTolLevel(final int tolLevel) {
		this.tolLevel = tolLevel;
	}
	
	/**
	 * Gets the descriptorID.
	 * 
	 * @return the descriptorID
	 */
	public final Long getDescriptorID() {
		return descriptorID;
	}
	
	/**
	 * Sets the descriptorID.
	 * 
	 * @param descriptorId the new descriptorID
	 */
	public final void setDescriptorID(final Long descriptorId) {
		descriptorID = descriptorId;
	}
	
	/**
	 * Gets the projectID.
	 * 
	 * @return the projectID
	 */
	public final Long getProjectID() {
		return projectID;
	}
	
	/**
	 * Sets the projectID.
	 * 
	 * @param projectId the new projectID
	 */
	public final void setProjectID(final Long projectId) {
		projectID = projectId;
	}
}

