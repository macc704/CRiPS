/*
 * NNoviceDebugger.java
 * Created on 2013/01/10 by macchan
 * Copyright(c) 2013 Yoshiaki Matsuzawa, Shizuoka Univerisy
 */
package nd.novicedebugger;

import nd.com.sun.tools.example.debug.gui.GUI;

/*
 * NoviceDebugger(��)
 * 
 * 2012/12/27 version 1.0.0 hakamata	�E�����[�X
 * 2012/12/27 version 1.0.1 hakamata	�E������͈͂Ōx�����C��
 * 2012/12/27 version 1.1.0 hakamata	�E�I������Turtle���ꏏ�ɏI������悤�ɂ���
 * 2012/12/27 version 1.2.0 hakamata	�E�\�[�X��ɂȂ����߂̓X�L�b�v(StepIntoLine�݂̂Ŏ��s�\�ɂȂ���)
 * 2013/01/06 version 1.3.0 hakamata	�Eturtle�Ƃ���ȊO�̏�����~�ʒu�����؂�ւ�
 * 										�E�\�[�X�R�[�h�ɍs�ԍ���ǉ�
 * 										�E�t�H���g��MS UI Gothic�ɕύX
 * 									 	�Eturtle������Ƃ��f�o�b�K���I������悤�ɕύX
 * 2013/01/07 version 1.4.0 hakamata	�E�X�L�b�v����Source Not Available���o�Ȃ��悤�ɏC��
 * 										�E�f�o�b�K�������W�ݒ�A
 *									  	�E����s�̏ꍇ�ɃX�L�b�v�����S�łȂ��̂��C��
 *										�ETurtle�̏ꍇ�̗�O�ɑΉ�
 * 2013/01/07 version 1.5.0 hakamata	�E�E�N���b�v�|�b�v�A�b�v���j���[�������A�ϐ��\���Ɏb��Ή�
 * 2013/01/08 version 0.1.0 matsuzawa	�ELook And Feel�̕ύX
 * 										�EUI�̏C�� Menubar�̒ǉ�, Toolbar�̈ʒu�ύX
 * 										�E�\�[�X�y�C���̃J���[�����O
 * 										�EGUI�N���X�� varTool��static�łȂ��悤�ɏC��
 * 										�܂��J���o�[�W�����Ȃ̂ŁC�o�[�W�����ԍ��߂��܂��� -> 0.1.0 
 * 2013/01/09 version 0.1.1 matsuzawa   �E�R���p�C�����x��1.7->1.5
 * 										�Etools.jar�̒���example�R�[�h�ƍ������Ă����̂ŁC�p�b�P�[�W���̕ύX�C�C��
 * 2013/01/09 version 0.1.2 matsuzawa   �EMac�ɑΉ�
 * 										�Etools.jar��Mac�̂��̂ɓ���ւ��D�iWindows�̂��̂��ƁCMac�œ������C��O��f���Ă���DMac�̂��̂�Win�ł������Ă���D�j
 * 2013/01/12 version 0.1.3 hakamata	�E���\�b�h���Ƃ̕ϐ��\�����ł���悤�ɂ���
 * 										�E�ꎟ���z���ϐ��\���ɑΉ�������
 * 										�EGUI�N���X��srcTool��private�ɂ��Astatic�łȂ��Ă������悤�ɂ���
 * 2013/01/12 version 0.1.4 matsuzawa	�E���\�b�h���Ƃ̕ϐ��\���C���t�@�N�^�����O
 * 										�E���\�b�h���Ƃ̕ϐ��\���C�o�O�C���icurrent��StackFrame�ɕϐ����Ȃ��Ƃ��ׂĕ\������Ȃ��j
 * 										�E���\�b�h���Ƃ̕ϐ��\���C�o�O�C���inull�̏ꍇ�̏����j
 * 										�E���\�b�h���Ƃ̕ϐ��\���C�\�����@�̕ύX�@�\
 * 										�E���\�b�h���Ƃ̕ϐ��\���CcurrentFrame�ȊO�̕ϐ����O���[�\��
 * 2013/01/13 version 0.1.5 hakamata	�EVariableTool�̗�O�ɂ��ďC��
 * 2013/01/15 version 0.1.6 hakamata	�E�z��̏����lnull�̏ꍇ�̏�����ǉ�
 * 										�E�X�e�b�v���s�Ԋu���X���C�h�o�[�Őݒ�ł���悤�ɂ���
 * 2013/01/17 version 0.1.7 hakamata	�E�������s���ł����s�Ԋu��ύX�ł���悤�ɂ���
 * 										�E�X���C�h�o�[���E�����ŏ��A�������ő�ɂȂ�悤�ɂ���
 * 2013/01/17 version 0.1.8 hakamata	�E�������s�c�[�����c�[���o�[�Ɉړ����A�X�e�b�v�{�^���Ɠ���
 * 										�E��~�A�Đ��A�X�e�b�v�{�^���ɕ����ł͂Ȃ��A�C�R����\��
 * 2013/01/17 verison 0.1.9 hakamata	�E�������s�c�[���̃��C�A�E�g�𒲐�
 * 										�E�X�N���[���o�[���X���C�_�[�ɕύX
 * 2013/01/22 version 0.1.10 hakamata	�E�x�����C��
 * 
 * 2013/09/26 version 0.2.0 hakamata	�EBlockEditor�ƘA�g
 * 										�E�s�ԃ��[�h
 * 										�EGUI�N���X��static������
 * 										�E�u���[�N�|�C���g�̕���
 * 										�E�I������̕ύX�ƉE�N���b�N���j���[�̎d�g�ݕύX
 * 2013/10/03 version 0.2.1 hakamata	�E����{�^����2�ɂ���
 * 										�E���x�ݒ�X���C�_��7�i�K(��)�ɂ���
 * 										�i���x�ő�����cont�Ɠ���)
 * 2013/10/10 version 0.2.2 hakamata	�E�X�e�b�v�������s���ƃu���[�N�|�C���g�Ŏ~�܂�Ȃ��̂��C��
 * 2013/10/10 version 0.2.3 hakamata	�E�ύX���ꂽ�ϐ������F�Ńn�C���C�g�����悤�ɂ���
 * 
 * 2013/10/11 version 0.2.4 hakamata	�E�u���[�N�|�C���g�C�\�����[�h�؂�ւ��Ccont�̃��O�����o��
 * 2013/10/11 version 0.2.5 hakamata	�E�s�ԍ����e�L�X�g����������ScrollPane�̃w�b�_�ɕύX
 * 										�E�W�����[�h(�s���[�h)�����܂ł̃A�C�R���ƍs�h��Ԃ��̌`�ɖ߂���
 * 2013/10/11 version 0.2.6 hakamata	�E�r���[�̔z�u��ύX(��̍��F�\�[�X�C��̉E�F�ϐ��C���F�R���\�[��)
 * 2013/10/15 version 0.2.7 hakamata	�EBreakpoint����UOFF�ɂ���
 * 										�E�ύX���������ϐ������łȂ��C�V�����ϐ������F�n�C���C�g�����悤�ɂ���
 * 2013/10/15 version 0.2.8 hakamata	�EBE����N�����̓E�B���h�E�k�����\�[�X�r���[�ŏ���
 * 
 * 2013/10/16 version 0.2.9 hakamata	�EJRE1.7.0_25��vm���N�����Ȃ��G���[�Ɏb��Ή�
 * 										�E�ϐ��������]��\���`�����j���[�ֈړ�
 *
 * 	�Eint x = 3;�@�͂n�j�����ǁC int y; �̓_���D
 * 		Java�̎d�l��,�錾�݂̂̃v���~�e�B�u�^�ϐ��̓X�^�b�N�ɐς܂�Ȃ��̂ł���Ő�����
 *�@�@�@
 * 
 * */
public class NNoviceDebugger {

	public static final String NAME = "NoviceDebugger";
	public static final String VERSION = "0.2.9";
	public static final String WINDOWTITLE = "DENO";

	public static void main(String[] args) {
		GUI gui = new GUI();
		gui.run(args);
	}

}
