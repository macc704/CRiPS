package bc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class BCSystem {

	// public static PrintStream out = System.out;
	public static PrintStream out = new PrintStream(new NullOutputStream());
}

class NullOutputStream extends OutputStream {
	public void write(int b) throws IOException {
	}
}
