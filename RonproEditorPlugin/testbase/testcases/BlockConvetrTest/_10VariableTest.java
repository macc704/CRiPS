import java.util.*;

/**
* �v���O�������F
* �쐬�ҁF 
* �쐬���F Tue Nov 13 21:54:12 JST 2012
*/
public class _10VariableTest extends Turtle{
	
	//�N������
	public static void main(String[] args) {
		_10VariableTest main = new _10VariableTest();
		main.run();
	}
	
	//���C������
	public void run() {
		int i = 0;
		i = i + 1;
		i++;
		double d = 0;
		d = d + 1;
		{	//���̏����т݂傤
			d++;
			d++;
		}
	}
	
}