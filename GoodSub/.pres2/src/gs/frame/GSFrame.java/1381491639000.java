package gs.frame;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

public class GSFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JScrollBar vBar;

	public static void main(String[] args) {
		GSFrame frame = new GSFrame();
		frame.open();
		frame.println("hoge");
		frame.println("hoge");
	}

	private JTextArea textArea = new JTextArea();
	private JTextArea consoleArea = new JTextArea();
	private JScrollPane cScroll;

	public GSFrame() {

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(100, 100, 300, 300);

		JScrollPane tScroll = new JScrollPane();
		cScroll = new JScrollPane();

		tScroll.setViewportView(textArea);
		cScroll.setViewportView(consoleArea);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				tScroll, cScroll);

		splitPane.setDividerLocation(200);

		getContentPane().add(splitPane);

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

	public JScrollBar getvBar() {
		return vBar;
	}

	public void setvBar(int value) {
		vBar.setValue(value);
	}

	public JScrollPane getcScroll() {
		return cScroll;
	}
}
