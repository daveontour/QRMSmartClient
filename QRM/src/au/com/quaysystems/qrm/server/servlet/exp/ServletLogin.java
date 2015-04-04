
package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.ModelPerson;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.QRMAsyncMessage;
import au.com.quaysystems.qrm.server.report.ReportProcessorData;
import au.com.quaysystems.qrm.server.servlet.SessionControl;
import au.com.quaysystems.qrm.server.servlet.SessionEntry;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;

@SuppressWarnings("serial")
@WebServlet (urlPatterns = {"/qrm", "/QRMLoginServer"}, asyncSupported = false)
public class ServletLogin extends HttpServlet {

	private static String WEB_LOCATION_NO_USER_PASSWORD = null;
	private static String WEB_LOCATION_TOO_MANY_SESSIONS = null;
	private static String WEB_LOCATION_NO_MULTIPLE_USERS = null;
	private Properties configProp = new Properties();
	private ComboPooledDataSource cpds;
	private HashMap<Long, String[]> repIDURLMap = new HashMap<Long, String[]>();

	private final ThreadLocal<HttpServletRequest> perThreadRequest = new ThreadLocal<HttpServletRequest>();
	private final ThreadLocal<HttpServletResponse> perThreadResponse = new ThreadLocal<HttpServletResponse>();
	private final ThreadLocal<HashMap<String, String>> perThreadMapStr = new ThreadLocal<HashMap<String, String>>();
	private final ThreadLocal<HashMap<Object, Object>> perThreadMapObj = new ThreadLocal<HashMap<Object, Object>>();

	protected JSONParser parser = new JSONParser();

	private Logger log = Logger.getLogger("au.com.quaysystems.qrm");

	protected final ThreadLocal<Session> perThreadSess = new ThreadLocal<Session>();
	protected final ThreadLocal<Long> perThreadUserID = new ThreadLocal<Long>();

	public void init(final ServletConfig sc) {

		try {
			super.init(sc);
		} catch (ServletException e2) {
			log.error("QRM Stack Trace", e2);
		}

		InputStream in;
		try {
			in = new FileInputStream(sc.getServletContext().getRealPath("/QRM.properties"));
			try {
				configProp.load(in);
			} catch (IOException e) {
				log.error("QRM Stack Trace", e);
			}
		} catch (FileNotFoundException e) {
			log.error("QRM Stack Trace", e);
		}


		PersistenceUtil.setInput(configProp, true);
		cpds = PersistenceUtil.getQRMLoginCPDS();


		WEB_LOCATION_NO_USER_PASSWORD = configProp.getProperty("WEB_LOCATION_NO_USER_PASSWORD");
		WEB_LOCATION_NO_MULTIPLE_USERS = configProp.getProperty("WEB_LOCATION_NO_MULTIPLE_USERS");
		WEB_LOCATION_TOO_MANY_SESSIONS = configProp.getProperty("WEB_LOCATION_TOO_MANY_SESSIONS");

		log.info("Logon Processor Started");
	}

