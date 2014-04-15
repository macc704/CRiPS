package src.coco.controller;

import java.util.List;

import src.coco.model.CCCompileErrorManager;

public class CCCompileErrorKindLoader extends CCCsvFileLoader {

	private CCCompileErrorManager manager;

	// private int linesNumber = 1;

	public CCCompileErrorKindLoader(CCCompileErrorManager manager) {
		this.manager = manager;
	}

	public void load(String filename) {
		loadData(filename);
	}

	@Override
	protected void separeteData(List<String> lines) {
		int index = Integer.parseInt(lines.get(0));
		int rare = Integer.parseInt(lines.get(1));
		String message = lines.get(2);

		manager.put(index, rare, message);
		// linesNumber++;
	}

	// 最初の状態で追加したエラーの数を保存しておく
	// public int getLines() {
	// return linesNumber;
	// }
}