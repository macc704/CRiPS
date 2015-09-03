package obpro.turtle;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

/*
 * プログラム名： ボタンタートル
 * 作成者： MegumiAraki
 * バージョン： 1.0 (20071212)
 */

public class ButtonTurtle extends ImageTurtle {

	public enum State {
		REREASED, PRESSED, CLICKED
	};

	/***************************************************************************
	 * 変数
	 **************************************************************************/

	private int fontsize = 18;
	private String text = null;
	private State state = State.REREASED;

	// private boolean textDirty = false;

	/***************************************************************************
	 * コンストラクタ
	 **************************************************************************/

	public ButtonTurtle() {
		this("Button Turtle");
	}

	public ButtonTurtle(int text) {
		this(new Integer(text));
	}

	public ButtonTurtle(double text) {
		this(new Double(text));
	}

	public ButtonTurtle(boolean text) {
		this(new Boolean(text));
	}

	public ButtonTurtle(String text) {
		this((Object) text);
	}

	public ButtonTurtle(Object text) {
		super();
		text(text.toString());
	}

	/***************************************************************************
	 * ボタン関連
	 **************************************************************************/

	public boolean isClicked() {
		return (state == State.CLICKED);
	}

	private void checkButtonStatus() {
		if (state != null) {
			switch (state) {
			case REREASED:
				if (isPressed()) {
					state = State.PRESSED;
					resetImage();
				}
				break;
			case PRESSED:
				if (!isPressed()) {
					state = State.CLICKED;
					resetImage();
				}
				break;
			case CLICKED:
				if (isPressed()) {
					state = State.PRESSED;
					resetImage();
				} else {
					state = State.REREASED;
					resetImage();
				}
				break;
			default:
				throw new RuntimeException();
			}
		}
	}

	private boolean isPressed() {
		return Turtle.defaultTurtle.mouseDown()
				&& this.contains(Turtle.defaultTurtle.mouseX(),
						Turtle.defaultTurtle.mouseY());
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

	public void draw(Graphics2D g) {
		// System.out.println(this);
		super.draw(g);
		checkButtonStatus();
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

		// calculate size
		int height = fontsize;
		Font f = new Font(DEFAULT_FONT, Font.PLAIN, height);
		FontMetrics fm = window.getFontMetrics(f);
		int width = SwingUtilities.computeStringWidth(fm, text);

		// draw
		width += margin * 2;
		height += margin * 2;
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setColor(color());

		// 背景
		Color c = g.getColor();
		if (state == State.PRESSED) {
			g.setColor(Color.BLACK);
		} else if (state == State.REREASED) {
			g.setColor(Color.WHITE);
		} else if (state == State.CLICKED) {
			g.setColor(Color.RED);
		} else {
			throw new RuntimeException();
		}
		g.fillRect(1, 1, width - 2, height - 2);
		g.setColor(c);

		// 枠線
		g.drawRect(1, 1, width - 2, height - 2);

		// 字
		g.setFont(f);
		g.drawString(text, margin, height * 4 / 5);/* fm.getAscent() */

		g.dispose();

		// set new image
		super.setImage(image);

	}

}