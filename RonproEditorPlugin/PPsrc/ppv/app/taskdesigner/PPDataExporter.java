/*
 * PPDataExporter.java
 * Created on 2012/05/09
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.app.taskdesigner;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ppv.app.taskdesigner.model.PPActualTask;
import ppv.app.taskdesigner.model.PPDefectFixingTask;
import ppv.app.taskdesigner.model.PPEstimatedTask;
import ppv.app.taskdesigner.model.PPTaskDesignModel;
import pres.loader.model.IPLUnit;
import pres.loader.utils.PLMetricsCalculator;
import clib.common.time.CTime;
import clib.common.time.CTimeInterval;

/**
 * @author macchan
 * 
 */
public class PPDataExporter {

	private PPTaskDesignModel model;
	private IPLUnit unit;

	/**
	 * @param manager
	 * @param unit
	 */
	public PPDataExporter(PPTaskDesignModel model, IPLUnit unit) {
		this.model = model;
		this.unit = unit;
	}

	/**
	 * @return
	 */
	public List<String[]> createActual() {
		return new A(model, unit).create();
	}

	/**
	 * @return
	 */
	public List<String[]> createCostEffectiveness() {
		return new B(model, unit).create();
	}

	/**
	 * @return
	 */
	public List<String[]> createDefect() {
		return new C(model, unit).create();
	}
}

class A {

	private PLMetricsCalculator metrics;
	private PPTaskDesignModel saveModel;
	private IPLUnit unit;

	public A(PPTaskDesignModel saveModel, IPLUnit unit) {
		this.saveModel = saveModel;
		this.unit = unit;
		this.metrics = new PLMetricsCalculator(unit);
	}

	public List<String[]> create() {
		List<String[]> data = new ArrayList<String[]>();
		data.add(createHeader());

		int count = 1;
		for (PPActualTask task : saveModel.getActualTasks()) {
			data.add(createOneLine(task, count));
			count++;
		}
		data.add(createFooter());
		return data;
	}

	private String[] createHeader() {
		// return new String[] { "No.", "開始時間", "終了時間", "作業時間", "累積時間", "作業内容",
		// "行数（区間）", "コンパイル（区間）", "実行（区間）", "行数（累積）", "コンパイル（累積）",
		// "実行（累積）" };
		return new String[] { "No.", "作業内容", "作業時間", "行数（区間）", "コンパイル（区間）",
				"実行（区間）", "時間(累積)", "行数（累積）", "コンパイル（累積）", "実行（累積）", "開始時間",
				"終了時間", };
	}

	private String[] createFooter() {
		CTime start = unit.getStart();
		String startS = start.toString();
		CTime end = unit.getEnd();
		String endS = end.toString();
		CTimeInterval wtA = metrics.getWorkingTime();
		// String wtAS = wtA.getMinuteString();
		String wtAS = Long.toString(wtA.getTime() / 1000 / 60);
		int locA = metrics.getLineCount();
		String locAS = Integer.toString(locA);
		int ccA = metrics.getCompileCount(end);
		String ccAS = Integer.toString(ccA);
		int rcA = metrics.getRunCount(end);
		String rcAS = Integer.toString(rcA);
		// return new String[] { "計", startS, endS, "--", wtAS, "--", "--",
		// "--",
		// "--", locAS, ccAS, rcAS };
		return new String[] { "計", "--", "--", "--", "--", "--", wtAS, locAS,
				ccAS, rcAS, startS, endS, };
	}

	private String[] createOneLine(PPActualTask task, int count) {
		String no = Integer.toString(count);
		CTime start = task.getStart();
		String startS = start.toString();
		CTime end = task.getEnd();
		String endS = end.toString();
		CTimeInterval wtA = metrics.getWorkingTime(end);
		String wtAS = Long.toString(wtA.getTime() / 1000 / 60);
		CTimeInterval wt = wtA.difference(metrics.getWorkingTime(start));
		String wtS = Long.toString(wt.getTime() / 1000 / 60);
		int locA = metrics.getLineCount(end);
		String locAS = Integer.toString(locA);
		int loc = locA - metrics.getLineCount(start);
		String locS = Integer.toString(loc);
		int ccA = metrics.getCompileCount(end);
		String ccAS = Integer.toString(ccA);
		int cc = ccA - metrics.getCompileCount(start);
		String ccS = Integer.toString(cc);
		int rcA = metrics.getRunCount(end);
		String rcAS = Integer.toString(rcA);
		int rc = rcA - metrics.getRunCount(start);
		String rcS = Integer.toString(rc);
		// return new String[] { no, startS, endS, wtS, wtAS, task.getName(),
		// locS, ccS, rcS, locAS, ccAS, rcAS };
		return new String[] { no, task.getName(), wtS, locS, ccS, rcS, wtAS,
				locAS, ccAS, rcAS, startS, endS };
	}
}

