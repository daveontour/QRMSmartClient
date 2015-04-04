package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.server.MatrixPainter;

@SuppressWarnings("serial")
@WebServlet (value = "/relMatrixItem", asyncSupported = false)
public class ServletRelMatrixItem extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		try {
			ImageIO.write(MatrixPainter.drawRelMatrixItem(
					(Boolean) objMap.get("TREATED"),
					(Boolean) objMap.get("HIGHLIGHT"),
					stringMap.get("ID")), "png", response.getOutputStream());
		} catch (Exception e1) {
			log.error("QRM Stack Trace", e1);
		}
	}
}
