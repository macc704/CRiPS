package obpro.gui;

import java.awt.BufferCapabilities;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * ウインドウを表現するクラス
 * 
 * @author macchan
 * @version 2.0
 */
public class BWindow {

	private JFrame frame;

	private BCanvas canvas;

	/**
	 * コンストラクタ
	 */
	public BWindow() {
		//フレーム生成
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//パネル＆イベントハンドラ生成
		CanvasPanel canvasPanel = new CanvasPanel();
		frame.getContentPane().add(canvasPanel);

		CanvasKeyEventHandler keyHandler = new CanvasKeyEventHandler();
		CanvasMouseEventHandler mouseHandler = new CanvasMouseEventHandler();
		frame.addKeyListener(keyHandler);
		frame.getContentPane().addKeyListener(keyHandler);
		canvasPanel.addKeyListener(keyHandler);
		canvasPanel.addMouseListener(mouseHandler);
		canvasPanel.addMouseMotionListener(mouseHandler);

		//キャンバス生成
		canvas = new BCanvas(canvasPanel, keyHandler, mouseHandler);
	}

	/**
	 * 位置を設定する
	 */
	public void setLocation(int x, int y) {
		frame.setLocation(x, y);
	}

	/**
	 * 大きさを設定する
	 */
	public void setSize(int width, int height) {
		frame.setSize(width, height);
	}

	/**
	 * （この）ウインドウを表示する
	 */
	public void show() {
		frame.setVisible(true);
	}

	/**
	 * 書き込みができるCanvasインスタンスを取得する
	 */
	public BCanvas getCanvas() {
		return canvas;
	}

}

/**
 * キーのイベントを拾うクラス
 */
class CanvasKeyEventHandler implements KeyListener {

	//定数
	public static final int NULL_KEY_CODE = -1;

	public static final int NULL_MOUSE_LOCATION = -1;

	//入力イベント関連
	private KeyEvent bufferKeyEvent = null;

	private KeyEvent capturedKeyEvent = null;

	private Set pressingKeys = new HashSet();

	/***************************************************
	 * リスナインターフェイスの実装
	 ****************************************************/

	public void keyPressed(KeyEvent e) {
		bufferKeyEvent = e;
		pressingKeys.add(new Integer(e.getKeyCode()));
	}

	public void keyReleased(KeyEvent e) {
		pressingKeys.remove(new Integer(e.getKeyCode()));
	}

	public void keyTyped(KeyEvent e) {
	}

	/***************************************************
	 * 公開インターフェイス
	 ****************************************************/

	public int key() {
		if (capturedKeyEvent != null) {
			return capturedKeyEvent.getKeyCode();
		} else {
			return NULL_KEY_CODE;
		}
	}

	public boolean isKeyDown() {
		return capturedKeyEvent != null;
	}

	public boolean isKeyPressing(int keycode) {
		return pressingKeys.contains(new Integer(keycode));
	}

	/***************************************************
	 * 更新関連
	 ****************************************************/

	public void update() {
		capturedKeyEvent = bufferKeyEvent;
		bufferKeyEvent = null;
	}

}

/**
 * マウスのイベントを拾うクラス
 */
class CanvasMouseEventHandler implements MouseListener, MouseMotionListener {

	//定数
	public static final int NULL_MOUSE_LOCATION = -1;

	//入力イベント関連
	private int mouseX = NULL_MOUSE_LOCATION;

	private int mouseY = NULL_MOUSE_LOCATION;

	private boolean isDragging = false;

	private MouseEvent bufferMouseEvent = null;

	private MouseEvent capturedMouseEvent = null;

	private Set pressingMouses = new HashSet();

	/***************************************************
	 * リスナインターフェイスの実装
	 ****************************************************/

	public void mousePressed(MouseEvent e) {
		bufferMouseEvent = e;
		pressingMouses.add(new Integer(e.getButton()));
	}

	public void mouseReleased(MouseEvent e) {
		bufferMouseEvent = e;
		pressingMouses.remove(new Integer(e.getButton()));
		isDragging = false;
	}

	public void mouseClicked(MouseEvent e) {
		bufferMouseEvent = e;
	}

	public void mouseEntered(MouseEvent e) {
		bufferMouseEvent = e;
	}

	public void mouseExited(MouseEvent e) {
		bufferMouseEvent = e;
		isDragging = false;
	}

	public void mouseMoved(MouseEvent e) {
		bufferMouseEvent = e;
	}

	public void mouseDragged(MouseEvent e) {
		bufferMouseEvent = e;
		isDragging = true;
	}

	/***************************************************
	 * 公開インターフェイス
	 ****************************************************/

	public int mouseX() {
		return mouseX;
	}

	public int mouseY() {
		return mouseY;
	}

	public boolean isMouseDown() {
		return isRightMouseDown() || isLeftMouseDown();
	}

