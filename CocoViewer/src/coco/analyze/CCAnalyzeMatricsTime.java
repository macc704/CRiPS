package coco.analyze;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import coco.controller.CCCsvFileLoader;

/**
 * メトリクスファイルから学生ごとの作業時間を計測するプログラム
 * 
 * @author Motoki Hirao
 */
public class CCAnalyzeMatricsTime extends CCCsvFileLoader {

	LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
	public static final String CAMMA = ",";
	PrintWriter pw;

	/*
	 * 対象データ:Terastation内の研究成果物/平尾/csvdata内/lectureXX/FileMetrics.csv
	 * 以下のmain文ではそれらをlectureごとに名付けを行い、測定
	 */
	public static void main(String[] args) {
		CCAnalyzeMatricsTime main = new CCAnalyzeMatricsTime();
		// main.loadData("metricsdata/FileMetrics.csv");

		// lecture分
		for (int i = 2; i < 14; i++) {
			if (i == 8)
				continue; // lecture08データは存在しないため
			main.loadData("metricsdata/FileMetrics" + String.format("%02d", i) + ".csv");
		}

		// 中間課題分
		for (int i = 35; i <= 105; i += 35) {
			main.loadData("metricsdata/FileMetricsM" + String.format("%02d", i) + ".csv");
		}

		// lecture11 chance課題（上記の命名規則から外れるため）
		main.loadData("metricsdata/FileMetrics11c.csv");

		// 書き出し処理
		main.writeData("result/CCMetricsResult.csv");
		System.out.println("SUCCUESS!");
	}

	@Override
	protected void separeteData(List<String> lines) throws IOException {
		String name = lines.get(0).split("-")[0];
		int time = Integer.parseInt(lines.get(3));

		if (map.containsKey(name)) {
			map.put(name, map.get(name) + time);
		} else {
			map.put(name, time);
		}

		// System.out.println("name: " + name +"\t time: " + time);
	}

	private void writeData(String filepath) {
		try {
			File outfile = createFile(filepath);
			pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile), "sjis")));
			inputHeader(outfile);

			for (Map.Entry<String, Integer> elem : map.entrySet()) {
				System.out.println(elem.getKey() + " : " + elem.getValue());
				StringBuffer buf = new StringBuffer();
				try {
					buf.append(elem.getKey());
					buf.append(CAMMA);
					buf.append(elem.getValue());
					buf.append(CAMMA);
					pw.println(buf.toString());
				} catch (Exception e) {
					System.err.println("Map data writing failed...");
					e.printStackTrace();
				}
			}

			pw.flush();
			pw.close();
		} catch (Exception e) {
			System.err.println("Write file failed...");
			e.printStackTrace();
		}
	}

	private File createFile(String filepath) throws IOException {
		File file = new File(filepath);
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	private void inputHeader(File filepath) throws IOException {
		StringBuffer buf = new StringBuffer();
		try {
			buf.append("ID");
			buf.append(CAMMA);
			buf.append("WORKTIME");
			buf.append(CAMMA);
			pw.println(buf.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
