/*
 * PPRonproPPVLoader.java
 * Created on 2012/02/18
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.datamanager;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CPath;
import clib.common.io.CIOUtils;

/**
 * @author macchan
 * 
 */
public class PPRonproPPVLoader implements IPPVLoader {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ppv.app.loader.IPPVLoader#load(clib.common.filesystem.CFile,
	 * clib.common.filesystem.CDirectory)
	 */
	@Override
	public void load(CFile zipfile, CDirectory dirTo) {
		String name = zipfile.getName().getName();
		if (dirTo.findChild(new CPath(name)) != null) {
			return;
		}

		// unzip
		CDirectory newDir = dirTo.findOrCreateDirectory(name);
		CDirectory pjDir = newDir
				.findOrCreateDirectory(PPDataManager.BASE_DIR_IN_PROJECT_NAME);
		CIOUtils.unZip(zipfile, pjDir);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ppv.app.datamanager.IPPVLoader#postProcess(clib.common.filesystem.CDirectory
	 * )
	 */
	@Override
	public void postProcess(CDirectory dirTo) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Ronpro";
	}

}
