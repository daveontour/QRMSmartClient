package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.DTOSearchParam;
import au.com.quaysystems.qrm.dto.ModelRiskLiteExt;
import au.com.quaysystems.qrm.server.RESTTransportContainer;

@SuppressWarnings("serial")
@WebServlet (value = "/getRiskLiteFetch", asyncSupported = false)
public class ServletGetRiskLiteFetch extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		
		DTOSearchParam searchParam = SearchStringHelper.parseSearch(stringMap, objMap, projectID, riskID, userID);

		@SuppressWarnings("unchecked")
		List<ModelRiskLiteExt> risks = sess.createSQLQuery(SearchStringHelper.getSQLSearchString(searchParam)).addEntity(ModelRiskLiteExt.class).list();
		outputXML(new RESTTransportContainer(risks.size(), 0, risks.size() - 1, 0, (new ArrayList<ModelRiskLiteExt>(risks))),response);
	}
}
