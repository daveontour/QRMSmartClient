package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelPersonLite;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.RESTTransportContainer;

@SuppressWarnings("serial")
@WebServlet (value = "/getAllRiskStakeholdersDS", asyncSupported = false)
public class ServletGetAllRiskStakeholdersDS extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		
		try (Connection conn = PersistenceUtil.getConnection((String) request.getSession().getAttribute("session.url"))){
			conn.setAutoCommit(true);
			PreparedStatement ps = conn.prepareStatement("Select * from allriskstakeholders where riskID = ?");
			ps.setLong(1, riskID);
			ResultSet rs = ps.executeQuery();
			ArrayList<ModelPersonLite> ppl = new ArrayList<ModelPersonLite>();
			while (rs.next()) {
				ModelPersonLite person = new ModelPersonLite();
				person.stakeholderID = rs.getLong("stakeholderID");
				person.name = rs.getString("name");
				person.email = rs.getString("email");
				person.compoundName = rs.getString("description");
				ppl.add(person);
			}
			conn.close();

			outputXML(new RESTTransportContainer(ppl.size(), 0, ppl.size() - 1,	0, ppl), response);
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} 
	}	
}
