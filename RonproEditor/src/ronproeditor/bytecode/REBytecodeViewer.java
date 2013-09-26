package ronproeditor.bytecode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

import ronproeditor.REApplication;
import ronproeditor.source.NonWrappingTextPane;
import clib.view.textpane.CJavaCodeKit;

public class REBytecodeViewer extends JPanel {

	private static final long serialVersionUID = 1L;

	private NonWrappingTextPane sourceTextPane = new NonWrappingTextPane();
	private JScrollPane sourceScrollPane = new JScrollPane(sourceTextPane,
			JScrollPane.VERTICAL_SCROLLBAR_NEVER,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	private NonWrappingTextPane jasminTextPane = new NonWrappingTextPane();
	private JScrollPane jasminScrollPane = new JScrollPane(jasminTextPane,
			JScrollPane.VERTICAL_SCROLLBAR_NEVER,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	private NonWrappingTextPane japaTextPane = new NonWrappingTextPane();
	private JScrollPane japaScrollPane = new JScrollPane(japaTextPane,
			JScrollPane.VERTICAL_SCROLLBAR_NEVER,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

	private JSplitPane split1 = new JSplitPane();
	private JSplitPane split2 = new JSplitPane();

	private LineNumberView lineNumberView;

	private Map<Integer, Integer> lineRelationShip;

	public REBytecodeViewer() {
		super();
		initializeViews();
	}

	public void initializeViews() {
		this.setLayout(new BorderLayout());

		sourceTextPane.setEditorKit(new CJavaCodeKit());
		sourceTextPane.setEditable(false);
		jasminTextPane.setEditable(false);
		japaTextPane.setEditable(false);

		split1.add(sourceScrollPane, JSplitPane.LEFT);
		split1.add(split2, JSplitPane.RIGHT);
		split2.add(jasminScrollPane, JSplitPane.LEFT);
		split2.add(japaScrollPane, JSplitPane.RIGHT);
		JScrollPane scroll = new JScrollPane(split1);

		this.lineNumberView = new LineNumberView(sourceTextPane);
		// scroll.setRowHeaderView(lineNumberView);
		sourceScrollPane.setRowHeaderView(lineNumberView);
		this.add(scroll, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 3));
		addLabel(panel, "Java Source");
		addLabel(panel, "Bytecode (Jasmin Style)");
		addLabel(panel, "Bytecode (言霊形式)");
		this.add(panel, BorderLayout.NORTH);
		// sourceScrollPane.setColumnHeaderView(new JLabel("Java Source"));
		// jasminScrollPane.setColumnHeaderView(new
		// JLabel("Bytecode(Jasmin形式)"));
		// japaScrollPane.setColumnHeaderView(new JLabel("Bytecode(日本語)"));
	}

	private void addLabel(JPanel panel, String text) {
		JLabel label = new JLabel(text, JLabel.CENTER);
		label.setFont(label.getFont().deriveFont(12));
		panel.add(label);
	}

	public void setDividerLocation(int w) {
		split1.setDividerLocation((int) (w / 3.1));
		split2.setDividerLocation((int) (w / 3.1));
	}

	public void setTexts(String file1, String file2, String file3) {
		setText(sourceTextPane, file1);
		setText(jasminTextPane, file2);
		setText(japaTextPane, file3);
	}

	private void setText(JTextPane textPane, String text) {
		textPane.setText(text);
		textPane.setCaretPosition(0);
		setTabs(textPane, REApplication.WHITESPACE_COUNT_FOR_TAB);
	}

	public void setLineRelationship(Map<Integer, Integer> lineRelationship) {
		this.lineRelationShip = lineRelationship;
	}

	public void setTabs(JTextPane textPane, int charactersPerTab) {
		FontMetrics fm = textPane.getFontMetrics(textPane.getFont());
		int charWidth = fm.charWidth('w');
		int tabWidth = charWidth * charactersPerTab;

		TabStop[] tabs = new TabStop[10];

		for (int j = 0; j < tabs.length; j++) {
			int tab = j + 1;
			tabs[j] = new TabStop(tab * tabWidth);
		}

		TabSet tabSet = new TabSet(tabs);
		SimpleAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setTabSet(attributes, tabSet);
		int length = textPane.getDocument().getLength();
		textPane.getStyledDocument().setParagraphAttributes(0, length,
				attributes, false);
	}

	/**
	 * フォントを変更する場合、必ずこのメソッドを通して変更する。 （でないと行番号がずれてしまう）
	 * 
	 * @param font
	 */
	public void changeFont(Font font) {
		this.sourceTextPane.setFont(font);
		this.lineNumberView.setFontInformation(font);
	}

	private class LineNumberView extends JComponent {

		private static final long serialVersionUID = 1L;

		private static final int MARGIN = 5;

		// private final JTextArea text;
		private final JTextPane text;

		private FontMetrics fontMetrics;

		private int topInset;

		private int fontAscent;

		private int fontHeight;

		// public LineNumberView(JTextArea sourceTextPane) {
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

		private int getComponentWidth() {
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
				// String text = String.valueOf(i + 1);
				String text = "*";
				int line = i + 1;
				if (lineRelationShip != null
						&& lineRelationShip.containsKey(line)) {
					text = String.valueOf(lineRelationShip.get(line));
				}
				int x = getComponentWidth() - MARGIN
						- fontMetrics.stringWidth(text);
				y = y + fontHeight;
				g.drawString(text, x, y);
			}
		}
	}

}
