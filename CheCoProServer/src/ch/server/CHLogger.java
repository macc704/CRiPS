package ch.server;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CHLogger {

	private PrintStream out = System.out;
	private DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss: ");

	public CHLogger() {
	}

	public void println(String contents) {
		String date = formatter.format(new Date());
		out.println(date + ":" + contents);
	}

}
