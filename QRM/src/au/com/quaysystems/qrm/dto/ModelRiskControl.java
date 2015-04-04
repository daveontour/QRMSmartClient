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
	@NamedNativeQuery(name = "getRiskRiskControls",resultClass = ModelRiskControl.class, query = "SELECT * FROM riskcontrols  WHERE riskID = :riskid"),
	@NamedNativeQuery(name = "getRiskControls", query = "SELECT * FROM riskcontrols  WHERE riskID = :riskid", resultClass = ModelRiskControl.class)
})

@Entity
@Table(name = "riskcontrols")
public class ModelRiskControl implements Serializable {

	private static final long serialVersionUID = 3697721929125785254L;

	public String contribution;
	public String control;
	public int effectiveness;
	@Id
	@GeneratedValue
	public long internalID;
	protected long riskID;

	public ModelRiskControl() {
	}
	public final String getContribution() {
		return contribution;
	}
	public final void setContribution(final String contribution) {
		this.contribution = contribution;
	}
	public final String getControl() {
		return control;
	}
	public final void setControl(final String control) {
		this.control = control;
	}
	public final int getEffectiveness() {
		return effectiveness;
	}
	public final void setEffectiveness(final int effectiveness) {
		this.effectiveness = effectiveness;
	}
	public final long getInternalID() {
		return internalID;
	}
	public final void setInternalID(final long internalID) {
		this.internalID = internalID;
	}
	public final long getRiskID() {
		return riskID;
	}
	public final void setRiskID(final long riskID) {
		this.riskID = riskID;
	}

}
