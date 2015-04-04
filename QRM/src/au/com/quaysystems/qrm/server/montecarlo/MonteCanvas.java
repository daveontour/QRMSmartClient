package au.com.quaysystems.qrm.server.montecarlo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

public class MonteCanvas {

	private static final int numHorizSpaccing = 10;
	private static final int numVertSpaccing = 20;
	private static final int NUMFREQS = 30;
	private static int WIDTH;
	private static int HEIGHT;
	private static int oX;
	private static int oY;
	private static double xLen;
	private static double yLen;
	private static double vSpace;
	private static double hSpace;
	private static List<Double> points;
	private static List<Double> points2;
	private static double maxValue = 0;
	private static double minValue = 0;
	private static double xInterval;
	private static int[] freq;
	private static Double[] freqValue;
	private static double freqMaxCount;
	private static double maxFreqScale;
	private static int barWidth = 5;
	private static Font axisFont = new Font("Arial", Font.PLAIN, 10);
	private static DecimalFormat xAxisLabelFormat;
	private static int percentileBlockSizeW;
	private static String xAxisTitle = "Horizontal Title";
	private static String Title = "Title";
	private static boolean bPre = false;
	private static Color RED = Color.RED;
	private static Color BLUE = Color.BLUE;
	private static Color BLACK = Color.BLACK;
	private static Color GRAY = Color.GRAY;
	private static Graphics2D g2d;
	private static Logger log = Logger.getLogger("au.com.quaysystems.qrm");


