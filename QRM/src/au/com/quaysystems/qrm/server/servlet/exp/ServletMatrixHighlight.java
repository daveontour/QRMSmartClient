package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.server.MatrixPainter;

@SuppressWarnings("serial")
@WebServlet (value = "/matrixHighlight", asyncSupported = false)
public class ServletMatrixHighlight extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		try {
			ImageIO.write(MatrixPainter.drawHighLight(Integer.parseInt(stringMap.get("elementWidth"))),
					"png", response.getOutputStream());
		} catch (Exception e1) {
			try {
				ImageIO.write(MatrixPainter.drawHighLight(140), "png",response.getOutputStream());
			} catch (Exception e2) {
				log.error("QRM Stack Trace", e2);
			}
		}
	}
}
