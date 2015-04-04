package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.server.montecarlo.MonteCanvas;

@SuppressWarnings("serial")
@WebServlet (value = "/getContingencyGraph", asyncSupported = false)
public class ServletGetContingencyGraph extends QRMRPCServlet {

	@SuppressWarnings("unchecked")
	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		ArrayList<Double> preMitCostResult = (ArrayList<Double>)request.getSession().getAttribute("riskContingencyPreMitCostResult");
		ArrayList<Double> postMitCostResult = (ArrayList<Double>)request.getSession().getAttribute("riskContingencyPostMitCostResult");

		MonteCanvas painter = new MonteCanvas();
		if (stringMap.get("preMit").contains("Un")){
			if (preMitCostResult == null){
				painter.setInput(null, "No Cost Consequences Found", "$ dollars", true);
			} else {
				painter.setInput(preMitCostResult, "Contengency Cost (Un Treated)", "$ dollars", true);
			}
		} else {
			if (postMitCostResult == null){
				painter.setInput(null, "No Cost Consequences Found", "$ dollars", false);
			} else {
				painter.setInput(postMitCostResult, "Contengency Cost (Treated)", "$ dollars", false);
			}
		}
		response.setHeader("Cache-Control", "no-cache");

		try {
			ImageIO.write(painter.getImage(Integer.parseInt(stringMap.get("WIDTH")), Integer.parseInt(stringMap.get("HEIGHT"))), "png", response.getOutputStream());
		} catch (Exception e1) {
			log.error("QRM Stack Trace", e1);
		}

	}
}
