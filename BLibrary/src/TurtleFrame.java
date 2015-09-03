import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

/*
 * TurtleFrame.java
 * Created on 2011/12/17
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */

/**
 * Class TurtleFrame.
 * 
 * @author macchan
 * 
 */
public class TurtleFrame extends JFrame implements ActionListener, ItemListener {
	// class TurtleFrame extends Frame implements ActionListener, ItemListener {

	/*******************************************
	 * Constants
	 *******************************************/
	// private static final boolean CONSOLE_APPLET = true;

	private static final String[] speedMenuString = { "no kame", "very fast",
			"fast", "normal", "slow" };

	private static final int[] speedStep = { 100000, 1000, 20, 5, 2 };

	private static final boolean DEFAULT_IS_ALWAYS_ON_TOP = false;

	/*******************************************
	 * Variables
	 *******************************************/

	private TurtleCanvas canvas = null;

	private Thread thread = null;

	private Object restartLock = "Restart Lock";

	private CheckboxMenuItem[] speedMenu = null;

	// public JSplitPane split;

	/*******************************************
	 * Constructors.
	 *******************************************/

	// for AWT Frame
	// public TurtleFrame(int x, int y, int width, int height) {
	//
	// MenuBar menubar = new MenuBar();
	// setMenuBar(menubar);
	//
	// //file menu
	// Menu file = new Menu("File", true);
	// menubar.add(file);
	// MenuItem restart =
	// new MenuItem("Restart", new MenuShortcut(KeyEvent.VK_S));
	// restart.addActionListener(this);
	// restart.setActionCommand("restart");
	// file.add(restart);
	// MenuItem quit = new MenuItem("Quit", new MenuShortcut(KeyEvent.VK_Q));
	// quit.addActionListener(this);
	// quit.setActionCommand("quit");
	// file.add(quit);
	//
	// //speed menu
	// Menu speed = new Menu("Speed", true);
	// speedMenu = new CheckboxMenuItem[speedMenuString.length];
	// for (int i = 0; i < speedMenuString.length; i++) {
	// speedMenu[i] = new CheckboxMenuItem(speedMenuString[i], false);
	// speedMenu[i].addItemListener(this);
	// speed.add(speedMenu[i]);
	// }
	// menubar.add(speed);
	// speedMenu[2].setState(true); //最初はNormalなので
	//
	// // canvas
	// canvas = new TurtleCanvas();
	// add(BorderLayout.CENTER, canvas());
	//
	// // size
	// setSize(width, height + 30);
	// show();
	// canvas.mappedWait();
	//
	// }
	// for swing JFrame
	public TurtleFrame(int x, int y, int width, int height) {

		// Turtleクラスで設定する
		// setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		MenuBar menubar = new MenuBar();
		setMenuBar(menubar);

		// file menu
		Menu file = new Menu("File", true);
		menubar.add(file);
		MenuItem restart = new MenuItem("Restart", new MenuShortcut(
				KeyEvent.VK_S));
		restart.addActionListener(this);
		restart.setActionCommand("restart");
		file.add(restart);
		MenuItem quit = new MenuItem("Quit", new MenuShortcut(KeyEvent.VK_Q));
		quit.addActionListener(this);
		quit.setActionCommand("quit");
		file.add(quit);

		// speed menu
		Menu speed = new Menu("Speed", true);
		speedMenu = new CheckboxMenuItem[speedMenuString.length];
		for (int i = 0; i < speedMenuString.length; i++) {
			speedMenu[i] = new CheckboxMenuItem(speedMenuString[i], false);
			speedMenu[i].addItemListener(this);
			speed.add(speedMenu[i]);
		}
		menubar.add(speed);
		speedMenu[3].setState(true); // 最初はNormalなので
		setKameSpeed(speedMenuString[3]);

		// view menu
		Menu view = new Menu("View", true);
		CheckboxMenuItem alwaysOnTop = new CheckboxMenuItem("always on top",
				DEFAULT_IS_ALWAYS_ON_TOP);
		alwaysOnTop.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				boolean isAlwaysOnTop = (event.getStateChange() == ItemEvent.SELECTED);
				if (JavaVertionChecker.getMinorVersion() >= 5
						&& !Turtle.isAppletOrJNLP()) {
					setAlwaysOnTop(isAlwaysOnTop);
				}
			}
		});
		if (JavaVertionChecker.getMinorVersion() >= 5
				&& !Turtle.isAppletOrJNLP()) {
			setAlwaysOnTop(DEFAULT_IS_ALWAYS_ON_TOP);
			view.add(alwaysOnTop);// set to default
			menubar.add(view);
		}
		toFront();

		initializeView();

		// size
		setSize(width, height + 30);
	}

	public void initializeView() {
		getContentPane().removeAll();
		canvas = new TurtleCanvas();
		getContentPane().add(BorderLayout.CENTER, canvas());
	}

	public ConsoleTextArea initializeViewWithConsole() {
		getContentPane().removeAll();
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		// Main
		canvas = new TurtleCanvas();
		canvas.setPreferredSize(new Dimension(300, 300));
		canvas.setMinimumSize(new Dimension(1, 1));
		canvas.setMaximumSize(new Dimension(2000, 2000));

		// South
		ConsoleTextArea console = new ConsoleTextArea();
		console.setBackground(Color.BLACK);
		console.setForeground(Color.WHITE);
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(console);
		scroll.setPreferredSize(new Dimension(1, 1));
		scroll.setMinimumSize(new Dimension(1, 1));
		scroll.setMaximumSize(new Dimension(2000, 2000));

		split.setLeftComponent(canvas);
		split.setRightComponent(scroll);
		getContentPane().add(BorderLayout.CENTER, split);
		split.setDividerLocation(getHeight() - 50);

		return console;
	}

	/************************************************
	 * Listener
	 ************************************************/

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("quit")) {
			try {
				System.exit(0);
			} catch (Exception ex) {
				dispose();
			}
		} else if (cmd.equals("restart")) {
			restart();
		}
	}

	public void itemStateChanged(ItemEvent e) {
		String s = (String) e.getItem();
		setKameSpeed(s);
	}

	public void setKameSpeed(String s) {
		for (int i = 0; i < speedMenuString.length; i++) {
			if (s.equals(speedMenuString[i])) {
				if (TurtleTurtle.speed(speedStep[i])) {
					for (int j = 0; j < speedMenuString.length; j++) {
						speedMenu[j].setState(i == j);
					}
				}
				break;
			}
		}
	}

	/************************************************
	 * restart and thread management
	 ************************************************/

	void restart() {

		stopThread();
		thread = new Thread() {
			public void run() {
				try {
					Turtle.resetAllInstances();
					Turtle.defaultTurtle.delegator(new TurtleTurtle());
					if (Turtle.captureMode) {
						setKameSpeed("no kame");
					}
					Turtle.defaultTurtle.start();
					if (Turtle.captureMode) {
						System.out.println("finished");
						Thread.sleep(1000);
						System.exit(0);
					}
					synchronized (restartLock) {
						thread = null;
						restartLock.notify();
					}
				} catch (Exception ex) {
					String msg = ex.getMessage();
					if (!(msg != null && msg.equals("Interrupted By User"))) {
						ex.printStackTrace();
					}
					synchronized (restartLock) {
						thread = null;
						restartLock.notify();
					}
				}
			}
		};
		thread.start();

	}

	/************************************************
	 * Canvas
	 ************************************************/

	public TurtleCanvas canvas() {
		return this.canvas;
	}

	/************************************************
	 * 座標関連
	 ************************************************/

	public int x() {
		return getLocation().x;
	}

	public void x(int x) {
		location(x, y());
	}

	public int y() {
		return getLocation().y;
	}

	public void y(int y) {
		location(x(), y);
	}

	public void location(int x, int y) {
		if (Turtle.captureMode) {
			System.out.println("x:" + x);
			System.out.println("y:" + y);
		}
		setLocation(x, y);
	}

	public void warp(int x, int y) {
		location(x, y);
	}

	public int width() {
		return getSize().width;
	}

	public void width(int width) {
		size(width, height());
		validate();
	}

	public int height() {
		return getSize().height;
	}

	public void height(int height) {
		size(width(), height);
	}

	public void size(int width, int height) {
		if (Turtle.captureMode) {
			System.out.println("width:" + width);
			System.out.println("height:" + height);
		}
		setSize(width, height);
		validate();
	}

	/************************************************
	 * dispose関連
	 ************************************************/

	public void dispose() {
		stopThread();
		super.dispose();
	}

	private void stopThread() {
		synchronized (restartLock) {
			if (thread != null) {
				thread.interrupt();
				try {
					restartLock.wait();
				} catch (Exception ex) {
				}

			}
		}
	}

}
