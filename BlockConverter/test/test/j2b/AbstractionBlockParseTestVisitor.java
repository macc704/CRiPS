package test.j2b;


import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.LineComment;

import bc.j2b.analyzer.JavaCommentManager;

public class AbstractionBlockParseTestVisitor extends ASTVisitor {

	// private PrintStream out = System.out;
	JavaCommentManager resolveAbstract;
	private HashMap<Integer, String> abstractComments = new HashMap<Integer, String>();

	public AbstractionBlockParseTestVisitor(File file, String enc) {
		resolveAbstract = new JavaCommentManager(file, enc);
	}

	public HashMap<Integer, String> getAbstractComment() {

		return abstractComments;
	}

	// public AbstractionBlockParseTestVisitor(PrintStream out){
	// this.out = out;
	// }

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(CompilationUnit node) {

		List<ASTNode> comments = node.getCommentList();
		for (ASTNode comment : comments) {
			if (comment instanceof LineComment) {
				resolveAbstract.getLineComment(comment.getStartPosition());
			}
		}
		return true;
	}
	//
	// @Override
	// public boolean visit(Block node){
	//
	// out.print("Position:" + node.getStartPosition() + "	");
	// out.println("Class:" + node.getParent().getClass().getSimpleName());
	// return true;
	// }
}
