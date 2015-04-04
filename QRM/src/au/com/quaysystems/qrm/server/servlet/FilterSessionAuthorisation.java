/*
 * 
 */
package au.com.quaysystems.qrm.server.servlet;

import java.io.IOException;
import java.util.Date;

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

import au.com.quaysystems.qrm.server.servlet.exp.ServletUserMessageManager;

/**
 * The Class FilterSessionAuthorisation.
 */
@WebFilter(urlPatterns = {"/images/paintMetric/*", "/QRMReportUpload/*","/getConsequenceImage/*","/QRMLikelihood/*","/RiskMatrixPainter/*","/QRMAttachment/*","/QRMComment/*","/QRMMSFormat/*","/report"})
public class FilterSessionAuthorisation implements Filter {

	private FilterConfig filterConfig;


	public final void doFilter(final ServletRequest request,
			final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {

		if ((((HttpServletRequest) request).getSession(true)).getAttribute("session.url") != null) {
			
			HttpSession httpSess = ((HttpServletRequest) request).getSession();

			Date nowDate = new Date();
			Long now = nowDate.getTime();
		
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
			return;
		} else {
			if (((HttpServletRequest)request).getRequestURI().endsWith("/poll")){
				// Getting to this point indicates that a session has been timed out by the container
				// Status 401 tells the client that the session has timed out so it will display the expiration message and 
				// request the login page.
				((HttpServletResponse) response).setStatus(401);
				return;
			} else {
				// A number of irrecoverable error got us here, so just dispatch the login page to the user
				filterConfig.getServletContext().getRequestDispatcher("/login.jsp").forward(request, response);
			}
			return;

		}
	}

	public final void init(final FilterConfig filterConfig)	throws ServletException {
		this.filterConfig = filterConfig;
	}

	public void destroy() {
	}
}
