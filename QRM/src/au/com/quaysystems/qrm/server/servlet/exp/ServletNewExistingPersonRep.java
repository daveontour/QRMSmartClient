package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import au.com.quaysystems.qrm.server.PersistenceUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/newExistingPersonRep", asyncSupported = false)
public class ServletNewExistingPersonRep extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		JSONObject dataJS = null;
		try {
			dataJS = (JSONObject) parser.parse(stringMap.get("DATA"));
		} catch (ParseException e1) {
			log.error("QRM Stack Trace", e1);
		}

		String email = (String) dataJS.get("email");
		String repURL = (String) request.getSession().getAttribute("session.url");

		Connection conn = null;
		try {
			conn = PersistenceUtil.getQRMLoginCPDS().getConnection();
			conn.setAutoCommit(true);

			PreparedStatement ps0 = conn.prepareStatement("SELECT * from stakeholders where email = ?");
			ps0.setString(1, email);
			ResultSet rs0 = ps0.executeQuery();
			if (!rs0.first()){
				outputJSON("User With Entered Email Address Does Not Exist", response);
				closeAll(rs0, ps0, conn);
				return;
			}
			long user = rs0.getLong("stakeholderID");
			closeAll(rs0, ps0, null);

			PreparedStatement ps = conn.prepareStatement("SELECT * from repositories WHERE url = ?");
			ps.setString(1, repURL);
			ResultSet rs = ps.executeQuery();
			rs.first();

			Connection connRep = PersistenceUtil.getConnection(repURL);
			CallableStatement cs2 = connRep.prepareCall("call addExistingUserToRep(?,?,?)");
			cs2.setLong(1, user);
			cs2.setLong(2, rs.getLong("repID"));
			cs2.setBoolean(3, rs.getBoolean("autoAddUsers"));
			cs2.execute();
			cs2.close();

			closeAll(null, cs2,connRep);			
			PersistenceUtil.removeSF((String) request.getSession().getAttribute("session.url"));

			outputJSON("User Added Successfully", response);
			return;

		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
			outputJSON("Unexpected error. \nUser may have not been added", response);
		} finally {
			closeAll(null,null,conn);
		}
	}
}
