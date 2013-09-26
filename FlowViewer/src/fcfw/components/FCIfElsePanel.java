/*
 * FIfElsePanel.java
 * Created on 2012/01/15
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package fcfw.components;

import java.awt.FontMetrics;
import java.awt.Graphics2D;

/**
 * @author macchan
 */
public class FCIfElsePanel extends FCPanel {

	private FCQuestionPanel question;
	private FCCompositePanel thenClause = new FCCompositePanel();
	private FCCompositePanel elseClause = new FCCompositePanel();

	/**
	 * 
	 */
	public FCIfElsePanel(String name) {
		question = new FCQuestionPanel(name);
	}

	/**
	 * @return the question
	 */
	public FCQuestionPanel getQuestion() {
		return question;
	}

	/**
	 * @return the ifClause
	 */
	public FCCompositePanel getThenClause() {
		return thenClause;
	}

	/**
	 * @return the elseClause
	 */
	public FCCompositePanel getElseClause() {
		return elseClause;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fcfw.components.FCPanel#layout()
	 */
	@Override
	public void layout() {
		question.layout();
		thenClause.layout();
		elseClause.layout();

		int w = 0;
		int y = 0;

		w = Math.max(question.getWidth(), thenClause.getWidth() + X_MARGIN
				+ elseClause.getWidth());

		question.setLocation(getConnectionX() - question.getConnectionX(), y);
		y += question.getHeight();
		thenClause.setLocation(getConnectionX() - thenClause.getConnectionX(), y);
		elseClause.setLocation(thenClause.getWidth() + X_MARGIN, y);
		y += Math.max(thenClause.getHeight(), elseClause.getHeight());

		setSize(w, y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fcfw.FCPanel#paint(java.awt.Graphics2D)
	 */
	@Override
	public void paintComponent(Graphics2D g2d) {
		int centerX = getConnectionX();
		int centerY = question.getHeight() / 2;
		int elseCenterX = thenClause.getWidth() + X_MARGIN
				+ elseClause.getConnectionX();
		int bottomY = getHeight();
		g2d.drawLine(centerX, 0, centerX, bottomY);
		g2d.drawLine(centerX, centerY, elseCenterX, centerY);
		g2d.drawLine(elseCenterX, centerY, elseCenterX, bottomY);
		g2d.drawLine(elseCenterX, bottomY, centerX, bottomY);
		g2d.fillPolygon(new int[] { centerX, centerX + 5, centerX + 5 },
				new int[] { bottomY, bottomY + 3, bottomY - 3 }, 3);
		paintChild(g2d, question);
		paintChild(g2d, thenClause);
		paintChild(g2d, elseClause);

		// paintChild‚ÌŒã
		FontMetrics fm = g2d.getFontMetrics();
		g2d.drawString("true", centerX + 5, centerY * 2 + fm.getAscent());
		g2d.drawString("false", centerX + question.getWidth() / 2 + 5, centerY
				- question.getHeight() / 2 + fm.getAscent());
	}

	public int getConnectionX() {
		return thenClause.getConnectionX();
	}

}
