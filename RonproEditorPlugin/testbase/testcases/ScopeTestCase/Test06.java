/**
* �v���O�������F
* �쐬�ҁF 
* �쐬���F Mon Oct 21 16:20:24 JST 2013
*/
public class Test06 extends Turtle {
	
	//�N������
	public static void main(String[] args) {
		Turtle.startTurtle(new Test06());
	}
	
	//�^�[�g���𓮂�������
	public void start() {
		Turtle t = new Turtle();
		if(t.getX() == 1){
			t = new Turtle();
		}
		t = t;
	}//  @(50, 50) [open]
	
}