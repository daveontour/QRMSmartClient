package au.com.quaysystems.qrm.server.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelAnalConfig;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.QRMTXManager;
import au.com.quaysystems.qrm.server.analysis.ChartProcessor;

public class AnalysisReportVisitor extends IReportProcessorVisitor {

	@SuppressWarnings( { "rawtypes", "unchecked" })
	@Override
	public void process(ReportProcessorData job, HashMap<Object, Object> taskParamMap, Session sess, Long reportSessionID,QRMTXManager txmgr) {

		String url = job.jdbcURL;
		
		long projectID = (Long)taskParamMap.get("projectID");
		Connection conn = PersistenceUtil.getConnection(job.jdbcURL);

		String[] clazzs = null;
		String[] param1s = null; 

		ArrayList<ModelAnalConfig> tools = new ArrayList<ModelAnalConfig>(sess.createCriteria(ModelAnalConfig.class).list());

		if (taskParamMap.get("class") != null && taskParamMap.get("param1")!= null){
			clazzs = ((String)taskParamMap.get("class")).split(":");
			param1s = ((String)taskParamMap.get("param1")).split(":");
		} else {
			taskParamMap.put("x", "730");
			taskParamMap.put("y", "600");
			taskParamMap.put("num", 30);
			taskParamMap.put("b3D", false);
			taskParamMap.put("bReverse", false);
			taskParamMap.put("bEx", true);
			taskParamMap.put("bHigh", true);
			taskParamMap.put("bMod", true);
			taskParamMap.put("bSig", true);
			taskParamMap.put("bLow", true);

			clazzs = new String[tools.size()];
			param1s = new String[tools.size()];
			for(int i = 0; i< tools.size(); i++){
				clazzs[i] = tools.get(i).clazz;
				param1s[i] = tools.get(i).param1;
			}
		}

		// For each chart, produce the graph and put it in the database for the report
		try {
			for (int i = 0; i< clazzs.length; i++){

				String clazzStr = clazzs[i];
				String param1Str = param1s[i];

				try {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					ChartProcessor processor = null;
					try {
						Class clazz = Class.forName(clazzStr);
						processor = (ChartProcessor) clazz.newInstance();
					} catch (Exception e1) {}

					taskParamMap.put("param1", param1Str);
					processor.process(out, taskParamMap, conn, null, null, null, sess, projectID, false);


					ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
					if (conn.isClosed()){
						conn = PersistenceUtil.getConnection(url);
					}
					conn.setAutoCommit(true);

					try {
						for(ModelAnalConfig tool:tools){
							// Find the title of the tool and place it and the image into the table for the report to get
							if (tool.clazz.contains(clazzStr) && tool.param1.contains(param1Str)){
								PreparedStatement pstmt = conn.prepareStatement("INSERT INTO reportsessiondata (sessionID ,dataBlob, dataString) VALUES (?,?,?)");
								pstmt.setLong(1, reportSessionID);
								pstmt.setBinaryStream(2, in,  in.available());
								pstmt.setString(3, tool.title);
								pstmt.executeUpdate();	
								
								break;							
							}
						}
					} catch (SQLException e) {
						log.error("QRM Stack Trace", e);
					}
				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
				}
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("QRM Stack Trace", e);
			}			
		}
	}
}
