package bc.classblockfilewriters;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JOptionPane;

public class LangDefFileDtdCopier extends Copier {

	private static String LANG_DEF_DTD_FILE = "lang_def.dtd";

	public LangDefFileDtdCopier(String baseDir) {
		super(baseDir);
		this.enc = "ISO-8859-1";
	}

	@Override
	public void print(File file) {
		try {
			BufferedReader br = createBufferReader(LANG_DEF_DTD_FILE);
			ByteArrayOutputStream turtleByteArray = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(turtleByteArray);

			copyLangDefDtdFile(ps, br);

			String home = getHomeDir(file);

			printFileListToLangDefDtdFile(ps, br, home);

			// menu情報のコピー
			// psに書きだしたものをすべて文字列に変換する
			printDOM(turtleByteArray.toString(), new File(file.getParentFile().getPath() + "/lang_def.dtd"));
			br.close();
			ps.close();
		} catch (Exception e) {
			int res = JOptionPane.showConfirmDialog(null, "Blockへの変換中にエラーが発生しました：lang_def_genuses_dtd", "警告", JOptionPane.DEFAULT_OPTION);
			if (res == 1) {
				e.printStackTrace();
				throw new RuntimeException("言語定義ファイル出力時にエラーが発生しました：lang_def_genuses_dtd");
			}
		}
	}

	public void copyLangDefDtdFile(PrintStream ps, BufferedReader br) throws IOException {
		// すべての行をコピーする
		String line;
		while ((line = br.readLine()) != null) {
			if (!line.contains("<!ENTITY lang_def_")) {
				ps.println(line);
			}
		}
	}

	public void printFileListToLangDefDtdFile(PrintStream ps, BufferedReader br, String home){
		ps.println("<!ENTITY lang_def_menu_project SYSTEM \"lang_def_menu_project.xml\">");
		ps.println("<!ENTITY lang_def_genuses SYSTEM \"lang_def_genuses.xml\">");
		ps.println("<!ENTITY lang_def_genuses_project SYSTEM \"lang_def_genuses_project.xml\">");

		ps.println("<!ENTITY lang_def_connectorshapes SYSTEM \"" + home + getBaseDir() + "lang_def_connectorshapes.xml\">");
		ps.println("<!ENTITY lang_def_genuses_stubs SYSTEM \"" + home + getBaseDir() + "lang_def_genuses_stubs.xml\">");
		ps.println("<!ENTITY lang_def_genuses_datatypes SYSTEM \"" + home + getBaseDir() + "lang_def_genuses_datatypes.xml\">");
		ps.println("<!ENTITY lang_def_genuses_variable SYSTEM \"" + home + getBaseDir() + "lang_def_genuses_variable.xml\">");
		ps.println("<!ENTITY lang_def_genuses_calc SYSTEM \"" + home + getBaseDir() + "lang_def_genuses_calc.xml\">");
		ps.println("<!ENTITY lang_def_genuses_procedure SYSTEM \"" + home + getBaseDir() + "lang_def_genuses_procedure.xml\">");
		ps.println("<!ENTITY lang_def_genuses_object SYSTEM \"" + home + getBaseDir() + "lang_def_genuses_object.xml\">");
		ps.println("<!ENTITY lang_def_genuses_math SYSTEM \"" + home + getBaseDir() + "lang_def_genuses_math.xml\">");
		ps.println("<!ENTITY lang_def_genuses_cui SYSTEM \"" + home + getBaseDir() + "lang_def_genuses_cui.xml\">");
		ps.println("<!ENTITY lang_def_genuses_turtle SYSTEM \"" + home + getBaseDir() + "lang_def_genuses_turtle.xml\">");
		ps.println("<!ENTITY lang_def_genuses_obprogui SYSTEM \"" + home + getBaseDir() + "lang_def_genuses_obprogui.xml\">");
		ps.println("<!ENTITY lang_def_families SYSTEM \"lang_def_families.xml\">");
		ps.println("<!ENTITY lang_def_etc SYSTEM \"" + home + getBaseDir() + "lang_def_etc.xml\">");
	}

}
