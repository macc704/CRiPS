package obpro.turtle;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Iterator;

/*
 * TurtleCanvas.java
 * Created on 2011/12/17
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */

/**
 * Class TurtleCanvas.
 * 
 * @author macchan
 */
public class TurtleCanvas extends Canvas {

	private Object mappedLock = "mappedLock";

	private boolean mapped = false;

	private Image offScreen = null;

	public TurtleCanvas() {
		setBackground(Color.white);
		setForeground(Color.black);
	}

	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		offScreen = createImage(w, h);
		// offScreen = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
	}

	// for canvas

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {

		// public void paintComponent(Graphics g) {
		mappedNotify();

		if (offScreen != null) {

			Graphics2D offG2d = (Graphics2D) offScreen.getGraphics();
			Color defaultColor = offG2d.getColor();
			offG2d.setColor(getBackground());
			offG2d.fillRect(0, 0, getWidth(), getHeight());
			offG2d.setColor(defaultColor);

			Iterator i = Turtle.getAllInstances().iterator();
			while (i.hasNext()) {
				Turtle sprite = (Turtle) i.next();
				sprite.paint(offG2d);
			}
			offG2d.dispose();

			g.drawImage(offScreen, 0, 0, null);

		}
	}

	// 失敗（描画されない．でももうちょっと調べれば何とかなるかも） 2011.12.16
	// public Color getPixelColor(int x, int y) {
	// if (offScreen == null) {
	// throw new RuntimeException("offScreen is null");
	// }
	//
	// int rgb = offScreen.getRGB(x, y); // BufferedImage.TYPE_INT_ARGB
	// return new Color(rgb);
	// }

	// for swing panel

	// public void paintComponent(Graphics g) {
	// super.paintComponent(g);
	// mappedNotify();
	//
	// Iterator i = Turtle.getAllInstances().iterator();
	// while (i.hasNext()) {
	// Turtle sprite = (Turtle) i.next();
	// sprite.paint((Graphics2D) g);
	// }
	// }

	public void mappedWait() {
		synchronized (mappedLock) {
			if (!mapped) {
				try {
					mappedLock.wait();
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public void mappedNotify() {
		synchronized (mappedLock) {
			if (!mapped) {
				mapped = true;
				mappedLock.notify();
			}
		}
	}

}