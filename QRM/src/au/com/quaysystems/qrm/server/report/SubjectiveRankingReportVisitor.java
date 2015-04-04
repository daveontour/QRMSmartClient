package au.com.quaysystems.qrm.server.report;

import java.sql.Connection;
import java.util.HashMap;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelRiskLite;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.QRMTXManager;

public class SubjectiveRankingReportVisitor extends IReportProcessorVisitor {

	@Override
	public void process(ReportProcessorData job, HashMap<Object, Object> taskParamMap, Session sess, Long reportSessionID,QRMTXManager txmgr) {
		int rank = 0;

		try (Connection conn = PersistenceUtil.getConnection(job.jdbcURL)){

			for (ModelRiskLite risk: txmgr.getAllProjectRisksLite(job.userID, (Long)taskParamMap.get("projectID"), (Boolean) taskParamMap.get("descendants"), sess)) {
				setReportSessionData(reportSessionID, "RISK",  risk.riskID, null, rank++, conn);
			}

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
	}
}
