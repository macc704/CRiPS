import java.util.*;

/**
* プログラム名：
* 作成者： 
* 作成日： Tue Nov 13 21:54:12 JST 2012
*/
public class _00BasicTest extends Turtle{
	
	//起動処理
	public static void main(String[] args) {
		_00BasicTest main = new _00BasicTest();
		main.run();
	}
	
	//メイン処理
	public void run() {
		int i = 0;
		while(i < 3){
			fd(100);
			rt(90);
			i++;
			i = i + 1;
		}
	}
	
}								