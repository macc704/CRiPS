/**
 * BWavSoundStream.java
 * Created on 2006/06/13
 * Copyright(c) Yoshiaki Matsuzawa at CreW Project
 */
package blib.bsound.components;

import java.io.BufferedInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import blib.bsound.framework.BInputStreamFactory;
import blib.bsound.framework.BSoundStream;

/**
 * Class BWavSoundStream.
 * 
 * @author macchan
 */
public class BWavSoundStream implements BSoundStream {

	private static final int STREAMING_BUF_SIZE = 1024;

	private BInputStreamFactory factory;
	private AudioInputStream audioStream = null;
	private int nBytesRead = 0;
	private byte[] streamingBuf = new byte[STREAMING_BUF_SIZE];

	public BWavSoundStream(BInputStreamFactory factory) throws Exception {
		this.factory = factory;
		reset();
	}

	public AudioFormat getFormat() {
		return audioStream.getFormat();
	}

	public synchronized void reset() {
		try {
			audioStream = AudioSystem
					.getAudioInputStream(new BufferedInputStream(factory
							.createInputStream()));// 2012.09.13 ver2 buffered
													// input streamでwrap
			nBytesRead = 0;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public synchronized void next() {
		try {
			nBytesRead = audioStream.read(streamingBuf, 0, streamingBuf.length);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public boolean isEnd() {
		return nBytesRead == -1;
	}

	public int getReadByteCount() {
		return nBytesRead;
	}

	public byte[] getReadBuffer() {
		return streamingBuf;
	}

}
