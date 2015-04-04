package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelReview;

@SuppressWarnings("serial")
@WebServlet (value = "/deleteReview", asyncSupported = false)
public class ServletDeleteReview extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		try {
			boolean someFailure = false;
			Long id = Long.parseLong(stringMap.get("REVIEWID"));
			if (checkDeleteReviewSecurity(userID, id,sess)){
				sess.beginTransaction();
				ModelReview review = (ModelReview) sess.get(ModelReview.class, id);
				sess.delete(review);
				sess.getTransaction().commit();
			} else {
				someFailure = true;
			}

			outputJSONB(someFailure,response);
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

	}
}
