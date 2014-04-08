/*
 * PPEventMarker.java
 * Created on 2011/06/07
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.view.parts.timelineview;

import java.awt.Color;
import java.util.List;

import pres.loader.logmodel.PLLog;
import pres.loader.model.IPLUnit;
import clib.common.utils.ICChecker;
import clib.view.timeline.model.CTimeTransformationModel;

/**
 * @author macchan
 */
public class PPEventLineView extends PPAbstractEventLineView {

	private static final long serialVersionUID = 1L;

	public PPEventLineView(CTimeTransformationModel timeModel, IPLUnit unit) {
		super(timeModel, unit);
	}

	public PPEventLineView(CTimeTransformationModel timeModel, IPLUnit unit,
			final String subType, Color color) {
		super(timeModel, unit);
		addEventsBySubType(subType, color);
	}

	public void addEventsBySubType(final String subType, Color color) {
		addEvents(new ICChecker<PLLog>() {
			public boolean check(PLLog t) {
				return subType.equals(t.getSubType());
			}
		}, color);
	}

	public void addEvents(ICChecker<PLLog> checker, Color color) {
		List<PLLog> logs = getUnit().getOrderedLogs().select(checker);
		addEvents(logs, color);
	}

	public void addEvents(List<PLLog> logs, Color color) {
		for (PLLog log : logs) {
			PPEventView ev = new PPEventView(log, color);
			addEventView(ev);
		}
		reLayout();
	}

}

/***********************************************************
 * Panelにしたかったが，大量(10000とか)をComponentを乗せるとsortで遅くなるので ひとまずやめ
 ***************************************************************/

class PPEventView extends PPAbstractEventView {

	private static final long serialVersionUID = 1L;

	private PLLog log;

	public PPEventView(PLLog log, Color color) {
		super(color);
		this.log = log;
		setToolTipText(log.toString());
	}

	public PLLog getLog() {
		return log;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ppv.view.timeline.AbstractEventLineView#refreshMe(clib.view.timeline.
	 * model.CTimeTransformationModel)
	 */
	@Override
	public void refreshMe(CTimeTransformationModel timeModel, int height) {
		int x = (int) timeModel.time2X(getLog().getTime());
		setBounds(x, 0, 1, height);
	}

}
