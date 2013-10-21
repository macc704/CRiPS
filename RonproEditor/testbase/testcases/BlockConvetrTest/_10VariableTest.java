import java.util.*;

/**
* プログラム名：
* 作成者： 
* 作成日： Tue Nov 13 21:54:12 JST 2012
*/
public class _10VariableTest extends Turtle{
	
	//起動処理
	public static void main(String[] args) {
		_10VariableTest main = new _10VariableTest();
		main.run();
	}
	
	//メイン処理
	public void run() {
		int i = 0;
		i = i + 1;
		i++;
		double d = 0;
		d = d + 1;
		{	//この処理びみょう
			d++;
			d++;
		}
	}
	
}