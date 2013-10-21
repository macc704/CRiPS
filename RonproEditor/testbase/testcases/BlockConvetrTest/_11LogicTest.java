import java.util.*;

/**
* プログラム名：
* 作成者： 
* 作成日： Tue Nov 13 21:54:12 JST 2012
*/
public class _11LogicTest extends Turtle{
	
	//起動処理
	public static void main(String[] args) {
		_11LogicTest main = new _11LogicTest();
		main.run();
	}
	
	//メイン処理
	public void run() {
		{//そのままもどらない，けど明確になっているのでＯＫ．2回で戻る
			boolean b1 = true;
			boolean b2 = false;
			boolean b3 = b1 && b2;
			boolean b4 = b1 || b2;
			boolean b5 = !b1;
			boolean b6 = !b1 && b2;
			boolean b7 = !(b1 && b2);
			boolean b8 = !!b1;
			boolean b9 = !!(b1 && b2);
			if(!!(!b1 && b2)){
			}
		}
	}
	
}