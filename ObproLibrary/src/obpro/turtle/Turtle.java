package obpro.turtle;
/*
 * Turtle.java
 * 
 * Created on 2003/06/12
 */

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;

import common.resource.CResourceFinder;

/*
 * Class Turtle.
 * 
 * version 1.0.2 2007/12/12 HolderTurtle追加のため変更
 * version 1.0.3 2007/12/12 InputTurtle追加のため変更
 * version 1.0.4 2007/12/19 ButtonTurtleバグの修正（初期化ブロックが呼ばれる前に，
 * draw()でstateを呼んでNullPointerが出ていた．その他InputTurtleの仕様変更．
 * version 1.0.5 2007/12/21 SoundTurtleを追加 
 * version 1.1.0 2011/09/28 Sound周りを修正 
 * 		・URLで指定することにより，jarの中のサウンドファイルを再生できるようにした．
 * 		・SoundをPathで指定することにより，クラスパス→ファイルパスの順で自動で探索する 
 * 		・Thread周りの実装がおかしく，サウンド再生，停止のテストに失敗していたのでバグ修正
 * version 1.2.0 2011/11/22 APIの大幅な変更 
 * 		・ImageTurtleについてもURLで指定することにより，jarの中のImageファイルを読み込めるようにした。
 * 		・KeyPressing，MousePressingAPIの追加，mouseDownはmouseClickedシリーズに変更。
 * version 1.2.1 2011/12/16 セルオートマトン機能・バグ修正 
 * 		・タートルのfdが微妙にずれる問題を修正(sinの誤差を丸めてしまう)
 * 		・TurtleTurtleのlocation(), direction()が正しく返されない問題を修正 
 * 		・CellTurtle追加
 * version 1.2.2 
 * 		・CellTurtle微調整
 * version 1.2.3
 * 		・TurtleTurtleのlocation(), direction()が正しく返されない問題を修正
 * 		 のエンバグでタートルが動かないバグを修正
 * version 1.2.4
 * 		・ウインドウ消去時にSoundTurtleのクリア 
 * version 1.2.5
 * 		・デフォルトタートルのwarpが出来るようにする． 
 * version 1.2.6
 * 		・Turtleのインターナルクラスを外に出した．
 * 		・CellTurtleの２次元版を出来るようにする． 
 * version 1.2.7
 * 		・CellTurtleの実装方法を少し変更．
 * version 1.2.8
 * 		・printNolnを追加
 * version 1.3.0 		
 * 		・新しいコレクションとしてListTurtleを追加
 * 		・これに伴い，TurtleTurtleへのdelegationに関するlooks指定のコードを変更
 * 		・Listに入れられるよう，Turtleにimage()関数を追加．
 * 		・HolderTurtleを@deprecated指定
 * version 1.4.0 		
 * 		・ファイル読み書き機能追加
 * version 1.4.1
 * 		・ファイル読み, StringBufferに変更（高速化！）
 * version 1.4.2
 *		・ファイル読み, enc指定可能に．
 *		・ファイル読み, URL指定可能に．
 * version 1.4.3
 *		・window.canvas().setBackground(Color);出来るようにする．
 * version 1.4.4 (1.5.0)
 *		・CardTurtleの仕様を変更，getNumber()が出来るようにする．
 *		・getNumberAtCursor()を非推奨
 * version 1.4.5 (1.5.1)
 *		・CardTurtleの仕様を変更，bgColorが設定出来るようにする．
 *      ・上記にともない，CardTurtleのbgColorがnull->WHITEにする．
 * version 1.5.2
 *		・ListTurtle bgColor設定時にImageを書き直すようにバグ修正．
 * version 1.5.3
 *		・TextTurtleにgetText(), getNumber()を追加．（TextとCardが重複コードなのでリファクタリングした方がよさそう）
 * version 1.5.4
 *		・DefaultのFontをDialog->MS Gothicに変更
 * version 1.5.5
 *		・addCursor(index, Object);を追加
 * version 1.5.6
 *		・addToBeforeCursor(Object)を追加
 *		・addToAfterCursor(Object)を追加
 * version 1.5.7
 *		・ListTurtleのget()でNull例外処理
 * version 1.5.7.x 2012/04/05
 * 		・obproバージョンと統合（capturemodeの取り入れ）
 * version 1.5.8 2012/04/05
 * 		・start関数をpublicに（オブプロ版でオーバーライドできない）
 *      
 * @author macchan
 * @version $Id: Turtle.java,v 1.11 2007/12/21 11:13:42 macchan Exp $
 */
public class Turtle implements KeyListener, MouseListener, MouseMotionListener {

	private static final String version = "1.5.8 (2012/04/05)";

	static {
		System.out.println("Turtle Version: " + version);
	}

