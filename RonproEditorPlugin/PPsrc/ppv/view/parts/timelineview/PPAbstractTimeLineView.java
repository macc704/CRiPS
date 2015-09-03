/*
 * PPAbstractTimeLineView.java
 * Created on 2011/06/09
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.view.parts.timelineview;

import java.awt.Dimension;

import javax.swing.JPanel;

import pres.loader.model.IPLUnit;
import clib.view.timeline.model.CTimeTransformationModel;

/**
 * @author macchan
 */
public abstract class PPAbstractTimeLineView extends JPanel {

	private static final long serialVersionUID = 1L;

	private IPLUnit unit;
	private CTimeTransformationModel timeModel;

	public PPAbstractTimeLineView(CTimeTransformationModel timeModel,
			IPLUnit unit) {
		this.timeModel = timeModel;
		this.unit = unit;
		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	}

	public CTimeTransformationModel getTimeModel() {
		return timeModel;
	}

	public IPLUnit getUnit() {
		return unit;
	}

}
