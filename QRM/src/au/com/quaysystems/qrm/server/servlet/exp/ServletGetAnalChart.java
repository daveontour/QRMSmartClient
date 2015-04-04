package au.com.quaysystems.qrm.server.servlet.exp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.server.analysis.ChartProcessor;

@SuppressWarnings("serial")
@WebServlet (value = "/getAnalChart", asyncSupported = false)
public class ServletGetAnalChart extends QRMRPCServlet {

	@SuppressWarnings("rawtypes")
	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		ChartProcessor processor = null;
		try {
			Class clazz = Class.forName(stringMap.get("class"));
			processor = (ChartProcessor) clazz.newInstance();
		} catch (Exception e1) {
			if (!(Boolean)objMap.containsKey("image")){
				log.error("QRM Stack Trace", e1);
			}
		}

		Connection conn = getSessionConnection(request);

		response.addHeader("Cache-Control", "no-cache");
		try {

			if ((Boolean)objMap.containsKey("map")){
				// Get the map for the image. "processMap" will park the images as a session  variable
				String map = processor.processMap(null,
						objMap, conn, request.getSession().getServletContext(),
						response, request,
						sess, Long.parseLong((String)objMap.get("contextID")));
				try {
					map = map.replaceAll("imageMap", "imageMap_"+stringMap.get("nocache"));
				} catch (Exception e) {	}
				outputJSON(map,response);
			} else if ((Boolean)objMap.containsKey("image")){
				// get the stored image
				response.addHeader("Cache-Control", "no-cache");
				response.addHeader("pragma", "No-Cache");
				ByteArrayOutputStream imageOut = ( ByteArrayOutputStream) request.getSession().getAttribute("imageStream");
				response.getOutputStream().write(imageOut.toByteArray());
			} else {
				// no map processing
				processor.process(response.getOutputStream(),
						objMap, conn, request.getSession().getServletContext(),
						response, request,
						sess, Long.parseLong((String) objMap.get("contextID")), false);
			}
		} catch (IOException e1) {
			log.error("QRM Stack Trace", e1);
		} finally {
			closeAll(null, null, conn);
		}

	}
}
