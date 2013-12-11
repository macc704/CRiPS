package src.coco.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import src.coco.model.CCCompileErrorManager;

public class CCCompileErrorConverter extends CCCsvFileLoader {

	private CCCompileErrorManager manager;
	private int addErrorID;
	private String CAMMA = ",";
	PrintWriter pw;

	public CCCompileErrorConverter(CCCompileErrorManager manager) {
		this.manager = manager;
		addErrorID = manager.getAllLists().size() + 1;
	}

	public void convertData(String inFileName, String outFileName)
			throws IOException {
		File outfile = new File(outFileName);
		pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outfile), "sjis")));
		inputHeader(outfile);
		loadData(inFileName);
		pw.flush();
		pw.close();
	}

	private void inputHeader(File outFile) throws IOException {
		StringBuffer buf = new StringBuffer();

		buf.append("ErrorID");
		buf.append(CAMMA);
		buf.append("プロジェクト名");
		buf.append(CAMMA);
		buf.append("ファイル名");
		buf.append(CAMMA);
		buf.append("発生時刻");
		buf.append(CAMMA);
		buf.append("修正時間");
		// buf.append("ErrorID,ファイル名,発生時刻,修正時間");
		pw.println(buf.toString());
	}

	protected void separeteData(List<String> lines) throws IOException {
		StringBuffer buf = new StringBuffer();

		// // errorIDはmessageListをmanagerに作ってindexOfメソッドで解決
		// // 存在していないerrorIDの場合、新しくエラーメッセージを記録する
		// // 先にシンボルなどのチェックをしてからgetMessageIDをする形にし、（シンボル）などに対応した
		// int errorID = 0;
		// String element = "";
		// if (lines.get(7) != null) {
		// element = "（" + lines.get(7) + "）";
		// }
		//
		// String message = lines.get(5) + element;
		//
		// try {
		// errorID = manager.getMessagesID(message);
		// } catch (Exception e) {
		// errorID = addErrorID;
		// manager.put(errorID, 6, message);
		// addErrorID++;
		// }
		//
		// String projectname = "";
		// String filename = lines.get(4);
		//
		// // 開始時刻はファイルのフルパスから持ってくる
		// // long beginTime = calculationBeginTime(lines.get(14));
		// long beginTime = 0;
		//
		// // 修正時間は取り出して時間を計算することに成功した
		// int correctTime = calculationCorrectTime(lines.get(16));

		// errorIDはmessageListをmanagerに作ってindexOfメソッドで解決
		// 存在していないerrorIDの場合、新しくエラーメッセージを記録する
		// 先にシンボルなどのチェックをしてからgetMessageIDをする形にし、（シンボル）などに対応した
		int errorID = 0;
		String element = "";
		if (lines.get(5) != null) {
			element = "（" + lines.get(5) + "）";
		}

		String message = lines.get(3) + element;

		try {
			errorID = manager.getMessagesID(message);
		} catch (Exception e) {
			errorID = addErrorID;
			manager.put(errorID, 6, message);
			addErrorID++;
		}

		// spiltは直接\\で区切ることができないので，いったん/に変換する
		// 理由については後日調査すること
		String projectname = "";
		String filename;

		String filepath = lines.get(2).replace("\\", "/");
		String[] filepathSegments = filepath.split("/");
		if (filepathSegments.length > 4) {
			// 暫定論プロのみ
			// TODO パスの切り出し方改良
			projectname = filepathSegments[filepathSegments.length - 4];
			filename = filepathSegments[filepathSegments.length - 1];
		} else {
			filename = lines.get(2);
		}

		// 発生時刻
		long beginTime = 0;
		if (lines.get(12).indexOf(" ") == -1) {
			beginTime = Long.parseLong(lines.get(12));
		}

		// 修正時刻
		long endTime = 0;
		if (lines.get(13).indexOf(" ") == -1) {
			endTime = Long.parseLong(lines.get(13));
		} else {
			endTime = calculationCorrectTimeAsMills(lines.get(14));
		}

		// データを書き込む
		buf.append(String.valueOf(errorID));
		buf.append(CAMMA);
		buf.append(projectname);
		buf.append(CAMMA);
		buf.append(filename);
		buf.append(CAMMA);
		buf.append(String.valueOf(beginTime));
		buf.append(CAMMA);
		buf.append(String.valueOf(endTime));
		pw.println(buf.toString());
		// out.write(errorID + "," + filename + "," + beginTime + ","
		// + correctTime + "\n");
	}

	// private long calculationBeginTime(String data) {
	// String[] tokenizer = data.split(" ");
	// String[] dates = tokenizer[0].split("/");
	// String[] times = tokenizer[1].split(":");
	//
	// int year = Integer.parseInt(dates[0]);
	// int month = Integer.parseInt(dates[1]);
	// int day = Integer.parseInt(dates[2]);
	// int hour = Integer.parseInt(times[0]);
	// int minute = Integer.parseInt(times[1]);
	// // int second = Integer.parseInt(times[2]);
	// int second = 0;
	//
	// Calendar calender = Calendar.getInstance();
	// calender.set(year, month, day, hour, minute, second);
	//
	// return calender.getTimeInMillis();
	// }

	private int calculationCorrectTimeAsMills(String time) {
		String[] tokanizer = time.split(":");
		int hour = Integer.parseInt(tokanizer[0]) * 3600;
		int minute = Integer.parseInt(tokanizer[1]) * 60;
		int second = Integer.parseInt(tokanizer[2]);
		return hour + minute + second * 1000;
	}
}