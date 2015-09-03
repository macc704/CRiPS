/**
* プログラム名：
* 作成者： 
* 作成日： Mon Oct 21 16:20:24 JST 2013
*/
public class Test06 extends Turtle {
	
	//起動処理
	public static void main(String[] args) {
		Turtle.startTurtle(new Test06());
	}
	
	//タートルを動かす処理
	public void start() {
		Turtle t = new Turtle();
		if(t.getX() == 1){
			t = new Turtle();
		}
		t = t;
	}//  @(50, 50) [open]
	
}