package ronproeditor.helpers;

import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import ronproeditor.REApplication;
import clib.view.textpane.CTextPaneUtils;

/**
 * コンソールをエミュレートするTextAreaクラス TextPane版
 * 
 * @author macchan
 * @version $Id: ConsoleTextPane.java,v 1.4 2010/03/10 15:44:28 turkey Exp $
 */
public class ConsoleTextPane extends JTextPane implements IConsole {

	private static final long serialVersionUID = 1L;

	// 関連
	private PrintStream consoleToStream = null;
	private JTextAreaInputStream in = new JTextAreaInputStream(this);
	private JTextAreaPrintStream out = new JTextAreaPrintStream(this,
			Color.BLACK);
	private JTextAreaPrintStream err = new JTextAreaPrintStream(this, Color.RED);

	/**
	 * コンストラクタ System.out, System.errのつなぎかえを行います
	 */
	public ConsoleTextPane() {
		this.initialize();
		in.refresh();
	}

	public InputStream getIn() {
		return in;
	}

	public PrintStream getOut() {
		return out;
	}

	public PrintStream getErr() {
		return err;
	}

	public void setConsoleToStream(PrintStream consoleToStream) {
		this.consoleToStream = consoleToStream;
	}

	public PrintStream getConsoleToStream() {
		return consoleToStream;
	}

	public void toLast() {
		setCaretPosition(getDocument().getLength());
		repaint();
	}

	/**
	 * 初期化します
	 */
	private void initialize() {
		CTextPaneUtils
				.setTabs(this, REApplication.WHITESPACE_COUNT_FOR_TAB * 2);// コンパイラのエラーの出し方にあわせる

		// キーによる、カーソルの移動を阻止する
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_HOME:
				case KeyEvent.VK_END:
				case KeyEvent.VK_PAGE_UP:
				case KeyEvent.VK_PAGE_DOWN:
					e.consume();
				}
			}
		});

		addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				toLast();
			}
		});

		// マウスによる、カーソルの移動を阻止する
		this.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				toLast();
			}

			public void mouseEntered(MouseEvent e) {
				toLast();
			}

			public void mouseExited(MouseEvent e) {
				toLast();
			}

			public void mousePressed(MouseEvent e) {
				toLast();
			}

			public void mouseReleased(MouseEvent e) {
				toLast();
			}
		});
	}

	public void println(String str) {
		out.println(str);
	}
}

/**
 * JTextAreaをコンソールにするための System.out エミュレータ
 * 
 * printlnメソッドと、printメソッド、flushメソッドのみオーバーライドします
 * その他は標準のPrintStreamの機能を使い、System.outにコネクトされます
 * 
 * @author macchan
 * @version $Id: ConsoleTextPane.java,v 1.4 2010/03/10 15:44:28 turkey Exp $
 */
class JTextAreaPrintStream extends PrintStream {

	// 定数
	private static final char CR = '\n';

	// 状態
	private StringBuffer buf = new StringBuffer();

	// フラグ
	private boolean invokeLater = false; // flush動作をSwingスレッドで処理するかどうか
	private boolean caretUpdate = true; // flush時にカーソルを移動するかどうか

	// 関連
	private ConsoleTextPane textArea = null;

	private MutableAttributeSet attribute = new SimpleAttributeSet();

	/**
	 * コンストラクタ
	 */
	public JTextAreaPrintStream(ConsoleTextPane textarea, Color color) {
		super(System.out); // System.outは仮コネクト
		this.textArea = textarea;
		StyleConstants.setForeground(attribute, color);
	}

	/***********************************
	 * print関連　（オーバーライド）
	 ***********************************/

	public void print(Object o) {
		this.printImpl(o.toString());
	}

	public void print(String s) {
		this.printImpl(s);
	}

	public void print(int i) {
		this.printImpl(new Integer(i).toString());
	}

	public void print(long l) {
		this.printImpl(new Long(l).toString());
	}

	public void print(char c) {
		this.printImpl(new Character(c).toString());
	}

	public void print(char[] c) {
		this.printImpl(new String(c));
	}

	public void print(boolean b) {
		this.printImpl(new Boolean(b).toString());
	}

	public void print(float s) {
		this.printImpl(new Float(s).toString());
	}

	public void print(double d) {
		this.printImpl(new Double(d).toString());
	}

