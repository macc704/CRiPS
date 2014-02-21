/**
 * SoundStream.java
 * Created on 2006/06/13
 * Copyright(c) Yoshiaki Matsuzawa at CreW Project
 */
package blib.bsound.framework;

import javax.sound.sampled.AudioFormat;

/**
 * Interface BSoundStream.
 * 
 * @author macchan
 */
public interface BSoundStream {

	public AudioFormat getFormat();

	public void reset();
	public void next();
	public boolean isEnd();

	public int getReadByteCount();
	public byte[] getReadBuffer();

}

