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

import src.coco.model.CCCompileErrorKind;
import src.coco.model.CCCompileErrorManager;

public class CCMainFrame2 extends JFrame {

	private static final long serialVersionUID = 1L;

	public static final String APP_NAME = "CoCo Viewer";
	public static final String VERSION = "0.0.1";

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

	private int errorkindsCount;

	public CCMainFrame2(CCCompileErrorManager manager, int errorkindsCount) {
		this.manager = manager;
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.width = d.width * 3 / 4;
		this.height = d.height * 3 / 4;
		this.buttonWidth = this.width / 8;
		this.buttonHeight = this.height / 8;
		this.errorkindsCount = errorkindsCount;
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
		// setAchivementsButton(headerPanel);

		rootPanel.add(headerPanel, BorderLayout.NORTH);
	}

	private void setCompileErrorNumber(JPanel headerPanel) {
		JLabel label = new JLabel();
		if (manager.getTotalErrorCount() == 0) {
			throw new RuntimeException("CocoViewer用データが作成されていない可能性があります");
		}
		int count = manager.getTotalErrorCount();
		long time = manager.getTotalErrorCorrectionTime();
		int wokingtime = manager.getTotalWorkingTime() * 60;
		double rate = manager.getCompileErrorCorrectionTimeRate();

		String timeStr = timeToString(time);
		String workingStr = timeToString(wokingtime);
		long avg = time / count;

		String string = "<html>これまでのコンパイルエラー修正数: " + count
				+ "　　これまでのコンパイルエラー修正時間累計: " + timeStr + "<br>"
				+ "１つあたり修正時間平均: " + avg + "秒" + "  これまでの総作業時間:  " + workingStr
				+ "<br>" + "  コンパイルエラー修正時間割合:  " + rate + "%" + "</html>";

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

	private String timeToString(long time) {
		long hour = time / 60 / 60;
		long minute = (time / 60) % 60;
		long second = time % 60;
		String timeStr = hour + "時間" + minute + "分" + second + "秒";
		return timeStr;
	}

	// private void setAchivementsButton(JPanel headerPanel) {
	// // TODO Hard Coding
	// JLabel label = new JLabel("実績ボタン");
	// CCAchivementButton achivementButton = new CCAchivementButton(manager,
	// "Achivement.csv", label);
	// headerPanel.add(achivementButton, BorderLayout.EAST);
	// achivementButton.setVisible(false);
	// }

	private void setButtonsPanel() {
		ArrayList<CCErrorElementButton2> buttons = new ArrayList<CCErrorElementButton2>();

		// エラーIDごとの数値を書き込み、ボタンを実装する
		for (CCCompileErrorKind allKinds : manager.getAllKinds()) {
			CCErrorElementButton2 button = new CCErrorElementButton2(
					buttonWidth, buttonHeight, allKinds, manager.getLibDir(),
					manager.getBase(), manager.getppProjectSet());
			buttons.add(button);
		}

		// ボタンを配置する
		JPanel buttonEreaPanel = new JPanel();
		buttonEreaPanel.setLayout(new GridLayout(
				((height * 15 / 16) / buttonHeight), (width / buttonWidth)));

		int i = 1;
		for (int x = 0; x < Math.sqrt(errorkindsCount); x++) {
			for (int y = 0; y < Math.sqrt(errorkindsCount); y++) {
				if (manager.getAllKinds().size() >= i) {
					if (manager.getKind(i).getErrors().size() > 0) {
						buttonEreaPanel.add(buttons.get(i - 1));
					} else {
						buttonEreaPanel.add(setEmptyButton(manager.getKind(i)));
					}
					i++;
				} else {
					buttonEreaPanel.add(setEmptyButton(null));
				}
			}
		}

		// int i = 1;
		// for (int x = 0; x < width || manager.getAllLists().size() > i; x +=
		// buttonWidth) {
		// for (int y = height / 16; y < height - buttonHeight; y +=
		// buttonHeight) {
		// if (manager.getAllLists().size() >= i) {
		// if (manager.getList(i).getErrors().size() > 0) {
		// buttonEreaPanel.add(buttons.get(i - 1));
		// } else {
		// buttonEreaPanel.add(setEmptyButton());
		// }
		// i++;
		// } else {
		// buttonEreaPanel.add(setEmptyButton());
		// }
		// }
		// }

		JScrollPane scrollPanel = new JScrollPane(buttonEreaPanel);
		rootPanel.add(scrollPanel, BorderLayout.SOUTH);
	}

	private JButton setEmptyButton(CCCompileErrorKind kind) {
		String message = "";
		if (kind != null) {
			message = kind.getMessage();
		}
		String rare = "";
		if (kind != null) {
			rare = "(レア度" + Integer.toString(kind.getRare()) + ")";
		}
		JButton emptyButton = new JButton("<html><center>未発生</center><br/>"
				+ message + rare + "</html>");
		emptyButton.setEnabled(false);
		emptyButton.setToolTipText("未発生です");
		emptyButton.setBackground(Color.GRAY);
		emptyButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		return emptyButton;
	}
}