/*
 * 
 */
package au.com.quaysystems.qrm.dto;

import java.io.Serializable;


// TODO: Auto-generated Javadoc
/**
 * The Class ModelControlEffectiveness.
 */
public class ModelControlEffectiveness  implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 587524541205122113L;
	
	/** The description. */
	public String description;
    
    /** The title. */
    public String title;
    
    /** The rank. */
    public int rank;
    
    /** The controlEffectivenessID. */
    public long controlEffectivenessID;
    
    /**
     * Instantiates a new model control effectiveness.
     */
    public ModelControlEffectiveness(){}
	
	/**
	 * Gets the control description.
	 * 
	 * @return the controlDescription
	 */
	public final String getControlDescription() {
		return description;
	}
	
	/**
	 * Sets the control description.
	 * 
	 * @param controlDescription the controlDescription to set
	 */
	public final void setControlDescription(final String controlDescription) {
		this.description = controlDescription;
	}
	
	/**
	 * Gets the control title.
	 * 
	 * @return the controlTitle
	 */
	public final String getControlTitle() {
		return title;
	}
	
	/**
	 * Sets the control title.
	 * 
	 * @param controlTitle the controlTitle to set
	 */
	public final void setControlTitle(final String controlTitle) {
		this.title = controlTitle;
	}
	
	/**
	 * Gets the effectiveness rank.
	 * 
	 * @return the effectivenessRank
	 */
	public final int getEffectivenessRank() {
		return rank;
	}
	
	/**
	 * Sets the effectiveness rank.
	 * 
	 * @param effectivenessRank the effectivenessRank to set
	 */
	public final void setEffectivenessRank(final int effectivenessRank) {
		this.rank = effectivenessRank;
	}
	
	/**
	 * Gets the internal id.
	 * 
	 * @return the internalID
	 */
	public final long getInternalID() {
		return controlEffectivenessID;
	}
	
	/**
	 * Sets the internal id.
	 * 
	 * @param internalID the internalID to set
	 */
	public final void setInternalID(final long internalID) {
		this.controlEffectivenessID = internalID;
	}
    
    

}