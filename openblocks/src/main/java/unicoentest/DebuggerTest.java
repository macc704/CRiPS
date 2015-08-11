package unicoentest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.unicoen.node.UniBlock;
import net.unicoen.node.UniClassDec;
import net.unicoen.node.UniExpr;
import net.unicoen.node.UniIdent;
import net.unicoen.node.UniIntLiteral;
import net.unicoen.node.UniMethodCall;
import net.unicoen.node.UniMethodDec;
import net.unicoen.node.UniStringLiteral;
import net.unicoen.node.UniVariableDec;
import edu.inf.shizuoka.debugger.DebuggerWorkspaceController;

public class DebuggerTest {

	public static void main(String[] args) {
		DebuggerTest test = new DebuggerTest();
		test.run();
	}

	public void run() {
//
//		UniClassDec dec = new UniClassDec("DebuggerTest", new ArrayList<String>(), new ArrayList<>());
//
//		UniMethodDec startMethod = new UniMethodDec("start", new ArrayList<>(), "void", new ArrayList<>(), new UniBlock());
//		startMethod.block.body = new ArrayList<>();
//		dec.members.add(startMethod);
//
//		UniVariableDec value = new UniVariableDec(new ArrayList<>(), "int", "i", new UniIntLiteral(1));
//		startMethod.block.body.add(value);
//
//		List<UniExpr> args = new ArrayList<>();
//		args.add(new UniStringLiteral("Hello World"));
//
//		UniMethodCall caller = new UniMethodCall(new UniIdent("MyLib"), "print", args);
//		startMethod.block.body.add(caller);
//
//		File blockSrc = new File("unicoen/" + dec.className + ".xml");
//
//		try {
//			blockSrc.createNewFile();
//			DebuggerWorkspaceController dws = new DebuggerWorkspaceController(dec, "ext/blocks/lang_def.xml", blockSrc);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		// BlockEditorDebbugger debugger = new BlockEditorDebbugger(ws, blocks)

	}

}
