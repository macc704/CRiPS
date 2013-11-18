package src.coco.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolTip;

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

import src.coco.model.CCCompileError;
import src.coco.model.CCCompileErrorList;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileElement;
import clib.common.filesystem.CPath;

public class CCGraphFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int width = 680;
	private int height = 560;

	private JPanel rootPanel = new JPanel();
	private CCCompileErrorList list;

	private CDirectory baseDir;

	// default
	public CCGraphFrame(CCCompileErrorList list, CDirectory baseDir) {
		this.list = list;
		this.baseDir = baseDir;
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) (d.width * 0.75);
		height = (int) (d.height * 0.75);
		initialize();
	}

	public void openGraph() {
		makeGraph();
		makeSourceList();
		add(rootPanel);
		getContentPane().add(rootPanel, BorderLayout.CENTER);
		pack();
	}

	private void initialize() {
		// rootPanel.setLayout(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(width, height);
		setTitle(CCMainFrame2.APP_NAME + " " + CCMainFrame2.VERSION + " - "
				+ list.getMessage() + " の詳細");
	}

	private void makeGraph() {
		// 日本語が文字化けしないテーマ
		// ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		// グラフデータを設定する
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < list.getErrors().size(); i++) {
			dataset.addValue(list.getErrors().get(i).getCorrectTime(), "修正時間",
					Integer.toString(i + 1));
		}

		// グラフの生成
		JFreeChart chart = ChartFactory.createLineChart(list.getMessage()
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

	// TODO リスト部分の実装
	private void makeSourceList() {
		// java7からDefaultListModelに格納するクラスを指定しなければならない
		DefaultListModel<String> model = new DefaultListModel<String>();
		for (int i = 0; i < list.getErrors().size(); i++) {
			model.addElement((i + 1) + " 回目の修正時間 ： "
					+ list.getErrors().get(i).getCorrectTime() + "秒");
		}

		final JList<String> jlist = new JList<String>(model);
		jlist.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				// 左クリック二回でオープンする
				if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() >= 2) {
					// 選択された要素がリストの何番目であるのかを取得し，その時のコンパイルエラー情報を取得
					int index = jlist.getSelectedIndex();
					CCCompileError compileError = list.getErrors().get(index);

					// ファイルパスに必要な要素の取り出し
					String projectname = compileError.getProjectname();
					String beginTime = String.valueOf(compileError
							.getBeginTime());
					String filename = compileError.getFilename();

					// コンパイルエラー発生時のファイルパスを設定
					// TODO Eclipse対応できてない
					// TODO ハードコーディング問題
					CPath path = new CPath("\\ppv.data\\cash\\hoge\\"
							+ projectname + "\\" + beginTime
							+ "\\ProjectBase\\" + filename);

					// 論プロからの起動を想定，CocoViewerのみではbaseDirはnull
					if (baseDir == null) {
						System.out.println("baseDir null");
					} else {
						// プログラムソースを捜し，それがnullでないこと＋ファイルであることを確認
						CFileElement fileElement = baseDir.findChild(path);
						if (fileElement.isFile() && fileElement != null) {
							CFile file = (CFile) fileElement;
							System.out.println("find!  "
									+ list.getErrors().get(index)
											.getBeginTime());
							// TODO PPProjectViewerFrameを使って表示できるようにしよう
							// プログラムファイルの内容読み込み
							StringBuffer buf = new StringBuffer();
							String line = "";
							if ((line = file.loadText()) != null) {
								buf.append(line);
								buf.append("\n");
								System.out.println(line);
							}

							// 読み込んだものを表示
							JTextPane textPane = new JTextPane();
							textPane.setText(buf.toString());
							textPane.setCaretPosition(0);
							JFrame frame = new JFrame();
							frame.add(textPane, BorderLayout.CENTER);
							frame.pack();
							frame.setVisible(true);
						}
					}
				}
			}
		});

		JScrollPane scrollPanel = new JScrollPane();
		scrollPanel.getViewport().setView(jlist);
		scrollPanel.setPreferredSize(new Dimension(width / 4, height / 3));

		rootPanel.add(scrollPanel, BorderLayout.EAST);
	}
}