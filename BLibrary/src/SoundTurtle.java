/*
 * SoundTurtle.java
 * Created on 2007/12/21 by macchan
 * Copyright(c) 2007 CreW Project
 */

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import blib.bsound.BSound;

import common.resource.CResourceFinder;

/**
 * SoundTurtle
 */
public class SoundTurtle extends CardTurtle {

	private static List<SoundTurtle> instances = new ArrayList<SoundTurtle>();

	public static void clearSound() {
		for (SoundTurtle instance : instances) {
			instance.stop();
		}
		instances.clear();
	}

	// private String path;
	private URL url;
	private BSound sound;

	public SoundTurtle(String path) {
		super();
		location(-100, -100);
		// this.path = path;
		setFile(path);
		instances.add(this);
	}

	public SoundTurtle() {
		this("");
	}

	public void file(String path) {
		setFile(path);
	}

	public void setFile(String path) {
		text("♪" + path + "♪");
		this.url = CResourceFinder.getResource(path, getCaller());
		this.sound = new BSound(url);
	}

	private Class<?> getCaller() {
		try {
			StackTraceElement[] elements = new Exception().getStackTrace();
			if (elements.length < 3) {
				throw new RuntimeException();
			}
			return Class.forName(elements[2].getClassName());
		} catch (Exception ex) {
			return Class.class;
		}
	}

	public void play() {
		sound.play();
	}

	public void stop() {
		sound.stop();
	}

	public void loop() {
		sound.loop();
	}

	public boolean isPlaying() {
		return sound.isPlaying();
	}

	public int getVolume() {
		return sound.getVolume();
	}

	public void setVolume(int volume) {
		sound.setVolume(volume);
	}

	public void loadOnMemory() {
		BSound.load(url);
	}

}
