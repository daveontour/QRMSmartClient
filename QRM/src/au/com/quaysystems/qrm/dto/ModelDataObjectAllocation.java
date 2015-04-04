/*
 * 
 */
package au.com.quaysystems.qrm.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

@NamedNativeQueries( {
	@NamedNativeQuery(name = "getAllocations", callable = true, query = "call getAllocations", resultClass = ModelDataObjectAllocation.class),
	@NamedNativeQuery(name = "getRolledAllocations", callable = true, query = "call getRolledAllocations", resultClass = ModelDataObjectAllocation.class)
})
@Entity
public class ModelDataObjectAllocation  implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7753153928941133245L;

	/** The id. */
	@Id
	long id;
	
	/** The PROB. */
	int PROB;
	
	/** The IMPACT. */
	int IMPACT;
	
	/** The RCOUNT. */
	int RCOUNT;
	
	/** The FLAG. */
	int FLAG;

	/** The matrixID. */
	long matrixID;
	
	/** The projectID. */
	long projectID;
	
	/** The lft. */
	long lft;
	
	/** The rgt. */
	long rgt;
	
	/**
	 * Instantiates a new model data object allocation.
	 */
	public ModelDataObjectAllocation() {}

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
	 * Gets the pROB.
	 * 
	 * @return the pROB
	 */
	public final int getPROB() {
		return PROB;
	}

	/**
	 * Sets the pROB.
	 * 
	 * @param prob the new pROB
	 */
	public final void setPROB(final int prob) {
		PROB = prob;
	}

	/**
	 * Gets the iMPACT.
	 * 
	 * @return the iMPACT
	 */
	public final int getIMPACT() {
		return IMPACT;
	}

	/**
	 * Sets the iMPACT.
	 * 
	 * @param impact the new iMPACT
	 */
	public final void setIMPACT(final int impact) {
		IMPACT = impact;
	}

	/**
	 * Gets the rCOUNT.
	 * 
	 * @return the rCOUNT
	 */
	public final int getRCOUNT() {
		return RCOUNT;
	}

	/**
	 * Sets the rCOUNT.
	 * 
	 * @param rcount the new rCOUNT
	 */
	public final void setRCOUNT(final int rcount) {
		RCOUNT = rcount;
	}

	/**
	 * Gets the fLAG.
	 * 
	 * @return the fLAG
	 */
	public final int getFLAG() {
		return FLAG;
	}

	/**
	 * Sets the fLAG.
	 * 
	 * @param flag the new fLAG
	 */
	public final void setFLAG(final int flag) {
		FLAG = flag;
	}

	/**
	 * Gets the matrixID.
	 * 
	 * @return the matrixID
	 */
	public final long getMatrixID() {
		return matrixID;
	}

	/**
	 * Sets the matrixID.
	 * 
	 * @param matrixID the new matrixID
	 */
	public final void setMatrixID(final long matrixID) {
		this.matrixID = matrixID;
	}

	/**
	 * Gets the projectID.
	 * 
	 * @return the projectID
	 */
	public final long getProjectID() {
		return projectID;
	}

	/**
	 * Sets the projectID.
	 * 
	 * @param projectID the new projectID
	 */
	public final void setProjectID(final long projectID) {
		this.projectID = projectID;
	}

	/**
	 * Gets the lft.
	 * 
	 * @return the lft
	 */
	public final long getLft() {
		return lft;
	}

	/**
	 * Sets the lft.
	 * 
	 * @param lft the new lft
	 */
	public final void setLft(final long lft) {
		this.lft = lft;
	}

	/**
	 * Gets the rgt.
	 * 
	 * @return the rgt
	 */
	public final long getRgt() {
		return rgt;
	}

	/**
	 * Sets the rgt.
	 * 
	 * @param rgt the new rgt
	 */
	public final void setRgt(final long rgt) {
		this.rgt = rgt;
	}


}
