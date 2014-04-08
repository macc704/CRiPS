/*
 * CZipDirectory.java
 * Created on 2012/03/01
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.view.frames;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;

/**
 * @author macchan
 * 
 */
public class CZDirectory extends CZElement {

	private List<CZElement> children = new ArrayList<CZElement>();

	/**
	 * @param entry
	 */
	public CZDirectory(ZipEntry entry) {
		super(entry);
	}

	public void add(CZElement child) {
		children.add(child);
	}

	/**
	 * @return the children
	 */
	public List<CZElement> getChildren() {
		return children;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ppv.view.frames.CZElement#isDirectory()
	 */
	@Override
	public boolean isDirectory() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ppv.view.frames.CZElement#isFile()
	 */
	@Override
	public boolean isFile() {
		return false;
	}

}
