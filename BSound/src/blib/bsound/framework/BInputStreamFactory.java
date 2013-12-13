/**
 * InputStreamFactory.java
 * Created on 2006/06/16
 * Copyright(c) Yoshiaki Matsuzawa at CreW Project
 */
package blib.bsound.framework;

import java.io.InputStream;

/**
 * Interface InputStreamFactory.
 * 
 * @author macchan
 */
public interface BInputStreamFactory {

	public InputStream createInputStream();
	
}
