import java.util.*;

/**
* �v���O�������F
* �쐬�ҁF 
* �쐬���F Tue Nov 13 21:54:12 JST 2012
*/
public class _11LogicTest extends Turtle{
	
	//�N������
	public static void main(String[] args) {
		_11LogicTest main = new _11LogicTest();
		main.run();
	}
	
	//���C������
	public void run() {
		{//���̂܂܂��ǂ�Ȃ��C���ǖ��m�ɂȂ��Ă���̂łn�j�D2��Ŗ߂�
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