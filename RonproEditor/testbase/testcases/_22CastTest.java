import java.util.*;

/**
* プログラム名：
* 作成者： 
* 作成日： Tue Nov 13 15:31:47 JST 2012
*/
public class _22CastTest{
	
	//起動処理
	public static void main(String[] args) {
		_22CastTest main = new _22CastTest();
		main.run();
	}
	
	//メイン処理
	public void run() {
		{ //そのまま戻らない．2回で戻る
			int i = (int)1.0;
			int j = Integer.parseInt("あいうえお");
			double d = (double)2;
			double d1 = Double.parseDouble("あいうえお");
			String s = Integer.toString(1);
			String s1 = Double.toString(1.0);
			double d3 = (double)i + 3;
		}
	}
	
}		