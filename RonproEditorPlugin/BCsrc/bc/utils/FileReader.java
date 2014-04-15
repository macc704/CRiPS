/*
 * FileReader.java
 * Created on 2011/09/28
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package bc.utils;

import java.io.File;

import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.system.CEncoding;

/**
 * @author macchan
 * 
 */
public class FileReader {

	public static String readFile(File file, String enc) {
		try {
			CFile cfile = CFileSystem.findFile(file.getAbsolutePath());
			cfile.setEncodingIn(CEncoding.get(enc));
			String text = cfile.loadTextAsIs();
			return text;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
