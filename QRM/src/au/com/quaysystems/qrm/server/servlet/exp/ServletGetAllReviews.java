package au.com.quaysystems.qrm.server.servlet.exp;

import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelReview;

@SuppressWarnings("serial")
@WebServlet (value = "/getAllReviews", asyncSupported = false)
public class ServletGetAllReviews extends QRMRPCServlet {

	final String sql = "SELECT reviewrisk.*, risk.riskProjectCode, review.* FROM reviewrisk "+ 
			"JOIN risk ON reviewrisk.riskID = risk.riskID "+ 
			"JOIN review ON reviewrisk.reviewID = review.reviewID "+
			"WHERE risk.riskProjectCode =";

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {


		String riskCode = stringMap.get("riskCode");
		
		if (riskCode != null){
			outputJSON(sess.createSQLQuery(sql+"'"+riskCode+"'").addEntity(ModelReview.class).list(),response);
		} else {
			outputJSON(sess.createCriteria(ModelReview.class).list(),response);
		}

	}
}
