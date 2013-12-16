package tea.analytics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import pres.loader.model.PLFile;
import tea.analytics.model.TCompileErrorHistory;
import tea.analytics.model.TCompileErrorHistorySegment;
import tea.analytics.model.TCompilePoint;
import clib.common.compiler.CDiagnostic;
import clib.common.compiler.CMessageParser;
import clib.common.time.CTimeInterval;

public class CompileErrorListFile {

	private static String CAMMA = ",";

	private List<CompileErrorAnalyzerList> compileErrorAnalyzes;

	public CompileErrorListFile(
			List<CompileErrorAnalyzerList> compileErrorAnalyzes) {
		this.compileErrorAnalyzes = compileErrorAnalyzes;
	}

	/****************************************************
	 * Make CSV File
	 ****************************************************/

	private void makeCSVFile(File file) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}
	}

	/****************************************************
	 * Output
	 ****************************************************/

	public void outputErrorList(File file, boolean coco) throws IOException,
			FileNotFoundException {
		makeCSVFile(file);

		PrintWriter pw = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(file), "sjis")));

		// header
		StringBuffer buf = new StringBuffer();
		buf.append("StudentName");
		buf.append(CAMMA);
		buf.append("Lecture");
		buf.append(CAMMA);
		buf.append("FileName");
		buf.append(CAMMA);
		buf.append("ErrorMessage");
		buf.append(CAMMA);
		buf.append("シンボル");
		buf.append(CAMMA);
		buf.append("シンボルの種類");
		buf.append(CAMMA);
		buf.append("ワードA");
		buf.append(CAMMA);
		buf.append("ワードAの種類");
		buf.append(CAMMA);
		buf.append("ワードB");
		buf.append(CAMMA);
		buf.append("ワードBの種類");
		buf.append(CAMMA);
		buf.append("ワードC");
		buf.append(CAMMA);
		buf.append("ワードCの種類");
		buf.append(CAMMA);
		buf.append("発生時刻");
		buf.append(CAMMA);
		buf.append("修正時刻");
		buf.append(CAMMA);
		buf.append("修正時間");
		buf.append(CAMMA);
		buf.append("isWorking");
		buf.append(CAMMA);
		buf.append("複合数");
		buf.append(CAMMA);
		buf.append("同時修正数");
		buf.append(CAMMA);
		buf.append("CT(sec)");
		buf.append(CAMMA);
		buf.append("GeneRef");
		pw.println(buf.toString());

		for (CompileErrorAnalyzerList analyze : compileErrorAnalyzes) {
			writeErrorList(analyze, pw, coco);
		}

		pw.close();
	}

	private void writeErrorList(CompileErrorAnalyzerList analyze,
			PrintWriter pw, boolean coco) {
		// 静大
		String[] line = analyze.getProject().getName().split("-");

		// 慶応
		// String[] line = analyze.getProject().getName().split("_");

		for (TCompileErrorHistory history : analyze.getHistories()) {
			StringBuffer buf = new StringBuffer();

			/**** 静大 ****/
			if (line.length >= 3) {
				// 学籍番号
				buf.append(line[0] + "-" + line[1]);
				buf.append(CAMMA);
				// レポート回
				buf.append(line[2]);
				buf.append(CAMMA);
			} else {
				buf.append("NA");
				buf.append(CAMMA);
				buf.append("NA");
				buf.append(CAMMA);
			}

			/**** 慶応 ****/
			// // 学籍番号
			// buf.append(line[2]);
			// buf.append(CAMMA);
			// // レポート回
			// buf.append(line[0]);
			// buf.append(CAMMA);

			/*** どちらでもない **/
			// buf.append("NA");
			// buf.append(CAMMA);
			// buf.append("NA");
			// buf.append(CAMMA);

			// 最初のセグメントのコンパイルエラーを取得
			CDiagnostic compileError = history.getSegments().getFirst()
					.getCompileError();

			// ファイル名 cocoViewerからならフルパス
			if (coco) {
				buf.append(compileError.getSourceName());
			} else {
				buf.append(compileError.getNoPathSourceName());
			}

			buf.append(CAMMA);

			CMessageParser parser = compileError.getMessageParser();

			if (parser.getMessageKind() == "警告") {
				continue;
			}

			// エラーメッセージorタグ
			if (parser.getAbstractionMessage() != null) {
				buf.append("\"");
				buf.append(parser.getAbstractionMessage());
				buf.append("\",");
			} else {
				buf.append("\"");
				buf.append(parser.getErrorMessage());
				buf.append("\",");
			}

			// シンボル
			if (parser.getSymbol() != null) {
				buf.append("\"");
				buf.append(parser.getSymbol());
				buf.append("\",");
				buf.append(parser.getSymbolKind());
				buf.append(CAMMA);
			} else {
				buf.append(CAMMA);
				buf.append(CAMMA);
			}

			// ワード
			if (parser.getAbstractionMessage() != null) {
				List<String> word = parser.getWords();
				List<String> kind = parser.getWordKinds();

				switch (word.size()) {
				case 1:
					buf.append("\"");
					buf.append(word.get(0));
					buf.append("\"");
					buf.append(CAMMA);
					buf.append(kind.get(0));
					buf.append(CAMMA);
					buf.append(CAMMA);
					buf.append(CAMMA);
					buf.append(CAMMA);
					buf.append(CAMMA);
					break;
				case 2:
					buf.append("\"");
					buf.append(word.get(0));
					buf.append("\"");
					buf.append(CAMMA);
					buf.append(kind.get(0));
					buf.append(CAMMA);
					buf.append("\"");
					buf.append(word.get(1));
					buf.append("\"");
					buf.append(CAMMA);
					buf.append(kind.get(1));
					buf.append(CAMMA);
					buf.append(CAMMA);
					buf.append(CAMMA);
					break;
				case 3:
					buf.append("\"");
					buf.append(word.get(0));
					buf.append("\"");
					buf.append(CAMMA);
					buf.append(kind.get(0));
					buf.append(CAMMA);
					buf.append("\"");
					buf.append(word.get(1));
					buf.append("\"");
					buf.append(CAMMA);
					buf.append(kind.get(1));
					buf.append(CAMMA);
					buf.append(word.get(2));
					buf.append(CAMMA);
					buf.append(kind.get(2));
					buf.append(CAMMA);
					break;
				default:
					buf.append(CAMMA);
					buf.append(CAMMA);
					buf.append(CAMMA);
					buf.append(CAMMA);
					buf.append(CAMMA);
					buf.append(CAMMA);
				}

			}

			// cocoViewerの時はミリ秒を返す
			// エラー発生時刻
			if (coco) {
				buf.append(history.getStart().getTime().getAsLong());
				buf.append(CAMMA);
				// エラー修正時刻
				if (history.getEnd() == null) {
					buf.append("-");
					buf.append(CAMMA);
				} else {
					buf.append(history.getEnd().getTime().getAsLong());
					buf.append(CAMMA);
				}
			} else {
				buf.append(history.getStart().getTime());
				buf.append(CAMMA);
				// エラー修正時刻
				if (history.getEnd() == null) {
					buf.append("-");
					buf.append(CAMMA);
				} else {
					buf.append(history.getEnd().getTime());
					buf.append(CAMMA);
				}
			}

			// CT
			CTimeInterval correctionTime = history.getCorrectionTime();
			if (correctionTime != null) {
				int hour = correctionTime.getHour() + correctionTime.getDay()
						* 24;
				buf.append(hour);
				buf.append(":");
				buf.append(correctionTime.getMinuteString());
				buf.append(":");
				buf.append(correctionTime.getSecondString());
				buf.append(CAMMA);
			} else {
				buf.append("-");
				buf.append(CAMMA);
			}

			// isWorking
			buf.append(isWorking(history, analyze));
			buf.append(CAMMA);

			// 複合数
			buf.append(history.getStart().getCompileResult().getDiagnostics()
					.size());
			buf.append(CAMMA);

			// 同時修正数
			if (history.getEnd() == null) {
				buf.append("-");
			} else {
				buf.append(history.getEnd().getFixedSegments().size());
			}
			buf.append(CAMMA);

			// CT(sec)
			int sec = correctionTime.getHour() * 60 * 60
					+ correctionTime.getMinute() * 60
					+ correctionTime.getSecond();
			buf.append(sec);
			buf.append(CAMMA);

			// GeneRef
			buf.append(history.containsGeneRefTime());
			// buf.append(CAMMA);

			// buf.append(history.getGeneRefTime());

			pw.println(buf.toString());
		}
		pw.flush();

	}

	private boolean isWorking(TCompileErrorHistory history,
			CompileErrorAnalyzerList analyze) {

		String fileName = history.getSegments().getFirst().getCompileError()
				.getSourceName();
		fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);

		// 警告の場合
		if (fileName.equals("no name")) {
			return false;
		}

		PLFile file = null;
		for (PLFile f : analyze.getProject().getFiles()) {
			if (f.getFileName().toString().toUpperCase()
					.equals(fileName.toUpperCase())) {
				file = f;
				break;
			}
		}

		for (TCompileErrorHistorySegment segment : history.getSegments()) {
			if (!segment.isWorking(file)) {
				return false;
			}
		}
		return true;
	}

	public void outputPatternList() throws IOException, FileNotFoundException {
		File file = new File("./Pattern.csv");
		makeCSVFile(file);

		PrintWriter pw = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(file), "sjis")));

		// header
		StringBuffer buf = new StringBuffer();
		buf.append("学籍番号");
		buf.append(CAMMA);
		buf.append("レポート回");
		buf.append(CAMMA);
		buf.append("コンパイル時刻");
		buf.append(CAMMA);
		// buf.append("ファイル名");
		// buf.append(CAMMA);
		buf.append("パターン1");
		buf.append(CAMMA);
		buf.append("パターン2-A");
		buf.append(CAMMA);
		buf.append("パターン2-B");
		buf.append(CAMMA);
		buf.append("パターン3-A");
		buf.append(CAMMA);
		buf.append("パターン3-B");
		buf.append(CAMMA);
		buf.append("パターン4");
		buf.append(CAMMA);
		buf.append("パターン5");
		pw.println(buf.toString());

		for (CompileErrorAnalyzerList analyze : compileErrorAnalyzes) {
			writePatternList(analyze, pw);
		}

		pw.close();
	}

	private void writePatternList(CompileErrorAnalyzerList analyze,
			PrintWriter pw) {
		// 静大
		String[] line = analyze.getProject().getName().split("-");

		// 慶応
		// String[] line = analyze.getProject().getName().split("_");

		for (TCompilePoint point : analyze.getCompilePoints()) {

			if (!point.hasFixedSegments()) {
				continue;
			}

			StringBuffer buf = new StringBuffer();

			/**** 静大 ****/
			if (line.length >= 3) {
				// 学籍番号
				buf.append(line[0] + "-" + line[1]);
				buf.append(CAMMA);
				// レポート回
				buf.append(line[2]);
				buf.append(CAMMA);
			} else {
				buf.append("NA");
				buf.append(CAMMA);
				buf.append("NA");
				buf.append(CAMMA);
			}

			/**** 慶応 ****/
			// // 学籍番号
			// buf.append(line[2]);
			// buf.append(CAMMA);
			// // レポート回
			// buf.append(line[0]);
			// buf.append(CAMMA);

			// コンパイル時刻
			buf.append(point.getTime());
			buf.append(CAMMA);

			// パターン
			if (point.getPattern().contains(1)) {
				buf.append("○");
			}
			buf.append(CAMMA);

			if (point.getPattern().contains(2)) {
				buf.append("○");
			}
			buf.append(CAMMA);

			if (point.getPattern().contains(3)) {
				buf.append("○");
			}
			buf.append(CAMMA);

			if (point.getPattern().contains(4)) {
				buf.append("○");
			}
			buf.append(CAMMA);

			if (point.getPattern().contains(5)) {
				buf.append("○");
			}
			buf.append(CAMMA);

			if (point.getPattern().contains(6)) {
				buf.append("○");
			}
			buf.append(CAMMA);

			if (point.getPattern().contains(7)) {
				buf.append("○");
			}

			// パターン
			// TCompilePoint point = history.getSegments().getLast().getEnd();
			// for (int i = 0; i < point.getPattern().size(); i++) {
			// buf.append(CAMMA);
			// String str = "";
			// switch (point.getPattern().get(i)) {
			// case 1:
			// str = "1";
			// break;
			// case 2:
			// str = "2-A";
			// break;
			// case 3:
			// str = "2-B";
			// break;
			// case 4:
			// str = "3-A";
			// break;
			// case 5:
			// str = "3-B";
			// break;
			// case 6:
			// str = "4";
			// break;
			// case 7:
			// str = "5";
			// break;
			// }
			// buf.append(str);
			// }

			pw.println(buf.toString());
		}
		pw.flush();
	}
	// public void outputMetricsList() throws IOException, FileNotFoundException
	// {
	// File file = new File("compileerror/Metrics.csv");
	// makeCSVFile(file);
	//
	// PrintWriter pw = new PrintWriter(new BufferedWriter(
	// new OutputStreamWriter(new FileOutputStream(file), "sjis")));
	//
	// // header
	// StringBuffer buf = new StringBuffer();
	// buf.append("学籍番号");
	// buf.append(CAMMA);
	// buf.append("レポート回");
	// buf.append(CAMMA);
	// buf.append("WorkingTime");
	// buf.append(CAMMA);
	// buf.append("LineCount");
	// buf.append(CAMMA);
	// buf.append("CompileCount");
	// buf.append(CAMMA);
	// buf.append("RunCount");
	// buf.append(CAMMA);
	// buf.append("ErrorCount");
	// buf.append(CAMMA);
	// buf.append("Time/CompileCount");
	// buf.append(CAMMA);
	// buf.append("Time/RunCount");
	// pw.println(buf.toString());
	//
	// for (ProjectCompileError pErr : cErrs) {
	// writeMetricsList(pErr, pw);
	// }
	//
	// pw.close();
	//
	// }
	//
	// public void outputFileList() throws IOException, FileNotFoundException {
	// File file = new File("compileerror/FileCount.csv");
	// makeCSVFile(file);
	//
	// PrintWriter pw = new PrintWriter(new BufferedWriter(
	// new OutputStreamWriter(new FileOutputStream(file), "sjis")));
	//
	// // header
	// StringBuffer buf = new StringBuffer();
	// buf.append("学籍番号");
	// buf.append(CAMMA);
	// buf.append("プロジェクト");
	// buf.append(CAMMA);
	// buf.append("ファイル名");
	// pw.println(buf.toString());
	//
	// for (ProjectCompileError pErr : cErrs) {
	// writeFileList(pErr, pw);
	// }
	//
	// pw.close();
	//
	// }
	//
	// public void outputErrCountByFile() throws IOException,
	// FileNotFoundException {
	// File file = new File("compileerror/ErrorCount.csv");
	// makeCSVFile(file);
	//
	// PrintWriter pw = new PrintWriter(new BufferedWriter(
	// new OutputStreamWriter(new FileOutputStream(file), "sjis")));
	//
	// // header
	// StringBuffer buf = new StringBuffer();
	// buf.append("学籍番号");
	// buf.append(CAMMA);
	// buf.append("プロジェクト");
	// buf.append(CAMMA);
	// buf.append("ファイル名");
	// buf.append(CAMMA);
	// buf.append("エラー数");
	// pw.println(buf.toString());
	//
	// for (ProjectCompileError pErr : cErrs) {
	// writeErrCountByFile(pErr, pw);
	// }
	//
	// pw.close();
	// }
	//
	// public void outputCompileCount() throws IOException,
	// FileNotFoundException {
	// File file = new File("compileerror/CompileCount.csv");
	// makeCSVFile(file);
	//
	// PrintWriter pw = new PrintWriter(new BufferedWriter(
	// new OutputStreamWriter(new FileOutputStream(file), "sjis")));
	//
	// // header
	// StringBuffer buf = new StringBuffer();
	// buf.append("学籍番号");
	// buf.append(CAMMA);
	// buf.append("プロジェクト");
	// buf.append(CAMMA);
	// buf.append("ファイル名");
	// buf.append(CAMMA);
	// buf.append("総コンパイル数");
	// buf.append(CAMMA);
	// buf.append("最初にエラーが0になるまでの回数");
	// buf.append(CAMMA);
	// buf.append("それ以降の回数");
	// buf.append(CAMMA);
	// buf.append("hasError");
	// pw.println(buf.toString());
	//
	// for (ProjectCompileError pErr : cErrs) {
	// writeCompileCountFile(pErr, pw);
	// }
	//
	// pw.close();
	// }
	//
	//
	// /****************************************************
	// * Write
	// ****************************************************/
	//
	//
	// private void writeMetricsList(ProjectCompileError pErr, PrintWriter pw) {
	// PLProject project = pErr.getPLProject();
	//
	// PLMetricsCalculator metrics = new PLMetricsCalculator(
	// project.getRootPackage());
	//
	// String projectName = project.getName().toString();
	//
	// // 静大
	// String[] line = projectName.split("-");
	//
	// // 慶応
	// // String[] line = projectName.split("_");
	//
	// // 静大
	// pw.print(line[1] + "-" + line[2] + CAMMA);
	// pw.print(line[3] + CAMMA);
	//
	// // 慶応
	// // pw.print(line[2] + CAMMA);
	// // pw.print(line[0] + CAMMA);
	//
	// pw.print(metrics.getWorkingTime().getMajorString() + CAMMA);
	// pw.print(metrics.getLineCount() + CAMMA);
	// pw.print(metrics.getCompileCount() + CAMMA);
	// pw.print(metrics.getRunCount() + CAMMA);
	// pw.print(pErr.getErrorCount() + CAMMA);
	// long time = metrics.getWorkingTime().getTime();
	// if (metrics.getCompileCount() != 0) {
	// pw.print(new CTimeInterval(time / metrics.getCompileCount())
	// .getMajorString() + CAMMA);
	// } else {
	// pw.print("null,");
	// }
	// if (metrics.getRunCount() != 0) {
	// pw.print(new CTimeInterval(time / metrics.getRunCount())
	// .getMajorString());
	// } else {
	// pw.print("null");
	// }
	// pw.println();
	// pw.flush();
	// }
	//
	// private void writeFileList(ProjectCompileError pErr, PrintWriter pw) {
	// String projectName = pErr.getPLProject().getName().toString();
	// String[] line = projectName.split("-");
	//
	// for (CompileErrorAnalyzer fileCErr : pErr.getFileCompileErrs()) {
	// StringBuffer buf = new StringBuffer();
	//
	// // 学籍番号
	// buf.append(line[1] + "-" + line[2]);
	// buf.append(CAMMA);
	//
	// // レポート回
	// buf.append(line[3]);
	// buf.append(CAMMA);
	//
	// // ファイル名
	// buf.append(fileCErr.getFileName());
	// buf.append(CAMMA);
	//
	// pw.println(buf.toString());
	// }
	// pw.flush();
	// }
	//
	// private void writeErrCountByFile(ProjectCompileError pErr, PrintWriter
	// pw) {
	// String projectName = pErr.getPLProject().getName().toString();
	// String[] line = projectName.split("-");
	//
	// for (CompileErrorAnalyzer fileCErr : pErr.getFileCompileErrs()) {
	// StringBuffer buf = new StringBuffer();
	//
	// // 学籍番号
	// buf.append(line[1] + "-" + line[2]);
	// buf.append(CAMMA);
	//
	// // レポート回
	// buf.append(line[3]);
	// buf.append(CAMMA);
	//
	// // ファイル名
	// buf.append(fileCErr.getFileName());
	// buf.append(CAMMA);
	//
	// // エラー総数
	// buf.append(fileCErr.getErrorCount());
	//
	// pw.println(buf.toString());
	// }
	// pw.flush();
	// }
	//
	// private void writeCompileCountFile(ProjectCompileError pErr, PrintWriter
	// pw) {
	// String projectName = pErr.getPLProject().getName().toString();
	// String[] line = projectName.split("-");
	//
	// for (CompileErrorAnalyzer fileCErr : pErr.getFileCompileErrs()) {
	// StringBuffer buf = new StringBuffer();
	//
	// // 学籍番号
	// buf.append(line[1] + "-" + line[2]);
	// buf.append(CAMMA);
	//
	// // レポート回
	// buf.append(line[3]);
	// buf.append(CAMMA);
	//
	// // ファイル名
	// buf.append(fileCErr.getFileName());
	// buf.append(CAMMA);
	//
	// int numOfCompiles = fileCErr.getCompileCount();
	// int first0Error = fileCErr.getFirstNoErrorCount();
	//
	// // 総コンパイル数
	// buf.append(numOfCompiles);
	// buf.append(CAMMA);
	//
	// // 最初にコンパイルエラーが0になった回数
	// buf.append(first0Error);
	// buf.append(CAMMA);
	//
	// // それ以降の回数
	// buf.append(numOfCompiles - first0Error);
	// buf.append(CAMMA);
	//
	// // 提出物にコンパイルエラーがあるか
	// if (fileCErr.hasLastCompileError()) {
	// buf.append("○");
	// }
	//
	// pw.println(buf.toString());
	// }
	// pw.flush();
	// }

}
