package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelQRMReport;
import au.com.quaysystems.qrm.server.PersistenceUtil;


@WebServlet (value = "/QRMReportUpload/*", asyncSupported = false)
public class ServletReportUploadManager extends HttpServlet {

	private static final long serialVersionUID = 1387308835754413189L;
	protected final ThreadLocal<HttpServletRequest> perThreadRequest = new ThreadLocal<HttpServletRequest>();
	protected final ThreadLocal<HttpServletResponse> perThreadResponse = new ThreadLocal<HttpServletResponse>();
	protected final ThreadLocal<Session> perThreadSess = new ThreadLocal<Session>();
	private static Logger log = Logger.getLogger("au.com.quaysystems.qrm");


	@Override
	public final void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		handleRequest(request, response);
	}

	@Override
	public final void doGet(final HttpServletRequest request,final HttpServletResponse response) throws ServletException,
			IOException {
		handleRequest(request, response);
	}

	@Override
	public final void init() {
		log.info("Report Upload Manager Processor Started");
	}

	public final void handleRequest(final HttpServletRequest request, final HttpServletResponse response) {

		if (request.getParameter("saveReport") != null) {
			perThreadRequest.set(request);
			perThreadResponse.set(response);

			try {
				perThreadSess.set(PersistenceUtil.getSession(
						(String) request.getSession().getAttribute("session.url")));
			} catch (Exception e2) {
				log.error("QRM Stack Trace", e2);
				perThreadSess.set(null);
			}
			saveReport(request, response);
		}
	}

	@SuppressWarnings("unchecked")
	private void saveReport(final HttpServletRequest request,
			final HttpServletResponse response) {

		HashMap<String, String> map = new HashMap<String, String>();

		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setFileCleaningTracker(new FileCleaningTracker());
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(16777216L);

		try {

			InputStream uploadedStream = null;
			String fileName = null;

			for (FileItem item : (List<FileItem>) upload.parseRequest(request)) {
				if (item.isFormField()) {
					map.put(item.getFieldName(), item.getString());
				} else {
					fileName = item.getName();
					uploadedStream = item.getInputStream();
				}
			}

			if (fileName.endsWith("zip")) {
				processZipFile(uploadedStream);
			} else {
				storeReport(uploadedStream, fileName);
			}

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

		try {
			ServletOutputStream out = response.getOutputStream();
			out.println("<html><body><script>" + map.get("jsToReturn")
					+ "</script></body></html>");
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("QRM Stack Trace", e);
		}
	}

	@SuppressWarnings( "rawtypes")
	private void processZipFile(final InputStream inputStream) {
		try {
			File file = new File("outFile.java");
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = inputStream.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			inputStream.close();

			ZipFile zf = null;
			try {
				zf = new ZipFile(file);
				Enumeration entries = zf.entries();

				while (entries.hasMoreElements()) {
					ZipEntry ze = (ZipEntry) entries.nextElement();
					long size = ze.getSize();
					if (size > 0) {
						if (!storeReport(zf.getInputStream(ze), ze.getName())) {
						}
					}
				}
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			} finally {
				try {
					zf.close();
				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
				}
			}
		} catch (IOException e) {
		}

	}

	private boolean storeReport(final InputStream inputStream,	final String reportFileName) {

		ModelQRMReport report = new ModelQRMReport();

		report.coreFile = false;
		report.template = false;

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			DataInputStream is2 = new DataInputStream(inputStream);
			while (true) {
				os.write(is2.readByte());
			}
		} catch (Exception eof) {
		}

		report.bodyText = os.toString();

		// Extract the meta information out of the report content

		String start = "<html-property name=\"description\">";
		String end = "</html-property>";
		String configStr;
		try {
			configStr = report.bodyText.substring(report.bodyText.indexOf(start)+ start.length(), report.bodyText.indexOf(end));
		} catch (RuntimeException e1) {
			return false;
		}

		HashMap<String, String> config = new HashMap<String, String>();
		String[] params = configStr.split(";");
		for (String param : params) {
			String[] pair = param.split(":");
			config.put(pair[0], pair[1]);
		}
		report.reportName = config.get("QRMReportName");
		if (report.reportName == null) {
			report.reportName = "Report Name";
		}

		report.reportDescription = config.get("QRMReportDescription");
		if (report.reportDescription == null) {
			report.reportDescription = "Report Description";
		}

		report.reporttype = config.get("QRMReportType");
		if (report.reporttype == null) {
			report.reporttype = "REGISTER";
		}
		
		report.visitor = config.get("QRMReportMask");
		
		// Optional set the internal ID of the report to allow finer control
		// i.e. reports with internalID between -10000 and -200000 throw up
		// detail risk configuration window
		Long internalID;
		try {
			internalID = Long.parseLong(config.get("QRMReportInternalID"));
		} catch (Exception e2) {
			internalID = null;
		}

		try {
			report.setDetailConfigWindow(Boolean.parseBoolean(config.get("QRMRiskDetailConfigWindow")));
		} catch (Exception e2) {
			report.setDetailConfigWindow(false);
		}
		
		try {
			report.setExcelOnlyFormat(Boolean.parseBoolean(config.get("QRMExcelOnlyFormat")));
		} catch (Exception e2) {
			report.setExcelOnlyFormat(false);
		}
		ByteArrayInputStream is = null;
		Connection conn = null;
		try {
			conn = PersistenceUtil.getConnection((String) perThreadRequest.get().getSession().getAttribute("session.url"));
			conn.setAutoCommit(false);

			is = new ByteArrayInputStream(report.bodyText.getBytes());

			String sql = "INSERT INTO reports (bodyText,reportDescription,reportName,reporttype, coreFile, reportFileName, visitor, detailConfigWindow, excelOnlyFormat ) VALUES (?,?,?,?,?,?,?,?,?)";
			if (internalID != null){
				sql = "INSERT INTO reports (bodyText,reportDescription,reportName,reporttype, coreFile, reportFileName, visitor, detailConfigWindow, excelOnlyFormat, internalID ) VALUES (?,?,?,?,?,?,?,?,?,?)";
			}
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setBinaryStream(1, is, is.available());
			stmt.setString(2, report.reportDescription);
			stmt.setString(3, report.reportName);
			stmt.setString(4, report.reporttype);
			stmt.setBoolean(5, false);
			stmt.setString(6, reportFileName);
			stmt.setString(7, report.visitor);
			stmt.setBoolean(8, report.isDetailConfigWindow());
			stmt.setBoolean(9, report.isExcelOnlyFormat());
			if (internalID != null){
				stmt.setLong(10, internalID);
			}
			stmt.executeUpdate();
			conn.commit();

			// Remove the session factory so it is recreated next time it is
			// requested so the the Hibernate environment is synced up with the
			// database after the JDBC calls
			PersistenceUtil.removeSF((String) perThreadRequest.get().getSession().getAttribute("session.url"));

			return true;
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			try {
				conn.rollback();
			} catch (SQLException e1) {
				log.error("QRM Stack Trace", e1);
			}
			return false;
		} finally {
			try {
				is.close();
				conn.close();
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}
		}
	}
}
