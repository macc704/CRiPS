package obpro.turtle;
/*
 * TurtleTurtle.java
 * Created on 2011/12/17
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

/**
 * Class TurtleTurtle.
 * 
 * @author macchan
 * 
 */
public class TurtleTurtle extends Turtle {

	/***************************************
	 * かめ定数
	 ***************************************/

	private static final double kameScale = 0.4;

	private Color kameColor = Color.green;

	/***************************************
	 * for animation
	 ***************************************/

	private static final int rotateWait = 20;

	private static final int moveWait = 20;

	private static int rotateStep = 5;

	private static int moveStep = 5;

	private static boolean withKame = true;

	/***************************************
	 * インスタンス変数
	 ***************************************/

	// turtle segments history
	private Vector history;

	// 2011.12.16 CellTurtleを作るにあたり，スーパークラスTurtleとデータ重複している問題を修正
	// private int angle; // turtle current angle (degree)
	// private double x, y; // turtle current position

	private double dx, dy; // dx = sin(angle), dy = -cos(angle)

	// private double x0;
	// private double y0; // center

	private boolean penDown; // pen status (up or down)

	private Color c; // pen color

	// turtle animation rubber line
	private int rx, ry;

	private boolean rubber = false;

	// turtle
	private int kameType = 0;

	/***************************************
	 * コンストラクタ
	 ***************************************/

	public TurtleTurtle() {
		init();
	}

	/***************************************
	 * 初期化
	 ***************************************/

	private void init() {
		size(30, 50);// kameScale = 0.4のとき
		// x = 100;
		// y = 100;
		x(100);
		y(100);
		// angle = 0;
		angle(0);
		dx = 0;
		dy = -1;
		penDown = true;
		history = new Vector(10);
		c = Color.black;
	}

	/*********************************************
	 * スピードの調整
	 *********************************************/

	public static boolean speed(int step) {
		if (step <= 0) {
			return false;
		}
		rotateStep = step;
		moveStep = step;
		withKame = (step < 10000);
		return true;
	}

	/*********************************************
	 * カメの描画
	 *********************************************/

	// set turtle angle
	void kameAngle(double a) {
		dx = Math.sin(theta(a));
		dy = -Math.cos(theta(a));
		dx = fixError(dx);
		dy = fixError(dy);
		kameShow(rotateWait);
	}

	// sin90やcos90の誤差を丸める
	private double fixError(double x) {
		if (-0.0000000000000001 < x && x <= 0.0000000000000001) {
			return 0d;
		}
		return x;
	}

	// turtle animation update
	void kameShow(int wait) {
		kameType++;
		update();
		if (withKame) {
			sleep(wait / 1000d); // macchan
		}
	}

	private void paintLocus(Graphics g) {
		for (int i = 0; i < history.size(); i++) {
			Line line = (Line) history.elementAt(i);
			g.setColor(line.c);
			g.drawLine(line.bx, line.by, line.ex, line.ey);
		}
		if (rubber) {
			g.setColor(c);
			// g.drawLine(rx, ry, (int) x, (int) y);
			g.drawLine(rx, ry, getX(), getY());
		}
	}

	// redraw method
	public void paint(Graphics g) {
		paintLocus((Graphics) g);
		if (withKame) {
			switch ((kameType / 2) % 4) {
			case 0:
			case 2:
				kameDraw(g, kame);
				break;
			case 1:
				kameDraw(g, kameR);
				break;
			case 3:
				kameDraw(g, kameL);
				break;
			}
		}
	}

	public BufferedImage image() {
		int width = getWidth();
		int height = getHeight();
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = image.getGraphics();
		g.translate(-getX() + (width / 2), -getY() + (height / 2));
		kameDraw(g, kame);
		g.dispose();
		return image;
	}

	// draw animation turtle
	void kameDraw(Graphics g, short data[][]) {
		// int ix = (int) x, iy = (int) y;
		int ix = getX(), iy = getY();
		g.setColor(kameColor);
		for (int i = 0; i < data.length; i++) {
			int px = 0, py = 0;
			for (int j = 0; j < data[i].length; j += 2) {
				int kx = data[i][j], ky = data[i][j + 1];
				int nx = (int) ((kx * (-dy) + ky * (-dx)) * kameScale);
				int ny = (int) ((kx * dx + ky * (-dy)) * kameScale);
				if (j > 0)
					g.drawLine(ix + px, iy + py, ix + nx, iy + ny);
				px = nx;
				py = ny;
			}
		}
	}

	/*********************************************
	 * Start
	 *********************************************/

	// default start method
	@Override
	public void start() {
		System.out.println("Turtle start method called");
	}

	/*********************************************
	 * 基本コマンド
	 *********************************************/

	// forward n step
	public void fd(int n) {
		// double xx = x;
		// double yy = y;
		double xx = x();
		double yy = y();
		if (penDown) {
			// rx = (int) x;
			// ry = (int) y;
			rx = getX();
			ry = getY();
			rubber = true;
		}
		for (int i = moveStep; i < n; i += moveStep) {
			// x = xx + dx * i;
			// y = yy + dy * i;
			x(xx + dx * i);
			y(yy + dy * i);
			kameShow(moveWait);
		}
		// x = xx + dx * n;
		// y = yy + dy * n;
		// 2011.12.16 ずれるので修正
		// x(xx + dx * n);
		// y(yy + dy * n);
		x(xx + dx * n);
		y(yy + dy * n);
		if (penDown) {
			// Line line = new Line((int) xx, (int) yy, (int) x, (int) y, c);
			Line line = new Line((int) xx, (int) yy, getX(), getY(), c);
			history.addElement(line);
			rubber = false;
		}
		kameShow(moveWait);
	}

