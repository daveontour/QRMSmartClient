package au.com.quaysystems.qrm.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name="risk")
public class ModelRiskLiteExt  extends IModelRiskLite {
	
	@Column(name="fromProjCode")
	public String fromProjCode;
	
	@Column(name="toProjCode")
	public String toProjCode;
	
	@Column(name="promotedProjectID")
	public Long promotedProjectID;
	
	@Column(name="contextRank")
	public Long subjectiveRank;
	
//	@Column(name="parentRiskProjectCode")
//	public String parentRiskProjectCode;
	
	@Transient
	public String promotionCode;

	public Long getSubjectiveRank() {
		return subjectiveRank;
	}

	public void setSubjectiveRank(Long subjectiveRank) {
		this.subjectiveRank = subjectiveRank;
	}
}
