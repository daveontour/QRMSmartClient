package au.com.quaysystems.qrm.server.report;

import java.sql.Connection;
import java.util.HashMap;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelRiskLite;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.QRMTXManager;

public class RiskReportVisitor extends IReportProcessorVisitor {

	@Override
	public void process(ReportProcessorData job, HashMap<Object, Object> taskParamMap, Session sess, Long reportSessionID,QRMTXManager txmgr) {
		
		try(Connection conn = PersistenceUtil.getConnection(job.jdbcURL)) {
			if (taskParamMap.containsKey("projectRisk")){
				String riskIDS = "";
				//The report was called as a 'project' report, so need to gather all the project risks
				int rank = 0;
				for (ModelRiskLite risk:txmgr.getAllProjectRisksLite((Long)taskParamMap.get("userID"), (Long)taskParamMap.get("projectID"), (Boolean)taskParamMap.get("descendants"), sess)){
					riskIDS = riskIDS+risk.riskID+",";
					setReportSessionData(reportSessionID, "RISK",  risk.riskID, null, rank++, conn);
				}
				taskParamMap.put("riskids", riskIDS);
			}

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} 
	}
}
