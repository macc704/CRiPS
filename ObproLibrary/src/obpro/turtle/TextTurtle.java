package obpro.turtle;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

/*
 * TextTurtle.java
 * Created on 2011/12/17
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */

/**
 * Class TextTurtle.
 * 
 * @author macchan
 */
public class TextTurtle extends ImageTurtle {

	/******************************************
	 * 変数
	 ******************************************/

	private int fontsize = 32;

	private String text = null;

	// private boolean textDirty = false;

	/******************************************
	 * コンストラクタ
	 ******************************************/

	public TextTurtle() {
		this("Text Turtle");
	}

	public TextTurtle(int text) {
		this(new Integer(text));
	}

	public TextTurtle(double text) {
		this(new Double(text));
	}

	public TextTurtle(boolean text) {
		this(new Boolean(text));
	}

	public TextTurtle(String text) {
		this((Object) text);
	}

	public TextTurtle(Object text) {
		super();
		text(text.toString());
	}

	/******************************************
	 * setter & getter
	 ******************************************/

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

	/******************************************
	 * Override
	 ******************************************/
	//
	// public void draw(Graphics2D g) {
	// if (textDirty) {
	// resetImage();
	// textDirty = false;
	// }
	// super.draw(g);
	// }
	/******************************************
	 * reset image
	 ******************************************/

	private synchronized void resetImage() {

		// calculate size
		int height = fontsize;

		Font f = new Font(DEFAULT_FONT, Font.PLAIN, height);
		FontMetrics fm = window.getFontMetrics(f);
		int width = SwingUtilities.computeStringWidth(fm, text);
		if (width == 0) {
			width = 1;
		}
		if (height == 0) {
			height = 1;
		}

		// draw
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setColor(color());
		g.setFont(f);
		g.drawString(text, 0, height * 4 / 5);/* fm.getAscent() */

		// set new image
		super.setImage(image);

	}

}