/*
* �v���O�������F�J�[�h�����X�g�P���烊�X�g�Q�Ɉړ�����v���O����
* Created on 2012/01/07
* Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
*/
public class MoveCards extends Turtle {
	
	// �N������
	public static void main(String[] args) {
		Turtle.startTurtle(new MoveCards(), args);
	}
	
	// �^�[�g���𓮂�������
	public void start() {
		hide();
		Turtle.window.size(550,300);
		ListTurtle<CardTurtle> list1 = new ListTurtle<CardTurtle>(true,"���X�g1");
		ListTurtle<CardTurtle> list2 = new ListTurtle<CardTurtle>(true,"���X�g2");
		{	//�ʒu���ړ�����
			list1.warp(50,40);
			list2.warp(50,110);
		}
		update();

		{	//�J�[�h������
			int i = 0;
			while(i < 10){
				list1.addLast(new CardTurtle(i * 10));
				update();
				i++;
			}
		}

		while(true){
			sleep(0.025);
			{	//��R�}�̏���
				if(list1.getSize() != 0){//�ړ�����
					list2.addLast(list1.getObjectAtCursor());
				}
			}
			update();
		}
	}
	
}