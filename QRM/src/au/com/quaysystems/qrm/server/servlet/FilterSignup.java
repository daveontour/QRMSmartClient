package au.com.quaysystems.qrm.server.servlet;

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

import nl.captcha.Captcha;
import au.com.quaysystems.qrm.server.servlet.exp.ServletSignupUser;

@WebFilter(urlPatterns = {"/signupUser"})
public class FilterSignup implements Filter {

	private FilterConfig filterConfig;
	public final void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)	throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;


		Captcha captcha = (Captcha) req.getSession().getAttribute(Captcha.NAME);
		request.setCharacterEncoding("UTF-8");
		if (captcha != null && captcha.isCorrect(request.getParameter("ANSWER"))) {
			req.getSession().removeAttribute("session.url");
			chain.doFilter(request, response);
			return;
		} else {
			if(request.getParameter("ORIGIN_WEBPAGE") != null){
				((HttpServletResponse) response).sendRedirect(ServletSignupUser.WEB_LOCATION_SECURITY_CODE_ERROR);
				return;
			} else {
				request.setAttribute("qrmBadSecurityCode", new Boolean(true));
				filterConfig.getServletContext().getRequestDispatcher("/login.jsp").forward(request, response);
				return;
			}
		}
	}

	public final void init(final FilterConfig filterConfig)	throws ServletException {
		this.filterConfig = filterConfig;
	}
	public void destroy() {
	}
}
