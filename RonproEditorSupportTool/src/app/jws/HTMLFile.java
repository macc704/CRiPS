package app.jws;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class HTMLFile {

	private String BREAK = "\n";

	private String TITLE = "2012年度課題";
	private String CSS_NAME = "cssbykengo.css";

	private File file = null;

	private List<File> files;

	private Map<String, String> commentMap;

	public HTMLFile(List<File> files, String path,
			Map<String, String> commentMap) {
		this.files = files;
		this.commentMap = commentMap;
		file = new File(path + "index.html");
	}

	public void output() {
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file), "sjis")));
			pw.print(createCode(files));
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String createCode(List<File> files) {
		StringBuffer buf = new StringBuffer();

		buf.append("<html>" + BREAK);

		// head
		buf.append("<head>" + BREAK);
		buf.append("<title>" + TITLE + "</title>" + BREAK);
		buf.append("<link href=\"" + CSS_NAME + "\" rel=\"stylesheet\"/>"
				+ BREAK);
		buf.append("</head>" + BREAK + BREAK);

		// body
		buf.append("<body>" + BREAK);
		buf.append("<div class=\"rowheader\">" + BREAK);
		buf.append("<H1>" + TITLE + "</H1>" + BREAK);
		buf.append("<div class=\"span10\">" + BREAK + BREAK);

		// table
		buf.append("<table>" + BREAK);

		for (File file : files) {
			buf.append("<tr>" + BREAK);
			// link
			buf.append("<td>" + BREAK);
			buf.append("<a href=\"./"
					+ file.getName().substring(0,
							file.getName().indexOf(".jar")) + ".jnlp\">");
			buf.append(file.getName().substring(0,
					file.getName().lastIndexOf("-")));
			buf.append("</a>" + BREAK);
			buf.append("</td>" + BREAK);
			// comment
			buf.append("<td>" + BREAK);
			String name = file.getName();
			name = name.substring(0, name.indexOf(".jar")) + ".zip";
			buf.append(commentMap.get(name));
			buf.append("</td>" + BREAK);
			buf.append("</tr>" + BREAK);
		}

		buf.append("</table>");

		buf.append("</div>" + BREAK);
		buf.append("</div>" + BREAK);
		buf.append("</body>" + BREAK);
		buf.append("</html>");

		return buf.toString();
	}

}
