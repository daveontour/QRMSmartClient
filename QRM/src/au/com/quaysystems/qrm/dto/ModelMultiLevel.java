/*
 * 
 */
package au.com.quaysystems.qrm.dto;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

@NamedNativeQueries( {
	@NamedNativeQuery(name = "getAllCategorys", query = "SELECT *, 0 as generation, 'title' AS projectTitle FROM riskcategory", resultClass = ModelMultiLevel.class),
	@NamedNativeQuery(name = "getProjectCategorys", callable = true, query = "call getProjectCategories(:projectID )", resultClass = ModelMultiLevel.class)
})
@Entity
@Table(name="riskcategory")
public class ModelMultiLevel  implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5902882926719654140L;
	
	/** The context id. */
	public Long contextID;
    
    /** The description. */
    public String description;
    
    /** The generation. */
    @Column(updatable=false, insertable=false)
    public Integer generation;
    
    /** The internal id. */
    @Id
    @GeneratedValue
    public Long internalID;
    
    /** The parent id. */
    public Long parentID;
    
    /** The sec. */
    @Transient
    public ArrayList<ModelMultiLevel> sec;
    
    /**
     * Instantiates a new model multi level.
     */
    public ModelMultiLevel(){}
	
	/**
	 * Gets the context id.
	 * 
	 * @return the contextID
	 */
	public final Long getContextID() {
		return contextID;
	}
	
	/**
	 * Sets the context id.
	 * 
	 * @param contextID the contextID to set
	 */
	public final void setContextID(final Long contextID) {
		this.contextID = contextID;
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
	 * Used so the same call can be used as for ModelPErson in Aalysis
	 * @return
	 */
	public final String getName() {
		return description;
	}	
	/**
	 * Sets the description.
	 * 
	 * @param description the description to set
	 */
	public final void setDescription(final String description) {
		this.description = description;
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
	 * @param generation the generation to set
	 */
	public final void setGeneration(final int generation) {
		this.generation = generation;
	}
	
	/**
	 * Gets the internal id.
	 * 
	 * @return the internalID
	 */
	public final Long getInternalID() {
		return internalID;
	}
	
	/**
	 * Sets the internal id.
	 * 
	 * @param internalID the internalID to set
	 */
	public final void setInternalID(final Long internalID) {
		this.internalID = internalID;
	}
	
	/**
	 * Gets the parent id.
	 * 
	 * @return the parentID
	 */
	public final Long getParentID() {
		return parentID;
	}
	
	/**
	 * Sets the parent id.
	 * 
	 * @param parentID the parentID to set
	 */
	public final void setParentID(final Long parentID) {
		this.parentID = parentID;
	}
	
	/**
	 * Gets the sec.
	 * 
	 * @return the sec
	 */
	public final ArrayList<ModelMultiLevel> getSec() {
		if (sec == null){
			sec = new ArrayList<ModelMultiLevel>();
		}
		return sec;
	}
	
	/**
	 * Sets the sec.
	 * 
	 * @param sec the sec to set
	 */
	public final void setSec(final ArrayList<ModelMultiLevel> sec) {
		this.sec = sec;
	}
}
