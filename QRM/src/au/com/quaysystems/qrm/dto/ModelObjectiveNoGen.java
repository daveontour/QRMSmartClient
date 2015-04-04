/*
 * 
 */

package au.com.quaysystems.qrm.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelObjective.
 */
@Entity
@Table(name="objective")
public class ModelObjectiveNoGen  implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2275350167031132360L;
	
	/** The objective. */
	public String objective;
   
    /** The objectiveID. */
    @Id
    @GeneratedValue
    public Long objectiveID;
    
    /** The parentID. */
    public long parentID;
    
    /** The projectID. */
    public long projectID;
    
    /** The project title. */
    @Column(insertable=false, updatable=false)
	@Transient
    public String projectTitle;

    /**
     * Instantiates a new model objective.
     */
    public ModelObjectiveNoGen(){}
    
    /**
     * Gets the description.
     * 
     * @return the description
     */
    public final String getDescription() {
        return objective;
    }

    /**
     * Sets the description.
     * 
     * @param value the new description
     */
    public final void setDescription(final String value) {
        this.objective = value;
    }


    /**
     * Gets the internal id.
     * 
     * @return the internal id
     */
    public final Long getInternalID() {
        return objectiveID;
    }

    /**
     * Sets the internal id.
     * 
     * @param value the new internal id
     */
    public final void setInternalID(final Long value) {
        this.objectiveID = value;
    }

     /**
      * Gets the parent id.
      * 
      * @return the parent id
      */
     public final long getParentID() {
        return parentID;
    }

    /**
     * Sets the parent id.
     * 
     * @param value the new parent id
     */
    public final void setParentID(final long value) {
        this.parentID = value;
    }

    /**
     * Gets the project id.
     * 
     * @return the project id
     */
    public final long getProjectID() {
        return projectID;
    }

    /**
     * Sets the project id.
     * 
     * @param value the new project id
     */
    public final void setProjectID(final long value) {
        this.projectID = value;
    }

    /**
     * Gets the risk context.
     * 
     * @return the risk context
     */
    public final String getRiskContext() {
        return projectTitle;
    }

    /**
     * Sets the risk context.
     * 
     * @param value the new risk context
     */
    public final void setRiskContext(final String value) {
        this.projectTitle = value;
    }
}
