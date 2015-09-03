package test.blib.bsound;

import blib.bsound.BSound;

/**
 * BSoundTest.java
 * Created on 2006/05/27
 * Copyright(c) Yoshiaki Matsuzawa at CreW Project
 */

/**
 * Class BSoundTest.
 * 
 * @author macchan
 */
public class BSoundTest {

	public static void main(String[] args) {
		new BSoundTest().main();
	}

	void main() {
		testBSound("testsrc/obpro/bsound/samples/sample.wav");
		testBSound("testsrc/obpro/bsound/samples/sample.mid");
		testBSound("testsrc/obpro/bsound/samples/sample.mp3");

		testBSoundLoop("testsrc/obpro/bsound/samples/sample.wav");
		testBSoundLoop("testsrc/obpro/bsound/samples/sample.mid");
		testBSoundLoop("testsrc/obpro/bsound/samples/sample.mp3");

		testDirectPlay("obpro/bsound/samples/sample.wav");
		testDirectPlay("obpro/bsound/samples/sample.mid");
		testDirectPlay("obpro/bsound/samples/sample.mp3");
	}

	void testBSound(String filename) {
		BSound sound = new BSound(filename);

		for (int i = 0; i < 2; i++) {
			System.out.println(filename + "の再生をします");
			sound.play();
			sleep(2);

			System.out.println(filename + "の再生を止めます");
			sound.stop();
		}
	}

	void testBSoundLoop(String filename) {
		BSound sound = new BSound(filename);
		System.out.println(filename + "のloop再生をします");
		sound.loop();
		sleep(10);

		System.out.println(filename + "のloop再生を止めます");
		sound.stop();
	}

	void testDirectPlay(String filename) {
		System.out.println(filename + "の簡易再生をします");
		BSound.play(filename);
	}

	void sleep(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
