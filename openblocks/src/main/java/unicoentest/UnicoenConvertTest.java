package unicoentest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import net.unicoen.mapper.ExtendedExpressionMapper;
import net.unicoen.node.UniClassDec;
import net.unicoen.parser.blockeditor.BlockGenerator;
import edu.mit.blocks.controller.WorkspaceController;

public class UnicoenConvertTest {

	public static void main(String[] args) {
		UnicoenConvertTest test = new UnicoenConvertTest();

		try {
			test.main();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void main() throws IOException {
		// javaファイルを設定

		ExtendedExpressionMapper mapper = new ExtendedExpressionMapper(false);

		Object node = mapper.parseFile("unicoen/Hoge.java");

		if(node instanceof UniClassDec){
			// javaファイルからunicoenモデルを作成
			UniClassDec classDec = (UniClassDec)node;

			File outputFile = new File("unicoen/"+ classDec.className + ".xml");
			outputFile.createNewFile();
			PrintStream out = new PrintStream(outputFile);

			BlockGenerator generator = new BlockGenerator(out, "ext/blocks/lang_def.xml");
			// unicoenモデルからBlockのXmlを作成
			generator.parse(classDec);

			// BlockEditor起動
			WorkspaceController controller = new WorkspaceController();
			controller.openBlockEditor("blockeditor/" + classDec.className + ".xml");

		}else{
			System.out.println("class parse error");
		}
	}

	public String getSrc(String path) throws IOException {
		File file = new File(path);

		FileReader fr = new FileReader(file);
		BufferedReader reader = new BufferedReader(fr);

		String line;
		String src = "";

		for (line = reader.readLine(); line != null; line = reader.readLine()) {
			src = src + line;
		}

		reader.close();
		fr.close();

		return src;
	}

}
