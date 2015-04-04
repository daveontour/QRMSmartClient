package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

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

import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.servlet.SessionControl;

@WebServlet (value = "/QRMComment/*", asyncSupported = false)
public class ServletCommentManager extends HttpServlet {

	private static final long serialVersionUID = 1387308835754413189L;
	private static Logger log = Logger.getLogger("au.com.quaysystems.qrm");


	@Override
	public final void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
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
		log.info("Comment Manager Processor Started");
	}

	public final void handleRequest(final HttpServletRequest request,
			final HttpServletResponse response) {

		if (request.getParameter("getCommentAttachment") != null) {
			getCommentAttachment(request, response);
		}
		if (request.getParameter("saveComment") != null) {
			saveComment(request, response);
		}
	}

	@SuppressWarnings("unchecked")
	private void saveComment(final HttpServletRequest request,	final HttpServletResponse response) {

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		if (isMultipart) {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setFileCleaningTracker(new FileCleaningTracker());
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setSizeMax(16777216L);

			try {
				HashMap<String, String> map = new HashMap<String, String>();
				InputStream uploadedStream = null;
				int sizeInBytes = 0;
				String fileName = null;
				Long userID = SessionControl.sessionMap.get(request.getSession().getId()).person.getStakeholderID();

				for (FileItem item : (List<FileItem>) upload.parseRequest(request)) {
					if (item.isFormField()) {
						map.put(item.getFieldName(), item.getString());
					} else {
						fileName = item.getName();
						sizeInBytes = new Long(item.getSize()).intValue();
						uploadedStream = item.getInputStream();
					}
				}

				Connection conn = PersistenceUtil.getConnection((String) request.getSession().getAttribute("session.url"));
				conn.setAutoCommit(false);

				String sql = null;
				PreparedStatement stmt = null;

				sql = "INSERT INTO auditcomments (  riskID, enteredByID, comment,  commenturl,  attachment,  attachmentFileName) VALUES (?,?,?,?,?,?)";
				stmt = conn.prepareStatement(sql);
				stmt.setLong(1, Long.parseLong(map.get("riskID")));
				stmt.setLong(2, userID);
				stmt.setString(3, map.get("comment"));
				stmt.setString(4, map.get("commenturl"));
				stmt.setBinaryStream(5, uploadedStream, sizeInBytes);
				stmt.setString(6, fileName);
				stmt.execute();

				conn.commit();
				uploadedStream.close();
				conn.close();

			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}
		}

		try {
			ServletOutputStream out = response.getOutputStream();
			out.println("<html><body><script>parent.getRiskComments();</script></body></html>");
			out.close();
		} catch (IOException e) {
			log.error("QRM Stack Trace", e);
		}
	}

	private void getCommentAttachment(final HttpServletRequest request,
			final HttpServletResponse response) {

		try {
			Connection conn = PersistenceUtil.getConnection((String) request.getSession().getAttribute("session.url"));
			ResultSet res = conn.createStatement().executeQuery("SELECT  attachment, attachmentFileName FROM auditcomments WHERE internalID = "+ request.getParameter("ATTACHMENTID"));

			res.first();

			ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
			InputStream stream = res.getBinaryStream("attachment");
			int a1 = stream.read();
			while (a1 >= 0) {
				byteArrayStream.write((char) a1);
				a1 = stream.read();
			}
			response.setContentType("application/x-download");
			response.setHeader("Content-Disposition", "attachment; filename="+ res.getString("attachmentFileName"));
			response.setHeader("Content-Length", new Integer(byteArrayStream.size()).toString());

			byteArrayStream.writeTo(response.getOutputStream());

			conn.close();
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
	}
}
