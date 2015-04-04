package au.com.quaysystems.qrm.server.report;

import java.util.HashMap;

import org.hibernate.Session;

import au.com.quaysystems.qrm.server.QRMTXManager;


public class MonteReportVisitor extends IReportProcessorVisitor {
	public void process(ReportProcessorData job, HashMap<Object, Object> taskParamMap, Session sess, Long reportSessionID,QRMTXManager txmgr) {
		
	}
}
