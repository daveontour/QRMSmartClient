package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.server.PersistenceUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/addConnectRepository", asyncSupported = false)
public class ServletAddConnectRepository extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		try {
			JSONObject dataJS = (JSONObject) parser.parse(stringMap	.get("DATA"));
			String repName = (String) dataJS.get("repName");
			String repCode = (String) dataJS.get("repCode");

			Connection conn = PersistenceUtil.getQRMLoginCPDS().getConnection();
			conn.setAutoCommit(true);

			PreparedStatement ps = conn.prepareStatement("SELECT * from repositories WHERE rep = ? AND orgcode = ?");
			ps.setString(1, repName);
			ps.setString(2, repCode);

			ResultSet rs = ps.executeQuery();
			boolean hasResult = rs.first();
			if (!hasResult) {
				outputJSON(false,response);
			} else {
				int repID = rs.getInt("repID");

				Connection connRep = getSessionConnection(request);
				CallableStatement cs = connRep.prepareCall("call addExistingUserToRep(?,?,?)");
				cs.setLong(1, userID);
				cs.setLong(2, repID);
				cs.setBoolean(3, rs.getBoolean("autoAddUsers"));
				cs.execute();

				closeAll(rs, ps, connRep);
				closeAll(rs, cs, connRep);

				outputJSON(true,response);
			}

			closeAll(rs, ps, conn);

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

	}
}
