package bc.classblockfilewriters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

import javax.swing.JOptionPane;

public class LangDefFileCopier implements Copier {

	private BufferedReader br;

	public void print(File file) {
		try {
			FileInputStream ldfReader = new FileInputStream(
					System.getProperty("user.dir") + "/ext/block/lang_def_turtle.xml");

			InputStreamReader ldfISR = new InputStreamReader(ldfReader, "SJIS");
			br = new BufferedReader(ldfISR);

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

			String ldfString = turtleByteArray.toString();

			FileOutputStream ldfOS = new FileOutputStream(file.getParentFile().getPath() + "/lang_def_project.xml");

			OutputStreamWriter ldfFOS = new OutputStreamWriter(ldfOS, "SJIS");
			BufferedWriter ldfWriter = new BufferedWriter(ldfFOS);

			ldfWriter.write(ldfString);
			ldfWriter.flush();
			ldfWriter.close();
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
