package ronproeditor.helpers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CompileErrorLog {
	String saveFileName = "compileErr.log";
	String presName = ".pres2";
	File path;
	long timestamp;

	public CompileErrorLog(File path, long timestamp) {
		this.path = path;
		this.timestamp = timestamp;
	}

	public void saveLog(List<String> outPutList) {
		// 内部的なコンパイルエラーの場合orコンパイルエラーが無い場合はreturn
		if (timestamp < 0||outPutList.isEmpty()) {
			return;
		}

		File presPath = new File(path, presName);
		File saveFile = new File(presPath, saveFileName);
		// System.out.println(saveFile.getAbsolutePath());
		try {
			if (!saveFile.exists()) { // 無ければ生成
				saveFile.createNewFile();

			}
			// PrintWriter pw = new PrintWriter(new BufferedWriter(new
			// FileWriter(saveFile)));
			FileWriter fw = new FileWriter(saveFile, true);
			StringBuffer sb = new StringBuffer();
			sb.append(timestamp);
			sb.append("\t");
			for (String s : outPutList) {
				sb.append(s.replaceAll("\t", "[Tab]").replaceAll("\n", "\t").replaceAll("\r", ""));
				sb.append("\t");
				System.out.println(s);
			}
			sb.append("\r\n");
			fw.write(sb.toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