	public boolean isRightMouseDown() {
		if (pressingMouses.contains(new Integer(MouseEvent.BUTTON3))) {
			return true;
		} else {
			return capturedMouseEvent == null ? false : capturedMouseEvent
					.getButton() == MouseEvent.BUTTON3;
		}
	}

	public boolean isLeftMouseDown() {
		if (pressingMouses.contains(new Integer(MouseEvent.BUTTON1))) {
			return true;
		} else {
			return capturedMouseEvent == null ? false : capturedMouseEvent
					.getButton() == MouseEvent.BUTTON1;
		}
	}

	public boolean isClick() {
		return capturedMouseEvent == null ? false
				: capturedMouseEvent.getID() == MouseEvent.MOUSE_CLICKED;
	}

	public boolean isSingleClick() {
		return capturedMouseEvent == null ? false
				: capturedMouseEvent.getID() == MouseEvent.MOUSE_CLICKED
						&& capturedMouseEvent.getClickCount() == 1;
	}

	public boolean isDoubleClick() {
		return capturedMouseEvent == null ? false
				: capturedMouseEvent.getID() == MouseEvent.MOUSE_CLICKED
						&& capturedMouseEvent.getClickCount() == 2;
	}

	public boolean isDragging() {
		return isDragging;
	}

	/***************************************************
	 * 更新関連
	 ****************************************************/

	public void update() {
		capturedMouseEvent = bufferMouseEvent;
		if (capturedMouseEvent != null) {
			mouseX = capturedMouseEvent.getX();
			mouseY = capturedMouseEvent.getY();
		}

		bufferMouseEvent = null;
	}

}

/**
 * Canvasの委譲先クラス
 * Canvasに書かれる形式をバッファし，Swing形式に変換し出力します．
 */
class CanvasPanel extends Canvas implements ComponentListener {

	private static final long serialVersionUID = 4997692113753908667L;

	//定数
	private static final int FILP_BUFFERSTRATEGY = 3;

	private static BufferStrategy NULL_BUFFERSTRATEGY = new NullBufferStrategy();

	private static final Graphics2D NULL_GRAPHICS = (Graphics2D) (new BufferedImage(
			1, 1, BufferedImage.TYPE_3BYTE_BGR).createGraphics());

	//属性
	private Graphics2D offGraphics;

	/**
	 * コンストラクタ
	 */
	public CanvasPanel() {
		addComponentListener(this);
		refreshOffGraphics();
	}

	/***************************************************
	 * BufferStrategy関連
	 ****************************************************/

	private void initializeBufferStrategy() {
		createBufferStrategy(FILP_BUFFERSTRATEGY);
		refreshOffGraphics();
	}

	private void refreshOffGraphics() {
		offGraphics = getGraphics2D();
		offGraphics.setColor(Color.WHITE);
		offGraphics.fillRect(0, 0, getWidth(), getHeight());
	}

	private Graphics2D getGraphics2D() {
		Graphics2D g2d = (Graphics2D) getStrategy().getDrawGraphics();
		if (g2d != null) {
			return g2d;
		} else {
			return NULL_GRAPHICS;
		}
	}

	private void flip() {
		getStrategy().show();
	}

	private BufferStrategy getStrategy() {
		BufferStrategy strategy = getBufferStrategy();
		if (strategy != null) {
			return strategy;
		} else {
			return NULL_BUFFERSTRATEGY;
		}
	}

	/***************************************************
	 * Component Listener関連
	 ****************************************************/

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentResized(ComponentEvent e) {
		initializeBufferStrategy();
	}

	public void componentShown(ComponentEvent e) {
		initializeBufferStrategy();
	}

	/***************************************************
	 * 描画関連
	 ****************************************************/

	public void drawLine(Color color, double x1, double y1, double x2, double y2) {
		offGraphics.setColor(color);
		offGraphics.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
	}

	public void drawFillTriangle(Color color, double x1, double y1, double x2,
			double y2, double x3, double y3) {
		offGraphics.setColor(color);
		offGraphics.fillPolygon(new int[] { (int) x1, (int) x2, (int) x3 },
				new int[] { (int) y1, (int) y2, (int) y3 }, 3);
	}

	public void drawArc(Color color, double x, double y, double width,
			double height, double startAngle, double arcAngle) {
		offGraphics.setColor(color);
		offGraphics.drawArc((int) x, (int) y, (int) width, (int) height,
				(int) startAngle, (int) arcAngle);
	}

	public void drawFillArc(Color color, double x, double y, double width,
			double height, double startAngle, double arcAngle) {
		offGraphics.setColor(color);
		offGraphics.fillArc((int) x, (int) y, (int) width, (int) height,
				(int) startAngle, (int) arcAngle);
	}

