/**
 * SoundPlayer.java
 * Created on 2006/05/26
 * Copyright(c) Yoshiaki Matsuzawa at CreW Project
 */
package blib.bsound.framework;

/**
 * Class SoundPlayer.
 * 
 * @author macchan
 */
public abstract class BSoundPlayer {

	public enum State {
		PLAYING, PAUSING, PAUSED, STOPPING, STOPPED
	}

	private String name = "";
	private State state = State.STOPPED;
	private boolean loop = false;

	/**
	 * 初期化されない場合は，例外を出すこと
	 */
	public BSoundPlayer(String name) {
		this.name = name;
	}

	/**
	 * 名前を取得する
	 */
	public String getName() {
		return name;
	}

	/**
	 * 再生する
	 */
	public abstract void play();

	/**
	 * 演奏を一時停止する
	 */
	public abstract void pause();

	/**
	 * 演奏を止める
	 */
	public abstract void stop();

	/**
	 * ループするかどうか調べる
	 */
	public boolean isLoop() {
		return loop;
	}

	/**
	 * ループするかどうか設定する
	 */
	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	/**
	 * 状態を調べる
	 */
	public State getState() {
		return state;
	}

	/**
	 * 状態を設定する
	 */
	public void setState(State state) {
		if (this.state != state) {
			this.state = state;
		}
	}

	/**
	 * ボリュームを取得する
	 */
	public abstract int getVolume();

	/**
	 * ボリュームを設定する
	 */
	public abstract void setVolume(int volume);

	/**
	 * 初期ボリュームを設定する
	 */
	public abstract int getDefaultVolume();

	/**
	 * エラーを出力する
	 */
	public void showError(String message) {
		System.err.println(message + " :" + getName());
	}
}
