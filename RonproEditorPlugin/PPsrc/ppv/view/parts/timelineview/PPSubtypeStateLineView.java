/*
 * PPSubtypeStateLineView.java
 * Created on 2011/06/30
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
public class PPSubtypeStateLineView extends PPAbstractStateLineView {

	private static final long serialVersionUID = 1L;

	private String subtype1;
	private String subtype2;

	private ICChecker<PLLog> checker1 = new ICChecker<PLLog>() {
		public boolean check(PLLog t) {
			return subtype1.equals(t.getSubType());
		}
	};

	private ICChecker<PLLog> checker2 = new ICChecker<PLLog>() {
		public boolean check(PLLog t) {
			return subtype2.equals(t.getSubType());
		}
	};

	public PPSubtypeStateLineView(CTimeTransformationModel timeModel,
			IPLUnit unit, Color color, String subtype1, String subtype2) {
		super(timeModel, unit, color);
		this.subtype1 = subtype1;
		this.subtype2 = subtype2;
		parseLogs();
	}

	protected List<PLLog> getLogsForParse() {
		return getUnit().getOrderedLogs().select(new ICChecker<PLLog>() {
			public boolean check(PLLog t) {
				return checker1.check(t) || checker2.check(t);
			}
		}).getElements();
	}

	protected boolean isEndTag(PLLog current, PLLog next) {
		return checker2.check(current);
	}

	protected boolean isStartTag(PLLog current, PLLog next) {
		return checker1.check(current);
	}

}
