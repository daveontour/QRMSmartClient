package au.com.quaysystems.qrm.dto;

import javax.persistence.Entity;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name="risk")
public class ModelRiskLiteRelationship  extends IModelRiskLite {	
	public Long  relationshipID;
}