	/***********************************
	 * println関連　（オーバーライド）
	 ***********************************/

	public void println() {
		this.printlnImpl("");
	}

	public void println(Object o) {
		this.printlnImpl(o.toString());
	}

	public void println(String s) {
		this.printlnImpl(s);
	}

	public void println(int i) {
		this.printlnImpl(new Integer(i).toString());
	}

	public void println(long l) {
		this.printlnImpl(new Long(l).toString());
	}

	public void println(char c) {
		this.printlnImpl(new Character(c).toString());
	}

	public void println(char[] c) {
		this.printlnImpl(new String(c));
	}

	public void println(boolean b) {
		this.printlnImpl(new Boolean(b).toString());
	}

	public void println(float s) {
		this.printlnImpl(new Float(s).toString());
	}

	public void println(double d) {
		this.printlnImpl(new Double(d).toString());
	}

	/***********************************
	 * flush関連　（オーバーライド）
	 ***********************************/

	/**
	 * textareaにStreamを出力します。
	 */
	public synchronized void flush() {
		this.flushImpl();
	}

	/***********************************
	 * 実装関連
	 ***********************************/

	/**
	 * print動作の実装です バッファにため,flushします(自動flush)
	 */
	private void printImpl(String s) {
		this.buf.append(s);
		this.flush();
	}

	/**
	 * println動作の実装です
	 */
	private void printlnImpl(String s) {
		this.printImpl(s + CR);
	}

	/**
	 * flush動作の実装です
	 */
	private void flushImpl() {
		// 前処理
		final String s = this.buf.toString();

		// 本処理(writeTextで書き込む)
		if (this.invokeLater) { // Swingスレッドで書き込み
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					writeText(s);
				}
			});
		} else { // このスレッドで書き込み(このまま実行)
			writeText(s);
		}

		// 後処理
		this.buf = new StringBuffer();

	}

	/***********************************
	 * テキストエリアに書き込み関連
	 ***********************************/

	/**
	 * テキストをテキストエリアに書き込みます
	 */
	private void writeText(String s) {

		if (this.textArea == null) {
			throw new NullPointerException("textarea is null");
		}

		// テキストエリアに書き込む
		textArea.toLast();
		int caret = this.textArea.getCaretPosition();
		int len = this.textArea.getDocument().getLength();
		int pos = caret < len ? caret : len;
		try {
			this.textArea.getDocument().insertString(pos, s, attribute);
			// 自動カーソル移動処理
			if (this.caretUpdate) {
				this.textArea.setCaretPosition(pos + s.length());
				this.textArea.repaint();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/***********************************
	 * Setter, Getter関連
	 ***********************************/

	public void setInvokeLater(boolean invokeLater) {
		this.invokeLater = invokeLater;
	}

	public boolean isInvokeLater() {
		return this.invokeLater;
	}

	public void setCaretUpdate(boolean caretUpdate) {
		this.caretUpdate = caretUpdate;
	}

	public boolean isCaretUpdate() {
		return this.caretUpdate;
	}

}

/**
 * JTextAreaをコンソールにするための System.in エミュレータ
 * 
 * 継承しているPrintableInputStreamのprintln機能を利用して書き込みます
 * Blockの仕組みはPrintableInputStream依存です
 * 
 * @author macchan
 * @version $Id: ConsoleTextPane.java,v 1.4 2010/03/10 15:44:28 turkey Exp $
 */
class JTextAreaInputStream extends PrintableInputStream {

	// 定数
	private static final int NULL = -1;

	// 状態
	private int inputStartCaretPosition = NULL; // 入力が始まったカーソル位置を覚えておく

	// 関連
	private ConsoleTextPane textArea = null;

	/**
	 * Constructor for JTextAreaInputStream.
	 */
	public JTextAreaInputStream(ConsoleTextPane textArea) {
		this.textArea = textArea;
		this.initialize();
	}

	public void refresh() {
		this.inputStartCaretPosition = NULL;
	}

	/**
	 * 初期化します
	 */
	private void initialize() {
		this.textArea.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				handleKeyPressed(e);
			}
		});
		this.textArea.addInputMethodListener(new InputMethodListener() {
			public void caretPositionChanged(InputMethodEvent event) {
				// donothing
			}

			public void inputMethodTextChanged(InputMethodEvent event) {
				memorizeStartPosition();
			}
		});
	}

	public synchronized void memorizeStartPosition() {
		// 最初の入力なら、位置を覚えておく
		if (this.inputStartCaretPosition == NULL) {
			this.inputStartCaretPosition = this.textArea.getCaretPosition();
		}
	}

	/**
	 * キーが押された時の処理
	 */
	public synchronized void handleKeyPressed(KeyEvent e) {
		this.memorizeStartPosition();

		// Enterなら、書き込み動作を行う
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			// e.consume();
			this.writeLine();
			this.inputStartCaretPosition = NULL;
			this.textArea.repaint();
		}
	}

	private void writeLine() {
		try {
			int inputEndCaretPosition = this.textArea.getCaretPosition();
			int len = inputEndCaretPosition - this.inputStartCaretPosition;
			String lineString = null;
			if (len <= 0) {
				lineString = "";
			} else {
				lineString = this.textArea.getText(
						this.inputStartCaretPosition, len);
			}

			// 書き込みます
			if (textArea.getConsoleToStream() != null) {
				textArea.getConsoleToStream().println(lineString);
				textArea.getConsoleToStream().flush();
			} else {
				super.println(lineString);
			}
		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}
	}

}

