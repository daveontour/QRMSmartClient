package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.server.PersistenceUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/enableLogon", asyncSupported = false)
public class ServletEnableLogon extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		Connection conn = null;
		try {
			conn = PersistenceUtil.getQRMLoginCPDS().getConnection();
			conn.setAutoCommit(true);
			PreparedStatement ps = conn.prepareCall("UPDATE stakeholders set allowLogon = 1 WHERE stakeholderID = ?");
			ps.setLong(1, Long.parseLong(stringMap.get("stakeholderID")));
			ps.executeUpdate();

			conn.close();
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} finally {
			closeAll(null,null,conn);
		}

	}

}
