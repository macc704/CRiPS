/**
 * AbstractSoundPlayer.java
 * Created on 2006/05/27
 * Copyright(c) Yoshiaki Matsuzawa at CreW Project
 */
package blib.bsound.framework;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.SourceDataLine;

import blib.bsound.components.BFloatVolumeController;


/**
 * Class StreamingSoundPlayer.
 * 
 * @author macchan
 */
public class BStreamingSoundPlayer extends BSoundPlayer {

	private BSoundStream source = null;
	private SourceDataLine line = null;
	private SoundPlayThread thread = null;
	private BFloatVolumeController volume = null;

	private Object lock = new Object();

	/**
	 * コンストラクタ
	 */
	public BStreamingSoundPlayer(String name, BSoundStream stream)
			throws Exception {
		super(name);
		this.source = stream;
		initialize();
	}

	private void initialize() {
		try {
			createLine();
			createVolumeControl();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private void createLine() throws Exception {
		Line.Info info = new DataLine.Info(SourceDataLine.class,
				this.source.getFormat());
		this.line = (SourceDataLine) AudioSystem.getLine(info);
	}

	public void createVolumeControl() throws Exception {
		this.line.open();
		if (!line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
			throw new Exception("Volume Control をサポートしていません");
		}

		FloatControl control = (FloatControl) line
				.getControl(FloatControl.Type.MASTER_GAIN);
		volume = new BFloatVolumeController(control);
		this.line.close();
	}

	/**
	 * 再生する
	 */
	public void play() {
		synchronized (lock) {
			try {
				if (!(getState() == State.STOPPED || getState() == State.PAUSED)) {
					return;
				}

				this.line.open();
				this.line.start();
				setState(State.PLAYING);
				startThread();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 演奏を一時停止する
	 */
	public void pause() {
		synchronized (lock) {
			if (getState() != State.PLAYING) {
				return;
			}

			this.line.stop();
			setState(State.PAUSING);
			waitToStopThread();
			setState(State.PAUSED);
		}
	}

	/**
	 * 演奏を止める
	 */
	public void stop() {
		synchronized (lock) {
			if (!(getState() == State.PLAYING || getState() == State.PAUSED)) {
				return;
			}

			setState(State.STOPPING);
			waitToStopThread();
			this.line.stop();
			this.line.close();
			this.line.flush();
			this.source.reset();
			setState(State.STOPPED);
		}
	}

	/**
	 * ボリュームを取得する
	 */
	public int getVolume() {
		return this.volume.getVolume();
	}

	/**
	 * ボリュームを設定する
	 */
	public void setVolume(int volume) {
		this.volume.setVolume(volume);
	}

	/**
	 * 初期ボリュームを取得する
	 */
	public int getDefaultVolume() {
		return this.volume.getDefaultVolume();
	}

	class SoundPlayThread extends Thread {
		public void run() {
			try {
				doStreaming();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				notifyToStopThread();
			}

		}
	}

	private void startThread() {
		synchronized (lock) {
			if (thread == null) {
				thread = new SoundPlayThread();
				thread.setPriority(Thread.currentThread().getPriority() - 1);
				thread.setDaemon(true);
				thread.start();
			}
		}
	}

	private void waitToStopThread() {
		synchronized (lock) {
			try {
				if (thread != null && Thread.currentThread() != thread) {
					lock.wait();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void notifyToStopThread() {
		synchronized (lock) {
			try {
				thread = null;
				lock.notifyAll();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void doStreaming() throws Exception {
		while (getState() == State.PLAYING) {
			source.next();

			if (source.isEnd()) {
				if (isLoop()) {
					source.reset();
					continue;
				} else {
					line.drain(); // this method causes blocking so it makes
									// waiting for the music finished.
					stop();
					break;
				}
			}
			line.write(source.getReadBuffer(), 0, source.getReadByteCount());
		}
	}

}
