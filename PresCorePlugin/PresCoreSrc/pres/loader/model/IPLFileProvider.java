/*
 * IPPSourceProvider.java
 * Created on 2011/06/06
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package pres.loader.model;

import clib.common.time.CTime;

/**
 * @author macchan
 * 
 */
public interface IPLFileProvider {
	public PLFile getFile(CTime time);
}