	/***************************************************
	 * static main
	 ****************************************************/

	public static void main(String argv[]) {
		String classname;
		if (argv.length >= 1) {
			classname = argv[0];
		} else {
			classname = "Turtle";
		}
		try {
			Object o = Class.forName(classname).newInstance();
			if (o.getClass() == Turtle.class) {
				System.out.println("実行コマンドが間違っています。(Turtle の後に自分のクラス名が必要です)");
				System.exit(0);
			} else if (o instanceof Turtle) {
				startTurtle((Turtle) o, argv);
			} else {
				System.out.println(classname
						+ " is not a subclass of Turtle class.");
			}
		} catch (Exception e) {
			System.out.println(classname + " クラスが見つかりません。コンパイルは通りましたか？");
		}
	}

	/****************************************
	 * Start and Stop
	 ****************************************/

	public static void startTurtle(Turtle turtle, String args[]) {
		if (args.length >= 1 && args[0].equals("capturemode")) {
			captureMode = true;
			System.out.println("capturemode!");
		}
		startTurtle(turtle);
	}

	public static void startTurtle(Turtle turtle) {
		allProtectedInstances.remove(turtle);
		defaultTurtle = turtle;
		window.addKeyListener(turtle);
		window.canvas().addKeyListener(turtle);
		window.canvas().addMouseListener(turtle);
		window.canvas().addMouseMotionListener(turtle);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		if (captureMode) {
			window.size(1000, 700);
		}
		window.setVisible(true);
		initialized = true;
		window.canvas().mappedWait();
		window.restart();
	}

	public static void stopTurtle() {
		window.dispose();
		window = new TurtleFrame(100, 100, 300, 300);
		initialized = false;
	}

	/****************************************
	 * For Applet
	 ****************************************/

	public static boolean captureMode = false;
	public static Applet applet = null;

	/****************************************
	 * static instances
	 ****************************************/

	private static List allProtectedInstances = new ArrayList();

	private static List allInstances = new ArrayList();

	public static List getAllInstances() {
		return new ArrayList(allInstances);
	}

	public static List getAllProtectedInstances() {
		return new ArrayList(allProtectedInstances);
	}

	public static void resetAllInstances() {
		allInstances.clear();
		for (int i = 0; i < allProtectedInstances.size(); i++) {
			Turtle t = (Turtle) allProtectedInstances.get(i);
			t.initialize();
		}
	}

	/***************************************************
	 * static variables
	 ****************************************************/

	private static Object instanciationLock = new Object();
	private static boolean isinstanciation = false;

	public static TurtleFrame window = new TurtleFrame(100, 100, 300, 300);

	public static Turtle defaultTurtle = null;

	private static final Turtle nullTurtle = new DefaultTurtle();

	public static boolean initialized = false;

	/***************************************************
	 * 変数
	 ****************************************************/

	// 親タートル関連
	protected ListTurtle parent;
	@SuppressWarnings("deprecation")
	protected HolderTurtle parentHolder;// @deprecated 下位互換性のため
	protected List<InputTurtle> inputTurtles = new ArrayList<InputTurtle>();

	// 座標関連
	private Point2D location = new Point2D.Double(100d, 100d); // 中心の位置

	private Point2D balance = new Point2D.Double(100d, 100d); // 重心の位置

	private Dimension2D size = new DoubleDimension(100d, 100d); // 大きさ

	private double angle = 0.0; // 回転角度

	private double direction = 0.0; // 方向

	// 形関連
	private Turtle looks = null; // 見た目

	private boolean show = true;

	private LineList originalShape = null; // 見た目

	private LineList shape = new LineList(); // 形

	// 軌跡関連
	private boolean penDown = false; // ペンが下りているか

	private Color penColor = Color.black; // ペンの色

	private LineList locus = new LineList(); // 軌跡

	// 委譲関連
	private Turtle delegator = null; // 委譲者

	// 形状変換関連
	private boolean dirty = true;

	private AffineTransform currentTransform = null;

	// 入力関連
	private KeyEvent keyEvent = null;

	private MouseEvent mouseEvent = null;

	private MouseEvent mouseMotionEvent = null;

	private boolean inputCaptured = true;

	/***************************************************
	 * コンストラクタ
	 ****************************************************/

	/**
	 * Constructor.
	 */
	public Turtle() {
		initialize();
	}

	/***************************************************
	 * initialize
	 ***************************************************/

	private void initialize() {
		synchronized (instanciationLock) {
			initializeParameter();
			if (this instanceof DefaultTurtle) {
				// do nothing
			} else if (isinstanciation) {
				delegator(nullTurtle);
			} else if (!initialized) {
				allProtectedInstances.add(this);
			} else if (this.getClass() == Turtle.class) {
				delegator(new TurtleTurtle());
				allInstances.add(this);
			} else if (this instanceof TurtleTurtle) {
				allInstances.add(this);
			} else {
				initializeLooks();
				allInstances.add(this);
			}
		}
	}

