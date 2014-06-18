package obpro.gui;

import java.awt.Color;
import java.awt.Font;

/**
 * キャンバスを表現するクラス
 * 
 * 各種書き込みメソッドにより，GUI描画を行なうことができます．
 * 実際の書き込み処理はCanvasPanelに委譲します．
 * （余計な処理をカプセル化していますので，中身を知りたい人はCanvasPanel(BWindow.java内)を参照せよ）
 * 
 * @author macchan
 * @version 2.0
 */
public class BCanvas {

	private CanvasPanel canvasPanel;
	private CanvasKeyEventHandler keyHandler;
	private CanvasMouseEventHandler mouseHandler;

	/**
	 * コンストラクタ
	 */
	public BCanvas(CanvasPanel canvasPanel, CanvasKeyEventHandler keyHandler,
			CanvasMouseEventHandler mouseHandler) {
		this.canvasPanel = canvasPanel;
		this.keyHandler = keyHandler;
		this.mouseHandler = mouseHandler;
	}

	/***************************************************
	 * 描画関連（第7,8回）
	 ****************************************************/

	/**
	 * 線を引きます
	 * 使用例:
	 * 座標(10, 10) から 座標(20, 20)へ黒い線を引く場合
	 * drawLine(Color.BLACK, 10, 10, 20, 20); 
	 */
	public void drawLine(Color color, double x1, double y1, double x2, double y2) {
		canvasPanel.drawLine(color, x1, y1, x2, y2);
	}

	/**
	 * 塗りつぶした三角形を書きます
	 * 使用例:
	 * 座標A(10, 10), 座標B(20, 20), 座標C(10,20)を頂点とする三角形を書く場合
	 * drawFillTriangle(Color.BLACK, 10, 10, 20, 20, 10, 20); 
	 */
	public void drawFillTriangle(Color color, double x1, double y1, double x2,
			double y2, double x3, double y3) {
		canvasPanel.drawFillTriangle(color, x1, y1, x2, y2, x3, y3);
	}

	/**
	 * 円弧を書きます
	 * 角度の単位は度です（0～360度)
	 * startAngleには弧を描き始める角度
	 *         90
	 * 180           0
	 *        270
	 * arcAngleには，弧全体の角度を書きます．弧は反時計回りに書かれます
	 * 使用例:
	 * 座標(10, 10)を左上として，高さ100, 幅100 の円弧を書く場合
	 * drawArc(Color.BLACK, 10, 10, 100, 100, 0, 360); 
	 */
	public void drawArc(Color color, double x, double y, double width,
			double height, double startAngle, double arcAngle) {
		canvasPanel.drawArc(color, x, y, width, height, startAngle, arcAngle);
	}

	/**
	 * 塗りつぶした円を書きます
	 * startAngleには弧を描き始める角度
	 *         90
	 * 180           0
	 *        270
	 * arcAngleには，弧全体の角度を書きます．弧は反時計回りに書かれます
	 * 使用例:
	 * 座標(10, 10)を左上として，高さ100, 幅100 左半分の円を書く場合
	 * drawFillArc(Color.BLACK, 10, 10, 100, 100, 90, 180); 
	 */
	public void drawFillArc(Color color, double x, double y, double width,
			double height, double startAngle, double arcAngle) {
		canvasPanel.drawFillArc(color, x, y, width, height, startAngle,
				arcAngle);
	}

	/***************************************************
	 * 描画関連（第9回以降）
	 ****************************************************/

	/**
	 * 文字を書きます
	 */
	public void drawText(Color color, String text, double x, double y) {
		canvasPanel.drawText(color, text, x, y);
	}

	/**
	 * （フォントサイズを指定して）文字を書きます
	 */
	public void drawText(Color color, String text, double x, double y, Font font) {
		canvasPanel.drawText(color, text, x, y, font);
	}

	/**
	 * 画像を書きます
	 */
	public void drawImage(String filename, double x, double y) {
		canvasPanel.drawImage(filename, x, y);
	}

