/**
 * FileInputStreamFactory.java
 * Created on 2006/06/16
 * Copyright(c) Yoshiaki Matsuzawa at CreW Project
 */
package blib.bsound.components;

import java.io.InputStream;
import java.net.URL;

import blib.bsound.framework.BInputStreamFactory;


/**
 * Class FileInputStreamFactory.
 * 
 * @author macchan
 */
public class BFileInputStreamFactory implements BInputStreamFactory {

	private URL file;

	// private File file;

	/**
	 * Constructor
	 */
	public BFileInputStreamFactory(URL file) {
		this.file = file;
		// try {
		// this.file = new File(file.toURI());
		// } catch (Exception ex) {
		// throw new RuntimeException(ex);
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see framework.InputStreamFactory#newInstance()
	 */
	public InputStream createInputStream() {
		try {
			return file.openStream();
			// return new FileInputStream(file);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
