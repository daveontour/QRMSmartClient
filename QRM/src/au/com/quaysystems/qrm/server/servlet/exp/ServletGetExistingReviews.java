package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelReview;
import au.com.quaysystems.qrm.server.RESTTransportContainer;

@SuppressWarnings("serial")
@WebServlet (value = "/getExistingReviews", asyncSupported = false)
public class ServletGetExistingReviews extends QRMRPCServlet {

	@SuppressWarnings("unchecked")
	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		outputXML(new RESTTransportContainer((List<ModelReview>) sess.createSQLQuery("SELECT * from review WHERE scheduledDate > NOW()").addEntity(ModelReview.class).list()),response);

	}
}
