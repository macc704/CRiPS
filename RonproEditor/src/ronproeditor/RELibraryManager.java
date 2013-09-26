/*
 * RELibraryManager.java
 * Created on 2007/09/21 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ronproeditor.helpers.FileSystemUtil;
import clib.common.system.CJavaSystem;

/**
 * RELibraryManager
 */
public class RELibraryManager {

	private File dir;

	public RELibraryManager(String dirname) {
		File dir = new File(dirname);
		if (!dir.exists()) {
			dir.mkdir();
		}
		this.dir = dir;
	}

	public String[] getLibsAsArray() {
		List<Library> libs = getLibs();
		String[] array = new String[libs.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = libs.get(i).getPath();
		}
		return array;
	}

	public String getLibString() {
		List<Library> libs = getLibs();
		String libString = ".";
		for (Library lib : libs) {
			libString += FileSystemUtil.PATH_SEPARATOR + lib.getPath();
		}
		return libString;
	}

	public List<Library> getLibs() {
		List<Library> libs = new ArrayList<Library>();
		for (File file : dir.listFiles()) {
			if (file.isFile() && file.getName().endsWith(".jar")
					&& !file.getName().startsWith(".")) {
				// .jarファイル(Macで自動的に作成される)を読まないように修正 2007/12/21
				libs.add(new Library(file));
			}
		}
		return libs;
	}
}

class Library {
	File file;

	Library(File file) {
		this.file = file;
	}

	String getPath() {
		if (CJavaSystem.getInstance().isWindows()) {
			// "はつけなくてよい．1.6.4で修正
			// return "\"" + file.getAbsolutePath() + "\"";
			return file.getAbsolutePath();
		} else {
			return file.getAbsolutePath();
		}
	}
}