	public final void paint(final int width, final int height) {

		WIDTH = width;
		HEIGHT = height;
		oX = 50;
		oY = HEIGHT - 60;
		yLen = (HEIGHT - 120);
		vSpace = (yLen / numVertSpaccing);

		try {
			primeData();
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

		xLen = (WIDTH - percentileBlockSizeW - oX);
		hSpace = (xLen / numHorizSpaccing);

		try {
			setBars();
		} catch (Exception e) {
			log.error("QRM Stack Trace", e);
		}

		try {
			drawGrid();
		} catch (RuntimeException e) {
			log.error("QRM Stack Trace", e);
		}
		try {
			drawAxisTitle();
		} catch (RuntimeException e) {
			log.error("QRM Stack Trace", e);
		}
		try {
			if (points2 == null) {
				plotBars();
			}
		} catch (RuntimeException e) {
			log.error("QRM Stack Trace", e);
		}
		try {
			plotPoints(points);
			if (points2 != null) {
				boolean preserve = bPre;
				bPre = false;
				plotPoints(points2);
				bPre = preserve;
			}
		} catch (RuntimeException e) {
			log.error("QRM Stack Trace", e);
		}
		try {
			drawAxis();
		} catch (RuntimeException e) {
			log.error("QRM Stack Trace", e);
		}
		try {
			drawPercentileBlock();
		} catch (RuntimeException e) {
			log.error("QRM Stack Trace", e);
		}
		try {
			drawXAxisLabels();
		} catch (RuntimeException e) {
			log.error("QRM Stack Trace", e);
		}
	}

	public final void setInput(final ArrayList<Double> pts, final String title,
			final String xAxis, final boolean bPre1) {
		points = pts;
		xAxisTitle = xAxis;
		Title = title;
		bPre = bPre1;
		points2 = null;
	}

	public final void setInput(final ArrayList<Double> pts,
			final ArrayList<Double> pts2, final String title, final String xAxis) {
		points = pts;
		points2 = pts2;
		xAxisTitle = xAxis;
		Title = title;
		bPre = true;
	}

	public final RenderedImage getImage(final int sizex, final int sizey) {
		BufferedImage image = new BufferedImage(sizex, sizey,
				BufferedImage.TYPE_4BYTE_ABGR);
		g2d = image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		paint(sizex, sizey);
		return image;
	}

	public final byte[] getOutputStream(final int sizex, final int sizey) {
		BufferedImage image = new BufferedImage(sizex, sizey,
				BufferedImage.TYPE_4BYTE_ABGR);
		g2d = image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		paint(sizex, sizey);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "png", out);
		} catch (IOException e) {
			log.error("QRM Stack Trace", e);
		}
		return out.toByteArray();
	}

	/**
	 * Draw percentile block.
	 */
	private void drawPercentileBlock() {

		if (points == null || points.size() == 0) {
			return;
		}

		g2d.setFont(new Font("Arial", Font.PLAIN, 11));

		TextLayout layout = new TextLayout("Percentile", g2d.getFont(), g2d
				.getFontRenderContext());
		Point dim = new Point(layout);

		int width = dim.x;
		int height = dim.y;

		int offset = (int) (oX + xLen + 50);
		layout.draw(g2d, offset, (int) (oY - yLen + 30));

		layout = new TextLayout("Value", g2d.getFont(), g2d
				.getFontRenderContext());
		dim = new Point(layout);
		layout.draw(g2d, offset + width + 25 - dim.x / 2,
				(int) (oY - yLen + 30));

		if (points2 != null) {
			layout = new TextLayout("Saving", g2d.getFont(), g2d
					.getFontRenderContext());
			Point dim2 = new Point(layout);
			int w = dim2.x;
			int d = WIDTH - offset - 50;
			layout.draw(g2d, (offset + d / 2 - w / 2),
					(int) (oY - yLen + 30 - height));
		}

		for (int i = 10; i <= 100; i += 10) {
			Integer percantile;
			Double value;

			if (points2 == null) {
				if (i == 100) {
					percantile = 99;
					value = points.get((points.size() - 1));
				} else {
					percantile = i;
					value = points.get((points.size() * i / 100));
				}
			} else {

				if (i == 100) {
					percantile = 99;
					Double value1 = points.get((points.size() - 1));
					Double value2 = points2.get((points2.size() - 1));
					value = value1 - value2;
				} else {
					percantile = i;
					Double value1 = points.get((points.size() * i / 100));
					Double value2 = points2.get((points2.size() * i / 100));
					value = value1 - value2;
				}
			}

			g2d.setFont(axisFont);

			String pStr = percantile.toString();
			layout = new TextLayout(pStr, g2d.getFont(), g2d
					.getFontRenderContext());
			dim = new Point(layout);

			int pWidth = dim.x;
			int ypos = (int) (oY - yLen + (i / 10 * height * 1.2) + 35);

			layout.draw(g2d, offset + width / 2 - pWidth / 2, ypos);

			layout = new TextLayout(xAxisLabelFormat.format(value), g2d
					.getFont(), g2d.getFontRenderContext());
			dim = new Point(layout);
			layout.draw(g2d, offset + width + 25 - dim.x / 2, ypos);
		}
	}

	/**
	 * Prime data.
	 */
	private void primeData() {

		if (points != null) {
			try {
				Collections.sort(points);

				if (points.size() > 0) {
					maxValue = points.get(points.size() - 1);
					minValue = points.get(0);
				} else {
					maxValue = 0;
					minValue = 0;
				}
			} catch (RuntimeException e) {
				log.error("QRM Stack Trace", e);
			}

			xInterval = maxValue - minValue;
			if (maxValue > 1 || (maxValue == 0 && minValue == 0)) {
				xAxisLabelFormat = new DecimalFormat("0.0");
			} else {
				xAxisLabelFormat = new DecimalFormat("0.###E0");
			}

		}

		if (points2 != null) {
			try {
				Collections.sort(points2);

				if (points2.size() > 0) {
					maxValue = Math.max(maxValue, points2
							.get(points2.size() - 1));
					minValue = Math.min(minValue, points2.get(0));
				}
			} catch (RuntimeException e) {
				log.error("QRM Stack Trace", e);
			}

			xInterval = maxValue - minValue;
			if (maxValue > 1 || (maxValue == 0 && minValue == 0)) {
				xAxisLabelFormat = new DecimalFormat("0.0");
			} else {
				xAxisLabelFormat = new DecimalFormat("0.###E0");
			}

		}

		int fnSize = 10;
		int fnH;
		do {
			axisFont = new Font("Arial", Font.BOLD, fnSize--);
			g2d.setFont(axisFont);
			fnSize = fnSize - 1;
			FontRenderContext frc = g2d.getFontRenderContext();
			TextLayout layout = new TextLayout("100", axisFont, frc);
			fnH = (int) (layout.getBounds().getHeight());
		} while (fnH > vSpace * 0.9);

		int titleW = new Point(new TextLayout(" Percentiles    Value ",
				axisFont, g2d.getFontRenderContext())).x;
		int dataW = new Point(new TextLayout(" "
				+ new DecimalFormat("0").format(freqMaxCount * 1.2) + "  "
				+ "99.9", axisFont, g2d.getFontRenderContext())).x;
		percentileBlockSizeW = Math.max(titleW, dataW) + 70;
	}

	/**
	 * Draw x axis labels.
	 */
	private void drawXAxisLabels() {

		ArrayList<String> labels = new ArrayList<String>();

		for (int i = 0; i <= numHorizSpaccing; i++) {
			labels.add(xAxisLabelFormat.format(minValue
					+ ((double) i / (double) numHorizSpaccing) * xInterval));
		}

		g2d.setFont(getNonOverlappingFontSize());
		for (int i = 0; i <= numHorizSpaccing; i++) {
			String title = labels.get(i);
			TextLayout layout = new TextLayout(title, g2d.getFont(), g2d
					.getFontRenderContext());

			Point dim = new Point(layout);
			layout.draw(g2d, (int) (oX + i * hSpace - dim.x / 2), oY + dim.y
					+ 15);

			g2d.drawLine((int) (oX + i * hSpace), oY + 6, (int) (oX + i
					* hSpace), oY + 10);

		}
	}

	/**
	 * Gets the non overlapping font size.
	 * 
	 * @return the non overlapping font size
	 */
	private Font getNonOverlappingFontSize() {

		Font xAxisLabelFont = axisFont;

		int fnSize = xAxisLabelFont.getSize();
		String maxString = xAxisLabelFormat.format(points
				.get(points.size() - 1));
		double hSpace = (xLen / numHorizSpaccing);
		g2d.setFont(xAxisLabelFont);
		while (new Point(new TextLayout(maxString, g2d.getFont(), g2d
				.getFontRenderContext())).x > (hSpace - 5)) {
			xAxisLabelFont = new Font("Arial", Font.PLAIN, fnSize);
			g2d.setFont(xAxisLabelFont);
			fnSize = fnSize - 1;
		}

		return xAxisLabelFont;
	}

	/**
	 * Draw axis.
	 */
	private static void drawAxis() {

		g2d.setColor(BLACK);
		// left vertical
		g2d.drawLine(oX - 8, oY, oX - 8, (int) (oY - yLen - 5));

		// right vertical
		if (minValue != maxValue && points2 == null) {
			g2d.drawLine((int) (oX + xLen + 8), oY, (int) (oX + xLen + 8),
					(int) (oY - yLen - 5));
			g2d.drawLine((int) (oX + xLen + 4), oY, (int) (oX + xLen + 12), oY);
		}

		// horizontal
		g2d.drawLine(oX, oY + 8, (int) (oX + xLen), oY + 8);
		g2d.drawLine(oX - 12, oY, oX - 4, oY);
		g2d.drawLine(oX, oY + 4, oX, oY + 12);
		g2d.drawLine((int) (oX + xLen), oY + 4, (int) (oX + xLen), oY + 12);

	}

	/**
	 * Draw axis title.
	 */
	private void drawAxisTitle() {

		g2d.setColor(BLACK);
		g2d.setFont(new Font("Arial", Font.BOLD, 14));

		TextLayout layout = new TextLayout(Title, g2d.getFont(), g2d
				.getFontRenderContext());
		Point dim = new Point(layout);
		layout.draw(g2d, Math.max(0, (int) (oX + xLen / 2 - dim.x / 2)),
				dim.y + 10);

		g2d.setFont(new Font("Arial", Font.BOLD, 12));
		layout = new TextLayout(xAxisTitle, g2d.getFont(), g2d
				.getFontRenderContext());
		dim = new Point(layout);
		layout.draw(g2d, Math.max(0, (int) (oX + xLen / 2 - dim.x / 2)), oY
				+ 30 + dim.y);

		layout = new TextLayout("Percentile", g2d.getFont(), g2d
				.getFontRenderContext());
		dim = new Point(layout);
		layout.draw(g2d, Math.max(0, (oX - 8 - dim.x / 2)), (int) (oY - yLen
				- dim.y - 11));

		if (minValue != maxValue && points2 == null) {
			layout = new TextLayout("Frequency", g2d.getFont(), g2d
					.getFontRenderContext());
			dim = new Point(layout);
			layout.draw(g2d, Math.max(0, (int) (oX + xLen - dim.x / 2)),
					(int) (oY - yLen - dim.y - 11));
		}
	}

	/**
	 * Draw grid.
	 */
	private void drawGrid() {

		maxFreqScale = freqMaxCount * 1.2;
		double freqScaleInterval = Math.floor(maxFreqScale / numVertSpaccing);

		g2d.setColor(BLACK);
		g2d.setFont(axisFont);

		for (int i = 1; i <= numVertSpaccing; i++) {
			String title = new Integer((i * (100 / numVertSpaccing)))
					.toString();
			TextLayout layout = new TextLayout(title, g2d.getFont(), g2d
					.getFontRenderContext());
			Point dim = new Point(layout);
			layout.draw(g2d, Math.max(0, (oX - 13 - dim.x)), (int) (oY + dim.y
					/ 2 - i * vSpace));

			if (minValue != maxValue && points2 == null) {
				layout = new TextLayout(new DecimalFormat("0").format(i
						* freqScaleInterval), g2d.getFont(), g2d
						.getFontRenderContext());
				layout.draw(g2d, (int) (oX + xLen + 12),
						(int) (oY + dim.y / 2 - i * vSpace));
			}
		}

		g2d.setColor(GRAY);

		float[] dash2 = { 1f, 1f, 1f };
		BasicStroke bs2 = new BasicStroke(1, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 1.0f, dash2, 2f);
		g2d.setStroke(bs2);

		for (int i = 1; i <= numVertSpaccing; i++) {
			g2d.drawLine(oX, (int) (oY - i * vSpace), (int) (oX + xLen),
					(int) (oY - i * vSpace));
		}

		for (int i = 1; i < numHorizSpaccing; i++) {
			g2d.drawLine((int) (oX + i * hSpace), oY, (int) (oX + i * hSpace),
					(int) (oY - yLen));
		}
	}

	private void plotPoints(final List<Double> pts) {

		if (pts == null || pts.size() == 0) {
			return;
		}

		Point prevPoint = new Point(oX, oY);

		g2d.setColor((bPre ? RED : BLUE));
		g2d.setStroke(new BasicStroke());

		for (int i = 0; i < points.size(); i++) {
			Point newPoint = calcNewPoint(i, pts.get(i));
			g2d.drawLine(prevPoint.x, prevPoint.y, newPoint.x, newPoint.y);
			prevPoint = new Point(newPoint.x, newPoint.y);
		}
	}

	/**
	 * Plot bars.
	 */
	private void plotBars() {

		g2d.setColor(GRAY);

		for (int i = 0; i < NUMFREQS; i++) {
			int count = freq[i];
			Double val = freqValue[i];

			Point size = calcBarPoint(count, val);

			g2d.setBackground(GRAY);
			g2d.fillRect(oX + size.x - barWidth / 2, oY - size.y, barWidth,	size.y);

		}
	}

	private Point calcBarPoint(final int i, final Double val) {

		if (points == null || points.size() == 0 || val == null) {
			return new Point(0, 0);
		}

		double percentXMax = (val - minValue) / xInterval;
		double percentYMax = i / maxFreqScale;

		return new Point((int) (percentXMax * xLen), (int) (percentYMax * yLen));
	}

	private Point calcNewPoint(final int i, final Double val) {
		double xVal = (val - minValue) / xInterval * xLen;
		double yVal = (double) (i) / (double) (points.size() - 1) * yLen;

		// Takes care of simple value
		if (minValue == maxValue && val == minValue) {
			xVal = (double) (i) / (double) (points.size() - 1) * xLen;
			yVal = (double) (i) / (double) (points.size() - 1) * yLen;
		}

		return new Point((int) (oX + xVal), (int) (oY - yVal));
	}

	/**
	 * Sets the bars.
	 */
	private static void setBars() {

		freq = new int[NUMFREQS];
		freqValue = new Double[NUMFREQS];
		freqMaxCount = 0;

		double barInterval = (maxValue - minValue) / NUMFREQS;
		if (barInterval == 0) {
			return;
		}

		if (points.size() <= NUMFREQS) {

			freq = new int[1];
			freqValue = new Double[1];

			freq[0] = 0;
			freqValue[0] = 0.0;

			return;
		}

		int count = 0;
		int interval = 0;
		double intervalmax = minValue + (interval + 1) * barInterval;

		try {
			for (Double value : points) {

				if (value < intervalmax) {
					count++;
				}

				if (value >= intervalmax) {
					freqValue[interval] = intervalmax - barInterval / 2;
					freq[interval] = count;
					if (count > freqMaxCount) {
						freqMaxCount = count;
					}
					count = 0;
					interval++;
					intervalmax = minValue + (interval + 1) * barInterval;
				}
			}
		} catch (RuntimeException e) {
			log.error("QRM Stack Trace", e);
		}
	}

	/**
	 * The Class Point.
	 */
	private class Point {

		public int x;
		public int y;

		public Point(final TextLayout layout) {
			this.x = new Double(layout.getBounds().getWidth()).intValue();
			this.y = new Double(layout.getBounds().getHeight()).intValue();
		}

		public Point(final int x, final int y) {
			this.x = x;
			this.y = y;
		}
	}
}
