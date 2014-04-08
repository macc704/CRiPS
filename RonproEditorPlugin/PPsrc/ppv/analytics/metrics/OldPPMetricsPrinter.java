/*
 * PPMetricsPrinter.java
 * Created on 2011/06/30
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.analytics.metrics;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ppv.app.datamanager.PPProjectSet;
import pres.loader.model.PLFile;
import pres.loader.model.PLPackage;
import pres.loader.model.PLProject;
import util.StringUtil;
import clib.common.filesystem.CFileSystem;
import clib.common.table.CCSVFileIO;

/**
 * @author macchan 作り中
 */
public class OldPPMetricsPrinter {

	private static String CAMMA = ",";

	private static String FILE_PATH = "./";
	private static String STUDENTLIST_FILE = FILE_PATH + "StudentNumber.txt";
	private static String REPORTLIST_FILE = FILE_PATH + "FileNameList.txt";
	private static String SELECTREPORT_FILE = FILE_PATH
			+ "SelectReportNameList.txt";
	private static String ABSORPTION_FILE = FILE_PATH + "Absorption.txt";
	private static String COMBINE_FILE = FILE_PATH + "Combine.csv";

	private List<String> students; // 学生番号リスト
	private List<String> noSubmitStudents; // 課題未提出学生番号リスト

	private int reportNum; // レポート回を指定
	private List<String[]> reportList;
	private List<List<String>> selectReportList;
	private HashMap<String, String> absorptionMap; // key:correction value:wrong
	private String[][] csvFile;

	private PrintWriter pw;

	// private NumberFormat formatter = new DecimalFormat("0.00");

	public OldPPMetricsPrinter() {
	}

	public void printMetrics(PPProjectSet projectSet, OutputStream out) {
		try {
			printMetrics0(projectSet, out);
		} catch (FileNotFoundException e) {
			new NotFoundFileDialog(e.getMessage()).open();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void printMetrics0(PPProjectSet projectSet, OutputStream out)
			throws IOException {

		// 学籍番号リスト作成
		try {
			this.students = createStudentList();
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException(STUDENTLIST_FILE);
		}

		// レポート一覧を作成
		try {
			this.reportList = createReportList();
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException(REPORTLIST_FILE);
		}

		// 選択問題のリストを作成
		createSelectReportList();

		createAbsorptionMap();

		loadCombineFile();

		this.noSubmitStudents = new ArrayList<String>(this.students);

		// PrintWriter
		pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out,
				"sjis")));
		pw.println(createHeader());
		pw.flush();

		// Projectの出力
		for (PLProject project : projectSet.getProjects()) {
			String[] line = project.getName().split("-");

			if (line.length <= 1) {
				continue;
			}
			String studentNumber = line[0] + "-" + line[1];

			this.reportNum = Integer.valueOf(line[2]);

			try {
				printOneProject(project);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			noSubmitStudents.remove(studentNumber);
		}

		// 未提出者の補完出力
		for (String studentNum : noSubmitStudents) {
			printComplement(studentNum);
		}

		pw.close();
	}

	private String createHeader() {
		StringBuffer buf = new StringBuffer();
		buf.append("StudentName");
		buf.append(CAMMA);
		buf.append("Lecture");
		buf.append(CAMMA);
		buf.append("FileName");
		buf.append(CAMMA);
		buf.append("Submitted");
		buf.append(CAMMA);
		buf.append("FileName(student)");
		buf.append(CAMMA);
		buf.append("WorkingTime");
		buf.append(CAMMA);
		buf.append("WorkingTime(min)");
		buf.append(CAMMA);
		buf.append("LineCount");
		buf.append(CAMMA);
		buf.append("CompileCount");
		buf.append(CAMMA);
		buf.append("RunCount");
		buf.append(CAMMA);
		buf.append("Time/Compile");
		buf.append(CAMMA);
		buf.append("Time/Run");
		buf.append(CAMMA);
		buf.append("BEWorkingTime(min)");
		buf.append(CAMMA);

		buf.append("CompileErrorCount");
		buf.append(CAMMA);
		buf.append("CompileErrorCT");
		buf.append(CAMMA);
		buf.append("CompileErrorRate");
		buf.append(CAMMA);

		buf.append("FKWritingCount");
		buf.append(CAMMA);
		buf.append("FKWritingTime(sec)");
		buf.append(CAMMA);
		buf.append("Overhead(%)");

		return buf.toString();
	}

