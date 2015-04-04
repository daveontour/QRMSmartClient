package au.com.quaysystems.qrm.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "incidentrisk")
public class ModelIncidentRisk implements Serializable {

	public ModelIncidentRisk(long incidentID, long riskID) {
		super();
		this.incidentID = incidentID;
		this.riskID = riskID;
	}
	@Id
	@GeneratedValue
	public long id;
	public long incidentID;
	public long riskID;
}
