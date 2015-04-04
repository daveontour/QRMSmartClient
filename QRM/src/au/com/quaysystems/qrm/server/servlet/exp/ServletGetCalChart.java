package au.com.quaysystems.qrm.server.servlet.exp;

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.ModelRiskLite;
import au.com.quaysystems.qrm.server.analysis.QRMCategoryURLGenerator;

@SuppressWarnings("serial")
@WebServlet (value = "/getCalChart", asyncSupported = false)
public class ServletGetCalChart extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {

		boolean parkImage = Boolean.parseBoolean(stringMap.get("map"));
		if (!parkImage){
			try {
				response.addHeader("Cache-Control", "no-cache");
				response.addHeader("pragma", "No-Cache");
				ByteArrayOutputStream imageOut = ( ByteArrayOutputStream) request.getSession().getAttribute("calStream");
				response.getOutputStream().write(imageOut.toByteArray());
			} catch (IOException e) {
				log.error("QRM Stack Trace", e);
			}			
			return;
		}


		List<ModelRiskLite> risks = getProjectRisksForUser(
				userID, 
				Long.parseLong(stringMap.get("contextID")),
				(Boolean.parseBoolean(stringMap.get("bDescend"))) ? 1 : 0, 
						sess);


		Collections.sort(risks, new RiskComparator());

		final TaskSeries s1 = new TaskSeries("Risk Exposure");
		for (ModelRiskLite risk : risks){
			s1.add(new Task(risk.riskProjectCode, new SimpleTimePeriod(risk.startExposure, risk.endExposure)));
		}

		TaskSeriesCollection collection = new TaskSeriesCollection();
		collection.add(s1);

		final JFreeChart chart = ChartFactory.createGanttChart("Risk Exposure Calendar","Risk ID","Date",collection,false, false,false); 

		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		chart.getTitle().setFont(new Font("Arial", Font.BOLD, 14));
		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setShadowVisible(false);
		renderer.setBarPainter(new StandardBarPainter());
		renderer.setBaseItemURLGenerator(new QRMCategoryURLGenerator(QRMConstants.RISK));
		final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());

		if (response != null){
			response.addHeader("Cache-Control", "no-cache");
			response.addHeader("pragma", "No-Cache");
		} 

		String map = getMapParkChart(chart, objMap, info, request);
		try {
			outputJSON(map.replaceAll("imageMap", "imageMap_"+stringMap.get("nocache")),response);
		} catch (Exception e) {	}

	}
	
	protected String getMapParkChart(final JFreeChart chart,	final HashMap<Object, Object> paramMap, ChartRenderingInfo info, HttpServletRequest req) {

		try {
			chart.setAntiAlias(true);
			ByteArrayOutputStream imageStream = new ByteArrayOutputStream(); 

			if (info != null){
				ChartUtilities.writeChartAsPNG(imageStream, chart, 
						Integer.parseInt((String) paramMap.get("x")),
						Integer.parseInt((String) paramMap.get("y")), info);

				req.getSession().setAttribute("calStream", imageStream);

				return ChartUtilities.getImageMap("imageMap",info);

			} else {
				ChartUtilities.writeChartAsPNG(imageStream, chart, 
						Integer.parseInt((String) paramMap.get("x")),
						Integer.parseInt((String) paramMap.get("y")));
				return null;
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

		return null;

	}

	protected final class RiskComparator implements Comparator<ModelRiskLite> {
		public int compare(final ModelRiskLite r1, final ModelRiskLite r2) {
			return r1.startExposure.compareTo(r2.startExposure);
		}
	}
}
