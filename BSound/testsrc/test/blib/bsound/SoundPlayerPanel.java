/**
 * SoundPlayerPanel.java
 * Created on 2006/05/27
 * Copyright(c) Yoshiaki Matsuzawa at CreW Project
 */
package test.blib.bsound;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import blib.bsound.framework.BSoundPlayer;


/**
 * Class SoundPlayerPanel.
 * 
 * @author macchan
 */
public class SoundPlayerPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private BSoundPlayer player;

	private JButton play = new JButton("Play");
	private JButton stop = new JButton("Stop");
	private JButton pause = new JButton("Pause");
	private JCheckBox loop = new JCheckBox("Loop");
	private JLabel state = new JLabel("STATE");
	private JSlider volume = new JSlider(0, 100);
	private JLabel filename = new JLabel("");

	/**
	 * コンストラクタ
	 */
	public SoundPlayerPanel(BSoundPlayer player) {
		super();
		this.player = player;
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.BLACK));

		JPanel namePanel = new JPanel();
		add(namePanel, BorderLayout.NORTH);
		JPanel controlPanel = new JPanel();
		add(controlPanel, BorderLayout.CENTER);

		filename.setText(player.getName());
		namePanel.add(filename);

		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				player.play();
			}
		});
		controlPanel.add(play);

		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				player.stop();
			}
		});
		controlPanel.add(stop);

		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				player.pause();
			}
		});
		controlPanel.add(pause);

		loop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				player.setLoop(loop.isSelected());
			}
		});
		controlPanel.add(loop);

		volume.setValue(player.getVolume());
		volume.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				player.setVolume(volume.getValue());
			}
		});
		controlPanel.add(volume);

		controlPanel.add(state);
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				showState();
			}
		}, 1000, 100);

	}

	public void showState() {
		state.setText(player.getState().toString());
	}

}
