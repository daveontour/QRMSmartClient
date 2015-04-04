package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelRepository;
import au.com.quaysystems.qrm.dto.ModelRiskLite;
import au.com.quaysystems.qrm.dto.ModelToleranceMatrix;
import au.com.quaysystems.qrm.server.MatrixPainter;
import au.com.quaysystems.qrm.server.PersistenceUtil;

@SuppressWarnings("serial")
@WebServlet (value = "/reportMatrixPainter", asyncSupported = false)
public class ServletReportMatrixPainter extends HttpServlet {
	
	public void doPost(final HttpServletRequest request, final HttpServletResponse response)  {
		doGet(request, response);
	}
	public void doGet(final HttpServletRequest request, final HttpServletResponse response)  {
		
		try {
			String riskID = (String)request.getParameter("riskID");
			String repID = (String)request.getParameter("repID");
			
				Session sess2 = PersistenceUtil.getSimpleControlSession();
			@SuppressWarnings("unchecked")
			List<ModelRepository> reps = (List<ModelRepository>)sess2.createSQLQuery("SELECT * FROM repositories WHERE repID = '"+repID+"'").addEntity(ModelRepository.class).list();
			sess2.close();
			
			Session sess = PersistenceUtil.getSession(reps.get(0).url);
			
			ModelRiskLite risk = (ModelRiskLite) sess.getNamedQuery("getRiskLite")
			.setLong("userID", 1)
			.setLong("riskID", Long.parseLong(riskID))
			.setLong("projectID", PersistenceUtil.getRootProjectID(reps.get(0).url))
			.uniqueResult();
			
				
			try {
				ImageIO.write(MatrixPainter.getPNGSingleRisk((ModelToleranceMatrix) sess.get(ModelToleranceMatrix.class, risk.matrixID), 150,150, risk), "png", response.getOutputStream());
			} catch (IOException e) {
			}

			sess.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