class B {

	private PPTaskDesignModel model;
	private PLMetricsCalculator metrics;

	private PPEstimatedTask NULL = new PPEstimatedTask("未設定");
	private Map<PPEstimatedTask, List<PPActualTask>> map = new LinkedHashMap<PPEstimatedTask, List<PPActualTask>>();

	private NumberFormat formatter = new DecimalFormat("00.0");
	private int totalEst = 0;
	private int totalAct = 0;

	public B(PPTaskDesignModel model, IPLUnit unit) {
		this.model = model;
		this.metrics = new PLMetricsCalculator(unit);
	}

	public List<String[]> create() {
		totalEst = 0;
		totalAct = 0;
		List<String[]> data = new ArrayList<String[]>();

		// 前処理
		for (PPEstimatedTask cetask : model.getEstimatedTasks()) {
			map.put(cetask, new ArrayList<PPActualTask>());
		}
		map.put(NULL, new ArrayList<PPActualTask>());
		for (PPActualTask task : model.getActualTasks()) {
			PPEstimatedTask cetask = task.getCeTask();
			if (cetask == null) {
				cetask = NULL;
			}

			List<PPActualTask> list = map.get(cetask);
			assert list != null;
			list.add(task);
		}

		// 出力
		data.add(createHeader());
		int count = 1;
		for (PPEstimatedTask cetask : map.keySet()) {
			data.add(createOneLine(cetask, count));
			count++;
		}
		data.add(createFooter());
		return data;
	}

	private String[] createOneLine(PPEstimatedTask ceTask, int count) {
		String no = Integer.toString(count);
		int total = 0;
		for (PPActualTask task : map.get(ceTask)) {
			CTimeInterval wtA = metrics.getWorkingTime(task.getEnd());
			CTimeInterval wt = wtA.difference(metrics.getWorkingTime(task
					.getStart()));
			int time = (int) (wt.getTime() / 1000 / 60);
			total += time;
		}
		int est = ceTask.getEstimation();
		String estimationS = Integer.toString(est);
		String totalS = Integer.toString(total);
		totalEst += est;
		totalAct += total;
		return new String[] { no, ceTask.getName(), estimationS, totalS,
				persent(total, est) };
	}

	private String[] createHeader() {
		return new String[] { "No.", "作業内容", "見積（分）", "実績（分）", "比率(%)" };
	}

	private String[] createFooter() {
		String totalEstS = Integer.toString(totalEst);
		String totalActS = Integer.toString(totalAct);
		return new String[] { "合計", "--", totalEstS, totalActS,
				persent(totalAct, totalEst) };
	}

	private String persent(int a, int b) {
		if (b <= 0) {
			return "(--)";
		}

		double d = ((double) a / (double) b) * 100d;
		return formatter.format(d) + "%";
	}
}

class C {

	private PPTaskDesignModel model;
	private PLMetricsCalculator metrics;

	private NumberFormat formatter = new DecimalFormat("00.0");
	private int totalTime = 0;

	public C(PPTaskDesignModel saveModel, IPLUnit unit) {
		this.model = saveModel;
		this.metrics = new PLMetricsCalculator(unit);
	}

	public List<String[]> create() {
		totalTime = 0;

		List<String[]> data = new ArrayList<String[]>();
		data.add(createHeader());

		int count = 1;
		for (PPDefectFixingTask task : model.getDefectFixingTasks()) {
			data.add(createOneLine(task, count));
			count++;
		}
		data.add(createFooter());
		return data;
	}

	private String[] createHeader() {
		return new String[] { "No.", "欠陥内容", "作業時間", "開始時間", "終了時間", };
	}

	private String[] createFooter() {
		String totalTimeS = formatter.format(totalTime / 1000d);
		return new String[] { "計", "--", totalTimeS, "--", "--" };
	}

	private String[] createOneLine(PPDefectFixingTask task, int count) {
		String no = Integer.toString(count);
		CTime start = task.getStart();
		String startS = start.toString();
		CTime end = task.getEnd();
		String endS = end.toString();
		CTimeInterval wtA = metrics.getWorkingTime(end);
		CTimeInterval wt = wtA.difference(metrics.getWorkingTime(start));
		String wtS = Long.toString(wt.getTime() / 1000 / 60);
		totalTime += wt.getTime();
		return new String[] { no, task.getName(), wtS, startS, endS };
	}
}
