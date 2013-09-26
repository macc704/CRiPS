/*
 * FCTerminalPanel.java
 * Created on 2012/01/16
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package fcfw.components;

import java.awt.Graphics2D;

/**
 * @author macchan
 * 
 */
public class FCTerminalPanel extends FCPanel {

	/*
	 * (non-Javadoc)
	 * 
	 * @see fcfw.components.FCPanel#layout()
	 */
	@Override
	public void layout() {
		setSize(COMMAND_WIDTH, COMMAND_HEIGHT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fcfw.components.FCPanel#paintComponent(java.awt.Graphics2D)
	 */
	@Override
	public void paintComponent(Graphics2D g2d) {
		int r2r = COMMAND_HEIGHT;
		int x = getConnectionX() - (r2r / 2);
		g2d.drawOval(x, 0, r2r, r2r);
	}
}
