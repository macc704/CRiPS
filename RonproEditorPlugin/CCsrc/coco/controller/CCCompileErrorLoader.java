package coco.controller;

import coco.model.CCCompileError;
import coco.model.CCCompileErrorManager;

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
		int errorID = Integer.parseInt(tokenizer[0]);
		String filePath = tokenizer[1];
		long beginTime = Long.parseLong(tokenizer[2]);
		long endTime = Long.parseLong(tokenizer[3]);
		int correctTime = Integer.parseInt(tokenizer[4]);

		manager.addError(new CCCompileError(errorID, filePath, beginTime,
				endTime, correctTime));
	}
}