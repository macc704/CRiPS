/*
 * CZElement.java
 * Created on 2012/03/01
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.view.frames;

import java.util.zip.ZipEntry;

/**
 * @author macchan
 * 
 */
public abstract class CZElement {

	private ZipEntry entry;

	/**
	 * 
	 */
	public CZElement(ZipEntry entry) {
		this.entry = entry;
	}

	public String getName() {
		return entry.getName();
	}

	public abstract boolean isDirectory();

	public abstract boolean isFile();
}
