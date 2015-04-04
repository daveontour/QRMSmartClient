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
 * The Class ModelIncidentConsequence.
 */
@Entity
@Table(name="incidentconseq")
public class ModelIncidentConsequence  implements Serializable {

 	/** The Constant serialVersionUID. */
	 private static final long serialVersionUID = -3820617460543943375L;
	
	/** The id. */
	@Id
    @GeneratedValue
 	public Long id;
	
	/** The description. */
	public String description;
	
	/** The quant type. */
	@Transient
    public ModelQuantImpactType quantType;
    
    /** The type id. */
    public Long typeID;
    
    /** The value. */
    public Double value;
    
    /** The incident id. */
    public Long incidentID;
    
    /**
     * Instantiates a new model incident consequence.
     */
    public ModelIncidentConsequence(){}
    
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
	 * @param description the description to set
	 */
	public final void setDescription(final String description) {
		this.description = description;
	}
	
	/**
	 * Gets the quant type.
	 * 
	 * @return the quantType
	 */
	public final ModelQuantImpactType getQuantType() {
		return quantType;
	}
	
	/**
	 * Sets the quant type.
	 * 
	 * @param quantType the quantType to set
	 */
	public final void setQuantType(final ModelQuantImpactType quantType) {
		this.quantType = quantType;
	}
	
	/**
	 * Gets the type id.
	 * 
	 * @return the typeID
	 */
	public final Long getTypeID() {
		return typeID;
	}
	
	/**
	 * Sets the type id.
	 * 
	 * @param typeID the typeID to set
	 */
	public final void setTypeID(final Long typeID) {
		this.typeID = typeID;
	}
	
	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public final Double getValue() {
		return value;
	}
	
	/**
	 * Sets the value.
	 * 
	 * @param value the value to set
	 */
	public final void setValue(final Double value) {
		this.value = value;
	}
}
