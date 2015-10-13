package bc.classblockfilewriters;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JOptionPane;

public class LangDefGenusesCopier extends Copier {

	private static String LANG_DEF_GENUSES_FILE = "lang_def_genuses.xml";

	public LangDefGenusesCopier(String baseDir) {
		super(baseDir);
	}

	public void print(File file) {
		// lang_def_genuses.xmlファイルを書き換える
		try {
			printDOM(readGenusString(), new FileOutputStream(file.getParentFile().getPath() + "/lang_def_genuses.xml"));
		} catch (Exception e) {
			int res = JOptionPane.showConfirmDialog(null, "Blockへの変換中にエラーが発生しました：lang_def_genuses message:" + e.getStackTrace().toString(), "警告", JOptionPane.DEFAULT_OPTION);
			if (res == 1) {
				e.printStackTrace();
				throw new RuntimeException("言語定義ファイル出力時にエラーが発生しました：lang_def_genuses");
			}
		}
	}

	public String readGenusString() throws IOException{
		// すべてのlang_def_genusesの行をコピーする
		BufferedReader br = createBufferReader(LANG_DEF_GENUSES_FILE);

		ByteArrayOutputStream turtleByteArray = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(turtleByteArray);

		String line;
		while ((line = br.readLine()) != null) {
			// 一行書き込み >>lang_def.xml
			ps.println(line);
			// プロジェクトのブロック定義ファイルの追加
			if (line.contains("&lang_def_genuses_turtle")) {
				ps.println("&lang_def_genuses_project;");
			}
		}

		br.close();
		return turtleByteArray.toString();
	}

}
