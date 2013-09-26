/*
 * FCModulePanel.java
 * Created on 2012/01/16
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package fcfw.components;

import java.awt.Graphics2D;

/**
 * @author macchan
 * 
 */
public class FCModulePanel extends FCPanel {

	private FCTerminalPanel start = new FCTerminalPanel();
	private FCCompositePanel body = new FCCompositePanel();
	private FCTerminalPanel end = new FCTerminalPanel();

	public void add(FCPanel child) {
		body.add(child);
	}

	/**
	 * @return the body
	 */
	public FCCompositePanel getBody() {
		return body;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fcfw.components.FCPanel#layout()
	 */
	@Override
	public void layout() {
		start.layout();
		body.layout();
		end.layout();

		int y = 0;
		start.setLocation(getConnectionX() - start.getConnectionX(), y);
		y += start.getHeight() + CONNECTOR_HEIGHT;
		body.setLocation(getConnectionX() - body.getConnectionX(), y);
		y += body.getHeight() + CONNECTOR_HEIGHT;
		end.setLocation(getConnectionX() - end.getConnectionX(), y);
		y += end.getHeight();

		setSize(body.getWidth(), y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fcfw.components.FCPanel#paintComponent(java.awt.Graphics2D)
	 */
	@Override
	public void paintComponent(Graphics2D g2d) {
		g2d.translate(getX(), getY());
		int centerX = getConnectionX();
		g2d.drawLine(centerX, 0, centerX, getHeight());
		paintChild(g2d, start);
		paintChild(g2d, body);
		paintChild(g2d, end);
		g2d.translate(-getX(), -getY());
	}

	public int getConnectionX() {
		int x = start.getConnectionX();
		return Math.max(x, body.getConnectionX());
	}

}
