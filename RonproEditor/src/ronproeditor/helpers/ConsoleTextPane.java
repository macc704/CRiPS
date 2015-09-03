package ronproeditor.helpers;

import java.awt.Color;
import java.awt.Font;
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
 * �R���\�[�����G�~�����[�g����TextArea�N���X TextPane��
 * 
 * @author macchan
 * @version $Id: ConsoleTextPane.java,v 1.4 2010/03/10 15:44:28 turkey Exp $
 */
public class ConsoleTextPane extends JTextPane implements IConsole {

	private static final long serialVersionUID = 1L;

	// �֘A
	private PrintStream consoleToStream = null;
	private JTextAreaInputStream in = new JTextAreaInputStream(this);
	private JTextAreaPrintStream out = new JTextAreaPrintStream(this,
			Color.BLACK);
	private JTextAreaPrintStream err = new JTextAreaPrintStream(this, Color.RED);

	/**
	 * �R���X�g���N�^ System.out, System.err�̂Ȃ��������s���܂�
	 */
	public ConsoleTextPane() {
		this.initialize();
		this.setFont(new Font(Font.MONOSPACED , Font.PLAIN, 12));
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
	 * ���������܂�
	 */
	private void initialize() {
		CTextPaneUtils
				.setTabs(this, REApplication.WHITESPACE_COUNT_FOR_TAB * 2);// �R���p�C���̃G���[�̏o�����ɂ��킹��
		
		// �L�[�ɂ��A�J�[�\���̈ړ���j�~����
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

		// �}�E�X�ɂ��A�J�[�\���̈ړ���j�~����
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
 * JTextArea���R���\�[���ɂ��邽�߂� System.out �G�~�����[�^
 * 
 * println���\�b�h�ƁAprint���\�b�h�Aflush���\�b�h�̂݃I�[�o�[���C�h���܂�
 * ���̑��͕W����PrintStream�̋@�\���g���ASystem.out�ɃR�l�N�g����܂�
 * 
 * @author macchan
 * @version $Id: ConsoleTextPane.java,v 1.4 2010/03/10 15:44:28 turkey Exp $
 */
class JTextAreaPrintStream extends PrintStream {

	// �萔
	private static final char CR = '\n';

	// ���
	private StringBuffer buf = new StringBuffer();

	// �t���O
	private boolean invokeLater = false; // flush�����Swing�X���b�h�ŏ������邩�ǂ���
	private boolean caretUpdate = true; // flush���ɃJ�[�\�����ړ����邩�ǂ���

	// �֘A
	private ConsoleTextPane textArea = null;

	private MutableAttributeSet attribute = new SimpleAttributeSet();

	/**
	 * �R���X�g���N�^
	 */
	public JTextAreaPrintStream(ConsoleTextPane textarea, Color color) {
		super(System.out); // System.out�͉��R�l�N�g
		this.textArea = textarea;
		StyleConstants.setForeground(attribute, color);
	}

	/***********************************
	 * print�֘A�@�i�I�[�o�[���C�h�j
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
	 * println�֘A�@�i�I�[�o�[���C�h�j
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
	 * flush�֘A�@�i�I�[�o�[���C�h�j
	 ***********************************/

	/**
	 * textarea��Stream���o�͂��܂��B
	 */
	public synchronized void flush() {
		this.flushImpl();
	}

	/***********************************
	 * �����֘A
	 ***********************************/

	/**
	 * print����̎����ł� �o�b�t�@�ɂ���,flush���܂�(����flush)
	 */
	private void printImpl(String s) {
		this.buf.append(s);
		this.flush();
	}

	/**
	 * println����̎����ł�
	 */
	private void printlnImpl(String s) {
		this.printImpl(s + CR);
	}

	/**
	 * flush����̎����ł�
	 */
	private void flushImpl() {
		// �O����
		final String s = this.buf.toString();

		// �{����(writeText�ŏ�������)
		if (this.invokeLater) { // Swing�X���b�h�ŏ�������
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					writeText(s);
				}
			});
		} else { // ���̃X���b�h�ŏ�������(���̂܂܎��s)
			writeText(s);
		}

		// �㏈��
		this.buf = new StringBuffer();

	}

	/***********************************
	 * �e�L�X�g�G���A�ɏ������݊֘A
	 ***********************************/

	/**
	 * �e�L�X�g���e�L�X�g�G���A�ɏ������݂܂�
	 */
	private void writeText(String s) {

		if (this.textArea == null) {
			throw new NullPointerException("textarea is null");
		}

		// �e�L�X�g�G���A�ɏ�������
		textArea.toLast();
		int caret = this.textArea.getCaretPosition();
		int len = this.textArea.getDocument().getLength();
		int pos = caret < len ? caret : len;
		try {
			this.textArea.getDocument().insertString(pos, s, attribute);
			// �����J�[�\���ړ�����
			if (this.caretUpdate) {
				this.textArea.setCaretPosition(pos + s.length());
				this.textArea.repaint();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/***********************************
	 * Setter, Getter�֘A
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
 * JTextArea���R���\�[���ɂ��邽�߂� System.in �G�~�����[�^
 * 
 * �p�����Ă���PrintableInputStream��println�@�\�𗘗p���ď������݂܂�
 * Block�̎d�g�݂�PrintableInputStream�ˑ��ł�
 * 
 * @author macchan
 * @version $Id: ConsoleTextPane.java,v 1.4 2010/03/10 15:44:28 turkey Exp $
 */
class JTextAreaInputStream extends PrintableInputStream {

	// �萔
	private static final int NULL = -1;

	// ���
	private int inputStartCaretPosition = NULL; // ���͂��n�܂����J�[�\���ʒu���o���Ă���

	// �֘A
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
	 * ���������܂�
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
		// �ŏ��̓��͂Ȃ�A�ʒu���o���Ă���
		if (this.inputStartCaretPosition == NULL) {
			this.inputStartCaretPosition = this.textArea.getCaretPosition();
		}
	}

	/**
	 * �L�[�������ꂽ���̏���
	 */
	public synchronized void handleKeyPressed(KeyEvent e) {
		this.memorizeStartPosition();

		// Enter�Ȃ�A�������ݓ�����s��
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

			// �������݂܂�
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
 * String���������߂�InputStream�G�~�����[�^
 * 
 * write(String)���\�b�h�ň�s���������ނ��Ƃ��ł��܂��B
 * ���̃N���X�𗘗p����ǂݎ�́Aread()�����Ƃ���buffer�ɏ������܂�Ă��Ȃ���΁A �������܂��܂Ńu���b�N���܂�
 * 
 * @author macchan
 * @version $Id: ConsoleTextPane.java,v 1.4 2010/03/10 15:44:28 turkey Exp $
 */
class PrintableInputStream extends InputStream {

	// �萔
	private static final char CR = '\n';

	private static final char END_SYMBOL = '\0';

	private static final int END_CODE = -1;

	// ���
	private byte[] buf = null; // �������byte��ŕ\������o�b�t�@

	private int cursor = 0; // byte����ǂ��܂œǂ񂾂��ۑ�����J�[�\��

	/**
	 * �R���X�g���N�^
	 */
	public PrintableInputStream() {
	}

	/**
	 * �o�b�t�@�ɏ������݂܂��B �����u���b�N���̃X���b�h��������A�u���b�N���������܂�
	 */
	public synchronized void println(String s) {
		// ���s�ƏI���L����������
		s = s + CR + END_SYMBOL;

		// �Ώە������byte��̃o�b�t�@�ɕϊ�����
		byte[] stringBuf = s.getBytes();

		// �o�b�t�@���I���ɒB���Ă�����A���̂܂܃o�b�t�@�ɁB
		if (this.isBufferEnd()) {
			this.buf = stringBuf;
		}
		// �o�b�t�@���I���ɒB���Ă��Ȃ�������A�p�������āA�V�����o�b�t�@�����
		else {
			int remain = buf.length - cursor; // �Ⴆ��8,4�̏ꍇ�A0,1,2,3��4�I�����Ă��邩��A�c��S�B
			int newbufsize = remain + stringBuf.length;
			byte[] newBuf = new byte[newbufsize];
			System.arraycopy(buf, cursor, newBuf, 0, remain);
			System.arraycopy(stringBuf, 0, newBuf, remain, stringBuf.length);
			// �Ⴆ��13,4�̏ꍇ�A13-4=9���R�s�[���Ă��̎�������9�ԖځB
			buf = newBuf;
		}

		// �J�[�\����߂��āA�u���b�N����������
		cursor = 0;
		notify();
	}

	/***************************************
	 * �I�[�o�[���C�h
	 ***************************************/

	/**
	 * �u���b�N�����ɓǂݍ��߂�o�C�g����Ԃ��܂�
	 */
	public synchronized int available() throws IOException {
		return buf == null ? 0 : buf.length - cursor;
	}

	/**
	 * �o�b�t�@����A�ꕶ���ǂݍ��݂܂�
	 */
	public synchronized int read() throws IOException {
		int readChar = END_CODE;

		try {
			// �ǂݍ��߂Ȃ���΃u���b�N
			if (isBufferEnd()) {
				wait();
			}

			// �ꕶ���ǂݍ���
			readChar = this.buf[cursor];
			this.cursor++;

			// �I��肾������-1
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
	 * �o�b�t�@���I�����Ă��邩�ǂ������ׂ܂�
	 */
	private synchronized boolean isBufferEnd() {
		return this.buf == null ? true : this.cursor >= this.buf.length;
	}
}
