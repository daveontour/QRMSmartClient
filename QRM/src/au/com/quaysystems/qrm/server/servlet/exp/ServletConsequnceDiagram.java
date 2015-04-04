package au.com.quaysystems.qrm.server.servlet.exp;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import au.com.quaysystems.qrm.QRMConstants;
import au.com.quaysystems.qrm.util.probability.IRandNumGenDist;
import au.com.quaysystems.qrm.util.probability.NormalDistribution;
import au.com.quaysystems.qrm.util.probability.SimpleDistribution;
import au.com.quaysystems.qrm.util.probability.TriGenDistribution;
import au.com.quaysystems.qrm.util.probability.TriangularDistribution;
import au.com.quaysystems.qrm.util.probability.TruncNormalDistribution;
import au.com.quaysystems.qrm.util.probability.UniformDistribution;


@WebServlet (value = "/getConsequenceImage/*", asyncSupported = false)
public class ServletConsequnceDiagram extends HttpServlet {

	/** The parser. */
	protected JSONParser parser = new JSONParser();
	private static Logger log = Logger.getLogger("au.com.quaysystems.qrm");


	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1387308835754413189L;

	@Override
	public final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	public final void init() {
		log.info("Consequence Diagram Processor Started");
	}

	@SuppressWarnings({ "unchecked"})
	@Override
	protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
			IOException {

		JSONObject formValues = null;
		IRandNumGenDist dist = null;

		try {
			formValues = (JSONObject) parser.parse(request.getParameter("VALUES"));
		} catch (ParseException e1) {
			return;
		}
	
		try {
	      Class<IRandNumGenDist> c = (Class<IRandNumGenDist>) Class.forName((String)formValues.get("distType"));
	      dist = c.newInstance(); // InstantiationException
	    } catch (Exception x) {
	      return;
	    } 
	    
    	ArrayList<Double> params = new ArrayList<Double>();

    	if (dist instanceof TruncNormalDistribution){
    		if (formValues.get("mean") == null || formValues.get("stdDev") == null || formValues.get("lower") == null || formValues.get("upper") == null) return;
	    	params.add(QRMConstants.getDoubleJS(formValues.get("mean")));
	    	params.add(QRMConstants.getDoubleJS(formValues.get("stdDev")));
	    	params.add(QRMConstants.getDoubleJS(formValues.get("lower")));
	    	params.add(QRMConstants.getDoubleJS(formValues.get("upper")));
	    	if (params.get(3) <= params.get(2)) return;
	    	dist.setParams(params);
	    } else if (dist instanceof NormalDistribution){
    		if (formValues.get("mean") == null || formValues.get("stdDev") == null ) return;
	    	params.add(QRMConstants.getDoubleJS(formValues.get("mean")));
	    	params.add(QRMConstants.getDoubleJS(formValues.get("stdDev")));
	    	dist.setParams(params);
	    }
	    	

    	if (dist instanceof TriGenDistribution){
    		if (formValues.get("min") == null || formValues.get("most") == null || formValues.get("max") == null || formValues.get("lower") == null || formValues.get("upper") == null) return;
	    	params.add(QRMConstants.getDoubleJS(formValues.get("min")));
	    	params.add(QRMConstants.getDoubleJS(formValues.get("most")));
	    	params.add(QRMConstants.getDoubleJS(formValues.get("max")));
	    	params.add(QRMConstants.getDoubleJS(formValues.get("lower")));
	    	params.add(QRMConstants.getDoubleJS(formValues.get("upper")));
	    	if (params.get(1) <= params.get(0)) return;
	    	if (params.get(2) <= params.get(1)) return;
	    	if (params.get(4) <= params.get(3)) return;
	    	dist.setParams(params);
	    } else if (dist instanceof TriangularDistribution){
    		if (formValues.get("min") == null || formValues.get("most") == null || formValues.get("max") == null ) return;
	    	params.add(QRMConstants.getDoubleJS(formValues.get("min")));
	    	params.add(QRMConstants.getDoubleJS(formValues.get("most")));
	    	params.add(QRMConstants.getDoubleJS(formValues.get("max")));
	    	if (params.get(1) <= params.get(0)) return;
	    	if (params.get(2) <= params.get(1)) return;
	    	dist.setParams(params);
	    }
    	
    	if (dist instanceof UniformDistribution){
    		if ( formValues.get("lower") == null || formValues.get("upper") == null) return;
	    	params.add(QRMConstants.getDoubleJS(formValues.get("lower")));
	    	params.add(QRMConstants.getDoubleJS(formValues.get("upper")));
	    	dist.setParams(params);
	    }
    	if (dist instanceof SimpleDistribution){
    		if ( formValues.get("simple") == null) return;
	    	params.add(QRMConstants.getDoubleJS(formValues.get("simple")));
	    	dist.setParams(params);
	    }    	
    	Object[] displayValues = dist.getDisplayData();    	
    	String[] labels = (String[]) displayValues[0];
    	Double[] vals = (Double[]) displayValues[1];

 		response.setContentType("image/png");
		response.addHeader("pragma", "No-Cache");

		BufferedImage image = new BufferedImage(
				Integer.parseInt(request.getParameter("WIDTH")), 
				Integer.parseInt(request.getParameter("HEIGHT")), 
				BufferedImage.TYPE_4BYTE_ABGR);
		
		Graphics2D g2d = image.createGraphics();

		getDiagram(
				Integer.parseInt(request.getParameter("WIDTH")), 
				Integer.parseInt(request.getParameter("HEIGHT")), 
				Boolean.parseBoolean(request.getParameter("PREMIT")), vals, labels, g2d);
		
		try {
			ImageIO.write(image, "png", response.getOutputStream());
		} catch (Exception e) {
			 log.error("QRM Stack Trace", e);
		}
		response.getOutputStream().close();
	}

