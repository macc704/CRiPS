/**
 * Hoge.java
 * Created on 2006/05/27
 * Copyright(c) Yoshiaki Matsuzawa at CreW Project
 */
package test.blib.bsound;

import java.awt.FlowLayout;

import javax.swing.JFrame;

import blib.bsound.BSoundSystem;
import blib.bsound.framework.BSoundPlayer;


/**
 * Class Hoge.
 * 
 * @author macchan
 */
public class SoundPlayerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		JFrame frame = new JFrame();
		frame.setLocation(100, 100);
		frame.setSize(600, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout());

		BSoundSystem.load("obpro/bsound/samples/sample.wav");
		BSoundSystem.load("testsrc/obpro/bsound/samples/sample.mp3");
		BSoundSystem.load("testsrc/obpro/bsound/samples/sample.mid");
		BSoundSystem.load("testsrc/obpro/bsound/samples/sample.hoge");

		BSoundPlayer player;
		SoundPlayerPanel panel;

		player = BSoundSystem.createPlayer("obpro/bsound/samples/sample.wav");
		panel = new SoundPlayerPanel(player);
		frame.getContentPane().add(panel);

		player = BSoundSystem
				.createPlayer("testsrc/obpro/bsound/samples/sample.wav");
		panel = new SoundPlayerPanel(player);
		frame.getContentPane().add(panel);

		player = BSoundSystem.createPlayer("obpro/bsound/samples/sample.mp3");
		panel = new SoundPlayerPanel(player);
		frame.getContentPane().add(panel);

		player = BSoundSystem
				.createPlayer("testsrc/obpro/bsound/samples/sample.mp3");
		panel = new SoundPlayerPanel(player);
		frame.getContentPane().add(panel);

		player = BSoundSystem.createPlayer("obpro/bsound/samples/sample.mid");
		panel = new SoundPlayerPanel(player);
		frame.getContentPane().add(panel);

		player = BSoundSystem
				.createPlayer("testsrc/obpro/bsound/samples/sample.mid");
		panel = new SoundPlayerPanel(player);
		frame.getContentPane().add(panel);

		frame.setVisible(true);
	}

}
