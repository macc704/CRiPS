import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class JNLPMain {

	public static boolean jnlp = false;

	public static void main(String[] args) throws Exception {
		JNLPMain.jnlp = true;

		// Create Console
		JFrame frame = new JFrame("Console");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ConsoleTextArea console = new ConsoleTextArea();
		console.setBackground(Color.BLACK);
		console.setForeground(Color.WHITE);
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(console);
		scroll.setPreferredSize(new Dimension(1, 1));
		scroll.setMinimumSize(new Dimension(1, 1));
		scroll.setMaximumSize(new Dimension(2000, 2000));
		frame.getContentPane().add(scroll);

		Turtle t = (Turtle) Class.forName(args[0]).newInstance();
		Turtle.startTurtle(t);

		Turtle.setConsole(console);
		Rectangle r = Turtle.window.getBounds();
		frame.setBounds(r.x + r.width, r.y, 500, 300 + 30);
		frame.setVisible(true);
	}

}
