package src.coco.controller;

import src.coco.model.CCCompileError;
import src.coco.model.CCCompileErrorManager;

public class CCCompileErrorLoader extends CCFileLoader {

	private CCCompileErrorManager manager;

	public CCCompileErrorLoader(CCCompileErrorManager manager) {
		this.manager = manager;
	}

	public void load(String filename) {
		loadData(filename);
	}

	@Override
	protected void separeteData(String line) {
		// 見づらいため、直接引数に渡す→一次変数に一旦格納してから引数を渡す、に変更
		String[] tokenizer = line.split(",");
		int errorID;
		String filePath;
		long beginTime;
		long endTime;
		int correctTime;

		errorID = Integer.parseInt(tokenizer[0]);
		filePath = tokenizer[1];
		beginTime = Long.parseLong(tokenizer[2]);
		endTime = Long.parseLong(tokenizer[3]);
		correctTime = Integer.parseInt(tokenizer[4]);

		CCCompileError error = new CCCompileError(errorID, filePath, beginTime, endTime, correctTime);
		manager.addError(error);
	}
}