package au.com.quaysystems.qrm.server.servlet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import au.com.quaysystems.qrm.dto.ModelPerson;
import au.com.quaysystems.qrm.server.servlet.exp.ServletUserMessageManager;

@WebFilter(urlPatterns = {"/QRMServer"})
public class FilterSession implements Filter {

	private Properties configProp = new Properties();
	private int sessionTimeout;  // in seconds
	private static Logger log = Logger.getLogger("au.com.quaysystems.qrm");
	private static HashSet<String> adminOps = new HashSet<String>();
	public final void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)	throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		String op = req.getParameter("OPERATION");
		boolean initUser = (req.getParameter("launch") != null)?true:false;
		if (initUser){
			op = "initUser";
		}

		try {
			if (log.isInfoEnabled()){
				HttpSession httpSess = ((HttpServletRequest) request).getSession();
				Long userID = SessionControl.sessionMap.get(httpSess.getId()).person.getStakeholderID();
				log.info("Processing: " + op+" (UserID: "+userID+")");
			}
		} catch (Exception e1) {
			//Do nothing;
		}

		// All requests to this servlet must have "OPERATION" parameter set 

		if (op == null) {
			((HttpServletResponse) response).setStatus(401);
			((HttpServletResponse) response).getWriter().println("Invalid Request");
			return;
		}

		if (op.indexOf("logout") != -1) {
			// Remove any previous session information
			try {
				SessionControl.sessionMap.remove(req.getSession().getId());
				SessionControl.logEndSession(req.getSession().getId());
			} catch (Exception e) {	}
			req.getSession().removeAttribute("session.url");
			req.getSession().removeAttribute("dbuser");
			req.getSession().removeAttribute("QRMPERSON");
			try {
				SessionControl.sessionMap.remove(req.getSession().getId());
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}
			req.getSession().invalidate();

			//This signals the script on the client to load the logout page.
			((HttpServletResponse) response).setStatus(401);
			((HttpServletResponse) response).getWriter().println("LOGGEDOUT*"+configProp.getProperty("LOGOUT_REDIRECT"));
			return;
		}

		// Pass through a request to remove a recipient which may be request by a non user
		if (op.equalsIgnoreCase("removeReportRecipient")) {
			chain.doFilter(request, response);
			return;
		}

		HttpSession httpSess = ((HttpServletRequest) request).getSession();
		// A session would have been created by the initUser process, so the absence of one
		// indicates an invalid connection

		if ((httpSess == null) || (httpSess.getId() == null)) {
			((HttpServletResponse) response).setStatus(401);
			req.getSession().invalidate();
			return;
		}

		//Check for an administrator operation
		if (adminOps.contains(op)) {
			ModelPerson repmgr = null;
			try {
				repmgr = (ModelPerson)((HttpServletRequest)request).getSession().getAttribute("QRMPERSON");
			} catch (Exception e) {
				((HttpServletResponse) response).setStatus(401);
				((HttpServletResponse) response).getWriter().println("Invalid Session");
				try {
					SessionControl.sessionMap.remove(httpSess.getId());
				} catch (Exception e1) {
					log.error("QRM Stack Trace", e1);
				}
				req.getSession().invalidate();
				return;
			}
			if (repmgr == null || repmgr.stakeholderID != 1){
				((HttpServletResponse) response).setStatus(401);
				((HttpServletResponse) response).getWriter().println("Invalid Session");
				try {
					SessionControl.sessionMap.remove(httpSess.getId());
				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
				}
				req.getSession().invalidate();
				return;
			} else {
				chain.doFilter(request, response);
			}
			return;
		}



		String sessUrl = (String) httpSess.getAttribute("session.url");
		// This attribute is set by the logon process, so it absence indicates an error
		
		if (sessUrl == null) {
			((HttpServletResponse) response).setStatus(401);
			((HttpServletResponse) response).getWriter().println("Invalid Session");
			try {
				SessionControl.sessionMap.remove(httpSess.getId());
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}
			req.getSession().invalidate();
			return;
		}

		// allow for programmatic disabling of session
		if (!SessionControl.sessionMap.get(httpSess.getId()).sessionEnabled) {
			try {
				SessionControl.sessionMap.remove(httpSess.getId());
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}
			((HttpServletResponse) response).setStatus(401);
			((HttpServletResponse) response).getWriter().println("Your session has been disabled by the administrator");
			return;
		}

		// Session is valid

		// Check for session timeout.
		Date nowDate = new Date();
		Long now = nowDate.getTime();
		Long lastTrans = (Long)	httpSess.getAttribute("LAST_QRM_TRANSACTION");

		if (lastTrans != null){
			if ((now - lastTrans) > (1000* sessionTimeout)){
				((HttpServletResponse) response).setStatus(401);
				return;
			}
		}
		
		// Record 
		httpSess.setAttribute("LAST_QRM_TRANSACTION", now);
		try {
			ServletUserMessageManager.sessionMemberMap.get(httpSess.getId()).lastRequest = now;
		} catch (Exception e) {
			// Nothing to worry about
		}
		SessionControl.sessionMap.get(httpSess.getId()).lastAccess = nowDate;
		SessionControl.sessionMap.get(httpSess.getId()).numTransactions++;


		chain.doFilter(request, response);
	}

	public final void init(final FilterConfig filterConfig)	throws ServletException {
		InputStream in;
		try {
			in = new FileInputStream(filterConfig.getServletContext().getRealPath("/QRM.properties"));
			try {
				configProp.load(in);
			} catch (IOException e) {
				log.error("QRM Stack Trace", e);
			}
		} catch (FileNotFoundException e) {
			log.error("QRM Stack Trace", e);
		}
		try {
			sessionTimeout = Integer.parseInt(configProp.getProperty("SESSION_TIMEOUT"));
		} catch (NumberFormatException e) {
			sessionTimeout = 600;
		}
		
		adminOps.add("getUserRepositories");
		adminOps.add("getAllRepositories");
		adminOps.add("getAllSessions");
		adminOps.add("newRepositories");
		adminOps.add("disableSession");
		adminOps.add("clearDefunct");
		adminOps.add("getTransactionUsage");
		adminOps.add("getAllScheduledJobs");
		adminOps.add("deleteJob");
		adminOps.add("disableLogon");
		adminOps.add("enableLogon");
		adminOps.add("getWorkQueueLengths");
		adminOps.add("deleteJobResult");
		adminOps.add("getCompletedJobResult");
		adminOps.add("disableUser");
		adminOps.add("normaliseProject");
		adminOps.add("addRepository");
		adminOps.add("getAllUsersDS");
		adminOps.add("getAvailDatabaseDS");
		adminOps.add("getDBTemplates");
		adminOps.add("sendBroadcastMessage");
		adminOps.add("resendEmail");
		adminOps.add("resendAllUnsent");
		adminOps.add("deleteCompletedJob");
		adminOps.add("deleteRepository");
		adminOps.add("updateRep");
		
		log.info("Session Filter Created");

	}
	public void destroy() {
	}
}
