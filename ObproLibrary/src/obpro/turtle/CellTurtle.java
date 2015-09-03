package obpro.turtle;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * CellTurtle.java
 * Created on 2011/12/16
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */

/**
 * @author macchan
 */
public class CellTurtle extends TurtleTurtle {

	// 携帯電話
	public static final int WEST_TOP = 1;
	public static final int TOP = 2;
	public static final int EAST_TOP = 3;
	public static final int WEST = 4;
	public static final int HERE = 5;
	public static final int EAST = 6;
	public static final int WEST_BOTTOM = 7;
	public static final int BOTTOM = 8;
	public static final int EAST_BOTTOM = 9;

	private List<Point> previousPoints;
	private List<Point> currentPoints = new ArrayList<Point>();

	private int size = 10;
	private int cashOfDistanceThreshold;

	public CellTurtle() {
		this(10);
	}

	public CellTurtle(int size) {
		this.size = size;
		int half = (size / 2);
		this.cashOfDistanceThreshold = half - 1 > 1 ? half - 1 : 1;
		setKameColor(Color.RED);
		up();
		warp(size, size);
		rt(90);
	}

	@Override
	public void fd(int x) {
		super.fd(size * x);
	}

	public void fd() {
		fd(1);
	}

	@Override
	public void bk(int x) {
		super.bk(size * x);
	}

	public void bk() {
		bk(1);
	}

	@Override
	public void right(double x) {
		rt(90);
		fd(x);
		lt(90);
	}

	public void right() {
		right(1);
	}

	@Override
	public void left(double x) {
		lt(90);
		fd(x);
		rt(90);
	}

	public void left() {
		left(1);
	}

	public void beginNextTurn() {
		previousPoints = new ArrayList<Point>(currentPoints);
	}

	public void endTurn() {
		previousPoints = null;
		update();
	}

	private boolean inTurn() {
		return previousPoints != null;
	}

	public void mark() {
		mark(HERE);
	}

	public void mark(int position) {
		mark(getMarkingCheckPoint(position));
	}

	private void mark(Point p) {
		if (isMarked(p, currentPoints)) {
			return;
		}
		currentPoints.add(p);
	}

	public void unmark() {
		unmark(HERE);
	}

	public void unmark(int position) {
		unmark(getMarkingCheckPoint(position));
	}

	private void unmark(Point p) {
		Iterator<Point> i = currentPoints.iterator();
		while (i.hasNext()) {
			Point pt = i.next();
			if (isOn(p, pt)) {
				i.remove();
			}
		}
	}

	public void flip() {
		flip(HERE);
	}

	public void flip(int position) {
		flip(getMarkingCheckPoint(position));
	}

	private void flip(Point p) {
		if (isMarked(p, currentPoints)) {
			unmark(p);
		} else {
			mark(p);
		}
	}

	public boolean isMarked() {
		return isMarked(HERE);
	}

	public boolean isMarked(int position) {
		if (inTurn()) {
			return isMarked(getMarkingCheckPoint(position), previousPoints);
		} else {
			return isMarked(getMarkingCheckPoint(position), currentPoints);
		}
	}

	private boolean isMarked(Point there, List<Point> points) {
		for (Point p : points) {
			if (isOn(p, there)) {
				return true;
			}
		}
		return false;
	}

	private boolean isOn(Point p1, Point p2) {
		return p1.distance(p2) < cashOfDistanceThreshold;
	}

	public Point getMarkingCheckPoint(int position) {
		switch (position) {
		case WEST_TOP:
			return getMarkingCheckPoint(-1, 1);
		case TOP:
			return getMarkingCheckPoint(0, 1);
		case EAST_TOP:
			return getMarkingCheckPoint(1, 1);
		case WEST:
			return getMarkingCheckPoint(-1, 0);
		case HERE:
			return getMarkingCheckPoint(0, 0);
		case EAST:
			return getMarkingCheckPoint(1, 0);
		case WEST_BOTTOM:
			return getMarkingCheckPoint(-1, -1);
		case BOTTOM:
			return getMarkingCheckPoint(0, -1);
		case EAST_BOTTOM:
			return getMarkingCheckPoint(1, -1);
		default:
			throw new RuntimeException("調べられる位置は1から9までです．");
		}
	}

	private Point getMarkingCheckPoint(int dx, int dy) {
		int x = getX();
		int y = getY();
		double thetaX = theta(angle() + direction() + 90.0);// 90度右向き
		double thetaY = theta(angle() + direction());
		// System.out.print(angle() + direction());
		int dxx = (int) (Math.sin(thetaX) * dx * size);
		int dxy = -(int) (Math.cos(thetaX) * dx * size);
		int dyx = (int) (Math.sin(thetaY) * dy * size);
		int dyy = -(int) (Math.cos(thetaY) * dy * size);
		// System.out.print("(" + dxx + "," + dxy + "," + dyx + "," + dyy +
		// ")");
		x = x + dxx + dyx;
		y = y + dxy + dyy;
		return new Point(x, y);
	}

	public void paint(Graphics g) {
		List<Point> copypoints = new ArrayList<Point>(currentPoints);
		for (Point p : copypoints) {
			if (p != null) {
				int half = size / 2;
				g.fillRect(p.x - half, p.y - half, size, size);
			}
		}
		super.paint(g);
	}

}
