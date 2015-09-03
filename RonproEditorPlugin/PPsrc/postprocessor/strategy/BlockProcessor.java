/*
 * BlockProcessor.java
 * Created on 2013/02/09 by macchan
 * Copyright(c) 2013 Yoshiaki Matsuzawa, Shizuoka Univerisy
 */
package postprocessor.strategy;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import postprocessor.strategy.NQuestion.LanguageRequirement;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.table.CCSVFileIO;

/**
 * @author macchan BlockProcessor
 */
public class BlockProcessor {

	private List<NQuestion> questions = new ArrayList<NQuestion>();
	private List<String> students = new ArrayList<String>();

	public BlockProcessor(CDirectory dir) {
		questions = loadQuestion(dir.findOrCreateFile("questions.csv"));
		students = dir.findOrCreateFile("students.txt").loadTextAsList();
	}

	private List<NQuestion> loadQuestion(CFile file) {
		List<NQuestion> questions = new ArrayList<NQuestion>();
		String[][] loaded = CCSVFileIO.load(file);
		for (int i = 1; i < loaded.length; i++) {// ヘッダを飛ばすので１から
			String[] r = loaded[i];
			questions.add(new NQuestion(r[0], r[1], r[2], Integer
					.parseInt(r[3]) == 1, LanguageRequirement.valueOf(r[4])));
		}
		return questions;
	}

	public List<List<String>> process(List<List<String>> table) {
		// データの準備
		Map<String, List<String>> dataMap = new HashMap<String, List<String>>();
		int size = table.size();
		for (int i = 1; i < size; i++) {// ヘッダを飛ばすので１から
			List<String> record = table.get(i);
			// Name + Lecture + FileName
			String key = record.get(0) + record.get(1) + record.get(2);
			dataMap.put(key, record);
		}

		// Header
		List<List<String>> newTable = new ArrayList<List<String>>();
		{
			List<String> record = new ArrayList<String>();
			record.add("");
			for (NQuestion question : questions) {
				if (isProcessQuestion(question)) {
					record.add(question.getFilename());
				}
			}
			newTable.add(record);
		}
		{
			List<String> record = new ArrayList<String>();
			record.add("");
			for (NQuestion question : questions) {
				if (isProcessQuestion(question)) {
					record.add(question.getId());
				}
			}
			newTable.add(record);
		}
		newTable.add(new ArrayList<String>());
		// {
		// List<String> record = new ArrayList<String>();
		// record.add("");
		// for (NQuestion question : questions) {
		// if (isProcessQuestion(question)) {
		// record.add(question.getLangReq().toString());
		// }
		// }
		// out.add(record);
		// }

		// Body
		for (String student : students) {
			List<String> record = new ArrayList<String>();
			record.add(student);
			for (NQuestion question : questions) {
				if (isProcessQuestion(question)) {
					String key = student + question.getLecture()
							+ question.getFilename();
					List<String> data = dataMap.get(key);
					if (data != null) {
						/*
						 * Student Lecture FileName WorkingTime WorkingTime(min)
						 * LineCount CompileCount RunCount Time/Compile Time/Run
						 * BEWorkingTime(min) total => 4, BE = 10 LineCount = 5
						 */
						// blockrate
						// String d = getBERate(data.get(4), data.get(10));
						// String d = Integer.toString(data.get(5));
						// record.add(d);

						// loc
						// String loc = data.get(5);
						// if (loc == null || loc.equals("0")) {
						// loc = "-";
						// }
						// record.add(loc);

						// WT
						String wt = data.get(4);
						if (wt == null || wt.equals("0")) {
							wt = "-";
						}
						record.add(wt);
					} else {
						record.add("-");
					}
				}
			}
			newTable.add(record);
		}
		return newTable;
	}

	private boolean isProcessQuestion(NQuestion q) {
		return q.isMandatory()
				&& q.getLangReq() == NQuestion.LanguageRequirement.ANY;
	}

	/**
	 */
	@SuppressWarnings("unused")
	private String getBERate(String total, String be) {
		int totalInt = 0;
		try {
			totalInt = Integer.parseInt(total);
		} catch (Exception ex) {
			return "-";
		}
		if (totalInt <= 0) {
			return "-";
		}

		int beInt = 0;
		try {
			beInt = Integer.parseInt(be);
		} catch (Exception ex) {
			return "0";
		}

		double rate = (double) beInt / (double) totalInt;
		return format.format(rate);
	}

	private static final DecimalFormat format = new DecimalFormat("###.#");
}
