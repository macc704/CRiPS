/*
 * PLWorkingTime.java
 * Created on 2013/02/07 by macchan
 * Copyright(c) 2013 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.loader.utils;

import java.util.List;

import clib.common.time.CTime;
import clib.common.time.CTimeInterval;
import clib.common.time.CTimeRange;

/**
 * PLWorkingTime
 */
public class PLWorkingTime {

	private List<CTimeRange> workingTimes;

	public PLWorkingTime(List<CTimeRange> workingTimes) {
		this.workingTimes = workingTimes;
	}

	/**
	 * @param time
	 * @return
	 */
	public CTimeInterval getTotalUntil(CTime time) {
		long accumulated = 0;
		for (CTimeRange workingTime : workingTimes) {
			if (workingTime.isBefore(time)) {
				accumulated += workingTime.getLength().getTime();
			}
			if (workingTime.isIncluding(time)) {
				accumulated += workingTime.getStart().diffrence(time).getTime();
			}
		}
		return new CTimeInterval(accumulated);
	}

	/**
	 * @return
	 */
	public CTimeInterval getTotalTime() {
		long accumulated = 0;
		for (CTimeRange workingTime : workingTimes) {
			accumulated += workingTime.getLength().getTime();
		}
		return new CTimeInterval(accumulated);
	}

	/**
	 * @param time
	 * @return
	 */
	public boolean isWorking(CTime time) {
		for (CTimeRange workingTime : workingTimes) {
			if (workingTime.isIncluding(time)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the workingTimes
	 */
	public List<CTimeRange> getWorkingTimes() {
		return workingTimes;
	}

}
