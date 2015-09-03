/*
 * PLWorkingTime.java
 * Created on 2011/06/23
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package pres.loader.utils;

import java.util.ArrayList;
import java.util.List;

import pres.loader.logmodel.PLLog;
import pres.loader.model.IPLUnit;
import clib.common.time.CTimeRange;

/**
 * @author macchan
 */
public class PLWorkingTimeCalculator {

	private static final long threshold = 1000/* msec */* 60/* sec */* 5/* min */;

	public PLWorkingTimeCalculator() {
	}

	public PLWorkingTime calculate(IPLUnit unit) {
		List<PLLog> logs = unit.getOrderedLogs();
		logs = filterRemoveInValidBlockLog(logs);
		logs = filterNonOneTextEdit(logs);
		logs = filterValidEditLogs(logs);

		PLRangeDetectionStateMachine<PLLog, CTimeRange> sm = new PLRangeDetectionStateMachine<PLLog, CTimeRange>() {
			protected boolean isStartTag(PLLog current, PLLog next) {
				return current.getTime().diffrence(next.getTime()).getTime() <= threshold;
			}

			protected boolean isEndTag(PLLog current, PLLog next) {
				return current.getTime().diffrence(next.getTime()).getTime() > threshold;
			}

			protected CTimeRange createEvent(PLLog startTag, PLLog endTag) {
				return new CTimeRange(startTag.getTime(), endTag.getTime());
			}
		};
		return new PLWorkingTime(sm.process(logs));
	}

	private List<PLLog> filterValidEditLogs(List<PLLog> logs) {
		List<PLLog> filtered = new ArrayList<PLLog>();
		for (PLLog log : logs) {
			if (log.getType().equals("TEXTEDIT_RECORD")) {
				filtered.add(log);
			} else if (log.getSubType().equals("COMPILE")) {
				filtered.add(log);
			} else if (log.getSubType().equals("START_RUN")) {
				filtered.add(log);
			} else if (log.getSubType().equals("START_DEBUG")) {
				filtered.add(log);
			} else if (log.getType().equals("BLOCK_COMMAND_RECORD")) {
				filtered.add(log);
			}
		}
		return filtered;
	}

	public PLWorkingTime calculateForBE(IPLUnit unit) {

		List<PLLog> logs = unit.getOrderedLogs();
		logs = filterRemoveInValidBlockLog(logs);
		logs = filterNonOneTextEdit(logs);
		logs = filterValidEditLogs(logs);

		PLRangeDetectionStateMachine<PLLog, CTimeRange> beSm = new PLRangeDetectionStateMachine<PLLog, CTimeRange>() {
			@Override
			protected boolean isStartTag(PLLog current, PLLog next) {
				if (!current.getType().equals("BLOCK_COMMAND_RECORD")) {
					return false;
				}
				return current.getTime().diffrence(next.getTime()).getTime() <= threshold;
			}

			@Override
			protected boolean isEndTag(PLLog current, PLLog next) {
				if (current.getType().equals("TEXTEDIT_RECORD")) {
					return true;
				}
				return current.getTime().diffrence(next.getTime()).getTime() > threshold;
			}

			@Override
			protected CTimeRange createEvent(PLLog startTag, PLLog endTag) {
				return new CTimeRange(startTag.getTime(), endTag.getTime());
			}
		};

		return new PLWorkingTime(beSm.process(logs));
	}

	public List<PLLog> filterForND(List<PLLog> logs) {
		List<PLLog> filtered = new ArrayList<PLLog>();
		for (PLLog log : logs) {
			if (log.getSubType().equals("START_DEBUG")) {
				filtered.add(log);
			} else if (log.getSubType().equals("STEP")) {
				filtered.add(log);
			} else if (log.getSubType().equals("STOP_DEBUG")) {
				filtered.add(log);
			} else if (log.getSubType().equals("FOCUS_GAINED")) {
				filtered.add(log);
			}
		}
		return filtered;
	}
	
	public PLWorkingTime calculateForND(IPLUnit unit) {
		List<PLLog> logs = unit.getOrderedLogs();
		logs = filterForND(logs);
		
		PLRangeDetectionStateMachine<PLLog, CTimeRange> ndSm = new PLRangeDetectionStateMachine<PLLog, CTimeRange>() {
			@Override
			protected boolean isStartTag(PLLog current, PLLog next) {
				if (!(current.getSubType().equals("START_DEBUG") || current.getSubType().equals("STEP"))) {
					return false;
				}
				return current.getTime().diffrence(next.getTime()).getTime() <= threshold;
			}
			@Override
			protected boolean isEndTag(PLLog current, PLLog next) {
				if (current.getSubType().equals("FOCUS_GAINED") || current.getSubType().equals("STOP_DEBUG")) {
					return true;
				}
				return current.getTime().diffrence(next.getTime()).getTime() > threshold;
			}
			@Override
			protected CTimeRange createEvent(PLLog startTag, PLLog endTag) {
				System.out.println("s: " + startTag.getSubType() + " Time: " + startTag.getTimestamp());
				System.out.println("e: " + endTag.getSubType() + " Time: " + endTag.getTimestamp());
				return new CTimeRange(startTag.getTime(), endTag.getTime());
			}
		};
		return new PLWorkingTime(ndSm.process(logs));
	}
	
	private List<PLLog> filterNonOneTextEdit(List<PLLog> logs) {
		PLLogSeparator separator = new PLLogSeparator();
		separator.separateForNonOneTextEdit(logs);
		return separator.getNonSeparated();
	}

	//		//保井バージョン　JavaをかきながらBEを立ち上げていると，加算されてしまう問題がある　
	//		List<PLLog> logsForUse = new ArrayList<PLLog>();
	//		for (PLLog log : unit.getLogs()) {
	//			if ("BLOCK_COMMAND_RECORD".equals(log.getType())) {
	//				logsForUse.add(log);
	//			}
	//		}

	// 榊原バージョン　JAVA_TO_BLOCK時以外のBlockEditorのログを取得するようにした．
	private List<PLLog> filterRemoveInValidBlockLog(List<PLLog> logs) {
		PLLogSeparator separator = new PLLogSeparator();
		separator.separateForInvalidBlockEditorLog(logs);
		return separator.getNonSeparated();
	}
}
