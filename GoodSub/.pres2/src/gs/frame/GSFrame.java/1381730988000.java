package gs.frame;

import helpers.ConsoleTextPane;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class GSFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JScrollBar vBar;
	private ConsoleTextPane console;

	public static void main(String[] args) {
		GSFrame frame = new GSFrame();
		frame.open();
		frame.println("hoge");
		frame.println("hoge");
	}

	private JTextArea textArea = new JTextArea();
	private JScrollPane tScroll = new JScrollPane();

	public GSFrame() {

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(100, 100, 300, 300);

		tScroll.setViewportView(textArea);
		getContentPane().add(tScroll);

		vBar = tScroll.getVerticalScrollBar();
	}

	public void open() {
		this.setVisible(true);
	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public void print(String s) {
		textArea.append(s);
	}

	public void println(String s) {
		textArea.append(s + "\n");
	}

	public JScrollPane gettScroll() {
		return tScroll;
	}

	public JScrollBar getvBar() {
		return vBar;
	}

	public void setvBar(int value) {
		vBar.setValue(value);
	}

	public void initializeClientFrame() {

	}

}
