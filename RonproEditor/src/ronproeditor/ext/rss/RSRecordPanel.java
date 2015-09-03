package ronproeditor.ext.rss;

import generef.knowledge.RSFailureKnowledge;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import util.StringUtil;
import clib.view.list.CListPanel;

public class RSRecordPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final String CAUSE = "原因";
	private final String HANDLE = "対処方法";

	private ImageIcon arrowIcon = new ImageIcon(
			"./ext/reflection/arrow-left.png");

	// Dialog
	private RSReflectionDialog reflectionDialog;

	// TextArea
	private JTextArea causeTextArea = new JTextArea(3, 50);
	private JTextArea handleTextArea = new JTextArea(3, 50);

	// Select Panel
	private RSSelectFailureKnowledgePanel selectCausePanel = new RSSelectFailureKnowledgePanel();
	private RSSelectFailureKnowledgePanel selectHandlePanel = new RSSelectFailureKnowledgePanel();

	public RSRecordPanel(RSReflectionDialog reflectionDialog) {
		this.reflectionDialog = reflectionDialog;
		initializeViews();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// ボーダーを設定
		setBorder();
	}

	private void initializeViews() {

		// 原因記述テキストエリアパネル作成
		JPanel causePanel = createTextAreaPanel(new JLabel(CAUSE),
				causeTextArea, selectCausePanel);
		causePanel.setBorder(BorderFactory.createTitledBorder("原因"));

		// 対処記述テキストエリアパネル作成
		JPanel handlePanel = createTextAreaPanel(new JLabel(HANDLE),
				handleTextArea, selectHandlePanel);
		handlePanel.setBorder(BorderFactory.createTitledBorder("対処方法"));

		add(causePanel, BorderLayout.CENTER);
		add(handlePanel, BorderLayout.SOUTH);
	}

	DocumentListener causeDocumentListener = new DocumentListener() {

		public void removeUpdate(DocumentEvent e) {
			recordKnowledge();
		}

		public void insertUpdate(DocumentEvent e) {
			recordKnowledge();
		}

		public void changedUpdate(DocumentEvent e) {
			recordKnowledge();
		}

		private void recordKnowledge() {
			List<RSFailureKnowledge> knowledges = reflectionDialog
					.getErrorListPanel().getSelectedKnowledges();
			for (RSFailureKnowledge knowledge : knowledges) {
				knowledge.setCause(causeTextArea.getText());
			}
			reflectionDialog.getErrorListPanel().failureKnowledgeWritten();
		}
	};

	DocumentListener handleDocumentListener = new DocumentListener() {

		public void removeUpdate(DocumentEvent e) {
			setHandle();
		}

		public void insertUpdate(DocumentEvent e) {
			setHandle();
		}

		public void changedUpdate(DocumentEvent e) {
			setHandle();
		}

		private void setHandle() {
			List<RSFailureKnowledge> knowledges = reflectionDialog
					.getErrorListPanel().getSelectedKnowledges();
			for (RSFailureKnowledge knowledge : knowledges) {
				knowledge.setHandle(handleTextArea.getText());
			}
			reflectionDialog.getErrorListPanel().failureKnowledgeWritten();
		}
	};

	private void setBorder() {
		TitledBorder border = BorderFactory.createTitledBorder("考察記述欄");
		border.setTitleFont(new Font(getFont().getName(), Font.PLAIN, 18));
		setBorder(border);
	}

	/***************************************************************************
	 * View Update
	 **************************************************************************/

	public void selectedErrorChanged() {
		update();
	}

	private void update() {

		refleshTextFields();

		removeAll();
		this.selectCausePanel.update(CAUSE);
		this.selectHandlePanel.update(HANDLE);
		initializeViews();

	}

	/***************************************************************************
	 * Set View
	 **************************************************************************/

	private JPanel createTextAreaPanel(JLabel label, JTextArea textArea,
			RSSelectFailureKnowledgePanel selectPanel) {

		label.setHorizontalAlignment(JLabel.CENTER);

		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		// text area
		textArea.setLineWrap(true);
		textArea.setFont(new Font(getFont().getName(), Font.PLAIN, getFont()
				.getSize()));
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(390, 90));

		// add component
		JPanel textAreaPane = new JPanel();
		textAreaPane.setLayout(new BorderLayout());
		textAreaPane.add(scrollPane, BorderLayout.CENTER);

		pane.add(textAreaPane, BorderLayout.CENTER);

		// 失敗知識があれば選択パネルを追加
		// if (selectPanel.getList().size() != 0) {
		pane.add(createSelectPanel(label.getText(), selectPanel),
				BorderLayout.EAST);
		// }

		return pane;
	}

	private JPanel createSelectPanel(String text,
			RSSelectFailureKnowledgePanel selectPanel) {
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		// add icon label
		JLabel iconLabel = new JLabel(arrowIcon);
		pane.add(iconLabel, BorderLayout.WEST);

		TitledBorder border = BorderFactory.createTitledBorder("（今までの記述から入力）");
		border.setBorder(new EmptyBorder(0, 0, 0, 0));
		pane.setBorder(border);
		pane.add(selectPanel, BorderLayout.CENTER);
		pane.setPreferredSize(new Dimension(450, 100));

		return pane;
	}

	private boolean isDifferentCause() {
		List<RSFailureKnowledge> knowledges = reflectionDialog
				.getErrorListPanel().getSelectedKnowledges();
		for (int i = 0; i < knowledges.size(); i++) {
			RSFailureKnowledge knowledge = knowledges.get(i);
			for (int j = 0; j < knowledges.size(); j++) {
				RSFailureKnowledge _knowledge = knowledges.get(j);
				if (knowledge != _knowledge
						&& !knowledge.getCause().equals(_knowledge.getCause())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isDifferentHandle() {
		List<RSFailureKnowledge> knowledges = reflectionDialog
				.getErrorListPanel().getSelectedKnowledges();
		for (int i = 0; i < knowledges.size(); i++) {
			RSFailureKnowledge knowledge = knowledges.get(i);
			for (int j = 0; j < knowledges.size(); j++) {
				RSFailureKnowledge _knowledge = knowledges.get(j);
				if (knowledge != _knowledge
						&& !knowledge.getHandle()
								.equals(_knowledge.getHandle())) {
					return true;
				}
			}
		}
		return false;
	}

	private void refleshTextFields() {
		List<RSFailureKnowledge> selectedErrors = reflectionDialog
				.getErrorListPanel().getSelectedKnowledges();
		if (selectedErrors.size() <= 0) {
			causeTextArea.setEnabled(false);
			handleTextArea.setEnabled(false);
			return;
		}
		causeTextArea.setEnabled(true);
		handleTextArea.setEnabled(true);

		causeTextArea.getDocument().removeDocumentListener(
				causeDocumentListener);
		handleTextArea.getDocument().removeDocumentListener(
				handleDocumentListener);

		if (!isDifferentCause()) {
			RSFailureKnowledge knowledge = selectedErrors.get(0);
			causeTextArea.setText(knowledge.getCause());
			// setCauseText(knowledge.getCause());
		} else {
			causeTextArea.setText("記述内容の異なる複数のエラーが選択されています。編集すると上書きされます。");
		}

		if (!isDifferentHandle()) {
			RSFailureKnowledge knowledge = selectedErrors.get(0);
			handleTextArea.setText(knowledge.getHandle());
			// setHandleText(knowledge.getHandle());
		} else {
			handleTextArea.setText("記述内容の異なる複数のエラーが選択されています。編集すると上書きされます。");
		}

		causeTextArea.getDocument().addDocumentListener(causeDocumentListener);
		handleTextArea.getDocument()
				.addDocumentListener(handleDocumentListener);
	}

	/***************************************************************************
	 * Class : Select Failure Knowledge Panel
	 **************************************************************************/

	private class RSSelectFailureKnowledgePanel extends CListPanel<Object> {

		private static final long serialVersionUID = 1L;

		private List<RSFailureKnowledge> showKnowledges = new ArrayList<RSFailureKnowledge>();

		private boolean cause = false;

		public RSSelectFailureKnowledgePanel() {
			// クリック時の動作設定
			getJList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			getJList().addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1
							&& e.getClickCount() == 2) {
						String message = (String) getSelectedElement();
						if (cause) {
							causeTextArea.setText(message);
						} else {
							handleTextArea.setText(message);
						}
					}
				}
			});
		}

		public void update(String text) {
			this.cause = text.equals(CAUSE);
			initializeViews();
		}

		private void initializeViews() {
			setFailureKnowledges();

			removeAll();

			List<Object> checkList = new ArrayList<Object>(); // 同じ文字列があるかすでに含まれているかチェックするリスト
			for (RSFailureKnowledge knowledge : showKnowledges) {
				if (cause && !containsElement(knowledge.getCause(), checkList)) {
					if (!knowledge.getCause().equals("よく分からない")) {
						addElement(knowledge.getCause());
					}
				} else if (!cause
						&& !containsElement(knowledge.getHandle(), checkList)) {
					if (!knowledge.getHandle().equals("エラーの部分を削除した")
							&& !knowledge.getHandle()
									.equals("エラーの部分をコメントアウトした")
							&& !knowledge.getHandle().equals(
									"エラーが発生していない時のソースコードに戻した")) {
						addElement(knowledge.getHandle());
					}
				}
			}

			// 末尾にデフォルトで設定しておく
			if (cause) {
				addElement("よく分からない");
			} else {
				addElement("エラーの部分を削除した");
				addElement("エラーの部分をコメントアウトした");
				addElement("エラーが発生していない時のソースコードに戻した");
			}

			refresh();
		}

		private void setFailureKnowledges() {
			showKnowledges.clear();
			List<RSFailureKnowledge> selectedKnowledges = reflectionDialog
					.getErrorListPanel().getSelectedKnowledges();
			for (RSFailureKnowledge knowledge : selectedKnowledges) {
				showKnowledges.addAll(reflectionDialog
						.getFailureKnowledgeRepository().getFailureKnowledges(
								knowledge.getCompileError().getMessageParser()
										.getAbstractionMessage()));
			}
		}

		public boolean containsElement(String text, List<Object> checkList) {
			String str = new String(text);
			if (checkList.contains(StringUtil.deleteNonChar(str))) {
				return true;
			} else {
				checkList.add(StringUtil.deleteNonChar(str));
				return false;
			}
		}

	}

}
