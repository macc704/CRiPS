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
import java.util.HashMap;
import java.util.Map;

import bc.classblockfilewriters.LangDefFileReWriter.Family;

public class LangDefFamiliesCopier implements Copier {

	private BufferedReader br;
	private Map<String, Family> projectFamilies = new HashMap<String, Family>();

	public void print(File file) {
		// TODO Auto-generated method stub
		try {
			FileInputStream ldfReader = new FileInputStream(
					"ext/block/lang_def_families.xml");

			InputStreamReader ldfISR = new InputStreamReader(ldfReader, "SJIS");
			br = new BufferedReader(ldfISR);

			// File ldf = new File("/ext/block/lang_def.dtd");

			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			PrintStream printStream = new PrintStream(byteArray);
			// ���ׂĂ̍s���R�s�[����
			String line;
			while ((line = br.readLine()) != null) {
				// ��s�������� >>lang_def.xml
				printStream.println(line);
			}
			// �ǉ��̃t�@�~���[����������

			for (String key : projectFamilies.keySet()) {
				printStream.println("<BlockFamily>");

				for (String member : projectFamilies.get(key).getFamilyMember()) {
					makeIndent(printStream, 1);
					printStream.print("<FamilyMember>");
					printStream.print("local-var-object-" + member);
					printStream.println("</FamilyMember>");
				}
				printStream.println("</BlockFamily>");
			}

			for (String key : projectFamilies.keySet()) {
				printStream.println("<BlockFamily>");

				for (String member : projectFamilies.get(key).getFamilyMember()) {
					makeIndent(printStream, 1);
					printStream.print("<FamilyMember>");
					printStream.print("private-var-object-" + member);
					printStream.println("</FamilyMember>");
				}
				printStream.println("</BlockFamily>");
			}

			// ps�ɏ������������̂����ׂĕ�����ɕϊ�����
			String ldfString = byteArray.toString();

			FileOutputStream ldfOS = new FileOutputStream(file.getParentFile()
					.getPath() + "/lang_def_families.xml");
			// FileReader ldfReader = new FileReader(
			// "ext/block/lang_def_menu_turtle.xml");
			OutputStreamWriter ldfFOS = new OutputStreamWriter(ldfOS, "SJIS");
			BufferedWriter ldfWriter = new BufferedWriter(ldfFOS);

			ldfWriter.write(ldfString);
			ldfWriter.flush();
			ldfWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void makeIndent(PrintStream out, int indent) {
		for (int i = 0; i < indent; i++) {
			out.print("\t");
		}
	}

	public void setProjectFamilies(Map<String, Family> families) {
		this.projectFamilies = families;
	}

}