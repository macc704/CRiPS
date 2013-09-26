/*
 * FCFlowchartPanel.java
 * Created on 2012/01/15
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package fcfw;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import fcfw.components.FCModulePanel;

/**
 * FCFW : FlowChart creating FrameWork
 * 
 * @author macchan
 */
public class FCFlowchartPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public static final int X_MARGIN = 75;
	public static final int Y_MARGIN = 20;

	private FCModulePanel main = new FCModulePanel();

	public FCFlowchartPanel() {
		setBackground(Color.WHITE);
	}

	public void refresh() {
		main.layout();
		setPreferredSize(new Dimension(X_MARGIN * 2 + main.getWidth(), Y_MARGIN
				* 2 + main.getHeight()));
		validate();
		repaint();
	}

	/**
	 * @return the main
	 */
	public FCModulePanel getRootPanel() {
		return main;
	}

	/*
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(X_MARGIN, Y_MARGIN);
		// g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_BUTT,
		// BasicStroke.JOIN_BEVEL));
		main.paint(g2d);
	}
}
