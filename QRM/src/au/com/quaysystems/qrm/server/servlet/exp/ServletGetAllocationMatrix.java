package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelDataObjectAllocation;
import au.com.quaysystems.qrm.dto.ModelToleranceMatrix;
import au.com.quaysystems.qrm.server.MatrixPainter;

@SuppressWarnings("serial")
@WebServlet (value = "/getAllocationMatrix", asyncSupported = false)
public class ServletGetAllocationMatrix extends QRMRPCServlet {
	
	protected ArrayList<ModelDataObjectAllocation> allocCache = null;
	protected String allocCacheID = null;
	protected ModelToleranceMatrix allocCacheMat = null;


	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		// This is called twice for the Risk Table Display. As a performance improvement, use a cached 
		// mat and allocation if possible.
		// ALLOC ID is set by the client and is the same for both treated and untreated

		ModelToleranceMatrix mat = allocCacheMat;
		ArrayList<ModelDataObjectAllocation> alloc = allocCache;

		boolean rolled = false;
		if (stringMap.get("ROLLED") != null) {
			rolled = true;
		}


		if (!stringMap.get("ALLOCID").equalsIgnoreCase(allocCacheID)){

			alloc = getToleranceAllocations(sess,rolled);
			allocCache = alloc;
			mat = getProjectMatrix(projectID, sess);
			allocCacheMat  = mat;

			allocCacheID = stringMap.get("ALLOCID");
		}


		response.setHeader("Cache-Control", "no-cache");

		int lft = 0, rgt = 0;

		if (Boolean.parseBoolean(stringMap.get("DESCENDANTS"))){

			Connection conn = getSessionConnection(request);

			try {
				PreparedStatement ps = conn.prepareStatement("SELECT lft, rgt FROM riskproject WHERE projectID = ?");
				ps.setLong(1, projectID);

				ResultSet rs = ps.executeQuery();
				rs.first();

				lft = rs.getInt("lft");
				rgt = rs.getInt("rgt");

			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
				return;
			} finally {
				closeAll(null, null, conn);
			}
		}

		try {
			ImageIO.write(MatrixPainter.getAllocationMatrix(
					mat,
					Integer.parseInt(stringMap.get("WIDTH")), 
					Integer.parseInt(stringMap.get("HEIGHT")),
					Boolean.parseBoolean(stringMap.get("DESCENDANTS")),
					Integer.parseInt(stringMap.get("STATE")),
					projectID, 
					alloc,lft, rgt),
					"png", response.getOutputStream());
		} catch (IOException e) {
			log.error("QRM Stack Trace", e);
		} finally {
			try {
				response.getOutputStream().flush();
			} catch (IOException e) {
				log.error("QRM Stack Trace", e);
			}
		}


	}
}
