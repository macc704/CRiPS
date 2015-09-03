import java.util.*;

/**
* プログラム名：
* 作成者： 
* 作成日： Wed Nov 14 12:15:12 JST 2012
*/
public class _24HashcodeTest{
	
	//起動処理
	public static void main(String[] args) {
		_24HashcodeTest main = new _24HashcodeTest();
		main.run();
	}
	
	//メイン処理
	public void run() {
		String s = "abc";
		int a = s.hashCode();
		int b = hashCode();
		System.out.println(s.hashCode());
		int c = "abc".hashCode();
		int d = ("abc" + "def").hashCode();
		int e = (s + "def").hashCode();
	}
	
}					