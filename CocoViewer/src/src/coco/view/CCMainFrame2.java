package src.coco.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import src.coco.model.CCCompileErrorList;
import src.coco.model.CCCompileErrorManager;

public class CCMainFrame2 extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String APP_NAME = "CoCo Viewer";
	public static final String VERSION = "0.0.7";

	// Button Size
	private int buttonWidth = 100;
	private int buttonHeight = 100;

	// Dialog size
	private int width = 1120;
	private int height = 740;

	// Compile Error Date
	private CCCompileErrorManager manager;

	// For GUI
	private JPanel rootPanel = new JPanel();

	public CCMainFrame2(CCCompileErrorManager manager) {
		this.manager = manager;
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.width = d.width * 3 / 4;
		this.height = d.height * 3 / 4;
		this.buttonWidth = this.width / 8;
		this.buttonHeight = this.height / 8;
		initialize();
	}

	private void initialize() {
		// titleなどの設定
		frameSetting();

		// rootPanel のレイアウト
		panelSetting();

		// 全体のコンパイル数表示
		setHeaderPanel();

		// ボタンを配置する
		setButtonsPanel();

		add(rootPanel);
	}

	private void frameSetting() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(width, height);
		setTitle(APP_NAME + " " + VERSION);
	}

	private void panelSetting() {
		rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));
		rootPanel.setSize(new Dimension(width, height));
	}

	private void setHeaderPanel() {
		JPanel headerPanel = new JPanel();

		headerPanel.setLayout(new BorderLayout());
		headerPanel.setMaximumSize(new Dimension(width, height / 24));

		setCompileErrorNumber(headerPanel);
		setAchivementsButton(headerPanel);

		rootPanel.add(headerPanel, BorderLayout.NORTH);
	}

	private void setCompileErrorNumber(JPanel headerPanel) {
		JLabel label = new JLabel();
		String string = "あなたのこれまでの総コンパイルエラー数 ： " + manager.getTotalErrorCount();
		label.setText(string);
		label.setMaximumSize(new Dimension(width, height / 24));
		label.setFont(new Font("Font2DHandle", Font.BOLD, 16));

		// label の背景を設定する場合は背景を不透明にする処理を加えること
		// label.setBackground(Color.yellow);
		// label.setOpaque(true);

		LineBorder lineborder = new LineBorder(Color.YELLOW, 2, true);
		label.setBorder(lineborder);

		headerPanel.add(label, BorderLayout.WEST);
	}

	private void setAchivementsButton(JPanel headerPanel) {
		// CCAchivementButton achivementButton = new CCAchivementButton(manager,
		// label);
		// headerPanel.add(label, BorderLayout.EAST);
	}

	private void setButtonsPanel() {
		ArrayList<CCErrorElementButton2> buttons = new ArrayList<CCErrorElementButton2>();

		// エラーIDごとの数値を書き込み、ボタンを実装する
		for (CCCompileErrorList list : manager.getAllLists()) {
			CCErrorElementButton2 button = new CCErrorElementButton2(
					buttonWidth, buttonHeight, list, manager.getLibDir(),
					manager.getBase());
			buttons.add(button);
		}

		// ボタンを配置する
		JPanel buttonEreaPanel = new JPanel();
		buttonEreaPanel.setLayout(new GridLayout(
				((height * 15 / 16) / buttonHeight), (width / buttonWidth)));

		int i = 1;
		for (int x = 0; x < width; x += buttonWidth) {
			for (int y = height / 16; y < height - buttonHeight; y += buttonHeight) {
				if (manager.getAllLists().size() >= i) {
					if (manager.getList(i).getErrors().size() > 0) {
						buttonEreaPanel.add(buttons.get(i - 1));
					} else {
						buttonEreaPanel.add(setEmptyButton());
					}
					i++;
				} else {
					buttonEreaPanel.add(setEmptyButton());
				}
			}
		}

		JScrollPane scrollPanel = new JScrollPane(buttonEreaPanel);
		rootPanel.add(scrollPanel, BorderLayout.SOUTH);
	}

	private JButton setEmptyButton() {
		JButton emptyButton = new JButton("未発生");
		emptyButton.setEnabled(false);
		emptyButton.setToolTipText("未発生です");
		emptyButton.setBackground(Color.GRAY);
		emptyButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		return emptyButton;
	}
}