	/***************************************************
	 * die
	 ****************************************************/

	public void die() {
		if (allInstances.contains(this)) {
			allInstances.remove(this);
		}
	}

	/***************************************************
	 * Reset
	 ****************************************************/

	private void initializeParameter() {
		// 座標関連
		location = new Point2D.Double(100d, 100d); // 中心の位置
		balance = new Point2D.Double(100d, 100d); // 重心の位置
		size = new DoubleDimension(100d, 100d); // 大きさ
		angle = 0.0; // 回転角度
		direction = 0.0; // 方向

		// 形関連
		looks = null; // 見た目
		show = true;
		originalShape = null; // 見た目
		shape = new LineList(); // 形

		// 軌跡関連
		penDown = false; // ペンが下りているか
		penColor = Color.black; // ペンの色
		locus = new LineList(); // 軌跡

		// 委譲関連
		delegator = null; // 委譲者

		// 形状変換関連
		dirty = true;
		currentTransform = null;

		// 入力関連
		keyEvent = null;
		mouseEvent = null;
		inputCaptured = true;
	}

	public void initializeLooks() {
		DefaultTurtle turtle = new DefaultTurtle();

		synchronized (instanciationLock) {
			isinstanciation = true;
			delegator(turtle);
			try {
				start();
			} catch (RuntimeException ex) {
				System.out.println(ex.getMessage());
			} finally {
				delegator(null);
				isinstanciation = false;
			}
		}

		turtle.die();
		this.originalShape = new LineList(turtle.locus());
		looks(this);
		resetScale();
	}

	/***************************************************
	 * Looks関連
	 ****************************************************/

	public synchronized void looks(Turtle looks) {
		if (delegator != null) {
			delegator.looks(looks);
			return;
		}
		this.looks = looks;
		this.shape = new LineList(looks.originalShape());
		this.currentTransform = null;
		this.dirty = true;
	}

	public Turtle looks() {
		if (delegator != null) {
			return delegator.looks();
		}
		return looks;
	}

	public void resetLooks() {
		if (delegator != null) {
			delegator.resetLooks();
			return;
		}
		looks(this);
	}

	public void looksSize() {
		if (delegator != null) {
			delegator.looksSize();
			return;
		}
		size(looks.size());
	}

	/***************************************************
	 * 委譲関連
	 ****************************************************/

	public void delegator(Turtle delegator) {
		this.delegator = delegator;
	}

	/***************************************************
	 * shape(形状) locus(軌跡)　関連
	 ****************************************************/

	protected LineList shape() {
		return this.shape;
	}

	protected LineList locus() {
		return this.locus;
	}

	protected LineList originalShape() {
		if (this.originalShape != null) {
			return this.originalShape;
		} else {
			return this.locus;
		}
	}

	/***************************************************
	 * show関連
	 ****************************************************/

	public void show() {
		show(true);
	}

	public void hide() {
		show(false);
	}

	public void show(boolean show) {
		if (delegator != null) {
			delegator.show(show);
		} else {
			this.show = show;
			if (show) {
				reshape();
			}
		}
	}

	public boolean isShow() {
		return show;
	}

	/***************************************************
	 * Location関連
	 ****************************************************/

	public Point2D location() {
		if (delegator != null) {
			return delegator.location();
		}
		return new Point2D.Double(x(), y());
	}

	public void location(Point2D location) {
		location(location.getX(), location.getY());
	}

	public void location(double x, double y) {

		if (penDown) {
			Point2D newLocation = new Point2D.Double(x, y);
			locus.add(new Line(location, newLocation, penColor));
		}

		double moveX = x - x();
		double moveY = y - y();

		location.setLocation(x, y);
		balance.setLocation(balance.getX() + moveX, balance.getY() + moveY);

		dirty = true;

	}

	public double x() {
		return location.getX();
	}

	public double y() {
		return location.getY();
	}

	public int getX() {
		return (int) x();
	}

	public int getY() {
		return (int) y();
	}

	public void x(double x) {
		location(x, y());
	}

	public void y(double y) {
		location(x(), y);
	}

	public double minX() {
		return x() - width() / 2;
	}

	public double minY() {
		return y() - height() / 2;
	}

	public double maxX() {
		return x() + width() / 2;
	}

	public double maxY() {
		return y() + height() / 2;
	}

	/***************************************************
	 * Rotated(回転後)Position関連
	 ****************************************************/

