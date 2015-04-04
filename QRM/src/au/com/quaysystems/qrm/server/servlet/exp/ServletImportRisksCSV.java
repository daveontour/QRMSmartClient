package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import au.com.bytecode.opencsv.CSVReader;
import au.com.quaysystems.qrm.dto.ModelImportTemplate;
import au.com.quaysystems.qrm.server.servlet.RiskInstallerHelper;

@SuppressWarnings("serial")
@WebServlet (value = "/importRisksCSV", asyncSupported = false)
public class ServletImportRisksCSV extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		Long tempFileID = null;
		JSONArray ids;
		JSONObject config = null;
		JSONObject configTemplate = null;
		Long projectID2 = null;
		List<String[]> items = null;

		ArrayList<Long> importIDs = new ArrayList<Long>();
		HashMap<String, String> configMap = new HashMap<String, String>();
		HashMap<String, Object> configMap2 = new HashMap<String, Object>();
		ArrayList<String[]> itemsToImport = new ArrayList<String[]>();

		try {
			JSONObject jsObj = (JSONObject) parser.parse(stringMap.get("DATA"));

			tempFileID = (Long)jsObj.get("tempFileID");
			projectID2 = (Long)jsObj.get("projectID");
			ids = (JSONArray)jsObj.get("ids");
			config = (JSONObject) jsObj.get("config");
			configTemplate = (JSONObject)jsObj.get("templateConfig");

			for (int i = 0; i<ids.size(); i++){
				importIDs.add(Long.parseLong((String)ids.get(i)));
			}

			for(Object key:config.keySet()){
				try {
					configMap.put((String)key, (String)config.get(key));
				} catch (Exception e) {
					configMap2.put((String)key, config.get(key));
				}
			}
			for(Object key:configTemplate.keySet()){
				try {
					configMap.put((String)key, (String)configTemplate.get(key));
				} catch (Exception e) {
					configMap2.put((String)key, configTemplate.get(key));
				}
			}			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// New Template name

		String templateName = configMap.get("newTemplateName");
		try {
			if (templateName != null){
				ModelImportTemplate ts = new ModelImportTemplate();
				ts.setUserID(userID);
				ts.setTemplateName(templateName);
				ts.setTemplate(config.toJSONString());
				sess.save(ts);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// recover the details from the uploaded file

		Connection conn = getSessionConnection(request);
		try {
			ResultSet res = conn.createStatement().executeQuery("SELECT contents FROM attachment WHERE internalID = "+ tempFileID);
			res.first();
			InputStream stream = res.getBinaryStream("contents");

			// File the itemsToImportList with the items;
			try {
				CSVReader reader = new CSVReader(new InputStreamReader(stream));
				items = reader.readAll();
				reader.close();
				for (Long index : importIDs ){
					itemsToImport.add(items.get(index.intValue()));
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			conn.createStatement().executeUpdate("DELETE FROM attachment WHERE internalID = "+ tempFileID);

		} catch (SQLException e2) {
			e2.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//Now gather up the elements to be imported. 

		HashMap<Integer, String> actionColumns = new HashMap<Integer, String>();
		HashMap<String, Integer> actionColumnsReverse = new HashMap<String, Integer>();
		for (int i=0; i<1000; i++){
			String field = configMap.get("column"+i);
			if (field == null){
				break;
			}
			if (field.endsWith("noImport")){
				continue;
			}
			actionColumns.put(i, field);
			actionColumnsReverse.put(field,i);
		}

		Integer riskKey = 0;

		if (configMap.get("key").contains("internalID")){
			riskKey = 0;
		}
		if (configMap.get("key").contains("qrmRiskCode")){
			riskKey = 1;
		}
		if (configMap.get("key").contains("externalID")){
			riskKey = 2;
		}

		boolean matchRisks = (Boolean)configMap2.get("matchRisk");

		RiskInstallerHelper installer = new RiskInstallerHelper(items,itemsToImport, actionColumns,actionColumnsReverse, configMap, riskKey, projectID2, userID,matchRisks, response,getSessionConnection(request),sess);
		if (installer.checkProbAndImpact()){
			installer.promptProbAndImpact();
			return;
		}
		installer.execute();

	}
}
