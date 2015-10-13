package bc.classblockfilewriters;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.JOptionPane;

public class LangDefFileCopier extends Copier {

	private static String LANG_DEF_TURTLE_FILE = "lang_def_turtle.xml";
	private static String LANG_DEF_PROJECT_FILE = "lang_def_project.xml";

	public LangDefFileCopier(String baseDir) {
		super(baseDir);
	}

	public void print(File file) {
		try {
			BufferedReader br = createBufferReader(LANG_DEF_TURTLE_FILE);

			ByteArrayOutputStream turtleByteArray = new ByteArrayOutputStream();
			PrintStream turtlePs = new PrintStream(turtleByteArray);

			// lang_def_turtleをすべての行をコピーする
			String line;
			while ((line = br.readLine()) != null) {
				// 一行書き込み >>lang_def.xml
				if (line.contains("lang_def_menu")) {
					// メニューの書き換え
					turtlePs.println("\t\t&lang_def_menu_project;");
				} else {
					turtlePs.println(line);
				}
			}

			printDOM(turtleByteArray.toString(), new FileOutputStream(file.getParentFile().getPath() + "/" + LANG_DEF_PROJECT_FILE));

			br.close();
			turtlePs.close();
		} catch (Exception e) {
			int res = JOptionPane.showConfirmDialog(null,
					"言語定義ファイル出力時にエラーが発生しました：lang_def_file message:" + e.getStackTrace().toString(), "警告",
					JOptionPane.DEFAULT_OPTION);
			if(res == 1){
				throw new RuntimeException("言語定義ファイル出力時にエラーが発生しました：lang_def_file");
			}
		}

	}

}
