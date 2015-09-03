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
public class PLFileStamp implements IPLFileStamp {

	private CFile file;
	private CTime time;
	private int lineCount;

	public PLFileStamp(CFile file) {
		this.file = file;
		this.time = new CTime(Long.parseLong(file.getName().getName()));
		this.lineCount = file.loadTextAsList().size();
	}

	public CTime getTime() {
		return time;
	}

	public String getSource() {
		return getFile().loadTextAsIs();
	}

	public CFile getFile() {
		return file;
	}

	public int getLineCount() {
		return this.lineCount;
	}

}
