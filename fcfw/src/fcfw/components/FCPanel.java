/*
 * FCPanel.java
 * Created on 2012/01/15
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package fcfw.components;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * @author macchan
 */
public abstract class FCPanel {

	public static final boolean DEBUG = false;

	public static final int COMMAND_WIDTH = 100;
	public static final int COMMAND_HEIGHT = 30;
	public static final int CONNECTOR_HEIGHT = 15;
	public static final int X_MARGIN = 20;

	private FCPanel parent;

	private int x = 0;
	private int y = 0;
	private int width = 0;
	private int height = 0;

	/**
	 * 
	 */
	public FCPanel() {
	}

	/**
	 * @return the parent
	 */
	public FCPanel getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(FCPanel parent) {
		this.parent = parent;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public abstract void layout();

	public final void paint(Graphics2D g2d) {
		{
			Color c = g2d.getColor();
			g2d.setColor(Color.WHITE);
			g2d.fillRect(1, 1, getWidth() - 2, getHeight() - 2);
			g2d.setColor(c);
		}
		if (DEBUG) {
			Color c = g2d.getColor();
			g2d.setColor(Color.RED);
			g2d.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
			g2d.setColor(c);
		}
		{
			// g2d.translate(getStartX(), 0);
			paintComponent(g2d);
			// g2d.translate(-getStartX(), 0);
		}
	}

	public abstract void paintComponent(Graphics2D g2d);

	protected void paintChild(Graphics2D g2d, FCPanel child) {
		g2d.translate(child.getX(), child.getY());
		child.paint(g2d);
		g2d.translate(-child.getX(), -child.getY());
	}

	public int getConnectionX() {
		return COMMAND_WIDTH / 2;
	}

}
