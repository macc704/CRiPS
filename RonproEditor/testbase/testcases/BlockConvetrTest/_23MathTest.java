import java.util.*;

/**
* プログラム名：
* 作成者： 
* 作成日： Wed Nov 14 12:15:12 JST 2012
*/
public class _23MathTest{
	
	//起動処理
	public static void main(String[] args) {
		_23MathTest main = new _23MathTest();
		main.run();
	}
	
	//メイン処理
	public void run() {
		double a = Math.sqrt(4.0);
		double b = Math.sin(Math.toRadians(90.0));
		double c = Math.cos(100);
		double d = Math.tan(100);
		double e = Math.log(100);
		double f = Math.toRadians(100);
		{	//定義されていない分
			double g = Math.acos(100);
			double h = Math.acos(100) + 20;
		}
	}
	
}					