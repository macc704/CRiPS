package src.coco.view;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import ppv.app.datamanager.PPProjectSet;
import src.coco.model.CCCompileErrorList;
import clib.common.filesystem.CDirectory;

public class CCErrorElementButton2 extends JButton {

	/**
	 * minigraphを表示する chartPanelがActionListenerに対応していないので、MouseListenerで実装
	 */

	private static final long serialVersionUID = 1L;

	private int width;
	private int height;

	private CCCompileErrorList list;

	private CDirectory baseDir;
	private CDirectory libDir;
	private PPProjectSet ppProjectSet;

	private ChartPanel chartpanel;
	private JFreeChart chart;

	private List<CCGraphFrame> graphframes = new ArrayList<CCGraphFrame>();

	public CCErrorElementButton2(CCCompileErrorList list, int width,
			int height, CDirectory baseDir, CDirectory libDir,
			PPProjectSet ppProjectSet) {
		this.list = list;
		this.width = width;
		this.height = height;
		this.baseDir = baseDir;
		this.libDir = libDir;
		this.ppProjectSet = ppProjectSet;

		super.setPreferredSize(new Dimension(width, height));
		super.setLayout(null);
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				chartpanel.setBounds(1, 1, getWidth(), getHeight());
				validate();
			}
		});
		makeGraph();
	}

	private void makeGraph() {
		// 日本語が文字化けしないテーマ(フォント指定で避けたので使わない)
		// ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());

		// グラフデータを設定する
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < list.getErrors().size(); i++) {
			dataset.addValue(list.getErrors().get(i).getCorrectionTime(),
					"修正時間", Integer.toString(i + 1));
		}

		// TODO: グラフの生成 messageが長すぎる場合、短くする処理をどうするか
		// 10文字くらいで区切って、ToolTipで補完する手段を考え中
		String message = list.getMessage();
		if (list.getMessage().length() > 10) {
			message = message.substring(0, 9) + "...";
		}

		chart = ChartFactory.createLineChart(message, "修正回数", "修正時間", dataset,
				PlotOrientation.VERTICAL, false, false, false);
		// フォント指定しないと文字化けする
		chart.getTitle().setFont(new Font("Font2DHandle", Font.PLAIN, 20));

		// 背景色のセット
		chart.setBackgroundPaint(new CCGraphBackgroundColor().graphColor(list
				.getRare()));

		// Plotクラスを準備
		CategoryPlot plot = chart.getCategoryPlot();

		// 縦軸の設定 ・ 軸は整数値のみを指すようにする
		NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
		numberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		numberAxis.setVerticalTickLabels(false);
		numberAxis.setAutoRangeStickyZero(true);
		numberAxis.setRangeWithMargins(0, 120);
		numberAxis.setLabelFont(new Font("Font2DHandle", Font.PLAIN, 16));

		// x軸の設定
		CategoryAxis domainAxis = (CategoryAxis) plot.getDomainAxis();
		domainAxis.setLabelFont(new Font("Font2DHandle", Font.PLAIN, 16));

		// プロットの設定
		LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot
				.getRenderer();
		renderer.setSeriesPaint(0, ChartColor.RED);
		renderer.setSeriesStroke(0, new BasicStroke(1));
		renderer.setSeriesShapesVisible(0, true);

		chartpanel = new ChartPanel(chart);
		chartpanel.setBounds(0, 0, width, height);

		chartpanel.addChartMouseListener(new ChartMouseListener() {
			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
			}

			@Override
			public void chartMouseClicked(ChartMouseEvent arg0) {
				CCGraphFrame graphframe = new CCGraphFrame(list, baseDir,
						libDir, ppProjectSet);
				graphframe.setVisible(true);
				graphframes.add(graphframe);
			}
		});

		// TODO: ToolTipが上手く表示できない
		chartpanel.setToolTipText(list.getErrors().size() + " : "
				+ list.getMessage());
		chartpanel.setDisplayToolTips(true);
		// デバッグ用
		// System.out.println(chartpanel.getToolTipText());

		add(chartpanel);
	}

	public void closeGraphFrame() {
		for (CCGraphFrame frame : graphframes) {
			frame.dispose();
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