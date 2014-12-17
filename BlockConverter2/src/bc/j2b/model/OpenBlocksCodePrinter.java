package bc.j2b.model;

import java.io.PrintStream;

@Deprecated
public class OpenBlocksCodePrinter {

	private String enc;

	public OpenBlocksCodePrinter(String enc) {
		this.enc = enc;
	}

	public void printXmlDec(PrintStream out, int indent) {
		makeIndent(out, indent);
		out.println("<?xml version=\"1.0\" encoding=\"" + enc + "\"?>");
		// out.println("<?xml version=\"1.0\" encoding=\"Shift_JIS\"?>");
	}

	public void printCodeBlocksTag(PrintStream out, int indent) {
		makeIndent(out, indent);
		out.println("<CODEBLOCKS>");
	}

	public void printCodeBlocksEndTag(PrintStream out, int indent) {
		makeIndent(out, indent);
		out.println("</CODEBLOCKS>");
	}

	public void printPagesTag(PrintStream out, int indent) {
		makeIndent(out, indent);
		out.println("<Pages>");
	}

	public void printPagesEndTag(PrintStream out, int indent) {
		makeIndent(out, indent);
		out.println("</Pages>");
	}

	public void printPageTag(PrintStream out, int indent, String name,
			int classNumber) {
		makeIndent(out, indent);
		out.print("<Page");
		out.print(" page-name=\"" + name + "\"");
		out.print(" page-color=\"40 40 40\"");
		out.print(" page-width=\"" + 1920 + "\"");
		out.print(" page-infullview=\"yes\"");
		out.print(" page-drawer=\"" + name + "\"");
		out.println(">");
	}

	public void printPageEndTag(PrintStream out, int indent) {
		makeIndent(out, indent);
		out.println("</Page>");
	}

	public void printPageBlocksTag(PrintStream out, int indent) {
		makeIndent(out, indent);
		out.println("<PageBlocks>");
	}

	public void printPageBlocksEndTag(PrintStream out, int indent) {
		makeIndent(out, indent);
		out.println("</PageBlocks>");
	}

	public void printBlockStubStartTag(PrintStream out, int indent,
			String parentGenus, String parentName) {
		makeIndent(out, indent);
		out.println("<BlockStub>");
		makeIndent(out, indent + 1);
		out.print("<StubParentName>");
		out.print(parentName);
		out.println("</StubParentName>");
		makeIndent(out, indent + 1);
		out.print("<StubParentGenus>");
		out.print(parentGenus);
		out.println("</StubParentGenus>");
	}

	public void printBlockBlockStubEndTag(PrintStream out, int indent) {
		makeIndent(out, indent);
		out.println("</BlockStub>");
	}

	public void printBlockTag(PrintStream out, int indent, String genusName,
			int id) {

		makeIndent(out, indent);
		out.print("<Block");
		out.print(" id=\"" + id + "\"");
		out.print(" genus-name=\"" + genusName + "\"");
		out.println(">");
	}

	public void printBlockEndTag(PrintStream out, int indent) {
		makeIndent(out, indent);
		out.println("</Block>");
	}

	public void printLabelTag(PrintStream out, int indent, String label) {
		makeIndent(out, indent);
		out.print("<Label>");
		out.print(label);
		out.println("</Label>");
	}

	public void printPageName(PrintStream out, int indent, String className) {
		makeIndent(out, indent);
		out.print("<PageLabel>");
		out.print(className);
		out.println("</PageLabel>");
	}

	public void printLocation(PrintStream out, int indent, int posX, int posY) {
		makeIndent(out, indent);
		out.println("<Location>");
		makeIndent(out, indent + 1);
		out.print("<X>");
		out.print(posX);
		out.println("</X>");
		makeIndent(out, indent + 1);
		out.print("<Y>");
		out.print(posY);
		out.println("</Y>");
		makeIndent(out, indent);
		out.println("</Location>");
	}

	public void printBeforeAfterBlockId(PrintStream out, int indent,
			int afterBlockId, int beforeBlockId) {
		if (beforeBlockId > 0) {
			makeIndent(out, indent);
			out.print("<BeforeBlockId>");
			out.print(beforeBlockId);
			out.println("</BeforeBlockId>");
		}

		if (afterBlockId > 0) {
			makeIndent(out, indent);
			out.print("<AfterBlockId>");
			out.print(afterBlockId);
			out.println("</AfterBlockId>");
		}
	}

