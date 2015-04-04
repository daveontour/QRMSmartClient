package au.com.quaysystems.qrm.dto;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

@SuppressWarnings("serial")
@NamedNativeQueries( {
	@NamedNativeQuery(name = "getAllProjectRisk", callable = true, query = "call getAllProjectRisk( :var_user_id,:var_projectID,:var_descendants)", resultClass = ModelRisk.class),
	@NamedNativeQuery(name = "getRisk", callable = true, query = "call getRisk(:userID,:riskID,:projectID)", resultClass = ModelRisk.class),
	@NamedNativeQuery(name = "getRequestRisks", query = "SELECT jobdata.jobID, riskdetail.* FROM jobdata, riskdetail WHERE jobdata.jobID = :jobID AND jobdata.riskID = riskdetail.riskID", resultClass = ModelRisk.class)
})
@Entity
@Table(name="risk")
public class ModelRisk extends IModelRisk {}
