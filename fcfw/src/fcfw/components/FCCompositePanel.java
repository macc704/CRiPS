/*
 * FCCompositePanel.java
 * Created on 2012/01/15
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package fcfw.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

/**
 * @author macchan
 */
public class FCCompositePanel extends FCPanel {

	public static final int MARGIN = 20;

	private String comment;
	private List<FCPanel> children = new ArrayList<FCPanel>();

	public FCCompositePanel() {
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	public void add(FCPanel child) {
		children.add(child);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fcfw.components.FCPanel#layout()
	 */
	@Override
	public void layout() {
		int centerX = getConnectionX();
		int w = 0;
		int y = CONNECTOR_HEIGHT;

		if (comment != null) {
			y += MARGIN;
		}

		for (FCPanel child : children) {
			child.layout();
			int x = centerX - child.getConnectionX();
			child.setLocation(x, y);
			w = Math.max(w, child.getX() + child.getWidth());
			y += child.getHeight() + CONNECTOR_HEIGHT;
		}

		if (comment != null) {
			y += MARGIN;
			w += MARGIN;
		}

		setSize(w, y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fcfw.components.IFCPanel#paint(java.awt.Graphics2D)
	 */
	@Override
	public void paintComponent(Graphics2D g2d) {
		int centerX = getConnectionX();
		// int y = 0;
		// g2d.drawLine(centerX, y, centerX, y + CONNECTOR_HEIGHT);
		g2d.drawLine(centerX, 0, centerX, getHeight() - 1);

		for (FCPanel child : children) {
			paintChild(g2d, child);
			// y += child.getHeight() + CONNECTOR_HEIGHT;
			// g2d.drawLine(centerX, y, centerX, y + CONNECTOR_HEIGHT);
		}

		if (comment != null) {
			Color c = g2d.getColor();
			g2d.setColor(Color.RED);
			g2d.drawString(comment, 0, X_MARGIN - 3);

			Stroke st = g2d.getStroke();
			float dash[] = { 10.0f, 3.0f };
			BasicStroke dashStroke = new BasicStroke(2.0f,
					BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash,
					0.0f);
			g2d.setStroke(dashStroke);
			g2d.drawRect(0, MARGIN, getWidth() - 1, getHeight() - 1 - MARGIN
					* 2);
			g2d.setStroke(st);

			g2d.setColor(c);
		}
	}

	public int getConnectionX() {
		int x = Math.min(getWidth(), COMMAND_WIDTH / 2);
		for (FCPanel child : children) {
			x = Math.max(x, child.getConnectionX());
		}

		if (comment != null) {
			x += MARGIN;
		}
		return x;
	}

}
