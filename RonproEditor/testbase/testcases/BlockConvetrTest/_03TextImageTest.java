/**
* プログラム名：
* 作成者： 
* 作成日： Tue Nov 06 14:51:41 JST 2012
*/
public class _03TextImageTest extends Turtle {
	
	//起動処理
	public static void main(String[] args) {
		Turtle.startTurtle(new _03TextImageTest());
	}
	
	//タートルを動かす処理
	public void start() {
		TextTurtle tt = new TextTurtle("aaa");
		tt.text("あいうえお");
		String text = tt.getText();
		ImageTurtle it = new ImageTurtle("bbb");
		it.image("aaa");
		it.size(100,300);
		TextTurtle tt2 = new TextTurtle();
		tt2 = tt;
		ImageTurtle it2 = new ImageTurtle();
		it2 = it;
	}
	
}							