package ronproeditor.ext.rss;

import generef.knowledge.RSFailureKnowledge;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;


public class RSErrorMessagePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private RSReflectionDialog reflectionDialog;

	private JTextArea textArea = new JTextArea(5, 100);

	public RSErrorMessagePanel(RSReflectionDialog reflectionDialog, int width,
			int height) {
		this.reflectionDialog = reflectionDialog;

		setPreferredSize(new Dimension(width, height));

		// Set Border
		setBorder();

		// Set Scroll Pane
		JScrollPane scrollPane = new JScrollPane(textArea);
		textArea.setEditable(false);
		textArea.setForeground(Color.RED);

		// add
		add(scrollPane);
	}

	private void setBorder() {
		TitledBorder border = BorderFactory.createTitledBorder("メッセージ");
		border.setTitleFont(new Font(getFont().getName(), Font.PLAIN, 15));
		setBorder(border);
	}

	public void selectedErrorChanged() {
		// Change Message
		if (reflectionDialog.getErrorListPanel().getSelectedKnowledges().size() > 0) {
			String message = "";
			for (RSFailureKnowledge knowledge : reflectionDialog
					.getErrorListPanel().getSelectedKnowledges()) {
				message = message + knowledge.getCompileError().getMessage()
						+ "\n";
			}
			textArea.setText(message);
			updateUI();
		}
	}

}
