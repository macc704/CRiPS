package ronproeditor.helpers;

import java.io.InputStream;
import java.io.PrintStream;

public interface IConsole {

	public void toLast();

	public InputStream getIn();

	public PrintStream getOut();

	public PrintStream getErr();

	public PrintStream getConsoleToStream();

	public void setConsoleToStream(PrintStream consoleToStream);

}
