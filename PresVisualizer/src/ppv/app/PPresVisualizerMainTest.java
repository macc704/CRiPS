/*
 * PPresVisualizerMain.java
 * Created on 2012/02/18
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileSystem;

/**
 * 
 */
public class PPresVisualizerMainTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CDirectory dir = CFileSystem.getHomeDirectory().findOrCreateDirectory(
				".ppvdata");
		new PPresVisualizer().run(dir);
	}

}
