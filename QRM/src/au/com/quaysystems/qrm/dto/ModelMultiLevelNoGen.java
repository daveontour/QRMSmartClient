/*
 * 
 */
package au.com.quaysystems.qrm.dto;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelMultiLevel.
 */
@Entity
@Table(name="riskcategory")
public class ModelMultiLevelNoGen  implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5902882926719654140L;
	
	/** The context id. */
	public Long contextID;
    
    /** The description. */
    public String description;
    
     /** The internal id. */
    @Id
    @GeneratedValue
    public Long internalID;
    
    /** The parent id. */
    public Long parentID;
    
    /** The sec. */
    @Transient
    public ArrayList<ModelMultiLevelNoGen> sec;
    
    /**
     * Instantiates a new model multi level.
     */
    public ModelMultiLevelNoGen(){}

	public void setDescription(String string) {
		this.description = string;
		
	}
	
}
