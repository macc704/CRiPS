/*
 * JavaEditorApplication.java
 * Created on 2007/09/14 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import nd.com.sun.tools.example.debug.gui.CommandInterpreter;
import nd.com.sun.tools.example.debug.gui.GUI;
import nd.novicedebugger.NDebuggerListener;
import nd.novicedebugger.NDebuggerManager;
import pres.core.model.PRCommandLog;
import pres.core.model.PRLog;
import pres.core.model.PRTextEditLog;
import ronproeditor.bytecode.REBytecodeViewerGenerator;
import ronproeditor.dialogs.RECommentInputDialog;
import ronproeditor.dialogs.RECreateFileNameDialogForCopy;
import ronproeditor.dialogs.RECreateFileNameDialogWithType;
import ronproeditor.dialogs.RECreateNameDialog;
import ronproeditor.dialogs.RECreateProjectNameDialog;
import ronproeditor.dialogs.REDirtyOptionDialog;
import ronproeditor.dialogs.RERefactoringFileNameDialog;
import ronproeditor.dialogs.RERefactoringProjectNameDialog;
import ronproeditor.ext.REBlockEditorManager;
import ronproeditor.ext.RECheCoProManager;
import ronproeditor.ext.RECocoViewerManager;
import ronproeditor.ext.RECreateCocoDataManager;
import ronproeditor.ext.REFlowViewerManager;
import ronproeditor.ext.REGeneRefManager;
import ronproeditor.ext.REPresVisualizerManager;
import ronproeditor.helpers.FileSystemUtil;
import ronproeditor.helpers.JavaEnv;
import ronproeditor.helpers.NewZipUtil;
import ronproeditor.helpers.RECommandExecuter;
import ronproeditor.views.DummyConsole;
import ronproeditor.views.REFrame;
import ronproeditor.views.RESourceEditor;
import ch.util.CHBlockEditorController;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileElement;
import clib.common.filesystem.CFileFilter;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.CFilename;
import clib.common.filesystem.CPath;
import clib.common.system.CJavaSystem;
import clib.preference.app.CPreferenceManager;
import clib.view.dialogs.CErrorDialog;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;


/*
 * Ronpro Editor Application
 * 
 * 
 * 2007/09/21 version 1.0.0 �����[�X 
 * 2007/09/21 version 1.0.1 -cp �������Ȃ���������̂��C�� -classpath�� 
 * 2007/09/21 version 1.0.2 �R���p�C�����ɃR���\�[�����N���A����悤�ɏC��
 * 2007/09/21 version 1.0.3 Mac Runtime#exec() ��classpath��""������Ɠ����Ȃ������C�� 
 * 2007/09/21 version 1.0.4 �R���\�[����Ctrl-R�ŋN�������Ƃ��̓��͂̕s����� 
 * 2007/09/21 version 1.1.0 �R���\�[����Err��Ԃ��\���i�e�L�X�g�y�C���ɕύX�j 
 * 2007/09/22 version 1.2.0 �L�[���[�h�n�C���C�g�ǉ�, �G�f�B�^���e�L�X�g�y�C���ɕύX 
 * 2007/09/22 version 1.3.0 �t�H�[�}�b�g���^�u��̂ɐ؂�ւ� , �e�L�X�g�y�C���̃^�u4������
 * 2007/09/22 version 1.4.0 �폜�@�\��ǉ��C���t�@�N�^�����O���@�̓��ꉻ 
 * 2007/09/22 version 1.5.0 TreeViewer�ŁC���[�g��������悤�ɂ��āC�g�������ǂ����� �f�t�H���g���[�g��MyProject�ɕύX 
 * 2007/09/22 version 1.5.1 �_�C�A���O�̌����ڂ̏C��
 * 2007/09/22 version 1.5.2 �T���v���e���v���[�g���g�[ 
 * 2007/09/22 version 1.5.3 Tree��Scroll�ɂȂ��Ă��Ȃ��đ����Ȃ�Ƃ������؂�Ă��܂����̂�Scroll�o�[������ 
 * 2007/09/22 version 1.5.4 Tree�������I������Ă��Ȃ��Ƃ��ɂ�����x�I�����Ă��܂��Ɨ�O���o�Ă����̂��C�� 
 * 2007/09/22 version 1.6.0 �p�b�P�[�W�𗘗p�ł���悤�ɂ����D�iRonproEditor���g���R���p�C�������������̂Łj
 * 2007/09/22 version 1.6.1 Mac�ŃX�y�[�X���g���Ȃ�����command���s�̎d�g�݂�ς��邱�ƂŏC�� 
 * 2007/09/22 version 1.6.2 Mac��Ctrl�L�[��Apple�L�[�ɂȂ�Ȃ������C�� 
 * 2007/09/22 version 1.6.3 Mac��Ctrl-Q���I�����Ă��܂��̂ŃR���p�C���̃V���[�g�J�b�g��Ctrl-E�ɕύX 
 * 2007/10/03 version 1.6.4 Windows�ŃX�y�[�X���g���Ȃ������C���i�u"�v�����Ȃ��Ă悩�����j 
 * 2007/10/07 version 1.6.5 �e���v���[�g��CVS�i�t�H���_�j���\������Ă��܂��Ă��āC�I������ƃG���[�ɂȂ��Ă��܂��̂��C�� 
 * 2007/10/08 version 1.6.6 ���C�u������blib.jar�ɕύX�D�e���v���[�g���f�t�H���g�p�b�P�[�W�̂�Turtle.java���g���悤�ɕύX 
 * 2007/10/08 version 1.6.7 ���C�u�����̃o�[�W�����A�b�v��blib101.jar 
 * 2007/12/14 version 1.7.0 �t�@�C���R�s�[�@�\��ǉ�
 * 2007/12/14 version 1.7.1 �R���p�C���ɐ������Ȃ��ƁC���s�ł��Ȃ��悤�ɕύX�i�R���p�C�����ɃN���X�t�@�C�����폜)
 * 2007/12/14 version 1.8.0 �o�C�g�R�[�h�w�K�@�\�̒ǉ�(Beta Windows�̂�) 
 * 2007/12/14 version 1.8.1 �ۑ������Ƃ��ɁC�N���X�t�@�C�����폜����悤�Ɏd�l�ύX 
 * 2007/12/20 version 1.8.2 .jar�t�@�C��(Mac�Ŏ����I�ɍ쐬�����)��ǂ܂Ȃ��悤�ɏC��, ��U�o�C�g�R�[�h�w�K�@�\�𖳌��� 
 * 2007/12/21 version 1.8.3 �R���p�C���̃G���[�̏o�����ɂ��킹�āC�R���\�[���̃^�u�̃T�C�Y�𒲐��i�R���p�C���G���[�̈ʒu������Ȃ��悤�ɂ����j 
 * 2007/12/21 version 1.8.4 ���C�u�����̃o�[�W�����A�b�v��blib105.jar, japa.jar(version1)�ɕύX�C�o�C�g�R�[�h�w�K�@�\�L����
 * 2007/12/26 version 1.8.5 �R�}���h���s���ɂ��̃R�}���h���Ȃ��ƃG���[���o�͂���悤�ɏC���ijavac�̃p�X���ʂ��ĂȂ��ꍇ�ւ̑Ώ��j 
 * 2008/11/11 version 1.8.6 �t�@�C���ǂݍ��݂ŉ�����ꍇ������C�ǂݍ��ݕ��@��JISAutoDetect�ɕύX 
 * 2008/11/17 version 1.8.7 �t�@�C���ǂݍ��݂ŉ�����ꍇ������C�������ݕ��@��SJIS�ɕύX 
 * 2009/11/17 version 1.8.8 �R���p�C�����ƂɃt�@�C����log�t�H���_�ɕۑ�����@�\�Alog�t�H���_��zip�ł܂Ƃ߂�@�\��ǉ��iby turkey�j 
 * 2010/01/06 version 1.8.9 MacOS10.5���ŃR���p�C�����s����̂ŁAjavac�I�v�V������ -encoding SJIS ��ǉ��iby turkey�j
 * 2010/01/07 version ���� �R���\�[���œ��{����͂���ƁA������t�H�[�J�X�������Ȃ��Ȃ�o�O���C���iby turkey�j
 * 2010/11/05 version 1.9.1 PRES�@�\�ǉ�
 * 2010/11/05 version 1.9.2 .����n�܂�e���v���[�g��ǂݍ��܂Ȃ��悤�ɂ���
 * 2011/09/29 version 2.0.0 �É���w���w���Ή��Łi�\�[�Xwarning�C���̂݁j 
 * 2011/10/10 version 2.1.0 BlockEditor��g�ݍ���
 * 2011/10/10 version 2.1.1 bat�t�@�C���𓯍��i�Ñ���ƂŁC�N���ł��Ȃ��w�����������߁j
 * 2011/10/15 version 2.1.2 �v���W�F�N�g�z���̃t�@�C���R�s�[�@�\�ǉ�
 * 2011/10/20 version 2.1.3 �X�}�[�g�u���[�X�@�\���폜
 * 							�X�}�[�g�C���f���g�@�\��ǉ�
 * 							Format�A���S���Y���̕ύX�i}}��}\n}�ɂ���j
 * 2011/10/22 version 2.1.4 ���f�B�A�t�@�C�����c���[�r���[�A���猩����悤�ɂ���i�����͓K���Ȃ̂Ō�ŏC�����ځj
 * 2011/10/22 version 2.1.5 blib��120�֓���
 * 2011/10/23 version 2.1.6 Format�A���S���Y���̕ύX�i{{��{\n{�ɂ���j
 * 							�X�}�[�g�C���f���g�A���S���Y����ύX���A{�͂��������Ă��C���f���g�i�߂͂P�܂ŁC}�̓C���f���g�߂������D
 * 
 * 2011/12/18 version 2.1.7 �EBlockEditor���o�[�W�����A�b�v�i�ۈ�j
 * 							�Ekana/FlowViewer�쐬�J�n
 * 							�Eblib��128�֓���
 * 2012/01/24 version 2.1.8 �E.csv�t�@�C�������悤�ɂ���D
 * 2012/02/04 version 2.1.9 �EFlowViewer�̎����X�V
 * 2012/09/27 version 2.2.0 �EBlockEditor�̃o�[�W�����A�b�v
 * 							�EGeneRef�V�X�e���g��
 * 							�EPreference
 * 2012/09/27 version 2.2.1 �EPreference�̃o�O�C��
 * 2012/09/28 version 2.2.2 �Eblib��123->129
 * 							�Eupdater��g��
 * 2012/10/02 version 2.2.3 sakakibara
 * 							�EGeneRef���o�[�W�����A�b�v 
 * 							�Ecompile��run���ɃR�}���h���s���Ԃ�\��������悤�ύX
 * 2012/10/03 version 2.2.4 matsuzawa	�EBlockEditor2.1.0
 * 							�EBlockEditor�̕����R�[�h�������� 
 * 2012/10/03 version 2.2.5 matsuzawa	�Eupdater.jar�̃o�[�W������1.1.0��
 * 2012/10/03 version 2.2.6 matsuzawa	�EBlockEditor2.1.1
 * 2012/10/03 version 2.2.7 matsuzawa	�EBlockEditor2.1.3
 * 2012/10/03 version 2.2.8 matsuzawa	�EBlockEditor2.1.4
 * 2012/10/03 version 2.2.9 matsuzawa	�EBlockEditor2.1.5
 * 2012/10/03 version 2.2.10 sakakibara	�EGeneRef1.0.4
 * 2012/10/03 version 2.2.11 matsuzawa	�EBlockEditor2.1.6
 * 2012/10/04 version 2.2.12 sakakibara	�EGeneRef1.0.5
 * 2012/10/04 version 2.2.13 sakakibara	�EGeneRef1.0.6
 * 							�EPreference��cancel���Ă���Ԃ��c���Ă��܂��s����C��
 * 2012/10/09 version 2.2.14 matsuzawa	�EBlockEditor2.1.9
 * 							�E���M���O����̏C���iSourceEditor�n�C���삵�Ă��Ȃ������j
 * 2012/10/10 version 2.2.15 matsuzawa
 * 							�ESourceEditor, SourceViewer�̕��� ���t�@�N�^�����O
 * 2012/10/10 version 2.2.16 matsuzawa	�EBlockEditor2.1.10
 * 2012/10/10 version 2.3.0 matsuzawa	�E���M���O�̒ǉ��CFORMAT_START, END, BLOCK_LOADING_START, END
 * 2012/10/10 version 2.3.1 matsuzawa	�EBlockEditor2.1.11
 * 2012/10/14 version 2.3.2 sakakibara	�EGeneRef1.0.7
 * 2012/10/14 version 2.3.3 sakakibara	�EGeneRef1.0.8
 * 2012/10/15 version 2.3.4 sakakibara	�EGeneRef1.0.9
 * 2012/10/16 version 2.4.0 matsuzawa �EBlockEditor 2.1.12
 * 									�EFont�̕ύX���o����D
 * 2012/10/16 version 2.4.1 matsuzawa Font�̕ύX, �o�O�̏C��
 * 2012/10/18 version 2.4.2 matsuzawa �EBlockEditor 2.2.0 �d��ȃo�O�C��
 * 									�EPreference Window�^�C�g��
 * 									�EJava Information�_�C�A���O
 * 2012/10/19 version 2.4.3 sakakibara	�EGeneRef1.0.10
 * 2012/10/18 version 2.4.4 matsuzawa �EBlockEditor 2.3.0 SS
 * 2012/10/18 version 2.4.5 matsuzawa �EBlockEditor 2.3.1
 * 2012/10/18 version 2.4.6 matsuzawa �EBlockEditor 2.3.2
 * 2012/10/18 version 2.4.7 matsuzawa �EFlowViewer SS
 * 2012/10/18 version 2.5.0 matsuzawa �E�R�����g�@�\�ǉ�
 * 2012/10/22 version 2.5.1 sakakibara	�EBE���J������Ԃ���GeneRef���\������Ȃ��o�O���C��
 * 									�EBE���J�������A�J������ԂŃt�@�C����ύX�������A�Z�[�u���������̃R���p�C��������\���ɕύX
 * 2012/10/23 version 2.5.2 matsuzawa�@�EBE 2.5.0 -SS, BE��dirty��Ԃ𔽉f�C��BE�o�O�C��		
 * 2012/10/24 version 2.5.3 sakakibara  �E�R���p�C��������ʏ��BE�CGeneRef�p�ɕ���
 * 										�EGeneRef 1.1.0
 * 2012/10/29 version 2.6.1 matsuzawa  �EBE 2.6.1
 * 2012/10/30 version 2.6.2 matsuzawa  �EBE 2.6.2
 *							sakakibara	�EGeneRef1.1.1
 * 2012/11/1 version 2.7.0  matsuzawa  �EBE 2.7.0
 * 2012/11/4 version 2.8.0  matsuzawa  �EBE 2.8.0
 * 2012/11/6 version 2.9.0  matsuzawa  �EBE 2.9.0
 * 2012/11/6 version 2.9.1  matsuzawa  �EBE 2.9.1
 * 										�Eblib1.5.13 (SoundTurtle�̎d�l�ύX)
 * 2012/11/6 version 2.9.2  matsuzawa  �EBE 2.9.2
 * 2012/11/7 version 2.9.4  matsuzawa  �EBE 2.9.4
 * 										�Eblib1.5.14 (setShow()�̒ǉ��Ȃ�)
 * 2012/11/7 version 2.9.5  matsuzawa  �EBE 2.9.5
 * 2012/11/7 version 2.9.6  matsuzawa  �EBE 2.9.6
 * 2012/11/8 version 2.9.7  matsuzawa  �EBE 2.9.7
 * 2012/11/13 version 2.10.0  sakaki �EGeneRef 1.1.2 �i�d��ȃo�O�C���j
 * 							matsuzawa  �EBE 2.10.0
 * 2012/11/13 version 2.10.2  matsuzawa  �EBE 2.10.2
 * 2012/11/14 version 2.10.3  matsuzawa  �EBE 2.10.3
 * 2012/11/14 version 2.10.4  matsuzawa  �EFormat����Undo���܂Ƃ߂Ăł��Ȃ������C��
 * 2012/11/14 version 2.10.5  matsuzawa	�EBE 2.10.5 
 * 										�ECUI, Turtle�Ń��j���[���؂�ւ��悤�ɐݒ�
 * 2012/11/14 version 2.10.7  matsuzawa	�EBE 2.10.7 
 * 2012/11/14 version 2.10.9  matsuzawa	�EBE 2.10.9 
 * 2012/11/14 version 2.10.10  matsuzawa	�EBE 2.10.10
 * 2012/11/15 version 2.10.11  matsuzawa	�EBE 2.10.11 ���ƒ�
 * 2012/11/15 version 2.10.12  matsuzawa	�EBE 2.10.12 ���ƒ�
 * 2012/11/15 version 2.10.13  matsuzawa	�EBE 2.10.13 ���ƒ�
 * 2012/11/23 version 2.10.14  matsuzawa	�EBE 2.10.14
 * 											�EJava�t�@�C������v���W�F�N�g��I�������FocusLost�̃��O���������Ƃ�Ȃ��o�O���C��
 * 2012/11/23 version 2.10.16  matsuzawa	�EBE 2.10.16
 * 2012/11/24 version 2.11.1  matsuzawa		�EBE 2.11.1 �֐�
 * 											�EJava->Block���̎��s�X���b�h��ThreadManager�����ɕύX
 * 2012/11/24 version 2.11.2  matsuzawa		�EBE 2.11.2 �֐�
 * 2012/11/24 version 2.11.5  matsuzawa		�EBE 2.11.5 �֐�
 * 2012/11/24 version 2.11.6  matsuzawa		�EBE 2.11.6 �֐� �d�v�ȃo�O�C��
 * 2012/11/24 version 2.11.7  matsuzawa		�EBE 2.11.7 FlowViewer�ɑΉ��D��������悤��A�e�X�g�Ńo�O���
 * 2012/11/25 version 2.11.8  matsuzawa		�EBE 2.11.8
 * 2012/11/29 version 2.11.9  sakakibara	�EGeneRef 1.1.4
 * 2012/12/03 version 2.11.10 sakakibara	�E�Â�JDK��BE�������Ȃ��o�O���C��
 * 2012/12/04 version 2.11.11 matsuzawa		�EBlock�̈����̕������C��
 * 											�EdoCompile2 RSErrorMessage ���߂�l�ɂȂ��Ă����̂��C��
 * 2012/12/27 version 2.12.1 hakamata		�ENoviceDebugger(��)�ǉ��@v1.0.0
 * 2012/12/27 version 2.12.2 hakamata		�ENoviceDebugger(��) v1.1.0
 * 2012/12/27 version 2.12.3 hakamata		�ENoviceDebugger(��) v1.2.0
 * 2013/01/06 version 2.12.4 hakamata		�ENoviceDebugger(��) v1.3.0, debugger�����R���p�C�����͎��s�ł��Ȃ��悤�ɏC��
 * 2013/01/07 version 2.12.5 hakamata		�ENoviceDebugger(��) v1.5.0
 * 2013/01/08 version 2.13.0 matsuzawa		�ENoviceDebugger�𐳎������[�X v 0.1.0
 * 2013/01/09 version 2.13.1 matsuzawa		�EBlockEditor 2.11.9
 * 2013/01/09 version 2.14.0 matsuzawa		�EBlockEditor 2.12.0
 * 2013/01/09 version 2.15.0 matsuzawa		�EBlockEditor 2.13.0
 * 											blib1514.jar -> blib1524.jar
 * 2013/01/09 version 2.15.1 matsuzawa		�EBlockEditor 2.13.1
 * 2013/01/09 version 2.15.2 matsuzawa		�EBlockEditor warpByTopLeft(), bgColor()�^�C���ǉ�
 * 											�EBlockEditor DebugRun�{�^���ǉ�
 * 											�ENoviceDebugger ���O�L�^
 * 2013/01/09 version 2.15.3 matsuzawa		�EJava1.7���K�v�������̂�1.5�ȏ�ɓ��ꂵ���D
 * 2013/01/09 version 2.15.4 matsuzawa		�ETools��NoviceDebugger���オ���Ă��܂����Ƃ�����̂��C���D
 * 2013/01/09 version 2.15.5 matsuzawa		�ENoviceDebugger0.1.1 �p�b�P�[�W���̕ύX�D
 * 2013/01/10 version 2.15.6 matsuzawa		�ENoviceDebugger0.1.2 Mac�ɑΉ��i�p�X�̐ݒ肪Windows�ˑ��������j
 * 2013/01/13 version 2.16.0 matsuzawa		�ENoviceDebugger0.1.4
 * 											�EClassFileAnalyzer�𗘗p���ăo�C�g�R�[�h�@�\�𕜊�
 * 2013/01/15 version 2.16.1 matsuzawa		�Eblib1524.jar->blib1525.jar
 * 											�EButtonTurtle, InputTurtle�̃u���b�N�ɑΉ�
 * 2013/01/15 version 2.16.2 matsuzawa		�EListTurtle#setCursor()�̃u���b�N�s��ɑΉ�
 * 2013/01/22 version 2.16.3 matsuzawa		�ENovieDebugger�̃o�[�W�����A�b�v
 * 2013/01/23 version 2.16.4 matsuzawa		�ENovieDebugger�̃��O��ǉ��D
 * 2013/03/14 version 2.16.5 matsuzawa		�EFont�̑傫���ύX�@�\
 * 2013/04/13 version 2.16.6 matsuzawa		�Emac snow leopard�ł�javac�����������������ijavac�̃I�v�V�����ŉ����j
 * 											�EMac�Ńf�t�H���g��Font��Osaka�ɂ���D
 * 2013/04/13 version 2.16.7 matsuzawa		�Emac snow leopard�ł�javac�������������������Ă��Ȃ�������������
 * 											�ijar�t�@�C���N���b�N�ŋN�����Cfile.encoding��US-ASCII�ɂȂ��Ă��܂��̂������ɏ㏑�j
 * 2013/04/13 version 2.16.8 matsuzawa		�E16.7�ł��܂������Ă��Ȃ������̂ŁC����  
 * 2013/04/18 version 2.16.9 matsuzawa		�Emac�ł̃f�o�b�K�̕��������Dfile.encoding���N�����Ă��炩����͖̂��Ӗ��D
 * 											.command�N���t�@�C�������ɂ�荪�{�I�ȉ�����}��D  
 * 
 * 2013/09/26 version 2.17.0 hakamata		�EDENO version0.2.0�Ɠ���
 * 
 * 2013/09/26 version 2.18.0 ohata			�EBE version 2.14.0�ƌ���
 * 
 * 2013/10/11 version 2.18.1 hakamata		�EDENO version0.2.4�Ɠ���
 * 											�EDENO��Breakpoint, ���s�ʒu�\�����[�h�̐؂�ւ�, cont, Focus�̃��O�����o�� 
 * 2013/10/16 version 2.19.0 matsuzawa		�E��L�V�o�[�W�����𓝍�����week3�p�o�[�W�����D
 * 2013/10/22 version 2.19.1 matsuzawa		�E�t�@�C���R�s�[�̕s��C��
 * 											�EBE �X�R�[�v����@�\
 * 											�EDENO Block�G�f�B�^�ł��폜
 * 2013/10/30 version 2.20.0 matsuzawa		�EPPV�g�ݍ���
 * 											�EBE �n�C���C�gbugfix
 * 											�E�s�ԍ���font���Ǐ]���Ȃ������C��
 * 											�E�S�p�́u{�v��ϊ����悤�Ƃ���ƃ\�[�X�S�̂����������Ȃ�����C��
 * 											�E���p��{����͂����Ƃ���}���������͂���@�\���폜
 * 2013/12/4 version 2.21.0 matsuzawa		�E�ڍׂ�git���O���Q�Ƃ̂���
 * 												�E������bugfix
 * 												�EBlockEditor������
 * 												�E�S�p�X�y�[�X�\���Ȃ�
 * 2013/12/15 version 2.22.0 matsuzawa		�Eblib�̍X�V�@blib.jar 1.5.26
 * 											�EDebugger��update()�����f�����̂��x����������
 * 												�Eblib�̍X�V waitrepaint���[�h
 * 												�Ewaitrepaint����
 * 												�ETurtle�e���v���ύX args�������Ƃ���
 * 2013/12/17 version 2.23.0 matsuzawa		�Egit�Q�Ƃ̂���
 * 2013/12/17 version 2.23.1 matsuzawa		�Egit�Q�Ƃ̂��� 19���o�[�W����
 * 2013/12/19 version 2.24.0 matsuzawa		�Egit�Q�Ƃ̂��� Cocoviewer�����߂�
 * 2014/01/08 version 2.25.0 matsuzawa		�Egit�Q�Ƃ̂���
 * 
 * 2014/10/01 version 2.27.0 ohata			�E2014�v���O���~���O�Љ�w�ȗp
 * 2014/10/11 version 2.27.1 ohata			�E�y���ȃo�O���C��
 * 
<<<<<<< HEAD
 * 2014/10/01 version 2.27.2 ohata			�E�R���\�[���̃t�H���g���G�f�B�^�̃t�H���g�Ɠ���
=======
 * 2014/10/18 version 2.27.2 ohata			�E�R���\�[���̃t�H���g���G�f�B�^�̃t�H���g�Ɠ���
>>>>>>> ronpro_plugin_master
 * 											�E�t�H���g�̕������ɂ��G���[�w�E���b�Z�[�W�̃Y�����C��
 * 2014/10/24 version 2.27.3 ohata			�Esize���\�b�h��Block>>Java�ϊ��̃G���[���C��
 * 											�ETurtle���p����������N���X�u���b�N���E�N���b�N�����Ƃ��̃R���e�L�X�g���j���[�ɁC�^�[�g�����j���[��ǉ�
 * 											�EList,Image,TextTurtle�Ȃǂ̃��\�b�h�Ăяo���u���b�N���B��
 * 2014/10/24 version 2.27.4 ohata			�E�R���e�L�X�g���j���[�ύX
 * 2014/10/24 version 2.27.5 ohata			�E���\�b�h�R�[�����̕`�揈�����C��
 * 											�EBlock>>Java�̃G���[���C�� 
 * 2014/10/24 version 2.27.6 ohata			�E���\�b�h�R�[�����̏C��,�e�X�g
 * 											�E�Q�ƃu���b�N�̃n�C���C�g�������C��
 * 2014/10/24 version 2.28.0 ohata			�E���\�b�h�R�[�����̃����[�X
 * 2014/10/24 version 2.28.1 ohata			�E���\�b�h�R�[�����̏C��
 * 2014/10/24 version 2.28.2 ohata			�E�ċA�Ή����ꎞ��~
 * 2014/10/24 version 2.28.3 ohata			�EBE�̍ċA�o�O���C���C���̑����\�b�h��`�̃o�O���C�� 
 * 2015/01/14 version 2.29.0 kato           �ECheCoPro�����[�X
 * 2015/01/14 version 2.29.1 kato           �ECheCoPro pull���O�C��
 * �����Ď�����
 * �EdoCompile2()�̐݌v���璷�Ȃ̂ōĐ݌v���邱�ƁD
 * �E"}"���������Ƃ��̃X�}�[�g�C���f���g
 * �Emanager�Q��initialize�̈ʒu�C���ꑋ�C�����邱��
 *�@�E�t�H���_���N���b�N�����Ƃ��C���L�̃G���[���łĂ���D
 *at ronproeditor.REApplication.writePresLog(REApplication.java:647)
 *at ronproeditor.views.RESourceEditor$2.focusLost(RESourceEditor.java:93)
 */
