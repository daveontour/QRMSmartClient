package au.com.quaysystems.qrm.server.report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import au.com.quaysystems.qrm.server.QRMTXManager;

public abstract class IReportProcessorVisitor {
	
	abstract public void process(ReportProcessorData job, HashMap<Object, Object> taskParamMap, Session sess, Long reportSessionID, QRMTXManager txmgr);

	
	protected Logger log = Logger.getLogger("au.com.quaysystems.qrm");

	public final void setReportSessionData(final long var_reportSessionID,
			final String var_dataElement, final long var_dataID,
			final String var_dataString, final int var_rank, final Connection conn) {

		try {

			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO reportsessiondata (sessionID ,dataElement ,dataID,dataString, rank) VALUES (?,?,?,?,? )");

			pstmt.setLong(1, var_reportSessionID);
			pstmt.setString(2, var_dataElement);
			pstmt.setLong(3, var_dataID);
			pstmt.setString(4, var_dataString);
			pstmt.setInt(5, var_rank);

			pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		}
	}
	
	public final void setInnerReportSessionData(final long reportSessionID,
			final String innerType, final long outerID,
			final String data, final Connection conn) {

		try {

			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO innerreportsessiondata (sessionID ,outerID ,innerType,dataBlob) VALUES (?,?,?,? )");

			pstmt.setLong(1, reportSessionID);
			pstmt.setLong(2, outerID);
			pstmt.setString(3, innerType);
			pstmt.setString(4, data);

			pstmt.executeUpdate();

		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		}
	}
}


