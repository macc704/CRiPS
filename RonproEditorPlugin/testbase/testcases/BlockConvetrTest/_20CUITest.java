import java.util.*;

public class _20CUITest{
	
	//�N������
	public static void main(String[] args) {
		_20CUITest main = new _20CUITest();
		main.run();
	}
	
	//���C������
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
		{	//nextLine()�͊ԈႢ�ŁCnext()�ł��D
			System.out.print(scanner.nextLine());
			System.out.print(scanner.next());
		}
		System.out.print(scanner.nextDouble());
	}
}								