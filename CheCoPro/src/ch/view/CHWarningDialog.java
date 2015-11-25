package ch.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ch.view.CHFileTree.FileTreeNode;
import clib.view.windowmanager.CWindowCentraizer;

public class CHWarningDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private boolean ok;
	private List<FileTreeNode> overlapesNodes = new ArrayList<FileTreeNode>();

	public CHWarningDialog(int language, List<FileTreeNode> overLapedNodes) {
		this.overlapesNodes = overLapedNodes;
		initialize(language);
	}

	private void initialize(int language) {
		this.setTitle("Warning!");
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setModal(true);
		this.setBounds(100, 100, 400, 200);
		this.setResizable(false);
		CWindowCentraizer.centerWindow(this);

		JLabel label = new JLabel();
		JButton cancelButton = new JButton("キャンセル");
		JButton okButton = new JButton("OK");
		
		if (language == 0) {
			label.setText("上書きされるファイルがあります．上書きしてもよろしいですか？");
			cancelButton.setText("キャンセル");
		} else {
			label.setText("If you wish to overwrite your existing project, press OK.");
			cancelButton.setText("CANCEL");
		}
		
		JPanel panel = new JPanel(new FlowLayout());
		panel.add(cancelButton);
		panel.add(okButton);

		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("cancel");
		okButton.addActionListener(this);
		okButton.setActionCommand("ok");

		this.getContentPane().add(label, BorderLayout.NORTH);
		this.getContentPane().add(createCenterPane(), BorderLayout.CENTER);
		this.getContentPane().add(panel, BorderLayout.SOUTH);
	}
	
	public JPanel createCenterPane() {
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(150, 100));
		panel.add(createOverlapedFilePnae(), BorderLayout.CENTER);
		return panel;
	}
	
	public JScrollPane createOverlapedFilePnae() {
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().setView(createOverlapedFileListArea());
		scrollPane.setPreferredSize(new Dimension(150, 100));
		return scrollPane;
	}
	
	public JTextArea createOverlapedFileListArea() {
		JTextArea textArea = new JTextArea();
		for (FileTreeNode aNode : overlapesNodes) {
			textArea.append(aNode.toString() + "\n");
		}
		textArea.setEditable(false);
		return textArea;
	}

	public void open() {
		this.setVisible(true);
	}

	public static void main(String[] args) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals("cancel")) {
			setOk(false);
		} else if (actionCommand.equals("ok")) {
			setOk(true);
		}
		this.dispose();
	}

	public boolean isOk() {
		return ok;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}
}
