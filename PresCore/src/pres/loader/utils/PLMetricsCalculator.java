/*
 * PLMetricsCalculator.java
 * Created on 2011/06/30
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package pres.loader.utils;

import pres.loader.logmodel.PLLog;
import pres.loader.model.IPLUnit;
import clib.common.time.CTime;
import clib.common.time.CTimeInterval;
import clib.common.time.CTimeOrderedList;
import clib.common.utils.ICChecker;

/**
 * @author macchan
 */
public class PLMetricsCalculator {

	private IPLUnit unit;

	// cashes
	private CTimeOrderedList<PLLog> compiles;
	private CTimeOrderedList<PLLog> saves;
	private CTimeOrderedList<PLLog> runs;
	private CTimeOrderedList<PLLog> allEvents;
	private CTime startTime;
	private CTime endTime;
	private PLWorkingTime workingTime;

	//for block editor
	private CTimeOrderedList<PLLog> addBlock;
	private CTimeOrderedList<PLLog> removeBlock;
	private PLWorkingTime beWorkingTime;
	
	// for debugger by hakamata
	private CTimeOrderedList<PLLog> debugs;
	private CTimeOrderedList<PLLog> steps;
	private CTimeOrderedList<PLLog> plays;
	private CTimeOrderedList<PLLog> aveSpeed;
	private PLWorkingTime ndWorkingTime;

	/**
	 * @param unit
	 * @param time
	 */
	public PLMetricsCalculator(IPLUnit unit) {
		this.unit = unit;
		initialize();
	}

	private void initialize() {
		this.compiles = getLogsBySubType("COMPILE");
		this.saves = getLogsBySubType("SAVE");
		this.runs = getLogsBySubType("START_RUN");
		this.allEvents = unit.getOrderedLogs();
		if (unit.hasRange()) {
			this.startTime = unit.getStart();
			this.endTime = unit.getEnd();
		} else {
			this.startTime = new CTime();
			this.endTime = new CTime();
		}
		this.workingTime = new PLWorkingTimeCalculator().calculate(unit);

		//for block
		this.addBlock = getLogsBySubType("BLOCK_ADDED");
		this.removeBlock = getLogsBySubType("BLOCK_REMOVED");
		this.beWorkingTime = new PLWorkingTimeCalculator().calculateForBE(unit);
		
		// for debugger by hakamata
		this.debugs = getLogsBySubType("START_DEBUG");
		this.steps = getLogsBySubType("STEP");
		this.plays = getLogsBySubType("DEBUG_PLAY");
		this.aveSpeed = getLogsBySubType("DEBUG_SPEED");
		fixDebugCount();
		this.ndWorkingTime = new PLWorkingTimeCalculator().calculateForND(unit);
	}

	public int getCompileCount(CTime time) {
		return getIndex(compiles, time);
	}

	public int getCompileCount() {
		return getCompileCount(endTime);
	}

	public int getSaveCount(CTime time) {
		return getIndex(saves, time);
	}

	public int getSaveCount() {
		return getSaveCount(endTime);
	}

	public int getRunCount(CTime time) {
		return getIndex(runs, time);
	}

	public int getRunCount() {
		return getRunCount(endTime);
	}

	public int getLineCount(CTime time) {
		return unit.getLineCount(time);
	}

	public int getLineCount() {
		return unit.getMaxLineCount();
		// return getLineCount(endTime);
	}

	public CTimeInterval getLeadingTime(CTime time) {
		return startTime.diffrence(time);
	}

	public CTimeInterval getLeadingTime() {
		return getLeadingTime(endTime);
	}

	public CTimeInterval getWorkingTime(CTime time) {
		return workingTime.getTotalUntil(time);
	}

	public CTimeInterval getWorkingTime() {
		return getWorkingTime(endTime);
	}

	public boolean isWorking(CTime time) {
		return workingTime.isWorking(time);
	}

	public int getAllEventCount() {
		return allEvents.size();
	}

	private int getIndex(CTimeOrderedList<PLLog> logs, CTime time) {
		PLLog log = logs.searchElementBefore(time);
		int index = 0;
		if (log != null) {
			index = logs.getElements().indexOf(log) + 1;
		}
		return index;
	}

	private CTimeOrderedList<PLLog> getLogsBySubType(final String subtype) {
		return unit.getOrderedLogs().select(new ICChecker<PLLog>() {
			public boolean check(PLLog t) {
				return t.getSubType().equals(subtype);
			}
		});
	}

	// block editor

	public CTimeInterval getBEWorkingTime(CTime time) {
		return beWorkingTime.getTotalUntil(time);
	}

	public CTimeInterval getBEWorkingTime() {
		return getBEWorkingTime(endTime);
	}

	public boolean isBEWorking(CTime time) {
		return beWorkingTime.isWorking(time);
	}

	/**
	 * @param time
	 * @return
	 */
	public int getBlockCount(CTime time) {
		return getIndex(addBlock, time) - getIndex(removeBlock, time);
	}

	/**
	 * @return
	 */
	public int getMaxBlockCount() {
		return getBlockCount(endTime);
	}

	/**
	 * @return
	 */
	public int getAddBlockCount() {
		return addBlock.size();
	}

	/**
	 * @return
	 */
	public int getRemoveBlockCount() {
		return removeBlock.size();
	}

	
	// for debugger by hakamata
	public int getDebugCount(CTime time) {
		return getIndex(debugs, time);
	}

	public int getDebugCount() {
		return getDebugCount(endTime);
	}
	
	public void fixDebugCount() {
		CTimeOrderedList<PLLog> tmpDebugs = new CTimeOrderedList<PLLog>();
		int j=0;
		if(debugs.size() > 0){
			tmpDebugs.add(debugs.get(0));
		}
		for(int i=1; i < debugs.size(); i++) {
			if(tmpDebugs.get(j).getTimestamp() != debugs.get(i).getTimestamp()){
				tmpDebugs.add(debugs.get(i));
				j++;
			}
		}
		debugs = tmpDebugs;
	}
	
	public int getStepCount(CTime time) {
		return getIndex(steps, time);
	}

	public int getStepCount() {
		return getStepCount(endTime);
	}
	
	public int getPlayCount(CTime time) {
		return getIndex(plays, time);
	}

	public int getPlayCount() {
		return getPlayCount(endTime);
	}
	
	public int getAverageSpeed(CTime time) {
		PLLog log = aveSpeed.searchElementBefore(time);
		int index = 0;
		if (log != null) {
			index = aveSpeed.getElements().indexOf(log) + 1;
		}
		int speeds = 500;
		for(int i = 0; i < index; i++){
			speeds += Integer.parseInt("" + aveSpeed.get(i).getArguments().get(1).toString());
		}
		if(index > 0) {
			return speeds / index;
		}
		else {
			return 500;
		}
	}

	public int getAverageSpeed() {
		return getAverageSpeed(endTime);
	}
	public CTimeInterval getNDWorkingTime(CTime time) {
		return ndWorkingTime.getTotalUntil(time);
	}

	public CTimeInterval getNDWorkingTime() {
		return getNDWorkingTime(endTime);
	}

	public boolean isNDWorking(CTime time) {
		return ndWorkingTime.isWorking(time);
	}
}
