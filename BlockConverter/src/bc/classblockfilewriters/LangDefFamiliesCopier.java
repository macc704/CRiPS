package bc.classblockfilewriters;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.JOptionPane;

public class LangDefFamiliesCopier extends Copier {

	private static String LANG_DEF_FAMILIES_FILENAME = "lang_def_families.xml";

	public LangDefFamiliesCopier(String baseDir) {
		super(baseDir);
	}

	public void print(File file) {
		try {
			BufferedReader br = createBufferReader(LANG_DEF_FAMILIES_FILENAME);

			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			PrintStream printStream = new PrintStream(byteArray);
			// すべての行をコピーする
			String line;
			while ((line = br.readLine()) != null) {
				// 一行書き込み >>lang_def.xml
				printStream.println(line);
			}

			printDOM(byteArray.toString(), new FileOutputStream(file.getParentFile().getAbsolutePath() + "/lang_def_families.xml"));

			br.close();
			printStream.close();
		} catch (Exception e) {
			int res = JOptionPane.showConfirmDialog(null,
					"Blockへの変換中にエラーが発生しました：lang_def_families massage:" + e.getStackTrace().toString(), "警告",
					JOptionPane.DEFAULT_OPTION);
			if(res == 1){
				e.printStackTrace();
				return;
			}
		}
	}
}
