package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelPerson;
import au.com.quaysystems.qrm.dto.ModelRepository;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.servlet.SessionControl;

@SuppressWarnings("serial")
@WebServlet (value = "/deleteRepository", asyncSupported = false)
public class ServletDeleteRepository extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		Connection conn = null;

		//All the foreign key constraints will take care of all the stuff
		try {
			conn = PersistenceUtil.getConnection(request.getParameter("repURL"));
			Statement st = conn.createStatement();
			st.executeUpdate("DELETE FROM riskproject");
			st.executeUpdate("DELETE FROM auditcomments");
			st.executeUpdate("DELETE FROM control_effectiveness_defn");
			st.executeUpdate("DELETE FROM incident");
			st.executeUpdate("DELETE FROM incidentconseq");
			st.executeUpdate("DELETE FROM incidentobjective");
			st.executeUpdate("DELETE FROM incidentrisk");
			st.executeUpdate("DELETE FROM metric");
			st.executeUpdate("DELETE FROM mitigationstep");
			st.executeUpdate("DELETE FROM objective");
			st.executeUpdate("DELETE FROM objectives_impacted");
			st.executeUpdate("DELETE FROM projectmetric");
			st.executeUpdate("DELETE FROM projectowners");
			st.executeUpdate("DELETE FROM projectriskmanagers");
			st.executeUpdate("DELETE FROM projectusers");
			st.executeUpdate("DELETE FROM quantimpacttype");
			st.executeUpdate("DELETE FROM reports");
			st.executeUpdate("DELETE FROM review");
			st.executeUpdate("DELETE FROM reviewrisk");
			st.executeUpdate("DELETE FROM risk");
			st.executeUpdate("DELETE FROM riskcontrols");
			st.executeUpdate("DELETE FROM riskcategory");
			st.executeUpdate("DELETE FROM riskconsequence");
			st.executeUpdate("DELETE FROM riskproject");
			st.executeUpdate("DELETE FROM subjrank");
			st.executeUpdate("DELETE FROM toldescriptors");
			st.executeUpdate("DELETE FROM tolerancematrix");
			st.executeUpdate("DELETE FROM userrepository");
			st.executeUpdate("DELETE FROM schedjob");


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error("QRM Stack Trace", e);
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("QRM Stack Trace", e);
			}
		}

		try {
			conn = PersistenceUtil.getQRMLoginCPDS().getConnection();
			conn.setAutoCommit(false);
			Statement st = conn.createStatement();
			st.executeUpdate("DELETE FROM repositories where url = '"+request.getParameter("repURL")+"'");
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error("QRM Stack Trace", e);
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				log.error("QRM Stack Trace", e);
			}
		}


		getAllRepositories(request,  response,  sess,  userID,  stringMap,  objMap,  projectID);


	}
	protected final void getAllRepositories(HttpServletRequest request, HttpServletResponse response, Session sess2, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID) {

		Session sess = PersistenceUtil.getSimpleControlSession();

		try {

			sess.setCacheMode(CacheMode.IGNORE);
			sess.setFlushMode(FlushMode.ALWAYS);

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
		} finally {
			sess.close();			
		}
	}
	@SuppressWarnings({ "unchecked" })
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
