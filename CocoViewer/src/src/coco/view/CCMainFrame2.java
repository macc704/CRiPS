package src.coco.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import src.coco.model.CCCompileErrorList;
import src.coco.model.CCCompileErrorManager;
import clib.common.filesystem.CDirectory;

public class CCMainFrame2 extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String APP_NAME = "CoCo Viewer";
	public static final String VERSION = "0.0.1";

	// Button Size
	private static final int ERRORBUTTONWIDTH = 100;
	private static final int ERRORBUTTONHEIGHT = 100;

	// Dialog size
	private int width = 1120;
	private int height = 740;

	// Compile Error Date
	private CCCompileErrorManager manager;

	// For GUI
	private JPanel rootPanel = new JPanel();

	public CCMainFrame2(CCCompileErrorManager manager, CDirectory baseDir) {
		this.manager = manager;
		// this.height = GraphicsEnvironment.getLocalGraphicsEnvironment()
		// .getMaximumWindowBounds().height - 25;
		// Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		// width = d.width - ERRORBUTTONWIDTH * 2;
		// height = d.height - ERRORBUTTONHEIGHT * 2;
		initialize(baseDir);
	}

	private void initialize(CDirectory baseDir) {
		// rootPanel のレイアウトをリセットする
		rootPanel.setLayout(null);

		// titleなどの設定
		frameSetting();

		// 全体のコンパイル数表示
		setCompileErrorNumber();

		// ボタンを配置する
		setMiniGraphButton(baseDir);

		// レイアウトした配置でコンテンツを追加
		getContentPane().add(rootPanel, BorderLayout.CENTER);
		// TODO: Windowサイズ変更に対応できるようにすること
		// this.addWindowListener(new WindowAdapter() {
		// public void windowStateChanged(WindowEvent e) {
		//
		// }
		// });
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
		label.setBounds(10, 5, 350, 25);

		// label の背景を設定する場合は背景を不透明にする処理を加えること
		// label.setBackground(Color.yellow);
		// label.setOpaque(true);
		rootPanel.add(label);
		// rootPanel.add(achivementButton);
	}

	private void setMiniGraphButton(CDirectory baseDir) {
		ArrayList<CCErrorElementButton2> buttons = new ArrayList<CCErrorElementButton2>();

		// エラーIDごとの数値を書き込み、ボタンを実装する
		for (CCCompileErrorList list : manager.getAllLists()) {
			CCErrorElementButton2 button = new CCErrorElementButton2(list,
					ERRORBUTTONWIDTH, ERRORBUTTONHEIGHT, baseDir);
			buttons.add(button);
		}

		// ボタンを配置する
		int i = 1;
		for (int x = 0; x < width - ERRORBUTTONWIDTH; x += ERRORBUTTONWIDTH) {
			for (int y = 40; y < height - ERRORBUTTONHEIGHT; y += ERRORBUTTONHEIGHT) {
				if (manager.getAllLists().size() >= i) {
					if (manager.getList(i).getErrors().size() > 0) {
						buttons.get(i - 1).setBounds(x, y, ERRORBUTTONWIDTH,
								ERRORBUTTONHEIGHT);
						rootPanel.add(buttons.get(i - 1));
					} else {
						setEmptyPanel(x, y);
					}
					i++;
				} else {
					setEmptyPanel(x, y);
				}
			}
		}
	}

	// クリックできないボタンを作成
	private void setEmptyPanel(int x, int y) {
		JButton emptyButton = new JButton("未発生");
		emptyButton.setEnabled(false);
		emptyButton.setToolTipText("未発生です");
		emptyButton.setBackground(Color.GRAY);
		emptyButton.setBounds(x, y, ERRORBUTTONWIDTH, ERRORBUTTONHEIGHT);
		rootPanel.add(emptyButton);
	}
}