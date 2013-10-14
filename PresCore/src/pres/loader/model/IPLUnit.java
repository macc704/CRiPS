/*
 * IPPPresModel.java
 * Created on 2011/06/05
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package pres.loader.model;

import java.util.List;

import pres.loader.logmodel.PLLog;
import clib.common.filesystem.CPath;
import clib.common.time.CTime;
import clib.common.time.CTimeOrderedList;
import clib.common.time.CTimeRange;

/**
 * @author macchan
 */
public interface IPLUnit extends IPLFileProvider {

	public String getName();

	public PLProject getProject();

	public CPath getPath();

	public CTimeRange getRange();

	public CTime getStart();

	public CTime getEnd();

	public boolean hasRange();

	public List<PLLog> getLogs();

	public CTimeOrderedList<PLLog> getOrderedLogs();

	public List<CTime> getSavePoints();

	public int getMaxLineCount();

	public int getLineCount(CTime time);

	/**
	 * @param sourceName
	 * @return
	 */
	//public boolean hasSource(String sourceName);

}
