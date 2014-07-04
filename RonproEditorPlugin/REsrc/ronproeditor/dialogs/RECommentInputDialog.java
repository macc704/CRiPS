package ronproeditor.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import ronproeditor.REApplication;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.system.CEncoding;

public class RECommentInputDialog extends JPanel {

	private static final long serialVersionUID = 1L;

	private CDirectory project;

	private JTextPane textPane = new JTextPane();

	public RECommentInputDialog(CDirectory project) {
		this.project = project;
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(400, 300));
		JPanel northPanel = new JPanel();
		northPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		northPanel.add(new JLabel("「" + project.getName() + "」"
				+ "をExportします．よろしいですね？"));
		add(northPanel, BorderLayout.NORTH);
		JPanel panel = new JPanel();
		add(panel);
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("本プロジェクトの感想 ・ コメント"));
		panel.add(new JScrollPane(textPane));
		CFile file = project.findOrCreateFile(REApplication.COMMENT_FILE);
		file.setEncodingIn(CEncoding.get(REApplication.SRC_ENCODING));
		textPane.setText(file.loadText());
	}

	public void save() {
		String text = textPane.getText();
		CFile file = project.findOrCreateFile(REApplication.COMMENT_FILE);
		file.setEncodingOut(CEncoding.get(REApplication.SRC_ENCODING));
		file.saveText(text);
	}

}
