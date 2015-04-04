package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;
import org.apache.log4j.Logger;

import au.com.quaysystems.qrm.server.PersistenceUtil;

@WebServlet (value = "/QRMAttachment/*", asyncSupported = false)
public class ServletAttachmentManager extends HttpServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1387308835754413189L;
	private static Logger log = Logger.getLogger("au.com.quaysystems.qrm");
	static final int BUFFER_SIZE = 64 * 1024;


	public final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
			IOException {
		handleRequest(request, response);
	}

	@Override
	public final void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		handleRequest(request, response);
	}
	@Override
	public final void init() {
		log.info("Attachment Manager Processor Started");
	}

	public final void handleRequest(final HttpServletRequest request, final HttpServletResponse response) {

		if (request.getParameter("getAttachment") != null) {
			getAttachment(request, response);
		}
		if (request.getParameter("saveAttachment") != null) {
			saveAttachment(request, response);
		}
		if (request.getParameter("getJobResult") != null) {
			getJobResult(request, response);
		}	
	}

	@SuppressWarnings("unchecked")
	private void saveAttachment(final HttpServletRequest request,	final HttpServletResponse response) {

		HashMap<String, String> map = new HashMap<String, String>();

		if (ServletFileUpload.isMultipartContent(request)) {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setFileCleaningTracker(new FileCleaningTracker());
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setSizeMax(16777216L);

			Connection conn = null;

			try {
				InputStream uploadedStream = null;
				int sizeInBytes = 0;
				String fileName = null;
				String contentType = null;

				for (FileItem item : (List<FileItem>) upload.parseRequest(request)) {
					if (item.isFormField()) {
						map.put(item.getFieldName(), item.getString());
					} else {
						fileName = item.getName();
						contentType = item.getContentType();
						sizeInBytes = new Long(item.getSize()).intValue();
						uploadedStream = item.getInputStream();
					}
				}

				conn = PersistenceUtil.getConnection((String) request.getSession().getAttribute("session.url"));
				conn.setAutoCommit(false);

				String sql = "INSERT INTO attachment (contents, attachmentFileName, fileType, description, hostID, hostType, attachmentURL) VALUES (?,?,?,?,?,?,?)";
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setBinaryStream(1, uploadedStream,sizeInBytes);
				stmt.setString(2, fileName);
				stmt.setString(3, contentType);
				String desc = map.get("description");
				if (desc != null && desc.length() > 0){
					stmt.setString(4, map.get("description"));
				} else {
					stmt.setString(4, fileName);					
				} 
				if (fileName.length() < 1){
					stmt.setString(4, map.get("url"));										
				}
				stmt.setString(5, map.get("hostID"));
				stmt.setString(6, map.get("hostType"));
				stmt.setString(7, map.get("url"));
				stmt.execute();

				conn.commit();
				uploadedStream.close();

			} catch (SizeLimitExceededException e){
				try {
					response.sendError(405,"Attachment File Size Limit Exceeded");
				} catch (Exception e2) {
					e.printStackTrace();
				}
				
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
				try {
					e.printStackTrace(response.getWriter());
				} catch (Exception e2) {
					// TODO: handle exception
				}

			} finally {
				try {
					if (conn != null){
						conn.close();
					}
				} catch (SQLException e) {
					log.error("QRM Stack Trace", e);
				}
			}
		} 
	}

	private void getAttachment(final HttpServletRequest request, final HttpServletResponse response) {

		try {
			Connection conn = PersistenceUtil.getConnection((String) request.getSession().getAttribute("session.url"));
			ResultSet res = conn.createStatement().executeQuery("SELECT contents, attachmentFileName FROM attachment WHERE internalID = "+ request.getParameter("ATTACHMENTID"));
			res.first();

			ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
			InputStream stream = res.getBinaryStream("contents");
			int a1 = stream.read();
			while (a1 >= 0) {
				byteArrayStream.write((char) a1);
				a1 = stream.read();
			}
			String filename = res.getString("attachmentFileName");

			if (filename.endsWith(".pdf") ||filename.endsWith(".PDF")){
				response.setContentType("application/pdf");
			} else if (filename.endsWith(".doc") ||filename.endsWith(".DOC")){
				response.setContentType("application/msword");
			} else if (filename.endsWith(".ppt") ||filename.endsWith(".PPT")){
				response.setContentType("application/vnd.ms-powerpoint");
			} else if (filename.endsWith(".xls") ||filename.endsWith(".XLS")){
				response.setContentType("application/vnd.ms-excel");
			} else if (filename.endsWith(".html") ||filename.endsWith(".HTML")){
				response.setContentType("text/html");
			} else {
				response.setContentType("application/x-download");
			}

			response.setHeader("Content-Disposition", "attachment; filename="+ res.getString("attachmentFileName"));
			response.setHeader("Content-Length", new Integer(byteArrayStream.size()).toString());

			byteArrayStream.writeTo(response.getOutputStream());

			res.close();
			conn.close();
			byteArrayStream.close();
			response.getOutputStream().close();

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
	}

	private void getJobResult(final HttpServletRequest request,	final HttpServletResponse response){
		Connection conn = PersistenceUtil.getConnection((String) request.getSession().getAttribute("session.url"));
		boolean error = false;
		try {
			ResultSet res = conn.createStatement().executeQuery("SELECT * FROM jobresult WHERE jobID = "+request.getParameter("JOBID"));
			res.first();

			InputStream stream = res.getBinaryStream("resultStr");

			ResultSet res2 = conn.createStatement().executeQuery("SELECT jobDescription, reportFormat FROM jobqueue WHERE jobID = "+request.getParameter("JOBID"));
			res2.first();
			String format = res2.getString("reportFormat");
			String desc = "";
			try {
				desc = res2.getString("jobDescription").replace(" ", "_");
			} catch (Exception e) {
				desc = "QRM_Job_"+ request.getParameter("JOBID");
			}

			desc = desc+"-"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

			response.setContentType("application/zip");
			if (format.equalsIgnoreCase("PDF")){
				response.setContentType("application/pdf");
				if (request.getParameter("open") != null) {
					response.setHeader("Content-Disposition", "inline; filename="+desc+".pdf");					
				} else {
					response.setHeader("Content-Disposition", "attachment; filename="+desc+".pdf");										
				}
				output(stream, response);
			} else if (format.equalsIgnoreCase("MS WORD")){
				response.setHeader("Content-Disposition", "attachment; filename="+desc+".doc.zip");
				createZippedOut(stream, desc+".doc", response);
			} else if (format.equalsIgnoreCase("MS POWERPOINT")){
				response.setHeader("Content-Disposition", "attachment; filename="+desc+".ppt.zip");
				createZippedOut(stream, desc+".ppt", response);
			} else if (format.equalsIgnoreCase("xml")){
				response.setHeader("Content-Disposition", "attachment; filename="+desc+".xml.zip");
				createZippedOut(stream, desc+".xml", response);
			}else if (format.equalsIgnoreCase("MS EXCEL")){
				response.setHeader("Content-Disposition", "attachment; filename="+desc+".xml.zip");
				createZippedOut(stream, desc+".xml", response);
			}else if (format.equalsIgnoreCase("xls")){
				response.setHeader("Content-Disposition", "attachment; filename="+desc+".xls.zip");
				createZippedOut(stream, desc+".xls", response);
			} else if (format.equalsIgnoreCase("HTML")){
				response.setContentType("text/html");
				output(stream, response);
			} else {
				response.setContentType("application/x-download");
				response.setHeader("Content-Disposition", "attachment; filename="+desc+"."+format.toLowerCase());
				output(stream, response);
				
			}
			conn.createStatement().executeUpdate("UPDATE jobqueue SET collected = true, readyToCollect = false WHERE jobID = "+request.getParameter("JOBID"));
			res2.close();
		} catch (Exception e3) {
			response.setContentType("text/html");
			try {
				response.getWriter().println("<h2>Error Producing Report</h2><br/><br/>");
				e3.printStackTrace(response.getWriter());
				error = true;
			} catch (IOException e) {
				log.error("QRM Stack Trace", e);
			}
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("QRM Stack Trace", e);
			}
			try {
				if (!error)response.getOutputStream().close();
			} catch (IOException e) {
				log.error("QRM Stack Trace", e);
			}
		}
	}
	
	public static void output(final InputStream stream, final HttpServletResponse response){
		try {
			ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
			int a1 = stream.read();
			while (a1 >= 0) {
				byteArrayStream.write((char) a1);
				a1 = stream.read();
			}
			response.setHeader("Content-Length", new Integer(byteArrayStream.size()).toString());
			byteArrayStream.writeTo(response.getOutputStream());
			byteArrayStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	public static void createZippedOut(InputStream in, String entryName, HttpServletResponse response) throws MessagingException {
		try {
			ZipOutputStream zout = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
			zout.putNextEntry(new ZipEntry(entryName));
			final byte[] buffer = new byte[ BUFFER_SIZE ];
			int n = 0;
			while ( -1 != (n = in.read(buffer)) ) {
				zout.write(buffer, 0, n);
			}
			zout.closeEntry();
			zout.close();
			in.close();
			response.getOutputStream().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
