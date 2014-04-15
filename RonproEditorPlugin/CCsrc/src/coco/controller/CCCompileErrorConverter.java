package src.coco.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;

import src.coco.model.CCCompileErrorManager;

public class CCCompileErrorConverter extends CCCsvFileLoader {

	private CCCompileErrorManager manager;
	// private int addErrorID;
	private String CAMMA = ",";
	PrintWriter pw;

	public CCCompileErrorConverter(CCCompileErrorManager manager) {
		this.manager = manager;
		// addErrorID = manager.getAllLists().size() + 1;
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
		buf.append("ファイルパス");
		buf.append(CAMMA);
		buf.append("発生時刻");
		buf.append(CAMMA);
		buf.append("修正完了時刻");
		buf.append(CAMMA);
		buf.append("修正時間");
		// buf.append("ErrorID,ファイル名,発生時刻,修正時間");
		pw.println(buf.toString());
	}

	protected void separeteData(List<String> lines) throws IOException {
		StringBuffer buf = new StringBuffer();

		// errorIDはmessageListをmanagerに作ってindexOfメソッドで解決
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
			// ErrorKindsに存在しない場合は，カウントしない
			return;

			// 新しくエラーメッセージを記録する場合の処理
			// errorID = addErrorID;
			// manager.put(errorID, 6, message);
			// addErrorID++;
		}

		// spiltは直接\\で区切ることができないので，いったん/に変換する
		// 理由については後日調査すること
		String filepath = lines.get(2).replace("\\", "/");

		long beginTime = 0;
		if (lines.get(12).indexOf(" ") == -1) {
			beginTime = Long.parseLong(lines.get(12));
		} else {
			beginTime = changeDateStringToLong(lines.get(12));
		}

		long endTime = 0;
		if (lines.get(13).indexOf(" ") == -1) {
			endTime = Long.parseLong(lines.get(13));
		} else {
			endTime = changeDateStringToLong(lines.get(13));
		}

		// correctionTime は CT値
		int correctTime = Integer.parseInt(lines.get(18));

		// データを書き込む
		buf.append(String.valueOf(errorID));
		buf.append(CAMMA);
		buf.append(filepath);
		buf.append(CAMMA);
		buf.append(String.valueOf(beginTime));
		buf.append(CAMMA);
		buf.append(String.valueOf(endTime));
		buf.append(CAMMA);
		buf.append(String.valueOf(correctTime));
		pw.println(buf.toString());
		// out.write(errorID + "," + filename + "," + beginTime + ","
		// + correctTime + "\n");
	}

	private long changeDateStringToLong(String data) {
		String[] tokenizer = data.split(" ");
		String[] dates = tokenizer[0].split("/");
		String[] times = tokenizer[1].split(":");

		int year = Integer.parseInt(dates[0]);
		int month = Integer.parseInt(dates[1]);
		int day = Integer.parseInt(dates[2]);

		int hour = Integer.parseInt(times[0]);
		int minute = Integer.parseInt(times[1]);
		int second = Integer.parseInt(times[2]);

		Calendar calender = Calendar.getInstance();
		calender.set(year, month, day, hour, minute, second);

		return calender.getTimeInMillis();
	}
}