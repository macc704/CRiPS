package ronproeditor.views;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.Position;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import clib.view.textpane.CJavaCodeKit;

public class REJavaCodeKit extends CJavaCodeKit {

	private static final long serialVersionUID = 1L;

	// http://terai.xrea.jp/Swing/ParagraphMark.html

	@Override
	public ViewFactory getViewFactory() {
		return new MyViewFactory();
		// return super.getViewFactory();
	}

	class MyViewFactory implements ViewFactory {

		public View create(Element elem) {
			String kind = elem.getName();
			if (kind != null) {
				if (kind.equals(AbstractDocument.ContentElementName)) {
					return new MyBigWhitespaceLabelView(elem);
				} else if (kind.equals(AbstractDocument.ParagraphElementName)) {
					// return new MyParagraphView(elem);
					return new ParagraphView(elem);
				} else if (kind.equals(AbstractDocument.SectionElementName)) {
					return new BoxView(elem, View.Y_AXIS);
				} else if (kind.equals(StyleConstants.ComponentElementName)) {
					return new ComponentView(elem);
				} else if (kind.equals(StyleConstants.IconElementName)) {
					return new IconView(elem);
				}
			}
			return new LabelView(elem);
		}
	}

	class MyParagraphView extends ParagraphView {
		private final Color pc = new Color(120, 130, 110);

		public MyParagraphView(Element elem) {
			super(elem);
		}

		@Override
		public void paint(Graphics g, Shape allocation) {
			super.paint(g, allocation);
			paintCustomParagraph(g, allocation);
		}

		private void paintCustomParagraph(Graphics g, Shape a) {
			try {
				Shape paragraph = modelToView(getEndOffset(), a,
						Position.Bias.Backward);
				Rectangle r = (paragraph == null) ? a.getBounds() : paragraph
						.getBounds();
				int x = r.x;
				int y = r.y;
				int h = r.height;
				Color old = g.getColor();
				g.setColor(pc);
				// paragraph mark
				g.drawLine(x + 1, y + h / 2, x + 1, y + h - 4);
				g.drawLine(x + 2, y + h / 2, x + 2, y + h - 5);
				g.drawLine(x + 3, y + h - 6, x + 3, y + h - 6);
				g.setColor(old);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class MyBigWhitespaceLabelView extends LabelView {
		private final Color pc = Color.red;

		public MyBigWhitespaceLabelView(Element elem) {
			super(elem);
		}

		@Override
		public void paint(Graphics g, Shape a) {
			super.paint(g, a);
			paintCustom(g, a);
		}

		private void paintCustom(Graphics g, Shape a) {
			Graphics2D g2 = (Graphics2D) g;
			Rectangle alloc = (a instanceof Rectangle) ? (Rectangle) a : a
					.getBounds();
			FontMetrics fontMetrics = g.getFontMetrics();
			int spaceW = fontMetrics.stringWidth(" ");
			int bigSpaceW = fontMetrics.stringWidth("　");
			int sumOfTabs = 0;
			String text = getText(getStartOffset(), getEndOffset()).toString();
			for (int i = 0; i < text.length(); i++) {
				String s = text.substring(i, i + 1);
				int previousStringWidth = fontMetrics.stringWidth(text
						.substring(0, i)) + sumOfTabs;
				int sx = alloc.x + previousStringWidth;
				int sy = alloc.y + alloc.height - fontMetrics.getDescent();
				if ("　".equals(s)) {
					int left = sx;
					int right = sx + bigSpaceW;
					int top = alloc.y;
					int bottom = sy;
					g2.setPaint(pc);
					g2.drawLine(left, top, right, top);
					g2.drawLine(left, bottom, right, bottom);
					g2.drawLine(left, top, left, bottom);
					g2.drawLine(right, top, right, bottom);
					g2.drawLine(left, top, right, bottom);
					g2.drawLine(right, top, left, bottom);
				} else if ("\t".equals(s)) {
					float tabWidth = (int) getTabExpander().nextTabStop(
							(float) sx, i)
							- sx - spaceW;// なぜspace分引くのかは不明．
					sumOfTabs += (int) tabWidth;
				}
			}
		}
	}
}
