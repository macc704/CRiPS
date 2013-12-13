/*
 * WaitRepaintTest.java
 * Created on 2013/12/12
 */

/**
 * @author hakamata
 */
public class WaitRepaintTest extends Turtle{
	public static void main(String[] args) {
		Turtle.startTurtle(new WaitRepaintTest(), args);
	}

	public void start() {
		hide();
		TextTurtle text = new TextTurtle("test");
		update();
	}
}