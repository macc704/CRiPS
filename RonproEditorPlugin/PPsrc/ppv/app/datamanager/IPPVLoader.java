/*
 * IPPVLoader.java
 * Created on 2012/02/18
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.datamanager;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;

/**
 * @author macchan
 */
public interface IPPVLoader {
	public void load(CFile zipfile, CDirectory dirTo);

	public void postProcess(CDirectory dirTo);
}
