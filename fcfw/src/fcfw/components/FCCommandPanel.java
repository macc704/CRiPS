/*
 * FCCommandPanel.java
 * Created on 2012/01/15
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package fcfw.components;

import java.awt.Graphics2D;

/**
 * @author macchan
 */
public class FCCommandPanel extends FCAbstractLabeledPanel {

	/**
	 * Constructor.
	 */
	public FCCommandPanel(String name) {
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
		g2d.drawRect(0, 0, getWidth(), getHeight());
	}

}
