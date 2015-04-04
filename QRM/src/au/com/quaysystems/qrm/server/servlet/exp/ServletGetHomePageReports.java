package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.Session;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.DTOReportItem;
import au.com.quaysystems.qrm.dto.ModelQRMReport;
import au.com.quaysystems.qrm.server.PersistenceUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/getHomePageReports", asyncSupported = false)
public class ServletGetHomePageReports extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		Session sess2 = PersistenceUtil.getSimpleControlSession();
		sess2.setCacheMode(CacheMode.IGNORE);
		sess2.setFlushMode(FlushMode.ALWAYS);

		try {

			outputJSON2B(getHomePageReports(sess).toArray(new DTOReportItem[] {}),response);
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} finally {
			sess2.close();
		}

	}

	private final ArrayList<DTOReportItem> getHomePageReports(Session sess){

		ArrayList<DTOReportItem> projreports = new ArrayList<DTOReportItem>();
		
		projreports.add(new DTOReportItem("Export Risks (Excel)", QRMConstants.EXPORTRISKSEXCEL, "EXPORT"));
		projreports.add(new DTOReportItem("Export Risks (XML)", QRMConstants.EXPORTRISKSXML, "EXPORT"));


		for (ModelQRMReport rep : getQRMReportsContext(sess)) {
			DTOReportItem repItem = new DTOReportItem(rep.reportName,rep.getInternalID(), "PROJECT");
			if (rep.isExcelOnlyFormat()){
				repItem.excelOnly = true;
			} 
			projreports.add(repItem);
		}
		

		for (ModelQRMReport rep : getQRMReportsRegister(sess)) {
			DTOReportItem repItem = new DTOReportItem(rep.reportName, rep.getInternalID(), "RISK");
			repItem.projectRisk = true;
			projreports.add(repItem);
		}

		for (ModelQRMReport rep : getQRMReportsRisk(sess)) {
			DTOReportItem repItem = new DTOReportItem(rep.reportName,rep.getInternalID(), "RISK");
			repItem.projectRisk = true;

			if (rep.isDetailConfigWindow()){
				repItem.detailConfigWindow = true;
			} 
			projreports.add(repItem);
		}

		return projreports;
	}
}