	public Point2D rotatedLocation() {
		return new Point2D.Double(rotatedX(), rotatedY());
	}

	public double rotatedX() {
		return shape.getBounds().getCenterX();
	}

	public double rotatedY() {
		return shape.getBounds().getCenterY();
	}

	public double rotatedMinX() {
		return rotatedX() - rotatedWidth() / 2;
	}

	public double rotatedMinY() {
		return rotatedY() - rotatedHeight() / 2;
	}

	public double rotatedMaxX() {
		return rotatedX() + rotatedWidth() / 2;
	}

	public double rotatedMaxY() {
		return rotatedY() + rotatedHeight() / 2;
	}

	/***************************************************
	 * Size関連
	 ****************************************************/

	public Dimension2D size() {
		if (delegator != null) {
			return delegator.size();
		}
		return new DoubleDimension(size);
	}

	public void size(Dimension2D dimension) {
		size(dimension.getWidth(), dimension.getHeight());
	}

	public void size(double width, double height) {

		if (width <= 0.1) {
			width = 0.1;
		}
		if (height <= 0.1) {
			height = 0.1;
		}

		size.setSize(width, height);
		dirty = true;

	}

	public double width() {
		return size().getWidth();
	}

	public double height() {
		return size().getHeight();
	}

	public int getWidth() {
		return (int) width();
	}

	public int getHeight() {
		return (int) height();
	}

	public void width(double width) {
		size(width, height());
	}

	public void height(double height) {
		size(width(), height);
	}

	public void large(double width, double height) {
		size(width() + width, height() + height);
	}

	public void large(double length) {
		large(length, length);
	}

	public void wide(double length) {
		large(length, 0);
	}

	public void tall(double length) {
		large(0, length);
	}

	public void small(double width, double height) {
		large(-width, -height);
	}

	public void small(double length) {
		large(-length);
	}

	public void narrow(double length) {
		wide(-length);
	}

	public void little(double length) {
		tall(-length);
	}

	/***************************************************
	 * Rotated(回転後)Size関連
	 ****************************************************/

	public double rotatedWidth() {
		return shape.getBounds().getWidth();
	}

	public double rotatedHeight() {
		return shape.getBounds().getHeight();
	}

	/***************************************************
	 * Bounds関連
	 ****************************************************/

	public Rectangle2D bounds() {
		return new Rectangle2D.Double(minX(), minY(), width(), height());
	}

	public void bounds(Rectangle2D r) {
		location(r.getCenterX(), r.getCenterY());
		size(r.getWidth(), r.getHeight());
	}

	public void bounds(double x, double y, double width, double height) {
		bounds(new Rectangle2D.Double(x, y, width, height));
	}

	public Rectangle2D rotatedBounds() {
		return shape.getBounds();
	}

	/***************************************************
	 * RotatePoint関連
	 ****************************************************/

	public Point2D balance() {
		return balance;
	}

	public double balanceX() {
		return balance().getX();
	}

	public double balanceY() {
		return balance().getY();
	}

	public int getBalanceX() {
		return (int) balanceX();
	}

	public int getBalanceY() {
		return (int) balanceY();
	}

	public void balance(double percentX, double percentY) {
		double xMag = percentX / 100d;
		double yMag = percentY / 100d;

		RelativePoint rp = new RelativePoint(xMag, yMag);
		balance = rp.getPoint(bounds());
	}

	/***************************************************
	 * move関連
	 ****************************************************/

	public void fd(double length) {
		if (delegator != null) {
			delegator.fd(length);
		} else {
			move(length, 0);
		}
	}

	public void bk(double length) {
		if (delegator != null) {
			delegator.bk(length);
		} else {
			move(length, 180);
		}
	}

	public void right(double length) {
		if (delegator != null) {
			delegator.right(length);
		} else {
			move(length, 90);
		}
	}

	public void left(double length) {
		if (delegator != null) {
			delegator.right(length);
		} else {
			move(length, -90);
		}
	}

	public void move(double length, double direction) {

		double theta = theta(angle() + direction() + direction);
		double xLength = Math.sin(theta) * length;
		double yLength = -Math.cos(theta) * length;

		double newX = x() + xLength;
		double newY = y() + yLength;

		warp(newX, newY);
	}

	public void warp(double x, double y) {
		if (delegator != null) {
			delegator.location(x, y);
		} else {
			location(x, y);
		}
	}

	public void warpByTopLeft(double x, double y) {
		double centerX = x + width() / 2;
		double centerY = y + height() / 2;
		warp(centerX, centerY);
	}

	/***************************************************
	 * scale関連
	 ****************************************************/

	public void scale(double scale) {
		scale(scale, scale);
	}

	public void scale(double scaleX, double scaleY) {
		size(width() * scaleX, height() * scaleY);
	}

