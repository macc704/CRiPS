package coco.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

abstract class CCCsvFileLoader {
	/***************************
	 * 継承:CCCompileErrorConverter, CCCompileErrorKindLoader
	 ***************************/

	protected void loadData(String filename) {
		try {
			ICsvListReader reader = new CsvListReader(new InputStreamReader(
					new FileInputStream(filename), "SJIS"),
					CsvPreference.EXCEL_PREFERENCE);
			// header 読み飛ばし
			reader.getHeader(true);
			List<String> lines = null;

			while ((lines = reader.read()) != null) {
				separeteData(lines);
			}
			reader.close();
		} catch (IOException e) {
			System.out.println(e);
		} finally {
		}
	}

	abstract protected void separeteData(List<String> lines) throws IOException;
}