	public final void doGet(final HttpServletRequest request, final HttpServletResponse response) {
		doPost(request, response);
	}
	public final void doPost(final HttpServletRequest request, final HttpServletResponse response) {

		perThreadRequest.set(request);
		perThreadResponse.set(response);
		perThreadMapStr.set(new HashMap<String, String>());
		perThreadMapObj.set(new HashMap<Object, Object>());


		StringBuffer sb = new StringBuffer();
		sb.append("QRM Login Server Call {");
		for (Object key : perThreadRequest.get().getParameterMap().keySet()) {
			perThreadMapStr.get().put((String) key,	perThreadRequest.get().getParameter(key.toString()));
			if (perThreadRequest.get().getParameter(key.toString()).equalsIgnoreCase("true")) {
				perThreadMapObj.get().put(key, new Boolean(true));
			} else if (perThreadRequest.get().getParameter(key.toString()).equalsIgnoreCase("false")) {
				perThreadMapObj.get().put(key, new Boolean(false));
			} else {
				perThreadMapObj.get().put(key,perThreadRequest.get().getParameter(key.toString()));
			}

			String keyStr = (String)key;
			if (!keyStr.contains("PASS") && !keyStr.contains("pass") && !keyStr.contains("secret") && !keyStr.contains("SECRET") && !keyStr.contains("isc_")){
				sb.append(key + ": "+ perThreadRequest.get().getParameter(key.toString())+", ");
			} else {
				sb.append(key + ": *********, ");

			}
		}
		sb.append("}");
		log.info(sb.toString());

		String url = (String) request.getSession().getAttribute("session.url");
		if (url != null){
			try {
				perThreadSess.set(PersistenceUtil.getSession(url));
				perThreadSess.get().setCacheMode(CacheMode.IGNORE);
				perThreadSess.get().setFlushMode(FlushMode.ALWAYS);
			} catch (Exception e2) {
				perThreadSess.set(null);
			}			
		} else {
			perThreadSess.set(null);
		}


		try {
			perThreadUserID.set(SessionControl.sessionMap.get(request.getSession().getId()).person.getStakeholderID());
		} catch (Exception e4) {
			try {
				perThreadUserID.set((Long)(request.getSession().getAttribute("QRMSESSIONUSERID")));
			} catch (Exception e) {
				perThreadUserID.set(null);
			}
		}

		String op = perThreadMapStr.get().get("OPERATION");

		if (perThreadRequest.get().getParameter("launch") != null  || op.indexOf("initUser") != -1){
			initUser(); 
		} else if (op.indexOf("selectRepository") != -1){
			selectRepository(); 
		} else if (op.indexOf("passwordReset") != -1){
			passwordReset(); 
		} else if (op.indexOf("updatePerson") != -1){
			updatePerson(); 
		} else if (op.indexOf("changePassword") != -1){
			changePassword(); 
		}
	}
	protected final void initUser() {

		try {
			String userName = perThreadMapStr.get().get("NAME");
			String password = perThreadMapStr.get().get("PASS");

			boolean repmgr = false;

			String type = perThreadMapStr.get().get("QRMADMIN");

			if( type == null){
				repmgr = false;
			} else {
				if (type.equalsIgnoreCase("rep")){
					repmgr = true;
				}
				if (type.equalsIgnoreCase("normal")){
					repmgr = false;
				}
			}

			HttpSession httpSess = (perThreadRequest.get()).getSession(true);

			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			int numUsers = 0;
			try {
				conn = cpds.getConnection();

				ps = conn.prepareCall("call checkNumUser(?)");
				ps.setString(1, userName);
				rs = ps.executeQuery();
				rs.last();
				numUsers = rs.getRow();
			} catch (Exception e) {

				PersistenceUtil.resetDBConnections();		

				conn = cpds.getConnection();

				ps = conn.prepareCall("call checkNumUser(?)");
				ps.setString(1, userName);
				rs = ps.executeQuery();
				rs.last();
				numUsers = rs.getRow();
			}

			if (numUsers == 0) {
				conn.close();
				perThreadRequest.get().setAttribute("qrmName", userName);
				perThreadRequest.get().setAttribute("qrmUserNotFound",	new Boolean(true));
				if(perThreadMapStr.get().get("ORIGIN_WEBPAGE") != null){
					perThreadResponse.get().sendRedirect(WEB_LOCATION_NO_USER_PASSWORD);
					return;
				}
				getServletConfig().getServletContext().getRequestDispatcher("/login.jsp").forward(perThreadRequest.get(), perThreadResponse.get());
				return;
			}
			if (numUsers > 1) {
				conn.close();
				perThreadRequest.get().setAttribute("qrmMultipleUsers",	new Boolean(true));
				if(perThreadMapStr.get().get("ORIGIN_WEBPAGE") != null){
					perThreadResponse.get().sendRedirect(WEB_LOCATION_NO_MULTIPLE_USERS);
					return;
				}
				getServletConfig().getServletContext().getRequestDispatcher("/login.jsp").forward(perThreadRequest.get(), perThreadResponse.get());
				return;
			}


			// One user found, so create a model and then check the password
			ModelPerson person = new ModelPerson();
			person.stakeholderID = rs.getLong("stakeholderID");
			person.name = rs.getString("name");

			if (!rs.getBoolean("allowLogon")){
				perThreadRequest.get().setAttribute("qrmLoginDisabled", new Boolean(true));
				getServletConfig().getServletContext().getRequestDispatcher("/login.jsp").forward(perThreadRequest.get(), perThreadResponse.get());
				return;
			} else {
				perThreadRequest.get().setAttribute("qrmLoginDisabled", null);
			}


			ps = conn.prepareCall("call checkUserID(?,?)");
			ps.setLong(1, person.stakeholderID);
			ps.setString(2, password);

			rs = ps.executeQuery();
			rs.last();
			if (rs.getRow() != 1) {
				conn.close();
				perThreadRequest.get().setAttribute("qrmName", userName);
				if(perThreadMapStr.get().get("ORIGIN_WEBPAGE") != null){
					perThreadResponse.get().sendRedirect(WEB_LOCATION_NO_USER_PASSWORD);
					return;
				}
				perThreadRequest.get().setAttribute("qrmPasswordIncorrect", new Boolean(true));
				getServletConfig().getServletContext().getRequestDispatcher("/login.jsp").forward(perThreadRequest.get(), perThreadResponse.get());
				return;
			}

			// Users Password is OK, set session info and see if there are
			// multiple repositories for this user
			httpSess.setAttribute("QRMSESSIONUSERID", person.stakeholderID);
			httpSess.setAttribute("QRMPERSON", person);
			httpSess.setAttribute("LAST_QRM_TRANSACTION", new Date().getTime());

			try {
				// Set the session timeout
				String androidClient = perThreadMapStr.get().get("ANDROID");
				if (androidClient != null){
					httpSess.setMaxInactiveInterval(Integer.parseInt(configProp.getProperty("ANDROID_CLIENT_SESSION_TIMEOUT")));
				} else {			
					httpSess.setMaxInactiveInterval(Integer.parseInt(configProp.getProperty("SESSION_TIMEOUT")));
				}
			} catch (Exception e) {
				httpSess.setMaxInactiveInterval(600);
			}


			if (repmgr){
				conn.close();
				getServletConfig().getServletContext().getRequestDispatcher("/repmgr.jsp").forward(perThreadRequest.get(), perThreadResponse.get());
				return;
			}

			conn.close();
			userRepositories();

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
	}
	private void userRepositories() {

		HttpSession httpSess = (perThreadRequest.get()).getSession(true);
		ModelPerson person = (ModelPerson) httpSess.getAttribute("QRMPERSON");
		Connection conn = null;
		try {
			conn = cpds.getConnection();
			PreparedStatement ps = conn.prepareCall("call getUserReps(?)");
			ps.setLong(1, person.stakeholderID);

			ResultSet rs = ps.executeQuery();
			rs.last();
			int numProj = rs.getRow();

			if (numProj == 1) {
				rs.first();
				startUserSession(httpSess, rs.getLong("repID"), person, false);
				return;
			}
			if (numProj == 0) {
				if (person.stakeholderID == 1){
					startUserSession(httpSess, 0L, person, true);					
				} else {
					getServletConfig().getServletContext().getRequestDispatcher("/login.jsp").forward(perThreadRequest.get(), perThreadResponse.get());
					return;
				}
			}

			if (numProj > 1) {
				rs.beforeFirst();

				ArrayList<String[]> reps = new ArrayList<String[]>();
				while (rs.next()) {
					reps.add(new String[]{rs.getString("rep"),new Long(rs.getLong("repID")).toString()});
				}

				if  (perThreadMapStr.get().get("ORG") != null){
					for (String[] rep:reps){
						if (rep.equals(perThreadMapStr.get().get("ORG"))){
							startUserSession(httpSess, Long.parseLong(rep[0]), person, false);
							return;
						}
					}
				}
				perThreadRequest.get().setAttribute("qrmSelectCatalog", reps);
				getServletConfig().getServletContext().getRequestDispatcher("/multirepositories.jsp").forward(perThreadRequest.get(), perThreadResponse.get());
				return;

			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("QRM Stack Trace", e);
			}
		}
	}
	public final void selectRepository() {

		boolean repmgr = false;

		String type = perThreadRequest.get().getParameter("QRMADMIN");

		if( type == null){
			repmgr = false;
		} else {
			if (type.equalsIgnoreCase("rep")){
				repmgr = true;
			}
			if (type.equalsIgnoreCase("normal")){
				repmgr = false;
			}
		}

		HttpSession httpSess = (perThreadRequest.get()).getSession();
		ModelPerson person = (ModelPerson) httpSess.getAttribute("QRMPERSON");
		startUserSession(httpSess, Long.parseLong(perThreadMapStr.get().get("ORG")), person, repmgr);

	}
	private void startUserSession(final HttpSession httpSess, final Long repID,	ModelPerson person, boolean repmgr) {

		if(repmgr){
			SessionEntry sessionEntry = new SessionEntry();
			sessionEntry.setUser(person);
			sessionEntry.setLastAccess(new Date());
			sessionEntry.setSessionID(httpSess.getId());
			sessionEntry.setRemoteAddr(perThreadRequest.get().getRemoteAddr());
			sessionEntry.setRemoteHost(perThreadRequest.get().getRemoteHost());

			log.info("Starting Session.  Session ID: "+ httpSess.getId() + " User ID: " + sessionEntry.getUser().getName());
			System.out.println("### SessionControl ###  Starting Session "+httpSess.getId());
			SessionControl.sessionMap.put(httpSess.getId(), sessionEntry);

			perThreadRequest.get().setAttribute("userName", person.name);
			perThreadRequest.get().setAttribute("repMgr", new Boolean(true));
			try {
				getServletConfig().getServletContext().getRequestDispatcher("/QRMApplication.jsp").forward(perThreadRequest.get(),	perThreadResponse.get());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}

		try {
			String res[] = getURLForOrg(repID);
			String url = res[0];

			if (!SessionControl.repLogonOK(url)){
				if(perThreadMapStr.get().get("ORIGIN_WEBPAGE") != null){
					perThreadResponse.get().sendRedirect(WEB_LOCATION_TOO_MANY_SESSIONS);
					return;
				} else {
					perThreadRequest.get().setAttribute("qrmTooManySessions", new Boolean(true));
					getServletConfig().getServletContext().getRequestDispatcher("/login.jsp").forward(perThreadRequest.get(), perThreadResponse.get());
					return;
				}
			} 

			try {
				SessionEntry sessionEntry = new SessionEntry();
				sessionEntry.setUser(person);
				sessionEntry.setLastAccess(new Date());
				//				sessionEntry.setOrganisation(org);
				sessionEntry.setDbURL(url);
				sessionEntry.setSessionID(httpSess.getId());
				sessionEntry.setDbUser(res[1]);
				sessionEntry.setRemoteAddr(perThreadRequest.get().getRemoteAddr());
				sessionEntry.setRemoteHost(perThreadRequest.get().getRemoteHost());


				httpSess.setAttribute("session.url", url);
				httpSess.setAttribute("dbuser", res[1]);

				log.info("Starting Session.  Session ID: "+ httpSess.getId() + " User ID: " + sessionEntry.getUser().getName());
				SessionControl.sessionMap.put(httpSess.getId(), sessionEntry);

				// Log the new session
				SessionControl.logNewSession(sessionEntry);

				perThreadRequest.get().setAttribute("userName", person.name);
				getServletConfig().getServletContext().getRequestDispatcher("/QRMApplication.jsp").forward(perThreadRequest.get(),	perThreadResponse.get());

			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

	}
	protected final void passwordReset() {

		try {
			String userName = perThreadMapStr.get().get("NAME");
			String email = perThreadMapStr.get().get("EMAIL");
			String answer = perThreadMapStr.get().get("ANSWER");
			String pass1 = perThreadMapStr.get().get("PASS1");
			String pass2 = perThreadMapStr.get().get("PASS2");


			if (perThreadMapStr.get().get("ANSWER") != null){
				// ANSWER is set so use it to reset the password

				if (!pass1.equals(pass2)){
					perThreadRequest.get().setAttribute("qrmMsg", "Your password could not be reset - password do not match. ");
					getServletConfig().getServletContext().getRequestDispatcher("/login.jsp").forward(perThreadRequest.get(), perThreadResponse.get());
					return;
				} else {
					Connection conn = cpds.getConnection();

					PreparedStatement ps = conn.prepareCall("select resetPasswordWithAnswer(?,?,?,?)");
					ps.setString(1, userName);
					ps.setString(2, email);
					ps.setString(3, answer);
					ps.setString(4, pass1);
					ResultSet rs = ps.executeQuery();
					rs.first();

					long rtn = rs.getLong(1);
					rs.close();
					ps.close();
					conn.close();


					if (rtn > 0) {
						perThreadRequest.get().setAttribute("qrmMsg", "Your password has been reset. ");
					} else {
						perThreadRequest.get().setAttribute("qrmMsg", "Your password could not be reset - contact support. ");
					}
					getServletConfig().getServletContext().getRequestDispatcher("/login.jsp").forward(perThreadRequest.get(), perThreadResponse.get());
					return;
				}
			} else {

				Connection conn = cpds.getConnection();

				PreparedStatement ps = conn.prepareStatement("SELECT stakeholders.stakeholderID, stakeholderpassword.secretquestion from stakeholders, stakeholderpassword WHERE stakeholders.stakeholderID = stakeholderpassword.stakeholderID AND LOWER(stakeholders.name) = LOWER(?) AND LOWER(stakeholders.email) = LOWER(?)AND stakeholderpassword.secretquestion IS NOT NULL;");
				ps.setString(1, userName);
				ps.setString(2, email);
				ResultSet rs = ps.executeQuery();

				if (rs.first()){
					// The User has a secret question set so use that to reset the password

					perThreadRequest.get().setAttribute("secretQuestion", rs.getString("secretquestion"));
					perThreadRequest.get().setAttribute("name", userName);
					perThreadRequest.get().setAttribute("email", email);
					getServletConfig().getServletContext().getRequestDispatcher("/secretquestion.jsp").forward(perThreadRequest.get(), perThreadResponse.get());
					return;

				} else {
					// The User does not have a secret question set, so use generate a password and send it to the registered email address

					Random rand = new Random();
					String st = "ABCDEFGHIJKLMNOPQRSTUVWQYZ1234567890";
					rand.setSeed(new Date().getTime());

					int idx = rand.nextInt(36);
					String pass = st.substring(idx, idx + 1);
					for (int i = 0; i < 6; i++) {
						idx = rand.nextInt(36);
						pass = pass + st.substring(idx, idx + 1);
					}
					ps = conn.prepareCall("select resetPassword(?,?,?)");
					ps.setString(1, userName);
					ps.setString(2, email);
					ps.setString(3, pass);
					rs = ps.executeQuery();
					rs.first();

					long rtn = rs.getLong(1);
					rs.close();
					ps.close();
					conn.close();

					if (rtn > 0) {
						try {
							conn = cpds.getConnection();
							PreparedStatement ps2 = conn.prepareStatement("SELECT stakeholders.stakeholderID, stakeholderpassword.secretquestion from stakeholders, stakeholderpassword WHERE stakeholders.stakeholderID = stakeholderpassword.stakeholderID AND LOWER(stakeholders.name) = LOWER(?) AND LOWER(stakeholders.email) = LOWER(?);");
							ps2.setString(1, userName);
							ps2.setString(2, email);
							ResultSet rs2 = ps2.executeQuery();
							rs2.first();

							ReportProcessorData job = new ReportProcessorData();
							job.jobID = QRMConstants.RAW_TEXT_EMAIL;
							job.userID = rs2.getLong("stakeholderID");
							job.emailTitle = "Quay Risk Manager - Password Reset";
							job.emailFormat = "text";
							job.emailContent = "Your password for Quay Risk Manager has been reset to: "+pass;

							QRMAsyncMessage message = new QRMAsyncMessage(QRMConstants.EMAIL_MSG, job, true);
							message.send();

						} catch (Exception e) {
							log.error("QRM Stack Trace", e);
							perThreadRequest.get().setAttribute("qrmMsg", "Your password has been reset but there was a problem emailing you the new password. Plesse contact support");
							return;
						} finally {
							conn.close();
						}
						perThreadRequest.get().setAttribute("qrmMsg", "Your password has been reset and emailed to '"+email+"'. ");
					} else {
						perThreadRequest.get().setAttribute("qrmMsg", "Your password could not be reset - contact support. ");
					}
					getServletConfig().getServletContext().getRequestDispatcher("/login.jsp").forward(perThreadRequest.get(), perThreadResponse.get());
				}
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
	}
	//	public String[] getURLForOrg(final String org) {
	//
	//		String[] res = orgURLMap.get(org);
	//
	//		if (res != null) {
	//			return res;
	//		}
	//
	//		Connection conn = null;
	//		try {
	//			conn = cpds.getConnection();
	//			PreparedStatement ps = conn.prepareStatement("SELECT * FROM repositories WHERE rep = ?");
	//			ps.setString(1, org);
	//			ResultSet rs = ps.executeQuery();
	//			rs.first();
	//			String url = rs.getString("url");
	//			String user = rs.getString("dbUser");
	//			String[] res1 = new String[]{url,user};
	//			try {
	//				orgURLMap.put(org, res1);
	//				repIDURLMap.put(rs.getLong("repID"), res1);
	//				return res1;
	//			} catch (Exception e1) {
	//				return null;
	//			}
	//		} catch (SQLException e) {
	//			log.error("QRM Stack Trace", e);
	//			return null;
	//		} finally {
	//			try {
	//				conn.close();
	//			} catch (SQLException e) {
	//				// TODO Auto-generated catch block
	//				log.error("QRM Stack Trace", e);
	//			}
	//		}
	//	}
	public String[] getURLForOrg(final Long repID) {

		String[] res = repIDURLMap.get(repID);

		if (res != null) {
			return res;
		}

		Connection conn = null;
		try {
			conn = cpds.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM repositories WHERE repID = ?");
			ps.setLong(1, repID);
			ResultSet rs = ps.executeQuery();
			rs.first();
			//			String org = rs.getString("rep");
			String url = rs.getString("url");
			String user = rs.getString("dbUser");
			String[] res1 = new String[]{url,user};

			try {
				//			orgURLMap.put(org, res1);
				repIDURLMap.put(repID, res1);
				return res1;
			} catch (Exception e1) {
				return null;
			}
		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
			return null;
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				log.error("QRM Stack Trace", e);
			}
		}
	}
	protected final void changePassword() {

		try {
			JSONObject data = (JSONObject) parser.parse(perThreadRequest.get().getParameter("DATA"));
			String oldPass = (String) data.get("passwordCurrent");
			String newPass = (String) data.get("passwordNew");
			String secretQuestion = (String) data.get("secretQuestion");
			String secretAnswer = (String) data.get("secretAnswer");

			HttpSession session = perThreadRequest.get().getSession(false);

			String name = SessionControl.sessionMap.get(session.getId()).person.name;
			ModelPerson person = new ModelPerson();
			person.stakeholderID = perThreadUserID.get();
			person.name = name;

			// check that the exiting password is correct
			Connection conn = cpds.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM stakeholderpassword WHERE stakeholderID = ? AND password = PASSWORD('"+oldPass+"')");
			ps.setLong(1, person.stakeholderID);

			ResultSet rs = ps.executeQuery();
			rs.first();
			if (rs.getInt(1) != 1) {
				conn.close();
				throw new Exception();
			}

			ps = conn.prepareStatement("UPDATE stakeholderpassword SET password = PASSWORD('"+newPass+"') WHERE stakeholderID = ? ");
			ps.setLong(1, person.stakeholderID);
			boolean res = (ps.executeUpdate() == 1) ? true : false;

			if (secretQuestion != null ){
				ps = conn.prepareStatement("UPDATE stakeholderpassword SET secretquestion = ?, secretanswer = PASSWORD(LOWER('"+secretAnswer+"')) WHERE stakeholderID = ? ");
				ps.setString(1, secretQuestion);
				ps.setLong(2, person.stakeholderID);

				ps.executeUpdate();
			}

			conn.close();

			outputJSONB(res);

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			outputJSONB(false);
		}
	}
	protected final void updatePerson() {
		try {
			JSONObject data = (JSONObject) parser.parse(perThreadRequest.get().getParameter("DATA"));

			Transaction tx = perThreadSess.get().beginTransaction();

			ModelPerson person = (ModelPerson) perThreadSess.get().get(ModelPerson.class, perThreadUserID.get());
			if (person == null) {
				throw new Exception();
			}
			person.setName((String) data.get("name"));
			person.setEmail((String) data.get("email"));

			perThreadSess.get().update(person);
			tx.commit();
			outputJSONB(person);


		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			ModelPerson error = new ModelPerson();
			error.stakeholderID = -1L;
			outputJSONB(error);
		}
	}
	protected void outputJSONB(final Object obj) {
		try {
			perThreadResponse.get().getWriter().println("(" + xsJSON.toXML(obj) + ")");
		} catch (IOException e) {
			log.error("QRM Stack Trace", e);
		}
	}
	protected XStream xsJSON = new XStream(new JsonHierarchicalStreamDriver() {
		@Override
		public HierarchicalStreamWriter createWriter(final Writer writer) {
			return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
		}
	});
}