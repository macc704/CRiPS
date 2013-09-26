/*
 * 家を書くプログラム
 * (1辺の長さを入力する)
 * 2003/05/08
 * Yoshiaki Matsuzawa
 */
public class InputHouse extends Turtle {

	//起動処理
	public static void main(String[] args) {
		Turtle.startTurtle(new InputHouse());
	}

	//タートルを動かす処理
	public void start() {

		int length;//1辺の長さ
		int rightAngle;//直角
		
		length = input();//1辺の長さを入力値に設定する
		rightAngle = 90;//直角を90度に設定する
		
		//屋根を書く
		rt(30); //30度右に回る
		fd(length); //x歩前に進む
		rt(120);
		fd(length);
		rt(120);
		fd(length);

		//本体を書く
		lt(rightAngle);
		fd(length);
		lt(rightAngle);
		fd(length);
		lt(rightAngle);
		fd(length);
		lt(rightAngle);
		fd(length);
	}

}