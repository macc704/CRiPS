package coco.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import coco.model.CCCompileErrorManager;

public class CCAddCompileErrorKinds {
	private CCCompileErrorManager manager;
	private int lines;

	public CCAddCompileErrorKinds(CCCompileErrorManager manager, int lines) {
		this.manager = manager;
		this.lines = lines;
	}

	public void addKinds(String inFileName, String outFileName)
			throws IOException {
		copyFile(inFileName, outFileName);
		FileWriter writer = new FileWriter(outFileName, true);
		while (manager.getAllKinds().size() >= lines) {
			String errorID = Integer.toString(lines);
			String message = manager.getKind(lines).getMessage();

			writer.write(errorID + "," + 6 + "," + message + "\n");
			lines++;
		}

		writer.close();
	}

	private void copyFile(String inFileName, String outFileName) {
		deleteOutFile(outFileName);
		try {
			BufferedReader breader = new BufferedReader(new InputStreamReader(
					new FileInputStream(inFileName), "SJIS"));
			FileWriter writer = new FileWriter(outFileName, true);
			String line;
			while ((line = breader.readLine()) != null) {
				writer.write(line + "\n");
			}
			breader.close();
			writer.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	private void deleteOutFile(String outFileName) {
		File outFile = new File(outFileName);
		if (outFile.exists()) {
			outFile.delete();
		}
	}
}
