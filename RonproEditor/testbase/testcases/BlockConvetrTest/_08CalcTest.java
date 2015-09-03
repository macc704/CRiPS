import java.util.*;

/**
* プログラム名：
* 作成者： 
* 作成日： Tue Nov 13 21:54:12 JST 2012
*/
public class _08CalcTest extends Turtle{
	
	//起動処理
	public static void main(String[] args) {
		_08CalcTest main = new _08CalcTest();
		main.run();
	}
	
	//メイン処理
	//Javaで足し算の連結は２項式の組み合わせでない．多項式となり他と意味が異なる
	public void run() {
		{//そのままもどんないっす　2回やれば戻る
			int i = 3 + 5 + 8;
			int j = 3 + 5 * 8;
			int k = 3 * 5 * 8;
			int l = 3 - 5 / 8;
			String t1 = "hoge";
			String t2 = "3" + t1;
			String t3 = t1 + "3";
			String t4 = "あなたの入力したのは" + t2 + "ですね";
			t4 = "あなたの入力したのは" + t2 + "ですね";
			int year = 3000;
			int japaneseYear = 4000;
			System.out.println(Integer.toString(year) + "年は" + japaneseYear + "年です");
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