/**
* プログラム名：
* 作成者： 
* 作成日： Wed Oct 03 21:54:44 JST 2012
*/
public class _07EmptyBlockTest extends Turtle {
	
	//起動処理
	public static void main(String[] args) {
		Turtle.startTurtle(new _07EmptyBlockTest());
	}
	
	
	//タートルを動かす処理
	public void start() {
		{//これはそのままもどんないっす　2回やれば戻る
			fd(100);;;
			rt(90);
			int x = 3;
			while(x > 3);{	//＜ここに;
				fd(10);
			}
		}
	}		
}				