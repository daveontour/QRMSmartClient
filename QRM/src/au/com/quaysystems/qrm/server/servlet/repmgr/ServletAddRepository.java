package au.com.quaysystems.qrm.server.servlet.repmgr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
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
import org.json.simple.parser.ParseException;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.ModelScheduledJob;
import au.com.quaysystems.qrm.server.JobController;
import au.com.quaysystems.qrm.server.JobScheduler;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.ScriptRunner;

@SuppressWarnings("serial")
@WebServlet (value = "/addRepository", asyncSupported = false)
public class ServletAddRepository extends QRMRPCServletRepMgr {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		try {
			JSONObject dataJS = (JSONObject) parser.parse(stringMap.get("DATA"));

			String repTitle = null;
			Long repmgr = null;
			String repname = null;
			String repemail = null;
			String reppass = null;
			String reporgcode = null;

			repTitle = (String) dataJS.get("repTitle");
			try {
				reporgcode  = (String) dataJS.get("reporgcode");
			} catch (Exception e) {
				reporgcode = null;
			}

			try {
				repmgr = getLongJS(dataJS.get("repmgr"));
			} catch (Exception e1) {
				repmgr = null;
			}
			try {
				repname = (String) dataJS.get("repname");
			} catch (Exception e) {
				repname = null;
			}
			try {
				repemail = (String) dataJS.get("repemail");
			} catch (Exception e) {
				repemail = null;
			}
			try {
				reppass = (String) dataJS.get("reppass");
			} catch (Exception e) {
				reppass = null;
			}

