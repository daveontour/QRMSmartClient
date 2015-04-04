package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelRiskLite;
import au.com.quaysystems.qrm.dto.ModelToleranceMatrix;
import au.com.quaysystems.qrm.server.MatrixPainter;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.QRMTXManager;

@SuppressWarnings("serial")
@WebServlet (value = "/riskMatrixPainter*", asyncSupported = false)
public final class ServletRiskMatrixPainter extends HttpServlet {
	
	private QRMTXManager txmgr = new QRMTXManager();
	private Logger log = Logger.getLogger("au.com.quaysystems.qrm");


	@Override
	protected void doGet(final HttpServletRequest req,	final HttpServletResponse res) throws ServletException,	IOException {
		doPost(req, res);
	}

	@Override
	protected void doPost(final HttpServletRequest request,	final HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("image/png");
		response.addHeader("pragma", "No-Cache");
		
		Session sess = PersistenceUtil.getSession(request.getParameter("qrmCatalog"));
		ModelRiskLite risk = txmgr.getRiskLite(Long.parseLong(request.getParameter("RISKID")), 1L, PersistenceUtil.getRootProjectID(request.getParameter("qrmCatalog")), sess);
		ModelToleranceMatrix mat = (ModelToleranceMatrix) sess.get(ModelToleranceMatrix.class, risk.matrixID);

		try {
			ImageIO.write(MatrixPainter.getPNGSingleRisk(mat, Integer.parseInt(request.getParameter("WIDTH")), Integer.parseInt(request.getParameter("HEIGHT")), risk), "png", response.getOutputStream());
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} finally {
			sess.close();
		}

		response.getOutputStream().close();
	}
}
