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

import javax.swing.JFrame;
import javax.swing.JLabel;

public class LangDefGenusesCopier implements Copier {

	private BufferedReader br;

	public void print(File file) {
		try {
			FileInputStream ldfReader = new FileInputStream(
					System.getProperty("user.dir")+ "/ext/block/lang_def_genuses.xml");

			InputStreamReader ldfISR = new InputStreamReader(ldfReader, "UTF-8");
			br = new BufferedReader(ldfISR);

			ByteArrayOutputStream turtleByteArray = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(turtleByteArray);
			// すべての行をコピーする
			String line;
			while ((line = br.readLine()) != null) {
				// 一行書き込み >>lang_def.xml
				ps.println(line);
				// プロジェクトのブロック定義ファイルの追加
				if (line.contains("&lang_def_genuses_turtle")) {
					ps.println("&lang_def_genuses_project;");
				}
			}
			// menu情報のコピー
			// psに書きだしたものをすべて文字列に変換する
			String ldfString = turtleByteArray.toString();

			FileOutputStream ldfOS = new FileOutputStream(file.getParentFile()
					.getPath() + "/lang_def_genuses.xml");

			OutputStreamWriter ldfFOS = new OutputStreamWriter(ldfOS, "UTF-8");
			BufferedWriter ldfWriter = new BufferedWriter(ldfFOS);

			ldfWriter.write(ldfString);
			ldfWriter.flush();
			ldfWriter.close();
		} catch (Exception e) {
			JFrame frame = new JFrame();
			JLabel label = new JLabel(e.getMessage());
			frame.add(label);
			frame.setSize(300, 300);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
			e.printStackTrace();
			throw new RuntimeException("言語定義ファイル出力時にエラーが発生しました：lang_def_genuses");
		}

	}

}