	/**
	 * 画像を書きます
	 * （幅と高さを引数にとり，その大きさに拡大，縮小します）
	 */
	public void drawImage(String filename, double x, double y, double width,
			double height) {
		canvasPanel.drawImage(filename, x, y, width, height);
	}

	/***************************************************
	 * フォント文字サイズの取得
	 ****************************************************/

	/**
	 * テキストの幅を取得します
	 */
	public int getTextWidth(String text, Font font) {
		return canvasPanel.getTextWidth(text, font);
	}

	/**
	 * テキストの高さを取得します
	 */
	public int getTextHeight(String text, Font font) {
		return canvasPanel.getTextHeight(text, font);
	}

	/***************************************************
	 * 画像サイズの取得
	 ****************************************************/

	/**
	 * 画像の幅を取得します
	 */
	public int getImageWidth(String filename) {
		return canvasPanel.getImageWidth(filename);
	}

	/**
	 * 画像の高さを取得します
	 */
	public int getImageHeight(String filename) {
		return canvasPanel.getImageHeight(filename);
	}

	/***************************************************
	 * 更新関連
	 ****************************************************/

	/**
	 * キャンバス全体を白く塗りつぶします
	 */
	public void clear() {
		canvasPanel.clear();
	}

	/**
	 * キャンバスを更新（再描画）します
	 */
	public void update() {
		canvasPanel.update();
		keyHandler.update();
		mouseHandler.update();
	}

	/***************************************************
	 * キー入力関連
	 ****************************************************/

	/**
	 * 押されたキーのコードを取得します
	 */
	public int getKeyCode() {
		return keyHandler.key();
	}

	/**
	 * 何らかのキーが押されたかどうかを調べます（継続は含まない）
	 */
	public boolean isKeyDown() {
		return keyHandler.isKeyDown();
	}

	/**
	 * 指定されたキーが押されている状態かどうかを調べます（継続も含む）
	 */
	public boolean isKeyPressing(int keycode) {
		return keyHandler.isKeyPressing(keycode);
	}

	/***************************************************
	 * マウス入力関連
	 ****************************************************/

	/**
	 * マウスのX座標を取得します
	 */
	public int getMouseX() {
		return mouseHandler.mouseX();
	}

	/**
	 * マウスのY座標を取得します
	 */
	public int getMouseY() {
		return mouseHandler.mouseY();
	}

	/**
	 * マウスが押されているかどうか調べます
	 */
	public boolean isMouseDown() {
		return mouseHandler.isMouseDown();
	}

	/**
	 * 右のマウスボタンが押されているかどうか調べます
	 */
	public boolean isRightMouseDown() {
		return mouseHandler.isRightMouseDown();
	}

	/**
	 * 左のマウスボタンが押されているかどうか調べます
	 */
	public boolean isLeftMouseDown() {
		return mouseHandler.isLeftMouseDown();
	}

	/**
	 * クリックかどうか調べます
	 * (何回でのクリックも反応します）
	 */
	public boolean isClick() {
		return mouseHandler.isClick();
	}

	/**
	 * シングルクリックかどうか調べます
	 */
	public boolean isSingleClick() {
		return mouseHandler.isSingleClick();
	}

	/**
	 * ダブルクリックかどうか調べます
	 */
	public boolean isDoubleClick() {
		return mouseHandler.isDoubleClick();
	}

	/**
	 * ドラッグ中かどうか調べます
	 */
	public boolean isDragging() {
		return mouseHandler.isDragging();
	}

	/***************************************************
	 * その他
	 ****************************************************/

	/**
	 * 指定された秒数待ちます
	 */
	public void sleep(double seconds) {
		try {
			Thread.sleep((long) (seconds * 1000));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * キャンパスの幅を取得します
	 */
	public int getCanvasWidth() {
		return canvasPanel.getWidth();
	}

	/**
	 * キャンパスの高さを取得します
	 */
	public int getCanvasHeight() {
		return canvasPanel.getHeight();
	}
}