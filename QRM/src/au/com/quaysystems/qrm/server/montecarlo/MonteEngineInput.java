package au.com.quaysystems.qrm.server.montecarlo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import au.com.quaysystems.qrm.dto.ModelQuantImpactType;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskProject;

@SuppressWarnings("serial")
public class MonteEngineInput implements Serializable {	
	public List<ModelRisk> risks;
	public HashMap<Long, ModelQuantImpactType> impactTypeMap;
	public List<ModelQuantImpactType> impactTypes;
	public HashMap<Long, ModelRiskProject> projects;
	public Integer its;
	public Date startDate;
	public Date endDate;
	public Boolean riskActive;
	public Boolean conActive;
	
	public MonteEngineInput(List<ModelRisk> risks,
			HashMap<Long, ModelQuantImpactType> impactTypeMap,
			List<ModelQuantImpactType> impactTypes,
			HashMap<Long, ModelRiskProject> projects, Integer its,
			Date startDate, Date endDate, Boolean riskActive, Boolean conActive) {
		super();
		this.risks = risks;
		this.impactTypeMap = impactTypeMap;
		this.impactTypes = impactTypes;
		this.projects = projects;
		this.its = its;
		this.startDate = startDate;
		this.endDate = endDate;
		this.riskActive = riskActive;
		this.conActive = conActive;
	}
	
	
}
