/*
 * HScaleComboBox.java
 * Copyright(c) 2005 CreW Project. All rights reserved.
 */
package ronproeditor.views;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.JComboBox;

import ronproeditor.views.CFontComboBox.FontComponent;

/**
 * Class HFontComboBox
 * 
 * @author macchan
 * @version $Id: HFontComboBox.java,v 1.3 2009/09/10 03:48:32 macchan Exp $
 */
public class CFontComboBox extends JComboBox<FontComponent> {

	public static final long serialVersionUID = 1L;

	private static int DEFAULT_SIZE = 12;

	public CFontComboBox() {
		Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAllFonts();
		for (int i = 0; i < fonts.length; i++) {
			Font font12 = new Font(fonts[i].getName(), fonts[i].getStyle(),
					DEFAULT_SIZE);
			addItem(new FontComponent(font12));
		}
		setMaximumSize(new Dimension(120, 20));
	}

	public void setSelectedFont(Font f) {
		setSelectedFontByName(f.getName());
	}

	public void setSelectedFontByName(String fontName) {
		int curIndex = getSelectedIndex();
		int newIndex = getIndex(fontName);

		if (newIndex >= 0 && curIndex != newIndex) {
			setSelectedIndex(newIndex);
		}
	}

	private int getIndex(String fontName) {
		int count = getItemCount();
		for (int i = 0; i < count; i++) {
			Font font = getItemAt(i).getFont();
			if (font.getName().equals(fontName)) {
				return i;
			}
		}
		return -1;
	}

	public Font getSelectedFont() {
		try {
			Object selectedItem = getSelectedItem();
			if (selectedItem != null) {
				return ((FontComponent) selectedItem).getFont();
			} else {
				return getFont();
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	class FontComponent {
		private Font f;

		public FontComponent(Font f) {
			this.f = f;
		}

		public Font getFont() {
			return f;
		}

		public String toString() {
			return f.getName();
		}
	}

}