	public void resetScale(double scale) {
		Rectangle2D org = originalBounds();
		size(org.getWidth() * scale, org.getHeight() * scale);
	}

	public void resetScale() {
		resetScale(1d);
	}

	public Scale scale() {
		Rectangle2D org = originalBounds();
		return new Scale(org.getWidth(), org.getHeight(), width(), height());
	}

	public double scaleX() {
		return scale().x();
	}

	public double scaleY() {
		return scale().y();
	}

	/***************************************************
	 * angle関連
	 ****************************************************/

	public void angle(double angle) {
		this.angle = angle;
		dirty = true;
	}

	public double angle() {
		return angle;
	}

	public void rt(double angle) {
		if (delegator != null) {
			delegator.rt(angle);
		} else {
			rotate(angle);
		}
	}

	public void lt(double angle) {
		if (delegator != null) {
			delegator.lt(angle);
		} else {
			rotate(-angle);
		}
	}

	public void rotate(double angle) {
		angle(this.angle + angle);
	}

	/***************************************************
	 * direction関連
	 ****************************************************/

	public void direction(double direction) {
		this.direction = direction;
	}

	public double direction() {
		return direction;
	}

	public void directionRt(double direction) {
		rotateDirection(direction);
	}

	public void directionLt(double direction) {
		rotateDirection(-direction);
	}

	public void rotateDirection(double direction) {
		direction(this.direction + direction);
	}

	/***************************************************
	 * thetaを求めるUtility
	 ****************************************************/

	protected double theta() {
		return theta(angle());
	}

	protected double theta(double angle) {
		// return angle / 360d * 2d * Math.PI;
		return Math.toRadians(angle);
	}

	/***************************************************
	 * pen関連
	 ****************************************************/

	public void up() {
		if (delegator != null) {
			delegator.up();
		} else {
			penDown = false;
		}
	}

	public void down() {
		if (delegator != null) {
			delegator.down();
		} else {
			penDown = true;
		}
	}

	/***************************************************
	 * color関連
	 ****************************************************/

	public void color(Color penColor) {
		if (delegator != null) {
			delegator.color(penColor);
		} else {
			this.penColor = penColor;
		}
	}

	public Color color() {
		return penColor;
	}

	/***************************************************
	 * あたり判定関連
	 ****************************************************/

	public boolean intersects(Turtle target) {
		if (!isShow() || !target.isShow()) {
			return false;
		}

		this.reshape();
		target.reshape();

		Rectangle2D r1 = this.rotatedBounds();
		Rectangle2D r2 = target.rotatedBounds();
		// return r1.intersects(r2);

		Rectangle2D intersection = r1.createIntersection(r2);
		if (intersection.isEmpty()) {
			return false;
		}
		return this.intersectsBounds(intersection)
				&& target.intersectsBounds(intersection);
	}

	public boolean contains(double x, double y) {
		if (!isShow()) {
			return false;
		}

		reshape();
		Rectangle2D rect = shape().getBounds();
		return rect.contains(x, y);
	}

	protected boolean intersectsBounds(Rectangle2D bounds) {
		if (looks() instanceof ImageTurtle) {
			return true;
		}
		return shape().intersects(bounds);
	}

	protected Shape rotatedShape() {
		AffineTransform transform = new AffineTransform();
		transform.rotate(theta(), balanceX(), balanceY());
		return transform.createTransformedShape(bounds());
	}

	/***************************************************
	 * キー入力関連
	 ****************************************************/

	private Map<Integer, Boolean> pressing = new HashMap<Integer, Boolean>();

	public void keyPressed(KeyEvent e) {
		keyEvent = e;
		inputCaptured = false;
		keyPressed(e.getKeyCode());
		pressing.put(e.getKeyCode(), true);
		for (InputTurtle input : inputTurtles) {
			input.captureText(e);
		}
	}

	void keyPressed(int keyCode) {
	}

	public void keyReleased(KeyEvent e) {
		pressing.put(e.getKeyCode(), false);
		keyReleased(e.getKeyCode());
	}

	void keyReleased(int keyCode) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public int key() {
		inputCaptured = true;
		if (keyEvent != null) {
			return keyEvent.getKeyCode();
		} else {
			return -1;
		}
	}

	/**
	 * use keyDown(int)
	 * 
	 * @deprecated
	 */
	public boolean keyDown() {
		inputCaptured = true;
		return keyEvent != null;
	}

	private void resetKey() {
		if (inputCaptured) {
			keyEvent = null;
		}
	}

	public boolean keyDown(int keycode) {
		if (pressing.containsKey(keycode)) {
			return pressing.get(keycode);
		}
		return false;
	}

	/***************************************************
	 * マウス入力関連
	 ****************************************************/

