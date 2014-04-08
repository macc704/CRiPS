/*
 * PPEventMarker.java
 * Created on 2011/06/07
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.view.parts.timelineview;

import java.awt.Color;
import java.util.List;

import pres.loader.model.IPLUnit;
import clib.common.time.CTime;
import clib.common.time.CTimeRange;
import clib.view.timeline.model.CTimeTransformationModel;

/**
 * @author macchan
 */
public final class PPNewStateLineView extends PPAbstractEventLineView {

	private static final long serialVersionUID = 1L;

	public PPNewStateLineView(CTimeTransformationModel timeModel, IPLUnit unit) {
		super(timeModel, unit);
	}

	public void addData(Color color, List<CTimeRange> ranges) {
		for (CTimeRange range : ranges) {
			PPNewStateView view = new PPNewStateView(range.getStart(),
					range.getEnd(), color);
			addEventView(view);
		}
		reLayout();
	}
}

class PPNewStateView extends PPAbstractEventView {
	private static final long serialVersionUID = 1L;

	private CTime start;
	private CTime end;

	public PPNewStateView(CTime start, CTime end, Color color) {
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
