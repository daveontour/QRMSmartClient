package au.com.quaysystems.qrm.server.servlet.exp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;

import au.com.quaysystems.qrm.dto.ModelToleranceMatrix;
import au.com.quaysystems.qrm.server.MatrixPainter;

@SuppressWarnings("serial")
@WebServlet (value = "/images/reportPreview", asyncSupported = false)
public class ServletReportPreview extends QRMRPCServlet {

	@Override
	void execute(HttpServletRequest request, HttpServletResponse response,
			Session sess, Long userID, HashMap<String, String> stringMap,
			HashMap<Object, Object> objMap, Long projectID, Long riskID) {
		
		
		response.setContentType("image/jpeg");
		
		long repID = 0;
		
		try {
			
			repID = Long.parseLong(stringMap.get("repID"));
			
			if (repID == 0){
				reportNotFound(request, response, stringMap,"Select Report To Execute");
				return;
			} else {
				String filename = getServletContext().getRealPath("/reports/preview/preview"+repID+".jpg");
				File file = new File(filename);
				
				if (!file.exists()){
					reportNotFound(request, response, stringMap,"Report Preview Not Available");
					return;
				}
				try (OutputStream out = response.getOutputStream()){
					ImageIO.write(ImageIO.read(file), "jpg", out);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
			reportNotFound(request, response, stringMap, "Report Preview Not Available");
			return;
		}
	}
	
	private void reportNotFound(HttpServletRequest request, HttpServletResponse response, HashMap<String, String> stringMap,String msg){
		
		int sizex;
		int sizey;
		try {
			sizex = Integer.parseInt(stringMap.get("w"));
			sizey = Integer.parseInt(stringMap.get("h"));
		} catch (NumberFormatException e1) {
			sizex = 600;
			sizey = 700;
		}
		
		BufferedImage image = new BufferedImage(sizex, sizey,BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Font fn = new Font("Arial", Font.BOLD, 14);
		g2d.setFont(fn);
		FontRenderContext frc = g2d.getFontRenderContext();
		TextLayout layout = new TextLayout(msg, fn, frc);
		
		int fnW = (int) (layout.getBounds().getWidth());
		
		g2d.setPaint(Color.BLUE);
		layout.draw(g2d, new Float((sizex / 2) - (fnW / 2)), new Float(40));

		try {
			ImageIO.write(image, "png", response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}


	}
}
