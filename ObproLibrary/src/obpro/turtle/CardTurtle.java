package obpro.turtle;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

/*
 * CardTurtle.java
 * Created on 2007/10/08 by macchan
 * Copyright(c) 2007 CreW Project
 */

/**
 * CardTurtle
 */
public class CardTurtle extends ImageTurtle {

	/***************************************************************************
	 * 変数
	 **************************************************************************/

	private int fontsize = 18;

	private String text = null;

	// private boolean textDirty = false;

	protected ListTurtle parent;

	/***************************************************************************
	 * コンストラクタ
	 **************************************************************************/

	public CardTurtle() {
		this("Card Turtle");
	}

	public CardTurtle(int text) {
		this(new Integer(text));
	}

	public CardTurtle(double text) {
		this(new Double(text));
	}

	public CardTurtle(boolean text) {
		this(new Boolean(text));
	}

	public CardTurtle(String text) {
		this((Object) text);
	}

	public CardTurtle(Object text) {
		super();
		text(text.toString());
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

	public String getText() {
		return text;
	}

	public int getNumber() {
		try {
			return Integer.parseInt(getText());
		} catch (Exception ex) {
			return -1;
		}
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

		// 塗り
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width - 1, height - 1);

		// 枠線
		g.setColor(color());
		g.drawRect(0, 0, width - 1, height - 1);

		// 字
		g.setFont(f);
		g.drawString(text, margin, height * 4 / 5);

		g.dispose();

		// set new image
		super.setImage(image);

	}

}