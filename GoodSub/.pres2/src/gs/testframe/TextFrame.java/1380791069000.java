package gs.testframe;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
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

	public TextFrame() {

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(100, 100, 100, 100);
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(textArea);
		getContentPane().add(scroll);

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
