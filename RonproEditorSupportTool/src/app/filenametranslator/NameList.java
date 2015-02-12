package app.filenametranslator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class NameList {

	// private File file;
	private Map<String, String> map;

	public NameList(File file) {
		// this.file = file;
		map = create(file);
	}

	private Map<String, String> create(File file) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "MS932"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] splitedLine = line.split(",");
				map.put(splitedLine[0], splitedLine[1]);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	public String getStudentId(String name) {
		return map.get(name);
	}

	public Map<String, String> getNameList() {
		return map;
	}

}
