/**
* プログラム名：
* 作成者： 
* 作成日： Wed Oct 03 21:54:44 JST 2012
*/
public class _05WindowTest extends Turtle {
	
	//起動処理
	public static void main(String[] args) {
		Turtle.startTurtle(new _05WindowTest());
	}
	
	
	//タートルを動かす処理
	public void start() {
		window.warp(30,30);
		fd(100);
	}
}						