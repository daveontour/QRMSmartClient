package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.JobBean;
import au.com.quaysystems.qrm.server.JSPBeanUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/getReportBean", asyncSupported = false)
public class ServletGetReportBean extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		JobBean bean = JSPBeanUtil.getReportJobBean(sess,request.getParameter("jobID") );
		outputJSON(bean,response);
	}
}
