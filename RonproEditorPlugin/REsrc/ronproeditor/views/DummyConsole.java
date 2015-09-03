package ronproeditor.views;

import java.io.InputStream;
import java.io.PrintStream;

import ronproeditor.helpers.IConsole;
import clib.common.execution.CNullInputStream;
import clib.common.execution.CNullPrintStream;

public class DummyConsole implements IConsole {

	private InputStream in = CNullInputStream.INSTANCE;

	private PrintStream out = CNullPrintStream.INSTANCE;
	private PrintStream err = CNullPrintStream.INSTANCE;
	private PrintStream consoleToStream = CNullPrintStream.INSTANCE;

	public void toLast() {
	}

	public InputStream getIn() {
		return in;
	}

	public void setIn(InputStream in) {
		this.in = in;
	}

	public PrintStream getOut() {
		return out;
	}

	public void setOut(PrintStream out) {
		this.out = out;
	}

	public PrintStream getErr() {
		return err;
	}

	public void setErr(PrintStream err) {
		this.err = err;
	}

	public PrintStream getConsoleToStream() {
		return consoleToStream;
	}

	public void setConsoleToStream(PrintStream consoleToStream) {
		this.consoleToStream = consoleToStream;
	}

}
