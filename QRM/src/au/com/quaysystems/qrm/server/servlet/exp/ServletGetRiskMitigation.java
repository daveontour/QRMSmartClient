package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelMitigationStep;

@SuppressWarnings("serial")
@WebServlet (value = "/getRiskMitigation", asyncSupported = false)
public class ServletGetRiskMitigation extends QRMRPCServlet {

	@SuppressWarnings("unchecked")
	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		outputJSON((List<ModelMitigationStep>)sess.getNamedQuery("getRiskMitigationsteps").setLong("riskID", riskID).list(),response);

	}

}
