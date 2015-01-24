package coco.view;

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

import coco.model.CCCompileErrorKind;
import coco.model.CCCompileErrorManager;
import pres.loader.logmodel.PRCocoViewerLog;

public class CCErrorElementButton2 extends JButton {

	/**
	 * minigraphを表示する chartPanelがActionListenerに対応していないので、MouseListenerで実装
	 */

	private static final long serialVersionUID = 1L;

	private int width;
	private int height;
	private String lang = "JP";

	private CCCompileErrorManager manager;
	private CCCompileErrorKind list;

	private ChartPanel chartpanel;
	private JFreeChart chart;

	private List<CCGraphFrame> graphframes = new ArrayList<CCGraphFrame>();

	public CCErrorElementButton2(CCCompileErrorManager manager,
			CCCompileErrorKind list, int width, int height, String lang) {
		this.manager = manager;
		this.list = list;
		this.width = width;
		this.height = height;
		this.lang = lang;

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
		// グラフデータ設定
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < list.getErrors().size(); i++) {
			if(lang == "JP") {
			dataset.addValue(list.getErrors().get(i).getCorrectionTime(),
					"修正時間", Integer.toString(i + 1));
			} else {
				dataset.addValue(list.getErrors().get(i).getCorrectionTime(),
						"Correction Time", Integer.toString(i + 1));	
			}
		}

		// TODO: ミニグラフのタイトルメッセージ表示 現在は10文字のみ表示
		String message = list.getMessage();
		if (list.getMessage().length() > 10) {
			message = message.substring(0, 9) + "...";
		}

		if(lang == "JP") {
		chart = ChartFactory.createLineChart(message, "修正回数", "修正時間", dataset,
				PlotOrientation.VERTICAL, false, false, false);
		} else {
			chart = ChartFactory.createLineChart(message, "number", "time", dataset,
					PlotOrientation.VERTICAL, false, false, false);
		}
		chart.getTitle().setFont(new Font("Font2DHandle", Font.PLAIN, 20));

		// 背景色セット
		chart.setBackgroundPaint(new CCGraphBackgroundColor().graphColor(list
				.getRare()));

		// Plotクラスを準備（順番重要）
		CategoryPlot plot = chart.getCategoryPlot();

		// y軸 ・ 軸は整数値のみ
		NumberAxis numberAxis = (NumberAxis) plot.getRangeAxis();
		numberAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		numberAxis.setVerticalTickLabels(false);
		numberAxis.setAutoRangeStickyZero(true);
		numberAxis.setRangeWithMargins(0, 120);
		numberAxis.setLabelFont(new Font("Font2DHandle", Font.PLAIN, 16));

		// x軸
		CategoryAxis domainAxis = (CategoryAxis) plot.getDomainAxis();
		domainAxis.setLabelFont(new Font("Font2DHandle", Font.PLAIN, 16));

		// プロット設定
		LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot
				.getRenderer();
		renderer.setSeriesPaint(0, ChartColor.RED);
		renderer.setSeriesStroke(0, new BasicStroke(1));
		renderer.setSeriesShapesVisible(0, true);

		// グラフをchartpanelに載せる
		chartpanel = new ChartPanel(chart);
		chartpanel.setBounds(0, 0, width, height);

		// クリック時に修正詳細画面を表示
		chartpanel.addChartMouseListener(new ChartMouseListener() {
			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
			}

			@Override
			public void chartMouseClicked(ChartMouseEvent arg0) {
				int errorID = -1;
				try {
					errorID = list.getErrors().get(0).getErrorID();
				} catch (Exception e) {
					e.printStackTrace();
				}

				CCGraphFrame graphframe = new CCGraphFrame(list, manager,
						errorID, lang);
				graphframe.setVisible(true);
				graphframes.add(graphframe);

				manager.writePresLog(PRCocoViewerLog.SubType.DETAIL_OPEN,
						errorID);
			}
		});

		// TODO: ToolTip表示
		chartpanel.setToolTipText(list.getErrors().size() + " : "
				+ list.getMessage());
		chartpanel.setDisplayToolTips(true);

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