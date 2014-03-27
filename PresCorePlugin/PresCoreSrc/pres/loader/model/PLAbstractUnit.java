/*
 * PPAbstractPresModel.java
 * Created on 2011/06/05
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package pres.loader.model;

import java.util.List;

import pres.loader.logmodel.PLLog;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CPath;
import clib.common.time.CTime;
import clib.common.time.CTimeOrderedList;
import clib.common.time.CTimeRange;
import clib.common.utils.ICChecker;

/**
 * @author macchan
 */
public abstract class PLAbstractUnit implements IPLUnit {

	private PLProject project;
	private CDirectory dir;
	private CPath path;

	public static int count = 0;
	public static int uncount = 0;

	private CTimeOrderedList<PLLog> logs = new CTimeOrderedList<PLLog>();

	public PLAbstractUnit(PLProject project, CDirectory dir, CPath path) {
		count++;
		this.project = project;
		this.dir = dir;
		this.path = path;
	}

	public PLProject getProject() {
		return project;
	}

	public String getName() {
		return getPath().toString();
	}

	public CDirectory getDir() {
		return dir;
	}

	public CPath getPath() {
		return path;
	}

	protected void putLog(PLLog log) {
		logs.add(log);
	}

	protected void clearLog() {
		logs.clear();
	}

	public List<PLLog> getLogs() {
		return logs.getElements();
	}

	public CTimeOrderedList<PLLog> getOrderedLogs() {
		return logs;
	}

	public CTime getStart() {
		if (!hasRange()) {
			throw new RuntimeException("No Range");
		}
		return new CTime(logs.getFirst().getTimestamp());
	}

	public CTime getEnd() {
		if (!hasRange()) {
			throw new RuntimeException("No Range");
		}
		return new CTime(logs.getLast().getTimestamp());
	}

	public CTimeRange getRange() {
		if (!hasRange()) {
			throw new RuntimeException("No Range");
		}
		return new CTimeRange(getStart(), getEnd());
	}

	public boolean hasRange() {
		return logs.size() > 0;
	}

	public PLLog searchLog(CTime time, ICChecker<PLLog> checker) {
		return logs.searchElement(time, checker);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		uncount++;
	}
}
