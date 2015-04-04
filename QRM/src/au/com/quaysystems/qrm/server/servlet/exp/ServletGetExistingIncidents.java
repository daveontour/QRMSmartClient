package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.CacheMode;
import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelIncident;
import au.com.quaysystems.qrm.server.RESTTransportContainer;

@SuppressWarnings("serial")
@WebServlet (value = "/getExistingIncidents", asyncSupported = false)
public class ServletGetExistingIncidents extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		sess.setCacheMode(CacheMode.REFRESH);
		outputXML(new RESTTransportContainer(sess.createCriteria(ModelIncident.class).list()),response);

	}
}
