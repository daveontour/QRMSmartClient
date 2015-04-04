package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelScheduledJob;
import au.com.quaysystems.qrm.server.PersistenceUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/removeReportRecipient", asyncSupported = false)
public class ServletRemoveReportRecipient extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		ResultSet rs;
		String repUrl = null;
		Connection conn = null;
		try {
			conn =  PersistenceUtil.getQRMLoginCPDS().getConnection();
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM repositories WHERE repID = ?");
			stmt.setLong(1, Long.parseLong(stringMap.get("repID")));
			rs = stmt.executeQuery();
			rs.first();
			repUrl = rs.getString("url");

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} finally {
			closeAll(null,null,conn);
		}

		Session sess2 = PersistenceUtil.getSession(repUrl);

		sess2.beginTransaction();
		ModelScheduledJob job = (ModelScheduledJob)sess.get(ModelScheduledJob.class, Long.parseLong(stringMap.get("jobID")));
		job.additionalUsers = job.additionalUsers.replaceAll(stringMap.get("NAME").toLowerCase(), ",").replaceAll(",,", ",");
		sess2.update(job);
		sess2.getTransaction().commit();

		sess2.close();

		try {
			request.setAttribute("email", stringMap.get("NAME"));
			request.setAttribute("reportName",	job.description);

			getServletConfig().getServletContext().getRequestDispatcher("/removedRecipient.jsp").forward(request, response);
		} catch (ServletException e) {
			log.error("QRM Stack Trace", e);
		} catch (IOException e) {
			log.error("QRM Stack Trace", e);
		}

	}
}
