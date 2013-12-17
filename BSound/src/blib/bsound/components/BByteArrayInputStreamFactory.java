/**
 * ByteArrayInputStreamFactory.java
 * Created on 2006/06/16
 * Copyright(c) Yoshiaki Matsuzawa at CreW Project
 */
package blib.bsound.components;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import blib.bsound.framework.BInputStreamFactory;



/**
 * Class ByteArrayInputStreamFactory.
 * @author macchan
 */
public class BByteArrayInputStreamFactory implements BInputStreamFactory {

	private byte[] source;
	
	/**
	 * Constructor
	 */
	public BByteArrayInputStreamFactory(byte[] source) {
		this.source = source;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see framework.InputStreamFactory#newInstance()
	 */
	public InputStream createInputStream() {
		return new ByteArrayInputStream(source);
	}

}
