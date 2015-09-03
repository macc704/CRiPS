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
import pres.loader.utils.PLRangeDetectionStateMachine;
import clib.common.time.CTime;
import clib.view.timeline.model.CTimeTransformationModel;

/**
 * @author macchan
 */
public abstract class PPAbstractStateLineView extends PPAbstractEventLineView {

	private static final long serialVersionUID = 1L;

	private Color color;

	public PPAbstractStateLineView(CTimeTransformationModel timeModel,
			IPLUnit unit, Color color) {
		super(timeModel, unit);
		this.color = color;
	}

	protected void parseLogs() {
		List<PLLog> logs = getLogsForParse();
		PLRangeDetectionStateMachine<PLLog, PPStateView> sm = new PLRangeDetectionStateMachine<PLLog, PPStateView>() {
			protected boolean isEndTag(PLLog current, PLLog next) {
				return PPAbstractStateLineView.this.isEndTag(current, next);
			}

			protected boolean isStartTag(PLLog current, PLLog next) {
				return PPAbstractStateLineView.this.isStartTag(current, next);
			}

			protected PPStateView createEvent(PLLog startTag, PLLog endTag) {
				return PPAbstractStateLineView.this.createEvent(startTag,
						endTag);
			}
		};
		List<PPStateView> views = sm.process(logs);
		for (PPStateView view : views) {
			addEventView(view);
		}
		reLayout();
	}

	protected abstract List<PLLog> getLogsForParse();

	protected abstract boolean isStartTag(PLLog current, PLLog next);

	protected abstract boolean isEndTag(PLLog current, PLLog next);

	protected PPStateView createEvent(PLLog startTag, PLLog endTag) {
		return new PPStateView(startTag.getTime(), endTag.getTime(), color);
	}
}

class PPStateView extends PPAbstractEventView {
	private static final long serialVersionUID = 1L;

	private CTime start;
	private CTime end;

	public PPStateView(CTime start, CTime end, Color color) {
		super(color);
		this.start = start;
		this.end = end;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ppv.view.timeline.AbstractEventLineView#refreshMe(clib.view.timeline.
	 * model.CTimeTransformationModel, int)
	 */
	@Override
	public void refreshMe(CTimeTransformationModel timeModel, int height) {
		int x1 = (int) timeModel.time2X(start);
		int x2 = (int) timeModel.time2X(end);

		// minの幅を1にする
		if (x2 == x1) {
			x2++;
		}
		setBounds(x1, 0, x2 - x1, height);
	}

}
