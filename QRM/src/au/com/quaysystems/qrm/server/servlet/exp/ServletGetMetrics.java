package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.DTOMetricElement;
import au.com.quaysystems.qrm.dto.ModelMetric;

@SuppressWarnings("serial")
@WebServlet (value = "/getMetrics", asyncSupported = false)
public class ServletGetMetrics extends QRMRPCServlet {

	@SuppressWarnings("unchecked")
	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		ArrayList<DTOMetricElement> list = new ArrayList<DTOMetricElement>();
		try {
			
			for (ModelMetric metric : (List<ModelMetric>) sess.createCriteria(ModelMetric.class).list()) {
				DTOMetricElement elem = new DTOMetricElement(metric.metricID,userID, projectID,(Boolean) objMap.get("DESCENDANTS"));
				list.add(elem);
			}
			outputJSON(list,response);
		} catch (Exception e) {
			try {
				log.error("QRM Stack Trace", e);
				response.setStatus(405);
				response.getWriter().println("Error retrieving metrics data");
				return;
			} catch (IOException e1) {
				log.error("QRM Stack Trace", e1);
			}
		}
	}
}
