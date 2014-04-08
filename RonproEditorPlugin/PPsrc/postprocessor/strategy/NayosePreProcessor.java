/*
 * NayosePreProcessor.java
 * Created on 2013/02/10 by macchan
 * Copyright(c) 2013 Yoshiaki Matsuzawa, Shizuoka Univerisy
 */
package postprocessor.strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.table.CCSVFileIO;

/**
 * @author macchan NayosePreProcessor
 */
public class NayosePreProcessor {

	// 1001-"wrongname", correctname
	private Map<String, String> wrongToCorrectNames = new HashMap<String, String>();

	public NayosePreProcessor(CDirectory dir) {
		for (CFile file : dir.getFileChildren()) {
			if (file.getName().getName().indexOf("Combine") != -1) {
				load(file);
			}
		}
	}

	private void load(CFile file) {
		// データの形
		// StudentName Lecture FileName FileName(student)
		// 7011-1066 2 Pentagon.java Gokakukei.java, Ｐｅｎｔａｇｏｎ.java

		List<List<String>> table = CCSVFileIO.loadAsListList(file);
		for (int i = 1; i < table.size(); i++) {// ヘッダを飛ばすので１から
			List<String> record = table.get(i);
			String student = record.get(0);
			String lecture = record.get(1);
			String correctName = record.get(2);
			for (String wrongName : record.get(3).split(",[ ]*")) {
				if (!correctName.equals(wrongName)) {
					String key = student + lecture + wrongName;
					wrongToCorrectNames.put(key, correctName);
					// System.out.println(key + " -- registered");
				}
			}
		}
	}

	public void process(List<List<String>> table) {
		for (int i = 1; i < table.size(); i++) {// ヘッダを飛ばすので１から
			List<String> record = table.get(i);
			String student = record.get(0);
			String lecture = record.get(1);
			String fileName = record.get(2);
			String key = student + lecture + fileName;
			if (wrongToCorrectNames.containsKey(key)) {
				record.set(2, wrongToCorrectNames.get(key));
				// System.out.println(key + " -- found");
			}
		}
	}
}
