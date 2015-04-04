package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelWelcomeData;

@SuppressWarnings("serial")
@WebServlet (value = "/getWelcomeData", asyncSupported = false)
public class ServletGetWelcomeData extends QRMRPCServlet {

	@SuppressWarnings("unchecked")
	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		try {
			List<ModelWelcomeData> data = (List<ModelWelcomeData>) sess.getNamedQuery("getWelcomeData").setLong("projectID", projectID).list();
			outputJSON(data,response);
		} catch (Exception e) {
			try {
				log.error("QRM Stack Trace", e);
				response.setStatus(405);
				response.getWriter().println("Error retrieving data");
				return;
			} catch (IOException e1) {
				log.error("QRM Stack Trace", e1);
			}

		}
	}
}
