/**
* プログラム名：
* 作成者： 
* 作成日： Thu Nov 08 10:57:58 JST 2012
*/
public class _02IfElseTest extends Turtle {
	
	//起動処理
	public static void main(String[] args) {
		Turtle.startTurtle(new _02IfElseTest());
	}
	
	//タートルを動かす処理
	public void start() {
		int x = 0;
		if(x == 3){
		}else if(x == 4){
			fd(50);
		}
		
		if(x == 3){
		}else {
			if(x == 4){
			}
			fd(50);
		}
		
	}
}