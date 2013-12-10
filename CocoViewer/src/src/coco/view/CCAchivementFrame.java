package src.coco.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import src.coco.controller.CCAchivementLoader;
import src.coco.model.CCAchivementData;
import src.coco.model.CCCompileErrorManager;

// TODO わかりやすく面白いレイアウト
public class CCAchivementFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int width = 720;
	private int height = 480;

	private CCCompileErrorManager manager;
	private String achiveFilename;

	public CCAchivementFrame(CCCompileErrorManager manager,
			String achiveFilename) {
		super();
		this.manager = manager;
		this.achiveFilename = achiveFilename;

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.width = d.width / 2;
		this.height = d.height / 2;

		frameSetting();
		setAchivmentPanel();
	}

	private void frameSetting() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(width, height);
		setTitle(CCMainFrame2.APP_NAME + " " + CCMainFrame2.VERSION + " 実績画面");
	}

	private void setAchivmentPanel() {
		JPanel panel = setTable();
		JScrollPane scrollpanel = new JScrollPane(panel);
		add(scrollpanel, BorderLayout.CENTER);
	}

	private JPanel setTable() {
		ArrayList<CCAchivementData> data = new ArrayList<CCAchivementData>();
		CCAchivementLoader loader = new CCAchivementLoader(data);
		loader.load(achiveFilename);

		JPanel panel = compileErrorAchivementsCheck(data);
		return panel;
	}

	private JPanel compileErrorAchivementsCheck(ArrayList<CCAchivementData> data) {
		JPanel achivementPanel = new JPanel();
		achivementPanel.setLayout(new BoxLayout(achivementPanel,
				BoxLayout.Y_AXIS));
		// TODO 処理がスマートじゃない 要リファクタリング
		for (CCAchivementData datum : data) {
			JPanel datumPanel = new JPanel();
			if (datum.getProperty() == 1) {
				if (compileErrorCountAchive(datum)) {
					datumPanel.add(hirolabel(datum));
				}
			} else if (datum.getProperty() == 2) {
				if (compileErrorKindsAchive(datum)) {
					datumPanel.add(hirolabel(datum));
				}
			} else if (datum.getProperty() == 3) {
				if (compileErrorCorrectTimeAchive(datum)) {
					datumPanel.add(hirolabel(datum));
				}
			}

			achivementPanel.add(datumPanel);
		}

		return achivementPanel;
	}

	private JLabel hirolabel(CCAchivementData datum) {
		JLabel label = new JLabel(datum.getHirotitle());
		label.setToolTipText(datum.getExplanation());
		return label;
	}

	private boolean compileErrorCountAchive(CCAchivementData data) {
		// コンパイルエラー総発生回数による報酬
		int count = manager.getTotalErrorCount();
		if (count > data.getThreshold()) {
			System.out.println("ErrorCount : " + data.getHirotitle() + " : "
					+ data.getExplanation());
		}

		return count > data.getThreshold();
	}

	private boolean compileErrorKindsAchive(CCAchivementData data) {
		// コンパイルエラー種類による報酬
		int kinds = manager.getAllLists().size();
		if (kinds > data.getThreshold()) {
			System.out.println("ErrorKinds : " + data.getHirotitle() + " : "
					+ data.getExplanation());
		}

		return kinds > data.getThreshold();
	}

	private boolean compileErrorCorrectTimeAchive(CCAchivementData data) {
		// コンパイルエラー修正時間による報酬
		int correctTime = manager.getAllCorrectTime();
		if (correctTime > data.getThreshold()) {
			System.out.println("CorrectTime : " + data.getHirotitle() + " : "
					+ data.getExplanation());
		}

		return correctTime > data.getThreshold();
	}
}
