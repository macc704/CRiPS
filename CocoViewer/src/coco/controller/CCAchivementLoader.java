package coco.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import coco.model.CCAchivementData;

public class CCAchivementLoader extends CCCsvFileLoader {

	ArrayList<CCAchivementData> data = new ArrayList<CCAchivementData>();

	public CCAchivementLoader(ArrayList<CCAchivementData> data) {
		this.data = data;
	}

	public void load(String filename) {
		loadData(filename);
	}

	protected void separeteData(List<String> lines) throws IOException {
		int property = Integer.parseInt(lines.get(0));
		int threshold = Integer.parseInt(lines.get(1));
		String hirotitle = lines.get(2);
		String explanation = lines.get(3);
		CCAchivementData datum = new CCAchivementData();
		datum.set(property, threshold, hirotitle, explanation);
		data.add(datum);
	}
}
