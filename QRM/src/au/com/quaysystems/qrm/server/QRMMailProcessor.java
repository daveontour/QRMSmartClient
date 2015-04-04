package au.com.quaysystems.qrm.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.ModelPerson;
import au.com.quaysystems.qrm.server.report.ReportProcessorData;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.thoughtworks.xstream.XStream;

public class QRMMailProcessor {

	private final XStream xsXML = new XStream();
	private ComboPooledDataSource cpds;
	private boolean enableMail;
	private Logger log = Logger.getLogger("au.com.quaysystems.qrm");
	static final int BUFFER_SIZE = 64 * 1024;


	public QRMMailProcessor(Properties configProp) {
		this.cpds = PersistenceUtil.getQRMLoginCPDS();
		this.enableMail = Boolean.parseBoolean(configProp.getProperty("ENABLEEMAIL"));
	}
	private boolean sendScheduledReport(ReportProcessorData job) throws Exception{

		// This writes it to a database which will be picked up by the mailing job (perl script)

		if(!enableMail) return false;

		ModelPerson person = getPerson(job.userID);
		String attachFileName = null;
		String msgBody = job.emailContent;

		if (!job.failed ){
			if (job.reportID == QRMConstants.EXPORTRISKSEXCEL){
				attachFileName = job.description+".xls";
			} else if (job.reportID == QRMConstants.EXPORTRISKSXML){
				attachFileName = "risks.xml";
			} else {
				attachFileName = job.description+".pdf";
			}
		} else {
			msgBody = "Sorry, there was an error processing your requested QRM scheduled job";
		}

		Connection conn = cpds.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO processemail (toName, toEmail, additionalUsers, emailTitle,emailType, bodyContent, attachFileName, attachZipFileName, jobID, schedJob, adminEmail, jobURL ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
			ps.setString(1, person.name);
			ps.setString(2, person.email);
			ps.setString(3, job.job.additionalUsers);
			ps.setString(4, job.emailTitle);
			ps.setString(5, "text/html");
			ps.setString(6, msgBody);
			ps.setString(7, attachFileName);
			ps.setString(8, "QRMScheduledReport.zip");
			ps.setLong(9, job.jobID);
			ps.setBoolean(10, true);
			ps.setBoolean(11, false);
			ps.setString(12, job.jdbcURL);
			ps.execute();
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			e.printStackTrace();
			return false;
		} finally {
			conn.close();
		}
		return true;
	}
	@SuppressWarnings( "rawtypes")
	public void deliver(Map dataMap) {

		if(!enableMail) return;

		try {
			ReportProcessorData job = (ReportProcessorData) xsXML.fromXML((String)dataMap.get("data"));

			if (job.schedJob){
				sendScheduledReport(job);
			}
		} catch(Exception t) {
			log.error(t);
		}		
	}
	@SuppressWarnings("unchecked")
	public void deliverAdminEmail(HashMap<String, Object> map) {

		if(!enableMail) return;

		HashMap<String, String> dataMap =  (HashMap<String, String>)map.get("data");

		try {
			Connection conn = cpds.getConnection();
			try {
				PreparedStatement ps = conn.prepareStatement("INSERT INTO processemail (emailTitle,emailType, bodyContent, schedJob, adminEmail ) VALUES (?,?,?,?,?)");
				ps.setString(1, dataMap.get("messageTitle"));
				ps.setString(2, "text/html");
				ps.setString(3, dataMap.get("messageBody"));
				ps.setBoolean(4, false);
				ps.setBoolean(5, true);
				ps.execute();
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
				e.printStackTrace();
			} finally {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private ModelPerson getPerson(Long id){
		ModelPerson person = new ModelPerson();
		Connection conn = null;
		try {
			conn = cpds.getConnection();
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM stakeholders WHERE stakeholderID = ?");
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			rs.first();

			person.setEmail(rs.getString("email"));
			person.setName(rs.getString("name"));

		} catch (Exception e1) {
			log.error("QRM Stack Trace", e1);
			e1.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("QRM Stack Trace", e);
			}
		}
		return person;
	}
}