			newRepositoryInt(repTitle, reporgcode,repmgr, repname, repemail, reppass, response);
		} catch (ParseException e) {
			log.error("QRM Stack Trace", e);
		}

	}
	
	final void newRepositoryInt(final String title, String reporgcode, Long repmgr,
			final String user, final String email, final String password,
			HttpServletResponse response) {


		String newUserURL = null;
		try {

			Connection conn = PersistenceUtil.getQRMLoginCPDS().getConnection();
			conn.setAutoCommit(false);

			if (repmgr == null) {
				PreparedStatement ps = conn.prepareStatement("SELECT stakeholderID, name FROM stakeholders WHERE email = ?");
				ps.setString(1, email);
				ResultSet rs = ps.executeQuery();

				if (rs.first()) {
					if (!user.equalsIgnoreCase(rs.getString("name"))) {
						outputJSON("Repository Manager's email exists, but the entered name does not match the name on record.\n\nRepository *not* created", response);
						return;
					}
					repmgr = rs.getLong("stakeholderID");
				} else {
					CallableStatement cs = conn.prepareCall("call createUser(?,?,?,?)");
					cs.setString(1, user);
					cs.setString(2, password);
					cs.setString(3, email);
					cs.registerOutParameter(4, Types.BIGINT);
					cs.execute();

					repmgr = cs.getLong(4);
					cs.close();
				}
				closeAll(rs, null, null);
			}

			newUserURL = "jdbc:mysql://"+PersistenceUtil.hostAndPort+"/temp?password="+ PersistenceUtil.getHostPass()+"&user="+PersistenceUtil.getHostUser()+"&zeroDateTimeBehavior=convertToNull&noAccessToProcedureBodies=true&autoReconnect=true&rewriteBatchedStatements=true";

			PreparedStatement ps = conn.prepareStatement("INSERT INTO repositories (rep, orgcode, repmgr, url,dbUser, dbPass) VALUES (?,?,?,?,?,?)",	Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, title);
			ps.setString(2, reporgcode);
			ps.setLong(3, repmgr);
			ps.setString(4, newUserURL);
			ps.setString(5, PersistenceUtil.getHostUser());
			ps.setString(6, PersistenceUtil.getHostPass());
			ps.execute();
			ResultSet rskeys = ps.getGeneratedKeys();
			rskeys.first();
			long repID = rskeys.getLong(1);
			
			rskeys.close();
			
			String cat = "qrm"+repID;
			
			newUserURL = "jdbc:mysql://"+PersistenceUtil.hostAndPort+"/"+cat+ "?password="+ PersistenceUtil.getHostPass()+"&user="+PersistenceUtil.getHostUser()+"&zeroDateTimeBehavior=convertToNull&noAccessToProcedureBodies=true&autoReconnect=true&rewriteBatchedStatements=true";
			PreparedStatement updatePS = conn.prepareStatement("UPDATE repositories SET url = ? WHERE repID = ?");
			updatePS.setString(1, newUserURL);
			updatePS.setLong(2, repID);
			updatePS.executeUpdate();
			conn.commit();
			updatePS.close();
			
			HashMap<String, String> subs = new HashMap<String, String>();
			subs.put("CREATE DATABASE IF NOT EXISTS qrm ", "CREATE DATABASE IF NOT EXISTS "+cat);
			subs.put("USE qrm ", "USE "+cat);
			ScriptRunner runner = new ScriptRunner(conn, false, true);
			runner.setLineSubstitutions(subs);

			runner.runScript(new BufferedReader(new FileReader(sc.getServletContext().getRealPath("sql/qrm.sql"))));		
			conn.createStatement().execute("USE qrmlogin");
			conn.setAutoCommit(true);

			ps = conn.prepareStatement("INSERT INTO userrepository (stakeholderID, repID) VALUES (?,?)");
			ps.setLong(1, repmgr);
			ps.setLong(2, repID);
			ps.execute();	

			closeAll(rskeys, ps, conn);

			Connection con = DriverManager.getConnection(newUserURL,PersistenceUtil.getHostUser(), PersistenceUtil.getHostPass());

			CallableStatement ps1 = con.prepareCall("call createNewRepository(?,?,?)");
			ps1.setString(1, title);
			ps1.setLong(2, repmgr);
			ps1.registerOutParameter(3, Types.BIGINT);
			ps1.execute();

			outputJSON("New Repository Created",  response);
			closeAll(null, null, con);

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
		PersistenceUtil.resetSimpleControlSessionFactory();

		// Schedule a daily export job for the repository
		// Partly a safety measure to mitigate loss of data in case of server crash. 

		try {
			ModelScheduledJob job = new  ModelScheduledJob();
			job.internalID = null;
			job.projectID = getRepRootProjectID(newUserURL);
			job.descendants = true;
			job.reportID = QRMConstants.EXPORTRISKSEXCEL;
			job.description = "Repository "+title+" Risk Export (Excel)";
			job.repository = getRepositoryID(newUserURL);
			job.userID = repmgr;
			job.timeStr = "00:00";
			job.Mon = true;
			job.Tue = true;
			job.Wed = true;
			job.Thu = true;
			job.Fri = true;
			job.Sat = true;
			job.Sun = true;
			job.email = true;
			job.internalID = null;

			Session sess = PersistenceUtil.getSession(newUserURL);


			//Save the job request
			try {
				sess.beginTransaction();
				Long jobID = (Long)sess.save(job);
				sess.getTransaction().commit();
				job.internalID = jobID;
				JobController.addJobAndScedule( new JobScheduler(job));				
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			} 
			
			try {
				ModelScheduledJob job2 = new  ModelScheduledJob();
				job2.projectID = getRepRootProjectID(newUserURL);
				job2.descendants = true;
				job2.repository = getRepositoryID(newUserURL);
				job2.userID = repmgr;
				job2.timeStr = "00:00";
				job2.Mon = true;
				job2.Tue = true;
				job2.Wed = true;
				job2.Thu = true;
				job2.Fri = true;
				job2.Sat = true;
				job2.Sun = true;
				job2.email = true;
				job2.internalID = null;
				job2.reportID = QRMConstants.EXPORTRISKSXML;
				job2.description = "Repository "+title+" Risk Export (XML)";

				sess.beginTransaction();
				Long jobID = (Long)sess.save(job2);
				sess.getTransaction().commit();

				job2.internalID = jobID;
				JobController.addJobAndScedule( new JobScheduler(job2));

			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			} finally {
				sess.close();
			}

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
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
				long projID = rs2.getLong("projectID");
				if (projID < rootProjectID && projID > 0){
					rootProjectID = projID;
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
