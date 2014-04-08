/*
 * PPEclipsePPVLoader.java
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
public class PPEclipsePPVLoader implements IPPVLoader {

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
		CDirectory newDirectory = dirTo.findOrCreateDirectory(name);
		CIOUtils.unZip(zipfile, newDirectory);

		// rename
		if (newDirectory.getDirectoryChildren().size() <= 0) {
			return;
		}
		CDirectory dir = newDirectory.getDirectoryChildren().get(0);
		dir.renameTo(PPDataManager.BASE_DIR_IN_PROJECT_NAME);

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
		PPProjectSet set = new PPProjectSet(dirTo);
		set.setSrcDirName("src");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Eclipse";
	}

}