	public void printSocketsPlug(PrintStream out, int indent, int numSockets,
			String connectorKind, String connectorType, String initType,
			String conLabel, boolean isExpandable, String positionType,
			int conId) {
		// if(connectorKind.equals())
		makeIndent(out, indent);
		if (connectorKind.equals("socket")) {
			out.print("<Sockets");
			out.print(" num-sockets=\"" + numSockets + "\"");
			out.println(">");
		} else if (connectorKind.equals("plug")) {
			out.println("<Plug>");
		}
		makeIndent(out, indent + 1);
		out.print("<BlockConnector ");
		out.print(" connector-kind=\"" + connectorKind + "\"");
		out.print(" connector-type=\"" + connectorType + "\"");
		out.print(" init-type=\"" + initType + "\"");
		out.print(" label=\"" + conLabel + "\"");
		if (isExpandable) {
			out.print(" is-expandable=\"yes\"");
		}
		out.print(" position-type=\"" + positionType + "\"");
		if (conId > 0) {
			out.print(" con-block-id=\"" + conId + "\"");
		}
		out.println("/>");
		makeIndent(out, indent);
		if (connectorKind.equals("socket")) {
			out.println("</Sockets>");
		} else if (connectorKind.equals("plug")) {
			out.println("</Plug>");
		}
	}

	public void printSocketsPlug(PrintStream out, int indent, int numSockets,
			String connectorKind, String connectorType, String initType,
			String conLabel, boolean isExpandable, String positionType,
			int first_conId, int second_conId) {
		makeIndent(out, indent);
		if (connectorKind.equals("socket")) {
			out.print("<Sockets");
			out.print(" num-sockets=\"" + numSockets + "\"");
			out.println(">");
		} else if (connectorKind.equals("plug")) {
			out.println("<Plug>");
		}
		makeIndent(out, indent + 1);
		out.print("<BlockConnector ");
		out.print(" connector-kind=\"" + connectorKind + "\"");
		out.print(" connector-type=\"" + connectorType + "\"");
		out.print(" init-type=\"" + initType + "\"");
		out.print(" label=\"" + conLabel + "\"");
		if (isExpandable) {
			out.print(" is-expandable=\"yes\"");
		}
		out.print(" position-type=\"" + positionType + "\"");
		if (first_conId > 0) {
			out.print(" con-block-id=\"" + first_conId + "\"");
		}
		out.println("/>");

		makeIndent(out, indent + 1);
		out.print("<BlockConnector ");
		out.print(" connector-kind=\"" + connectorKind + "\"");
		out.print(" connector-type=\"" + connectorType + "\"");
		out.print(" init-type=\"" + initType + "\"");
		out.print(" label=\"" + conLabel + "\"");
		if (isExpandable) {
			out.print(" is-expandable=\"yes\"");
		}
		out.print(" position-type=\"" + positionType + "\"");
		if (second_conId > 0) {
			out.print(" con-block-id=\"" + second_conId + "\"");
		}
		out.println("/>");
		makeIndent(out, indent);
		if (connectorKind.equals("socket")) {
			out.println("</Sockets>");
		} else if (connectorKind.equals("plug")) {
			out.println("</Plug>");
		}

	}

	public void makeIndent(PrintStream out, int number) {
		for (int i = 0; i < number; i++) {
			out.print("\t");
		}
	}

	public void printSocketsTag(PrintStream out, int indent, int numSockets) {
		makeIndent(out, indent);
		out.print("<Sockets");
		out.print(" num-sockets=\"" + numSockets + "\"");
		out.println(">");
	}

	public void printSocketsEndTag(PrintStream out, int indent) {
		makeIndent(out, indent);
		out.println("</Sockets>");
	}

	public void printPlugTag(PrintStream out, int indent) {
		makeIndent(out, indent);
		out.println("<Plug>");
	}

	public void printPlugEndTag(PrintStream out, int indent) {
		makeIndent(out, indent);
		out.println("</Plug>");
	}

	public void printBlockConnector(PrintStream out, int indent,
			String connectorKind, String connectorType, String initType,
			String conLabel, boolean isExpandable, String positionType,
			int conId) {

		makeIndent(out, indent + 1);
		out.print("<BlockConnector ");
		out.print(" connector-kind=\"" + connectorKind + "\"");
		out.print(" connector-type=\"" + connectorType + "\"");
		out.print(" init-type=\"" + initType + "\"");
		out.print(" label=\"" + conLabel + "\"");
		if (isExpandable) {
			out.print(" is-expandable=\"yes\"");
		}
		out.print(" position-type=\"" + positionType + "\"");
		if (conId > 0) {
			out.print(" con-block-id=\"" + conId + "\"");
		}
		out.println("/>");

	}
}
