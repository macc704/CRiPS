package src.coco.controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

abstract class CCFileLoader {
	/***************************
	 * åpè≥:CCAddCompileErrorKinds, CCompileErrorLoader
	 ***************************/

	protected void loadData(String filename) {
		try {
			BufferedReader breader = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename), "SJIS"));
			String line = breader.readLine(); // àÍçsñ⁄ì«Ç›îÚÇŒÇµ
			while ((line = breader.readLine()) != null) {
				separeteData(line);
			}
			breader.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	abstract protected void separeteData(String line) throws IOException;
}