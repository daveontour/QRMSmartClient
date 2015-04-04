package au.com.quaysystems.qrm.server.servlet.exp;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.DefaultPieDataset;

import au.com.quaysystems.qrm.dto.ModelMetric;
import au.com.quaysystems.qrm.server.PersistenceUtil;
import au.com.quaysystems.qrm.server.QRMTXManager;
import au.com.quaysystems.qrm.server.servlet.MetricEval;

@WebServlet (value = "/images/paintMetric", asyncSupported = false)
public final class ServletMetricPainter extends HttpServlet {

	private static final long serialVersionUID = 13L;
	protected static final QRMTXManager txmgr = new QRMTXManager();
	private static Logger log = Logger.getLogger("au.com.quaysystems.qrm");

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	private void processRequest(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		
		Session sess = PersistenceUtil.getSession((String) request.getSession().getAttribute("session.url"));

		Long projectID = Long.parseLong(request.getParameter("projectID"));
		Long metricID =  Long.parseLong(request.getParameter("metricID"));
		Long userID = Long.parseLong(request.getParameter("userID"));
		Boolean descendants = Boolean.parseBoolean(request.getParameter("DESCENDANTS"));
		if (descendants == null){
			descendants = false;
		}


		ModelMetric metric = new ModelMetric();

		try {

			Connection conn = PersistenceUtil.getConnection((String) request.getSession().getAttribute("session.url"));
			CallableStatement cs = conn.prepareCall("call getMetricConfig(?,?)");
			cs.setLong(1, projectID);
			cs.setLong(2, metricID);

			ResultSet rs = cs.executeQuery();
			if (rs.first()){
				metric.setTitle(rs.getString("title"));
				metric.setMethod(rs.getString("method"));
				metric.setDescription(rs.getString("description"));
				metric.setGrayl(rs.getDouble("grayl"));
				metric.setGrayu(rs.getDouble("grayu"));
				metric.setGreenl(rs.getDouble("greenl"));
				metric.setGreenu(rs.getDouble("greenu"));
				metric.setYellowl(rs.getDouble("yellowl"));
				metric.setYellowu(rs.getDouble("yellowu"));
				metric.setRedl(rs.getDouble("redl"));
				metric.setRedu(rs.getDouble("redu"));
				metric.setLow(rs.getDouble("low"));
				metric.setHigh(rs.getDouble("high"));
			}

			rs.close();
			conn.close();
		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		}

		Plot plot = null;
		
		switch (metric.method){
		case "toleranceAllocation":
			plot = toleranceAllocation(request, response, sess, userID, projectID, metricID, metric,descendants);
			break;
		case "statusAllocation":
			plot = statusAllocation(request, response, sess, userID, projectID, metricID, metric,descendants);
			break;
		case "treatmentAllocation":
			plot = treatmentAllocation(request, response, sess, userID, projectID, metricID, metric,descendants);
			break;
		}
		
		plot.setOutlineVisible(false);

		response.setContentType("image/png");
		response.addHeader("pragma", "No-Cache");
		response.addHeader("Cache-Control", "No-Cache");


		final JFreeChart chart = new JFreeChart(metric.title,JFreeChart.DEFAULT_TITLE_FONT, plot, false);
		chart.setBackgroundPaint(new Color(255,255, 255));
		chart.getTitle().setFont(new Font("Verdana", Font.BOLD, 11));

		try {
			ChartUtilities.writeChartAsPNG(response.getOutputStream(), chart, 200, 200);
		} catch (IOException e) {
			log.error("QRM Stack Trace", e);
		}
		
		sess.close();
	}

//	public Plot numProjects(final HttpServletRequest request, final HttpServletResponse response,Session sess, Long userID, Long projectID, Long metricID, ModelMetric metric) {
//		
//		MetricEval me = new MetricEval((String) request.getSession().getAttribute("session.url"), userID, projectID);
//
//		final DefaultValueDataset dataset = new DefaultValueDataset(me.getNumProjects());
//		final ThermometerPlot plot = new ThermometerPlot(dataset);
//
//		plot.setUnits(ThermometerPlot.UNITS_NONE);
//		plot.setColumnRadius(10);
//		plot.setBulbRadius(20);
//		plot.setValueLocation(ThermometerPlot.RIGHT);
//		plot.setValueFont(new Font("Verdana", Font.BOLD, 12));
//		plot.setValuePaint(Color.BLACK);
//
//		return plot;
//	}
//
//	public Plot numRisks(final HttpServletRequest request, final HttpServletResponse response,Session sess, Long userID, Long projectID, Long metricID, ModelMetric metric) {
//
//		MetricEval me = new MetricEval((String)request.getSession().getAttribute("session.url"), userID, projectID);
//
//		final DefaultValueDataset dataset = new DefaultValueDataset(me.getNumRisks());
//		final ThermometerPlot plot = new ThermometerPlot(dataset);
//		plot.setUnits(ThermometerPlot.UNITS_NONE);
//		plot.setColumnRadius(10);
//		plot.setBulbRadius(15);
//		plot.setValueLocation(ThermometerPlot.RIGHT);
//		plot.setLowerBound(0.0);
//		plot.setValueFont(new Font("Verdana", Font.BOLD, 12));
//		plot.setValuePaint(Color.BLACK);
//
//		ModelMetric m = metric;
//		plot.setUpperBound(m.getRedu());
//		plot.setSubrangeInfo(ThermometerPlot.NORMAL, m.getGreenl(), m.getGreenu());
//		plot.setSubrangeInfo(ThermometerPlot.WARNING, m.getYellowl(), m.getYellowu());
//		plot.setSubrangeInfo(ThermometerPlot.CRITICAL, m.getRedl(), m.getRedu());
//		plot.setFollowDataInSubranges(true);
//
//		return plot;
//	}
//
//	public Plot percentApproveMitPlanMetric(final HttpServletRequest request, final HttpServletResponse response,Session sess, Long userID, Long projectID, Long metricID, ModelMetric metric) {
//		MetricEval me = new MetricEval((String) request.getSession().getAttribute("session.url"), userID, projectID,descendants);
//
//		final DefaultValueDataset dataset = new DefaultValueDataset(me.getPercentApproveMitPlanMetric());
//		final ThermometerPlot plot = new ThermometerPlot(dataset);
//		plot.setUnits(ThermometerPlot.UNITS_NONE);
//		plot.setColumnRadius(10);
//		plot.setBulbRadius(15);
//		plot.setValueLocation(ThermometerPlot.RIGHT);
//		plot.setValueFont(new Font("Verdana", Font.BOLD, 12));
//		plot.setValuePaint(Color.BLACK);
//
//		ModelMetric m = metric;
//		plot.setUpperBound(100.0);
//		plot.setSubrangeInfo(ThermometerPlot.NORMAL, m.getGreenl(), m.getGreenu());
//		plot.setSubrangeInfo(ThermometerPlot.WARNING, m.getYellowl(), m.getYellowu());
//		plot.setSubrangeInfo(ThermometerPlot.CRITICAL, m.getRedl(), m.getRedu());
//		plot.setFollowDataInSubranges(true);
//
//		return plot;
//	}

