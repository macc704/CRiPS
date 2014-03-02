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
		int errorID = Integer.parseInt(tokenizer[0]);
		String filePath = tokenizer[1];
		long beginTime = Long.parseLong(tokenizer[2]);
		long endTime = Long.parseLong(tokenizer[3]);
		int correctTime = Integer.parseInt(tokenizer[4]);

		manager.addError(new CCCompileError(errorID, filePath, beginTime,
				endTime, correctTime));
	}
}