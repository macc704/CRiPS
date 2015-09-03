/**
* プログラム名：
* 作成者： 
* 作成日： Wed Oct 03 21:54:44 JST 2012
*/
public class _02TurtleTest extends Turtle {
	
	//起動処理
	public static void main(String[] args) {
		Turtle.startTurtle(new _02TurtleTest());
	}
	
	
	//タートルを動かす処理
	public void start() {
		Turtle t = new Turtle();
		t.fd(100);
		t.bk(100);
		int a = t.input();
		fd(input());
		Turtle t1 = new Turtle();
		t1 = t;
	}
}						