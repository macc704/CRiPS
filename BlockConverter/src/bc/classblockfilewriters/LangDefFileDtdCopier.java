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

public class LangDefFileDtdCopier implements Copier {

	BufferedReader br;

	public void print(File file) {
		// TODO Auto-generated method stub
		try {
			FileInputStream ldfReader = new FileInputStream(
					System.getProperty("user.dir") + "/ext/block/lang_def.dtd");

			// FileReader ldfReader = new FileReader(
			// "ext/block/lang_def_menu_turtle.xml");

			InputStreamReader ldfISR = new InputStreamReader(ldfReader,
					"ISO-8859-1");
			br = new BufferedReader(ldfISR);

			// File ldf = new File("/ext/block/lang_def.dtd");

			ByteArrayOutputStream turtleByteArray = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(turtleByteArray);
			// すべての行をコピーする
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.contains("<!ENTITY lang_def_")) {
					ps.println(line);
				}
			}

			// TODO 相対パスを計算する
			File tmp = new File(file.getPath());
			String home = "";
			
			while (!hasExtFolder(tmp)) {
				tmp = tmp.getParentFile();
				home = home + "../";
			}
			ps.println("<!ENTITY lang_def_menu_project SYSTEM \"lang_def_menu_project.xml\">");
			ps.println("<!ENTITY lang_def_genuses SYSTEM \"lang_def_genuses.xml\">");
			ps.println("<!ENTITY lang_def_genuses_project SYSTEM \"lang_def_genuses_project.xml\">");

			ps.println("<!ENTITY lang_def_connectorshapes SYSTEM \"" + home
					+ "ext/block/lang_def_connectorshapes.xml\">");
			ps.println("<!ENTITY lang_def_genuses_stubs SYSTEM \"" + home
					+ "ext/block/lang_def_genuses_stubs.xml\">");
			ps.println("<!ENTITY lang_def_genuses_datatypes SYSTEM \"" + home
					+ "ext/block/lang_def_genuses_datatypes.xml\">");
			ps.println("<!ENTITY lang_def_genuses_variable SYSTEM \"" + home
					+ "ext/block/lang_def_genuses_variable.xml\">");
			ps.println("<!ENTITY lang_def_genuses_calc SYSTEM \"" + home
					+ "ext/block/lang_def_genuses_calc.xml\">");
			ps.println("<!ENTITY lang_def_genuses_procedure SYSTEM \"" + home
					+ "ext/block/lang_def_genuses_procedure.xml\">");
			ps.println("<!ENTITY lang_def_genuses_object SYSTEM \"" + home
					+ "ext/block/lang_def_genuses_object.xml\">");
			ps.println("<!ENTITY lang_def_genuses_math SYSTEM \"" + home
					+ "ext/block/lang_def_genuses_math.xml\">");
			ps.println("<!ENTITY lang_def_genuses_cui SYSTEM \"" + home
					+ "ext/block/lang_def_genuses_cui.xml\">");
			ps.println("<!ENTITY lang_def_genuses_turtle SYSTEM \"" + home
					+ "ext/block/lang_def_genuses_turtle.xml\">");
			ps.println("<!ENTITY lang_def_families SYSTEM \"lang_def_families.xml\">");
			ps.println("<!ENTITY lang_def_etc SYSTEM \"" + home
					+ "ext/block/lang_def_etc.xml\">");
			// menu情報のコピー
			// psに書きだしたものをすべて文字列に変換する
			String ldfString = turtleByteArray.toString();

			FileOutputStream ldfOS = new FileOutputStream(file.getParentFile()
					.getPath() + "/lang_def.dtd");
			// FileReader ldfReader = new FileReader(
			// "ext/block/lang_def_menu_turtle.xml");
			OutputStreamWriter ldfFOS = new OutputStreamWriter(ldfOS,
					"ISO-8859-1");
			BufferedWriter ldfWriter = new BufferedWriter(ldfFOS);

			ldfWriter.write(ldfString);
			ldfWriter.flush();
			ldfWriter.close();
		} catch (Exception e) {
			int res = JOptionPane.showConfirmDialog(null,
					"Blockへの変換中にエラーが発生しました：lang_def_genuses_dtd", "警告",
					JOptionPane.DEFAULT_OPTION);
			if(res == 1){
				e.printStackTrace();
				throw new RuntimeException("言語定義ファイル出力時にエラーが発生しました：lang_def_genuses_dtd");
			}
		}
	}

	private boolean hasExtFolder(File file) {
		File[] files = file.getParentFile().listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory() && files[i].getName().equals("ext")) {
				return true;
			}
		}
		return false;
	}
}
