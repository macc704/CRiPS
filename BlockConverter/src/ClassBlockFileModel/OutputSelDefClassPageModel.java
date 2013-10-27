package ClassBlockFileModel;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class OutputSelDefClassPageModel {

	private File file;
	private File menuFile;

	// private String[] classpaths;

	private List<SelDefClassModel> requestClass = new ArrayList<SelDefClassModel>();

	public OutputSelDefClassPageModel(File file, File menuFile) {
		this.file = file;
		this.menuFile = menuFile;
		// this.classpaths = classpaths;
	}

	public void setSelDefClassModel(List<SelDefClassModel> models) {
		for (SelDefClassModel model : models) {
			requestClass.add(model);
		}
	}

	public void print() throws Exception {
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(byteArray);

		ByteArrayOutputStream menuByteArray = new ByteArrayOutputStream();
		PrintStream menuPs = new PrintStream(menuByteArray);

		int lineNum = 0;

		ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		ps.println("<BlockGenusList>");

		menuPs.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		menuPs.println("<BlockLangDef>");
		makeIndent(menuPs, lineNum + 1);
		menuPs.println("<BlockDrawerSet name=\"factory\" type=\"stack\" location=\"southwest\" window-per-drawer=\"no\" drawer-draggable=\"no\">");

		makeIndent(menuPs, lineNum + 2);
		menuPs.println("<BlockDrawer name=\"self-def-class\" type=\"factory\" button-color=\"247 0 0\">");

		for (SelDefClassModel selDefClass : requestClass) {
			selDefClass.print(ps, 0, menuPs);
		}

		ps.println("</BlockGenusList>");
		makeIndent(menuPs, lineNum + 2);
		menuPs.println("</BlockDrawer>");
		makeIndent(menuPs, lineNum + 1);
		menuPs.println("</BlockDrawerSet>");
		menuPs.println("</BlockLangDef>");

		String blockString = byteArray.toString();
		String menuString = menuByteArray.toString();

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"));
		BufferedWriter menuBw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(menuFile), "UTF-8"));

		bw.write(blockString);
		bw.flush();
		bw.close();

		menuBw.write(menuString);
		menuBw.flush();
		menuBw.close();

		ps.close();
		menuPs.close();
	}

	public void makeIndent(PrintStream out, int number) {
		for (int i = 0; i < number; i++) {
			out.print("\t");
		}
	}
}
