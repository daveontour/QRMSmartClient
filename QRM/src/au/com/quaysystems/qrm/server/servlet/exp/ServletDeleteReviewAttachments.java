package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONArray;

@SuppressWarnings("serial")
@WebServlet (value = "/deleteReviewAttachments", asyncSupported = false)
public class ServletDeleteReviewAttachments extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		try(Connection conn = getSessionConnection(request)){
			for (Object x : (JSONArray) parser.parse(request.getParameter("DATA"))) {
				conn.createStatement().executeUpdate("DELETE FROM attachment WHERE internalID = "+ (Long)x);
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
		try {
			response.getOutputStream().print(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
