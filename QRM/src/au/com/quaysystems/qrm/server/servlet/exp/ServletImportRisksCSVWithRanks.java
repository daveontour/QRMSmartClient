package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import au.com.quaysystems.qrm.server.servlet.RiskInstallerHelper;

@SuppressWarnings("serial")
@WebServlet (value = "/importRisksCSVWithRanks", asyncSupported = false)
public class ServletImportRisksCSVWithRanks extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		try {
			JSONObject jsObj = (JSONObject) parser.parse(stringMap.get("DATA"));
			Long key = (Long)jsObj.get("tempFileID");
			String probRankStr = (String)jsObj.get("probRank");			
			String impactRankStr = (String)jsObj.get("impactRank");

			RiskInstallerHelper installer = new RiskInstallerHelper(probRankStr, impactRankStr, key,userID, response,getSessionConnection(request),sess);
			installer.execute();

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