public class REApplication implements ICFwApplication {

	/***********************
	 * Static Variables.
	 ***********************/

	// Application's Information.
	public static final String APP_NAME = "Ronpro Editor";
	public static final String VERSION = "2.29.1";
	public static final String BUILD_DATE = "2014/11/10";
	public static final String DEVELOPERS = "Yoshiaki Matsuzawa & CreW Project & Sakai Lab";
	public static final String COPYRIGHT = "Copyright(c) 2007-2014 Yoshiaki Matsuzawa & CreW Project & Sakai Lab. All Rights Reserved.";

	public static final String SRC_ENCODING = "SJIS";
	// public static final String SRC_ENCODING = "UTF-8"; // for test
	public static final boolean COMMENT = true;
	public static final String COMMENT_FILE = ".comment.txt";

	public static final String FILE_EXTENSION = "java";
	public static final String RUNNABLE_EXTENSION = "class";
	public static final String DEFAULT_ROOT = "MyProjects";
	public static final String TEMPLATE_FOLDER = "templates";
	public static final String LIB_FOLDER = "lib";
	public static final String EXTENSION_FOLDER = "ext";
	public static final String TRASH_FOLDER = ".Trash";

	public static final int WHITESPACE_COUNT_FOR_TAB = 2;

	/***********************
	 * Main Method
	 ***********************/

