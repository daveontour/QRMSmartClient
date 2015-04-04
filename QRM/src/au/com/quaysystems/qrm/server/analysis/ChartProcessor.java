package au.com.quaysystems.qrm.server.analysis;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public abstract class ChartProcessor {

	public Boolean var_descendants;
	public Boolean var_reverse;
	public Boolean var_3D;
	public Long var_context_id;
	public String param1;
	public DefaultCategoryDataset dataset;
	public DefaultPieDataset pieSet;
	public String title;
	public String value;
	public Connection conn = null;
	public Statement statement;
	public String listOfProjects;
	public int num;
	public Boolean bEx = true;
	public Boolean bHigh = true;
	public Boolean bSig = true;
	public Boolean bMod = true;
	public Boolean bLow = true;
	public String projectTitle;
	public String map;
	private ByteArrayOutputStream imageStream;
	private Logger log = Logger.getLogger("au.com.quaysystems.qrm");


	synchronized public String processMap(final OutputStream out,
			final HashMap<Object, Object> paramMap, final Connection co,
			final ServletContext sc, final HttpServletResponse resp,
			final HttpServletRequest req, final Session sess, Long projectID) {

		process(out, paramMap, co, sc, resp, req, sess, projectID, true);
		req.getSession().setAttribute("imageStream", imageStream);
		return map;

	}

	public void process(final OutputStream out,
			final HashMap<Object, Object> paramMap, final Connection co,
			final ServletContext sc, final HttpServletResponse resp,
			final HttpServletRequest request, final Session sess, Long projectID, boolean parkImage) {

		conn = co;

		var_descendants = (Boolean) paramMap.get("bDescend");
		
		if (var_descendants == null ){
			var_descendants = (Boolean) paramMap.get("descendants");
		}

		try {
			num = Integer.parseInt((String) paramMap.get("num"));
		} catch (ClassCastException e1) {
			try {
				num = ((Long) paramMap.get("num")).intValue();
			} catch (ClassCastException e) {
				num = (Integer) paramMap.get("num");
			}
		}
		var_reverse = (Boolean) paramMap.get("bReverse");
		var_3D = (Boolean) paramMap.get("b3D");
		try {
			var_context_id = (Long.parseLong((String) paramMap.get("contextID")));
			listOfProjects = var_context_id.toString();
		} catch (ClassCastException e1) {
			var_context_id = (Long) paramMap.get("contextID");
			listOfProjects = var_context_id.toString();
		}
		param1 = (String) paramMap.get("param1");
		dataset = new DefaultCategoryDataset();
		pieSet = new DefaultPieDataset();
		title = "Bar Chart Ttile";
		value = "Value";

		bEx = (Boolean) paramMap.get("bEx");
		bHigh = (Boolean) paramMap.get("bHigh");
		bSig = (Boolean) paramMap.get("bSig");
		bMod = (Boolean) paramMap.get("bMod");
		bLow = (Boolean) paramMap.get("bLow");

		try {
			if (var_descendants) {
				ResultSet rsTemp = conn.createStatement().executeQuery("SELECT subprojectID FROM subprojects WHERE projectID = "+ var_context_id);
				while (rsTemp.next()) {
					Long pid = rsTemp.getLong(1);
					if (pid != -1) {
						listOfProjects = listOfProjects + "," + pid;
					}
				}
			}
		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
		}
		try {
			ResultSet rsTemp = conn.createStatement().executeQuery(	"SELECT projecttitle  FROM riskproject WHERE projectID = "+ var_context_id);
			rsTemp.first();
			projectTitle = rsTemp.getString(1);

		} catch (SQLException e) {
			log.error("QRM Stack Trace", e);
			projectTitle = "";
		}
	}

	public final void outputChart(final JFreeChart chart,
			final HashMap<Object, Object> paramMap, final OutputStream out,
			final HttpServletResponse resp, ChartRenderingInfo info) {
		
		try {
			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			chart.setBackgroundPaint(Color.white);
			plot.setBackgroundPaint(Color.white);
		} catch (ClassCastException e1) {
			// Do nothing coz this may just because it is being called by the report job
		}

		try {
			chart.setAntiAlias(true);
			if (resp != null){
				resp.setContentType("image/png");
			}
			if (info != null){
				try {
					ChartUtilities.writeChartAsPNG(out, chart, 
							Integer.parseInt((String) paramMap.get("x")),
							Integer.parseInt((String) paramMap.get("y")), info);
				} catch (ClassCastException e) {
					ChartUtilities.writeChartAsPNG(out, chart, 
							((Long) paramMap.get("x")).intValue(),
							((Long) paramMap.get("y")).intValue(), info);
				}
			} else {
				try {
					ChartUtilities.writeChartAsPNG(out, chart, 
							Integer.parseInt((String) paramMap.get("x")),
							Integer.parseInt((String) paramMap.get("y")));
				} catch (Exception e) {
					ChartUtilities.writeChartAsPNG(out, chart, 
							((Long) paramMap.get("x")).intValue(),
							((Long) paramMap.get("y")).intValue(), info);
				}
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

	}

	public void getMapParkChart(final JFreeChart chart,	final HashMap<Object, Object> paramMap, ChartRenderingInfo info) {

		try {
			chart.setAntiAlias(true);

			imageStream = new ByteArrayOutputStream(); 

			if (info != null){
				ChartUtilities.writeChartAsPNG(imageStream, chart, 
						Integer.parseInt((String) paramMap.get("x")),
						Integer.parseInt((String) paramMap.get("y")), info);
				map = ChartUtilities.getImageMap("imageMap",info);

			} else {
				ChartUtilities.writeChartAsPNG(imageStream, chart, 
						Integer.parseInt((String) paramMap.get("x")),
						Integer.parseInt((String) paramMap.get("y")));
				map = null;
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}
	}
}
