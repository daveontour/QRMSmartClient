package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.DTOSmartClientMenuItem;
import au.com.quaysystems.qrm.dto.ModelQRMReport;

@SuppressWarnings("serial")
@WebServlet (value = "/getProjectReports", asyncSupported = false)
public class ServletGetProjectReports extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		ArrayList<DTOSmartClientMenuItem> projreports = new ArrayList<DTOSmartClientMenuItem>();
		for (ModelQRMReport rep : getQRMReportsContext(sess)) {
			DTOSmartClientMenuItem sr2 = new DTOSmartClientMenuItem(rep.reportName,"icons/16/icon-pdf.png", rep.getInternalID());
			if (rep.isExcelOnlyFormat()){
				sr2.setClick("if (!checkTableSelection()) return;setBaseReportValues('" + rep.getInternalID()+ "');reportParamMap.put('format' , 'MS Excel');qrmSubmitBackGroundReport();");
			} else {
				sr2.setClick("if (!checkTableSelection()) return;setBaseReportValues('" + rep.getInternalID()+ "');preProcessReportFormat();");
			}
			projreports.add(sr2);
		}
		// Two specific items to add risk export capability
		DTOSmartClientMenuItem ex = new DTOSmartClientMenuItem("Export Risks (Excel)","icons/16/export1.png", QRMConstants.EXPORTRISKSEXCEL);
		ex.setClick("if (!checkTableSelection()) return;setBaseReportValues('" + QRMConstants.EXPORTRISKSEXCEL+ "');qrmSubmitBackGroundReport();");
		projreports.add(ex);
		DTOSmartClientMenuItem ex1 = new DTOSmartClientMenuItem("Export Risks (XML)","icons/16/export1.png", QRMConstants.EXPORTRISKSXML);
		ex1.setClick("if (!checkTableSelection()) return;setBaseReportValues('" + QRMConstants.EXPORTRISKSXML+ "');qrmSubmitBackGroundReport();");
		projreports.add(ex1);


		outputJSON(projreports,response);

	}
}
