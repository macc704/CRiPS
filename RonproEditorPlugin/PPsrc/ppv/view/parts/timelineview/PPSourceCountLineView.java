/*
 * PPSourceCountView.java
 * Created on 2011/06/07
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.view.parts.timelineview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import pres.loader.model.IPLUnit;
import clib.common.time.CTime;
import clib.view.timeline.model.CTimeTransformationModel;

/**
 * @author macchan
 */
public class PPSourceCountLineView extends PPAbstractTimeLineView {

	private static final long serialVersionUID = 1L;

	public PPSourceCountLineView(CTimeTransformationModel timeModel,
			IPLUnit unit) {
		super(timeModel, unit);
		setOpaque(false);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// prepare
		Graphics2D g2d = (Graphics2D) g;
		int w = getWidth();
		int h = getHeight();

		// calculate
		double yscale = h / (double) getUnit().getMaxLineCount();
		List<Point> points = new ArrayList<Point>();
		points.add(new Point(0, h));
		for (CTime savePoint : getUnit().getSavePoints()) {
			int x = (int) getTimeModel().time2X(savePoint);
			points.add(new Point(x, points.get(points.size() - 1).y));
			int y = (int) (h - (getUnit().getLineCount(savePoint) * yscale));
			points.add(new Point(x, y));
		}
		points.add(new Point(w, 0));

		// draw line
		Stroke orgStroke = g2d.getStroke();
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(2));
		Point previous = new Point(0, h);
		for (Point point : points) {
			g2d.drawLine(previous.x, previous.y, point.x, point.y);
			previous = point;
		}
		g2d.setStroke(orgStroke);
	}
}
