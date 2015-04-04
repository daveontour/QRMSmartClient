package au.com.quaysystems.qrm.server.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelDataObjectAllocation;
import au.com.quaysystems.qrm.dto.ModelToleranceMatrix;
import au.com.quaysystems.qrm.server.MatrixPainter;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.QRMTXManager;

public class ToleranceAllocationReportVisitor extends IReportProcessorVisitor {

	@Override
	public void process(ReportProcessorData job, HashMap<Object, Object> taskParamMap, Session sess, Long reportSessionID,QRMTXManager txmgr) {

		ByteArrayOutputStream array = new ByteArrayOutputStream();

		boolean descendants = (Boolean) taskParamMap.get("descendants");
		long projectID = (Long)taskParamMap.get("projectID");
		ModelToleranceMatrix mat =txmgr.getProjectMatrix(projectID,sess);

		ArrayList<ModelDataObjectAllocation> summaries = txmgr.getToleranceAllocations(sess, false);

		int lft = 0, rgt = 0;

		if (descendants){

			Connection conn = PersistenceUtil.getConnection(job.jdbcURL);

			try {
				PreparedStatement ps = conn.prepareStatement("SELECT lft, rgt FROM riskproject WHERE projectID = ?");
				ps.setLong(1, projectID);

				ResultSet rs = ps.executeQuery();
				rs.first();

				lft = rs.getInt("lft");
				rgt = rs.getInt("rgt");

			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
				return;
			} finally {
				try {
					conn.close();
				} catch (SQLException e) {
					log.error("QRM Stack Trace", e);
				}
			}

		}
		try {
			ImageIO.write(MatrixPainter.getAllocationMatrixWithAxisLabels(mat, 300, 300, descendants, 1, projectID, summaries, lft,rgt),"png",	array);
		} catch (IOException e) {
			log.error("QRM Stack Trace", e);
		}

		taskParamMap.put("ALLOCTREATED", array.toByteArray());

		array = new ByteArrayOutputStream();

		try {
			ImageIO.write(MatrixPainter.getAllocationMatrixWithAxisLabels(mat, 300, 300,	descendants, 0, projectID, summaries, lft, rgt),"png", array);
		} catch (IOException e) {
			log.error("QRM Stack Trace", e);
		}

		taskParamMap.put("ALLOCUNTREATED", array.toByteArray());

		array = new ByteArrayOutputStream();

		try {
			ImageIO.write(MatrixPainter.getAllocationMatrix(mat, 300, 300,	descendants, 3, projectID, summaries, lft, rgt),"png", array);
		} catch (IOException e) {
			log.error("QRM Stack Trace", e);
		}
		taskParamMap.put("ALLOCCURRENT", array.toByteArray());
	}
}
