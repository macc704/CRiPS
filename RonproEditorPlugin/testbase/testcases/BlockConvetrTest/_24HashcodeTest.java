import java.util.*;

/**
* �v���O�������F
* �쐬�ҁF 
* �쐬���F Wed Nov 14 12:15:12 JST 2012
*/
public class _24HashcodeTest{
	
	//�N������
	public static void main(String[] args) {
		_24HashcodeTest main = new _24HashcodeTest();
		main.run();
	}
	
	//���C������
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