	// backward n step
	public void bk(int n) {
		// double xx = x;
		// double yy = y;
		double xx = x();
		double yy = y();
		if (penDown) {
			rx = getX();
			ry = getY();
			rubber = true;
		}
		for (int i = moveStep; i < n; i += moveStep) {
			x(xx - dx * i);
			y(yy - dy * i);
			kameShow(moveWait);
		}
		x(xx - dx * n);
		y(yy - dy * n);
		if (penDown) {
			Line line = new Line((int) xx, (int) yy, getX(), getY(), c);
			history.addElement(line);
			rubber = false;
		}
		kameShow(moveWait);
	}

	@Override
	public void warp(double x, double y) {
		super.warp(x, y);
		kameShow(moveWait);
	}

	// right turn n degree
	public void rt(int n) {
		for (int i = rotateStep; i < n; i += rotateStep) {
			// kameAngle(angle + i);
			kameAngle((int) angle() + i);
		}
		// angle = (angle + n) % 360;
		angle((angle() + n) % 360);
		kameAngle(angle());
	}

	// left turn n degree
	public void lt(int n) {
		for (int i = rotateStep; i < n; i += rotateStep) {
			// kameAngle(angle - i);
			kameAngle((int) angle() - i);
		}
		// angle = (angle - n) % 360;
		angle((angle() - n) % 360);
		kameAngle(angle());
	}

	// pen up
	public void up() {
		penDown = false;
	}

	// pen down
	public void down() {
		penDown = true;
	}

	// change color
	public void color(Color nc) {
		c = nc;
	}

	/*********************************************
	 * Turtle とのつなぎ
	 *********************************************/

	public void rt(double angle) {
		rt((int) angle);
	}

	public void lt(double angle) {
		lt((int) angle);
	}

	public void fd(double angle) {
		fd((int) angle);
	}

	public void bk(double angle) {
		bk((int) angle);
	}

	public void paint(Graphics2D g) {
		if (looks() != null) {
			paintLocus((Graphics) g);
			super.paint(g);
			return;
		}
		if (isShow()) {
			paint((Graphics) g);
		}
	}

	/***************************************
	 * Class Turtle.Line
	 ***************************************/

	class Line {
		public int bx;

		public int by;

		public int ex;

		public int ey;

		public Color c;

		public Line(int bx, int by, int ex, int ey, Color c) {
			this.bx = bx;
			this.by = by;
			this.ex = ex;
			this.ey = ey;
			this.c = c;
		}
	}

	/***************************************
	 * カメ
	 ***************************************/

	/**
	 * @param kameColor
	 *            the kameColor to set
	 */
	protected void setKameColor(Color kameColor) {
		this.kameColor = kameColor;
	}

	final static short kame[][] = {
			{ -12, -6, -12, 6, 0, 18, 12, 6, 12, -6, 0, -18, -12, -6 },
			{ -18, -12, -12, -6 },
			{ -6, -24, 0, -18, 6, -24 },
			{ 12, -6, 18, -12 },
			{ 12, 6, 18, 12 },
			{ -6, 24, 0, 18, 6, 24 },
			{ -18, 12, -12, 6 },
			{ -18, 12, -18, -12, -6, -24, 6, -24, 18, -12, 18, 12, 6, 24, -6,
					24, -18, 12 }, { -15, -15, -18, -24, -9, -21 },
			{ 9, -21, 18, -24, 15, -15 }, { 15, 15, 18, 24, 9, 21 },
			{ -9, 21, -18, 24, -15, 15 }, { -3, 24, 0, 30, 3, 24 },
			{ -6, -24, -12, -36, -6, -48, 6, -48, 12, -36, 6, -24 } };

	final static short kameR[][] = {
			{ -12, -6, -12, 6, 0, 18, 12, 6, 12, -6, 0, -18, -12, -6 },
			{ -18, -12, -12, -6 },
			{ -6, -24, 0, -18, 6, -24 },
			{ 12, -6, 18, -12 },
			{ 12, 6, 18, 12 },
			{ -6, 24, 0, 18, 6, 24 },
			{ -18, 12, -12, 6 },
			{ -18, 12, -18, -12, -6, -24, 6, -24, 18, -12, 18, 12, 6, 24, -6,
					24, -18, 12 }, { -15, -15, -24, -18, -9, -21 },
			{ -9, 21, -24, 18, -15, 15 }, { -3, 24, -3, 30, 3, 24 },
			{ -6, -24, -6, -36, 0, -48, 12, -48, 18, -36, 6, -24 },
			{ 9, -21, 18, -30, 15, -15 }, { 15, 15, 18, 30, 9, 21 } };

	final static short kameL[][] = {
			{ -12, -6, -12, 6, 0, 18, 12, 6, 12, -6, 0, -18, -12, -6 },
			{ -18, -12, -12, -6 },
			{ -6, -24, 0, -18, 6, -24 },
			{ 12, -6, 18, -12 },
			{ 12, 6, 18, 12 },
			{ -6, 24, 0, 18, 6, 24 },
			{ -18, 12, -12, 6 },
			{ -18, 12, -18, -12, -6, -24, 6, -24, 18, -12, 18, 12, 6, 24, -6,
					24, -18, 12 }, { -15, -15, -18, -30, -9, -21 },
			{ -9, 21, -18, 30, -15, 15 }, { -3, 24, 3, 30, 3, 24 },
			{ -6, -24, -18, -36, -12, -48, 0, -48, 6, -36, 6, -24 },
			{ 9, -21, 24, -18, 15, -15 }, { 15, 15, 24, 18, 9, 21 } };

}
