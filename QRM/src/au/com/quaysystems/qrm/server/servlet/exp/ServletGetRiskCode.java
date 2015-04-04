package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskLiteBasic;

@SuppressWarnings("serial")
@WebServlet (value = "/getRiskCode", asyncSupported = false)
public class ServletGetRiskCode extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		ModelRiskLiteBasic risk = (ModelRiskLiteBasic)sess.createCriteria(ModelRiskLiteBasic.class)
				.add(Restrictions.eq("riskProjectCode", request.getParameter("RISKID")))
				.uniqueResult();


		request.getSession().setAttribute("riskContingencyPreMitCostResult", null);
		request.getSession().setAttribute("riskContingencyPostMitCostResult", null);

		try {
			ModelRisk r2 = getRisk(risk.riskID, userID,projectID, sess);
			r2.userUpdateSecurity = checkUpdateSecurity(r2.riskID, userID, request);
			outputJSON(r2,response);
		} catch (Exception e) {
			try {
				(response).getWriter().println("Error Retrieving Risk");
				return;
			} catch (IOException e1) {
				log.error("QRM Stack Trace", e1);
			}
		}
	}
}
