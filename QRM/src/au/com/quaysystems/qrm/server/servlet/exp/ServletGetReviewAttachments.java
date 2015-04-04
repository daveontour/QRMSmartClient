package au.com.quaysystems.qrm.server.servlet.exp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.DTOAttachmentHolder;

@SuppressWarnings("serial")
@WebServlet (value = "/getReviewAttachments", asyncSupported = false)
public class ServletGetReviewAttachments extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		try {

			String sql = "SELECT internalID, attachmentFileName, attachmentURL, fileType, description, hostID, hostType FROM attachment WHERE hostID="
					+ stringMap.get("REVIEWID")	+ " AND hostType='REVIEW'";
			Connection conn = getSessionConnection(request);
			Statement st = conn.createStatement();

			ArrayList<DTOAttachmentHolder> pairs = new ArrayList<DTOAttachmentHolder>();
			try {
				ResultSet rs = st.executeQuery(sql);
				while (rs.next()) {
					String desc = rs.getString("description");
					String url = rs.getString("attachmentURL");
					Long internalID = rs.getLong("internalID");

					String urlStr = null;
					String filename = rs.getString("attachmentFileName");
					if ((filename != null) && (filename.length() > 0)) {
						urlStr = "./QRMAttachment?getAttachment=true&ATTACHMENTID="+ internalID;
					}
					pairs.add(new DTOAttachmentHolder(desc, url, urlStr, internalID));
				}
				closeAll(rs, st, conn);
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
				outputJSONB(false,response);
				return;
			} finally {
				closeAll(null, st, conn);
			}

			outputJSONB(pairs,response);

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			outputJSONB(false,response);
		}
	}
}