	// --- implements MouseListener ---
	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	private boolean leftMousePressing = false;
	private boolean rightMousePressing = false;

	public void mousePressed(MouseEvent e) {
		mouseEvent = e;
		inputCaptured = false;
		if (e.getButton() == MouseEvent.BUTTON1) {
			leftMousePressing = true;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			rightMousePressing = true;
		}
		mousePressed(e.getX(), e.getY());
	}

	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			leftMousePressing = false;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			rightMousePressing = false;
		}
		mouseReleased(e.getX(), e.getY());
	}

	// --- implements MouseMotionListener ---

	public void mouseDragged(MouseEvent e) {
		mouseMotionEvent = e;
	}

	public void mouseMoved(MouseEvent e) {
		mouseMotionEvent = e;
	}

	// --- original method ---

	void mousePressed(int x, int y) {
	}

	void mouseReleased(int x, int y) {
	}

	public int mouseX() {
		inputCaptured = true;
		if (mouseEvent != null) {
			return mouseEvent.getX();
		} else if (mouseMotionEvent != null) {
			return mouseMotionEvent.getX();
		} else {
			return 0;
		}
	}

	public int mouseY() {
		inputCaptured = true;
		if (mouseEvent != null) {
			return mouseEvent.getY();
		} else if (mouseMotionEvent != null) {
			return mouseMotionEvent.getY();
		} else {
			return 0;
		}
	}

	public boolean mouseClicked() {
		return leftMouseClicked() || rightMouseClicked();
	}

	public boolean mouseDown() {
		return leftMouseDown() || rightMouseDown();
	}

	public boolean leftMouseClicked() {
		inputCaptured = true;
		return mouseEvent == null ? false
				: mouseEvent.getButton() == MouseEvent.BUTTON1;
	}

	public boolean leftMouseDown() {
		return leftMousePressing;
	}

	public boolean rightMouseClicked() {
		inputCaptured = true;
		return mouseEvent == null ? false
				: mouseEvent.getButton() == MouseEvent.BUTTON3;
	}

	public boolean rightMouseDown() {
		return rightMousePressing;
	}

	public boolean doubleClick() {
		inputCaptured = true;
		return mouseEvent == null ? false : mouseEvent.getClickCount() == 2;
	}

	private void resetMouse() {
		if (inputCaptured) {
			mouseEvent = null;
		}
	}

	/***************************************************
	 * 標準入出力関連
	 ****************************************************/

	public String inputString() {
		InputStream input;
		if (window.console != null) {
			input = window.console.in;
		} else {
			input = System.in;
		}
		String returnString = null;
		try {
			InputStreamReader isr = new InputStreamReader(input);
			BufferedReader br = new BufferedReader(isr);
			returnString = br.readLine();
			return returnString;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// added by macchan 10/8
	public double inputDouble() {
		double returnDouble = 0;
		returnDouble = Double.parseDouble(inputString());
		return returnDouble;
	}

	public int input() {
		int returnInt = 0;
		returnInt = Integer.parseInt(inputString());
		return returnInt;
	}

	public void print(Object o) {
		printlnInternal(o);
	}

	public void print(int x) {
		printlnInternal(new Integer(x));
	}

	public void print(double x) {
		printlnInternal(new Double(x));
	}

	public void print(boolean x) {
		printlnInternal(new Boolean(x));
	}

	private void printlnInternal(Object o) {
		if (window.console != null) {
			window.console.out.println(o);
		} else {
			System.out.println(o);
		}
	}

	public void printNoln(Object o) {
		printInternal(o);
	}

	public void printNoln(int x) {
		printInternal(new Integer(x));
	}

	public void printNoln(double x) {
		printInternal(new Double(x));
	}

	public void printNoln(boolean x) {
		printInternal(new Boolean(x));
	}

	private void printInternal(Object o) {
		if (window.console != null) {
			window.console.out.print(o);
		} else {
			System.out.print(o);
		}
	}

	/***************************************************
	 * ファイルのセーブ，ロード
	 ****************************************************/

	public void saveToFile(String filename, String text) {
		try {
			FileWriter fw = new FileWriter(filename);
			fw.append(text);
			fw.close();
		} catch (Exception ex) {
			// throw new RuntimeException(ex);
			print("書き込みに失敗しました " + ex.getMessage());
		}
	}

	public String loadFromFile(String filename) {
		return loadFromFile(filename, null);
	}

	public String loadFromFile(String filename, String enc) {
		try {
			URL url = CResourceFinder.getResource(filename, getCaller());

			BufferedReader br;
			if (enc == null) {
				br = new BufferedReader(new InputStreamReader(url.openStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(url.openStream(),
						enc));
			}
			StringBuffer buf = new StringBuffer();
			String line = "";
			while ((line = br.readLine()) != null) {
				if (buf.length() > 0) {
					buf.append("\n");
				}
				buf.append(line);
			}
			br.close();
			return buf.toString();
		} catch (Exception ex) {
			// throw new RuntimeException(ex);
			print("読み込みに失敗しました " + ex.getMessage());
			return "";
		}
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

	/***************************************************************************
	 * ランダム関連
	 **************************************************************************/

	private Random random = new Random();

	public void setRandomSeed(long seed) {
		random = new Random(seed);
	}

	/**
	 * 0からmax(は含まない）までのランダムな値を返します．
	 */
	public int random(int max) {
		return random.nextInt(max);
	}

	/***************************************************
	 * Sleep関連
	 ****************************************************/

	public void sleep(double second) {
		synchronized (instanciationLock) {
			if (isinstanciation) {
				throw new RuntimeException("タートル生成中にアニメーションが呼ばれました");
			}

			try {
				Thread.sleep((long) (second * 1000d));
			} catch (InterruptedException ex) {
				throw new RuntimeException("Interrupted By User");
			}
		}
	}

	/***************************************************
	 * paint関連
	 ****************************************************/

	public void update() {
		window.canvas().repaint();
		resetKey();
		resetMouse();
	}

	public void paint(Graphics2D g) {
		if (show) {
			reshape();
			draw(g);
		}
	}

	public void draw(Graphics2D g) {
		locus.paint(g);

		if (looks instanceof ImageTurtle) {
			ImageTurtle it = (ImageTurtle) looks;
			g.drawImage(it.image(), it.createTransformOp(theta(), width(),
					height(), rotatedWidth(), rotatedHeight()),
					(int) rotatedMinX(), (int) rotatedMinY());
			return;
		}

		shape.paint(g);
	}

	/***************************************************
	 * Start関連
	 ****************************************************/

	public void start() {
	}

	/***************************************************
	 * Transform 形状変換関連
	 ****************************************************/

	protected boolean dirty() {
		return dirty;
	}

	protected void dirty(boolean dirty) {
		this.dirty = dirty;
	}

	private Rectangle2D originalBounds() {
		return originalShape.getBounds();
	}

	protected void reshape() {
		if (shape() != null && dirty) {
			doTransform();
			dirty = false;
		}
	}

	protected synchronized void doTransform() {

		// 既に現在変換済みなら一旦元に戻す
		if (currentTransform != null) {
			try {
				AffineTransform reverse = currentTransform.createInverse();
				shape.transform(reverse);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// 処理
		AffineTransform transform = createTransform();
		shape.transform(transform);
		currentTransform = transform;
	}

	protected AffineTransform createTransform() {
		AffineTransform transform = new AffineTransform();

		Rectangle2D r = shape.getBounds();
		Scale scale = new Scale(r.getWidth(), r.getHeight(), width(), height());

		// 逆順になっています
		transform.rotate(theta(), balanceX(), balanceY());
		transform.translate(balanceX(), balanceY());
		transform.scale(scale.x(), scale.y());
		transform.translate(-balanceX(), -balanceY());
		transform.translate(x(), y());
		transform.translate(-r.getCenterX(), -r.getCenterY());

		return transform;
	}

	/***************************************************
	 * Listに入れられるように，imageを取ってこれるようにする．(2012/01/08)
	 ****************************************************/

	private static final BufferedImage nullImage = new BufferedImage(1, 1,
			BufferedImage.TYPE_4BYTE_ABGR);

	public BufferedImage image() {
		if (delegator != null) {
			return delegator.image();
		} else {
			return nullImage;
		}
	}
}

/***************************************************
 * 
 * Class DefaultTurtle.
 * 
 ****************************************************/

class DefaultTurtle extends Turtle {
	public DefaultTurtle() {
		down();
	}
}

/***************************************************
 * 
 * Class Scale.
 * 
 ****************************************************/

class Scale {

	private double x = 1.0;

	private double y = 1.0;

	public Scale(Dimension2D d1, Dimension2D d2) {
		this(d1.getWidth(), d1.getHeight(), d2.getWidth(), d2.getHeight());
	}

	public Scale(double w1, double h1, double w2, double h2) {
		this(w2 / w1, h2 / h1);
	}

	public Scale(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double x() {
		return x;
	}

	public void x(double x) {
		this.x = x;
	}

	public double y() {
		return y;
	}

	public void y(double y) {
		this.y = y;
	}

	public void add(Scale scale) {
		x = x * scale.x;
		y = y * scale.y;
	}

}

/***************************************************
 * 
 * Class DoubleDimension.
 * 
 ****************************************************/

class DoubleDimension extends Dimension2D {

	private double width = 0d;

	private double height = 0d;

	/**
	 * Constructor.
	 */
	public DoubleDimension(double width, double height) {
		setSize(width, height);
	}

	/**
	 * Constructor for copy.
	 */
	public DoubleDimension(Dimension2D dimension) {
		this(dimension.getWidth(), dimension.getHeight());
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
	}

	public Object clone() {
		return new DoubleDimension(width, height);
	}

}

/***************************************************
 * 
 * Class RelativePoint.
 * 
 ****************************************************/

class RelativePoint {

	private double xMagnification = 0.5d;
	private double yMagnification = 0.5d;

	/**
	 * Constructor.
	 */
	public RelativePoint(double xMagnification, double yMagnification) {
		setMagnification(xMagnification, yMagnification);
	}

	public Point2D getPoint(Rectangle2D rectangle) {
		double x = rectangle.getX();
		double y = rectangle.getY();
		double centerX = x + rectangle.getWidth() * xMagnification;
		double centerY = y + rectangle.getHeight() * yMagnification;
		return new Point2D.Double(centerX, centerY);
	}

	public void setMagnification(double xMagnification, double yMagnification) {
		this.xMagnification = xMagnification;
		this.yMagnification = yMagnification;
	}

}

/***************************************************
 * 
 * Class Line.
 * 
 ****************************************************/

class Line {

	private Point2D p1 = null;

	private Point2D p2 = null;

	private Color color = null;

	/**
	 * Constructor.
	 */
	public Line(Point2D p1, Point2D p2, Color color) {
		this(p1.getX(), p1.getY(), p2.getX(), p2.getY(), color);
	}

	/**
	 * Constructor.
	 */
	public Line(double p1X, double p1Y, double p2X, double p2Y, Color color) {
		this.p1 = new Point2D.Double(p1X, p1Y);
		this.p2 = new Point2D.Double(p2X, p2Y);
		this.color = color;
	}

	/**
	 * Constructor for copy.
	 */
	public Line(Line line) {
		p1 = new Point2D.Double(line.p1.getX(), line.p1.getY());
		p2 = new Point2D.Double(line.p2.getX(), line.p2.getY());
		color = line.color;
	}

	public void transform(AffineTransform transform) {
		transform.transform(p1, p1);
		transform.transform(p2, p2);
	}

	public Line2D getLine2D() {
		return new Line2D.Double(p1, p2);
	}

	public void paint(Graphics2D g) {
		Color originalColor = g.getColor();
		g.setColor(color);
		g.draw(getLine2D());
		g.setColor(originalColor);
	}

}

/***************************************************
 * 
 * Class LineList.
 * 
 ****************************************************/

class LineList {

	private boolean dirty = false;

	private Rectangle2D bounds = new Rectangle2D.Double(0, 0, 0, 0);

	private List lines = new ArrayList();

	/**
	 * Constructor.
	 */
	public LineList() {
	}

	/**
	 * Constructor for copy.
	 */
	public LineList(LineList lines) {
		Iterator i = lines.lines.iterator();
		while (i.hasNext()) {
			Line line = (Line) i.next();
			this.lines.add(new Line(line));
			dirty = true;
		}
	}

	public synchronized void add(Line line) {
		lines.add(line);
		dirty = true;
	}

	public synchronized void remove(Line line) {
		lines.remove(line);
		dirty = true;
	}

	public synchronized void transform(AffineTransform transform) {
		Iterator i = lines.iterator();
		while (i.hasNext()) {
			Line line = (Line) i.next();
			line.transform(transform);
		}
		dirty = true;
	}

	public synchronized void paint(Graphics2D g) {
		Iterator i = lines.iterator();
		while (i.hasNext()) {
			Line line = (Line) i.next();
			line.paint(g);
		}
	}

	public synchronized Rectangle2D getBounds() {

		if (dirty) {
			synchronized (this) {
				Rectangle2D allBounds = null;
				Iterator i = lines.iterator();
				while (i.hasNext()) {
					Line line = (Line) i.next();
					Rectangle2D bounds = line.getLine2D().getBounds2D();
					if (allBounds == null) {
						allBounds = bounds;
					} else {
						allBounds = allBounds.createUnion(bounds);
					}
				}
				if (allBounds == null) {
					allBounds = new Rectangle2D.Double(0, 0, 0, 0);
				}

				bounds = allBounds;
				dirty = false;
			}
		}

		return bounds;

	}

	public synchronized boolean intersects(Rectangle2D rectangle) {
		Iterator i = lines.iterator();
		while (i.hasNext()) {
			Line line = (Line) i.next();
			if (rectangle.intersectsLine(line.getLine2D())) {
				return true;
			}
		}
		return false;
	}

}
