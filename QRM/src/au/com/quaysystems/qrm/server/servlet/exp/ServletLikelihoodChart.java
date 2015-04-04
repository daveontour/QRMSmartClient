
package au.com.quaysystems.qrm.server.servlet.exp;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;

import au.com.quaysystems.qrm.QRMConstants;

@WebServlet (value = "/QRMLikelihood/*", asyncSupported = false)
public final class ServletLikelihoodChart extends HttpServlet {
	
	private static final long serialVersionUID = 13L;
	private static final DecimalFormat pct = new DecimalFormat("0.0%");
	private static Logger log = Logger.getLogger("au.com.quaysystems.qrm");


	@Override
	public final void init() {
		log.info("Liklihood Chart Processor Started");
	}
	@Override
	protected void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		response.setContentType("image/png");
		response.addHeader("pragma", "No-Cache");

		boolean rep = false;
		if (request.getParameterMap().containsKey("rep")) {
			rep = true;
		}

		boolean preMit = false;
		if (request.getParameter("preMit").equalsIgnoreCase("TRUE")) {
			preMit = true;
		}
		boolean singlePhase = false;
		if (request.getParameter("singlePhase").equalsIgnoreCase("TRUE")) {
			singlePhase = true;
		}
		boolean treated = false;
		try {
			if (request.getParameter("treated").equalsIgnoreCase("TRUE")) {
				treated = true;
			}
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
			treated = false;
		}
		double alpha;
		double probParam;
		double T;
		double days;
		double type;

		try {
			alpha = Double.parseDouble(request.getParameter("alpha"));
		} catch (NumberFormatException e) {
			alpha = 0.0;
		}
		try {
			probParam = Double.parseDouble(request.getParameter("prob"));
		} catch (NumberFormatException e) {
			probParam = 0.0;
		}
		try {
			T = Double.parseDouble(request.getParameter("t"));
		} catch (NumberFormatException e) {
			T = 0.0;
		}
		try {
			days = Double.parseDouble(request.getParameter("days"));
		} catch (NumberFormatException e) {
			days = 0.0;
		}
		try {
			type = Integer.parseInt(request.getParameter("mitType"));
		} catch (NumberFormatException e) {
			log.error("QRM Stack Trace", e);
			type = 4;
		}

		paintLikelihoodChart(response.getOutputStream(), rep, preMit, days, T,
				alpha, type, probParam, singlePhase, treated);
	}

	public static void paintLikelihoodChart(final OutputStream out,
			final boolean isForReport, final boolean preMit, final double days,
			final double T, final double alpha, final double type,
			final double probParam, boolean singlePhase, boolean treated) {

		String title = "Post Mitigation Risk Likelihood";
		String xAxis = "Number of Occurances (N)";
		String yAxis = "Probability of N";
		DecimalFormat threeDec = new DecimalFormat("0.00");
		String probStr = (threeDec.format( probParam));

		Color color = new Color(0, 0, 255, 100);
		
		Color titleColor = preMit ? Color.RED : Color.BLUE;
		
		if (preMit) {
			title = "Pre Mitigation Risk Likelihood";
			color = new Color(255, 0, 0, 100);
		}
		
		if (singlePhase){
			title = "Risk Likelihood";
			if (treated){
				color = new Color(0, 0, 255, 100);
				titleColor = Color.BLUE;
			}
		}

		try {
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();

			double cumProb = 0.0;
			for (int n = 0; n <= 10; n++) {
				double alphat = alpha * (days / T);
				double prob = Math.exp(-alphat)	* ((Math.pow(alphat, n) / QRMConstants.fact(n)));
				cumProb += prob;
				dataset.addValue(prob, "Row 1", "" + n);
			}
			dataset.addValue(1 - cumProb, "Row 1", ">10");

			if (isForReport) {
				yAxis = "Pr(N)";
			}
			if (type == 4) {
				dataset = null;
			}

			JFreeChart chart = ChartFactory.createBarChart(title, xAxis, yAxis,	dataset, PlotOrientation.VERTICAL, false, false, false);
			chart.setBackgroundPaint(Color.white);
			chart.setBorderVisible(false);
			chart.getTitle().setFont(new Font("Verdana", Font.BOLD, isForReport ? 11 : 14));
			chart.getTitle().setPaint(titleColor);

			CategoryPlot plot = (CategoryPlot) chart.getPlot();

			final CategoryAxis xA = new CategoryAxis(xAxis);
			xA.setLabelFont(new Font("Verdana", Font.BOLD, isForReport ? 10:14));
			plot.setDomainAxis(xA);

			final NumberAxis yA = new NumberAxis(yAxis);
			yA.setLabelFont(new Font("Verdana", Font.BOLD, isForReport ? 10:14));
			plot.setRangeAxis(yA);

			plot.setBackgroundPaint(Color.white);
			plot.setRangeGridlinePaint(Color.BLACK);
			plot.setRangeGridlinesVisible(true);

			BarRenderer r1 = (BarRenderer) plot.getRenderer(0);
			r1.setSeriesPaint(0, color);
			r1.setShadowVisible(false);
			r1.setBarPainter(new StandardBarPainter());

			if (type == 4) {
				if (probParam ==0){
					plot.setNoDataMessage("Not Set");
				} else {
					plot.setNoDataMessage("Risk Probability = " + probStr + "%");					
				}
				plot.setNoDataMessageFont(new Font("Verdana", Font.BOLD, 20));
				plot.setNoDataMessagePaint(color);
				plot.setRangeGridlinesVisible(false);
				plot.setOutlineVisible(true);
			} else {
				double alphat = alpha * (days / T);
				Double prob = 1 - (Math.exp(-alphat) * ((Math.pow(alphat, 0) / QRMConstants.fact(0))));
				TextTitle subtitle1 = new TextTitle("Risk Probability = "+ pct.format(prob));
				subtitle1.setFont(new Font("Verdana", Font.PLAIN,isForReport ? 10 : 12));
				chart.addSubtitle(subtitle1);
			}

			chart.setAntiAlias(true);

			if (isForReport) {
				ChartUtilities.writeChartAsPNG(out, chart, 280, 200);
			} else {
				ChartUtilities.writeChartAsPNG(out, chart, 400, 250);
			}

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

	}
}
