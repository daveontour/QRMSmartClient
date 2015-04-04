package au.com.quaysystems.qrm.server.analysis;

import java.awt.Color;
import java.awt.Font;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;

import au.com.quaysystems.qrm.QRMConstants;

public class GeneralRiskAnalysis extends ChartProcessor {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 13L;
	private  QRMCategoryURLGenerator urlGen  = new QRMCategoryURLGenerator(QRMConstants.RISK);
	private Logger log = Logger.getLogger("au.com.quaysystems.qrm");


	@Override
	public void process(final OutputStream out,
			final HashMap<Object, Object> paramMap, final Connection conn,
			final ServletContext sc, final HttpServletResponse resp,
			final HttpServletRequest req, final Session sess,Long projectID, boolean parkImage) {

		super.process(out, paramMap, conn, sc, resp, req, sess, projectID, parkImage);

		if (param1.equals("cost")) {
			title = "Mitigiation Cost - " + this.projectTitle;
			value = "Cost";
			ArrayList<CostAnalysisElement> costs = new ArrayList<CostAnalysisElement>();

			try {
				statement = conn.createStatement();
				ResultSet rs1 = statement.executeQuery("SELECT SUM(estCost) AS cost, risk.riskProjectCode FROM mitigationstep,risk WHERE risk.riskID = mitigationstep.riskID AND mitigationstep.response = 0 AND risk.projectID IN ("+ listOfProjects	+ ") " +
				" GROUP BY mitigationstep.riskID, risk.riskProjectCode ORDER BY cost");
				while (rs1.next()) {
					costs.add(new CostAnalysisElement(rs1.getString("riskProjectCode"), rs1.getDouble("cost")));
				}
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}

			Collections.sort(costs);
			if (var_reverse) {
				Collections.reverse(costs);
			}

			if (costs.size() > 0) {
				for (int i = 0; i < num && i < costs.size(); i++) {
					dataset.addValue(costs.get(i).cost, "Risks", costs.get(i).riskCode);
				}
			}
		}

		if (param1.equals("contingency") || param1.equals("contingencyWeighted")) {
			title = "Contingency Cost - " + this.projectTitle;
			value = "Cost";
			ArrayList<CostAnalysisElement> costs = new ArrayList<CostAnalysisElement>();

			try {
				statement = conn.createStatement();
				ResultSet rs1 = statement.executeQuery("SELECT estimatedContingencey,useCalculatedContingency,postMitContingency,postMitContingencyWeighted,preMitContingency,preMitContingencyWeighted, treated,riskProjectCode FROM risk WHERE risk.projectID IN ("+ listOfProjects	+ ") " );
				while (rs1.next()) {
					if (rs1.getBoolean("useCalculatedContingency")){
						if (param1.equals("contingencyWeighted")){
							title = "Weighted Contingency Cost - " + this.projectTitle;
							if (rs1.getBoolean("treated")){
								costs.add(new CostAnalysisElement(rs1.getString("riskProjectCode"), rs1.getDouble("postMitContingencyWeighted")));
							} else {
								costs.add(new CostAnalysisElement(rs1.getString("riskProjectCode"), rs1.getDouble("preMitContingencyWeighted")));
							}
						} else {
							if (rs1.getBoolean("treated")){
								costs.add(new CostAnalysisElement(rs1.getString("riskProjectCode"), rs1.getDouble("postMitContingency")));
							} else {
								costs.add(new CostAnalysisElement(rs1.getString("riskProjectCode"), rs1.getDouble("preMitContingency")));
							}
						}
					} else {
						costs.add(new CostAnalysisElement(rs1.getString("riskProjectCode"), rs1.getDouble("estimatedContingencey")));
					}
				}
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}

			Collections.sort(costs);
			if (var_reverse) {
				Collections.reverse(costs);
			}

			if (costs.size() > 0) {
				for (int i = 0; i < num && i < costs.size(); i++) {
					dataset.addValue(costs.get(i).cost, "Risks", costs.get(i).riskCode);
				}
			}
		}

		if (param1.equals("days")) {
			title = "Days Since Last Update - " + this.projectTitle;
			value = "Days";
			ArrayList<RiskDaysAnalysisElement> days = new ArrayList<RiskDaysAnalysisElement>();
			try {
				statement = conn.createStatement();
				ResultSet rs1 = statement
				.executeQuery("SELECT risk.timeUpdated, risk.riskProjectCode FROM risk WHERE risk.projectID IN ("
						+ listOfProjects
						+ ") ORDER BY timeUpdated ASC");

				while (rs1.next()) {

					try {
						days.add(new RiskDaysAnalysisElement(rs1.getString(2),	rs1.getTimestamp(1).getTime()));
					} catch (Exception e) {
						continue;
					}
				}
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}

			Collections.sort(days);
			if (var_reverse) {
				Collections.reverse(days);
			}

			long now = new java.util.Date().getTime();

			if (days.size() > 0) {
				for (int i = 0; i < num && i < days.size(); i++) {
					try {
						double numDays = (double) (now - days.get(i).date)	/ (double) (1000L * 60L * 60L * 24L);
						dataset.addValue(numDays, "Risks", days.get(i).riskCode);
					} catch (Exception e) {
						log.error("QRM Stack Trace", e);
					}
				}
			}

		}

		if (param1.equals("count")) {
			title = "Number of Control Measures - " + this.projectTitle;
			value = "Number of Measures";

			ArrayList<RiskCountAnalysisElement> controls = new ArrayList<RiskCountAnalysisElement>();
			try {
				statement = conn.createStatement();
				ResultSet rs1 = statement
				.executeQuery("SELECT COUNT(*) AS CNT, risk.riskProjectCode FROM riskcontrols, risk  WHERE risk.riskID = riskcontrols.riskID AND risk.projectID IN ("
						+ listOfProjects
						+ ") GROUP BY riskcontrols.riskID, risk.riskProjectCode ORDER BY CNT DESC");

				while (rs1.next()) {
					controls.add(new RiskCountAnalysisElement(rs1
							.getString("riskProjectCode"), rs1
							.getInt("CNT")));
				}
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}
			Collections.sort(controls);
			if (var_reverse) {
				Collections.reverse(controls);
			}

			if (controls.size() > 0) {

				for (int i = 0; i < num && i < controls.size(); i++) {
					try {
						dataset.addValue(controls.get(i).count, "Risks",controls.get(i).riskCode);
					} catch (Exception e) {
						log.error("QRM Stack Trace", e);
					}
				}
			}
		}

		if (param1.equals("mitsteps")) {
			title = "Number of Mitigation Steps - " + this.projectTitle;
			value = "Number of Steps";

			ArrayList<RiskCountAnalysisElement> mitSteps = new ArrayList<RiskCountAnalysisElement>();
			try {
				statement = conn.createStatement();
				ResultSet rs1 = statement.executeQuery("SELECT COUNT(*) AS CNT, risk.riskProjectCode FROM mitigationstep, risk  WHERE risk.riskID = mitigationstep.riskID AND risk.projectID IN ("
						+ listOfProjects
						+ ") GROUP BY mitigationstep.riskID, risk.riskProjectCode ORDER BY CNT DESC");

				while (rs1.next()) {
					mitSteps.add(new RiskCountAnalysisElement(rs1
							.getString("riskProjectCode"), rs1
							.getInt("CNT")));
				}
			} catch (SQLException e) {
				log.error("QRM Stack Trace", e);
			}

			Collections.sort(mitSteps);
			if (var_reverse) {
				Collections.reverse(mitSteps);
			}

			if (mitSteps.size() > 0) {
				for (int i = 0; i < num && i < mitSteps.size(); i++) {
					try {
						dataset.addValue(mitSteps.get(i).count, "Risks", mitSteps.get(i).riskCode);
					} catch (Exception e) {
						log.error("QRM Stack Trace", e);
					}
				}
			}
		}

		try {

			if (this.var_descendants) {
				title = title + " (including sub projects)";
			}

			JFreeChart chart = null;
			if (!var_3D) {
				chart = ChartFactory.createBarChart(title, "Risk ID", value,
						dataset, PlotOrientation.HORIZONTAL, false, true, true);
			} else {
				chart = ChartFactory.createBarChart3D(title, "Risk ID", value,
						dataset, PlotOrientation.HORIZONTAL, false, true, true);

			}

			CategoryPlot plot = (CategoryPlot) chart.getPlot();
			plot.setBackgroundPaint(Color.white);
			chart.getTitle().setFont(new Font("Arial", Font.BOLD, 12));
			BarRenderer renderer = (BarRenderer) plot.getRenderer();
			renderer.setShadowVisible(false);
			renderer.setBarPainter(new StandardBarPainter());

			renderer.setBaseItemURLGenerator(urlGen);
			final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());


			if (resp != null){
				resp.addHeader("Cache-Control", "no-cache");
				resp.addHeader("pragma", "No-Cache");
			}

			if (parkImage){
				getMapParkChart(chart, paramMap, info);
			} else {
				outputChart(chart, paramMap, out, resp, info);
			}
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	private static class CostAnalysisElement implements	Comparable<CostAnalysisElement> {

		public Double cost;
		public String riskCode;

		public CostAnalysisElement(final String code, final double cost) {
			this.cost = cost;
			this.riskCode = code;
		}

		public int compareTo(final CostAnalysisElement o) {
			return (o.cost).compareTo(cost);
		}
	}

	private static class RiskCountAnalysisElement implements Comparable<RiskCountAnalysisElement> {

		public Integer count;
		public String riskCode;

		public RiskCountAnalysisElement(final String code, final int count) {
			this.count = count;
			this.riskCode = code;
		}

		public int compareTo(final RiskCountAnalysisElement arg0) {
			return (arg0.count).compareTo(count);
		}
	}

	private static class RiskDaysAnalysisElement implements	Comparable<RiskDaysAnalysisElement> {

		public Long date;
		public String riskCode;

		public RiskDaysAnalysisElement(final String code, final long l) {
			this.date = l;
			this.riskCode = code;
		}

		public int compareTo(final RiskDaysAnalysisElement arg0) {
			return (arg0.date).compareTo(date);
		}
	}
}
