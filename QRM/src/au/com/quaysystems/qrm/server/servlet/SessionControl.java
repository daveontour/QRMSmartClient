package au.com.quaysystems.qrm.server.servlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.apache.log4j.Logger;

import au.com.quaysystems.qrm.server.PersistenceUtil;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public final class SessionControl {
	public static HashMap<String, SessionEntry> sessionMap = new HashMap<String, SessionEntry>();
	private static Logger log = Logger.getLogger("au.com.quaysystems.qrm");


	public static int numRepSessions(String repUrl){
		int sess = 0;
		for(SessionEntry se: sessionMap.values()){
			if (se.dbURL.equalsIgnoreCase(repUrl)){
				sess++;
			}
		}
		return sess;
	}

	public static boolean repLogonOK(String repURL){

		ComboPooledDataSource cpds = PersistenceUtil.getQRMLoginCPDS();

		int maxSessions = 10;

		try (Connection conn = cpds.getConnection()){

			PreparedStatement ps = conn.prepareStatement("select sessionlimit from repositories where url = ?");
			ps.setString(1, repURL);
			ResultSet rs = ps.executeQuery();
			rs.first();

			maxSessions = rs.getInt("sessionlimit");

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			maxSessions = 0;
		} 

		if (numRepSessions(repURL) < maxSessions  || maxSessions == -1){
			return true;
		} else {
			return false;
		}
	}

	public static void logNewSession(SessionEntry sessionEntry) {
		ComboPooledDataSource cpds = PersistenceUtil.getQRMLoginCPDS();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try (Connection conn = cpds.getConnection()){

			ps = conn.prepareStatement("SELECT repID FROM repositories WHERE url = ?");
			ps.setString(1, sessionEntry.dbURL);
			rs = ps.executeQuery();
			rs.first();
			Long repID = rs.getLong("repID");

			rs.close();
			ps.close();

			ps = conn.prepareStatement("INSERT INTO repositorysession (repID,  userid, sessionid, sessioncountatstart) VALUES(?,?,?,?)");
			ps.setLong(1, repID);
			ps.setLong(2, sessionEntry.person.stakeholderID);
			ps.setString(3, sessionEntry.sessionID);
			ps.setLong(4, numRepSessions(sessionEntry.dbURL));
			ps.executeUpdate();

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} 
	}

	public static void logEndSession(String sessID) {

		ComboPooledDataSource cpds = PersistenceUtil.getQRMLoginCPDS();

		try (Connection conn = cpds.getConnection()){
			
			PreparedStatement ps = conn.prepareStatement("SELECT t1.url FROM repositories AS t1 JOIN repositorysession AS t2 ON t1.repID = t2.repID WHERE t2.sessionid = ?");
			ps.setString(1, sessID);

			ResultSet rs = ps.executeQuery();

			if (rs.first()){
				
				String url = rs.getString("url");
				rs.close();

				ps = conn.prepareStatement("UPDATE repositorysession SET sessioncountatend = ?, sessionenddate = NOW() WHERE  sessionid = ? AND sessionenddate = 0");
				ps.setLong(1, numRepSessions(url));
				ps.setString(2, sessID);
				ps.executeUpdate();
				ps.close();
				
			} else {
				System.out.println("Repository URL could not be found for Session ID: "+sessID);
			}

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} 
	}
}
