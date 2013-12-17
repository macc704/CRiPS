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
		// Œ©‚Ã‚ç‚¢‚½‚ßA’¼Úˆø”‚É“n‚·¨ˆêŸ•Ï”‚Éˆê’UŠi”[‚µ‚Ä‚©‚çˆø”‚ğ“n‚·A‚É•ÏX
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