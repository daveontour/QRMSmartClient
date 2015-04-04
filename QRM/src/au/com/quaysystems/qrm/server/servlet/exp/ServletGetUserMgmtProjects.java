package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

@SuppressWarnings("serial")
@WebServlet (value = "/getUserMgmtProjects", asyncSupported = false)
public class ServletGetUserMgmtProjects extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		try {
			Object[] arr = new Object[3];
			arr[0] = getAllMgmtRiskProjectsForUserLite(userID, sess);
			arr[1] = sess.getNamedQuery("getAllRepPersonLite").list();
			arr[2] = sess.getNamedQuery("getAllMatrix").list();
			outputJSON2B(arr,response);
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

	}
}
