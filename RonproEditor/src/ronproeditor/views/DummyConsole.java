package ronproeditor.views;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import ronproeditor.helpers.IConsole;

public class DummyConsole implements IConsole {

	private InputStream in = new NullInputStream();

	private PrintStream out = new NullPrintStream();
	private PrintStream err = new NullPrintStream();
	private PrintStream consoleToStream = new NullPrintStream();

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

	class NullInputStream extends InputStream {
		@Override
		public int read() throws IOException {
			return 0;
		}
	}

	class NullPrintStream extends PrintStream {
		public NullPrintStream() {
			super(new OutputStream() {
				@Override
				public void write(int b) throws IOException {
				}
			});
		}
	}

}
