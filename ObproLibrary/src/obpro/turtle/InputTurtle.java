package obpro.turtle;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;


/**
 * プログラム名： 入力タートル
 * 作成者： MegumiAraki
 * バージョン： 1.0 (20071212)
 */

/**
 * InputTurtle
 */
public class InputTurtle extends ImageTurtle {

	/***************************************************************************
	 * 変数
	 **************************************************************************/

	private boolean active = true;
	private boolean isJapaneseMode = false;
	private int fontsize = 20;

	private String text = null;

	private TextConverter textConverter = new TextConverter();

	// private boolean textDirty = false;

	/***************************************************************************
	 * 定数
	 **************************************************************************/
	// キーコード
	private static int BIGIN_ALPHABET = 65;// aのキーコード
	private static int BIGIN_NUMBER = 48;// 0のキーコード

	/***************************************************************************
	 * コンストラクタ
	 **************************************************************************/

	public InputTurtle() {
		this("");
	}

	public InputTurtle(int text) {
		this(new Integer(text));
	}

	public InputTurtle(double text) {
		this(new Double(text));
	}

	public InputTurtle(boolean text) {
		this(new Boolean(text));
	}

	public InputTurtle(String text) {
		this((Object) text);
	}

	public InputTurtle(Object text) {
		super();
		text(text.toString());
		Turtle.defaultTurtle.inputTurtles.add(this);
	}

	/***************************************************************************
	 * 入力関連
	 **************************************************************************/

	// テキストの入力を受け付ける
	protected void captureText(KeyEvent evt) {
		if (!active) {
			return;
		}

		String newText = this.text;
		if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			newText = choppedText();
		} else {
			// newText += getSingleByteCharacter(Turtle.defaultTurtle.key());
			// newText += evt.getKeyChar();
			newText += getSingleByteCharacter(evt.getKeyCode());
		}

		if (isJapaneseMode) {
			newText = textConverter.convert(newText);
		}

		text(newText);
		resetImage();
	}

	public void clearText() {
		text("");
	}

	private String choppedText() {
		String text = text();
		if (text.length() != 0) {
			return text = text.substring(0, text.length() - 1);
		} else {
			return text;
		}
	}

	private String getSingleByteCharacter(int keyCode) {
		if (keyCode == -1) {
			return "";
		}
		// String text = KeyEvent.getKeyText(keyCode);
		// return text.toLowerCase();

		if (keyCode >= BIGIN_ALPHABET && keyCode <= (BIGIN_ALPHABET + 25)) {// アルファベット
			char key = (char) (keyCode + 32);
			return String.valueOf(key);
		} else if (keyCode >= BIGIN_NUMBER && keyCode <= (BIGIN_NUMBER + 9)) {// 数字
			int number = keyCode - 48;
			return String.valueOf(number);
		} else if (keyCode == KeyEvent.VK_COMMA) {// コンマ
			return ",";
		} else if (keyCode == KeyEvent.VK_PERIOD) {// ピリオド
			return ".";
		} else if (keyCode == KeyEvent.VK_MINUS) {
			return "ー";
		} else if (keyCode == KeyEvent.VK_EXCLAMATION_MARK) {
			return "!";
		} else if (keyCode == KeyEvent.VK_SLASH) {
			return "?";
		} else if (keyCode == KeyEvent.VK_ESCAPE) {
			isJapaneseMode = !isJapaneseMode;
			return "";
		}
		// else if (keyCode == KeyEvent.VK_FULL_WIDTH) {
		// toJapaneseMode();
		// return "";
		// } else if (keyCode == KeyEvent.VK_HALF_WIDTH) {
		// toEnglishMode();
		// return "";
		// }
		else {
			return "";
		}
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return this.active;
	}

	public void toJapaneseMode() {
		this.isJapaneseMode = true;
	}

	public void toEnglishMode() {
		this.isJapaneseMode = false;
	}

	/***************************************************************************
	 * setter & getter
	 **************************************************************************/

	public void text(int text) {
		text(new Integer(text));
	}

	public void text(double text) {
		text(new Double(text));
	}

	public void text(boolean text) {
		text(new Boolean(text));
	}

	public void text(String text) {
		text((Object) text);
	}

	public void text(Object text) {
		if (text != null) {
			this.text = text.toString();
		} else {
			this.text = "null";
		}

		resetImage();
	}

	public String text() {
		return text;
	}

	public void fontsize(int fontsize) {
		this.fontsize = fontsize;
		resetImage();
	}

	public int fontsize() {
		return fontsize;
	}

	/**
	 * override
	 */
	public void color(Color penColor) {
		super.color(penColor);
		resetImage();
	}

	/***************************************************************************
	 * Override
	 **************************************************************************/
	//
	// public void draw(Graphics2D g) {
	// if (textDirty) {
	// resetImage();
	// textDirty = false;
	// }
	// super.draw(g);
	// }
	/***************************************************************************
	 * reset image
	 **************************************************************************/
	int margin = 5;

	private synchronized void resetImage() {

		// save original location
		int orgX = getX() - getWidth() / 2;
		int orgY = getY() - getHeight() / 2;

		// calculate size
		int height = fontsize;
		Font f = new Font("Dialog", Font.PLAIN, height);
		FontMetrics fm = window.getFontMetrics(f);
		int width = SwingUtilities.computeStringWidth(fm, text);

		// draw
		width += margin * 2;
		height += margin * 2;
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setColor(color());

		// 枠線
		g.drawRect(1, 1, width - 2, height - 2);

		// 字
		g.setFont(f);
		g.drawString(text, margin, height * 4 / 5);

		g.dispose();

		// set new image
		super.setImage(image);

		// set to original x, y
		x(orgX + getWidth() / 2);
		y(orgY + getHeight() / 2);

	}

	public void die() {
		Turtle.defaultTurtle.inputTurtles.remove(this);
	}
}