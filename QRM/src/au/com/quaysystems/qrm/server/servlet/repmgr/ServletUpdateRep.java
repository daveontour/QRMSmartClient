package au.com.quaysystems.qrm.server.servlet.repmgr;

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
import org.json.simple.JSONObject;

import au.com.quaysystems.qrm.server.PersistenceUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/updateRep", asyncSupported = false)
public class ServletUpdateRep extends QRMRPCServletRepMgr {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		
		try {
			JSONObject dataJS = (JSONObject) parser.parse(stringMap.get("DATA"));

			Long repID = getLongJS(dataJS.get("repID"));
			String rep = (String) dataJS.get("rep");
			Long repmgr = getLongJS(dataJS.get("repmgr"));
			String url = (String) dataJS.get("url");
			String orgname = (String) dataJS.get("orgname");
			Boolean active = (Boolean) dataJS.get("active");
			Boolean autoAddUsers = (Boolean) dataJS.get("autoAddUsers");
			Long sessionLimit = getLongJS(dataJS.get("sessionlimit"));
			Long userLimit = getLongJS(dataJS.get("userlimit"));
			String repLogonMessage = (String) dataJS.get("repLogonMessage");

			Connection conn = PersistenceUtil.getQRMLoginCPDS().getConnection();
			CallableStatement cs = conn.prepareCall("CALL updateRepository(?,?,?,?,?,?,?,?,?,?,?,?)");
			cs.setString(1, rep);
			cs.setString(2, url);
			cs.setString(3, orgname);
			cs.setLong(4, repmgr);
			cs.setLong(5, sessionLimit);
			cs.setLong(6, userLimit);
			cs.setBoolean(7, active);
			cs.setBoolean(8, autoAddUsers);
			cs.setLong(9, 1L);
			cs.setLong(10, repID);
			cs.setString(11, repLogonMessage);
			cs.registerOutParameter(12, Types.BIGINT);

			cs.execute();

			//ensure that the the Rep Manager is correctly configured in the userrep table

			Connection connRep = PersistenceUtil.getConnection(url);


			PreparedStatement ps = conn.prepareStatement("SELECT * from userrepository where stakeholderID = ? and repID = ?");
			ps.setLong(1,repmgr);
			ps.setLong(2, repID);
			ResultSet rs = ps.executeQuery();


			// User not configured in the user repository
			if (!rs.first()){
				try {
					CallableStatement cs2 = connRep.prepareCall("call addExistingUserToRep(?,?,?)");
					cs2.setLong(1, repmgr);
					cs2.setLong(2, repID);
					cs2.setBoolean(3, true);
					cs2.execute();
					cs2.close();

					closeAll(rs, cs2,null);
				} catch (Exception e1) {
					log.error("QRM Stack Trace", e1);
				}
			}

			// Ensure the rep manager is configured as an owner/manager and as the risk manager for the root project
			connRep.setAutoCommit(false);
			Long rootProjectID = getRepRootProjectID(url);

			try {

				PreparedStatement stu = null;
				try {
					stu = connRep.prepareStatement("SELECT * FROM  projectusers WHERE projectID = ? AND stakeholderID = ?");
					stu.setLong(1, rootProjectID);
					stu.setLong(2, repmgr);
					if (!stu.executeQuery().first()){
						stu = connRep.prepareStatement("INSERT INTO projectusers (projectID, stakeholderID) VALUES (?,?)");
						stu.setLong(1, rootProjectID);
						stu.setLong(2, repmgr);
						stu.executeUpdate();
					}
					connRep.commit();
				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
				}
				PreparedStatement sto = null;
				try {
					sto = connRep.prepareStatement("SELECT * FROM  projectowners WHERE projectID = ? AND stakeholderID = ?");
					sto.setLong(1, rootProjectID);
					sto.setLong(2, repmgr);
					if (!sto.executeQuery().first()){
						sto = connRep.prepareStatement("INSERT INTO projectowners (projectID, stakeholderID) VALUES (?,?)");
						sto.setLong(1, rootProjectID);
						sto.setLong(2, repmgr);
						sto.executeUpdate();
					}
					connRep.commit();
				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
				}
				PreparedStatement stm = null;
				try {
					stm = connRep.prepareStatement("SELECT * FROM  projectriskmanagers WHERE projectID = ? AND stakeholderID = ?");
					stm.setLong(1, rootProjectID);
					stm.setLong(2, repmgr);
					if(stm.executeQuery().first()){
						stm = connRep.prepareStatement("INSERT INTO projectriskmanagers (projectID, stakeholderID) VALUES (?,?)");
						stm.setLong(1, rootProjectID);
						stm.setLong(2, repmgr);
						stm.executeUpdate();
					}
					connRep.commit();
				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
				}


				PreparedStatement str = null;
				try {
					str = connRep.prepareStatement("UPDATE riskproject SET projectRiskManagerID = ? WHERE projectID = ?");
					str.setLong(2, rootProjectID);
					str.setLong(1, repmgr);
					str.executeUpdate();
					connRep.commit();
				} catch (Exception e) {
					log.error("QRM Stack Trace", e);
				}

				closeAll(null, stu, null);
				closeAll(null, sto, null);
				closeAll(null, stm, null);
				closeAll(null, str, null);

			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			} finally {
				closeAll(null, null, connRep);
			}


			long statusCode = cs.getLong(12);

			if (statusCode >= 0) {
				outputJSON("Repository Updated OK", response);
			} else {
				outputJSON("Repository Update Failed", response);
			}

			closeAll(null, cs, conn);

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

		PersistenceUtil.resetSimpleControlSessionFactory();
	}
	
	
	public Long getRepRootProjectID(String url){

		Connection connRep = PersistenceUtil.getConnection(url);
		Statement stmt = null;
		ResultSet rs2 = null;


		try {
			stmt = connRep.createStatement();
			rs2 = stmt.executeQuery("SELECT * from riskproject");

			Long rootProjectID = Long.MAX_VALUE;
			while (rs2.next()){
				if (rs2.getLong("projectID") < rootProjectID){
					rootProjectID = rs2.getLong("projectID");
				}
			}

			return rootProjectID;
		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		} finally {
			closeAll(rs2, stmt, connRep);
		}

		return null;
	}
}
