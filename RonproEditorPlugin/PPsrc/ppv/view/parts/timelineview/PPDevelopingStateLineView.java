/*
 * PPSourceCountView.java
 * Created on 2011/06/07
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.view.parts.timelineview;

import java.awt.Color;
import java.util.List;

import pres.core.model.PRProjectLog;
import pres.loader.logmodel.PLLog;
import pres.loader.model.IPLUnit;
import clib.view.timeline.model.CTimeTransformationModel;

/**
 * @author macchan
 */
public class PPDevelopingStateLineView extends PPSubtypeStateLineView {

	private static final long serialVersionUID = 1L;

	public PPDevelopingStateLineView(CTimeTransformationModel timeModel,
			IPLUnit unit, Color color) {
		super(timeModel, unit, color, PRProjectLog.SubType.START.toString(),
				PRProjectLog.SubType.STOP.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ppv.view.timeline.PPStateLineView#getLogsForParse()
	 */
	@Override
	protected List<PLLog> getLogsForParse() {
		// そのものではなくて，ROOTじゃないと，START, STOPログがない
		return getUnit().getProject().getRootPackage().getLogs();
	}
}
