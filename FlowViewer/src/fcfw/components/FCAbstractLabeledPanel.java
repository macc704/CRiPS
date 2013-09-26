/*
 * FCAbstractNamedPanel.java
 * Created on 2012/01/15
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package fcfw.components;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

import javax.swing.SwingUtilities;

/**
 * @author macchan
 */
public abstract class FCAbstractLabeledPanel extends FCPanel {

	private String label = "";

	/**
	 * Constructor.
	 */
	public FCAbstractLabeledPanel(String label) {
		this.label = label;
	}

	/**
	 * @return the name
	 */
	public String getLabel() {
		return label;
	}

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
	 * @see fcfw.components.FCPanel#paint(java.awt.Graphics2D)
	 */
	@Override
	public final void paintComponent(Graphics2D g2d) {
		paintString(g2d);
		paintFigure(g2d);
	}

	public void paintString(Graphics2D g2d) {
		FontMetrics fm = g2d.getFontMetrics();
		int width = SwingUtilities.computeStringWidth(fm, label);
		int x = getWidth() / 2 - width / 2;
		int y = getHeight() / 2 - fm.getHeight() / 2 + fm.getAscent();
		g2d.drawString(getLabel(), x, y);
	}

	public abstract void paintFigure(Graphics2D g2d);

}
