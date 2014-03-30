package bc.b2j.model;

import bc.apps.OutputSourceModel;

public class JavaSourceWriter {

	public JavaSourceWriter() {
	}

	public void print(ProgramModel root, OutputSourceModel out) {
		for (PageModel page : root.getPages()) {
			page.print2(out);

		}

	}

}
