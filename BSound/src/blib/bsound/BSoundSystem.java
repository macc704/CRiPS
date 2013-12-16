package blib.bsound;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import common.resource.CResourceFinder;

import blib.bsound.components.BMidiSoundPlayer;
import blib.bsound.components.BMp3SoundStream;
import blib.bsound.components.BNullSoundPlayer;
import blib.bsound.components.BWavSoundStream;
import blib.bsound.framework.BInputStreamFactory;
import blib.bsound.framework.BSoundPlayer;
import blib.bsound.framework.BStreamingSoundPlayer;


/**
 * Class BSoundSystem.
 * 
 * @author macchan
 */
public class BSoundSystem {

	private static BSoundManager sounds = new BSoundManager();

	/**
	 * Factory Method
	 */
	public static final void load(String path) {
		URL url = CResourceFinder.getResource(path);
		if (url == null) {
			System.err.println(path + "は読み込めませんでした．");
			return;
		}
		load(url);
	}

	/**
	 * Factory Method
	 */
	public static final void load(File file) {
		try {
			load(file.toURI().toURL());
		} catch (Exception ex) {
			System.err.println(file.getAbsolutePath() + "は読み込めませんでした．");
		}
	}

	/**
	 * Factory Method
	 */
	public static final void load(URL file) {
		try {
			sounds.loadSound(file);
		} catch (Exception ex) {
			System.err.println(file.getFile() + "は読み込めませんでした．");
		}
	}

	/**
	 * Factory Method
	 */
	public static final BSoundPlayer createPlayer(String path) {
		URL url = CResourceFinder.getResource(path);
		if (url == null) {
			BNullSoundPlayer player = new BNullSoundPlayer(path);
			player.showError("ファイルが見つかりません");
			return player;
		}
		return createPlayer(url);
	}

	/**
	 * Factory Method
	 */
	public static final BSoundPlayer createPlayer(File file) {
		try {
			return createPlayer(file.toURI().toURL());
		} catch (Exception ex) {
			BNullSoundPlayer player = new BNullSoundPlayer(file.toString());
			player.showError("ファイルが読み込めません");
			return player;
		}
	}

	/**
	 * Factory Method
	 */
	public static final BSoundPlayer createPlayer(URL file) {
		if (file == null) {
			BNullSoundPlayer player = new BNullSoundPlayer("null");
			player.showError("ファイルがnullです");
			return player;
		}

		String filename = file.toString();

		try {
			// test
			InputStream stream = file.openStream();
			stream.close();

			BInputStreamFactory factory = sounds.getInputStreamFactory(file);

			if (filename.endsWith(".wav")) {
				return createWavSoundPlayer(factory, filename);
			} else if (filename.endsWith(".mp3")) {
				return createMP3SoundPlayer(factory, filename);
			} else if (filename.endsWith(".mid")) {
				return createMidiSoundPlayer(factory, filename);
			}
		} catch (FileNotFoundException ex) {
			BNullSoundPlayer player = new BNullSoundPlayer(filename);
			player.showError("ファイルが見つかりません");
			return player;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		BNullSoundPlayer player = new BNullSoundPlayer(filename);
		player.showError("SoundPlayerを初期化できません");
		return player;
	}

	private static final BSoundPlayer createWavSoundPlayer(
			BInputStreamFactory factory, String name) throws Exception {
		BWavSoundStream stream = new BWavSoundStream(factory);
		return new BStreamingSoundPlayer(name, stream);
	}

	private static final BSoundPlayer createMidiSoundPlayer(
			BInputStreamFactory factory, String name) throws Exception {
		return new BMidiSoundPlayer(name, factory);
	}

	private static final BSoundPlayer createMP3SoundPlayer(
			BInputStreamFactory factory, String name) throws Exception {
		BMp3SoundStream stream = new BMp3SoundStream(factory);
		return new BStreamingSoundPlayer(name, stream);
	}
}
