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

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.table.CCSVFileIO;
import postprocessor.strategy.NQuestion.LanguageRequirement;

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
			questions.add(
					new NQuestion(r[0], r[1], r[2], Integer.parseInt(r[3]) == 1, LanguageRequirement.valueOf(r[4])));
		}
		return questions;
	}

	public List<List<String>> process(List<List<String>> table, String reqData) {
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
		// newTable.add(new ArrayList<String>());
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

		{
			List<String> record = new ArrayList<String>();
			record.add("");
			for (NQuestion question : questions) {
				if (isProcessQuestion(question)) {
					record.add(question.getLangReq().toString());
				}
			}
			newTable.add(record);
		}

		// Body
		for (String student : students) {
			List<String> record = new ArrayList<String>();
			record.add(student);
			for (NQuestion question : questions) {
				if (isProcessQuestion(question)) {
					String key = student + question.getLecture() + question.getFilename();
					List<String> data = dataMap.get(key);
					if (data != null) {
						/*
						 * Student Lecture FileName WorkingTime WorkingTime(min)
						 * LineCount CompileCount RunCount Time/Compile Time/Run
						 * BEWorkingTime(min) total => 4, BE = 10 LineCount = 5
						 */
						if (reqData == null) {
							System.err.println("reqData is null");
							return newTable;
						} else
							switch (reqData) {
							case "AllWorkingTime":
								String wt = data.get(4);
								if (wt == null || wt.equals("0")) {
									wt = "-";
								}
								record.add(wt);
								break;
							case "BlockWorkingTimeRate":
								String d = getBERate(data.get(4), data.get(10));
								record.add(d);
								break;
							case "CompileCorrectTime":
								String compileCT = convertCompileCT(data.get(19));
								record.add(compileCT);
								break;

							case "CompileCorrectTimeRate":
								String compileCTRate = getCCETRate(data.get(20));
								record.add(compileCTRate);
								break;

							case "LOC":
								String loc = data.get(5);
								if (loc == null || loc.equals("0")) {
									loc = "-";
								}
								record.add(loc);
								break;

							default:
								break;
							}
						// blockrate
						// String d = getBERate(data.get(4), data.get(10));
						// // String d = Integer.toString(data.get(5));
						// record.add(d);

						// loc
						// String loc = data.get(5);
						// if (loc == null || loc.equals("0")) {
						// loc = "-";
						// }
						// record.add(loc);

						// WT
						// String wt = data.get(4);
						// if (wt == null || wt.equals("0")) {
						// wt = "-";
						// }
						// record.add(wt);

						// compileCorrectTime
						// String compileCT = data.get(19);
						// record.add(compileCT);

						// compileCorrectTimeRate
						// String compileCTRate = data.get(20);
						// record.add(compileCTRate);

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
		// return q.isMandatory() && q.getLangReq() ==
		// NQuestion.LanguageRequirement.ANY;
		return q.isMandatory();
	}

	/**
	 */
	// @SuppressWarnings("unused")
	private String convertCompileCT(String stringTime) {
		// stringTimeType is 00:00(:00:0000)

		StringBuilder tmp = new StringBuilder(stringTime);
		int hour = Integer.parseInt(tmp.substring(0, 2));
		int min = Integer.parseInt(tmp.substring(3, 5));
		return String.valueOf((hour * 60 + min));
	}

	private String getCCETRate(String ccetr) {
		double tmp = 0;
		try {
			tmp = Double.parseDouble(ccetr);
		} catch (Exception ex) {
			return "-1";
		}
		if (tmp < 0 || Double.isNaN(tmp)) {
			return "-1";
		}
		String formated = format.format(tmp);
		// System.out.println(tmp + " : " + formated);
		return formated;
		// return String.valueOf(tmp);
	}

	private String getBERate(String total, String be) {
		double totalDouble = 0;
		try {
			totalDouble = Double.parseDouble(total);
		} catch (Exception ex) {
			return "-";
		}
		if (totalDouble <= 0) {
			return "-";
		}

		double beDouble = 0;
		try {
			beDouble = Double.parseDouble(be);
		} catch (Exception ex) {
			return "0";
		}

		double rate = beDouble / totalDouble;
		return format.format(rate);
		// return String.valueOf(rate);
	}

	private static final DecimalFormat format = new DecimalFormat("###.##");
}
