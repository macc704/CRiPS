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

public class CCAnalyzeCompileErrorTime extends CCCsvFileLoader {

	LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
	public static final String CAMMA = ",";
	PrintWriter pw;

	public static void main(String[] args) {
		CCAnalyzeCompileErrorTime main = new CCAnalyzeCompileErrorTime();
		for(int i = 2; i < 14; i++) {
			if(i == 8) continue; // lecture08データはない
			main.loadData("compileerrordata/CCCompileError" + String.format("%02d", i) + ".csv");
		}

		for(int i = 35; i <= 105; i += 35) {
			main.loadData("compileerrordata/CCCompileErrorM" + String.format("%02d", i) + ".csv");	
		}
		main.loadData("compileerrordata/CCCompileError11c.csv");
		
		main.writeData("result/CCResult.csv");
		System.out.println("SUCCUESS!");
	}

	@Override
	protected void separeteData(List<String> lines) throws IOException {
		// isWorking Check
		if (lines.get(15).equals("FALSE")) {
			return;
		}
		String filepath = lines.get(2).replace("\\", "/");
		String[] segments = filepath.split("/");
		String name = segments[segments.length - 4].split("-")[0];
		int correctTime = Integer.parseInt(lines.get(18));
		// System.out.println("name:" + name + "\t CT:" + correctTime);

		if (map.containsKey(name)) {
			map.put(name, map.get(name) + correctTime);
		} else {
			map.put(name, correctTime);
		}
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
			buf.append("CT");
			buf.append(CAMMA);
			pw.println(buf.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
