package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.ModelRiskLite;
import au.com.quaysystems.qrm.dto.ModelToleranceMatrix;
import au.com.quaysystems.qrm.server.MatrixPainter;

@SuppressWarnings("serial")
@WebServlet (value = "/getRelMatrix", asyncSupported = false)
public class ServletGetRelMatrix extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		ArrayList<ModelRiskLite> risks = null;

		if (stringMap.get("REQUIRERISKS").contains("YES")) {
			risks = new ArrayList<ModelRiskLite>(getAllProjectRisksLite(
					userID, projectID,
					(Boolean) objMap.get("DESCENDANTS"),
					sess));
		} else {
			risks = null;
		}

		ModelToleranceMatrix mat = getProjectMatrix(projectID, sess);
		

		try (ServletOutputStream out = response.getOutputStream()){
			ImageIO.write(MatrixPainter.getPNGRelMatrix(mat, Integer.parseInt(stringMap.get("WIDTH")), 	Integer.parseInt(stringMap.get("HEIGHT")), 	risks, QRMConstants.CURRENT),
							"png", 
							out);
		} catch (Exception e) {
			//log.error("QRM Stack Trace", e);
		}

	}
}