	public static void main(String args[]) {
		REApplication application = new REApplication();
		application.main();
	}

	/***********************
	 * Variables.
	 ***********************/

	private String compileCommand;
	private String runCommand;

	private RESourceManager sourceManager = new RESourceManager();
	private RELibraryManager libraryManager = new RELibraryManager(LIB_FOLDER);
	private RESourceTemplateManager templateManager = new RESourceTemplateManager(
			TEMPLATE_FOLDER);
	private CPreferenceManager preferenceManager;

	private REFrame frame;

	private RECreateFileNameDialogWithType createFileDialog;
	private RECreateProjectNameDialog createProjectDialog;
	private REDirtyOptionDialog dirtyOptionDialog;
	private RERefactoringProjectNameDialog refactorProjectNameDialog;
	private RERefactoringFileNameDialog refactorFileNameDialog;
	private RECreateFileNameDialogForCopy copyFileNameDialog;

	private PresProjectManager presManager;
	private REBlockEditorManager blockManager;
	private REFlowViewerManager flowManager;
	private REGeneRefManager generefManager;
	private REPresVisualizerManager ppvManager;
	private RECheCoProManager checoproManager; // CheCoPro(kato)
	private GUI deno;
	private RECreateCocoDataManager createCocoDataManager;
	private RECocoViewerManager cocoViewerManager;

