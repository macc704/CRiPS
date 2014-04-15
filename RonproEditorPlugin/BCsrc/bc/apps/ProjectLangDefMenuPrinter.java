package bc.apps;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ProjectLangDefMenuPrinter {

	private List<String> request = new ArrayList<String>();

	public ProjectLangDefMenuPrinter() {

	}

	public void setRequest(String str) {
		request.add(str);
	}

	public void print(PrintStream ps, int lineNumber, String name)
			throws Exception {
		makeIndent(ps, lineNumber);
		ps.println("<BlockGenusMember>" + name + "</BlockGenusMember>");

	}

	public void makeIndent(PrintStream out, int number) {
		for (int i = 0; i < number; i++) {
			out.print("\t");
		}
	}

}
