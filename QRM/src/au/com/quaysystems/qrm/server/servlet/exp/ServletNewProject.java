package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.server.PersistenceUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/newProject", asyncSupported = false)
public class ServletNewProject extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		try {
			JSONObject projectJS = (JSONObject) parser.parse(stringMap.get("DATA"));

			String projectTitle = (String) projectJS.get("projectTitle");
			String projectDesc = (String) projectJS.get("projectDescription");
			String projectCode = (String) projectJS.get("projectCode");
			Long parentID = getLongJS(projectJS.get("parentID"));

			Date startDate = new Date();
			try {
				startDate = df.parse(((String) projectJS.get("projectStartDate")));
			} catch (Exception e) {
				startDate = new Date();
			}

			Date endDate = new Date();
			try {
				endDate = df.parse(((String) projectJS.get("projectEndDate")));
			} catch (Exception e) {
				endDate = new Date(new Date().getTime() + 365 * 24 * 60 * 60 * 1000);
			}

			Connection conn = PersistenceUtil.getConnection((String) request.getSession().getAttribute("session.url"));
			CallableStatement cstmt = conn.prepareCall("{call addProject(?,?,?,?,?,?,?,?,?)}");

			cstmt.setLong(1, parentID);
			cstmt.setString(2, projectDesc);
			cstmt.setString(3, projectTitle);
			cstmt.setString(4, projectCode);
			cstmt.setLong(5, userID);
			cstmt.setDate(6, new java.sql.Date(startDate.getTime()));
			cstmt.setDate(7, new java.sql.Date(endDate.getTime()));
			cstmt.setInt(8, 0);
			cstmt.registerOutParameter(9, java.sql.Types.BIGINT);

			cstmt.execute();

			closeAll(null, cstmt, conn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("QRM Stack Trace", e);
		}

		PersistenceUtil.removeSF((String) request.getSession().getAttribute("session.url"));
		// JDBC was used, so reset the Session factory so the caches get synced


	}
}
