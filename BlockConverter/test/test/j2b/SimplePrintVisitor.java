package test.j2b;

import java.io.PrintStream;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;

public class SimplePrintVisitor extends ASTVisitor {

	// private LinkedList<Object> stack = new LinkedList<Object>();

	private PrintStream out = System.out;

	public SimplePrintVisitor() {
	}

	public SimplePrintVisitor(PrintStream out) {
		this.out = out;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#preVisit(org.eclipse.jdt.core.dom
	 * .ASTNode)
	 */
	@Override
	public void preVisit(ASTNode node) {
		out.println("preVisit:" + node.getClass().getName());
	}

	private int x = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#preVisit2(org.eclipse.jdt.core.dom
	 * .ASTNode)
	 */
	@Override
	public boolean preVisit2(ASTNode node) {
		makeIndent();
		String name = node.getClass().getSimpleName();
		out.print("<");
		out.print(name);
		Map<?, ?> properties = node.properties();
		for (Object key : properties.keySet()) {
			Object value = properties.get(key);
			out.print(" ");
			out.print(key);
			out.print("=");
			out.print("\"" + value + "\"");
		}
		out.print(">");

		if (node instanceof SimpleName || node instanceof NumberLiteral
				|| node instanceof PrimitiveType) {
			out.print(node);
		}
		out.println();
		x++;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.core.dom.ASTVisitor#postVisit(org.eclipse.jdt.core.dom
	 * .ASTNode)
	 */
	@Override
	public void postVisit(ASTNode node) {
		x--;
		makeIndent();
		String name = node.getClass().getSimpleName();
		out.print("</");
		out.print(name);
		out.print(">");
		out.println();
	}

	private void makeIndent() {
		for (int i = 0; i < x; i++) {
			out.print("\t");
		}
	}
}
