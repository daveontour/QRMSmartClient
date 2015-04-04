package au.com.quaysystems.qrm.server.servlet.repmgr;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.DTOTransactionMap;
import au.com.quaysystems.qrm.server.ConcurrentManager;

@SuppressWarnings("serial")
@WebServlet (value = "/getWorkQueueLengths", asyncSupported = false)
public class ServletGetWorkQueueLengths extends QRMRPCServletRepMgr {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		ArrayList<DTOTransactionMap> map = new ArrayList<DTOTransactionMap>(); 
		map.add(new DTOTransactionMap("Email Queue", new Long(ConcurrentManager.emailQueue.getQueueLength())));
		map.add(new DTOTransactionMap("Monte Carlo Queue", new Long(ConcurrentManager.monteQueue.getQueueLength())));
		map.add(new DTOTransactionMap("Report Queue", new Long(ConcurrentManager.reportQueue.getQueueLength())));
		outputJSON(map, response);


	}
}
