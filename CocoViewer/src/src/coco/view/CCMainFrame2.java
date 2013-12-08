package src.coco.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
	private JPanel headerPanel = new JPanel();
	private JPanel buttonEreaPanel = new JPanel();

	public CCMainFrame2(CCCompileErrorManager manager) {
		this.manager = manager;
		// this.height = GraphicsEnvironment.getLocalGraphicsEnvironment()
		// .getMaximumWindowBounds().height - 25;
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.width = d.width * 3 / 4;
		this.height = d.height * 3 / 4;
		this.buttonWidth = this.width / 8;
		this.buttonHeight = this.height / 8;
		initialize();
	}

	private void initialize() {
		// rootPanel のレイアウトをリセットする
		// rootPanel.setLayout(null);
		panelSetting();

		// titleなどの設定
		frameSetting();

		// 全体のコンパイル数表示
		setCompileErrorNumber();

		// ボタンを配置する
		setMiniGraphButton();

		// レイアウトした配置でコンテンツを追加
		rootPanel.add(headerPanel, BorderLayout.NORTH);
		rootPanel.add(buttonEreaPanel, BorderLayout.SOUTH);
		add(rootPanel);
		// getContentPane().add(rootPanel, BorderLayout.CENTER);
		// TODO: Windowサイズ変更に対応できるようにすること
		// this.addWindowListener(new WindowAdapter() {
		// public void windowStateChanged(WindowEvent e) {
		//
		// }
		// });
	}

	private void panelSetting() {
		rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));
		rootPanel.setSize(new Dimension(width, height));
		headerPanel.setLayout(new BorderLayout());
		headerPanel.setSize(new Dimension(width, height / 16));
		// buttonEreaPanel.setLayout(new BoxLayout(buttonEreaPanel,
		// BoxLayout.X_AXIS));
		buttonEreaPanel.setLayout(new FlowLayout());
		buttonEreaPanel.setSize(new Dimension(width, height * 15 / 16));
	}

	private void frameSetting() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(width, height);
		setTitle(APP_NAME + " " + VERSION);
	}

	private void setCompileErrorNumber() {
		JLabel label = new JLabel();
		String string = "あなたのこれまでの総コンパイルエラー数 ： " + manager.getTotalErrorCount();
		label.setText(string);
		// CCAchivementButton achivementButton = new CCAchivementButton(manager,
		// label);
		// achivementButton.setBounds(10, 5, 350, 25);
		// label.setBounds(10, 5, 350, 25);

		// label の背景を設定する場合は背景を不透明にする処理を加えること
		// label.setBackground(Color.yellow);
		// label.setOpaque(true);
		headerPanel.add(label, BorderLayout.WEST);
		// rootPanel.add(achivementButton);
	}

	private void setMiniGraphButton() {
		ArrayList<CCErrorElementButton2> buttons = new ArrayList<CCErrorElementButton2>();

		// エラーIDごとの数値を書き込み、ボタンを実装する
		for (CCCompileErrorList list : manager.getAllLists()) {
			CCErrorElementButton2 button = new CCErrorElementButton2(
					buttonWidth, buttonHeight, list, manager.getLibDir(),
					manager.getBase());
			buttons.add(button);
		}

		// ボタンを配置する
		int i = 1;
		for (int x = 0; x < width; x += buttonWidth) {
			JPanel verticalPanel = new JPanel();
			verticalPanel.setLayout(new BoxLayout(verticalPanel,
					BoxLayout.Y_AXIS));

			for (int y = height / 16; y < height - buttonHeight; y += buttonHeight) {
				if (manager.getAllLists().size() >= i) {
					if (manager.getList(i).getErrors().size() > 0) {
						verticalPanel.add(buttons.get(i - 1));
					} else {
						verticalPanel.add(setEmptyButton());
					}
					i++;
				} else {
					verticalPanel.add(setEmptyButton());
				}
			}

			buttonEreaPanel.add(verticalPanel);
		}
	}

	// クリックできないボタンを作成
	private JButton setEmptyButton() {
		JButton emptyButton = new JButton("未発生");
		emptyButton.setEnabled(false);
		emptyButton.setToolTipText("未発生です");
		emptyButton.setBackground(Color.GRAY);
		emptyButton.setSize(new Dimension(buttonWidth, buttonHeight));
		// emptyButton.setBounds(x, y, buttonWidth, buttonHeight);
		// rootPanel.add(emptyButton);
		return emptyButton;
	}
}