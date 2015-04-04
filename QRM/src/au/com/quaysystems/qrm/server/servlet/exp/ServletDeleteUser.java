package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.parser.ParseException;

import au.com.quaysystems.qrm.dto.ModelRiskProject;
import au.com.quaysystems.qrm.server.PersistenceUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/deleteUser", asyncSupported = false)
public class ServletDeleteUser extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		Long user = null;
		try {
			user = (Long) parser.parse(stringMap.get("DATA"));
		} catch (ParseException e1) {
			log.error("QRM Stack Trace", e1);
		}

		String repURL = (String) request.getSession().getAttribute("session.url");
		long rootProjectID = PersistenceUtil.getRootProjectID((String) request.getSession().getAttribute("session.url"));
		ModelRiskProject project = getRiskProjectDetails(rootProjectID,	sess);
		if (project.projectRiskManagerID.longValue() != user){
			outputJSON("Only the risk manager of the root project can delete users.\n\nYou can disable access to the user by removing 'Risk User' rights", response);
			return;
		}

		try {
			Connection conn2 = PersistenceUtil.getQRMLoginCPDS().getConnection();
			PreparedStatement ps0 = conn2.prepareStatement("SELECT * from repositories WHERE url = ?");
			ps0.setString(1, repURL);
			ResultSet rs = ps0.executeQuery();
			rs.first();

			CallableStatement ps = conn2.prepareCall("call deleteUser(?,?,?)");
			try {
				ps.setLong(1, user);
				ps.setLong(2, rs.getLong("repID"));
				ps.registerOutParameter(3, Types.BIGINT);			
				ps.execute();
				if (ps.getLong(3) == -1L) {
					outputJSON("The user is still assigned to a risk or mitigation task. \nUser has not been removed", response);
					return;
				} else {
					Connection connRep = PersistenceUtil.getConnection(repURL);
					Statement stRep = connRep.createStatement();
					try {
						stRep.execute("delete from projectowners where stakeholderID = "+user);
						stRep.execute("delete from projectriskmanagers where stakeholderID = "+user);
						stRep.execute("delete from projectusers where stakeholderID = "+user);
					} catch (Exception e) {
						log.error("QRM Stack Trace", e);
						closeAll(null,stRep,connRep);
					}
				} 
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
				outputJSON("User Deletion Failed", response);
				return;
			} finally {
				closeAll(null, ps, conn2);
				closeAll(rs, ps0,null);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error("QRM Stack Trace", e);
		}
		outputJSON("User Deleted", response);


	}
}
