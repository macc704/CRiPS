package ClassBlockFileModel;

import java.io.File;
import java.io.PrintStream;

public class OutputSelDefClassPageModel {

	private File file;
	private String enc;
	private String[] classpaths;

	private SelDefClassModel requestClass;

	public OutputSelDefClassPageModel(File file, String enc, String[] classpaths) {
		this.file = file;
		this.enc = enc;
		this.classpaths = classpaths;
	}

	public void setSelDefClassModel(SelDefClassModel classModel) {
		requestClass = classModel;
	}

	public void print() throws Exception {
		PrintStream ps = new PrintStream(file, enc);
		requestClass.print(ps, 0);
		ps.close();
	}

}