	public void drawText(Color color, String text, double x, double y) {
		offGraphics.setColor(color);
		offGraphics.drawString(text, (int) x, (int) y);
	}

	public void drawText(Color color, String text, double x, double y, Font font) {
		FontMetrics fontMetrics = offGraphics.getFontMetrics(font);
		int topY = (int) y + fontMetrics.getAscent();

		//前処理
		offGraphics.setColor(color);
		Font originalFont = offGraphics.getFont();
		offGraphics.setFont(font);

		//処理
		offGraphics.drawString(text, (int) x, topY);

		//後処理
		offGraphics.setFont(originalFont);
	}

	public void drawImage(String filename, double x, double y, double width,
			double height) {
		BufferedImage image = BImageProvider.getInstance().getImage(filename);
		double scaleX = width / image.getWidth();
		double scaleY = height / image.getHeight();
		AffineTransform transform = AffineTransform.getScaleInstance(scaleX,
				scaleY);
		AffineTransformOp transformOp = new AffineTransformOp(transform,
				AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		drawImage(image, transformOp, x, y);
	}

	public void drawImage(String filename, double x, double y) {
		BufferedImage image = BImageProvider.getInstance().getImage(filename);
		drawImage(image, null, x, y);
	}

	private void drawImage(BufferedImage image, AffineTransformOp transformOp,
			double x, double y) {
		offGraphics.drawImage(image, transformOp, (int) x, (int) y);
	}

	/***************************************************
	 * フォント文字サイズの取得
	 ****************************************************/

	public int getTextWidth(String text, Font font) {
		FontMetrics fontMetrics = offGraphics.getFontMetrics(font);
		return fontMetrics.stringWidth(text);
	}

	public int getTextHeight(String text, Font font) {
		FontMetrics fontMetrics = offGraphics.getFontMetrics(font);
		return fontMetrics.getHeight();
	}

	/***************************************************
	 * 画像サイズの取得
	 ****************************************************/

	public int getImageWidth(String filename) {
		BufferedImage image = BImageProvider.getInstance().getImage(filename);
		return image.getWidth();
	}

	public int getImageHeight(String filename) {
		BufferedImage image = BImageProvider.getInstance().getImage(filename);
		return image.getHeight();
	}

	/***************************************************
	 * 更新関連
	 ****************************************************/

	public void update() {
		flip();
	}

	public void clear() {
		refreshOffGraphics();
	}

}

class NullBufferStrategy extends BufferStrategy {

	private static final Graphics2D NULL_GRAPHICS = (Graphics2D) (new BufferedImage(
			1, 1, BufferedImage.TYPE_3BYTE_BGR).createGraphics());

	public boolean contentsLost() {
		return false;
	}

	public boolean contentsRestored() {
		return false;
	}

	public BufferCapabilities getCapabilities() {
		return null;
	}

	public Graphics getDrawGraphics() {
		return NULL_GRAPHICS;
	}

	public void show() {
	}

}

/**
 * 画像読み込みクラス
 */
class BImageProvider {

	//定数
	private static final int DUMMY_IMAGE_FONT_SIZE = 12;

	private static final Font DUMMY_IMAGE_FONT = new Font("Dialog", Font.PLAIN,
			DUMMY_IMAGE_FONT_SIZE);

	/********************************
	 * SingleTonの実装
	 ********************************/

	private static BImageProvider instance;

	public static BImageProvider getInstance() {
		if (instance == null) {
			instance = new BImageProvider();
		}
		return instance;
	}

	//属性
	private Map images = new HashMap();

	/**
	 * コンストラクタ
	 */
	private BImageProvider() {
		super();
	}

	/**
	 * 画像を取得する（なければ新しく生成）
	 */
	public BufferedImage getImage(String filename) {
		if (!images.containsKey(filename)) {
			images.put(filename, prepareImage(filename));
		}
		return (BufferedImage) images.get(filename);
	}

	/********************************
	 * 以下，画像読み込み処理
	 ********************************/

	private BufferedImage prepareImage(String filename) {
		try {
			return loadImage(filename);
		} catch (Exception ex) {
			return createDummyImage(filename);
		}
	}

	private BufferedImage loadImage(String filename) throws IOException {
		File f = new File(filename);
		BufferedImage image = ImageIO.read(f);
		return image;
	}

	private BufferedImage createDummyImage(String filename) {
		//画像を生成する
		int width = DUMMY_IMAGE_FONT_SIZE * filename.length();
		int height = DUMMY_IMAGE_FONT_SIZE * 2;
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_4BYTE_ABGR);

		//ダミー画像を書き込む
		Graphics g = image.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width - 1, height - 1);
		g.setColor(Color.BLACK);
		g.setFont(DUMMY_IMAGE_FONT);
		g.drawRect(0, 0, width - 1, height - 1);
		g.drawString(filename, 10, height / 2);
		g.dispose();

		return image;
	}
}