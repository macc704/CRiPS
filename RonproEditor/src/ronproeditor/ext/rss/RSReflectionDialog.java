package ronproeditor.ext.rss;

import generef.knowledge.RSFailureKnowledge;
import generef.knowledge.RSFailureKnowledgeRepository;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import ronproeditor.ext.REGeneRefManager;

public class RSReflectionDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	// Dialog Size
	private int width = 1120;
	private int height = 720;

	// Manager
	private REGeneRefManager manager;

	// Model
	private RSFailureKnowledgeRepository fkRepository;
	private List<RSFailureKnowledge> inputFailureKnowledges = new ArrayList<RSFailureKnowledge>();

	// Dialog
	private RSEmptyTextDialog emptyTextDialog;

	// Panel
	private JPanel rootPane;
	private RSSourcePanel sourcePanel;
	private RSErrorMessagePanel errorMessagePanel;
	private RSRecordPanel recordPanel;
	private RSErrorListPanel errorListPanel;

	private long openDialogTime = 0;
	private long closeDialogTime = 0;

	public RSReflectionDialog(REGeneRefManager manager, int width, int height) {

		this.manager = manager;
		this.width = width;
		this.height = height;

		// Set Panel
		this.errorMessagePanel = new RSErrorMessagePanel(this, width, 130);
		this.sourcePanel = new RSSourcePanel(this);
		this.recordPanel = new RSRecordPanel(this);
		this.errorListPanel = new RSErrorListPanel(this);

		// Set Dialog
		this.emptyTextDialog = new RSEmptyTextDialog(manager);

		initializeDialog();
		initializeViews();
	}

	private void initializeDialog() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setSize(new Dimension(width, height));
		setLocationRelativeTo(null);
		setResizable(true);
		setModal(true);
		setTitle(REGeneRefManager.APP_NAME + " " + REGeneRefManager.VERSION);
		addComponentListener(new ComponentListener() {

			public void componentShown(ComponentEvent e) {
			}

			public void componentResized(ComponentEvent e) {
				getContentPane().setSize(getSize());
			}

			public void componentMoved(ComponentEvent e) {
			}

			public void componentHidden(ComponentEvent e) {
			}
		});
	}

	private void initializeViews() {

		// ボタン作成
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				okFinish();
			}
		});

		// パネル作成
		rootPane = new JPanel();
		rootPane.setLayout(new BorderLayout());

		// ボタンパネル追加
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(okButton);
		rootPane.add(buttonPanel, BorderLayout.SOUTH);

		// ソース表示パネル・エラーメッセージ表示・記述パネルの設定
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		TitledBorder border = BorderFactory.createTitledBorder("エラー内容");
		border.setTitleFont(new Font(panel.getFont().getName(), Font.PLAIN, 18));
		panel.setBorder(border);
		panel.add(sourcePanel, BorderLayout.NORTH);
		panel.add(errorMessagePanel, BorderLayout.CENTER);

		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());
		pane.add(panel, BorderLayout.NORTH);
		pane.add(recordPanel, BorderLayout.CENTER);
		rootPane.add(pane, BorderLayout.CENTER);

		// エラー一覧パネル追加
		JPanel westPane = new JPanel();
		westPane.setLayout(new BorderLayout());
		westPane.add(createTimePanel(), BorderLayout.NORTH);
		westPane.add(errorListPanel, BorderLayout.CENTER);
		rootPane.add(westPane, BorderLayout.WEST);

		getContentPane().add(rootPane);
	}

	/**
	 * 失敗知識が記述されているかどうかを判定します
	 */
	private boolean isEmpty() {
		for (RSFailureKnowledge knowledge : this.inputFailureKnowledges) {
			if (knowledge.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 修正時間表示パネルを作成します
	 * 
	 * @return
	 */
	private JPanel createTimePanel() {
		JPanel pane = new JPanel();
		TitledBorder border = BorderFactory.createTitledBorder("エラー1個の修正時間");
		border.setTitleFont(new Font(pane.getFont().getName(), Font.PLAIN, 18));
		pane.setBorder(border);
		pane.add(createTimeLabel());
		return pane;
	}

	private JLabel createTimeLabel() {
		JLabel label = new JLabel();
		String text = "<html><center>" + manager.getCorrectionTime()
				+ "秒<center></html>";
		label.setText(text);
		label.setHorizontalAlignment(JLabel.CENTER);
		return label;
	}

	/***************************************************************************
	 * Dialog open and close
	 **************************************************************************/

	public void open(List<RSFailureKnowledge> failureKnowledges,
			RSFailureKnowledgeRepository fkRepository) {
		if (failureKnowledges.size() > 0) {

			this.inputFailureKnowledges = failureKnowledges;
			this.fkRepository = fkRepository;

			// Set Open Time
			this.openDialogTime = System.currentTimeMillis();
			for (RSFailureKnowledge knowledge : inputFailureKnowledges) {
				if (knowledge.getWindowOpenTime() == 0) {
					knowledge.setWindowOpenTime(this.openDialogTime);
				}
			}

			// Set Panel
			errorListPanel.setKnowledge(failureKnowledges);

			setVisible(true);
		}
	}

	private void close() {

		// Set Close Time
		this.closeDialogTime = System.currentTimeMillis();
		for (RSFailureKnowledge knowledge : inputFailureKnowledges) {
			if (knowledge.getWindowCloseTime() == 0) {
				knowledge.setWindowCloseTime(this.closeDialogTime);
			}
		}

		// clear
		errorListPanel.clear();

		// Set WritingTime
		if (manager.getCompileResult() == null
				|| manager.getCompileResult().getDiagnostics().size() <= 0) {
			manager.setWritingReflectionTime(0);
		} else {
			manager.setWritingReflectionTime(getWritingReflectionTime());
		}

		manager.saveFailureKnowledge();
		manager.refreshCompileHistory();

		setVisible(false);
	}

	/***************************************************************************
	 * Button Action
	 **************************************************************************/

	private void okFinish() {
		if (!isEmpty()) {
			emptyTextDialog.open();
		} else {
			close();
		}
	}

	/***************************************************************************
	 * getter and setter
	 **************************************************************************/

	public RSSourcePanel getSourcePanel() {
		return sourcePanel;
	}

	public RSErrorMessagePanel getErrorMessagePanel() {
		return errorMessagePanel;
	}

	public RSRecordPanel getRecordPanel() {
		return recordPanel;
	}

	public RSErrorListPanel getErrorListPanel() {
		return errorListPanel;
	}

	public List<RSFailureKnowledge> getInputFailureKnowledges() {
		return inputFailureKnowledges;
	}

	public RSFailureKnowledgeRepository getFailureKnowledgeRepository() {
		return fkRepository;
	}

	public REGeneRefManager getManager() {
		return manager;
	}

	public long getWritingReflectionTime() {
		return this.closeDialogTime - this.openDialogTime;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

}
