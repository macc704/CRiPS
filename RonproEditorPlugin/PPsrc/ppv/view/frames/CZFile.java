/*
 * CZFile.java
 * Created on 2012/03/01
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.view.frames;

import java.util.zip.ZipEntry;

/**
 * @author macchan
 */
public class CZFile extends CZElement {

	/**
	 * @param entry
	 */
	public CZFile(ZipEntry entry) {
		super(entry);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ppv.view.frames.CZElement#isDirectory()
	 */
	@Override
	public boolean isDirectory() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ppv.view.frames.CZElement#isFile()
	 */
	@Override
	public boolean isFile() {
		return true;
	}
}
