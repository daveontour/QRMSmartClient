package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.server.PersistenceUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/addMitigationUpdate", asyncSupported = false)
public class ServletAddMitigationUpdate extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		if (!checkUpdateSecurity(riskID, userID, request)) {
			outputJSON(getRisk(riskID, userID, projectID, sess),response);
			return;
		}

		try (Connection conn = 	PersistenceUtil.getConnection((String) request.getSession().getAttribute("session.url"))){			
			Long mitStepID = Long.parseLong(stringMap.get("MITSTEPID"));
			conn.createStatement().executeUpdate("INSERT INTO `updatecomment`(`hostID`,`hostType`,`description`,`personID`) VALUES("+mitStepID+", 'MITIGATION', '"+stringMap.get("UPDATE")+"', "+userID+")");			
		} catch(Exception e){
			log.error("QRM Stack Trace", e);
		}
		outputJSON(getRisk(riskID, userID,projectID, sess),response);
		notifyUpdate(Long.parseLong(stringMap.get("RISKID")),request);
	}
}
