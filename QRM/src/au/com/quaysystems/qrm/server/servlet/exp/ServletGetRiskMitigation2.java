package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelMitigationStep;
import au.com.quaysystems.qrm.server.RESTTransportContainer;

@SuppressWarnings("serial")
@WebServlet (value = "/getRiskMitigation2", asyncSupported = false)
public class ServletGetRiskMitigation2 extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		@SuppressWarnings("unchecked")
		List<ModelMitigationStep> mits = (List<ModelMitigationStep>)sess.getNamedQuery("getRiskMitigationsteps").setLong("riskID", riskID).list();
		outputXML(new RESTTransportContainer(mits.size(), 0, mits.size() - 1, 0, (new ArrayList<ModelMitigationStep>(mits))),response);

	}
}
