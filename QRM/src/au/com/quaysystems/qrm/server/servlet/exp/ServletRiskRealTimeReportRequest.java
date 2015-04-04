package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

@SuppressWarnings("serial")
@WebServlet (value = "/riskRealTimeReportRequest", asyncSupported = false)
public class ServletRiskRealTimeReportRequest extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		Connection conn = getSessionConnection(request);

		try {
			request.setAttribute("riskID", stringMap.get("riskID"));
			request.setAttribute("projectID", Long.parseLong( stringMap.get("projectID")));
			request.setAttribute("url", request.getSession().getAttribute("session.url"));
			request.setAttribute("descendants" , objMap.get("descendants"));
			request.setAttribute("userID" , userID);
			request.setAttribute("sess" , sess);
			request.setAttribute("conn" , conn);

			getServletConfig().getServletContext().getRequestDispatcher("/RiskDetail.jsp").forward(request, response);
			return;
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} finally{
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("QRM Stack Trace", e);
			}
		}
	}
}
