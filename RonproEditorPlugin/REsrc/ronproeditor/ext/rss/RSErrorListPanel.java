package ronproeditor.ext.rss;

import generef.knowledge.RSFailureKnowledge;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import clib.view.list.CListPanel;

public class RSErrorListPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final int WIDTH = 220;
	private final int HEIGHT = 320;

	// Dialog
	private RSReflectionDialog reflectionDialog;

	private LabelCellRenderer renderer;

	// View
	private CListPanel<RSFailureKnowledge> errorListPanel = new CListPanel<RSFailureKnowledge>();
	private JButton allSelectButton = new JButton("全て選択");

	public RSErrorListPanel(RSReflectionDialog reflectionDialog) {
		this.reflectionDialog = reflectionDialog;

		// set renderer
		ImageIcon[] icons = new ImageIcon[2];
		icons[0] = new ImageIcon("./ext/reflection/rss_check.png");
		icons[1] = new ImageIcon("./ext/reflection/rss_box.png");
		renderer = new LabelCellRenderer(icons);

		initializeViews();
	}

	@SuppressWarnings("unchecked")
	private void initializeViews() {
		// Set Panel
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setLayout(new BorderLayout());

		// Set Border
		setBorder();

		errorListPanel.getJList().setCellRenderer(renderer);

		setListColor(new Color(255, 200, 255));
		errorListPanel.getJList().setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		// クリック時の動作設定
		errorListPanel.getJList().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						selectedErrorChanged();
					}
				});

		allSelectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSelectAllErrors();
			}
		});
	}

	private void setBorder() {
		TitledBorder border = BorderFactory.createTitledBorder("エラー一覧");
		border.setTitleFont(new Font(getFont().getName(), Font.PLAIN, 18));
		setBorder(border);
	}

	private void doSelectAllErrors() {
		errorListPanel.getJList().addSelectionInterval(0,
				errorListPanel.getJList().getLastVisibleIndex());
	}

	private void selectedErrorChanged() {
		reflectionDialog.getSourcePanel().selectedErrorChanged();
		reflectionDialog.getRecordPanel().selectedErrorChanged();
		reflectionDialog.getErrorMessagePanel().selectedErrorChanged();
		changeListColor();
	}

	public void setKnowledge(List<RSFailureKnowledge> failureKnowledges) {

		// remove
		removeAll();
		errorListPanel.removeAll();

		// Set Label
		// JLabel errorCountLabel = getFixedErrorCountLabel(failureKnowledges
		// .size());

		// Set ListPanel
		setErrorListPanel(failureKnowledges);

		// add(errorCountLabel, BorderLayout.NORTH);
		add(errorListPanel, BorderLayout.CENTER);
		add(allSelectButton, BorderLayout.SOUTH);
	}

	private void setErrorListPanel(List<RSFailureKnowledge> failureKnowledges) {

		for (RSFailureKnowledge failureKnowledge : failureKnowledges) {
			errorListPanel.addElement(failureKnowledge);
		}
		errorListPanel.refresh();

		// デフォルトで一番上のエラーを選択
		if (failureKnowledges.size() > 0) {
			errorListPanel.getJList().setSelectedIndex(0);
			selectedErrorChanged();
		}
	}

	// private JLabel getFixedErrorCountLabel(int count) {
	// JLabel label = new JLabel();
	// label.setText("修正されたエラー数：" + count + "個");
	// label.setHorizontalAlignment(JLabel.CENTER);
	// label.setForeground(new Color(255, 0, 0));
	// label.setFont(new Font("Dialog", Font.PLAIN, 14));
	// return label;
	// }

	public void clear() {
		setListColorIndex(new ArrayList<Integer>());
	}

	public List<RSFailureKnowledge> getSelectedKnowledges() {
		return errorListPanel.getSelectedElements();
	}

	/***************************************************************************
	 * change List color
	 **************************************************************************/

	private void changeListColor() {
		List<Integer> checkErrorIndexes = new ArrayList<Integer>();
		for (int i = 0; i < reflectionDialog.getInputFailureKnowledges().size(); i++) {
			RSFailureKnowledge knowledge = reflectionDialog
					.getInputFailureKnowledges().get(i);
			if (knowledge.isEmpty()) {
				checkErrorIndexes.add(i);
			}
		}
		setListColorIndex(checkErrorIndexes);
	}

	private void setListColorIndex(List<Integer> indexes) {
		renderer.setColorListIndex(indexes);
	}

	private void setListColor(Color color) {
		renderer.setListColor(color);
	}

	/***************************************************************************
	 * Class : Renderer
	 **************************************************************************/

	private class LabelCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1L;

		private ImageIcon checkIcon;
		private ImageIcon boxIcon;

		private List<Integer> indexes;
		private Color color;

		public LabelCellRenderer(ImageIcon[] icons) {
			this.checkIcon = icons[0];
			this.boxIcon = icons[1];
			this.indexes = null;
			this.color = new Color(240, 240, 255);
		}

		@Override
		public Component getListCellRendererComponent(JList<?> list,
				Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list,
					value, index, isSelected, cellHasFocus);

			if (this.indexes == null) {
				return label;
			}

			if (this.indexes.contains(index)) {
				if (list.isSelectedIndex(index)) {
					// 選択行はデフォルトの色
					label.setBackground(list.getSelectionBackground());
				} else {
					label.setBackground(color);
				}
				label.setIcon(boxIcon);
			} else {
				label.setIcon(checkIcon);
			}

			return label;
		}

		public void setColorListIndex(List<Integer> indexes) {
			this.indexes = indexes;
		}

		public void setListColor(Color color) {
			this.color = color;
		}

	}

	public void failureKnowledgeWritten() {
		changeListColor();
		repaint();
	}

}
