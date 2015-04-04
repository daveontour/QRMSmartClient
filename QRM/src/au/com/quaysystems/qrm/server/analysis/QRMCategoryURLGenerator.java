package au.com.quaysystems.qrm.server.analysis;

import java.util.HashMap;

import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.chart.urls.PieURLGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.PieDataset;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.server.analysis.AllocationRiskAnalysis.QRMDataSet;

public class QRMCategoryURLGenerator implements CategoryURLGenerator, PieURLGenerator {

	private HashMap<String, Long > idMap = new HashMap<String, Long >();
	private int type;
	private QRMDataSet multiDataSet;

	public QRMCategoryURLGenerator(Integer type) {
		super();
		this.type = type;
	}

	public QRMCategoryURLGenerator(Integer type,HashMap<String, Long > idMap ) {
		super();
		this.type = type;
		this.idMap = idMap;
	}

	public QRMCategoryURLGenerator(Integer type, HashMap<String, Long> idMap, QRMDataSet dataset) {
		super();
		this.type = type;
		this.idMap = idMap;
		this.multiDataSet = dataset;
	}

    @SuppressWarnings({"rawtypes" })
	public String generateURL(CategoryDataset dataset, int series, int category) {

		Comparable seriesKey = dataset.getRowKey(series);
		Comparable categoryKey = dataset.getColumnKey(category);

		switch (type){
		case QRMConstants.OWNERS:
			return "javascript:ownerFind("+idMap.get(categoryKey.toString())+",  '"+seriesKey.toString()+"')";
		case QRMConstants.MANAGERS:
			return "javascript:managerFind("+idMap.get(categoryKey.toString())+",  '"+seriesKey.toString()+"')";
		case QRMConstants.STATUS:
			return "javascript:statusFind('"+categoryKey.toString()+"',  '"+seriesKey.toString()+"')";
		case QRMConstants.RISK:
			return "javascript:getRiskCodeAndDisplay('"+categoryKey.toString()+"')";
		case QRMConstants.CATEGORIES:
			return "javascript:categoryFind("+idMap.get(categoryKey.toString())+",  '"+seriesKey.toString()+"')";
		}
		return "";
	}
	
    @SuppressWarnings({"rawtypes" })
	public String generateURL(PieDataset dataset, Comparable key,  int pieIndex) {
		if (type == QRMConstants.TOLERANCE) return "javascript:toleranceFind('"+key+"')";
		if (type == QRMConstants.MULITOLERANCE) return "javascript:projectToleranceFind('"+key+"',  "+idMap.get(this.multiDataSet.getColumnKey(pieIndex))+")";
		return null;
    }
}
