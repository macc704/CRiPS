package app.jws;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class JNLPFile {

	private String BREAK = "\n";
	private String TAB = "\t";

	// File
	private File file = null;

	// jnlp
	private String URL = "https://dl.dropbox.com/u/45222776/prog2012/middle/";
	private String jnlpFileName = "";

	// information
	private String TITLE = "middle";
	private String VENDOR = "prog2012";

	// resources
	private String VERSION = "1.7";
	private String LIBRARY = "blib.jar";
	private String jarFileName = "";

	// application-desc
	private String MAIN_CLASS = "JNLPMain";
	private String[] arguments = { "Middle" };

	public JNLPFile(String jarFileName, String path) {
		this.jarFileName = jarFileName;
		this.jnlpFileName = jarFileName
				.substring(0, jarFileName.indexOf("jar")) + "jnlp";
		this.file = new File(path + jnlpFileName);
	}

	public String createCode() {
		StringBuffer buf = new StringBuffer();

		// jnlp
		buf.append("<jnlp" + BREAK);
		buf.append(" spec=\"1.0+\" " + BREAK);
		buf.append(" codebase=\"" + URL + "\"" + BREAK);
		buf.append(" href=\"" + jnlpFileName + "\">" + BREAK + BREAK);

		// information
		buf.append(TAB + "<information>" + BREAK);
		buf.append(TAB + TAB + "<title>" + TITLE + "</title>" + BREAK);
		buf.append(TAB + TAB + "<vendor>" + VENDOR + "</vendor>" + BREAK);
		buf.append(TAB + TAB + "<offline-allowed/>" + BREAK);
		buf.append(TAB + "</information>" + BREAK + BREAK);

		// resources
		buf.append(TAB + "<resources>" + BREAK);
		buf.append(TAB + TAB + "<j2se version=\"" + VERSION + "\"/>" + BREAK);
		buf.append(TAB + TAB + "<jar href=\"" + LIBRARY + "\"/>" + BREAK);
		buf.append(TAB + TAB + "<jar href=\"" + jarFileName + "\"/>" + BREAK);
		buf.append(TAB + "</resources>" + BREAK + BREAK);

		// application-desc
		buf.append(TAB + "<application-desc main-class=\"" + MAIN_CLASS + "\">"
				+ BREAK);
		for (int i = 0; i < arguments.length; i++) {
			buf.append(TAB + TAB + "<argument>" + arguments[i] + "</argument>"
					+ BREAK);
		}
		buf.append(TAB + "</application-desc>" + BREAK + BREAK);

		buf.append("</jnlp>");

		return buf.toString();
	}

	public void outputFile() {
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file), "sjis")));
			pw.print(createCode());
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
