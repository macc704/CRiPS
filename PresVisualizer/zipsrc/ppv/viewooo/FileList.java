/*
 * FileList.java
 * Created on Jul 6, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.viewooo;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

import pres.loader.model.PLFile;
import pres.loader.model.PLProject;

/**
 * @author macchan
 */
public class FileList extends JFrame {

	private static final long serialVersionUID = 1L;

	private JScrollPane scroll = new JScrollPane();
	private JList<X> list = new JList<X>();

	public FileList() {
		getContentPane().add(scroll);
		scroll.setViewportView(list);
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					open();
				}
			}
		});
	}

	public void open() {
		X x = (X) list.getSelectedValue();
		PLFile f = x.file;

		PRFileViewerFrame frame = new PRFileViewerFrame(f);
		frame.setTitle(f.getPath().toString());
		frame.setVisible(true);
	}

	public void setPresModel(PLProject model) {
		DefaultListModel<X> listModel = new DefaultListModel<X>();
		list.setModel(listModel);

		// list.setCellRenderer(new ListCellRenderer() {
		// public Component getListCellRendererComponent(JList list,
		// Object value, int index, boolean isSelected,
		// boolean cellHasFocus) {
		// if (value instanceof PresFile) {
		// PresFile f = (PresFile) value;
		// if (!isSelected) {
		// return new JLabel(f.getPath().toString());
		// } else {
		// JLabel label = new JLabel(f.getPath().toString());
		// label.setBackground(Color.BLUE);
		// return label;
		// }
		// }
		// return null;
		// }
		// });
		for (PLFile f : model.getFiles()) {
			listModel.addElement(new X(f));
		}
	}
}

class X {
	PLFile file;

	X(PLFile file) {
		this.file = file;
	}

	public String toString() {
		return file.getPath().toString();
	}
}
