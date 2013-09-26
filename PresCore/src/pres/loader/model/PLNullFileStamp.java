/*
 * PLTimeStampImpl.java
 * Created on 2011/06/06
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package pres.loader.model;

import clib.common.filesystem.CFile;
import clib.common.time.CTime;

/**
 * @author macchan
 * 
 */
public class PLNullFileStamp implements IPLFileStamp {

	private CTime time;

	public PLNullFileStamp(CTime time) {
		this.time = time;
	}

	public CTime getTime() {
		return time;
	}

	public String getSource() {
		return "";
	}

	public CFile getFile() {
		throw new RuntimeException("not implemented");
	}

	public int getLineCount() {
		return 0;
	}
}