	private List<String> createStudentList() throws FileNotFoundException,
			IOException {
		List<String> list = new ArrayList<String>();
		FileInputStream fs = new FileInputStream(STUDENTLIST_FILE);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));

		String line;
		while ((line = br.readLine()) != null) {
			list.add(line);
		}
		br.close();

		return list;
	}

	private List<String[]> createReportList() throws IOException {
		List<String[]> list = new ArrayList<String[]>();

		File file = new File(REPORTLIST_FILE);
		if (!file.exists()) {
			throw new FileNotFoundException();
		}
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] lines = line.split(",");
			list.add(lines);
		}
		reader.close();

		return list;
	}

	private void createSelectReportList() {

		selectReportList = new ArrayList<List<String>>();

		File file = new File(SELECTREPORT_FILE);
		if (!file.exists()) {
			return;
		}

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;

			while ((line = reader.readLine()) != null) {
				List<String> list = new ArrayList<String>();
				String[] lines = line.split(",");
				for (int i = 0; i < lines.length; i++) {
					list.add(lines[i]);
				}
				selectReportList.add(list);
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createAbsorptionMap() {

		File file = new File(ABSORPTION_FILE);
		if (!file.exists()) {
			return;
		}

		absorptionMap = new HashMap<String, String>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] lines = line.split(",");
				absorptionMap.put(lines[0], lines[1]);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadCombineFile() {
		File file = new File(COMBINE_FILE);
		if (!file.exists()) {
			return;
		}
		csvFile = CCSVFileIO.load(CFileSystem.findFile(COMBINE_FILE));
	}

	private void printComplement(String studentNum) {
		for (int i = 0; i < reportList.get(reportNum - 1).length; i++) {
			String name = reportList.get(reportNum - 1)[i];
			List<String> select = containsSelectList(name);

			if (select == null) {
				// 選択課題でない場合の補完
				complement(studentNum, "" + reportNum, name, "未提出");
			} else {
				// 選択課題である場合の補完
				int selectNum = selectReportList.indexOf(select) + 1;
				complement(studentNum, "" + reportNum, "選択課題" + selectNum,
						"未提出");
				i += select.size() - 1;
			}
		}
	}

	private void printOneProject(PLProject project) throws IOException {

		PLPackage pack = project.getRootPackage();

		// CompileErrorAnalyzerList analyzerList = new
		// CompileErrorAnalyzerList(project);
		// analyzerList.analyze();

		List<PPMetrics> metricsList = new ArrayList<PPMetrics>();
		for (PLFile f : pack.getFiles()) {
			metricsList.add(new PPMetrics(f/* , analyzerList */));
		}

		for (int i = 0; i < reportList.get(reportNum - 1).length; i++) {
			String fileName = reportList.get(reportNum - 1)[i];
			String[] lines = project.getName().split("-");
			String student = lines[0] + "-" + lines[1];
			String lecture = String.valueOf(Integer.parseInt(lines[2]) + 1);

			PPMetrics metrics = null;
			if (csvFile == null) {
				metrics = getPPMetrics(fileName, metricsList);
			} else {
				List<String> names = getFileNames(student, fileName, csvFile);
				metrics = getPPMetrics(names, metricsList);
			}

			printOneUnit(student, lecture, fileName, metrics);
		}

	}

	private void printOneUnit(String student, String lecture, String fileName,
			PPMetrics metrics) {
		StringBuffer buf = new StringBuffer();
		buf.append(student);
		buf.append(CAMMA);
		buf.append(lecture);
		buf.append(CAMMA);
		buf.append(fileName);
		buf.append(CAMMA);
		if (metrics != null) {
			buf.append("○");
			buf.append(CAMMA);
			buf.append(metrics.getMetricsPrintString());
		} else {
			buf.append("×");
		}
		pw.println(buf.toString());
		pw.flush();
	}

	private PPMetrics getPPMetrics(String fileName, List<PPMetrics> metricsList) {

		PPMetrics metrics = null;

		for (PPMetrics _metrics : metricsList) {
			String unitFileName = StringUtil.convertToHalfSize(
					_metrics.getFileName()).toUpperCase();

			if (absorptionMap.containsKey(fileName)) {
				String value = absorptionMap.get(fileName).toUpperCase();
				if (fileName.toUpperCase().equals(unitFileName)
						|| value.equals(unitFileName)) {
					if (metrics == null) {
						metrics = _metrics;
					} else {
						metrics.addMetrics(_metrics);
					}
				}
			} else {
				if (fileName.toUpperCase().equals(unitFileName)) {
					if (metrics == null) {
						metrics = _metrics;
					} else {
						metrics.addMetrics(_metrics);
					}
				}
			}
		}
		return metrics;
	}

	private List<String> getFileNames(String student, String fileName,
			String[][] lines) {
		int num = getStudentFileName(lines[0]);
		List<String> names = new ArrayList<String>();
		for (int i = 0; i < lines.length; i++) {
			String[] values = lines[i];
			if (values[0].equals(student) && values[2].equals(fileName)) {
				String[] strs = values[num].split(",");
				for (int j = 0; j < strs.length; j++) {
					names.add(strs[j].replaceAll(" ", ""));
				}
				break;
			}
		}
		return names;
	}

	private int getStudentFileName(String[] values) {
		int num = 0;
		for (int i = 0; i < values.length; i++) {
			if ("FileName(student)".equals(values[i])) {
				num = i;
			}
		}
		return num;
	}

	private PPMetrics getPPMetrics(List<String> names,
			List<PPMetrics> metricsList) {
		PPMetrics metrics = null;
		for (PPMetrics _metrics : metricsList) {
			if (names.contains(_metrics.getFileName())) {
				if (metrics == null) {
					metrics = _metrics;
				} else {
					metrics.addMetrics(_metrics);
				}
			}
		}
		return metrics;
	}

	// private void printOneProject(PLProject project) throws IOException {
	// // printOneUnit(project.getRootPackage(), out, project.getName());
	//
	// PLPackage pack = project.getRootPackage();
	//
	// CompileErrorAnalyzerList analyzerList = new CompileErrorAnalyzerList(
	// project);
	// analyzerList.analyze();
	//
	// for (int i = 0; i < reportList.get(reportNum - 1).length; i++) {
	// String fileName = reportList.get(reportNum - 1)[i];
	// IPLUnit unit = getUnit(fileName, pack);
	//
	// List<String> select = containsSelectList(fileName);
	//
	// if (unit == null) { // ドントケアを出力
	//
	// if (select == null) {
	// printOneUnit(analyzerList, null, project.getName(),
	// fileName, null);
	// } else {
	// int selectNum = selectReportList.indexOf(select) + 1;
	// if (select.indexOf(fileName) == select.size() - 1) {
	// printOneUnit(analyzerList, null, project.getName(),
	// "選択課題" + selectNum, null);
	// }
	// }
	//
	// } else {
	//
	// if (select == null) {
	// printOneUnit(analyzerList, unit, project.getName(),
	// fileName, unit.getName());
	// } else {
	// String temp = "";
	//
	// if (i < reportList.get(reportNum - 1).length - 1) {
	// i++;
	// String text = reportList.get(reportNum - 1)[i];
	// while (select.contains(text)) {
	// if (getUnit(text, pack) != null) {
	// temp += " " + text;
	// }
	// i++;
	// if (i < reportList.get(reportNum - 1).length) {
	// text = reportList.get(reportNum - 1)[i];
	// } else {
	// break;
	// }
	// }
	// i--;
	// }
	//
	// int selectNum = selectReportList.indexOf(select) + 1;
	// printOneUnit(analyzerList, unit, project.getName(), "選択課題"
	// + selectNum, fileName + temp);
	// }
	//
	// }
	//
	// }
	//
	// }

	private List<String> containsSelectList(String name) {
		for (List<String> select : selectReportList) {
			if (select.contains(name)) {
				return select;
			}
		}
		return null;
	}

	// private void printOneUnit(CompileErrorAnalyzerList analyzerList,
	// IPLUnit unit, String studentName, String fileName,
	// String unitFileName) throws IOException {
	// StringBuffer buf = new StringBuffer();
	// String[] line = studentName.split("-");
	//
	// if (unit == null) {
	// complement(line[0] + "-" + line[1], line[2], fileName, "×");
	// return;
	// }
	//
	// PLMetricsCalculator metrics = new PLMetricsCalculator(unit);
	// FailureKnowledgeAnalyzer fkAnalyzer = loadFailureKnowledgeAnalyzer(unit);
	//
	// // Student Number
	// buf.append(line[0] + "-" + line[1]);
	// buf.append(CAMMA);
	//
	// // Lecture Number
	// int lecture = Integer.parseInt(line[2]) + 1;
	// buf.append(lecture);
	// buf.append(CAMMA);
	//
	// // File Name
	// buf.append(fileName);
	// buf.append(CAMMA);
	//
	// // File Name(Student)
	// if (unitFileName.split("/").length > 1) {
	// int num = unitFileName.split("/").length;
	// buf.append(unitFileName.split("/")[num - 1]);
	// } else {
	// buf.append(unitFileName);
	// }
	// buf.append(CAMMA);
	//
	// // Submitted
	// buf.append("○");
	// buf.append(CAMMA);
	//
	// // WorkingTime
	// long workingTime = 0;
	// if (fkAnalyzer != null) {
	// workingTime = metrics.getWorkingTime().getTime()
	// - fkAnalyzer.getWritingTime();
	// } else {
	// workingTime = metrics.getWorkingTime().getTime();
	// }
	// // buf.append(metrics.getWorkingTime().getMajorString());
	// buf.append(new CTimeInterval(workingTime).getMajorString());
	// buf.append(CAMMA);
	//
	// // WorkingTime(min)
	// CTimeInterval interval = new CTimeInterval(workingTime);
	// int minute = interval.getHour() * 60 + interval.getMinute();
	// buf.append(minute);
	// buf.append(CAMMA);
	//
	// // Line Count
	// buf.append(metrics.getLineCount());
	// buf.append(CAMMA);
	//
	// // Compile Count
	// buf.append(metrics.getCompileCount());
	// buf.append(CAMMA);
	//
	// // Run Count
	// buf.append(metrics.getRunCount());
	// buf.append(CAMMA);
	//
	// long time = metrics.getWorkingTime().getTime();
	//
	// // Time/Compile
	// int compileCount = metrics.getCompileCount();
	// if (compileCount > 0) {
	// buf.append(new CTimeInterval(time / metrics.getCompileCount())
	// .getMajorString());
	// } else {
	// buf.append("");
	// }
	// buf.append(CAMMA);
	//
	// // Time/Run
	// int runCount = metrics.getRunCount();
	// if (runCount > 0) {
	// buf.append(new CTimeInterval(time / metrics.getRunCount())
	// .getMajorString());
	// } else {
	// buf.append("");
	// }
	// buf.append(CAMMA);
	//
	// // TODO H24.2.21 保井追加 cvsにBlockEditorの作業時間を記述する(分)
	// int beWorkingTime = metrics.getBEWorkingTime().getHour() * 60
	// + metrics.getBEWorkingTime().getMinute();
	// if (beWorkingTime > 0) {
	// buf.append(beWorkingTime);
	// } else {
	// buf.append("0");
	// }
	// buf.append(CAMMA);
	//
	// // CompileError
	// CompileErrorAnalyzer compileErrorAnalyzer = analyzerList
	// .getChild(unitFileName);
	// if (compileErrorAnalyzer != null) {
	// // Count
	// buf.append(compileErrorAnalyzer.getHistories().size());
	// buf.append(CAMMA);
	//
	// // Total Correction Time
	// buf.append(compileErrorAnalyzer.getCorrectionTime());
	// buf.append(CAMMA);
	//
	// // rate
	// long correctionTime = compileErrorAnalyzer.getCorrectionTime()
	// .getTime();
	// double rate = (double) correctionTime / workingTime * 100;
	// buf.append(rate);
	// buf.append(CAMMA);
	// } else {
	// buf.append(CAMMA);
	// buf.append(CAMMA);
	// buf.append(CAMMA);
	// }
	//
	// // Failure Knowledge
	// if (fkAnalyzer != null) {
	// // count
	// buf.append(fkAnalyzer.getKnowledges().size());
	// buf.append(CAMMA);
	//
	// // time(sec)
	// double sec = (double) fkAnalyzer.getWritingTime() / 1000;
	// buf.append(sec);
	// buf.append(CAMMA);
	//
	// // overhead(%)
	// double overhead = (double) fkAnalyzer.getWritingTime()
	// / workingTime * 100;
	// buf.append(overhead);
	// } else {
	// buf.append(CAMMA);
	// buf.append(CAMMA);
	// }
	//
	// pw.println(buf.toString());
	// pw.flush();
	//
	// }

	// private FailureKnowledgeAnalyzer loadFailureKnowledgeAnalyzer(IPLUnit
	// unit) {
	// FailureKnowledgeRepositoryAnalyzer analyzer = new
	// FailureKnowledgeRepositoryAnalyzer(
	// unit.getProject());
	// if (analyzer.existRepositoryFile()) {
	// if (analyzer.getFailureKnowledgeAnalyzer(unit.getName()) != null) {
	// return analyzer.getFailureKnowledgeAnalyzer(unit.getName());
	// }
	// }
	// return null;
	// }

	private void complement(String studentName, String lectureNum,
			String fileName, String mark) {
		// ドントケア１行吐き出す
		StringBuffer buf = new StringBuffer();

		buf.append(studentName);
		buf.append(CAMMA);
		int lecture = Integer.parseInt(lectureNum) + 1;
		buf.append(lecture);
		buf.append(CAMMA);
		buf.append(fileName);
		buf.append(CAMMA);
		buf.append(mark);
		buf.append(CAMMA);
		buf.append("");
		buf.append(CAMMA);
		for (int j = 0; j < 13; j++) {
			buf.append("");
			buf.append(CAMMA);
		}
		pw.println(buf.toString());
		pw.flush();
	}

	private class NotFoundFileDialog extends JDialog {

		private static final long serialVersionUID = 1L;

		private String fileName;

		private JButton okButton = new JButton("OK");

		public NotFoundFileDialog(String fileName) {
			this.fileName = fileName;
			initializeViews();
		}

		private void initializeViews() {
			setSize(new Dimension(200, 150));
			setTitle("NotFoundFileException");
			setLocationRelativeTo(null);

			okButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					doClose();
				}
			});

			JPanel pane = new JPanel();
			pane.setLayout(new BorderLayout());

			JLabel label = new JLabel(fileName + "がありません");
			label.setHorizontalTextPosition(JLabel.CENTER);
			pane.add(label, BorderLayout.CENTER);
			pane.add(okButton, BorderLayout.SOUTH);

			getContentPane().add(pane);
		}

		private void doClose() {
			setVisible(false);
		}

		public void open() {
			setVisible(true);
		}

	}

}
