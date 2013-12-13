package framework;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class DnDFramework {

	public static void open(String title, String instruction,
			DropStrategy dropStrategy) {
		JFrame frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setTitle(title);

		DnDPanel panel = new DnDPanel(instruction);
		panel.setDropStrategy(dropStrategy);
		panel.setPreferredSize(new Dimension(200, 200));
		frame.getContentPane().add(panel);

		frame.pack();
		frame.setVisible(true);
	}
}
