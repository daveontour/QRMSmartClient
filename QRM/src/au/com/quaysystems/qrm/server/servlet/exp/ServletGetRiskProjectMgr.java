package au.com.quaysystems.qrm.server.servlet.exp;

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

import au.com.quaysystems.qrm.dto.ModelMetric;
import au.com.quaysystems.qrm.dto.ModelMetricProject;
import au.com.quaysystems.qrm.dto.ModelPerson;
import au.com.quaysystems.qrm.server.PersistenceUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/getRiskProjectMgr", asyncSupported = false)
public class ServletGetRiskProjectMgr extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		Connection conn = null;
		try {
			Object[] arr = new Object[4];
			arr[0] = getRiskProjectDetails(projectID,	sess);
			arr[2] = getProjectMetrics(sess, projectID).values().toArray();
			arr[3] = sess.getNamedQuery("getAllReports").list();

			conn = PersistenceUtil.getQRMLoginCPDS().getConnection();
			conn.setAutoCommit(true);
			PreparedStatement ps = conn.prepareCall("call getRepUsersFromRepURL(?)");
			ps.setString(1, (String) request.getSession().getAttribute("session.url"));
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
			arr[1] = ppl;

			outputJSON(arr, response);

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} finally {
			closeAll(null,null,conn);
		}
	}
	
	@SuppressWarnings("unchecked")
	private HashMap<Long, Object> getProjectMetrics(Session sess, Long projectID) {

		// Get all the metric, then the project specific metrics (by replacing
		// entries in a hash table

		List<ModelMetric> metrics = (List<ModelMetric>)sess.createSQLQuery("select * from metric").addEntity(ModelMetric.class).list();
		List<ModelMetricProject> custommetrics = (List<ModelMetricProject>)sess
				.createSQLQuery("select * from projectmetric where projectID = :projectID")
				.addEntity(ModelMetricProject.class)
				.setLong("projectID", projectID).list();

		HashMap<Long, Object> map = new HashMap<Long, Object>();
		for (ModelMetric met : metrics) {
			if (met.configLimit || met.configRange) {
				map.put(met.metricID, met);
			}
		}
		for (ModelMetricProject met : custommetrics) {
			if (met.configLimit || met.configRange) {
				ModelMetric tm = (ModelMetric) map.get(met.metricID);

				met.title = tm.title;
				met.description = tm.description;
				met.method = tm.method;

				map.put(met.metricID, met);
			}
		}

		return map;
	}

}
