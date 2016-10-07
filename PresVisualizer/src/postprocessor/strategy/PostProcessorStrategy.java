package postprocessor.strategy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.CFilename;
import clib.common.table.CCSVFileIO;
import clib.view.framework.DropStrategy;

public class PostProcessorStrategy implements DropStrategy {

	// private CFile absorption;

	@Override
	public void dropPerformed(List<File> files) throws Exception {

		// prepare directory
		if (files.size() != 1) {
			throw new RuntimeException("ドロップできるディレクトリは1つのみです");
		}
		if (!files.get(0).isDirectory()) {
			throw new RuntimeException("ファイルはドロップできません");
		}
		CDirectory dir = CFileSystem.findDirectory(files.get(0).getAbsolutePath());

		// 入力
		CFile in = dir.findOrCreateFile("FileMetrics.csv");
		String basename = in.getName().getName();
		List<List<String>> table = CCSVFileIO.loadAsListList(in);

		// 処理
		ProjectNameProcessor pjNameProcessor = new ProjectNameProcessor(dir);
		table = pjNameProcessor.process(table);
		save(dir, basename + "_1NameConverted", table);// debug
		NayosePreProcessor nayosePreProcessor = new NayosePreProcessor(dir);
		nayosePreProcessor.process(table);
		save(dir, basename + "_2NayosePreProcessed", table);// debug
		NayoseProcessor nayoseProcessor = new NayoseProcessor();
		table = nayoseProcessor.process(table);
		save(dir, basename + "_3NayoseProcessed", table);// debug

		/*
		 * // 出力 csv用
		 * 
		 */BlockProcessor blockProcessor = new BlockProcessor(dir);
		List<List<String>> saveTable;
		/*
		 * // AllWorkingTime(min) saveTable = blockProcessor.process(table,
		 * "AllWorkingTime"); save(dir, basename +"AllWorkingTime", saveTable);
		 * 
		 * // BlockWorkingTimeRate saveTable = blockProcessor.process(table,
		 * "BlockWorkingTimeRate"); save(dir, basename + "BlockWorkingTimeRate",
		 * saveTable);
		 * 
		 * // CompileCorrectTime(min) saveTable = blockProcessor.process(table,
		 * "CompileCorrectTime"); save(dir, basename + "CompileCorrectTime",
		 * saveTable);
		 * 
		 * // CompileCorrectTimeRate saveTable = blockProcessor.process(table,
		 * "CompileCorrectTimeRate"); save(dir, basename +
		 * "CompileCorrectTimeRate", saveTable);
		 * 
		 * // LOC saveTable = blockProcessor.process(table, "LOC"); save(dir,
		 * basename + "LOC", saveTable);
		 */

		// JSON出力用
		List<List<String>> wot = blockProcessor.process(table, "AllWorkingTime");
		List<List<String>> bwtr = blockProcessor.process(table, "BlockWorkingTimeRate");
		List<List<String>> ect = blockProcessor.process(table, "CompileCorrectTime");
		List<List<String>> ectr = blockProcessor.process(table, "CompileCorrectTimeRate");
		List<List<String>> loc = blockProcessor.process(table, "LOC");

		StringBuilder tmp = new StringBuilder();
		tmp.append("var data2012 = [\n");
		// System.out.println("var data2012 = [");

		// 変数名に-は使えないため課題名の-は_で置き換え
		List<String> tmpQuestion = wot.get(1);
		List<String> question = new ArrayList<>();
		for (String q : tmpQuestion) {
			question.add(q.replaceAll("-", "_"));
		}

		for (int i = 3; i < wot.size(); i++) {
			// System.out.println("\t" + "number:" + wot.get(i).get(0));
			tmp.append("{\n\"number\":\"" + wot.get(i).get(0) + "\",\n");
			tmp.append(printMetrics("wot", wot.get(i), question));
			tmp.append(printMetrics("bwtr", bwtr.get(i), question));
			tmp.append(printMetrics("ect", ect.get(i), question));
			tmp.append(printMetrics("ectr", ectr.get(i), question));
			tmp.append(printMetrics("loc", loc.get(i), question));
			tmp.append("},\n");
		}
		tmp.append("];\n");
		// System.out.println(tmp.toString());

		try {
			File file = new File(new File(System.getProperty("user.home"), "Desktop"), "2012data.txt");
			FileWriter filewriter = new FileWriter(file);
			filewriter.write(tmp.toString());
			filewriter.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	String printMetrics(String name, List<String> metrics, List<String> question) {
		StringBuilder tmp = new StringBuilder();
		for (int i = 1; i < metrics.size(); i++) {
			// -とかNaNになっているヤツを-1に統一
			String number = metrics.get(i).equals("-") || metrics.get(i).equals("NaN") ? "-1" : metrics.get(i);
			tmp.append("\t\"" + name + question.get(i) + "\":" + number + ",\n");
		}
		return tmp.toString();
	}

	String oldPrintMetrics(String name, List<String> metrics, List<String> question) {
		StringBuilder tmp = new StringBuilder();
		// System.out.println("\t" + name + ":[");
		tmp.append("\t\"" + name + "\":{\n");

		for (int i = 1; i < metrics.size(); i++) {

			String number = metrics.get(i).equals("-") || metrics.get(i).equals("NaN") ? "-1" : metrics.get(i);
			tmp.append("\t\t\"" + question.get(i) + "\":" + number + ",\n");
		}

		tmp.append("\t" + "},\n");
		return tmp.toString();
	}

	void save(CDirectory dir, String name, List<List<String>> table) {
		CFile out = dir.getParentDirectory().findOrCreateFile(new CFilename(name, "csv"));
		CCSVFileIO.saveByListList(table, out);
	}

}
