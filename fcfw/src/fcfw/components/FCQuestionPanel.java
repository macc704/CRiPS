/*
 * FCDiamondPanel.java
 * Created on 2012/01/15
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package fcfw.components;

import java.awt.Graphics2D;

/**
 * @author macchan
 * 
 */
public class FCQuestionPanel extends FCAbstractLabeledPanel {

	/**
	 * Constructor.
	 */
	public FCQuestionPanel(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fcfw.components.FCAbstractNamedPanel#paintFigure(java.awt.Graphics2D)
	 */
	@Override
	public void paintFigure(Graphics2D g2d) {
		int centerX = getWidth() / 2;
		int centerY = getHeight() / 2;
		// è„Ç©ÇÁéûåvâÒÇË
		int w = getWidth();
		int h = getHeight();
		g2d.drawLine(centerX, 0, w, centerY);
		g2d.drawLine(w, centerY, centerX, h);
		g2d.drawLine(centerX, h, 0, centerY);
		g2d.drawLine(0, centerY, centerX, 0);
	}
}