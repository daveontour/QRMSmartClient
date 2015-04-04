package au.com.quaysystems.qrm.dto;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "incidentobjective")
public class ModelIncidentObjective implements Serializable {

	public ModelIncidentObjective(long incidentID, long objectiveID) {
		super();
		this.incidentID = incidentID;
		this.objectiveID = objectiveID;
	}
	@Id
	@GeneratedValue
	public long id;
	public long incidentID;
	public long objectiveID;
}
