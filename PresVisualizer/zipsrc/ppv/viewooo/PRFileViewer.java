/*
 * PRFileViewer.java
 * Created on Jul 6, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.viewooo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import pres.loader.logmodel.PLLog;
import pres.loader.model.IPLFileStamp;
import pres.loader.model.PLFile;

/**
 * @author macchan
 * 
 */
@SuppressWarnings("serial")
public class PRFileViewer extends JPanel {

	private PLFile file;

	public PRFileViewer(PLFile file) {
		this.file = file;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		int w = getWidth();
		int h = getHeight();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, w, h);

		List<IPLFileStamp> stamps = file.getStamps();
		long start = stamps.get(0).getTime().getAsLong();
		long end = stamps.get(stamps.size() - 1).getTime().getAsLong();
		long width = end - start;
		double xscale = w / (double) width;
		double yscale = h
				/ (double) stamps.get(stamps.size() - 1).getLineCount();

		for (PLLog log : file.getLogs()) {
			// System.out.println(log.getType());
			// System.out.println(log.getSubType());
			if (log.getType().equals("COMMAND_RECORD")
					&& log.getSubType().equals("COMPILE")) {
				g2d.setColor(Color.RED);
				long time = log.getTimestamp();
				int x = (int) ((time - start) * xscale);
				g2d.drawLine(x, 0, x, h);
			} else if (log.getType().equals("COMMAND_RECORD")
					&& log.getSubType().equals("START_RUN")) {
				g2d.setColor(Color.BLUE);
				long time = log.getTimestamp();
				int x = (int) ((time - start) * xscale);
				g2d.drawLine(x, 0, x, h);
			} else if (log.getType().equals("TEXTEDIT_RECORD_ECLIPSE")) {
				g2d.setColor(Color.CYAN.brighter().brighter().brighter());
				long time = log.getTimestamp();
				int x = (int) ((time - start) * xscale);
				g2d.drawLine(x, 0, x, h);
			} else if (log.getType().equals("TEXTEDIT_RECORD")) {
				g2d.setColor(Color.CYAN.brighter().brighter().brighter());
				long time = log.getTimestamp();
				int x = (int) ((time - start) * xscale);
				g2d.drawLine(x, 0, x, h);
			}

			g2d.setColor(Color.BLACK);
			Stroke orgStroke = g2d.getStroke();
			g2d.setStroke(new BasicStroke(2));
			List<Point> points = new ArrayList<Point>();
			for (IPLFileStamp stamp : stamps) {
				int x = (int) ((stamp.getTime().getAsLong() - start) * xscale);
				int y = (int) (h - (stamp.getLineCount() * yscale));
				points.add(new Point(x, y));
			}

			Point previous = new Point(0, h);
			for (Point point : points) {
				g2d.drawLine(previous.x, previous.y, point.x, point.y);
				previous = point;
			}

			g2d.setStroke(orgStroke);
		}
	}
}
