package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.server.PersistenceUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/deleteProject", asyncSupported = false)
public class ServletDeleteProject extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		try {

			Connection conn = getSessionConnection(request);
			CallableStatement cstmt = conn.prepareCall("{call deleteProject(?)}");
			cstmt.setLong(1, Long.parseLong(stringMap.get("DATA")));
			cstmt.execute();
			closeAll(null, cstmt, conn);

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

		// JDBC was used, so reset the Session factory so the caches get synced
		PersistenceUtil.removeSF((String) request.getSession().getAttribute("session.url"));


	}
}
