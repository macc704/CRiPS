/*
 * NayoseProcessor.java
 * Created on 2013/02/09 by macchan
 * Copyright(c) 2013 Yoshiaki Matsuzawa, Shizuoka Univerisy
 */
package postprocessor.strategy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author macchan NayoseProcessor
 */
public class NayoseProcessor {

	public NayoseProcessor() {
	}

	public List<List<String>> process(List<List<String>> table) {
		Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();
		map.put("header", table.get(0));// header

		// body
		for (int i = 1; i < table.size(); i++) {// ヘッダを飛ばすので１から
			List<String> record = table.get(i);
			String student = record.get(0);
			String lecture = record.get(1);
			String fileName = record.get(2);
			String key = student + lecture + fileName;
			if (map.containsKey(key)) {
				// 名寄せする
				List<String> already = map.get(key);
				aggregate(already, record);
			} else {
				map.put(key, record);
			}
		}
		return new ArrayList<List<String>>(map.values());
	}

	private void aggregate(List<String> alreadyRecord, List<String> newRecord) {
		int size = alreadyRecord.size();
		for (int i = 3; i < size; i++) {// 0, 1, 2はStudent, Lecture, Nameなので飛ばす
			alreadyRecord.set(i,
					aggregate(alreadyRecord.get(i), newRecord.get(i)));
		}
	}

	private String aggregate(String already, String newOne) {
		try {
			double d1 = Double.parseDouble(already);
			double d2 = Double.parseDouble(newOne);
			return Double.toString(d1 + d2);
		} catch (Exception ex) {
			return already + ", " + newOne;
		}
	}
}
