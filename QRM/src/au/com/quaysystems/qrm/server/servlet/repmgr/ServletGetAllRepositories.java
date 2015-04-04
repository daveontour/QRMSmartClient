package au.com.quaysystems.qrm.server.servlet.repmgr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelPerson;
import au.com.quaysystems.qrm.dto.ModelRepository;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.servlet.SessionControl;

@SuppressWarnings("serial")
@WebServlet (value = "/getAllRepositories", asyncSupported = false)
public class ServletGetAllRepositories extends QRMRPCServletRepMgr {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		try {

			List<ModelRepository> reps = getAllReps();

			Connection conn = PersistenceUtil.getQRMLoginCPDS().getConnection();
			conn.setAutoCommit(true);
			PreparedStatement ps = conn.prepareCall("CALL getAllRepMgrs()");
			ResultSet rs = ps.executeQuery();
			ArrayList<ModelPerson> ppl = new ArrayList<ModelPerson>();
			while (rs.next()) {
				ModelPerson person = new ModelPerson();
				person.stakeholderID = rs.getLong("stakeholderID");
				person.name = rs.getString("name");
				person.email = rs.getString("email");
				ppl.add(person);
			}
			closeAll(rs, ps, conn);
			outputJSON(new Object[] { reps, ppl }, response);

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

	}
	
	@SuppressWarnings("unchecked")
	private List<ModelRepository> getAllReps(){
		Session sess = PersistenceUtil.getSimpleControlSession();
		List<ModelRepository> reps = (List<ModelRepository>)sess.createSQLQuery("SELECT * FROM repositories")
				.addEntity(ModelRepository.class)
				.list();
		for (ModelRepository rep : reps){
			rep.sessions = SessionControl.numRepSessions(rep.url);
		}
		sess.close();
		return reps;
	}

}
