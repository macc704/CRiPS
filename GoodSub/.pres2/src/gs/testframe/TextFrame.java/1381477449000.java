package gs.testframe;

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

public class TextFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JScrollBar vBar;

	public static void main(String[] args) {
		TextFrame frame = new TextFrame();
		frame.open();
		frame.println("hoge");
		frame.println("hoge");
	}

	private JTextArea textArea = new JTextArea();
	private JTextArea consoleArea = new JTextArea();

	public TextFrame() {
		Container textCon;
		Container consoleCon;

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(100, 100, 300, 300);

		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(textArea);
		getContentPane().add(scroll);
		scroll.setViewportView(consoleArea);
		getContentPane().add(scroll);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				textArea, consoleArea);
		getContentPane().add(splitPane);

		vBar = scroll.getVerticalScrollBar();
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
}
