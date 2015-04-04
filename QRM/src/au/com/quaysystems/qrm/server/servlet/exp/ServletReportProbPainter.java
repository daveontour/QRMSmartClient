package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelRepository;
import au.com.quaysystems.qrm.server.PersistenceUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/reportProbPainter", asyncSupported = false)
public class ServletReportProbPainter extends HttpServlet {

	public void doPost(final HttpServletRequest request, final HttpServletResponse response)  {
		doGet(request, response);
	}
	public void doGet(final HttpServletRequest request, final HttpServletResponse response)  {

		try {
			String riskID = (String)request.getParameter("riskID");
			String repID = (String)request.getParameter("repID");
			String state = (String)request.getParameter("state");

			Session sess2 = PersistenceUtil.getSimpleControlSession();
			@SuppressWarnings("unchecked")
			List<ModelRepository> reps = (List<ModelRepository>)sess2.createSQLQuery("SELECT * FROM repositories WHERE repID = '"+repID+"'").addEntity(ModelRepository.class).list();
			sess2.close();
			
			Connection conn = PersistenceUtil.getConnection(reps.get(0).url);


			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM  risk WHERE riskID = ?");
			stmt.setLong(1, Long.parseLong(riskID));
			ResultSet rs = stmt.executeQuery();

			rs.first();
			
			double days = (rs.getDate("endExposure").getTime() - rs.getDate("startExposure").getTime()) / (1000 * 60 * 60 * 24);

			
			ServletOutputStream out = response.getOutputStream();

			try {
				if (state.contains("pre")){
					ServletLikelihoodChart.paintLikelihoodChart(out, true, true, days, rs.getDouble("liket"), rs.getDouble("likealpha"),(rs.getLong("liketype") < 4) ? 3.0 : 4.0, rs.getDouble("likeprob"),false,false);
				}

				if (state.contains("post")){
					ServletLikelihoodChart.paintLikelihoodChart(out, true, false, days, rs.getDouble("likepostT"), rs.getDouble("likepostAlpha"), (rs.getLong("likepostType") < 4) ? 3.0 : 4.0, rs.getDouble("likepostProb"),false, false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			rs.close();
			conn.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
