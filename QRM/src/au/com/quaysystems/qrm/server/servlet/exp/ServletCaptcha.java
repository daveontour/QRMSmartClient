package au.com.quaysystems.qrm.server.servlet.exp;

import static nl.captcha.Captcha.NAME;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.captcha.Captcha;
import nl.captcha.servlet.CaptchaServletUtil;

import org.apache.log4j.Logger;

import au.com.quaysystems.qrm.QRMConstants;

@WebServlet (value = "/simpleImg", asyncSupported = false)
public class ServletCaptcha extends HttpServlet {

	private static final long serialVersionUID = 13L;
	private static final String PARAM_HEIGHT = "height";
	private static final String PARAM_WIDTH = "width";
	protected int _width = QRMConstants.Captcha_width;
	protected int _height = QRMConstants.Captcha_height;
	private static Logger log = Logger.getLogger("au.com.quaysystems.qrm");


	@Override
	public final void init() throws ServletException {
		
		log.info("Captch Servlet Processor Started");

		if (getInitParameter(PARAM_HEIGHT) != null) {
			_height = Integer.valueOf(getInitParameter(PARAM_HEIGHT));
		}

		if (getInitParameter(PARAM_WIDTH) != null) {
			_width = Integer.valueOf(getInitParameter(PARAM_WIDTH));
		}
	}

	@Override
	public final void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,	IOException {
		resp.addHeader("Cache-Control", "no-cache");
		Captcha captcha = new Captcha.Builder(_width, _height).addText().gimp().build();

		try {
			CaptchaServletUtil.writeImage(resp, captcha.getImage());
			req.getSession().setAttribute(NAME, captcha);
		} catch (Exception e) {
			//Captcha exception
		}
	}
}
