/*
 * PRFileViewerFrame.java
 * Created on Jul 6, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.viewooo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import pres.loader.model.PLFile;

/**
 * @author macchan
 * 
 */
@SuppressWarnings("serial")
public class PRFileViewerFrame extends JFrame {

	private PRFileViewer viewer;
	private JScrollPane scroll = new JScrollPane();
	private JButton plus = new JButton("+");
	private JButton minus = new JButton("-");

	public PRFileViewerFrame(PLFile file) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(400, 200);

		viewer = new PRFileViewer(file);
		scroll.setViewportView(viewer);

		JToolBar toolbar = new JToolBar();
		toolbar.add(plus);
		toolbar.add(minus);

		getContentPane().add(toolbar, BorderLayout.NORTH);
		getContentPane().add(scroll);

		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaleUp();
			}
		});
		minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaleDown();
			}
		});
	}

	double scale = 1.0d;

	void scaleUp() {
		scale = scale * 2.0d;
		update();
	}

	void scaleDown() {
		scale = scale * 0.5d;
		update();
	}

	void update() {
		Dimension d = scroll.getViewport().getSize();
		d.width = (int) (d.width * scale);
		viewer.setPreferredSize(d);
		viewer.revalidate();
	}

}
