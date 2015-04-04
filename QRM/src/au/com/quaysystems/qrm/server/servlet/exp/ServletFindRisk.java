package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import au.com.quaysystems.qrm.dto.ModelRiskLiteBasic;

@SuppressWarnings("serial")
@WebServlet (value = "/findRisk", asyncSupported = false)
public class ServletFindRisk extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		ModelRiskLiteBasic risk = (ModelRiskLiteBasic)sess.createCriteria(ModelRiskLiteBasic.class)
				.add(Restrictions.eq("riskProjectCode", request.getParameter("RISKID")))
				.uniqueResult();

		try {
			boolean secOK = checkViewSecurity(risk.riskID, userID, risk.securityLevel, risk.projectID, request);
			if (secOK){
				outputJSONB(sess.getNamedQuery("getRiskLite").setLong("userID", userID).setLong("riskID", risk.riskID).setLong("projectID", risk.projectID).uniqueResult(),response);
			} else {
				outputJSONB(null,response);
			}
		} catch (Exception e) {
			outputJSONB(null,response);
		}
	}
}