	public final synchronized void getDiagram(final int width,
			final int height, final boolean preMit, final Double[] values,
			final String[] labels, final Graphics2D g2d) {

		try {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

			int oX = 25;
			int oY = height - 40;
			int xLen = (width - oX - 25);
			int numBars = values.length;
			Double maxValue = 0.0;
			int maxBarHeight = height - 75;
			for (Double val : values) {
				if (val > maxValue) {
					maxValue = val;
				}
			}
			double scaleFactor = maxBarHeight / maxValue;
			Double barInterval = (double) xLen / (double) numBars;
			Double barWidth = barInterval * 0.8;

			g2d.setColor(Color.BLACK);

			g2d.drawLine(oX, oY, (oX + xLen), oY);
			g2d.drawLine(oX, oY - 4, oX, oY + 4);
			g2d.drawLine((oX + xLen), oY - 4, (oX + xLen), oY + 4);

			// Draw Bars
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
			g2d.setColor(preMit ? Color.RED : Color.BLUE);
			for (int i = 0; i < numBars; i++) {
				Double barHeight = values[i] * scaleFactor;
				Double barOrigin = oX + (i + 0.5) * barInterval - (0.5 * barWidth);
				g2d.fillRect(barOrigin.intValue(), oY - barHeight.intValue(),barWidth.intValue(), barHeight.intValue());
			}

			// Draw Labels
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
			g2d.setColor(Color.BLACK);
			for (int i = 0; i < numBars; i++) {

				String label = labels[i];

				if (label.length() > 0) {
					g2d.setFont(new Font("Arial", Font.BOLD, 9));
					AffineTransform oldTransform = g2d.getTransform();
					AffineTransform ct = AffineTransform.getTranslateInstance(new Float(oX + (i + 0.5) * barInterval), (oY + 12));
					g2d.transform(ct);
					g2d.transform(AffineTransform.getRotateInstance(Math.PI / 4));
					g2d.drawString(label, 0, 0);
					g2d.setTransform(oldTransform);
				}
			}

			// Draw YAxis title
			g2d.setFont(new Font("Arial", Font.BOLD, 12));
			g2d.setColor(Color.BLACK);
			AffineTransform oldTransform = g2d.getTransform();
			AffineTransform ct = AffineTransform.getTranslateInstance(20,height * 2 / 4);
			g2d.transform(ct);
			g2d.transform(AffineTransform.getRotateInstance(3 * Math.PI / 2));
			g2d.drawString("Proportion", 0, 0);
			g2d.setTransform(oldTransform);
		} catch (NullPointerException e) {
			log.error("QRM Stack Trace", e);	
		}
	}
}
