/*
 * PPEventMarker.java
 * Created on 2011/06/07
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.view.parts.timelineview;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import pres.loader.model.IPLUnit;
import clib.view.timeline.model.CTimeTransformationModel;

/**
 * @author macchan
 */
public abstract class PPAbstractEventLineView extends PPAbstractTimeLineView {

	private static final long serialVersionUID = 1L;

	private List<PPAbstractEventView> eventViews = new ArrayList<PPAbstractEventView>();

	public PPAbstractEventLineView(CTimeTransformationModel timeModel,
			IPLUnit unit) {
		super(timeModel, unit);
		initialize();
	}

	private void initialize() {
		setLayout(null);
		setOpaque(false);
		getTimeModel().addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				reLayout();
			}
		});
	}

	protected void reLayout() {
		for (PPAbstractEventView ev : eventViews) {
			ev.refreshMe(getTimeModel(), getHeight());
		}
	}

	// Swing独自の方法だと，Sortして遅くなるので，一時的に変更

	// public void addEventView(PPAbstractEventView view) {
	// eventViews.add(view);
	// add(view);
	// }

	public void addEventView(PPAbstractEventView view) {
		eventViews.add(view);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		for (PPAbstractEventView ev : eventViews) {
			g2d.setBackground(ev.getBackground());
			g2d.setColor(ev.getForeground());
			g2d.fill(ev.getBounds());
		}
	}
}

abstract class PPAbstractEventView extends JPanel {
	private static final long serialVersionUID = 1L;
	private Color color;

	public PPAbstractEventView(Color color) {
		this.color = color;
		setBackground(color);
		setForeground(color);
		setOpaque(true);
	}

	public Color getColor() {
		return color;
	}

	public abstract void refreshMe(CTimeTransformationModel timeModel,
			int height);
}
