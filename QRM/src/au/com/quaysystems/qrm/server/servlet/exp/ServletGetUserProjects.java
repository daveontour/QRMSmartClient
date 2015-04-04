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
import au.com.quaysystems.qrm.dto.DTOSmartClientMenuItem;
import au.com.quaysystems.qrm.dto.ModelQRMReport;
import au.com.quaysystems.qrm.dto.ModelRepository;
import au.com.quaysystems.qrm.server.PersistenceUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/getUserProjects", asyncSupported = false)
public class ServletGetUserProjects extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		Session sess2 = PersistenceUtil.getSimpleControlSession();
		sess2.setCacheMode(CacheMode.IGNORE);
		sess2.setFlushMode(FlushMode.ALWAYS);

		try {
			Object[] arr = new Object[9];
			arr[0] = getAllRiskProjectsForUserLite(userID,	sess);
			arr[1] = sess.getNamedQuery("getAllRepPersonLite").list();
			arr[2] = sess.getNamedQuery("getAllMatrix").list();
			arr[3] = sess.getNamedQuery("getAllCategorys").list();
			arr[4] = sess.getNamedQuery("getAllObjectives").list();
			arr[5] = getActionMenu(sess);
			arr[6] = sess.getNamedQuery("getAllQuantTypes").list();
			arr[7] = getPerson(userID, sess);
			arr[8] = ((ModelRepository)(sess2.get(ModelRepository.class, getRepositoryID((String)request.getSession().getAttribute("session.url"))))).repLogonMessage;

			outputJSON2B(arr,response);
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} finally {
			sess2.close();
		}

	}
	
	protected final Object getActionMenu(final Session sess) {

		/*
		 * Not ideal philosophically, but this create Javascript based on the
		 * available reports which is injected into the report menu
		 */
		ArrayList<DTOSmartClientMenuItem> riskreports = new ArrayList<DTOSmartClientMenuItem>();
		ArrayList<DTOSmartClientMenuItem> projectRiskreports = new ArrayList<DTOSmartClientMenuItem>();
		for (ModelQRMReport rep : getQRMReportsRisk(sess)) {
			riskreports.add(new DTOSmartClientMenuItem(rep.reportName,rep.getInternalID(),true, false, rep.isDetailConfigWindow()));
			projectRiskreports.add(new DTOSmartClientMenuItem(rep.reportName,rep.getInternalID(),false, true, rep.isDetailConfigWindow()));
		}

		ArrayList<DTOSmartClientMenuItem> regreports = new ArrayList<DTOSmartClientMenuItem>();
		for (ModelQRMReport rep : getQRMReportsRegister(sess)) {
			DTOSmartClientMenuItem sr2 = new DTOSmartClientMenuItem(rep.reportName,"icons/16/icon-pdf.png", rep.getInternalID());
			sr2.setClick("eval('var rep = {checkTableSelection:true, noSelectWindow:true,baseValues:true, reportID:"+ rep.getInternalID()+ ", registerReport:true, submit:true}');qoReportEngine.flowController(rep)");
			regreports.add(sr2);
		}

		ArrayList<DTOSmartClientMenuItem> projreports = new ArrayList<DTOSmartClientMenuItem>();
		for (ModelQRMReport rep : getQRMReportsContext(sess)) {
			projreports.add(new DTOSmartClientMenuItem(rep.reportName, rep.getInternalID(),rep.isExcelOnlyFormat()));
		}
		
		// Two specific items to add risk export capability
		projreports.add(new DTOSmartClientMenuItem("Export Risks (Excel)", QRMConstants.EXPORTRISKSEXCEL,false));
		projreports.add(new DTOSmartClientMenuItem("Export Risks (XML)", QRMConstants.EXPORTRISKSXML, false));

		ArrayList<DTOSmartClientMenuItem> incidentreports = new ArrayList<DTOSmartClientMenuItem>();
		for (ModelQRMReport rep : getQRMReportsIncident(sess)) {
			incidentreports.add(new DTOSmartClientMenuItem(rep.reportName,rep.getInternalID(), rep.isExcelOnlyFormat()));
		}

		ArrayList<DTOSmartClientMenuItem> reviewreports = new ArrayList<DTOSmartClientMenuItem>();
		for (ModelQRMReport rep : getQRMReportsReview(sess)) {
			reviewreports.add(new DTOSmartClientMenuItem(rep.reportName,rep.getInternalID(), rep.isExcelOnlyFormat()));
		}

		ArrayList<DTOSmartClientMenuItem> repositoryreports = new ArrayList<DTOSmartClientMenuItem>();
		for (ModelQRMReport rep : getQRMReportsRepository(sess)) {
			repositoryreports.add(new DTOSmartClientMenuItem(rep.reportName,rep.getInternalID(), rep.isExcelOnlyFormat()));
		}

		Object rm1 = new DTOSmartClientMenuItem("qoCurrent.PaneNum == 0 && (typeof(project) != \"string\")","menuRegisterReports", "Register Reports", "icons/16/icon-pdf.png", true, false, regreports.toArray(new DTOSmartClientMenuItem[] {}));
		Object rm2 = new DTOSmartClientMenuItem("(typeof(project) != \"string\")","menuProjectReports", "Project Reports",  "icons/16/icon-pdf.png", true, false, projreports.toArray(new DTOSmartClientMenuItem[] {}));
		Object rm3 = new DTOSmartClientMenuItem("RiskTable.getSelection().length > 0 && qoCurrent.PaneNum == 0  && (typeof(project) != \"string\")", "menuRiskReports", "Risk Reports", "icons/16/icon-pdf.png", true, false,	riskreports.toArray(new DTOSmartClientMenuItem[] {}));
		Object rm4 = new DTOSmartClientMenuItem("true", "menuReviewReports", "Review Reports", "icons/16/icon-pdf.png", true, false, reviewreports.toArray(new DTOSmartClientMenuItem[] {}));
		Object rm5 = new DTOSmartClientMenuItem("true", "menuIncidentReports", "Incident Reports", "icons/16/icon-pdf.png", true, false, incidentreports.toArray(new DTOSmartClientMenuItem[] {}));
		Object rm6 = new DTOSmartClientMenuItem("true",	"menuRepositoryReports", "Repository Reports", "icons/16/icon-pdf.png", true, false, repositoryreports.toArray(new DTOSmartClientMenuItem[] {}));


		return new Object[] { rm1, rm2, rm3, rm4, rm5, rm6, projectRiskreports.toArray(new DTOSmartClientMenuItem[] {})  };
	}

}
