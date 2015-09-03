/**
* プログラム名：
* 作成者： 
* 作成日： Wed Oct 03 21:54:44 JST 2012
*/
public class _06MouseTest extends Turtle {
	
	//起動処理
	public static void main(String[] args) {
		Turtle.startTurtle(new _06MouseTest());
	}
	
	
	//タートルを動かす処理
	public void start() {
		Turtle t = new Turtle();
		while(mouseDown() == true){
			int i = mouseX();
			boolean b = mouseDown();
			t.looks(t);
			if(t.intersects(t)){
				b = t.mouseDown();
				if(b == true){
					if(key() == 3){
					}
				}
			}
		}
		int i1 = mouseY();
		boolean b1 = mouseClicked();
		boolean b2 = leftMouseClicked();
		boolean b3 = rightMouseClicked();
		boolean b4 = doubleClick();
		boolean b5 = leftMouseDown();
		boolean b6 = rightMouseDown();
		boolean b7 = keyDown(3);
	}		
}				