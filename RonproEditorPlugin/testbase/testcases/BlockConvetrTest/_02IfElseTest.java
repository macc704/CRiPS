/**
* �v���O�������F
* �쐬�ҁF 
* �쐬���F Thu Nov 08 10:57:58 JST 2012
*/
public class _02IfElseTest extends Turtle {
	
	//�N������
	public static void main(String[] args) {
		Turtle.startTurtle(new _02IfElseTest());
	}
	
	//�^�[�g���𓮂�������
	public void start() {
		int x = 0;
		if(x == 3){
		}else if(x == 4){
			fd(50);
		}
		
		if(x == 3){
		}else {
			if(x == 4){
			}
			fd(50);
		}
		
	}
}