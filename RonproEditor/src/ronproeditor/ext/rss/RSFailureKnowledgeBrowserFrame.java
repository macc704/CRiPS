package ronproeditor.ext.rss;

import generef.knowledge.RSFailureKnowledge;
import generef.knowledge.RSFailureKnowledgeRepository;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

import ronproeditor.ext.REGeneRefManager;
import clib.view.list.CListPanel;

public class RSFailureKnowledgeBrowserFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private final int WIDTH = 950;
	private final int HEIGHT = 700;

	private REGeneRefManager manager;

	private CListPanel<String> errorSelectPanel = new CListPanel<String>();
	private KnowledgeViewPanel knowledgeViewPanel = new KnowledgeViewPanel(this);

	private String currentErrorMessage;

	public RSFailureKnowledgeBrowserFrame(REGeneRefManager manager) {
		this.manager = manager;
		initializeViews();
	}

	private void initializeViews() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		setTitle("FailureKnowledgeBrowser");

		setErrorSelectPanel();

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				new JScrollPane(errorSelectPanel), new JScrollPane(
						knowledgeViewPanel));
		splitPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		getContentPane().add(splitPane);
	}

	public void open() {
		setVisible(true);
	}

	public void close() {
		setVisible(false);
	}

	public void update() {
		clear();
		setErrorSelectPanel();
		knowledgeViewPanel.setKnowledgeViewPanel(currentErrorMessage);
		errorSelectPanel.updateUI();
		knowledgeViewPanel.updateUI();
	}

	private void clear() {
		errorSelectPanel.removeAll();
		knowledgeViewPanel.removeAll();
	}

	private void setErrorSelectPanel() {

		RSFailureKnowledgeRepository knowledges = manager.getFKRepository();

		// Elementの設定
		for (String message : knowledges.getFailureKnowledgeKinds()) {
			errorSelectPanel.addElement(message);
		}

		errorSelectPanel.refresh();

		// クリック時の動作設定
		errorSelectPanel.getJList().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		errorSelectPanel.getJList().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() == 1) {
					String errorMessage = errorSelectPanel.getSelectedElement();
					currentErrorMessage = errorMessage;
					knowledgeViewPanel.removeAll();
					knowledgeViewPanel.setKnowledgeViewPanel(errorMessage);
					knowledgeViewPanel.updateUI();
				}
			}
		});

	}

	private class KnowledgeViewPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		private JFrame root;

		public KnowledgeViewPanel(JFrame root) {
			initializeView();
			this.root = root;
		}

		private void initializeView() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		}

		public void setKnowledgeViewPanel(String errorMessage) {
			for (RSFailureKnowledge knowledge : manager.getFKRepository()
					.getFailureKnowledges(errorMessage)) {
				add(createKnowledgePanel(knowledge));
			}
		}

		// @SuppressWarnings("deprecation")
		private JPanel createKnowledgePanel(RSFailureKnowledge knowledge) {
			JPanel viewerPane = new JPanel();
			viewerPane.setLayout(new BorderLayout());

			// Set SourcePanel
			RSSourcePanel sourcePanel = new RSSourcePanel(root.getWidth() - 50,
					root.getHeight());
			String rootPath = manager.getApplication().getSourceManager()
					.getCRootDirectory().toString();
			File unFixedFile = new File(rootPath
					+ knowledge.getUnFixedFilePath());
			File fixedFile = new File(rootPath + knowledge.getFixedFilePath());
			sourcePanel.setSourceFile(unFixedFile, fixedFile);
			// sourcePanel.setSourceFile(knowledge.getUnFixedFile(),
			// knowledge.getFixedFile());

			JPanel pane = new JPanel();
			pane.setLayout(new FlowLayout());
			JPanel causePane = createTextAreaPane("エラーの原因",
					createScrollPane(knowledge.getCause()));
			JPanel handlePane = createTextAreaPane("エラーへの対処方法",
					createScrollPane(knowledge.getHandle()));
			pane.add(causePane);
			pane.add(handlePane);

			viewerPane.add(sourcePanel, BorderLayout.CENTER);
			viewerPane.add(pane, BorderLayout.SOUTH);

			return viewerPane;
		}

		private JScrollPane createScrollPane(String message) {

			// テキストエリアの設定
			JTextArea textArea = new JTextArea(3, 50);
			textArea.setText(message);
			textArea.setLineWrap(true);
			textArea.setEditable(false);

			// スクロールパネルの設定
			JScrollPane scrollPane = new JScrollPane(textArea);
			scrollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

			return scrollPane;
		}

		private JPanel createTextAreaPane(String message, JScrollPane scrollPane) {
			JPanel textAreaPanel = new JPanel();

			textAreaPanel.setLayout(new BoxLayout(textAreaPanel,
					BoxLayout.Y_AXIS));
			JLabel handleLabel = new JLabel(message);
			textAreaPanel.add(handleLabel);
			textAreaPanel.add(scrollPane);

			return textAreaPanel;
		}
	}

}
