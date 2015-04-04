package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectHeader;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.Task;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.mpx.MPXWriter;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.utility.NumberUtility;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import au.com.quaysystems.qrm.dto.ModelMitigationStep;
import au.com.quaysystems.qrm.dto.ModelPerson;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskConsequence;
import au.com.quaysystems.qrm.dto.ModelRiskControl;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.QRMTXManager;

import com.thoughtworks.xstream.XStream;

@WebServlet (value = "/QRMMSFormat/*", asyncSupported = false)
public class ServletExportMSFormats extends HttpServlet {

	protected JSONParser parser = new JSONParser();
	private static final long serialVersionUID = 1387308835754413189L;
	private static HSSFWorkbook wb;
	protected final QRMTXManager txmgr = new QRMTXManager();
	private static Logger log = Logger.getLogger("au.com.quaysystems.qrm");


	@Override
	public final void init() {
		log.info("Export MS Format Processor Started");
	}

	@Override
	public final void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		doPost(request, response);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected final void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {


		Session session = PersistenceUtil.getSession(
				(String) request.getSession().getAttribute("session.url"));

		ArrayList<ModelRisk> risks = new ArrayList<ModelRisk>();
		try {
			JSONArray data = (JSONArray) parser.parse(request.getParameter("DATA"));
			long rootProjectID = PersistenceUtil.getRootProjectID((String) request.getSession().getAttribute("session.url"));
			for (Object obj : data) {
				Long id = Long.parseLong(obj.toString());
				risks.add(txmgr.getRisk(id, 1L, rootProjectID, session));
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

		response.setHeader("Cache-Control", "no-cache");

//		if (request.getParameter("OPERATION").indexOf("EXCEL") != -1) {
//			exportAsExcel(response.getOutputStream(), risks, response);
//		}
		if (request.getParameter("OPERATION").indexOf("PROJECTMPX") != -1) {
			exportAsProject(response, risks, session.createCriteria(ModelPerson.class).list(), true);
		}
		if (request.getParameter("OPERATION").indexOf("PROJECTXML") != -1) {
			exportAsProject(response, risks, session.createCriteria(ModelPerson.class).list(), false);
		}
		if (request.getParameter("OPERATION").indexOf("XML") != -1) {
			exportAsXML(response.getOutputStream(), risks, response);
		}
		session.close();
	}

	public synchronized static void exportAsXML(final OutputStream out, final ArrayList<ModelRisk> risks, final HttpServletResponse response) {

		XStream xsXML = new XStream();
		xsXML.alias("risk", ModelRisk.class);
		xsXML.alias("control", ModelRiskControl.class);
		xsXML.alias("mitigationstep", ModelMitigationStep.class);
		xsXML.alias("riskconsequence", ModelRiskConsequence.class);
		xsXML.setMode(XStream.NO_REFERENCES);
		if (response != null){
			response.setContentType("text/xml");
			response.setHeader("Content-Disposition","attachment;filename=RiskExport.xml");
		}
		try {
			xsXML.toXML(risks, out);
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
	}

//	public synchronized static void exportAsExcel(final OutputStream out, final List<ModelRisk> risks, final HttpServletResponse response) {
//
//		wb = new HSSFWorkbook();
//		int cellIndex = 1;
//
//		HSSFFont font = wb.createFont();
//		font.setFontHeightInPoints((short) 10);
//		font.setFontName("Arial"); //$NON-NLS-1$
//		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//
//		HSSFSheet sheet0 = wb.createSheet("Risks"); //$NON-NLS-1$
//		HSSFRow row = sheet0.createRow(1);
//		HSSFCellStyle headStyle = wb.createCellStyle();
//		headStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
//		headStyle.setFont(font);
//		headStyle.setWrapText(true);
//
//		HSSFCell cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString("Risk ID"));
//		cell.setCellStyle(headStyle);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString("Title"));
//		cell.setCellStyle(headStyle);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString("Description"));
//		cell.setCellStyle(headStyle);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString("Consequences"));
//		cell.setCellStyle(headStyle);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString("Causes"));
//		cell.setCellStyle(headStyle);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellStyle(headStyle);
//		cell.setCellValue(new HSSFRichTextString("Mitigation Plan"));
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString("Start of Exposure"));
//		cell.setCellStyle(headStyle);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString("End of Exposure"));
//		cell.setCellStyle(headStyle);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString("Risk Owner"));
//		cell.setCellStyle(headStyle);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString("Risk Manager"));
//		cell.setCellStyle(headStyle);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString("Risk Manager 2"));
//		cell.setCellStyle(headStyle);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString("Risk Manager 3"));
//		cell.setCellStyle(headStyle);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString("Current Impact"));
//		cell.setCellStyle(headStyle);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString("Current Probability"));
//		cell.setCellStyle(headStyle);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString("Current Tolerance"));
//		cell.setCellStyle(headStyle);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue((new HSSFRichTextString("Primary Risk Category")));
//		cell.setCellStyle(headStyle);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellStyle(headStyle);
//		cell.setCellValue((new HSSFRichTextString("Controls in Place")));
//
//		HSSFCellStyle cellStyle = wb.createCellStyle();
//		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm")); //$NON-NLS-1$
//		cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
//		cellStyle.setAlignment(HSSFCellStyle.ALIGN_JUSTIFY);
//		cellStyle.setWrapText(true);
//	
//
//		int rowIndex0 = 3;
//		try {
//			for (ModelRisk risk : risks) {
//				row = sheet0.createRow(rowIndex0++);
//				writeExcelRisk(risk, wb, sheet0, row, cellStyle);
//			}
//		} catch (RuntimeException e) {
//			log.error("QRM Stack Trace", e);
//		}
//		
//		for (int i = 0; i < 20; i++) {
//			sheet0.autoSizeColumn(i);
//		}
//		sheet0.setColumnWidth(2,10000);
//		sheet0.setColumnWidth(3,10000);
//		sheet0.setColumnWidth(4,10000);
//		sheet0.setColumnWidth(5,10000);
//		sheet0.setColumnWidth(6,10000);
//		sheet0.setColumnWidth(17,10000);
//
//
//		try {
//			if (response != null ){
//				response.setHeader("Content-Disposition", "attachment;filename=RiskExport.xls");
//			}
//			wb.write(out);
//		} catch (IOException e) {
//			log.error("QRM Stack Trace", e);
//		}
//	}
//
//	public synchronized static final void writeExcelRisk(final ModelRisk risk,
//			final HSSFWorkbook wb, final HSSFSheet sheet0, final HSSFRow row,
//			final HSSFCellStyle style) {
//
//		int cellIndex = 1;
//		String str = "";
//
//		HSSFCell cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString(risk.getRiskID()));
//		cell.setCellStyle(style);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString(risk.getTitle()));
//		cell.setCellStyle(style);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString(risk.getDescription()));
//		cell.setCellStyle(style);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString(risk.getConsequences()));
//		cell.setCellStyle(style);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString(risk.getCause()));
//		cell.setCellStyle(style);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellStyle(style);
//		str = "";
//		for (ModelMitigationStep step : risk.getMitigationPlan()) {
//			str = str.concat(step.getStepDescription() + " - "
//					+ step.getPersonResponsible() + "\n");
//		}
//		cell.setCellValue((new HSSFRichTextString(str)));
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(risk.getBeginExposure());
//		HSSFCellStyle cellStyle = wb.createCellStyle();
//		cellStyle.setVerticalAlignment(style.getVerticalAlignment());
//		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("d-mmm-yy")); //$NON-NLS-1$
//		cell.setCellStyle(cellStyle);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(risk.getEndExposure());
//		cell.setCellStyle(cellStyle);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString(risk.getOwnerName()));
//		cell.setCellStyle(style);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString(risk.getManager1Name()));
//		cell.setCellStyle(style);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString(risk.getManager2Name()));
//		cell.setCellStyle(style);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(new HSSFRichTextString(risk.getManager3Name()));
//		cell.setCellStyle(style);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(risk.getCurrentImpact());
//		cellStyle = wb.createCellStyle();
//		cellStyle.setVerticalAlignment(style.getVerticalAlignment());
//
//		cell.setCellStyle(cellStyle);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(risk.getCurrentProb());
//		cell.setCellStyle(cellStyle);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue(risk.getCurrentTolerance());
//		cell.setCellStyle(cellStyle);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellValue((new HSSFRichTextString(risk.getPrimCatName())));
//		cell.setCellStyle(style);
//
//		cell = row.createCell(cellIndex++);
//		cell.setCellStyle(style);
//		str = "";
//		for (ModelRiskControl control : risk.getControls()) {
//			str = str.concat(control.getControl() + "\n");
//		}
//		cell.setCellValue((new HSSFRichTextString(str)));
//	}

	private void exportAsProject(final HttpServletResponse response, final List<ModelRisk> risks,
			final List<ModelPerson> resources, final boolean bMPX) {

		ProjectFile file = new ProjectFile();

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy"); //$NON-NLS-1$
		SimpleDateFormat tf = new SimpleDateFormat("H:m"); //$NON-NLS-1$

		file.setAutoTaskID(true);
		file.setAutoTaskUniqueID(true);
		file.setAutoResourceID(true);
		file.setAutoResourceUniqueID(true);
		file.setAutoOutlineLevel(true);
		file.setAutoOutlineNumber(true);
		file.setAutoWBS(true);
		file.setAutoCalendarUniqueID(true);

		ProjectCalendar calendar = null;

		try {
			calendar = file.addDefaultBaseCalendar();
		} catch (Exception e1) {
		}
		calendar.setWorkingDay(Day.SUNDAY, 1);
		calendar.setWorkingDay(Day.SATURDAY, 1);

		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(tf.parse("08:00"));
		} catch (java.text.ParseException e2) {
			// TODO Auto-generated catch block
			log.error("QRM Stack Trace", e2);
		}

		Calendar cal2 = Calendar.getInstance();
		try {
			cal2.setTime(tf.parse("16:00"));
		} catch (java.text.ParseException e2) {
			// TODO Auto-generated catch block
			log.error("QRM Stack Trace", e2);
		}

//		sunHours.addDateRange(new DateRange(cal.getTime(), cal2.getTime()));
//		satHours.addDateRange(new DateRange(cal.getTime(), cal2.getTime()));

		ArrayList<ModelRisk> riskList = new ArrayList<ModelRisk>();
		ArrayList<Task> taskList = new ArrayList<Task>();
		ArrayList<Double> taskListPercent = new ArrayList<Double>();

		ArrayList<ModelMitigationStep> stepList = new ArrayList<ModelMitigationStep>();
		ArrayList<Task> taskList2 = new ArrayList<Task>();

		Date earliest = null;
		try {
			earliest = df.parse("01/01/2030");
		} catch (java.text.ParseException e) {
			log.error("QRM Stack Trace", e);
		}

		if (risks != null) {
			for (ModelRisk risk : risks) {
				if ((risk.getBeginExposure()).before(earliest)) {
					earliest = (risk.getBeginExposure());
				}
				if (risk.getMitigationPlan() != null) {
					for (ModelMitigationStep step : risk.getMitigationPlan()) {
						if ((step.getStartDate()).before(earliest)) {
							earliest = (risk.getBeginExposure());
						}
					}
				}
			}
		}

		ProjectHeader header = file.getProjectHeader();
		header.setStartDate(earliest);

		//
		// Add resources
		//

		HashMap<Long, Resource> resourceMap = new HashMap<Long, Resource>();

		for (ModelPerson p : resources) {
			if (p != null) {
				try {
					Resource resource1 = file.addResource();
					resource1.setName(p.getName());
					resource1.setText4(p.getEmail());
					resourceMap.put(p.getStakeholderID(), resource1);
				} catch (RuntimeException e) {

				}
			}
		}

		ModelPerson unassignedPerson = new au.com.quaysystems.qrm.dto.ModelPerson();
		unassignedPerson.setName("Not Assigned"); //$NON-NLS-1$
		unassignedPerson.setEmail("-"); //$NON-NLS-1$

		Resource resource1 = file.addResource();
		resource1.setName(unassignedPerson.getName());
		resource1.setText4(unassignedPerson.getEmail());
		resourceMap.put(0L, resource1);

		Task riskSummary = file.addTask();
		riskSummary.setName("Project Risks"); //$NON-NLS-1$

		for (ModelRisk risk : risks) {

			Task task = riskSummary.addTask();
			task.setName(risk.getRiskID() + " - " + risk.getTitle()); //$NON-NLS-1$
			task.setText2(risk.getRiskID());

			// Add the exposure period as a sub task

			long end = (risk.getEndExposure()).getTime();
			long start = (risk.getBeginExposure()).getTime();
			long dur = end - start;
			long ldays = dur / (1000 * 60 * 60 * 24);
			int duration = (int) ldays;

			double percent = 0;

			Task exposurePeriod = task.addTask();
			exposurePeriod.setName("Exposure Period"); //$NON-NLS-1$
			exposurePeriod.setText2(risk.getRiskID());
			exposurePeriod.setDuration(Duration.getInstance(duration,
					TimeUnit.DAYS));
			exposurePeriod.setConstraintDate((risk.getBeginExposure()));
			exposurePeriod.setConstraintType(ConstraintType.MUST_START_ON);

			Date now = new Date();
			if (now.after((risk.getEndExposure()))) {
				exposurePeriod.setPercentageComplete(100);
				exposurePeriod.setActualStart((risk.getBeginExposure()));
			} else if (now.after((risk.getBeginExposure()))) {
				long x = now.getTime() - (risk.getBeginExposure()).getTime();
				long z = (risk.getEndExposure()).getTime()
				- (risk.getBeginExposure()).getTime();
				percent = (double) x / (double) z;
				exposurePeriod.setPercentageComplete(NumberUtility
						.getDouble(100 * percent));
				exposurePeriod.setActualStart((risk.getBeginExposure()));
			}
			riskList.add(risk);
			taskList.add(exposurePeriod);
			taskListPercent.add(percent);

				// Add each of the mitigation steps as a sub task of the
				// mitigation plan
				boolean mitPlanSet = false;
				Task mitTask = null;
				for (ModelMitigationStep step : risk.getMitigationPlan()) {
					if (step.response){
						continue;
					}
					if (!mitPlanSet){
						mitTask = task.addTask();
						mitTask.setName("Mitigation Plan"); //$NON-NLS-1$
						mitPlanSet = true;
					}
					Task subTask = mitTask.addTask();


					subTask.setName(step.getStepDescription());
					subTask.setDuration(Duration.getInstance(0,	TimeUnit.DAYS));
					subTask.setConstraintDate((step.getEndDate()));
					subTask.setConstraintType(ConstraintType.MUST_FINISH_ON);
					subTask.setCost(step.getEstimatedCost());
					subTask.setPercentageComplete(step.getPercentComplete());
					subTask.setText2(risk.getRiskID());
					subTask.setText3(step.getInternalID().toString());

					stepList.add(step);
					taskList2.add(subTask);
				}
			}
//		}

		for (int i = 0; i < riskList.size(); i++) {

			long end = (riskList.get(i).getEndExposure()).getTime();
			long start = (riskList.get(i).getBeginExposure()).getTime();
			long dur = end - start;
			long ldays = dur / (1000 * 60 * 60 * 24);
			int duration = (int) ldays;

			ModelRisk tempRisk = riskList.get(i);
			ResourceAssignment assOwn;
			ResourceAssignment assMgr;
			try {
				assOwn = taskList.get(i).addResourceAssignment(
						resourceMap.get(tempRisk.getOwnerID()));
				assMgr = taskList.get(i).addResourceAssignment(
						resourceMap.get(tempRisk.getManager1ID()));

				assOwn.setWork(Duration.getInstance(duration, TimeUnit.DAYS));
				assOwn.setActualWork(Duration.getInstance(duration
						* taskListPercent.get(i), TimeUnit.DAYS));
				assOwn.setRemainingWork(Duration.getInstance(duration
						- duration * taskListPercent.get(i), TimeUnit.DAYS));
				assOwn.setStart((riskList.get(i).getBeginExposure()));

				assMgr.setWork(Duration.getInstance(duration, TimeUnit.DAYS));
				assMgr.setActualWork(Duration.getInstance(duration
						* taskListPercent.get(i), TimeUnit.DAYS));
				assMgr.setRemainingWork(Duration.getInstance(duration
						- duration * taskListPercent.get(i), TimeUnit.DAYS));
				assMgr.setStart((riskList.get(i).getBeginExposure()));

			} catch (RuntimeException e) {
			}
		}

//		for (int i = 0; i < stepList.size(); i++) {
//
//			long end = (stepList.get(i).getEndDate()).getTime();
//			long start = (stepList.get(i).getStartDate()).getTime();
//			long dur = end - start;
//			long ldays = dur / (1000 * 60 * 60 * 24);
//			int duration = (int) ldays;
//
////			ResourceAssignment assOwn;
////			try {
////
////				Resource res;
////				try {
////					if (resourceMap.get(stepList.get(i)
////							.getPersonResponsibleID()) != null) {
////						res = resourceMap.get(stepList.get(i)
////								.getPersonResponsibleID());
////					} else {
////						res = resourceMap.get(0L);
////					}
////				} catch (RuntimeException e) {
////					res = resourceMap.get(0L);
////				}
////				assOwn = taskList2.get(i).addResourceAssignment(res);
////				assOwn.setWork(Duration.getInstance(duration, TimeUnit.DAYS));
////				assOwn.setActualWork(Duration.getInstance(duration
////						* stepList.get(i).getPercentComplete() / 100,
////						TimeUnit.DAYS));
////				assOwn.setRemainingWork(Duration.getInstance(
////						duration - duration
////						* stepList.get(i).getPercentComplete() / 100,
////						TimeUnit.DAYS));
////				assOwn.setStart((stepList.get(i).getStartDate()));
////
////			} catch (RuntimeException e) {
////
////			}
//
//		}

		try {
			if (bMPX) {
				response.setHeader("Content-Disposition","attachment;filename=Risk_Export_Project.mpx");
				new MPXWriter().write(file, response.getOutputStream());
			} else {
				response.setHeader("Content-Disposition","attachment;filename=Risk_Export_Project.xml");
				new MSPDIWriter().write(file, response.getOutputStream());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("QRM Stack Trace", e);
		}

	}
}
