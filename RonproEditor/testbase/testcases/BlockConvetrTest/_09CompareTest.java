import java.util.*;

/**
* プログラム名：
* 作成者： 
* 作成日： Tue Nov 13 21:54:12 JST 2012
*/
public class _09CompareTest extends Turtle{
	
	//起動処理
	public static void main(String[] args) {
		_09CompareTest main = new _09CompareTest();
		main.run();
	}
	
	//メイン処理
	public void run() {
		{	//
			boolean b1 = 3 <= 4;
			boolean b2 = 3 < 4;
			boolean b3 = 3 >= 4;
			boolean b4 = 3 > 4;
			boolean b5 = 3 == 4;
			boolean b6 = 3 != 4;
		}
		{	//
			boolean b1 = 3.0 <= 4;
			boolean b2 = 3.0 < 4;
			boolean b3 = 3.0 >= 4;
			boolean b4 = 3.0 > 4;
			boolean b5 = 3.0 == 4;
			boolean b6 = 3.0 != 4;
		}
		{	//
			boolean b1 = true;
			boolean b2 = false;
			boolean b3 = b1 == b2;
			boolean b4 = b1 != b2;
		}
		{	//
			String a = "abc";
			String b = "def";
			boolean b1 = a.equals(b);
			boolean b2 = !(a.equals(b));
			boolean b3 = a == b;
			boolean b4 = a != b;
		}
		{	//
			Object a = new Object();
			Object b = new Object();
			boolean b1 = a.equals(b);
			boolean b2 = !(a.equals(b));
			boolean b3 = a == b;
			boolean b4 = a != b;
		}
	}
	
}