package au.com.quaysystems.qrm.server.servlet.repmgr;

import java.io.IOException;

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
import au.com.quaysystems.qrm.server.servlet.SessionControl;

@WebFilter(urlPatterns = {
		"/sessionControl",
		"/getAllRepositories",
		"/getWorkQueueLengths",
		"/getAllUsersDS", 
		"/addRepository",
		"/updateRep",
		"/normaliseProject"})
public class FilterRepMgrCall implements Filter {

	private static Logger log = Logger.getLogger("au.com.quaysystems.qrm");

	public final void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)	throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		ModelPerson repmgr = null;

		HttpSession httpSess = req.getSession();

		if ((httpSess == null) || (httpSess.getId() == null)) {
			handleError(req,resp, httpSess);
			return;
		}

		try {
			repmgr = (ModelPerson)((HttpServletRequest)request).getSession().getAttribute("QRMPERSON");
		} catch (Exception e) {
			handleError(req,resp, httpSess);
			return;
		}
		if (repmgr == null || repmgr.stakeholderID != 1){
			handleError(req,resp, httpSess);
			return;
		} else {
			chain.doFilter(request, response);
		}
		return;
	}
	
	private void handleError( HttpServletRequest req, HttpServletResponse resp,HttpSession httpSess) {
		resp.setStatus(401);
		try {
			resp.getWriter().println("Invalid Session");
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		try {
			SessionControl.sessionMap.remove(httpSess.getId());
		} catch (Exception e1) {
			log.error("QRM Stack Trace", e1);
		}
		try {
			req.getSession().invalidate();
		} catch (Exception e) {
			// Do nothing
		}
		return;
	}
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}
}
