/**
* �v���O�������F
* �쐬�ҁF 
* �쐬���F Tue Nov 06 14:51:41 JST 2012
*/
public class _03TextImageTest extends Turtle {
	
	//�N������
	public static void main(String[] args) {
		Turtle.startTurtle(new _03TextImageTest());
	}
	
	//�^�[�g���𓮂�������
	public void start() {
		TextTurtle tt = new TextTurtle("aaa");
		tt.text("����������");
		String text = tt.getText();
		ImageTurtle it = new ImageTurtle("bbb");
		it.image("aaa");
		it.size(100,300);
		TextTurtle tt2 = new TextTurtle();
		tt2 = tt;
		ImageTurtle it2 = new ImageTurtle();
		it2 = it;
	}
	
}							