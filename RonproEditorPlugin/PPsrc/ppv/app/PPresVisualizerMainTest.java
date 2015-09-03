/*
 * PPresVisualizerMain.java
 * Created on 2012/02/18
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileSystem;

/**
 * mac 1.7以上では，-Dsun.jnu.encoding=UTF-8オプションをつけて実行すること
 */
public class PPresVisualizerMainTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CDirectory dir = CFileSystem.getHomeDirectory().findOrCreateDirectory(
				".ppvdata");
		// System.out.println(System.getProperty("sun.jnu.encoding"));
		// Map<String, String> env = System.getenv();
		// for(String key :env.keySet()){
		// String value = env.get(key);
		// System.out.println(key+" = " +value);
		// }
		new PPresVisualizer().run(dir);
	}

}