	/***********************
	 * Construct & Start
	 ***********************/

	private void main() {
		initializeLookAndFeel();
		initializeCommands();
		initializeAndOpen(DEFAULT_ROOT);
	}

	private void initializeAndOpen(String rootDirName) {
		prepareRootDirectory(rootDirName);
		createAndOpenWindow();
		prepareDialogs();

		presManager = new PresProjectManager();
		presManager.initialize();
		blockManager = new REBlockEditorManager(this);
		flowManager = new REFlowViewerManager(this);
		generefManager = new REGeneRefManager(this);
		ppvManager = new REPresVisualizerManager(this);
		createCocoDataManager = new RECreateCocoDataManager(this);
		cocoViewerManager = new RECocoViewerManager(this);
		checoproManager = new RECheCoProManager(this);

		this.sourceManager.setFileFilter(CFileFilter.ACCEPT_BY_NAME_FILTER(
				"*.java", "*.hcp", "*.c", "*.cpp", "Makefile", "*.oil", "*.rb",
				"*.bat", "*.tex", "*.jpg", "*.gif", "*.png", "*.wav", "*.mp3",
				"*.csv"));
		// this.sourceManager.setDirFilter(CFileFilter.IGNORE_BY_NAME_FILTER(".*",
		// "CVS", "bin"));
		// @TODO ������Ǝ������邱�� 2011/11/22
	}

