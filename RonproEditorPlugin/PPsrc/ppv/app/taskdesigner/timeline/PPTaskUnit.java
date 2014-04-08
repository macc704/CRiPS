/*
 * PPTaskUnit.java
 * Created on 2012/06/07
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.taskdesigner.timeline;

import java.awt.Color;
import java.util.List;

import ppv.app.taskdesigner.model.PPRangeTask;
import pres.loader.logmodel.PLLog;
import pres.loader.model.IPLUnit;
import pres.loader.model.PLFile;
import pres.loader.model.PLProject;
import clib.common.filesystem.CPath;
import clib.common.time.CTime;
import clib.common.time.CTimeOrderedList;
import clib.common.time.CTimeRange;

/**
 * @author macchan マーカークラスとしての役割．
 */
public class PPTaskUnit<T extends PPRangeTask> implements IPLUnit {

	private String name;
	private IPPTaskProvider<T> provider;
	private Color color;

	/**
	 * 
	 */
	public PPTaskUnit(String name, IPPTaskProvider<T> provider, Color color) {
		this.name = name;
		this.provider = provider;
		this.color = color;
	}

	/**
	 * @return the provider
	 */
	public IPPTaskProvider<T> getProvider() {
		return provider;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pres.loader.model.IPLFileProvider#getFile(clib.common.time.CTime)
	 */
	@Override
	public PLFile getFile(CTime time) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pres.loader.model.IPLUnit#getProject()
	 */
	@Override
	public PLProject getProject() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pres.loader.model.IPLUnit#getPath()
	 */
	@Override
	public CPath getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pres.loader.model.IPLUnit#getRange()
	 */
	@Override
	public CTimeRange getRange() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pres.loader.model.IPLUnit#getStart()
	 */
	@Override
	public CTime getStart() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pres.loader.model.IPLUnit#getEnd()
	 */
	@Override
	public CTime getEnd() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pres.loader.model.IPLUnit#hasRange()
	 */
	@Override
	public boolean hasRange() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pres.loader.model.IPLUnit#getLogs()
	 */
	@Override
	public List<PLLog> getLogs() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pres.loader.model.IPLUnit#getOrderedLogs()
	 */
	@Override
	public CTimeOrderedList<PLLog> getOrderedLogs() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pres.loader.model.IPLUnit#getSavePoints()
	 */
	@Override
	public List<CTime> getSavePoints() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pres.loader.model.IPLUnit#getMaxLineCount()
	 */
	@Override
	public int getMaxLineCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pres.loader.model.IPLUnit#getLineCount(clib.common.time.CTime)
	 */
	@Override
	public int getLineCount(CTime time) {
		// TODO Auto-generated method stub
		return 0;
	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see pres.loader.model.IPLUnit#hasSource(java.lang.String)
//	 */
//	@Override
//	public boolean hasSource(String sourceName) {
//		// TODO Auto-generated method stub
//		return false;
//	}

}
