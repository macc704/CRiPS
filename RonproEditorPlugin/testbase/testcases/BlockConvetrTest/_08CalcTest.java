import java.util.*;

/**
* �v���O�������F
* �쐬�ҁF 
* �쐬���F Tue Nov 13 21:54:12 JST 2012
*/
public class _08CalcTest extends Turtle{
	
	//�N������
	public static void main(String[] args) {
		_08CalcTest main = new _08CalcTest();
		main.run();
	}
	
	//���C������
	//Java�ő����Z�̘A���͂Q�����̑g�ݍ��킹�łȂ��D�������ƂȂ葼�ƈӖ����قȂ�
	public void run() {
		{//���̂܂܂��ǂ�Ȃ������@2����Ζ߂�
			int i = 3 + 5 + 8;
			int j = 3 + 5 * 8;
			int k = 3 * 5 * 8;
			int l = 3 - 5 / 8;
			String t1 = "hoge";
			String t2 = "3" + t1;
			String t3 = t1 + "3";
			String t4 = "���Ȃ��̓��͂����̂�" + t2 + "�ł���";
			t4 = "���Ȃ��̓��͂����̂�" + t2 + "�ł���";
			int year = 3000;
			int japaneseYear = 4000;
			System.out.println(Integer.toString(year) + "�N��" + japaneseYear + "�N�ł�");
			int japanese = 100;
			int math = 50;
			int english = 40;
			int tt = japanese + math + english;
			double total1 = (double)japanese + math + english;
			double total2 = (double)(japanese + math) + english;
			double total3 = ((double)japanese + math) + english;
			double total4 = (double)(japanese + math + english);
			double total5 = (double)japanese + math - english;
			double total6 = (double)japanese * math / english;
		}
	}
	
}	