package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.QRMAsyncMessage;



@SuppressWarnings("serial")
@WebServlet (urlPatterns = {"/signupUser"}, asyncSupported = false)

public class ServletSignupUser extends HttpServlet {

	private ServletConfig sc;

	public static String WEB_LOCATION_SECURITY_CODE_ERROR;

	private String NEW_USER_TOO_MANY_USERS_ERROR = "Number of users for the organisation has been exceeded. \nUser not added.";
	private String NEW_USER_EMAIL_EXISTS = "Entered email address already exists on the system \nUser not added.";
	private String NEW_USER_OK = "Resgistered Successfully. You May Now Logon.";

	private static Logger log = Logger.getLogger("au.com.quaysystems.qrm");
	
	@Override
	public void init(final ServletConfig sc) {
		
		this.sc = sc;

	}


	public void doPost(final HttpServletRequest request, final HttpServletResponse response)  {
		doGet(request, response);
	}
	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response)  {

		HashMap<String, String> stringMap = new HashMap<>();
		HashMap<Object, Object> objMap = new HashMap<>();



		for (Object key : request.getParameterMap().keySet()) {

			stringMap.put((String) key,	request.getParameter(key.toString()));

			if (request.getParameter(key.toString()).equalsIgnoreCase("true")) {
				objMap.put(key, new Boolean(true));
			} else if (request.getParameter(key.toString()).equalsIgnoreCase("false")) {
				objMap.put(key, new Boolean(false));
			} else {
				objMap.put(key,	request.getParameter(key.toString()));
			}

		}

		response.setHeader("Cache-Control", "no-cache");

		// Invoke the requested method
		try  {
			execute( request, response,  stringMap, objMap);
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
	}




	protected final void execute(HttpServletRequest request, HttpServletResponse response,
			HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap) {

		String name = stringMap.get("NAME");
		String pass1 = stringMap.get("PASS1");
		String pass2 = stringMap.get("PASS2");
		String email = stringMap.get("EMAIL");
		String orgcode = stringMap.get("ORG");
//		boolean agree = false;
//		try {
//			agree = stringMap.get("READ").contains("on");
//		} catch (Exception e1) {
//			agree = false;
//		}
//
//		if (!agree){
//			request.setAttribute("qrmMsgLogin", new String("You need to agree to the conditions of use"));
//			try {
//				sc.getServletContext().getRequestDispatcher("/login.jsp").forward(request, response);
//				return;
//			} catch (Exception e) {
//				log.error("QRM Stack Trace", e);
//				return;
//			}			
//		}

		if (!pass1.equals(pass2)) {
			request.setAttribute("qrmMsgLogin", new String("Password do not match."));
			try {
				sc.getServletContext().getRequestDispatcher("/login.jsp").forward(request, response);
				return;
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
				return;
			}
		}

		if ((email == null) || (email.length() < 5) || (name == null)|| (name.length() < 5)) {
			request.setAttribute("qrmMsgLogin",new String("Name and/or Email do not meet minimum length requirements."));
			try {
				sc.getServletContext().getRequestDispatcher("/login.jsp").forward(request,response);
				return;
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
				return;
			}
		}

		if ((orgcode == null) || (orgcode.length() < 5)	|| (orgcode.length() > 6)) {
			request.setAttribute("qrmMsgLogin",	new String("Organisation Code Invalid."));
			try {
				sc.getServletContext().getRequestDispatcher("/login.jsp").forward(request,	response);
				return;
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
				return;
			}
		}

		Connection conn = null;

		try {
			conn = PersistenceUtil.getQRMLoginCPDS().getConnection();
			conn.setAutoCommit(true);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * from repositories WHERE orgcode = '"+ orgcode + "'");

			boolean hasResults = rs.first();
			if (!hasResults) {
				request.setAttribute("qrmMsgLogin",	new String("Organisation Code Not Found."));
				try {
					sc.getServletContext().getRequestDispatcher("/login.jsp").forward(request,response);
					return;
				} catch (Exception e) {
					return;
				} finally {
					closeAll(null,null, conn);
				}
			}

			String msg = addUserCommon(conn, name, pass1, email, rs.getInt("repID"), rs.getBoolean("autoAddUsers"), rs.getString("url"));
			request.setAttribute("qrmMsg", msg);

			if (msg.contains(NEW_USER_OK)){
				// Send a message to administrator to notify of new user
				try {
					HashMap<String, String> job = new HashMap<String, String>();
					job.put("messageTitle", "New User Signed Up");
					job.put("messageBody", "Name: "+name+"\nEmail Address: "+email);
					new QRMAsyncMessage(QRMConstants.ADMINEMAIL_MSG, job, false).send();
				} catch (Exception e1) {
					log.error("QRM Stack Trace", e1);
				}
				try {
					sc.getServletContext().getRequestDispatcher("/login.jsp").forward(request,response);
					return;
				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
					return;
				} finally {
					closeAll(rs,stmt, conn);
				}
			} else {
				try {
					sc.getServletContext().getRequestDispatcher("/login.jsp").forward(request,response);
				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
				} finally {
					closeAll(rs,stmt, conn);
				}
				return;
			}
		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		} finally {
			closeAll(null,null, conn);
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

		return NEW_USER_OK;
	}

	protected static void closeAll(final ResultSet resultSet,
			final Statement statement, final Connection connection) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
			} // nothing we can do
		}
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
			} // nothing we can do
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
			} // nothing we can do
		}
	}

}