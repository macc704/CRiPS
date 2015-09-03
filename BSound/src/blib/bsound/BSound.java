package blib.bsound;

import java.io.File;
import java.net.URL;

import blib.bsound.framework.BSoundPlayer;

/*
 * 初心者用　音出しクラス．　
 * オブプロ履修者のために． mp3, wav, midファイルを簡単に制御できます．
 * 
 * １．基本的な使い方 BSound sound = new BSound("sample.wav"); 
 * sound.loop();
 * 
 * ２．いちいちインスタンスを生成しない簡易メソッドを使う場合
 * BSound.play("sample.wav");
 * 
 * １．はＢＧＭ，２．は効果音に最適です． サンプルコードBSoundTestを参照ください．
 * 
 * このクラスで音を再生した場合デフォルトでストリーミング再生を行いますが， 反応速度が重要な場合は，メモリにロードしておく必要があります．
 * BSound.load("sample.wav"); 当然ながら，BGM等の長いファイルは，ロードするとメモリを圧迫します．気をつけてください．
 * 
 * なお，現在のバージョンでは，midiファイルの音量調節はできません．
 * 
 * version 2 2012.09.13 jdk1.7でresetできないエラーが出る問題，BufferedInputStreamをラップすることで解決（BWavSoundStream.java）
 * 
 * @author macchan
 */
public class BSound {

	/*********************************************
	 * クラスメソッド
	 *********************************************/

	/**
	 * 再生する（止められません）
	 */
	public static final void play(String path) {
		new BSound(path).play();
	}

	/**
	 * ボリュームを指定して再生する（止められません）
	 */
	public static final void play(String path, int volume) {
		BSound sound = new BSound(path);
		sound.setVolume(volume);
		sound.play();
	}

	/**
	 * メモリ上にサウンドデータを読み込みます(反応が早くなりますが，メモリ領域が必要です)
	 */
	public static final void load(String path) {
		BSoundSystem.load(path);
	}

	/**
	 * メモリ上にサウンドデータを読み込みます(反応が早くなりますが，メモリ領域が必要です)
	 */
	public static final void load(File file) {
		BSoundSystem.load(file);
	}

	/**
	 * メモリ上にサウンドデータを読み込みます(反応が早くなりますが，メモリ領域が必要です)
	 */
	public static final void load(URL url) {
		BSoundSystem.load(url);
	}

	/*********************************************
	 * BSound本体
	 *********************************************/

	private BSoundPlayer player = null;

	public BSound(String path) {
		player = BSoundSystem.createPlayer(path);
	}

	public BSound(File file) {
		player = BSoundSystem.createPlayer(file);
	}

	public BSound(URL url) {
		player = BSoundSystem.createPlayer(url);
	}

	/*
	 * ------------------------- 操作系 -------------------------
	 */

	/**
	 * 再生します
	 */
	public void play() {
		player.setLoop(false);
		player.play();
	}

	/**
	 * ループ再生します
	 */
	public void loop() {
		player.setLoop(true);
		player.play();
	}

	/**
	 * 停止します
	 */
	public void stop() {
		player.stop();
	}

	/**
	 * 再生中かどうか調べます
	 */
	public boolean isPlaying() {
		return player.getState() == BSoundPlayer.State.PLAYING;
	}

	/*
	 * ------------------------- ボリュームコントロール系 （ボリュームは0-100の100段階設定ができます）
	 * -------------------------
	 */

	/**
	 * 現在のボリュームを取得します．
	 */
	public int getVolume() {
		return player.getVolume();
	}

	/**
	 * ボリュームを設定します．
	 */
	public void setVolume(int volume) {
		player.setVolume(volume);
	}

	/**
	 * 初期ボリュームを取得します
	 */
	public int getDefaultVolume() {
		return player.getDefaultVolume();
	}

}
