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
public class FCWhilePanel extends FCPanel {

	private FCQuestionPanel question;
	private FCCompositePanel doClause = new FCCompositePanel();

	/**
	 * 
	 */
	public FCWhilePanel(String name) {
		question = new FCQuestionPanel(name);
	}

	/**
	 * @return the question
	 */
	public FCQuestionPanel getQuestion() {
		return question;
	}

	/**
	 * @return the doClause
	 */
	public FCCompositePanel getDoClause() {
		return doClause;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fcfw.components.FCPanel#layout()
	 */
	@Override
	public void layout() {
		question.layout();
		doClause.layout();

		int w = 0;
		int y = 0;

		w = Math.max(question.getWidth(), doClause.getWidth());
		w += X_MARGIN * 4;

		question.setLocation(getConnectionX() - question.getConnectionX(), y);
		y += question.getHeight();
		doClause.setLocation(getConnectionX() - doClause.getConnectionX(), y);
		y += doClause.getHeight();

		y += CONNECTOR_HEIGHT * 2;

		setSize(w, y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fcfw.FCPanel#paint(java.awt.Graphics2D)
	 */
	@Override
	public void paintComponent(Graphics2D g2d) {
		int centerX = question.getX() + question.getConnectionX();
		int breakCenterX = doClause.getX() + doClause.getWidth() + X_MARGIN;
		int continueCenterX = X_MARGIN;
		int centerY = question.getHeight() / 2;
		int bottomY = getHeight();
		int rightBottomY = bottomY - CONNECTOR_HEIGHT;
		int doBottomY = rightBottomY - CONNECTOR_HEIGHT;
		int continueCenterY = -(CONNECTOR_HEIGHT / 2);

		// do
		g2d.drawLine(centerX, 0, centerX, doBottomY);
		g2d.drawLine(centerX, doBottomY, continueCenterX, doBottomY);
		g2d.drawLine(continueCenterX, doBottomY, continueCenterX,
				continueCenterY);
		g2d.drawLine(continueCenterX, continueCenterY, centerX, continueCenterY);
		g2d.fillPolygon(new int[] { centerX, centerX - 5, centerX - 5 },
				new int[] { continueCenterY, continueCenterY + 3,
						continueCenterY - 3 }, 3);

		// else
		g2d.drawLine(centerX, centerY, breakCenterX, centerY);
		g2d.drawLine(breakCenterX, centerY, breakCenterX, rightBottomY);
		g2d.drawLine(breakCenterX, rightBottomY, centerX, rightBottomY);
		g2d.fillPolygon(new int[] { centerX, centerX + 5, centerX + 5 },
				new int[] { rightBottomY, rightBottomY + 3, rightBottomY - 3 },
				3);
		g2d.drawLine(centerX, rightBottomY, centerX, bottomY);

		paintChild(g2d, question);
		paintChild(g2d, doClause);

		// paintChild‚ÌŒã
		FontMetrics fm = g2d.getFontMetrics();
		g2d.drawString("true", centerX + 5, centerY * 2 + fm.getAscent());
		g2d.drawString("false", centerX + question.getWidth() / 2 + 5, centerY
				- question.getHeight() / 2 + fm.getAscent());
	}

	public int getConnectionX() {
		return X_MARGIN * 2 + doClause.getConnectionX();
	}

}
