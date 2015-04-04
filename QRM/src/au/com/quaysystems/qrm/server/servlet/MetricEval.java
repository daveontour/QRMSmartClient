package au.com.quaysystems.qrm.server.servlet;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;

import org.apache.log4j.Logger;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.server.PersistenceUtil;

public final class MetricEval  {

	private String url;
	private Long userID;
	private Long projectID;
	private boolean descendants;
	private static Logger log = Logger.getLogger("au.com.quaysystems.qrm");

	public MetricEval(String url, Long userID, Long projectID, boolean descendants){
		this.url = url;
		this.userID = userID;
		this.projectID = projectID;
		this.descendants = descendants;
	}


	public int getNumProjects() {

		int met = 0;

		try (Connection conn = PersistenceUtil.getConnection(url)){
			ResultSet rs = callMetric("getNumProjectsMetric", conn);
			rs.first();
			met = rs.getInt("param1");
			conn.close();
		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		} 

		return met;
	}

	public int getNumRisks() {

		int risks = 0;

		try (Connection conn = PersistenceUtil.getConnection(url)){
			String sql = null;
			if (!descendants){
				sql = "SELECT COUNT(*) AS param1 FROM risk WHERE projectID = ?";
			} else {
				sql = "SELECT COUNT(*) AS param1 FROM projectriskdetails WHERE spprojectID = ?";
			}
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setLong(1, projectID);
			ResultSet rs = ps.executeQuery();
			rs.first();
			risks = rs.getInt("param1");
		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		}
		return risks;

	}

	public double getPercentApproveMitPlanMetric() {

		int totalRisk = 0;
		int mitPlans = 0;
		totalRisk = getNumRisks();

		String sql = "SELECT COUNT(*) AS CNT FROM projectriskdetails WHERE  projectriskdetails.dateMitApp IS NOT NULL AND spprojectID = ?";
		if (!descendants){
			sql = "SELECT COUNT(*) AS CNT FROM risk WHERE  risk.dateMitApp IS NOT NULL AND projectID = ?";
		}

		try(Connection conn = PersistenceUtil.getConnection(url)) {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setLong(1, projectID);
				ResultSet rs = ps.executeQuery();
				rs.first();
				mitPlans = rs.getInt("CNT");				
		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		} 

		if (totalRisk > 0) {
			Double per =  100 * (double) mitPlans / totalRisk;
			DecimalFormat twoDForm = new DecimalFormat("#.##");
			return Double.valueOf(twoDForm.format(per));
		} else {
			return 0.0;
		}
	}

	public HashMap<String,  Double> statusAllocation(){

		HashMap<String, Double> map = new HashMap<String, Double>();


		try (Connection conn = PersistenceUtil.getConnection(url)){


			map.put("Inactive", 0.0);
			map.put("Active", 0.0);
			map.put("Pending", 0.0);

			String sql = null;

			if (!descendants) { 
				sql =
						" 	SELECT COUNT(*), currentTolerance, \"Pending\" AS status from risk where startExposure > current_date  AND projectID = ?" +
								" 	UNION" +
								" 	SELECT COUNT(*), currentTolerance, \"Inactive\" AS status from risk where endExposure < current_date  AND projectID = ?" +
								" 	UNION " +
								" 	SELECT COUNT(*), currentTolerance, \"Active\" AS status from risk where startExposure <= current_date AND endExposure >= current_date  AND projectID  = ? ";
			} else {
				sql =
						" 	SELECT COUNT(*), currentTolerance, \"Pending\" AS status from projectriskdetails where startExposure > current_date  AND spprojectID = ?" +
								" 	UNION" +
								" 	SELECT COUNT(*), currentTolerance, \"Inactive\" AS status from projectriskdetails where endExposure < current_date  AND spprojectID = ?" +
								" 	UNION " +
								" 	SELECT COUNT(*), currentTolerance, \"Active\" AS status from projectriskdetails where startExposure <= current_date AND endExposure >= current_date  AND spprojectID  = ? ";				
			}

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setLong(1, projectID);
			ps.setLong(2, projectID);
			ps.setLong(3, projectID);


			ResultSet rs = ps.executeQuery();

			while (rs.next()){
				map.put(rs.getString("status"), rs.getDouble("COUNT(*)"));
			} 
		}catch (Exception e){
			e.printStackTrace();	
		} 

		return map;
	}
	public HashMap<String,  Double> treatmentAllocation(){

		HashMap<String, Double> map = new HashMap<String, Double>();

		try (Connection conn = PersistenceUtil.getConnection(url)){


			map.put("Treated", 0.0);
			map.put("Un Treated", 0.0);

			String sql = null; 

			if (!descendants){
				sql =
						" 	SELECT COUNT(*), currentTolerance, \"Treated\" AS status from risk where treated = 1  AND projectID = ?" +
								" 	UNION" +
								" 	SELECT COUNT(*), currentTolerance, \"Un Treated\" AS status from risk where treated = 0  AND projectID = ?"; 
			} else {
				sql =
						" 	SELECT COUNT(*), currentTolerance, \"Treated\" AS status from projectriskdetails where treated = 1  AND spprojectID = ?" +
								" 	UNION" +
								" 	SELECT COUNT(*), currentTolerance, \"Un Treated\" AS status from projectriskdetails where treated = 0  AND spprojectID = ?"; 

			}

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setLong(1, projectID);
			ps.setLong(2, projectID);


			ResultSet rs = ps.executeQuery();

			while (rs.next()){
				map.put(rs.getString("status"), rs.getDouble("COUNT(*)"));
			} 
		}catch (Exception e){
			e.printStackTrace();	
		} 		
		return map;
	}
	public HashMap<Integer, Double> toleranceAllocation() {

		HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		map.put(QRMConstants.ToleranceExtreme, 0.0);
		map.put(QRMConstants.ToleranceHigh, 0.0);
		map.put(QRMConstants.ToleranceSignificant, 0.0);
		map.put(QRMConstants.ToleranceModerate, 0.0);
		map.put(QRMConstants.ToleranceLow, 0.0);


		try(Connection conn = PersistenceUtil.getConnection(url)) {

			CallableStatement cs = conn.prepareCall("call getTolAllocationMetric(?,?)");
			cs.setLong(1, projectID);
			cs.setLong(2, descendants?1:0);

			ResultSet rs = cs.executeQuery();
			while (rs.next()){
				map.put(rs.getInt("currentTolerance"), rs.getDouble("COUNT"));
			}

		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		}

		return map;
	}

	private synchronized ResultSet callMetric(String metric, Connection conn){
		try {
			CallableStatement cs = conn.prepareCall("call "+metric+"(?,?)");
			cs.setLong(1, projectID);
			cs.setLong(2, userID);
			ResultSet rs = cs.executeQuery();
			return rs;
		} catch (SQLException e) {
			return null;
		}
	}
}
