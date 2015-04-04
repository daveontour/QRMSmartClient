package au.com.quaysystems.qrm.server.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.hibernate.Session;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.ModelRiskLite;
import au.com.quaysystems.qrm.dto.ModelToleranceMatrix;
import au.com.quaysystems.qrm.server.MatrixPainter;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.QRMTXManager;

public class RelMatrixReportVisitor extends IReportProcessorVisitor {

	@Override
	public void process(ReportProcessorData job, HashMap<Object, Object> taskParamMap, Session sess, Long reportSessionID,QRMTXManager txmgr) {


		boolean descendants = (Boolean) taskParamMap.get("descendants");
		long projectID = (Long)taskParamMap.get("projectID");

		ArrayList<ModelRiskLite> risks = new ArrayList<ModelRiskLite>(txmgr.getAllProjectRisksLite(job.userID, projectID, descendants, sess));
		int rank = 0;
		
		try (Connection conn = PersistenceUtil.getConnection(job.jdbcURL)){
			for (ModelRiskLite risk: risks) {
				setReportSessionData(reportSessionID, "RISK",  risk.riskID, null, rank++, conn);
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

		ByteArrayOutputStream array = new ByteArrayOutputStream();
		ModelToleranceMatrix mat = (ModelToleranceMatrix) sess.get(ModelToleranceMatrix.class, risks.get(0).matrixID);

		int state = QRMConstants.CURRENT;


		try {
			if(taskParamMap.containsKey("inherentState") && (Boolean)taskParamMap.get("inherentState")){
				state = QRMConstants.INHERENT;
			}
			if(taskParamMap.containsKey("treatedState") && (Boolean)taskParamMap.get("treatedState")){
				state = QRMConstants.TREATED;
			}
		} catch (Exception e1) {
			state = QRMConstants.CURRENT;
		}


		try {
			ImageIO.write(MatrixPainter.getPNGRelMatrix(mat, 500, 500, risks, state),"png", array);
		} catch (IOException e) {
			log.error("QRM Stack Trace", e);
		}
		taskParamMap.put("QRMRELMATRIX", array.toByteArray());
		taskParamMap.put("RELMATRIXSTATE", state);
	}

}
