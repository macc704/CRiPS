package test.j2b;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.eclipse.jdt.core.dom.CompilationUnit;

import bc.utils.ASTParserWrapper;

/**
 * 
 * @author Administrator
 * 
 */
public class AbstractBlockTest {

	public static void main(String[] args) throws Exception {
		new AbstractBlockTest().run();
	}

	void run() throws Exception {
		File dir = new File("testcase");
		JFileChooser loadFilechooser = new JFileChooser(dir);
		final FileFilter filterJavaExtention = new FileNameExtensionFilter(
				"JAVAƒtƒ@ƒCƒ‹(*.java)", "java");
		loadFilechooser.addChoosableFileFilter(filterJavaExtention);

		int loadSelected = loadFilechooser.showOpenDialog(loadFilechooser);
		if (loadSelected == JFileChooser.APPROVE_OPTION) {
			File loadFile = loadFilechooser.getSelectedFile();
			CompilationUnit unit = ASTParserWrapper.parse(loadFile,
					"JISAutoDetect", new String[] {});

			// ASTPrinter.printNode(unit, 0);
			AbstractionBlockParseTestVisitor visitor01 = new AbstractionBlockParseTestVisitor(
					loadFile, "JISAutoDetect");
			unit.accept(visitor01);

			// PrintStream ps = new PrintStream(
			// ChangeExtension.changeToXmlExtension(loadFile.getPath()));
			// AbstractionBlockParseTestVisitor visitor02 = new
			// AbstractionBlockParseTestVisitor(ps);
			// unit.accept(visitor02);
		}
	}
}
