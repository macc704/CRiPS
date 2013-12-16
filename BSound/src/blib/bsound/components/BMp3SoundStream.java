package blib.bsound.components;

import java.io.BufferedInputStream;

import javax.sound.sampled.AudioFormat;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import blib.bsound.framework.BInputStreamFactory;
import blib.bsound.framework.BSoundStream;

/**
 * MP3をストリーミング再生する
 * 
 * @author hashiyaman
 * @version $Id: MP3StreamingSoundSource.java,v 1.8 2006/06/11 09:52:56
 *          hashiyaman Exp $
 */
public class BMp3SoundStream implements BSoundStream {

	// 定数
	private static final int STREAMING_BUF_SIZE = 1024;

	// 変数
	private BInputStreamFactory factory;

	private Decoder mp3Decoder;
	private Header currentHeader;
	private Bitstream mp3Stream;

	private int nBytesRead = 0;
	private byte[] streamingBuf;
	private byte[] byteBuf = new byte[STREAMING_BUF_SIZE * 4];

	/**
	 * コンストラクタ
	 */
	public BMp3SoundStream(BInputStreamFactory factory) {
		try {
			this.factory = factory;
			initializeStream();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private synchronized void initializeStream() {
		try {
			mp3Decoder = new Decoder();

			if (mp3Stream != null) {
				mp3Stream.close();
			}
			mp3Stream = new Bitstream(new BufferedInputStream(
					factory.createInputStream()));// 2012.09.13 ver2 buffered
			// input streamでwrap

			currentHeader = mp3Stream.readFrame();
			mp3Decoder.initialize(currentHeader);

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public AudioFormat getFormat() {
		return createAudioFormat(this.mp3Decoder);
	}

	public void reset() {
		try {
			initializeStream();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public synchronized void next() {
		try {
			SampleBuffer decodedFrameData = (SampleBuffer) mp3Decoder
					.decodeFrame(currentHeader, mp3Stream);
			streamingBuf = toByteArray(decodedFrameData.getBuffer(), 0,
					decodedFrameData.getBufferLength());
			nBytesRead = decodedFrameData.getBufferLength() * 2;
			mp3Stream.closeFrame();
			currentHeader = mp3Stream.readFrame();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public boolean isEnd() {
		return currentHeader == null;
	}

	public int getReadByteCount() {
		return nBytesRead;
	}

	public byte[] getReadBuffer() {
		return streamingBuf;
	}

	private AudioFormat createAudioFormat(Decoder mp3Decoder) {
		AudioFormat format = new AudioFormat(mp3Decoder.getOutputFrequency(),
				16, mp3Decoder.getOutputChannels(), true, false);
		return format;
	}

	private byte[] toByteArray(short[] samples, int offset, int length) {
		byte[] byteArray = getByteArray(length * 2);
		int index = 0;
		short sample;
		while (length-- > 0) {
			sample = samples[offset++];
			byteArray[index++] = (byte) sample;
			byteArray[index++] = (byte) (sample >>> 8);
		}
		return byteArray;
	}

	private byte[] getByteArray(int length) {
		if (byteBuf.length < length) {
			byteBuf = new byte[length + STREAMING_BUF_SIZE];
		}
		return byteBuf;
	}
}
