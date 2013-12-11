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
		CCCompileError error = new CCCompileError();

		// 見づらいため、直接引数に渡す→一次変数に一旦格納してから引数を渡す、に変更
		String[] tokenizer = line.split(",");
		int errorID;
		String projectname = "";
		String filename;
		long beginTime;
		long endTime;

		// TODO もう少しきれいに描く
		if (tokenizer.length == 5) {
			// 論プロから fileまでがフルパスの想定
			errorID = Integer.parseInt(tokenizer[0]);
			projectname = tokenizer[1];
			filename = tokenizer[2];
			beginTime = Long.parseLong(tokenizer[3]);
			endTime = Long.parseLong(tokenizer[4]);
		} else {
			// CocoViewerStartから
			errorID = Integer.parseInt(tokenizer[0]);
			filename = tokenizer[1];
			beginTime = Long.parseLong(tokenizer[2]);
			endTime = Long.parseLong(tokenizer[3]);
		}

		error.setData(errorID, projectname, filename, beginTime, endTime);
		manager.getList(errorID).addError(error);
		manager.totalErrorCountUp();
	}
}