import java.util.*;

/**
* プログラム名：
* 作成者： 
* 作成日： Tue Nov 13 21:54:12 JST 2012
*/
public class _12ColorTest extends Turtle{
	
	//起動処理
	public static void main(String[] args) {
		_12ColorTest main = new _12ColorTest();
		main.run();
	}
	
	//メイン処理
	public void run() {
		color(java.awt.Color.blue);
		color(java.awt.Color.cyan);
		color(java.awt.Color.green);
		color(java.awt.Color.magenta);
		color(java.awt.Color.orange);
		color(java.awt.Color.pink);
		color(java.awt.Color.red);
		color(java.awt.Color.white);
		color(java.awt.Color.yellow);
		color(java.awt.Color.gray);
		color(java.awt.Color.lightGray);
		color(java.awt.Color.darkGray);
		color(java.awt.Color.black);
		Turtle t = new Turtle();
		t.color(java.awt.Color.red);
	}
	
}