import java.util.*;

public class _20CUITest{
	
	//起動処理
	public static void main(String[] args) {
		_20CUITest main = new _20CUITest();
		main.run();
	}
	
	//メイン処理
	public void run() {
		System.out.print("a");
		System.out.println("a");
		double d1 = 30.0;
		double d2 = 30;
		double d = Math.random();
		Scanner c = new Scanner(System.in);
		int a = c.nextInt();
		String s = c.next();
		Scanner scanner = new Scanner(System.in);
		{	//nextLine()は間違いで，next()です．
			System.out.print(scanner.nextLine());
			System.out.print(scanner.next());
		}
		System.out.print(scanner.nextDouble());
	}
}								