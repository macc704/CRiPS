/*
 * PresFileStamp.java
 * Created on Jul 6, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package pres.loader.model;

import clib.common.filesystem.CFile;
import clib.common.time.CTime;
import clib.common.time.ICTimeOrderable;

/**
 * @author macchan
 * 
 */
public interface IPLFileStamp extends ICTimeOrderable {

	public CTime getTime();

	public String getSource();

	public int getLineCount();

	public CFile getFile();

}
