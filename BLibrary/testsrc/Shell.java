import java.awt.Color;

/*
 * Shell.java
 * Created on 2012/01/09
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */

/**
 * @author macchan
 */
public class Shell extends Turtle {

	public static void main(String[] args) {
		Turtle.startTurtle(new Shell());
	}

	public void start() {
		window.canvas().setBackground(Color.red);
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 4; j++) {
				fd((i) * 10);
				rt(90);
				System.out.println(getX());//常に100を返すバグ
				System.out.println(x());
				System.out.println(location());	
			}
			rt(20);
			// System.out.println(getX());//常に100を返すバグ
			// System.out.println(x());
			// System.out.println(location());
			//System.out.println(getLocation());	
		}		
	}
}
