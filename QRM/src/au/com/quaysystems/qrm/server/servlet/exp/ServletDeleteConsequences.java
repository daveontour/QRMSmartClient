package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelRiskConsequence;

@SuppressWarnings("serial")
@WebServlet (value = "/deleteConsequences", asyncSupported = false)
public class ServletDeleteConsequences extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		
		sess.beginTransaction();
		sess.delete((ModelRiskConsequence) sess.get(ModelRiskConsequence.class,Long.parseLong(request.getParameter("ID"))));
		sess.getTransaction().commit();
		outputJSON(getRiskConsequences(riskID, sess),response);
	}
}
