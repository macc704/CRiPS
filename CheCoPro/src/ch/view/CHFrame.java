package ch.view;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

public class CHFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JScrollBar vBar;

	// private ConsoleTextPane console;

	public static void main(String[] args) {
		CHFrame frame = new CHFrame();
		frame.open();
		frame.println("hoge");
		frame.println("hoge");
	}

	private JTextArea textArea = new JTextArea();
	private JScrollPane tScroll = new JScrollPane();
	private JTextArea consoleArea = new JTextArea();
	private JScrollPane cScroll = new JScrollPane();

	public CHFrame() {

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

	public void initializeClientFrame(/* REApplication application */) {

		// �N���C�A���g�p�ɃR���\�[��������
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 300, 300);
		this.setTitle("Client");
		// console = application.getFrame().getConsole();
		cScroll.setViewportView(consoleArea);
		// JScrollPane cScroll = new JScrollPane(console);
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				this.gettScroll(), cScroll);
		splitPane.setDividerLocation(200);
		this.getContentPane().add(splitPane);
	}

	public JTextArea getConsoleArea() {
		return consoleArea;
	}

	// public String getConsoleText() {
	// return console.getText();
	// }

}