	public Plot toleranceAllocation(final HttpServletRequest request, final HttpServletResponse response,Session sess, Long userID, Long projectID, Long metricID, ModelMetric metric, Boolean descendants) {

		DefaultPieDataset pieSet = new DefaultPieDataset();
		MetricEval me = new MetricEval((String) request.getSession().getAttribute("session.url"), userID, projectID,descendants);

		HashMap<Integer, Double> map = me.toleranceAllocation();
		
		pieSet.setValue("Extreme", map.get(5));
		pieSet.setValue("High", map.get(4));
		pieSet.setValue("Significant", map.get(3));
		pieSet.setValue("Moderate", map.get(2));
		pieSet.setValue("Low", map.get(1));

		PiePlot p = new PiePlot(pieSet);

		p.setIgnoreZeroValues(true);
		p.setLabelLinksVisible(false);
		PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator("{2}", new DecimalFormat("0"), new DecimalFormat("0.00%"));
		p.setLabelGenerator(generator);
		p.setSimpleLabels(true);

		p.setSectionPaint("Extreme", Color.red);
		p.setSectionPaint("High", Color.orange);
		p.setSectionPaint("Significant", Color.yellow);
		p.setSectionPaint("Moderate", Color.green);
		p.setSectionPaint("Low", Color.cyan);

		return p;
	}
	
	public Plot statusAllocation(final HttpServletRequest request, final HttpServletResponse response,Session sess, Long userID, Long projectID, Long metricID, ModelMetric metric, Boolean descendants) {

		MetricEval me = new MetricEval((String) request.getSession().getAttribute("session.url"), userID,projectID,descendants);

		DefaultPieDataset pieSet = new DefaultPieDataset();
		HashMap<String, Double> map = me.statusAllocation();
		
		pieSet.setValue("Pending", map.get("Pending"));
		pieSet.setValue("Inactive", map.get("Inactive"));
		pieSet.setValue("Active", map.get("Active"));

		PiePlot p = new PiePlot(pieSet);

		p.setIgnoreZeroValues(true);
		p.setLabelLinksVisible(false);
		PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator("{0}");
		p.setLabelGenerator(generator);
		p.setSimpleLabels(true);

		
		p.setSectionPaint("Pending", Color.decode("#00BFFF"));
		p.setSectionPaint("Active", Color.decode("#FFC1C1"));
		p.setSectionPaint("Inactive", Color.decode("#E0E0E0"));

		return p;
	}

	public Plot treatmentAllocation(final HttpServletRequest request, final HttpServletResponse response,Session sess, Long userID, Long projectID, Long metricID, ModelMetric metric, Boolean descendants) {

		MetricEval me = new MetricEval((String) request.getSession().getAttribute("session.url"), userID, projectID,descendants);

		DefaultPieDataset pieSet = new DefaultPieDataset();
		HashMap<String, Double> map = me.treatmentAllocation();
		
		pieSet.setValue("Treated", map.get("Treated"));
		pieSet.setValue("Un Treated", map.get("Un Treated"));

		PiePlot p = new PiePlot(pieSet);

		p.setIgnoreZeroValues(true);
		p.setLabelLinksVisible(false);
		PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator("{0}");
		p.setLabelGenerator(generator);
		p.setSimpleLabels(true);

		
		p.setSectionPaint("Treated", Color.decode("#9AFF9A"));
		p.setSectionPaint("Un Treated", Color.decode("#FFC0CB"));

		return p;
	}
	
}
