package coco.controller;

import java.io.IOException;
import java.util.List;

import coco.model.CCCompileErrorManager;

public class CCMetricsLoader extends CCCsvFileLoader {

	private CCCompileErrorManager manager;

	public CCMetricsLoader(CCCompileErrorManager manager) {
		this.manager = manager;
	}

	public void load(String filename) {
		loadData(filename);
	}

	@Override
	protected void separeteData(List<String> lines) throws IOException {
		manager.addTotalWorkingTime(Integer.parseInt(lines.get(3)));
	}

}
