package blib.bsound.components;

import java.util.Timer;
import java.util.TimerTask;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import blib.bsound.framework.BInputStreamFactory;
import blib.bsound.framework.BSoundPlayer;


/**
 * Midiを再生する
 * 
 * @author Shinora
 * 
 */
public class BMidiSoundPlayer extends BSoundPlayer {

	private static final long TIMER_INTERVAL = 1000;

	// private BInputStreamFactory factroy;

	private Sequencer sequencer = null;
	private Sequence sequence = null;
	private Timer timer = null;

	/**
	 * Constructor
	 * 
	 * @param file
	 *            読み込むMidiファイル
	 */
	public BMidiSoundPlayer(String name, BInputStreamFactory factory) {
		super(name);

		try {
			// this.factroy = factory;
			sequence = MidiSystem.getSequence(factory.createInputStream());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void initializeSequencer() {
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequencer.setSequence(sequence);
			updateLoopState();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void terminateSequencer() {
		sequencer.close();
		sequencer = null;
	}

	private void startTimer() {
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new MidiStateTimerTask(), TIMER_INTERVAL,
					TIMER_INTERVAL);
		}
	}

	private void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public void play() {
		if (getState() != State.PLAYING) {
			try {
				if (sequencer == null) {
					initializeSequencer();
				}
				startTimer();
				sequencer.start();
				setState(State.PLAYING);

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void setLoop(boolean loop) {
		super.setLoop(loop);
		updateLoopState();
	}

	private void updateLoopState() {
		if (sequencer != null) {
			if (isLoop()) {
				sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
			} else {
				sequencer.setLoopCount(0);
			}
		}
	}

	public void pause() {
		if (getState() == State.PLAYING) {
			sequencer.stop();
			stopTimer();
			setState(State.PAUSING);
		}
	}

	public synchronized void stop() {
		if (getState() != State.STOPPED) {
			sequencer.stop();
			sequencer.setMicrosecondPosition(0);
			stopTimer();
			terminateSequencer();
			setState(State.STOPPED);
		}
	}

	public int getVolume() {
		return 100;
	}

	public void setVolume(int volume) {
		showError("midiファイルのボリューム調整はできません（未実装）");
	}

	public int getDefaultVolume() {
		return 100;
	}

	class MidiStateTimerTask extends TimerTask {
		public void run() {
			if (sequencer != null && !sequencer.isRunning()) {
				stop();
			}
		}
	}
}
