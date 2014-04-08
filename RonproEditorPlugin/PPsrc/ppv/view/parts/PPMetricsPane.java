/*
 * PPRunPointPanel.java
 * Created on 2011/06/22
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.view.parts;

import java.awt.BorderLayout;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTable;

import pres.loader.model.IPLUnit;
import pres.loader.utils.PLMetricsCalculator;
import clib.common.model.ICModelChangeListener;
import clib.common.time.CTime;
import clib.view.table.model.CMapTableModel;
import clib.view.timeline.model.CTimeModel;

/**
 * @author macchan
 */
public class PPMetricsPane extends JPanel {

	private static final long serialVersionUID = 1L;

	private CTimeModel timeModel;
	private IPLUnit unit;

	private Map<String, Object> metrics = new LinkedHashMap<String, Object>();
	private CMapTableModel<String, Object> mapTableModel = new CMapTableModel<String, Object>(
			metrics);
	// private CCommonTablePanel tablePanel = new CCommonTablePanel();
	private JTable table = new JTable();

	private PLMetricsCalculator calc;
	private String divider = "  /  ";

	/**
	 * @param timePane
	 * @param project
	 */
	public PPMetricsPane(CTimeModel timeModel, IPLUnit unit) {
		this.timeModel = timeModel;
		this.unit = unit;
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		table.setModel(mapTableModel);
		add(table);
		timeModel.addModelListener(new ICModelChangeListener() {
			public void modelUpdated(Object... args) {
				refresh();
			}
		});

		this.calc = new PLMetricsCalculator(unit);
	}

	void refresh() {
		CTime time = timeModel.getTime();
		metrics.put("Time", calc.getLeadingTime(time).getMajorString()
				+ divider + calc.getLeadingTime().getMajorString());
		metrics.put("WorkingTime", calc.getWorkingTime(time).getMajorString()
				+ divider + calc.getWorkingTime().getMajorString());
		metrics.put("isWorking", calc.isWorking(time));
		metrics.put("LineCount",
				calc.getLineCount(time) + divider + calc.getLineCount());
		metrics.put("CompileCount",
				calc.getCompileCount(time) + divider + calc.getCompileCount());
		metrics.put("SaveCount",
				calc.getSaveCount(time) + divider + calc.getSaveCount());
		metrics.put("RunCount",
				calc.getRunCount(time) + divider + calc.getRunCount());
		metrics.put("AllEventCount", "0" + divider + calc.getAllEventCount());

		// TODO H24.1.21 保井追加 BlockEditorの作業時間をメトリックスパネルに表示
		blockEditorMetrics(time);
		
		// デバッガ関連のメトリクスを表示
		debuggerMetrics(time);

		mapTableModel.refresh();
	}

	/**
	 * TODO H24.1.21 保井追加 BlockEditorの作業時間をメトリックスパネルに表示
	 * 
	 * @param time
	 */
	private void blockEditorMetrics(CTime time) {
		if (calc.getBEWorkingTime().getTime() != 0) {
			metrics.put("BlockEditorWorkingTime", calc.getBEWorkingTime(time)
					.getMajorString()
					+ divider
					+ calc.getBEWorkingTime().getMajorString());
			metrics.put("isBlockEditorWorking", calc.isBEWorking(time));
			metrics.put("AddBlockCount",
					"0" + divider + calc.getAddBlockCount());
			metrics.put("RemoveBlockCount",
					"0" + divider + calc.getRemoveBlockCount());
		}
	}
	
	/**
	 * デバッガ関連 by hakamata
	 * @param time
	 */
	private void debuggerMetrics(CTime time) {
		metrics.put("DebugCount", calc.getDebugCount(time) + divider + calc.getDebugCount());
		metrics.put("StepCount", calc.getStepCount(time) + divider + calc.getStepCount());
		metrics.put("PlayCount", calc.getPlayCount(time) + divider + calc.getPlayCount());
		metrics.put("DebugWorkingTime", calc.getNDWorkingTime(time).getMajorString() + divider + calc.getNDWorkingTime().getMajorString());
	}

}
