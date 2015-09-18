package coco.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import clib.common.compiler.CJavaCompilerFactory;
import coco.model.CCCompileErrorKind;
import coco.model.CCCompileErrorManager;
import pres.loader.logmodel.PRCocoViewerLog;

/*
 * 
 * 2013/12/19 version 0.0.1  論プロに組み込んだバージョン
 * 2014/05/30 version 0.1.0  諸機能の追加
 * 								・ソースコードオープン機能
 * 								・グラフ概形切り替えボタン
 * 								・操作ログをpres2.logに書き出し
 * 								・EclipsePlugin対応
 * 2014/07/14 version 0.1.1 ソースコード比較画面の対象ファイルをそのファイルだけか，プロジェクトごとかを選択可能に
 * 							CCGraphFrameのソースコードをリファクタリング
 * 
 */

public class CCMainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String APP_NAME = "CoCo Viewer";
	public static final String VERSION = "0.1.1";

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
	private ArrayList<CCErrorElementButton> buttons = new ArrayList<CCErrorElementButton>();
	private JMenuBar menuBar = new JMenuBar();
	private JMenu menu;
	private Action actionCreateCocoData;
	
	public CCMainFrame(CCCompileErrorManager manager) {
		this.manager = manager;
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.width = d.width * 3 / 4;
		this.height = d.height * 3 / 4;
		this.buttonWidth = this.width / 8;
		this.buttonHeight = this.height / 8;
		initialize();
	}

	private void initialize() {
		rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));
		rootPanel.setSize(new Dimension(width, height));

		frameSetting();

		// add all compile error data and time range button
		setHeader();

		// window
		setTablePanel();

		// put rootpanel on dialog
		getContentPane().add(rootPanel, BorderLayout.CENTER);
	}

	private void frameSetting() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(width, height);
		setTitle(APP_NAME + " " + VERSION);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				for (CCErrorElementButton button : buttons) {
					button.closeGraphFrame();
				}
				manager.writePresLog(PRCocoViewerLog.SubType.COCOVIEWER_CLOSE);
			}
		});
		
		setMenuBarInit();
	}
	
	private void setMenuBarInit() {
		setJMenuBar(menuBar);
		menu = new JMenu("Menu");
		
		{
			Action action = new AbstractAction() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					createData();
				}
			};
			action.putValue(Action.NAME, "Create CocoData");
			action.setEnabled(true);
			actionCreateCocoData = action;
		}
		
		menu.add(actionCreateCocoData);
		menuBar.add(menu);		
	}
	
	private void createData() {
		if (!CJavaCompilerFactory.hasEmbededJavaCompiler()) {
			int res = JOptionPane.showConfirmDialog(null,
					"JDKを利用していない場合，予期しない動作や処理時間が長くなる可能性があります．よろしいでしょうか？", "コンパイラのチェック",
					JOptionPane.OK_CANCEL_OPTION);
			if (res != JOptionPane.OK_OPTION) {
				return;
			}
		}
	}

	private void setHeader() {
		JPanel headerpanel = new JPanel();
		headerpanel.setLayout(new BorderLayout());
		headerpanel.setMaximumSize(new Dimension(width, height / 24));

		setCompileErrorNumber(headerpanel);
		setChangeGraphRangeButton(headerpanel);

		rootPanel.add(headerpanel, BorderLayout.NORTH);
	}
	
	private void setCompileErrorNumber(JPanel panel) {
		JLabel label = new JLabel();

		int count = manager.getTotalErrorCount();
		long time = manager.getTotalErrorCorrectionTime();
		int wokingtime = manager.getTotalWorkingTime() * 60;
		double rate = manager.getCompileErrorCorrectionTimeRate();

		String timeStr = timeToString(time);
		String workingStr = timeToString(wokingtime);
		long avg = 0;
		if (count != 0) {
			avg = time / count;
		}

		String string = "<html>これまでのコンパイルエラー修正数: " + count
				+ "　　これまでのコンパイルエラー修正時間累計: " + timeStr + "<br>"
				+ "１つあたり修正時間平均: " + avg + "秒" + "  これまでの総作業時間:  " + workingStr
				+ "<br>" + "  コンパイルエラー修正時間割合:  " + rate + "%" + "</html>";

		label.setText(string);
		label.setMaximumSize(new Dimension(width, height / 24));
		label.setFont(new Font("Font2DHandle", Font.BOLD, 16));

		LineBorder lineborder = new LineBorder(Color.YELLOW, 2, true);
		label.setBorder(lineborder);

		panel.add(label, BorderLayout.WEST);
	}

	private String timeToString(long time) {
		long hour = time / 60 / 60;
		long minute = (time / 60) % 60;
		long second = time % 60;
		String timeStr = hour + "時間" + minute + "分" + second + "秒";
		return timeStr;
	}

	private void setChangeGraphRangeButton(JPanel panel) {
		String[] labels = { "120秒固定モード", "グラフ概形モード  " };
		final JComboBox<String> comboBox = new JComboBox<String>(labels);

		comboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (comboBox.getSelectedIndex() == 0) {
					for (CCErrorElementButton button : buttons) {
						button.changeLockedRange();
					}
				} else if (comboBox.getSelectedIndex() == 1) {
					for (CCErrorElementButton button : buttons) {
						button.changeAutoRange();
					}
				} else {
					throw new RuntimeException("グラフモードが選択されていません");
				}
			}
		});

		panel.add(comboBox, BorderLayout.EAST);
	}

	private void setTablePanel() {

		for (CCCompileErrorKind list : manager.getAllKinds()) {
			CCErrorElementButton button = new CCErrorElementButton(manager,
					list, buttonWidth, buttonHeight);
			buttons.add(button);
		}

		JPanel buttonsEreaPanel = new JPanel();
		buttonsEreaPanel.setLayout(new GridLayout((height * 15 / 16)
				/ buttonHeight, width / buttonWidth));
		int i = 1;
		int errorkindsCount = manager.getAllKinds().size();
		for (int x = 0; x < Math.sqrt(errorkindsCount); x++) {
			for (int y = 0; y < Math.sqrt(errorkindsCount); y++) {
				if (manager.getAllKinds().size() >= i) {
					if (manager.getKind(i).getErrors().size() > 0) {
						buttonsEreaPanel.add(buttons.get(i - 1));
					} else {
						buttonsEreaPanel
								.add(setEmptyButton(manager.getKind(i)));
					}
					i++;
				} else {
					buttonsEreaPanel.add(setEmptyButton(null));
				}
			}
		}

		JScrollPane scrollPanel = new JScrollPane(buttonsEreaPanel);
		rootPanel.add(scrollPanel, BorderLayout.SOUTH);
	}

	// TODO: クリック出来ないボタンの作成は，CCErrorElementButton2内のほうが良い
	private JButton setEmptyButton(CCCompileErrorKind list) {
		String message = "";
		if (list != null) {
			message = list.getMessage();
		}
		String rare = "";
		if (list != null) {
			rare = "(レア度" + Integer.toString(list.getRare()) + ")";
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