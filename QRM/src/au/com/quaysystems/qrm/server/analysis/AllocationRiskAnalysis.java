package au.com.quaysystems.qrm.server.analysis;

import java.awt.Color;
import java.awt.Font;
import java.io.OutputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
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
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.TableOrder;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.dto.ModelMultiLevel;
import au.com.quaysystems.qrm.dto.ModelPerson;
import au.com.quaysystems.qrm.server.QRMTXManager;

public class AllocationRiskAnalysis extends ChartProcessor {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 13L;

	private QRMDataSet dataset = new QRMDataSet();
	private DefaultPieDataset pieSet = new DefaultPieDataset();
	private QRMCategoryURLGenerator urlGen  = new QRMCategoryURLGenerator(QRMConstants.STATUS);
	private String cat;
	private Logger log = Logger.getLogger("au.com.quaysystems.qrm");

	private PiePlot piePlot;
	@SuppressWarnings("unchecked")
	@Override
	public final void process(final OutputStream out,
			final HashMap<Object, Object> paramMap, final Connection conn,
			final ServletContext sc, final HttpServletResponse resp,
			final HttpServletRequest req, final Session sess, final Long projectID, boolean parkImage) {

		super.process(out, paramMap, conn, sc, resp, req, sess, projectID, parkImage);

		ArrayList<Long> ids = new ArrayList<Long>();

		QRMTXManager txmgr = new QRMTXManager();

		HashMap<Long, ModelPerson> ppl = new HashMap<Long, ModelPerson>();
		HashMap<Long, ModelMultiLevel> lev = new HashMap<Long, ModelMultiLevel>();
		HashMap<String, Long > idMap = new HashMap<String, Long >();

		if (param1.equals("categories")) {
			for (ModelMultiLevel p : txmgr.getProjectCategorys(projectID, sess)) {
				lev.put(p.getInternalID(), p);
				idMap.put(p.getName(), p.internalID);
			}
			ModelMultiLevel unCat = new ModelMultiLevel();
			unCat.setDescription("Un Categorised");
			lev.put(0L, unCat);
			idMap.put("Un Categorised", 0L);
			urlGen  = new QRMCategoryURLGenerator(QRMConstants.CATEGORIES, idMap);
		} else {
			for (ModelPerson p : new ArrayList<ModelPerson>(sess.getNamedQuery("getAllRepPersonLite").list())) {
				ppl.put(p.getStakeholderID(), p);
				idMap.put(p.name, p.getStakeholderID());
			}
			urlGen  = new QRMCategoryURLGenerator((param1.equals("owners"))?QRMConstants.OWNERS:QRMConstants.MANAGERS, idMap);
		}

		if (param1.equals("owners") || param1.equals("managers") || param1.equals("categories")) {

			String analysisItem = "ownerID";

			if (param1.equals("owners")) {
				title = "Allocation by Risk Owner - " + this.projectTitle;
				cat = "Risk Owner";
				value = "Number Assigned";
				analysisItem = "ownerID";
			}
			if (param1.equals("managers")) {
				title = "Allocation by Risk Manager - " + this.projectTitle;
				cat = "Risk Manager";
				value = "Number Assigned";
				analysisItem = "manager1ID";
			}
			if (param1.equals("categories")) {
				title = "Allocation by Risk Category - " + this.projectTitle;
				cat = "Risk Categories";
				value = "Number in Category";
				analysisItem = "primCatID";
			}


			try {

				HashMap<Integer,HashMap<Long, Double>> map = new HashMap<Integer,HashMap<Long, Double>>();
				map.put(QRMConstants.ToleranceExtreme, new HashMap<Long, Double>());
				map.put(QRMConstants.ToleranceHigh, new HashMap<Long, Double>());
				map.put(QRMConstants.ToleranceSignificant, new HashMap<Long, Double>());
				map.put(QRMConstants.ToleranceModerate, new HashMap<Long, Double>());
				map.put(QRMConstants.ToleranceLow, new HashMap<Long, Double>());

				ResultSet rs = conn.createStatement().executeQuery("SELECT "+ analysisItem+ " AS analysisItem, COUNT(*), currentTolerance FROM risk WHERE projectID IN ("+ listOfProjects + ") GROUP BY currentTolerance,"+ analysisItem);
				while (rs.next()){
					map.get(rs.getInt("currentTolerance")).put(rs.getLong("analysisItem"), rs.getDouble("COUNT(*)"));
					if (!ids.contains(rs.getLong("analysisItem"))) {
						ids.add(rs.getLong("analysisItem"));
					}
				}

				// need to do this after all the ids are filled
				if (bEx) {
					for (Long id : ids) {
						Double val = map.get(QRMConstants.ToleranceExtreme).get(id);
						if (val == null)val = 0.0;
						try {
							dataset.addValue(val, "Extreme", ppl.get(id).getName());
						} catch (Exception e) {
							dataset.addValue(val, "Extreme", lev.get(id).getName());
						}
					}
				}
				if (bHigh) {
					for (Long id : ids) {
						Double val = map.get(QRMConstants.ToleranceHigh).get(id);
						if (val == null)val = 0.0;
						try {
							dataset.addValue(val, "High", ppl.get(id).getName());
						} catch (Exception e) {
							dataset.addValue(val, "High", lev.get(id).getName());
						}
					}
				}
				if (bSig) {
					for (Long id : ids) {
						Double val = map.get(QRMConstants.ToleranceSignificant).get(id);
						if (val == null)val = 0.0;
						try {
							dataset.addValue(val, "Significant", ppl.get(id).getName());
						} catch (Exception e) {
							dataset.addValue(val, "Significant", lev.get(id).getName());
						}
					}
				}
				if (bMod) {
					for (Long id : ids) {
						Double val = map.get(QRMConstants.ToleranceModerate).get(id);
						if (val == null)val = 0.0;
						try {
							dataset.addValue(val, "Moderate", ppl.get(id).getName());
						} catch (Exception e) {
							dataset.addValue(val, "Moderate", lev.get(id).getName());
						}
					}
				}
				if (bLow) {
					for (Long id : ids) {
						Double val = map.get(QRMConstants.ToleranceLow).get(id);
						if (val == null)val = 0.0;
						try {
							dataset.addValue(val, "Low", ppl.get(id).getName());
						} catch (Exception e) {
							dataset.addValue(val, "Low", lev.get(id).getName());
						}
					}
				}

			} catch (SQLException e) {
				log.error("QRM Stack Trace", e);
			}
		}

		if (param1.equals("status")) {

			urlGen  = new QRMCategoryURLGenerator(QRMConstants.STATUS);
			title = "Allocation by Status - " + this.projectTitle;
			cat = "Risk Status";

			try {

				HashMap<String,HashMap<Integer, Double>> map = new HashMap<String,HashMap<Integer, Double>>();

				map.put("Inactive", new HashMap<Integer,Double>());
				map.put("Active", new HashMap<Integer,Double>());
				map.put("Pending", new HashMap<Integer,Double>());

				String sql =
					" 	SELECT COUNT(*), currentTolerance, \"Pending\" AS status from risk where startExposure > current_date  AND projectID IN ("+ listOfProjects + ") GROUP BY currentTolerance" +
					" 	UNION" +
					" 	SELECT COUNT(*), currentTolerance, \"Inactive\" AS status from risk where endExposure < current_date  AND projectID IN ("+ listOfProjects + ") GROUP BY currentTolerance" +
					" 	UNION " +
					" 	SELECT COUNT(*), currentTolerance, \"Active\" AS status from risk where startExposure <= current_date AND endExposure >= current_date  AND projectID IN ("+ listOfProjects + ") GROUP BY currentTolerance";

				ResultSet rs = conn.createStatement().executeQuery(sql);

				while (rs.next()){
					map.get(rs.getString("status")).put(rs.getInt("currentTolerance"), rs.getDouble("COUNT(*)"));
				}

				if (bEx) {
					dataset.addValue(map.get("Pending").get(QRMConstants.ToleranceExtreme), "Extreme","Pending");
					dataset.addValue(map.get("Active").get(QRMConstants.ToleranceExtreme), "Extreme","Active");
					dataset.addValue(map.get("Inactive").get(QRMConstants.ToleranceExtreme), "Extreme","In Active");
				}
				if (bHigh) {
					dataset.addValue(map.get("Inactive").get(QRMConstants.ToleranceHigh), "High","In Active");
					dataset.addValue(map.get("Pending").get(QRMConstants.ToleranceHigh), "High","Pending");
					dataset.addValue(map.get("Active").get(QRMConstants.ToleranceHigh), "High","Active");
				}
				if (bSig) {
					dataset.addValue(map.get("Inactive").get(QRMConstants.ToleranceSignificant), "Significant","In Active");
					dataset.addValue(map.get("Pending").get(QRMConstants.ToleranceSignificant), "Significant","Pending");
					dataset.addValue(map.get("Active").get(QRMConstants.ToleranceSignificant), "Significant","Active");
				}
				if (bMod) {
					dataset.addValue(map.get("Inactive").get(QRMConstants.ToleranceModerate), "Moderate","In Active");
					dataset.addValue(map.get("Pending").get(QRMConstants.ToleranceModerate), "Moderate","Pending");
					dataset.addValue(map.get("Active").get(QRMConstants.ToleranceModerate), "Moderate","Active");
				}
				if (bLow) {
					dataset.addValue(map.get("Inactive").get(QRMConstants.ToleranceLow), "Low","In Active");
					dataset.addValue(map.get("Pending").get(QRMConstants.ToleranceLow), "Low","Pending");
					dataset.addValue(map.get("Active").get(QRMConstants.ToleranceLow), "Low","Active");
				}
			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			} 
		}

		if (param1.equalsIgnoreCase("tolerance")){

			urlGen  = new QRMCategoryURLGenerator(QRMConstants.TOLERANCE);

			try {

				CallableStatement cs = conn.prepareCall("call getTolAllocationMetric(?,?)");
				cs.setLong(1, projectID);
				try {
					cs.setInt(2, ((Boolean) paramMap.get("bDescend"))?1:0);
				} catch (NullPointerException e) {
					cs.setInt(2, ((Boolean) paramMap.get("descendants"))?1:0);
				}

				ResultSet rs = cs.executeQuery();
				while (rs.next()){
					switch (rs.getInt("currentTolerance")){
					case QRMConstants.ToleranceExtreme:
						pieSet.setValue("Extreme", rs.getDouble("COUNT"));
						break;
					case QRMConstants.ToleranceHigh:
						pieSet.setValue("High", rs.getDouble("COUNT"));
						break;
					case QRMConstants.ToleranceSignificant:
						pieSet.setValue("Significant", rs.getDouble("COUNT"));
						break;
					case QRMConstants.ToleranceModerate:
						pieSet.setValue("Moderate", rs.getDouble("COUNT"));
						break;
					case QRMConstants.ToleranceLow:
						pieSet.setValue("Low", rs.getDouble("COUNT"));
						break;
					}
				}

			} catch (SQLException e) {
				log.error("QRM Stack Trace", e);
			}

			piePlot = new PiePlot(pieSet);

//			piePlot.setBackgroundPaint(new Color(198, 222, 255));
			piePlot.setOutlineVisible(false);
			piePlot.setIgnoreZeroValues(true);
			piePlot.setLabelLinksVisible(false);
			PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator("{2}", new DecimalFormat("0"), new DecimalFormat("0.00%"));
			piePlot.setLabelGenerator(generator);
			piePlot.setSimpleLabels(true);

			piePlot.setSectionPaint("Extreme", Color.red);
			piePlot.setSectionPaint("High", Color.orange);
			piePlot.setSectionPaint("Significant", Color.yellow);
			piePlot.setSectionPaint("Moderate", Color.green);
			piePlot.setSectionPaint("Low", new Color(0,255,255,255));
		}

		if (param1.equalsIgnoreCase("multitolerance")){


			try {


				CallableStatement cs = conn.prepareCall("call getTolAllocationMetricDeep(?)");
				cs.setLong(1, projectID);

				ResultSet rs = cs.executeQuery();
				while (rs.next()){

					idMap.put(rs.getString("projectTitle"), rs.getLong("projectID"));

					switch (rs.getInt("currentTolerance")){
					case QRMConstants.ToleranceExtreme:
						dataset.addValue(rs.getDouble("COUNT"), "Extreme",rs.getString("projectTitle"));
						break;
					case QRMConstants.ToleranceHigh:
						dataset.addValue(rs.getDouble("COUNT"), "High", rs.getString("projectTitle"));
						break;
					case QRMConstants.ToleranceSignificant:
						dataset.addValue(rs.getDouble("COUNT"), "Significant", rs.getString("projectTitle"));
						break;
					case QRMConstants.ToleranceModerate:
						dataset.addValue(rs.getDouble("COUNT"), "Moderate" ,rs.getString("projectTitle"));
						break;
					case QRMConstants.ToleranceLow:
						dataset.addValue(rs.getDouble("COUNT"), "Low", rs.getString("projectTitle") );
						break;
					}
				}

				urlGen  = new QRMCategoryURLGenerator(QRMConstants.MULITOLERANCE, idMap, dataset);


			} catch (Exception e) {
				log.error("QRM Stack Trace", e);
			}

		}

		try {
			conn.close();
		} catch (SQLException e) {}

		try {

			if (this.var_descendants) {
				title = title + " (including sub projects)";
			}

			JFreeChart chart = null;
			if(param1.equalsIgnoreCase("tolerance")){

				chart = new JFreeChart("Tolerance Allocation",JFreeChart.DEFAULT_TITLE_FONT, piePlot, false);
				final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
				if (resp != null){
					resp.addHeader("Cache-Control", "no-cache");
					resp.addHeader("pragma", "No-Cache");
				}
				piePlot.setURLGenerator(urlGen);
				chart.setBackgroundPaint(Color.white);
				piePlot.setBackgroundPaint(Color.white);


				if (parkImage){
					getMapParkChart(chart, paramMap, info);
				} else {
					outputChart(chart, paramMap, out, resp, info);
				}
			} else if (param1.equalsIgnoreCase("multitolerance")){

//				Color ctrlColor = new Color(198, 222, 255);

				chart = ChartFactory.createMultiplePieChart("Sub Project Tolerance Allocation", dataset, TableOrder.BY_COLUMN, false, false, false); 
				chart.getTitle().setFont(new Font("Verdana", Font.BOLD, 11));

				final MultiplePiePlot plot = (MultiplePiePlot) chart.getPlot();
				final JFreeChart subchart = plot.getPieChart();
				subchart.getTitle().setFont(new Font("Verdana", Font.BOLD, 11));

				final PiePlot piePlot = (PiePlot) subchart.getPlot();
				piePlot.setURLGenerator(urlGen);

//				piePlot.setBackgroundPaint(ctrlColor);
				piePlot.setOutlineVisible(false);
				piePlot.setLabelLinksVisible(false);
				piePlot.setLabelGenerator(null);

				piePlot.setIgnoreZeroValues(true);

				piePlot.setSectionPaint("Extreme", Color.red);
				piePlot.setSectionPaint("High", Color.orange);
				piePlot.setSectionPaint("Significant", Color.yellow);
				piePlot.setSectionPaint("Moderate", Color.green);
				piePlot.setSectionPaint("Low", new Color(0,255,255,255));

				if(paramMap.containsKey("dashboard")){
//					chart.setBackgroundPaint(ctrlColor);
//					plot.setBackgroundPaint(ctrlColor);
//					subchart.setBackgroundPaint(ctrlColor);
//					piePlot.setBackgroundPaint(ctrlColor);
				} else {
					chart.setBackgroundPaint(Color.white);
					plot.setBackgroundPaint(Color.white);
					subchart.setBackgroundPaint(Color.white);
					piePlot.setBackgroundPaint(Color.white);
				}

				final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());

				if (parkImage){
					getMapParkChart(chart, paramMap, info);
				} else {
					outputChart(chart, paramMap, out, resp, info);
				}

			} else {
				if (dataset instanceof QRMDataSet){
					if (!var_3D) {
						chart = ChartFactory.createStackedBarChart(title, cat, value, ((QRMDataSet) dataset).getDataSet(), PlotOrientation.HORIZONTAL, false, true, true);
					} else {
						chart = ChartFactory.createStackedBarChart3D(title, cat, value, ((QRMDataSet) dataset).getDataSet(), PlotOrientation.HORIZONTAL, false, true, true);
					}
				} else {
					if (!var_3D) {
						chart = ChartFactory.createStackedBarChart(title, cat, value, dataset, PlotOrientation.HORIZONTAL, false, true, true);
					} else {
						chart = ChartFactory.createStackedBarChart3D(title, cat, value, dataset, PlotOrientation.HORIZONTAL, false, true, true);
					}
				}

				CategoryPlot plot = (CategoryPlot) chart.getPlot();
				plot.setBackgroundPaint(Color.white);
				chart.getTitle().setFont(new Font("Arial", Font.BOLD, 12));
				BarRenderer renderer = (BarRenderer) plot.getRenderer();
				renderer.setBarPainter(new StandardBarPainter());

				renderer.setSeriesPaint(0, Color.red);
				renderer.setSeriesPaint(1, Color.orange);
				renderer.setSeriesPaint(2, Color.yellow);
				renderer.setSeriesPaint(3, Color.green);
				renderer.setSeriesPaint(4, new Color(0,255,255,255));

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
			}

		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		} 
	}

	@SuppressWarnings("serial")
	class QRMDataSet extends DefaultCategoryDataset {

		HashMap<String, DataSetElement> map = new HashMap<String, DataSetElement>(); 

		protected void addValue(Double val, String rowKey, String colKey){

			if (val == null) return;

			DataSetElement elem = map.get(colKey);

			if (elem == null){
				elem  = new  DataSetElement(rowKey, colKey );
				map.put(colKey, elem);
			}

			Double rowVal = elem.rowElement.get(rowKey);
			if (rowVal == null){
				elem.rowElement.put(rowKey, 0.0);
				rowVal = 0.0;
			}

			elem.rowElement.put(rowKey,  rowVal+val);
		}


		public ArrayList<DataSetElement> getSorted(){
			ArrayList<DataSetElement> list = new ArrayList<DataSetElement>(map.values());
			Collections.sort(list);
			return list;
		}

		public DefaultCategoryDataset getDataSet(){

			DefaultCategoryDataset dataset = new DefaultCategoryDataset();

			for (DataSetElement e:getSorted()){
				for (String s: new String[]{"Extreme", "High", "Significant","Moderate","Low"}){
					if (e.rowElement.get(s) != null){
						dataset.addValue(e.rowElement.get(s), s, e.colKey);
					} else {
						dataset.addValue(0.0, s, e.colKey);
					}
				}
			}
			return dataset;
		}
	}

	class DataSetElement implements Comparable<DataSetElement>{

		HashMap<String, Double> rowElement;
		String colKey;

		public DataSetElement(String rowKey, String colKey){
			this.colKey = colKey;
			rowElement = new HashMap<String, Double> ();
			rowElement.put(rowKey, 0.0);
		}

		@Override
		public int compareTo(DataSetElement arg0) {
			return -1*count().compareTo(arg0.count());
		}

		public Double count(){
			Double count = 0.0;
			for (Double d:rowElement.values()){
				count = count+d;
			}
			return count;
		}
	}
}
