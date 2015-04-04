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

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

@NamedNativeQueries( {
	@NamedNativeQuery(name = "getProjectObjectives", callable = true, query = "call getProjectObjectives(:projectID )", resultClass = ModelObjective.class),
	@NamedNativeQuery(name = "getAllObjectives",resultClass = ModelObjective.class, query = "SELECT 0 AS GENERATION, objective.* FROM objective")
})
@Entity
@Table(name="objective")
public class ModelObjective  implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2275350167031132360L;
	
	/** The objective. */
	public String objective;
   
   /** The generation. */
   @Column(updatable=false, insertable=false)
    public int generation;
    
    /** The objectiveID. */
    @Id
    @GeneratedValue
    public Long objectiveID;
    public long parentID;
    public long projectID;


    /** The project title. */
    @Column(insertable=false, updatable=false)
	@Transient
    public String projectTitle;

    /**
     * Instantiates a new model objective.
     */
    public ModelObjective(){}
    
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
     * Gets the generation.
     * 
     * @return the generation
     */
    public final int getGeneration() {
        return generation;
    }

     /**
      * Sets the generation.
      * 
      * @param value the new generation
      */
     public final void setGeneration(final int value) {
        this.generation = value;
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
