package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.CacheMode;
import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelDataObjectAllocation;
import au.com.quaysystems.qrm.dto.ModelMultiLevel;
import au.com.quaysystems.qrm.dto.ModelPerson;
import au.com.quaysystems.qrm.dto.ModelRiskProject;
import au.com.quaysystems.qrm.dto.ModelToleranceMatrix;

@SuppressWarnings("serial")
@WebServlet (value = "/getRiskProject", asyncSupported = false)
public class ServletGetRiskProject extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		
		try {
			sess.setCacheMode(CacheMode.IGNORE);
			ModelRiskProject proj = (ModelRiskProject) sess.get(ModelRiskProject.class, projectID);
		
			for (ModelPerson p : getProjectRiskManagers( projectID, sess)) {
				proj.riskmanagerIDs.add(p.getStakeholderID());
			}
			
			setRiskowners(getProjectRiskOwners( projectID, sess), proj, true);
			proj.matrix = getProjectMatrix( projectID, sess);
			proj.setRiskCategorys(new ArrayList<ModelMultiLevel>(getProjectCategorys( projectID, sess)));
			proj.setTolActions(getProjectTolActions( projectID, sess));
			proj.extraStuff = getAllocation( request,  response,  sess,  userID, stringMap, objMap,  projectID,  riskID);
			
			outputJSON(proj,response);
		} catch (Exception e) {
			e.printStackTrace();
			outputJSON(false,response);
		}
	}

	private String getAllocation(HttpServletRequest request, HttpServletResponse response, Session sess, Long userID, HashMap<String, String> stringMap, HashMap<Object, Object> objMap, Long projectID, Long riskID){

		boolean rolled = false;
		if (stringMap.get("ROLLED") != null) {
			rolled = Boolean.parseBoolean(stringMap.get("ROLLED"));
		}

		ModelToleranceMatrix mat = getProjectMatrix(projectID, sess);
		ArrayList<ModelDataObjectAllocation> summaries = getToleranceAllocations(sess, rolled);
		int lft = 0, rgt = 0;

		int maxProb = mat.getMaxProb();
		int maxImpact = mat.getMaxImpact();

		if (Boolean.parseBoolean(stringMap.get("DESCENDANTS"))){


			try (Connection conn = getSessionConnection(request)) {
				
				PreparedStatement ps = conn.prepareStatement("SELECT lft, rgt FROM riskproject WHERE projectID = ?");
				ps.setLong(1, projectID);

				ResultSet rs = ps.executeQuery();
				rs.first();

				lft = rs.getInt("lft");
				rgt = rs.getInt("rgt");

			} catch (Exception e) {
				log.error(e);
				return null;
			}
		}

		Integer[][] valMapT = new Integer[maxProb][maxImpact];
		Integer[][] valMapU = new Integer[maxProb][maxImpact];

		for (int x = 0; x < maxProb; x++) {
			for (int y = 0; y < maxImpact; y++) {
				valMapT[x][y] = 0;
				valMapU[x][y] = 0;
			}
		}


		for (ModelDataObjectAllocation alloc : summaries) {
			if (alloc.getMatrixID() == mat.matrixID && (( alloc.getLft() >= lft && alloc.getRgt() <= rgt && Boolean.parseBoolean(stringMap.get("DESCENDANTS")) || (alloc.getProjectID() == projectID))) ) {
				int y = alloc.getPROB() -1;
				int x = alloc.getIMPACT()-1;

				if (alloc.getFLAG() == 0){
					valMapU[y][x] = valMapU[y][x]+alloc.getRCOUNT();
				}
				if (alloc.getFLAG() == 1){
					valMapT[y][x] = valMapT[y][x]+alloc.getRCOUNT();
				}
			}
		}

		StringBuffer sbu = new StringBuffer();
		StringBuffer sbt = new StringBuffer();

		for (int y = 0; y < maxProb; y++) {
			for (int x = 0; x < maxImpact; x++) {
				sbu.append(valMapU[y][x]);
				sbu.append(":");
				sbt.append(valMapT[y][x]);
				sbt.append(":");
			}
		}

		return sbt.toString()+";"+sbu.toString();
	}

}
