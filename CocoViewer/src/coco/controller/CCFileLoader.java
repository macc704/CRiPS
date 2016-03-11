package coco.controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class CCFileLoader {
	/***************************
	 * 継承:CCAddCompileErrorKinds, CCompileErrorLoader
	 ***************************/

	public void loadData(String filename) {
		try {
			BufferedReader breader = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename), "SJIS"));
			String line = breader.readLine(); // 一行目読み飛ばし
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