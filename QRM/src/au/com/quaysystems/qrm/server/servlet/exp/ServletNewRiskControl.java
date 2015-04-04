package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;

import au.com.quaysystems.qrm.dto.ModelRiskControl;

@SuppressWarnings("serial")
@WebServlet (value = "/newRiskControl", asyncSupported = false)
public class ServletNewRiskControl extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		// Check if the user has update rights to the risk.
		if (!checkUpdateSecurity(riskID, userID, request)) {
			outputJSON(getRisk(riskID, userID, projectID, sess),response);
			return;
		}
		Transaction tx = sess.beginTransaction();
		ModelRiskControl control = new ModelRiskControl();

		control.setRiskID(riskID);
		control.setControl("Enter the details of the control measure in place");
		control.setEffectiveness(1);
		control.setContribution("Minor");

		sess.save(control);
		tx.commit();

		outputJSON(getRisk(riskID, userID,projectID, sess),response);
		notifyUpdate(Long.parseLong(stringMap.get("RISKID")),request);
	}
}
