/*
 * TurtleLaunchApplet.java
 * 
 * Created on 2003/06/23
 */

import java.applet.Applet;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class TurtleLaunchApplet.
 * 
 * @author macchan
 * @version $Id: TurtleLaunchApplet.java,v 1.1 2007/10/08 11:23:25 macchan Exp $
 */
public class TurtleLaunchApplet extends Applet implements ActionListener {

	public static boolean initialized = false;

	private String launchClassName = null;

	/**
	 * @see java.applet.Applet#init()
	 */
	public void init() {
		launchClassName = getParameter("turtle");

		Button button = new Button(launchClassName);
		button.addActionListener(this);

		add(button);

		initialized = true;
	}

	/**
	 * @see java.applet.Applet#start()
	 */
	public void start() {
	}

	/**
	 * @see java.applet.Applet#stop()
	 */
	public void stop() {
		Turtle.stopTurtle();
	}

	/**
	 * @see java.applet.Applet#destroy()
	 */
	public void destroy() {
		Turtle.stopTurtle();
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		Turtle.applet = TurtleLaunchApplet.this;
		Turtle.stopTurtle();

		Thread t = new Thread() {
			public void run() {
				try {
					Turtle t = (Turtle) Class.forName(launchClassName)
							.newInstance();
					Turtle.startTurtle(t);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		t.start();
	}

}
