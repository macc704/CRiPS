/**
* �v���O�������F
* �쐬�ҁF 
* �쐬���F Wed Oct 03 21:54:44 JST 2012
*/
public class _02TurtleTest extends Turtle {
	
	//�N������
	public static void main(String[] args) {
		Turtle.startTurtle(new _02TurtleTest());
	}
	
	
	//�^�[�g���𓮂�������
	public void start() {
		Turtle t = new Turtle();
		t.fd(100);
		t.bk(100);
		int a = t.input();
		fd(input());
		Turtle t1 = new Turtle();
		t1 = t;
	}
}						