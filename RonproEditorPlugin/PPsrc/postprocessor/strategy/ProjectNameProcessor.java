/*
 * ProjectNameProcessor.java
 * Created on 2013/02/10 by macchan
 * Copyright(c) 2013 Yoshiaki Matsuzawa, Shizuoka Univerisy
 */
package postprocessor.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.table.CCSVFileIO;

/**
 * @author macchan ProjectNameProcessor
 */
public class ProjectNameProcessor {

	// moodleNo, Lecture
	private Map<String, String> moodleToLecture = new HashMap<String, String>();

	public ProjectNameProcessor(CDirectory dir) {
		for (CFile file : dir.getFileChildren()) {
			if (file.getName().getName().indexOf("moodle_lecture_table") != -1) {
				load(file);
			}
		}
	}

	private void load(CFile file) {
		// データの形
		// Moodle, Lecture

		List<List<String>> table = CCSVFileIO.loadAsListList(file);
		for (int i = 1; i < table.size(); i++) {// ヘッダを飛ばすので１から
			List<String> record = table.get(i);
			String moodle = record.get(0);
			String lecture = record.get(1);
			moodleToLecture.put(moodle, lecture);
		}
	}

	public List<List<String>> process(List<List<String>> table) {
		List<List<String>> newTable = new ArrayList<List<String>>();

		// header
		List<String> header = table.get(0);
		List<String> newTableHeader = new ArrayList<String>();
		newTableHeader.add("Student");
		newTableHeader.add("Lecture");
		int size = header.size();
		for (int i = 1; i < size; i++) {// 最初の列は，かぶるので飛ばす
			newTableHeader.add(header.get(i));
		}
		newTable.add(newTableHeader);

		// body
		size = table.size();
		for (int i = 1; i < size; i++) {// ヘッダを飛ばすので１から
			List<String> record = table.get(i);
			newTable.add(processOne(record));
		}
		return newTable;
	}

	private List<String> processOne(List<String> record) {
		List<String> newRecord = new ArrayList<String>();
		// 一つ目の処理
		newRecord.add(getStudentNumber(record.get(0)));
		newRecord.add(getLecture(record.get(0)));
		// 残りをコピー
		int size = record.size();
		for (int i = 1; i < size; i++) {// 一つ目を飛ばすので１から
			newRecord.add(record.get(i));
		}
		return newRecord;
	}

	// 7011-1066-1(Root) -> 7011-1066
	private String getStudentNumber(String org) {
		String[] tokens = org.split("-");
		if (tokens.length != 3) {
			throw new RuntimeException();
		}
		return tokens[0] + "-" + tokens[1];
	}

	// 7011-1066-1(Root) -> 1
	private String getLecture(String org) {
		String[] tokens = org.split("-");
		if (tokens.length != 3) {
			throw new RuntimeException();
		}
		String moodleNo = tokens[2].substring(0, tokens[2].indexOf("("));
		if (!moodleToLecture.containsKey(moodleNo)) {
			throw new RuntimeException("no moodleNo = " + moodleNo);
		}
		return moodleToLecture.get(moodleNo);
	}
}
