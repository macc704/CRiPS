import java.util.*;

/**
* �v���O�������F
* �쐬�ҁF 
* �쐬���F Thu Nov 15 10:21:19 JST 2012
*/
public class _30MethodTest extends Turtle{
	
	//�N������
	public static void main(String[] args) {
		_30MethodTest main = new _30MethodTest();
		main.run();
	}
	
	//���C������
	public void run() {
		m0();
		m1(100);
		m4();
		int x = m3();
		m5((double)x);
	}
	
	public void m0() {
	}
	
	public void m1(int x) {
		rt(x);
		x = 3;
		m2(1,true,"����������", 2);
	}
	
	public void m2(int a, boolean b, String c, double d) {
		if(b){
			fd(a);
		}else {
			print(c);
			d = Math.sin(d);
			//Math.sin(d);
		}
	}
	
	public int m3() {
		return 3;
	}
	
	public double m4() {
		double d = 1.0;
		int x = 0;
		return d;
	}
	
	public void m5(double d){
		fd(d);
	}
	
	
}			