	private void initializeLookAndFeel() {
		if (CJavaSystem.getInstance().isWindows()) {
			try {
				UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void initializeCommands() {
		if (CJavaSystem.getInstance().hasCommand("java")) {
			this.runCommand = "java";
		} else {
			JOptionPane.showMessageDialog(frame, "java�R�}���h��������܂���",
					"�N�����`�F�b�N�ɂЂ�������܂���", JOptionPane.ERROR_MESSAGE);
			// System.exit(0);
		}

		this.compileCommand = CJavaSystem.getInstance().getJavacCommand();
		if (this.compileCommand == null) {
			JOptionPane.showMessageDialog(frame, "javac�R�}���h��������܂���",
					"�N�����`�F�b�N�Ɉ���������܂���", JOptionPane.ERROR_MESSAGE);
			// System.exit(0);
		}
	}

	private void prepareDialogs() {
		createProjectDialog = new RECreateProjectNameDialog(this);
		createFileDialog = new RECreateFileNameDialogWithType(this);
		dirtyOptionDialog = new REDirtyOptionDialog(this);
		refactorProjectNameDialog = new RERefactoringProjectNameDialog(this);
		refactorFileNameDialog = new RERefactoringFileNameDialog(this);
		copyFileNameDialog = new RECreateFileNameDialogForCopy(this);
		copyFileNameDialog.setTitle("�t�@�C���i�N���X�j�̃R�s�[");
	}

	private void prepareRootDirectory(String rootDirName) {
		File root = new File(rootDirName);
		if (!root.exists()) {
			root.mkdir();
		}
		sourceManager.setRootDirectory(root);

		sourceManager.setFileFilter(CFileFilter
				.ACCEPT_BY_EXTENSION_FILTER("java"));
		sourceManager.setDirFilter(CFileFilter.IGNORE_BY_NAME_FILTER(".*"));

		CFile preferenceFile = CFileSystem.findDirectory(DEFAULT_ROOT)
				.findOrCreateFile(".pref/preference");
		preferenceManager = new CPreferenceManager(preferenceFile);
	}

	private void createAndOpenWindow() {
		frame = new REFrame(this);
		frame.initialize();
		frame.setVisible(true);
	}

	/***********************
	 * Getters
	 ***********************/

	public RESourceManager getSourceManager() {
		return sourceManager;
	}

	public RESourceTemplateManager getTemplateManager() {
		return templateManager;
	}

	public REFrame getFrame() {
		return frame;
	}

	public RELibraryManager getLibraryManager() {
		return libraryManager;
	}

	// public PresProjectManager getPresManager() {
	// return presManager;
	// }

	public CPreferenceManager getPreferenceManager() {
		return preferenceManager;
	}

	/***********************
	 * Application Method
	 ***********************/

	public void doCreateProject() {
		doClose();

		createProjectDialog.open();
		if (createProjectDialog.getState() == RECreateNameDialog.State.INPUTTED) {
			getSourceManager().createProject(
					createProjectDialog.getInputtedName());
		}
	}

	public void doCreateFile() {
		doClose();

		if (getSourceManager().getProjectDirectory() == null) {
			JOptionPane.showMessageDialog(frame, "�v���W�F�N�g���I������Ă��܂���",
					"�t�@�C���i�N���X�j�����܂���", JOptionPane.ERROR_MESSAGE);
			return;
		}

		createFileDialog.open();
		if (createFileDialog.getState() == RECreateNameDialog.State.INPUTTED) {
			getSourceManager().createFile(createFileDialog.getInputtedName(),
					createFileDialog.getSelectedTemplate());
		}
	}

	public void doOpen(File file) {
		doClose();

		if (file.getName().endsWith("java")) {// @TODO ������Ǝ������邱�� 2011/11/22
			getSourceManager().open(file);
		}
	}

	public void doSave() {
		if (getSourceManager().hasCurrentFile()) {
			getFrame().getEditor().doSave();

			blockManager.doCompileBlock(); // �v�F�t�@�C���폜�̑O�Ɏ��s

			deleteRunnable(getSourceManager().getCurrentFile());

			// TODO
			writePresLog(PRCommandLog.SubType.SAVE);// TODO

			// blockManager.doRefleshBlock();
			flowManager.refreshChart();

		}
	}

	public void doBlockToJavaSave() {
		if (getSourceManager().hasCurrentFile()) {
			getFrame().getEditor().doSave();
			deleteRunnable(getSourceManager().getCurrentFile());

			// TODO
			writePresLog(PRCommandLog.SubType.SAVE);// TODO

			// TODO ��Əd��
			flowManager.refreshChart();
			
			checoproManager.sendText();
			checoproManager.sendFiles();
		}
	}

	public void doRefactoring() {
		if (getSourceManager().hasCurrentFile()) {
			doRefactorFileName();
		} else {
			doRefactorProjectName();
		}
	}

	private void doRefactorProjectName() {
		if (getSourceManager().getProjectDirectory() == null) {
			JOptionPane.showMessageDialog(frame, "�v���W�F�N�g���I������Ă��܂���",
					"�v���W�F�N�g����ύX�ł��܂���", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (getSourceManager().hasCurrentFile()
				&& getFrame().getEditor().isDirty()) {
			JOptionPane.showMessageDialog(frame, "�\�[�X���Z�[�u����Ă��܂���",
					"�v���W�F�N�g����ύX�ł��܂���", JOptionPane.ERROR_MESSAGE);
			return;
		}

		doClose();

		refactorProjectNameDialog
				.open(getSourceManager().getProjectDirectory());
		if (refactorProjectNameDialog.getState() == RECreateNameDialog.State.INPUTTED) {
			getSourceManager().refactorProjectName(
					refactorProjectNameDialog.getInputtedName());
		}
	}

	private void doRefactorFileName() {
		if (!getSourceManager().hasCurrentFile()) {
			JOptionPane.showMessageDialog(frame, "�\�[�X���I������Ă��܂���",
					"�t�@�C������ύX�ł��܂���", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (getSourceManager().hasCurrentFile()
				&& getFrame().getEditor().isDirty()) {
			JOptionPane.showMessageDialog(frame, "�\�[�X���Z�[�u����Ă��܂���",
					"�t�@�C������ύX�ł��܂���", JOptionPane.ERROR_MESSAGE);
			return;
		}

		File file = getSourceManager().getCurrentFile();

		doClose();

		refactorFileNameDialog.open(file);
		if (refactorFileNameDialog.getState() == RECreateNameDialog.State.INPUTTED) {
			getSourceManager().refactorFileName(file,
					refactorFileNameDialog.getInputtedName());
		}
	}

	public void doFileCopy() {
		if (!getSourceManager().hasCurrentFile()) {
			JOptionPane.showMessageDialog(frame, "�\�[�X���I������Ă��܂���",
					"�t�@�C�����R�s�[�ł��܂���", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (getSourceManager().hasCurrentFile()
				&& getFrame().getEditor().isDirty()) {
			JOptionPane.showMessageDialog(frame, "�\�[�X���Z�[�u����Ă��܂���",
					"�t�@�C�����R�s�[�ł��܂���", JOptionPane.ERROR_MESSAGE);
			return;
		}

		File file = getSourceManager().getCurrentFile();

		doClose();

		File recommendedFile = new File(file.getParentFile(), "CopyOf"
				+ FileSystemUtil.cutExtension(file));

		copyFileNameDialog.open(recommendedFile);
		if (copyFileNameDialog.getState() == RECreateNameDialog.State.INPUTTED) {
			getSourceManager().copyFile(file,
					copyFileNameDialog.getInputtedProject(),
					copyFileNameDialog.getInputtedName());
		}
	}

	public void doDelete() {
		if (getSourceManager().hasCurrentFile()) {
			doDeleteFile();
		} else {
			doDeleteProject();
		}
	}

	private void doDeleteProject() {
		if (getSourceManager().getProjectDirectory() == null) {
			JOptionPane.showMessageDialog(frame, "�v���W�F�N�g���I������Ă��܂���",
					"�v���W�F�N�g���폜�ł��܂���", JOptionPane.ERROR_MESSAGE);
			return;
		}

		int res = JOptionPane.showConfirmDialog(frame, "�{����"
				+ getSourceManager().getProjectDirectory().getName()
				+ "���폜���Ă悢�ł����H �ȉ��̃t�@�C�������ׂč폜����܂�", "�ŏI�m�F",
				JOptionPane.WARNING_MESSAGE);
		if (res == JOptionPane.OK_OPTION) {
			doClose();
			File file = getSourceManager().getProjectDirectory();
			file.renameTo(new File(getSourceManager().makeTrashFolder(), file
					.getName()));
			getSourceManager().fireRefreshedEvent();
		}
	}

	private void doDeleteFile() {
		if (!getSourceManager().hasCurrentFile()) {
			JOptionPane.showMessageDialog(frame, "�\�[�X���I������Ă��܂���",
					"�t�@�C�����폜�ł��܂���", JOptionPane.ERROR_MESSAGE);
			return;
		}

		File file = getSourceManager().getCurrentFile();

		int res = JOptionPane.showConfirmDialog(frame, "�{����" + file.getName()
				+ "���폜���Ă悢�ł����H", "�ŏI�m�F", JOptionPane.WARNING_MESSAGE);
		if (res == JOptionPane.OK_OPTION) {
			doClose();
			file.renameTo(new File(getSourceManager().makeTrashFolder(), file
					.getName()));
			getSourceManager().fireRefreshedEvent();
		}
	}

	public void doClose() {
		if (getSourceManager().hasCurrentFile()) {
			dirtyCheck();
			getSourceManager().close();
		}
	}

	private void dirtyCheck() {
		if (getSourceManager().hasCurrentFile()
				&& getFrame().getEditor().isDirty()) {
			dirtyOptionDialog.open();
		}
	}

	public void doSetProjectDirectory(File file) {
		doClose();

		getSourceManager().setProjectDirectory(file);
	}

	public void doRefresh() {
		doClose();
		getSourceManager().fireRefreshedEvent();
	}

	public void doRefreshCurrentEditor() {
		RESourceEditor editor = frame.getEditor();
		if (editor != null) {
			editor.refresh();
		}
	}

	public void doExit() {
		try {
			dirtyCheck();
			presManager.terminate();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	// public void doMakeLogArchive() {
	// try {
	// String zipName = "log_"
	// + ronproeditor.helpers.TimeUtil.getDateString() + ".zip";
	// ronproeditor.helpers.ZipUtil.createZip(zipName, new String[] {
	// "log", "MyProjects" });
	// } catch (IOException e) {
	// e.printStackTrace(frame.getConsole().err);
	// frame.getConsole().setText(
	// frame.getConsole().getText() + "�\���󂠂�܂��񂪃G���[���������܂����B\n");
	// frame.getConsole().setText(
	// frame.getConsole().getText() + "�J���҂܂Ō�A���������B\n");
	// }
	// }

	// turkey�̃R�[�h
	// public void doSaveCompileLog() {
	//
	// File logRoot = new File("log");
	// if (logRoot.exists() == false)
	// logRoot.mkdir();
	//
	// File project = new File(logRoot, sourceManager.getProjectDirectory()
	// .getName());
	// if (project.exists() == false)
	// project.mkdir();
	//
	// String name = getSourceManager().getCurrentFile().getName();
	// File file = new File(project, name + "."
	// + ronproeditor.helpers.TimeUtil.getNowString());
	// FileSystemUtil.save(file, getFrame().getEditor().getText());
	//
	// }

	public void doCompile() {
		doCompileNonBlocking();
	}

	// public void doCompileBlocking() {
	// doCompile(true);
	// }

	public void doCompileNonBlocking() {
		doCompile(false);
	}

	/**
	 * �R���p�C�������܂�
	 * 
	 * @param blocking
	 *            �R���p�C�����u���b�L���O���邩
	 */
	private void doCompile(boolean blocking) {
		// doSaveCompileLog(); turkey�̃R�[�h

		if (getSourceManager().hasCurrentFile()
				&& getFrame().getEditor().isDirty()) {
			JOptionPane.showMessageDialog(frame, "�\�[�X���Z�[�u����Ă��܂���",
					"�R���p�C���ł��܂���", JOptionPane.ERROR_MESSAGE);
			return;
		}

		File target = getSourceManager().getCurrentFile();
		if (hasRunnableFile(target)) {
			deleteRunnable(target);
		}

		frame.getConsole().setText("");

		JavaEnv env = FileSystemUtil.createJavaEnv(getSourceManager()
				.getRootDirectory(), getSourceManager().getCurrentFile());
		String cp = libraryManager.getLibString();

		ArrayList<String> commands = new ArrayList<String>();
		commands.add(compileCommand);
		if (CJavaSystem.getInstance().isMac()) {
			commands.add("-J-Dfile.encoding="
					+ RECommandExecuter.commandEncoding);
		}
		commands.add("-g");
		commands.add("-encoding");
		commands.add(REApplication.SRC_ENCODING);
		commands.add("-classpath");
		commands.add(cp);
		commands.add(env.source);

		writePresLog(PRCommandLog.SubType.COMPILE);// TODO
													// BlockEditor�̎����R���p�C�����O���L�^����Ă��܂��D

		// if (blocking) {
		// // �iBlockEditor�̂��߂̃R���p�C���j�@�u���b�L���O����D
		// try {
		// CommandExecuter.executeCommandWait(commands, env.dir,
		// frame.getConsole());
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
		// } else {
		// // �ʏ�R���p�C���D�@�u���b�L���O���Ȃ��D
		// CommandExecuter.executeCommand(commands, env.dir,
		// frame.getConsole());
		// }

		RECommandExecuter.executeCommand(commands, env.dir, frame.getConsole(), frame.getConsole().getFontMetrics(frame.getConsole().getFont()));

		generefManager.handleCompileDone();
	}

	/*
	 * BlockEditor��GeneRef�̂��߂̃R���p�C������ 2012.12.04 ���̐݌v�͉��Ȃ̂ōĐ݌v����
	 * 
	 * @return
	 */
	public String doCompile2(boolean verbose) {
		JavaEnv env = FileSystemUtil.createJavaEnv(
				sourceManager.getRootDirectory(),
				sourceManager.getCurrentFile());
		String cp = libraryManager.getLibString();

		ArrayList<String> commands = new ArrayList<String>();
		commands.add(compileCommand);
		if (CJavaSystem.getInstance().isMac()) {
			commands.add("-J-Dfile.encoding="
					+ RECommandExecuter.commandEncoding);
		}
		commands.add("-g");
		if (verbose) {
			commands.add("-verbose");
		}
		commands.add("-encoding");
		commands.add(REApplication.SRC_ENCODING);
		commands.add("-classpath");
		commands.add(cp);
		commands.add(env.source);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DummyConsole console = new DummyConsole();
		console.setErr(new PrintStream(out));

		try {
			RECommandExecuter.executeCommandWait(commands, env.dir, console, getFrame().getConsole().getFontMetrics(getFrame().getConsole().getFont()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return out.toString();
	}

	private CFile lastFile;

	// TODO
	public void writePresLog(PRCommandLog.SubType subType, Object... args) {
		try {
			CFile file = getSourceManager().getCCurrentFile();
			if (file == null) {
				file = lastFile;
			} else {
				lastFile = file;
			}
			CPath path = file.getRelativePath(getSourceManager()
					.getCCurrentProject());
			PRLog log = new PRCommandLog(subType, path, args);
			writePresLog(log);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void writePresTextEditLog(PRTextEditLog.SubType subType, int offset,
			int len, String text) {
		try {
			CPath path = getSourceManager().getCCurrentFile().getRelativePath(
					getSourceManager().getCCurrentProject());
			PRLog log = new PRTextEditLog(subType, path, offset, len, text);
			writePresLog(log);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void writePresLog(PRLog log) {
		try {
			presManager.getRecordingProject(
					getSourceManager().getCCurrentProject()).record(log);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void doRun() {
		File target = getSourceManager().getCurrentFile();
		if (!hasRunnableFile(target)) {
			JOptionPane.showMessageDialog(frame, "�R���p�C���ɐ������Ă��܂���", "���s�ł��܂���",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		JavaEnv env = FileSystemUtil.createJavaEnv(getSourceManager()
				.getRootDirectory(), getSourceManager().getCurrentFile());
		String cp = libraryManager.getLibString();
		ArrayList<String> commands = new ArrayList<String>();
		commands.add(runCommand);
		commands.add("-classpath");
		commands.add(cp);
		commands.add(env.runnable);
		
		RECommandExecuter.executeCommand(commands, env.dir, frame.getConsole(), frame.getConsole().getFontMetrics(frame.getConsole().getFont()));
		writePresLog(PRCommandLog.SubType.START_RUN);// TODO
	}

	public void doDebugRun() {

		File target = getSourceManager().getCurrentFile();
		if (!hasRunnableFile(target)) {
			JOptionPane.showMessageDialog(frame, "�R���p�C���ɐ������Ă��܂���", "���s�ł��܂���",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (deno != null && deno.isRunning()) {
			JOptionPane.showMessageDialog(frame, "�O�̃f�o�b�O��ʂ��J�����ςȂ��ł�",
					"���s�ł��܂���", JOptionPane.ERROR_MESSAGE);
			return;
			// CFrameUtils.toFront(deno.getFrame());
			// return;
		}

		// �p�X���擾
		JavaEnv env = FileSystemUtil.createJavaEnv(getSourceManager()
				.getRootDirectory(), getSourceManager().getCurrentFile());
		String args[] = new String[6];
		// �\�[�X�p�X
		args[0] = "-sourcepath";
		args[1] = env.dir.getAbsolutePath();
		// �N���X�p�X
		args[2] = "-classpath";
		String libString = libraryManager.getLibString();
		libString = env.dir.getAbsolutePath() + FileSystemUtil.PATH_SEPARATOR
				+ libString;
		if (CJavaSystem.getInstance().isWindows()) {
			libString = "\"" + libString + "\"";
		}
		args[3] = libString;
		// �N���X��
		args[4] = env.runnable;
		// waitrepaint
		args[5] = "waitrepaint";

		// xml
		// String[] libs = getLibraryManager().getLibsAsArray();
		// try {
		// new JavaToBlockMain().run(getSourceManager().getCurrentFile(),
		// REApplication.SRC_ENCODING, libs);
		// } catch (Exception e) {
		// e.printStackTrace();
		// CErrorDialog.show(getFrame(), "Block�ϊ����̃G���[", e);
		// }

		NDebuggerManager.registerListener(new NDebuggerListener() {
			public void stepPressed() {
				writePresLog(PRCommandLog.SubType.STEP);
			}

			public void debugStarted() {
				writePresLog(PRCommandLog.SubType.START_DEBUG);
			}

			public void debugFinished() {
				writePresLog(PRCommandLog.SubType.STOP_DEBUG);
			}

			public void playPressed() {
				writePresLog(PRCommandLog.SubType.DEBUG_PLAY);
			}

			public void stopPressed() {
				writePresLog(PRCommandLog.SubType.DEBUG_STOP);
			}

			public void speedSet(int speed) {
				writePresLog(PRCommandLog.SubType.DEBUG_SPEED, speed);
			}

			public void contPressed() {
				writePresLog(PRCommandLog.SubType.DEBUG_CONT);
			}

			public void breakpointSet() {
				writePresLog(PRCommandLog.SubType.DEBUG_BPSET);
			}

			public void breakpointClear() {
				writePresLog(PRCommandLog.SubType.DEBUG_BPCLR);
			}

			public void changeAPMode(String mode) {
				writePresLog(PRCommandLog.SubType.DEBUG_CHANGEMODE, mode);
			}
		});
		deno = new GUI();
		deno.run(args);
		deno.getFrame().addWindowFocusListener(new WindowFocusListener() {
			public void windowLostFocus(WindowEvent e) {
				writePresLog(PRCommandLog.SubType.FOCUS_LOST, "DENO");
			}

			public void windowGainedFocus(WindowEvent e) {
				writePresLog(PRCommandLog.SubType.FOCUS_GAINED, "DENO");
			}
		});
		CommandInterpreter cmdint = new CommandInterpreter(deno.getEnv());
		// deno.getEnv().setBlockEditor(blockManager.getBlockEditor());
		// if(blockManager.getBlockEditor() != null) {
		// deno.beMode();
		// }
		cmdint.executeCommand("run");
	}

	// Helper
	public boolean hasRunnableFile(File source) {
		if (source == null) {
			return false;
		}
		File file = getRunnableFile(source);
		return file != null && file.exists();
	}

	public boolean deleteRunnable(File source) {
		if (hasRunnableFile(source)) {
			return getRunnableFile(source).delete();
		}
		return false;
	}

	private File getRunnableFile(File source) {
		if (source == null) {
			throw new IllegalArgumentException();
		}
		File dir = source.getParentFile();
		File runnableFile = new File(dir, FileSystemUtil.cutExtension(source)
				+ "." + RUNNABLE_EXTENSION);
		return runnableFile;
	}

	// Java -version
	// CommandExecuter.executeCommandWait("java -version",
	// getSourceManager().getProjectDirectory(), frame
	// .getConsole());

	public void doKillAll() {
		RECommandExecuter.killAll();
	}

	public void doFormat() {
		getFrame().getEditor().format();
	}

	public void doShowBytecode() {
		if (!getSourceManager().hasCurrentFile()) {
			JOptionPane.showMessageDialog(frame, "�\�[�X���I������Ă��܂���",
					"�o�C�g�R�[�h���{���ł��܂���", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (getSourceManager().hasCurrentFile()
				&& getFrame().getEditor().isDirty()) {
			JOptionPane.showMessageDialog(frame, "�\�[�X���Z�[�u����Ă��܂���",
					"�o�C�g�R�[�h���{���ł��܂���", JOptionPane.ERROR_MESSAGE);
			return;
		}

		File target = getSourceManager().getCurrentFile();
		if (!hasRunnableFile(target)) {
			JOptionPane.showMessageDialog(frame, "�R���p�C���ɐ������Ă��܂���",
					"�o�C�g�R�[�h���{���ł��܂���", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			File classFile = getRunnableFile(target);
			String className = FileSystemUtil.cutExtension(classFile);
			REBytecodeViewerGenerator.showViewer(classFile, className, frame);
		} catch (Exception ex) {
			CErrorDialog.show(getFrame(), "Handle Exception", ex);
			return;
		}
	}

	/****************************
	 * �ꎞ�̌q��
	 ****************************/

	public void doOpen(CFileElement file) {
		this.doOpen(file.toJavaFile());
	}

	public void doSetProjectDirectory(CDirectory directory) {
		if (directory == null) {
			this.doSetProjectDirectory((File) null);
		} else {
			this.doSetProjectDirectory(directory.toJavaFile());
		}
	}

	public ICFwResourceRepository getResourceRepository() {
		return sourceManager;
	}

	private JFileChooser chooser = new JFileChooser();

	public void doExport() {
		try {
			CDirectory project = getSourceManager().getCCurrentProject();
			if (project == null) {
				JOptionPane.showMessageDialog(frame, "�v���W�F�N�g���I������Ă��܂���",
						"Export�ł��܂���", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (!COMMENT) {// CONFIRM ONLY
				int res = 0;
				res = JOptionPane.showConfirmDialog(frame,
						"�u" + project.getName() + "�v" + "��Export���܂��D��낵���ł��ˁH",
						"�v���W�F�N�g���m�F", JOptionPane.OK_CANCEL_OPTION);
				if (res != JFileChooser.APPROVE_OPTION) {
					return;
				}
			} else {// COMMENT
				RECommentInputDialog input = new RECommentInputDialog(project);
				int res = 0;
				res = JOptionPane.showConfirmDialog(frame, input,
						"�v���W�F�N�g���m�F�ƃR�����g����", JOptionPane.OK_CANCEL_OPTION);
				input.save();
				if (res != JFileChooser.APPROVE_OPTION) {
					return;
				}
			}

			// dat�t�@�C���̃R�s�[
			copyDatFileToProject();

			chooser.setSelectedFile(new File(CFileSystem.getExecuteDirectory()
					.getAbsolutePath() + "/" + project.getName() + ".zip"));
			int res = chooser.showSaveDialog(getFrame());
			if (res != JFileChooser.APPROVE_OPTION) {
				return;
			}

			File f = chooser.getSelectedFile();
			CFilename name = new CFilename(f.getName());
			name.setExtension("zip");
			CDirectory dir = CFileSystem.findDirectory(f.getParentFile()
					.getAbsolutePath());
			CFile zip = dir.findOrCreateFile(name);
			NewZipUtil.createZip(zip, project, project);

			JOptionPane
					.showConfirmDialog(frame, name.toString()
							+ "�Ƃ���zip�t�@�C����Export���܂����D", "�������܂���",
							JOptionPane.OK_OPTION);

		} catch (Exception ex) {
			ex.printStackTrace(frame.getConsole().getErr());
			CErrorDialog.show(frame, "Export���ɃG���[���������܂����D", ex);
			// JOptionPane.showConfirmDialog(frame, "Export���ɃG���[���������܂����D",
			// "�G���[",
			// JOptionPane.ERROR_MESSAGE);
		} finally {
			// �R�s�[����dat�t�@�C�����폜
			deleteDatFileFromProject();
		}
	}

	public void doOpenPreferencePage() {
		this.preferenceManager.openPreferenceFrame();
	}

	public void doOpenBlockEditor() {
		// for test
//		chBlockEditorController = new CHBlockEditorController("");
//		chBlockEditorController.setFileOpened(true);
//		checoproManager.openBlockEditorForCH(chBlockEditorController,
//				getResourceRepository().getCCurrentFile().toJavaFile()
//				, getResourceRepository().getCCurrentProject().getAbsolutePath().toString()
//				+ "/lang_def_project.xml");
		blockManager.doOpenBlockEditor();
		// 20130926 DENO��BE�𒼐ڎQ�Ƃ���@�b��Ή�
		if (deno != null && deno.isRunning()) {
			deno.getEnv().setBlockEditor(blockManager.getBlockEditor());
		}
	}

	public void doOpenFlowViewer() {
		flowManager.doOpenFlowViewer();
	}

	public void doOpenGeneRefBrowser() {
		generefManager.openGeneRefBrowser();
	}

	// CheCoPro(kato)
	public void doStartCheCoPro() {
		checoproManager.startCheCoPro();
	}

	private void copyDatFileToProject() {
		try {
			// TODO ���}���u�@mac����NullPointerException���o��
			generefManager.copyDatFileToProject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteDatFileFromProject() {
		try {
			generefManager.deleteDatFileFromProject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doOpenPPV() {
		try {
			ppvManager.openPresVisualizer();
		} catch (Exception ex) {
			ex.printStackTrace();
			CErrorDialog.show(frame, "OpenPPV���ɃG���[���������܂����D", ex);
		}
	}

	public void doCreateCocoData() {
		try {
			createCocoDataManager.createCocoData();
		} catch (Exception ex) {
			ex.printStackTrace();
			CErrorDialog.show(frame, "Create CocoData���ɃG���[���������܂����D", ex);
		}
	}

	public void doOpenCocoViewer() {
		try {
			cocoViewerManager.openCocoViewer(createCocoDataManager
					.getPPProjectSet());
		} catch (Exception ex) {
			ex.printStackTrace();
			CErrorDialog.show(frame, "Open CocoViewer���ɃG���[���������܂����D", ex);
		}
	}

	public void doOpenClearCash() {
		try {
			ppvManager.clearCash();
		} catch (Exception ex) {
			ex.printStackTrace();
			CErrorDialog.show(frame, "Clear Cash���ɃG���[���������܂����D", ex);
		}
	}

	private CHBlockEditorController chBlockEditorController;
	
	public CHBlockEditorController getChBlockEditorController() {
		return chBlockEditorController;
	}

	public void setChBlockEditorController(
			CHBlockEditorController chBlockEditorController) {
		this.chBlockEditorController = chBlockEditorController;
	}

	public REApplication doOpenNewRE(String dirPath) {
		REApplication application = new REApplication();
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		application.initializeLookAndFeel();
		application.initializeCommands();
		application.initializeAndOpen(dirPath);
		// �Ԃ�l�ǉ��ikato�j
		return application;
	}

	// private void sourceColoringTest(){
	// try {
	// List<JTextPane> panes = new ArrayList<JTextPane>();
	// DefaultStyledDocument doc = (DefaultStyledDocument) (getFrame()
	// .getEditor().getViewer().getTextPane().getDocument());
	// int len = doc.getLength();
	// String text = doc.getText(0, len);
	// int start = 0;
	// for (int i = 0; i < len; i++) {
	// char c = text.charAt(i);
	// if (c == '\n') {
	// int end = i;
	// DefaultStyledDocument copy = CCopyUtil.copyDeep(doc);
	// copy.remove(end, len - end);// ���ԏd�v
	// copy.remove(0, start);// ���ԏd�v
	// JTextPane pane = new JTextPane(copy);
	// panes.add(pane);
	// start = i + 1;
	// }
	// }
	//
	// JFrame f = new JFrame();
	// f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	// f.setVisible(true);
	// JList<JTextPane> list = new JList<JTextPane>(new Vector<JTextPane>(
	// panes));
	// list.setCellRenderer(new ListCellRenderer<JTextPane>() {
	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * javax.swing.ListCellRenderer#getListCellRendererComponent
	// * (javax.swing.JList, java.lang.Object, int, boolean, boolean)
	// */
	// public Component getListCellRendererComponent(
	// JList<? extends JTextPane> list, JTextPane value,
	// int index, boolean isSelected, boolean cellHasFocus) {
	// return value;
	// }
	// });
	// f.getContentPane().add(list);
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// }

}
