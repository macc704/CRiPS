import java.util.*;

/**
* �v���O�������F
* �쐬�ҁF 
* �쐬���F Tue Nov 13 15:31:47 JST 2012
*/
public class _22CastTest{
	
	//�N������
	public static void main(String[] args) {
		_22CastTest main = new _22CastTest();
		main.run();
	}
	
	//���C������
	public void run() {
		{ //���̂܂ܖ߂�Ȃ��D2��Ŗ߂�
			int i = (int)1.0;
			int j = Integer.parseInt("����������");
			double d = (double)2;
			double d1 = Double.parseDouble("����������");
			String s = Integer.toString(1);
			String s1 = Double.toString(1.0);
			double d3 = (double)i + 3;
		}
	}
	
}		