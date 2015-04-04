package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import au.com.quaysystems.qrm.server.PersistenceUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/newPersonRep", asyncSupported = false)
public class ServletNewPersonRep extends QRMRPCServlet {
	
	private String NEW_USER_TOO_MANY_USERS_ERROR = "Number of users for the organisation has been exceeded. \nUser not added.";
	private String NEW_USER_EMAIL_EXISTS = "Entered Email Address already exist on the system \nUser not added.";
	private String NEW_USER_OK = "Resgistered Successfully. You May Now Logon.";


	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		JSONObject dataJS = null;
		try {
			dataJS = (JSONObject) parser.parse(stringMap.get("DATA"));
		} catch (ParseException e1) {
			log.error("QRM Stack Trace", e1);
		}

		String name = (String) dataJS.get("name");
		String email = (String) dataJS.get("email");
		String pass = (String) dataJS.get("passwordNew");
		String repURL = (String) request.getSession().getAttribute("session.url");

		if ((email == null) || (email.length() < 5) || (name == null) || (name.length() < 5)) {
			outputJSON("User Name or Email do not meet length requirements", response);
			return;
		}

		Connection conn = null;
		try {
			conn = PersistenceUtil.getQRMLoginCPDS().getConnection();
			conn.setAutoCommit(true);
			PreparedStatement ps = conn.prepareStatement("SELECT * from repositories WHERE url = ?");
			ps.setString(1, repURL);
			ResultSet rs = ps.executeQuery();
			rs.first();

			String msg = addUserCommon(conn, name, pass, email, rs.getLong("repID"), rs.getBoolean("autoAddUsers"), repURL);
			closeAll(rs, ps, conn);

			if (msg.endsWith(NEW_USER_OK)){
				msg = "User Added Successfully";
			}

			if (msg.endsWith(NEW_USER_EMAIL_EXISTS)){
				msg = "-1";
			}


			// JDBC was used, so reset the Session factory so the caches get synced
			PersistenceUtil.removeSF((String) request.getSession().getAttribute("session.url"));

			outputJSON(msg, response);
			return;

		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
			outputJSON("Unexpected error. \nUser may have not been added", response);
		} finally {
			closeAll(null,null,conn);
		}
	}
	
	private String addUserCommon(final Connection conn, final String name,
			final String pass, final String email, final long repID,
			final boolean autoEnable, final String repURL) throws SQLException {

		CallableStatement cs = conn.prepareCall("call checkAndCreateUser(?,?,?,?,?)");
		cs.setString(1, name);
		cs.setString(2, pass);
		cs.setString(3, email.toLowerCase());
		cs.setLong(4, repID);
		cs.registerOutParameter(5, Types.BIGINT);

		cs.execute();

		Long userID = cs.getLong(5);
		cs.close();

		if (userID.longValue() == -100) {
			return NEW_USER_TOO_MANY_USERS_ERROR;
		}
		if (userID.longValue() == -200) {
			return NEW_USER_EMAIL_EXISTS;
		}


		Connection connRep = PersistenceUtil.getConnection(repURL);
		CallableStatement cs2 = connRep.prepareCall("call addExistingUserToRep(?,?,?)");
		cs2.setLong(1, userID);
		cs2.setLong(2, repID);
		cs2.setBoolean(3, autoEnable);
		cs2.execute();
		cs2.close();

		closeAll(null, cs2,connRep);

		//		ReportProcessorData job = new ReportProcessorData();
		//		job.taskParamMap = new HashMap<Object, Object>();
		//		job.userID = userID;
		//		job.projectID = 0L;
		//		job.jdbcURL = repURL;
		//		job.schedJob = false;
		//		job.sendStatusUpdates = false;
		//		job.format = "HTML";
		//		job.sendEmail = true;
		//		job.emailFormat = "text/html";
		//		job.emailTitle = "Welcome to Quay Risk Manager";
		//		job.reportID = IQRMConstants.NEW_USER_REPORT;
		//
		//		// Send the message to the report procesor
		//		try {
		//			QRMAsyncMessage message = new QRMAsyncMessage(job);
		//			message.send();
		//		} catch (Exception e) {
		//			log.error("QRM Stack Trace", e);
		//		}
		return NEW_USER_OK;
	}

}
