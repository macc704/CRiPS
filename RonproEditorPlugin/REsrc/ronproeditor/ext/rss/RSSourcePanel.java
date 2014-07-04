package ronproeditor.ext.rss;

import generef.knowledge.RSFailureKnowledge;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ronproeditor.REApplication;
import ronproeditor.views.RESourceViewer;

public class RSSourcePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final String UNFIXED = "＜エラー発生時のソースコード＞";
	private final String FIXED = "＜現在のソースコード＞";

	private int fontHeight;

	// Panel Size
	private int width;
	private int height;

	// Panel
	private JPanel sourcePanel = new JPanel();
	private JScrollPane scrollPane = new JScrollPane();
	private RESourceViewer unFixedView = new RESourceViewer();
	private RESourceViewer fixedView = new RESourceViewer();

	// Dialog
	private RSReflectionDialog reflectionDialog;

	public RSSourcePanel(RSReflectionDialog reflectionDialog) {
		this.reflectionDialog = reflectionDialog;
		this.width = reflectionDialog.getWidth() * 3 / 4;
		this.height = reflectionDialog.getHeight() * 1 / 3;
		this.fontHeight = new RESourceViewer().getLineNumberView()
				.getFontMetrics().getHeight();
		initializeViews();
	}

	public RSSourcePanel(int width, int height) {
		this.width = width * 3 / 4;
		this.height = height * 1 / 3;
		this.fontHeight = new RESourceViewer().getLineNumberView()
				.getFontMetrics().getHeight();
		initializeViews();
	}

	private void initializeViews() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(width, height));

		sourcePanel.setLayout(new BoxLayout(sourcePanel, BoxLayout.X_AXIS));
		sourcePanel.add(createPanel(null, null));
		add(sourcePanel);
	}

	private JPanel createPanel(File unFixedFile, File fixedFile) {

		// Set View Pane
		JPanel sourcePanel = new JPanel();
		sourcePanel.setPreferredSize(new Dimension(width, height));
		sourcePanel.setLayout(new BorderLayout());
		this.unFixedView = createSourceView(unFixedFile);
		this.fixedView = createSourceView(fixedFile);

		this.unFixedView
				.setPreferredSize(new Dimension(width / 2 - 10, height));
		this.fixedView.setPreferredSize(new Dimension(width / 2 - 10, height));

		JPanel viewPane = new JPanel();
		viewPane.setLayout(new BoxLayout(viewPane, BoxLayout.X_AXIS));
		viewPane.add(unFixedView);
		viewPane.add(fixedView);
		sourcePanel.add(viewPane, BorderLayout.CENTER);

		// Set Scroll Pane
		scrollPane = new JScrollPane(sourcePanel);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(width, height));

		// Set label Pane
		JPanel labelPane = createLabelPanel();

		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());
		pane.add(labelPane, BorderLayout.NORTH);
		pane.add(scrollPane, BorderLayout.CENTER);

		return pane;
	}

	private RESourceViewer createSourceView(File file) {
		RESourceViewer view = new RESourceViewer(readFile(file));
		view.getTextPane().setEditable(false);
		view.getTextPane().setBackground(Color.WHITE);
		view.setPreferredSize(new Dimension(width / 2 - 15, getViewHeight(view)));
		return view;
	}

	private JPanel createLabelPanel() {
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());

		JLabel unFixedLabel = createLabel(UNFIXED);
		JLabel fixedLabel = createLabel(FIXED);

		pane.add(unFixedLabel, BorderLayout.WEST);
		pane.add(fixedLabel, BorderLayout.EAST);

		return pane;
	}

	private JLabel createLabel(String text) {
		JLabel label = new JLabel(text);
		label.setPreferredSize(new Dimension(width / 2, 20));
		label.setHorizontalAlignment(JLabel.CENTER);
		return label;
	}

	private String readFile(File file) {

		if (file == null) {
			return "";
		}

		StringBuffer buf = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), REApplication.SRC_ENCODING));
			String str = "";
			while ((str = reader.readLine()) != null) {
				buf.append(str);
				buf.append("\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buf.toString();
	}

	public void selectedErrorChanged() {
		List<RSFailureKnowledge> knowledges = reflectionDialog
				.getErrorListPanel().getSelectedKnowledges();
		if (knowledges.size() <= 0) {
			return;
		}

		// Set Source
		String rootPath = reflectionDialog.getManager().getApplication()
				.getSourceManager().getCRootDirectory().getAbsolutePath()
				.toString();
		File unFixedFile = new File(rootPath
				+ knowledges.get(0).getUnFixedFilePath());
		File fixedFile = new File(rootPath
				+ knowledges.get(0).getFixedFilePath());
		setSourceFile(unFixedFile, fixedFile);

		// Set Highlight
		unFixedView.getTextPane().getHighlighter().removeAllHighlights();
		for (RSFailureKnowledge knowledge : knowledges) {
			int lineNumber = (int) knowledge.getCompileError().getLineNumber();
			unFixedView.highlightLine(lineNumber);
		}

		// Set Scrollbar
		int lineNumber = (int) knowledges.get(0).getCompileError()
				.getLineNumber();
		setScrollBarPosition(lineNumber);
	}

	private int getViewHeight(RESourceViewer view) {
		int colmn = view.getText().split("\n").length;
		return fontHeight * (colmn + 7); // 7行分余分に設定（スクロールバー対策）
	}

	public void setSourceFile(File unFixedFile, File fixedFile) {
		sourcePanel.removeAll();
		sourcePanel.add(createPanel(unFixedFile, fixedFile));
	}

	/**
	 * 指定した行数にスクロールバーをスライドさせます
	 * 
	 * @param colmn
	 */
	private void setScrollBarPosition(int colmn) {
		if (colmn < 8) {
			scrollPane.getViewport().setViewPosition(new Point(0, 0));
		} else {
			scrollPane.getViewport().setViewPosition(
					new Point(0, (int) ((colmn - 8) * fontHeight))); // 8行分ずらして表示
		}
	}

}
