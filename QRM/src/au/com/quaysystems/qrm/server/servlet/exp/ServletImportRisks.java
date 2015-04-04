package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.IOException;
import java.io.InputStream;
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

import au.com.quaysystems.qrm.dto.ModelMitigationStep;
import au.com.quaysystems.qrm.dto.ModelRisk;
import au.com.quaysystems.qrm.dto.ModelRiskConsequence;
import au.com.quaysystems.qrm.dto.ModelRiskControl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

@SuppressWarnings("serial")
@WebServlet (value = "/importRisks", asyncSupported = false)
public class ServletImportRisks extends QRMRPCServlet {

	@SuppressWarnings("unchecked")
	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		ArrayList<Long> importIDs = new ArrayList<Long>(); 

		try {
			for(Object obj:(JSONArray) parser.parse(stringMap.get("DATA"))){
				importIDs.add(getLongJS(obj));
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

		Long tempFileID = importIDs.get(0);
		importIDs.remove(0);

		//////
		Connection conn = getSessionConnection(request);

		List<ModelRisk> risks = null;
		try {
			ResultSet res = conn.createStatement().executeQuery("SELECT contents FROM attachment WHERE internalID = "+ tempFileID);
			res.first();
			InputStream stream = res.getBinaryStream("contents");

			XStream xsXML = new XStream() {
				protected MapperWrapper wrapMapper(MapperWrapper next) {
					return new MapperWrapper(next) {
						@SuppressWarnings("rawtypes")
						public boolean shouldSerializeMember(Class definedIn, String fieldName) {
							if (definedIn == Object.class) {
								return false;
							}
							return super.shouldSerializeMember(definedIn, fieldName);
						}
					};
				}
			};
			xsXML.alias("risk", ModelRisk.class);
			xsXML.alias("control", ModelRiskControl.class);
			xsXML.alias("mitigationstep", ModelMitigationStep.class);
			xsXML.alias("riskconsequence", ModelRiskConsequence.class);
			xsXML.setMode(XStream.NO_REFERENCES);

			risks = (List<ModelRisk>)xsXML.fromXML(stream);
			
			System.out.println(xsXML.toXML(risks));

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

		/////////

		//		List<ModelRisk> risks = (List<ModelRisk>) request.getSession().getAttribute("importedRisks");
		ArrayList<ModelRisk> risksToSave = new ArrayList<ModelRisk>();

		for (Long id:importIDs){
			for (ModelRisk risk:risks){
				risk.projectID = null;
				if (id.longValue() == risk.riskID.longValue()){
					risksToSave.add(risk);
					continue;
				}
			}
		}

		try {
			response.getWriter().println("Import Risks: ");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			log.error("QRM Stack Trace", e1);
		}

		for (ModelRisk risk:risksToSave){
			try {
				installSingleRisk(risk, null, userID,  projectID,  sess,  response);
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}
		}
	}
}