/**
 * Stringを書き込めるInputStreamエミュレータ
 * 
 * write(String)メソッドで一行ずつ書き込むことができます。
 * このクラスを利用する読み手は、read()したときにbufferに書き込まれていなければ、 書き込まれるまでブロックします
 * 
 * @author macchan
 * @version $Id: ConsoleTextPane.java,v 1.4 2010/03/10 15:44:28 turkey Exp $
 */
class PrintableInputStream extends InputStream {

	// 定数
	private static final char CR = '\n';

	private static final char END_SYMBOL = '\0';

	private static final int END_CODE = -1;

	// 状態
	private byte[] buf = null; // 文字列をbyte列で表現するバッファ

	private int cursor = 0; // byte列をどこまで読んだか保存するカーソル

	/**
	 * コンストラクタ
	 */
	public PrintableInputStream() {
	}

	/**
	 * バッファに書き込みます。 もしブロック中のスレッドがいたら、ブロックを解除します
	 */
	public synchronized void println(String s) {
		// 改行と終了記号を加える
		s = s + CR + END_SYMBOL;

		// 対象文字列をbyte列のバッファに変換する
		byte[] stringBuf = s.getBytes();

		// バッファが終わりに達していたら、そのままバッファに。
		if (this.isBufferEnd()) {
			this.buf = stringBuf;
		}
		// バッファが終わりに達していなかったら、継ぎ足して、新しいバッファを作る
		else {
			int remain = buf.length - cursor; // 例えば8,4の場合、0,1,2,3と4つ終了しているから、残り４つ。
			int newbufsize = remain + stringBuf.length;
			byte[] newBuf = new byte[newbufsize];
			System.arraycopy(buf, cursor, newBuf, 0, remain);
			System.arraycopy(stringBuf, 0, newBuf, remain, stringBuf.length);
			// 例えば13,4の場合、13-4=9こコピーしてその次だから9番目。
			buf = newBuf;
		}

		// カーソルを戻して、ブロックを解除する
		cursor = 0;
		notify();
	}

	/***************************************
	 * オーバーライド
	 ***************************************/

	/**
	 * ブロックせずに読み込めるバイト数を返します
	 */
	public synchronized int available() throws IOException {
		return buf == null ? 0 : buf.length - cursor;
	}

	/**
	 * バッファから、一文字読み込みます
	 */
	public synchronized int read() throws IOException {
		int readChar = END_CODE;

		try {
			// 読み込めなければブロック
			if (isBufferEnd()) {
				wait();
			}

			// 一文字読み込む
			readChar = this.buf[cursor];
			this.cursor++;

			// 終わりだったら-1
			if ((char) readChar == END_SYMBOL) {
				return END_CODE;
			}

		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}

		return readChar;
	}

	/***************************************
	 * private
	 ***************************************/

	/**
	 * バッファが終了しているかどうか調べます
	 */
	private synchronized boolean isBufferEnd() {
		return this.buf == null ? true : this.cursor >= this.buf.length;
	}
}
