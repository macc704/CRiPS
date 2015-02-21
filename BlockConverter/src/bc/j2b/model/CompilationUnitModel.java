package bc.j2b.model;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import bc.BlockConverter;


public class CompilationUnitModel extends ElementModel {

	private List<ClassModel> classes = new ArrayList<ClassModel>();

	public CompilationUnitModel() {
	}

	public List<ClassModel> getClasses() {
		return classes;
	}

	public void addClass(ClassModel clazz) {
		clazz.setParent(this);
		classes.add(clazz);
	}

	@Override
	public void print(PrintStream out, int indent) {
		makeIndent(out, indent);
		out.println("<?xml version=\"1.0\" encoding=\""
				+ BlockConverter.ENCODING_BLOCK_XML + "\"?>");
		makeIndent(out, indent);
		out.println("<CODEBLOCKS>");
		makeIndent(out, indent + 1);
		out.println("<Pages>");
		for (int i = 0; i < classes.size(); i++) {
			ElementModel child = classes.get(i);
			child.print(out, indent + 2);
		}
		makeIndent(out, indent + 1);
		out.println("</Pages>");
		makeIndent(out, indent);
		out.println("</CODEBLOCKS>");
		// out.close();
	}
	
	public void print(PrintStream out, int indent, boolean isNewOpenBlocks){
		if(isNewOpenBlocks) {
			makeIndent(out, indent);
			out.println("<?xml version=\"1.0\" encoding=\""
					+ BlockConverter.ENCODING_BLOCK_XML + "\" standalone=\"no\"?>");
			makeIndent(out, indent);
			out.println("<cb:CODEBLOCKS xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
			makeIndent(out, indent + 1);
			out.println("xsi:schemaLocation=\"http://education.mit.edu/openblocks/ns http://education.mit.edu/openblocks/codeblocks.xsd\"");
			makeIndent(out, indent + 1);
			out.println("xmlns:cb=\"http://education.mit.edu/openblocks/ns\">");
			makeIndent(out, indent + 1);
			out.println("<Pages collapsible-pages=\"no\">");
			for (int i = 0; i < classes.size(); i++) {
				ElementModel child = classes.get(i);
				child.print(out, indent + 2);
			}
			makeIndent(out, indent + 1);
			out.println("</Pages>");
			makeIndent(out, indent);
			out.println("</cb:CODEBLOCKS>");
		} else {
			print(out, indent);
		}
	}
}
