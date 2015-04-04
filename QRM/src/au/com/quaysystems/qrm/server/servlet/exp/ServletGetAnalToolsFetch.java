package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.DTOAnalysisConfigElements;
import au.com.quaysystems.qrm.dto.ModelAnalConfig;
import au.com.quaysystems.qrm.server.RESTTransportContainer;

@SuppressWarnings("serial")
@WebServlet (value = "/getAnalToolsFetch", asyncSupported = false)
public class ServletGetAnalToolsFetch extends QRMRPCServlet {

	@SuppressWarnings("unchecked")
	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		ArrayList<ModelAnalConfig> rawTools = new ArrayList<ModelAnalConfig>(sess.createCriteria(ModelAnalConfig.class).list());
		ArrayList<DTOAnalysisConfigElements> cfg = new ArrayList<DTOAnalysisConfigElements>();
		if (rawTools != null) {
			for (ModelAnalConfig s : rawTools) {
				cfg.add(new DTOAnalysisConfigElements(s.title, s.clazz,
						s.param1, s.b3D, s.bTol, s.bMatrix, s.bContext,
						s.bDescend, s.bNumElem, s.bReverse));
			}
		}
		outputXML(new RESTTransportContainer(cfg.size(), 0, cfg.size(), 0,
				(new ArrayList<DTOAnalysisConfigElements>(cfg.subList(0, cfg.size())))),response);

	}
}
