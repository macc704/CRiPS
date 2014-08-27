package ronproeditor.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;

import ronproeditor.REApplication;
import ronproeditor.source.NonWrappingTextPane;
import clib.view.textpane.CJavaCodeKit;
import clib.view.textpane.CTextPaneUtils;

public class RESourceViewer extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextPane textPane;
	private LineNumberView lineNumberView;
	private JScrollPane scroll; // CheCoPro

	public RESourceViewer() {
		this("");
	}

	public RESourceViewer(String text) {
		initializeViews();
		setText(text);
	}

	public void initializeViews() {

		this.textPane = createTextPane();

		this.setLayout(new BorderLayout());

		// this.textArea = new JTextArea();
		// textArea.getDocument().putProperty(PlainDocument.tabSizeAttribute,
		// new Integer(4));
		textPane.setEditorKit(new CJavaCodeKit());
		this.lineNumberView = new LineNumberView(textPane);

		scroll = new JScrollPane(textPane);
		scroll.setRowHeaderView(lineNumberView);
		this.add(BorderLayout.CENTER, scroll);
	}

	protected JTextPane createTextPane() {
		return new NonWrappingTextPane();
	}

	public JTextPane getTextPane() {
		return textPane;
	}

	public String getText() {
		return textPane.getText();
	}

	public void setText(String text) {
		textPane.setText(text);
		textPane.setCaretPosition(0);
		CTextPaneUtils
				.setTabs(textPane, REApplication.WHITESPACE_COUNT_FOR_TAB);
	}

	public LineNumberView getLineNumberView() {
		return lineNumberView;
	}

	public JScrollPane getScroll() {
		return scroll;
	}

	/**
	 * 指定した行の文字列をハイライトします
	 * 
	 * @param lineNumber
	 */
	public void highlightLine(int lineNumber) {
		Highlighter highlighter = textPane.getHighlighter();

		try {
			Document doc = textPane.getDocument();
			String str = doc.getText(0, doc.getLength());

			String[] line = str.split("\n");
			int total = 0;
			for (int i = 0; i < lineNumber - 1; i++) {
				total += line[i].length() + 1; // +1 = 改行分
			}
			int width = line[lineNumber - 1].length();

			highlighter
					.addHighlight(total, total + width,
							new DefaultHighlighter.DefaultHighlightPainter(
									Color.YELLOW));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

	/***************************************************************************
	 * Class : LineNumberView
	 **************************************************************************/

	public class LineNumberView extends JComponent {

		private static final long serialVersionUID = 1L;

		private static final int MARGIN = 5;
		private static final int DEBUG_BUTTON_MARGIN = 20;

		// private final JTextArea text;
		private final JTextPane text;

		private FontMetrics fontMetrics;

		private int topInset;

		private int fontAscent;

		private int fontHeight;

		// public LineNumberView(JTextArea textArea) {
		public LineNumberView(JTextPane textArea) {
			text = textArea;
			this.setFontInformation(text);
			text.getDocument().addDocumentListener(new DocumentListener() {
				public void insertUpdate(DocumentEvent e) {
					repaint();
				}

				public void removeUpdate(DocumentEvent e) {
					repaint();
				}

				public void changedUpdate(DocumentEvent e) {
				}
			});
			text.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					revalidate();
					repaint();
				}
			});
			setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));

			this.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {

					int pos = getLineAtPoint(e.getY());
					pos = pos + 1; // 行数は１から始まるので、１を足す。

					// breakpoint@turkey 一時停止
					// if (application != null) {
					// application.setBreakPoint(pos);
					// }

				}
			});

		}

		public void setFontInformation(JTextPane text) {
			setFontInformation(text.getFont());
		}

		public void setFontInformation(Font font) {
			fontMetrics = getFontMetrics(font);
			fontHeight = fontMetrics.getHeight();
			fontAscent = fontMetrics.getAscent();
			topInset = text.getInsets().top;
		}

		public FontMetrics getFontMetrics() {
			return fontMetrics;
		}

		private int getComponentWidth() {
			return DEBUG_BUTTON_MARGIN + getLineTextWidth();
		}

		private int getLineTextWidth() {
			Document doc = text.getDocument();
			Element root = doc.getDefaultRootElement();
			int lineCount = root.getElementIndex(doc.getLength());
			int maxDigits = Math.max(3, String.valueOf(lineCount).length());
			return maxDigits * fontMetrics.stringWidth("0") + MARGIN * 2;
		}

		private int getLineAtPoint(int y) {
			Element root = text.getDocument().getDefaultRootElement();
			int pos = text.viewToModel(new Point(0, y));
			return root.getElementIndex(pos);
		}

		public Dimension getPreferredSize() {
			return new Dimension(getComponentWidth(), text.getHeight());
		}

		public void paintComponent(Graphics g) {

			Rectangle clip = g.getClipBounds();
			g.setColor(getBackground());
			g.fillRect(clip.x, clip.y, clip.width, clip.height);
			g.setColor(getForeground());
			int base = clip.y - topInset;
			int start = getLineAtPoint(base);
			int end = getLineAtPoint(base + clip.height);
			int y = topInset - fontHeight + fontAscent + start * fontHeight;
			for (int i = start; i <= end; i++) {
				String text = String.valueOf(i + 1);
				int x = DEBUG_BUTTON_MARGIN + getLineTextWidth() - MARGIN
						- fontMetrics.stringWidth(text);
				y = y + fontHeight;
				g.drawString(text, x, y);
			}

			int buttonSize = 10;
			int topMargin = topInset + fontAscent - buttonSize;
			g.setColor(Color.black);
			g.drawLine(DEBUG_BUTTON_MARGIN, 0, DEBUG_BUTTON_MARGIN,
					text.getHeight());
			g.setColor(Color.blue);

			// breakpoint@turkey 一時停止
			// for (int i = 1; i <= end; i++) {
			// if (application != null && application.isBreakPoint(i))
			// g.fillOval(3, topMargin + fontHeight * (i - 1), buttonSize,
			// buttonSize);
			// }

		}
	}

	/**
	 * フォントを変更する場合、必ずこのメソッドを通して変更する。 （でないと行番号がずれてしまう）
	 * 
	 * @param font
	 */
	public void changeFont(Font font) {
		getTextPane().setFont(font);
		getLineNumberView().setFontInformation(font);
	}

}
