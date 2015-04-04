package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelRiskLiteExt;
import au.com.quaysystems.qrm.server.RESTTransportContainer;

@SuppressWarnings("serial")
@WebServlet (value = "/getRiskLiteParentRPC", asyncSupported = false)
public class ServletGetRiskLiteParentRPC extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		Connection conn = getSessionConnection(request);
		CallableStatement pc = null;
		ResultSet rs = null;
		ArrayList<ModelRiskLiteExt> risks = new ArrayList<ModelRiskLiteExt>();
		HashMap<Long, ModelRiskLiteExt> parentRisks = new HashMap<Long, ModelRiskLiteExt>();
		try {
			pc = conn.prepareCall("call getFamily(?,?)");
			pc.setLong(1, projectID);
			pc.setLong(2, userID);
			rs = pc.executeQuery();
			while (rs.next()){

				ModelRiskLiteExt parent = new ModelRiskLiteExt();
				ModelRiskLiteExt child = new ModelRiskLiteExt();

				parent.riskProjectCode = rs.getString("pcode");
				parent.title = rs.getString("ptitle");
				parent.description = rs.getString("pdescription");
				parent.riskID = rs.getLong("parentID");
				parentRisks.put(parent.riskID, parent);

				child.riskProjectCode = rs.getString("ccode");
				child.title = rs.getString("ctitle");
				child.description = rs.getString("cdescription");
				child.riskID = rs.getLong("childID");
//				child.parentSummaryRisk = rs.getLong("parentID");

				risks.add(child);

			}

			for (ModelRiskLiteExt parent : parentRisks.values()){
				risks.add(parent);
			}

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} finally {
			closeAll(rs, pc, conn);			
		}

		outputXML(new RESTTransportContainer(risks.size(), 0, risks.size() - 1, 0, risks),response);

	}
}
