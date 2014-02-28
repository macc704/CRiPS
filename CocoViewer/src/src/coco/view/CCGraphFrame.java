package src.coco.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import ppv.app.datamanager.PPDataManager;
import ppv.app.datamanager.PPProjectSet;
import pres.loader.model.IPLUnit;
import pres.loader.model.PLFile;
import pres.loader.model.PLProject;
import src.coco.model.CCCompileError;
import src.coco.model.CCCompileErrorList;
import clib.common.filesystem.CDirectory;
import clib.common.time.CTime;

public class CCGraphFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int width = 680;
	private int height = 560;

	private JPanel rootPanel = new JPanel();
	private CCCompileErrorList list;

	private JFreeChart chart;

	private CDirectory baseDir;
	private CDirectory libDir;
	private PPProjectSet ppProjectSet;

	private List<CCSourceCompareViewer> sourceviewers = new ArrayList<CCSourceCompareViewer>();

	// default
	public CCGraphFrame(CCCompileErrorList list, CDirectory baseDir,
			CDirectory libDir, PPProjectSet ppProjectSet) {
		this.list = list;
		this.baseDir = baseDir;
		this.libDir = libDir;
		this.ppProjectSet = ppProjectSet;
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) (d.width * 0.6);
		height = (int) (d.height * 0.6);
		initialize();
		setGraphAndTable();
	}

	private void setGraphAndTable() {
		rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.X_AXIS));

		setGraph();
		setWestPanel();

		add(rootPanel);
		getContentPane().add(rootPanel, BorderLayout.CENTER);
		pack();
	}

	private void setWestPanel() {
		JPanel westpanel = new JPanel();
		westpanel.setLayout(new BorderLayout());

		setChangeGraphRangeComboBox(westpanel);
		setSourceTable(westpanel);

		rootPanel.add(westpanel);
	}

	private void initialize() {
		// rootPanel.setLayout(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(width, height);
		setTitle(CCMainFrame2.APP_NAME + " " + CCMainFrame2.VERSION + " - "
				+ list.getMessage() + " の詳細");

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				closeSourceViewers();
			}
		});
	}

	private void setGraph() {
		// 日本語が文字化けしないテーマ
		// ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		// グラフデータを設定する
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < list.getErrors().size(); i++) {
			dataset.addValue(list.getErrors().get(i).getCorrectionTime(),
					"修正時間", Integer.toString(i + 1));
		}

		// グラフの生成
		chart = ChartFactory.createLineChart(list.getMessage()
				+ "の修正時間   レア度: " + list.getRare(), "修正回数", "修正時間", dataset,
				PlotOrientation.VERTICAL, true, true, false);

		// フォント指定しないと文字化けする
		chart.getTitle().setFont(new Font("Font2DHandle", Font.PLAIN, 20));
		chart.getLegend().setItemFont(new Font("Font2DHandle", Font.PLAIN, 16));

		// 背景色のセット
		chart.setBackgroundPaint(new CCGraphBackgroundColor().graphColor(list
				.getRare()));

		// TODO: CategoryPlotを継承してクリック可能にして使える情報を増やすこと
		CategoryPlot plot = chart.getCategoryPlot();

		// y軸の設定 ・ 軸は整数値のみを指すようにする
		NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
		numberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		numberAxis.setVerticalTickLabels(false);
		numberAxis.setAutoRangeStickyZero(true);
		numberAxis.setRange(0, 120);
		numberAxis.setLabelFont(new Font("Font2DHandle", Font.PLAIN, 16));

		// x軸の設定
		CategoryAxis domainAxis = (CategoryAxis) plot.getDomainAxis();
		domainAxis.setLabelFont(new Font("Font2DHandle", Font.PLAIN, 16));

		// プロットの設定
		LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot
				.getRenderer();
		renderer.setSeriesPaint(0, ChartColor.RED);
		renderer.setSeriesStroke(0, new BasicStroke(2));
		renderer.setSeriesShapesVisible(0, true);

		// グラフをJPanel上に配置する
		ChartPanel chartpanel = new ChartPanel(chart);
		// chartpanel.setBounds(0, 0, width - 15, height - 40);

		// TODO: TIPS表示されない
		JToolTip tooltip = new JToolTip();
		chartpanel.setToolTipText(list.getErrors().size() + " : "
				+ list.getMessage());
		tooltip.setComponent(chartpanel);
		chartpanel.setDisplayToolTips(true);

		rootPanel.add(chartpanel, BorderLayout.WEST);
		// rootPanel.add(chartpanel);
	}

	private void setChangeGraphRangeComboBox(JPanel panel) {
		String[] labels = { "120秒固定モード", "グラフ概形モード" };
		final JComboBox<String> comboBox = new JComboBox<String>(labels);

		comboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CategoryPlot plot = chart.getCategoryPlot();
				NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();

				if (comboBox.getSelectedIndex() == 0) {
					numberAxis.setRange(0, 120);
				} else if (comboBox.getSelectedIndex() == 1) {
					numberAxis.setAutoRange(true);
				} else {
					throw new RuntimeException("グラフモードが選択されていません");
				}
			}
		});

		// comboBox.setSize(new Dimension(width / 12, height / 12));
		panel.add(comboBox, BorderLayout.NORTH);
	}

	private void setSourceTable(JPanel panel) {
		String[] columnNames = { "修正回数", "発生時刻", "プログラム名", "修正時間" };
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);
		for (int i = 0; i < list.getErrors().size(); i++) {
			String count = String.valueOf(i + 1);
			String time = new CTime(list.getErrors().get(i).getBeginTime())
					.toString();
			String filename = list.getErrors().get(i).getFilenameNoPath();
			String correctTime = String.valueOf(list.getErrors().get(i)
					.getCorrectionTime())
					+ "秒";

			String[] oneTableData = { count, time, filename, correctTime };
			model.addRow(oneTableData);
		}

		final JTable table = new JTable(model);
		table.setDefaultEditor(Object.class, null); // テーブルを編集不可にする

		// // java7からDefaultListModelに格納するクラスを指定しなければならない
		// DefaultListModel<String> model = new DefaultListModel<String>();
		// for (int i = 0; i < list.getErrors().size(); i++) {
		// model.addElement((i + 1) + " 回目の修正時間 ： "
		// + list.getErrors().get(i).getCorrectTime() + "秒");
		// }
		//
		// final JList<String> jlist = new JList<String>(model);

		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// 左クリック二回でオープンする
				if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() >= 2) {
					// 選択された要素がリストの何番目であるのかを取得し，その時のコンパイルエラー情報を取得
					int index = table.getSelectedRow();
					CCCompileError compileError = list.getErrors().get(index);

					// ファイルパスに必要な要素の取り出し
					String projectname = compileError.getProjectName();
					String filename = compileError.getFilename();

					if (baseDir == null) {
						System.out.println("baseDir null");
						return;
					}

					if (ppProjectSet == null) {
						PPDataManager datamanager = new PPDataManager(baseDir);
						datamanager.setLibDir(libDir);

						CDirectory projectSetDir = datamanager
								.getDataDir()
								.findDirectory(compileError.getProjectSetName());
						ppProjectSet = new PPProjectSet(projectSetDir);
						datamanager.loadProjectSet(ppProjectSet, true, true);
					}

					IPLUnit model = null;
					for (PLProject project : ppProjectSet.getProjects()) {
						if (project.getName().equals(projectname)) {
							// 単体のみ
							for (PLFile file : project.getFiles()) {
								if (file.getName().equals(filename)) {
									model = file;
								}
							}

							// そのプロジェクト全体
							// model = project.getRootPackage();
						}
					}

					if (model == null) {
						throw new RuntimeException(
								"コンパイルエラー発生時のソースコード捜索に失敗しました");
					}

					final CCSourceCompareViewer sourceviewer = new CCSourceCompareViewer(
							model);
					long beginTime = compileError.getBeginTime();
					sourceviewer.getTimelinePane().getTimeModel2()
							.setTime(new CTime(beginTime));
					long endTime = compileError.getEndTime();
					sourceviewer.getTimelinePane().getTimeModel()
							.setTime(new CTime(endTime));
					// frame.openToggleExtraView();

					sourceviewer.setBounds(50, 50, 1000, 700);
					sourceviewer.setVisible(true);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							// 青修正前，赤修正後
							sourceviewer.fitScale();
						}
					});

					sourceviewers.add(sourceviewer);
				}
			}
		});

		JScrollPane scrollPanel = new JScrollPane();
		scrollPanel.getViewport().setView(table);

		panel.add(scrollPanel, BorderLayout.CENTER);
	}

	public void closeSourceViewers() {
		for (CCSourceCompareViewer viewer : sourceviewers) {
			viewer.dispose();
		}
	}

	public void changeLockedRange() {
		CategoryPlot plot = chart.getCategoryPlot();
		NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
		numberAxis.setRangeWithMargins(0, 120);
	}

	public void changeAutoRange() {
		CategoryPlot plot = chart.getCategoryPlot();
		NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
		numberAxis.setAutoRange(true);
	}
}