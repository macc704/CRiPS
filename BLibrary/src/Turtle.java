
/*
 * Turtle.java
 * 
 * Created on 2003/06/12
 */

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;

import common.resource.CResourceFinder;

/*
 * Class Turtle.
 * 
 * version 1.0.2 2007/12/12 HolderTurtle�ǉ��̂��ߕύX
 * version 1.0.3 2007/12/12 InputTurtle�ǉ��̂��ߕύX
 * version 1.0.4 2007/12/19 ButtonTurtle�o�O�̏C���i�������u���b�N���Ă΂��O�ɁC
 * draw()��state���Ă��NullPointer���o�Ă����D���̑�InputTurtle�̎d�l�ύX�D
 * version 1.0.5 2007/12/21 SoundTurtle��ǉ� 
 * version 1.1.0 2011/09/28 Sound������C�� 
 * 		�EURL�Ŏw�肷�邱�Ƃɂ��Cjar�̒��̃T�E���h�t�@�C�����Đ��ł���悤�ɂ����D
 * 		�ESound��Path�Ŏw�肷�邱�Ƃɂ��C�N���X�p�X���t�@�C���p�X�̏��Ŏ����ŒT������ 
 * 		�EThread����̎��������������C�T�E���h�Đ��C��~�̃e�X�g�Ɏ��s���Ă����̂Ńo�O�C��
 * version 1.2.0 2011/11/22 API�̑啝�ȕύX 
 * 		�EImageTurtle�ɂ��Ă�URL�Ŏw�肷�邱�Ƃɂ��Cjar�̒���Image�t�@�C����ǂݍ��߂�悤�ɂ����B
 * 		�EKeyPressing�CMousePressingAPI�̒ǉ��CmouseDown��mouseClicked�V���[�Y�ɕύX�B
 * version 1.2.1 2011/12/16 �Z���I�[�g�}�g���@�\�E�o�O�C�� 
 * 		�E�^�[�g����fd�������ɂ��������C��(sin�̌덷���ۂ߂Ă��܂�)
 * 		�ETurtleTurtle��location(), direction()���������Ԃ���Ȃ������C�� 
 * 		�ECellTurtle�ǉ�
 * version 1.2.2 
 * 		�ECellTurtle������
 * version 1.2.3
 * 		�ETurtleTurtle��location(), direction()���������Ԃ���Ȃ������C��
 * 		 �̃G���o�O�Ń^�[�g���������Ȃ��o�O���C��
 * version 1.2.4
 * 		�E�E�C���h�E��������SoundTurtle�̃N���A 
 * version 1.2.5
 * 		�E�f�t�H���g�^�[�g����warp���o����悤�ɂ���D 
 * version 1.2.6
 * 		�ETurtle�̃C���^�[�i���N���X���O�ɏo�����D
 * 		�ECellTurtle�̂Q�����ł��o����悤�ɂ���D 
 * version 1.2.7
 * 		�ECellTurtle�̎������@�������ύX�D
 * version 1.2.8
 * 		�EprintNoln��ǉ�
 * version 1.3.0 		
 * 		�E�V�����R���N�V�����Ƃ���ListTurtle��ǉ�
 * 		�E����ɔ����CTurtleTurtle�ւ�delegation�Ɋւ���looks�w��̃R�[�h��ύX
 * 		�EList�ɓ������悤�CTurtle��image()�֐���ǉ��D
 * 		�EHolderTurtle��@deprecated�w��
 * version 1.4.0 		
 * 		�E�t�@�C���ǂݏ����@�\�ǉ�
 * version 1.4.1
 * 		�E�t�@�C���ǂ�, StringBuffer�ɕύX�i�������I�j
 * version 1.4.2
 *		�E�t�@�C���ǂ�, enc�w��\�ɁD
 *		�E�t�@�C���ǂ�, URL�w��\�ɁD
 * version 1.4.3
 *		�Ewindow.canvas().setBackground(Color);�o����悤�ɂ���D
 * version 1.4.4 (1.5.0)
 *		�ECardTurtle�̎d�l��ύX�CgetNumber()���o����悤�ɂ���D
 *		�EgetNumberAtCursor()��񐄏�
 * version 1.4.5 (1.5.1)
 *		�ECardTurtle�̎d�l��ύX�CbgColor���ݒ�o����悤�ɂ���D
 *      �E��L�ɂƂ��Ȃ��CCardTurtle��bgColor��null->WHITE�ɂ���D
 * version 1.5.2
 *		�EListTurtle bgColor�ݒ莞��Image�����������悤�Ƀo�O�C���D
 * version 1.5.3
 *		�ETextTurtle��getText(), getNumber()��ǉ��D�iText��Card���d���R�[�h�Ȃ̂Ń��t�@�N�^�����O���������悳�����j
 * version 1.5.4
 *		�EDefault��Font��Dialog->MS Gothic�ɕύX
 * version 1.5.5
 *		�EaddCursor(index, Object);��ǉ�
 * version 1.5.6
 *		�EaddToBeforeCursor(Object)��ǉ�
 *		�EaddToAfterCursor(Object)��ǉ�
 * version 1.5.7
 *		�EListTurtle��get()��Null��O����
 * version 1.5.7.x 2012/04/05
 * 		�Eobpro�o�[�W�����Ɠ����icapturemode�̎�����j
 * version 1.5.8 2012/04/05
 * 		�Estart�֐���public�Ɂi�I�u�v���łŃI�[�o�[���C�h�ł��Ȃ��j
 * version 1.5.9 2012/09/28
 * 		�Ejdk1.7��sound�������Ȃ�����fix
 * version 1.5.12 2012/10/09
 * 		�E�ŋ߂�Java(1.7)��applet��alwaysOnTop�����������ƁC�N�����Ȃ���� 
 * 		�ETurtleFrame if (JavaVertionChecker.getMinorVersion() >= 5 && && !TurtleLaunchApplet.initialized) {
 * 		�EJFrame.EXIT_ON_CLOSE��JDK1.7�ŋ֎~�ɂȂ�D
 * 			174�s�ڏC�� if (!TurtleLaunchApplet.initialized) {
 * 			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 * version 1.5.13 2012/11/06
 * 		�EBE�ɑΉ����邽�߁CSoundTurtle�̎d�l�ύX
 * version 1.5.14 2012/11/07
 * 		�EBE�ɑΉ����邽�߁CsetShow()��ǉ�
 * version 1.5.15 2012/11/14
 * 		�EJava Web Start�ɑΉ����邽��ImageTurtle�̃��\�[�X�擾���@��ύX
 * version 1.5.16 2012/11/14
 * 		�E������"jnlp"��n���ƃR���\�[�����\�������悤�ɕύX
 * 		�EJava Web Start�ŏ������Ȃ��Ă����삷��悤�ɕύX
 * version 1.5.17 2012/11/27
 * 		�EJNLPMain���쐬
 * version 1.5.18 2012/11/28
 * 		�EJava Web Start���̕s����C��
 * version 1.5.23 2012/12/19
 * 		�EJava Web Start����EXIT_ON_CLOSE�ɂȂ�Ȃ��s����C��
 * 		�EJava Web Start���̃R���\�[����ʃE�C���h�E�ɕύX
 * version 1.5.24 2013/01/09
 *      �EDEFAULT_IS_ALWAYS_ON_TOP ��false��
 *      �E�N������toFront();
 * version 1.5.25 2013/01/15
 *      �EButtonTurtle, InputTurtle��getText()���\�b�h��ǉ�
 * version 1.5.26 2013/12/15
 *      �EDefaultTurtle��getX()�Ȃǂ̓��삪������������fix
 *      �Edebugger��update()�����f�����悤�ɁCwaitrepaint���[�h��ǉ�
 *      �EListTurtle��autoupdate���[�h��ǉ�(default��false)
 * version 1.5.27 2015/08/14
 * 		�EcreateTurtle()���\�b�h�̒ǉ� for SSS JUnicoen�f��
 *      
 * @author macchan
 * @version $Id: Turtle.java,v 1.11 2007/12/21 11:13:42 macchan Exp $
 */
public class Turtle implements KeyListener, MouseListener, MouseMotionListener {

	private static final String version = "1.5.27 (2015/08/14)";

	static {
		System.out.println("Turtle Version: " + version);
	}

	/***************************************************
	 * static variables
	 ****************************************************/

	private static Object instanciationLock = new Object();
	private static boolean isinstanciation = false;

	public static TurtleFrame window = new TurtleFrame(100, 100, 300, 300);
	private static ConsoleTextArea console;

	public static Turtle defaultTurtle = null;
	private static final Turtle nullTurtle = new DefaultTurtle();

	public static boolean initialized = false;

	private static List allProtectedInstances = new ArrayList();
	private static List allInstances = new ArrayList();

	/****************************************
	 * For Applet
	 ****************************************/

	public static boolean captureMode = false;
	public static Applet applet = null;

	/***************************************************
	 * static main
	 ****************************************************/

	public static void main(String argv[]) {
		String classname;
		if (argv.length >= 1) {
			classname = argv[0];
		} else {
			classname = "Turtle";
		}
		try {
			Object o = Class.forName(classname).newInstance();
			if (o.getClass() == Turtle.class) {
				System.out.println("���s�R�}���h���Ԉ���Ă��܂��B(Turtle �̌�Ɏ����̃N���X�����K�v�ł�)");
				System.exit(0);
			} else if (o instanceof Turtle) {
				startTurtle((Turtle) o, argv);
			} else {
				System.out.println(classname + " is not a subclass of Turtle class.");
			}
		} catch (Exception e) {
			System.out.println(classname + " �N���X��������܂���B�R���p�C���͒ʂ�܂������H");
		}
	}

	/****************************************
	 * Start and Stop
	 ****************************************/

	public static void startTurtle(Turtle turtle, String args[]) {
		if (args.length >= 1 && args[0].equals("capturemode")) {
			captureMode = true;
			System.out.println("capturemode!");
		}
		if (args.length >= 1 && args[0].equals("waitrepaint")) {
			window.canvas().setWaitRepaint(true);
		}
		startTurtle(turtle);
	}

	public static void startTurtle(Turtle turtle) {
		allProtectedInstances.remove(turtle);
		defaultTurtle = turtle;
		window.addKeyListener(turtle);
		window.canvas().addKeyListener(turtle);
		window.canvas().addMouseListener(turtle);
		window.canvas().addMouseMotionListener(turtle);

		if (!Turtle.isApplet()) {// normal
			window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} else {// applet
			window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			window.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					SoundTurtle.clearSound();
				}
			});
			console = window.initializeViewWithConsole();
		}

		if (captureMode) {
			window.size(1000, 700);
		}

		window.setVisible(true);
		initialized = true;
		window.canvas().mappedWait();
		window.restart();
	}

	public static void stopTurtle() {
		window.dispose();
		window = new TurtleFrame(100, 100, 300, 300);
		initialized = false;
	}

	public static List getAllInstances() {
		return new ArrayList(allInstances);
	}

	public static List getAllProtectedInstances() {
		return new ArrayList(allProtectedInstances);
	}

	public static void resetAllInstances() {
		allInstances.clear();
		for (int i = 0; i < allProtectedInstances.size(); i++) {
			Turtle t = (Turtle) allProtectedInstances.get(i);
			t.initialize();
		}
	}

	public static boolean isAppletOrJNLP() {
		return isApplet() || isJNLP();
	}

	public static boolean isApplet() {
		return TurtleLaunchApplet.initialized == true;
	}

	public static boolean isJNLP() {
		return JNLPMain.jnlp;
	}

	/***************************************************
	 * �ϐ�
	 ****************************************************/

	// �e�^�[�g���֘A
	protected ListTurtle parent;
	@SuppressWarnings("deprecation")
	protected HolderTurtle parentHolder;// @deprecated ���ʌ݊����̂���
	protected List<InputTurtle> inputTurtles = new ArrayList<InputTurtle>();

	// ���W�֘A
	private Point2D location = new Point2D.Double(100d, 100d); // ���S�̈ʒu

	private Point2D balance = new Point2D.Double(100d, 100d); // �d�S�̈ʒu

	private Dimension2D size = new DoubleDimension(100d, 100d); // �傫��

	private double angle = 0.0; // ��]�p�x

	private double direction = 0.0; // ����

	// �`�֘A
	private Turtle looks = null; // ������

	private boolean show = true;

	private LineList originalShape = null; // ������

	private LineList shape = new LineList(); // �`

	// �O�Պ֘A
	private boolean penDown = false; // �y��������Ă��邩

	private Color penColor = Color.black; // �y���̐F

	private LineList locus = new LineList(); // �O��

	// �Ϗ��֘A
	private Turtle delegator = null; // �Ϗ���

	// �`��ϊ��֘A
	private boolean dirty = true;

	private AffineTransform currentTransform = null;

	// ���͊֘A
	private KeyEvent keyEvent = null;

	private MouseEvent mouseEvent = null;

	private MouseEvent mouseMotionEvent = null;

	private boolean inputCaptured = true;

	/***************************************************
	 * �R���X�g���N�^
	 ****************************************************/

	/**
	 * Constructor.
	 */
	public Turtle() {
		initialize();
	}

	/***************************************************
	 * initialize
	 ***************************************************/

	private void initialize() {
		synchronized (instanciationLock) {
			initializeParameter();
			if (this instanceof DefaultTurtle) {
				// do nothing
			} else if (isinstanciation) {
				delegator(nullTurtle);
			} else if (!initialized) {
				allProtectedInstances.add(this);
			} else if (this.getClass() == Turtle.class) {
				delegator(new TurtleTurtle());
				allInstances.add(this);
			} else if (this instanceof TurtleTurtle) {
				allInstances.add(this);
			} else {
				initializeLooks();
				allInstances.add(this);
			}
		}
	}

	/***************************************************
	 * die
	 ****************************************************/

	public void die() {
		if (allInstances.contains(this)) {
			allInstances.remove(this);
		}
	}

	/***************************************************
	 * Reset
	 ****************************************************/

	private void initializeParameter() {
		// ���W�֘A
		location = new Point2D.Double(100d, 100d); // ���S�̈ʒu
		balance = new Point2D.Double(100d, 100d); // �d�S�̈ʒu
		size = new DoubleDimension(100d, 100d); // �傫��
		angle = 0.0; // ��]�p�x
		direction = 0.0; // ����

		// �`�֘A
		looks = null; // ������
		show = true;
		originalShape = null; // ������
		shape = new LineList(); // �`

		// �O�Պ֘A
		penDown = false; // �y��������Ă��邩
		penColor = Color.black; // �y���̐F
		locus = new LineList(); // �O��

		// �Ϗ��֘A
		delegator = null; // �Ϗ���

		// �`��ϊ��֘A
		dirty = true;
		currentTransform = null;

		// ���͊֘A
		keyEvent = null;
		mouseEvent = null;
		inputCaptured = true;
	}

	public void initializeLooks() {
		DefaultTurtle turtle = new DefaultTurtle();

		synchronized (instanciationLock) {
			isinstanciation = true;
			delegator(turtle);
			try {
				start();
			} catch (RuntimeException ex) {
				System.out.println(ex.getMessage());
			} finally {
				delegator(null);
				isinstanciation = false;
			}
		}

		turtle.die();
		this.originalShape = new LineList(turtle.locus());
		looks(this);
		resetScale();
	}

	/***************************************************
	 * for Turtle 2.0
	 ****************************************************/

	public Turtle createTurtle() {
		return new Turtle();
	}

	/***************************************************
	 * Looks�֘A
	 ****************************************************/

	public synchronized void looks(Turtle looks) {
		if (delegator != null) {
			delegator.looks(looks);
			return;
		}
		this.looks = looks;
		this.shape = new LineList(looks.originalShape());
		this.currentTransform = null;
		this.dirty = true;
	}

	public Turtle looks() {
		if (delegator != null) {
			return delegator.looks();
		}
		return looks;
	}

	public void resetLooks() {
		if (delegator != null) {
			delegator.resetLooks();
			return;
		}
		looks(this);
	}

	public void looksSize() {
		if (delegator != null) {
			delegator.looksSize();
			return;
		}
		size(looks.size());
	}

	/***************************************************
	 * �Ϗ��֘A
	 ****************************************************/

	public void delegator(Turtle delegator) {
		this.delegator = delegator;
	}

	/***************************************************
	 * shape(�`��) locus(�O��) �֘A
	 ****************************************************/

	protected LineList shape() {
		return this.shape;
	}

	protected LineList locus() {
		return this.locus;
	}

	protected LineList originalShape() {
		if (this.originalShape != null) {
			return this.originalShape;
		} else {
			return this.locus;
		}
	}

	/***************************************************
	 * show�֘A
	 ****************************************************/

	public void show() {
		if (delegator != null) {
			delegator.show();
			return;
		}
		show(true);
	}

	public void hide() {
		if (delegator != null) {
			delegator.hide();
			return;
		}
		show(false);
	}

	public void show(boolean show) {
		if (delegator != null) {
			delegator.show(show);
			return;
		}
		this.show = show;
		if (show) {
			reshape();
		}
	}

	public void setShow(boolean show) {
		if (delegator != null) {
			delegator.setShow(show);
			return;
		}
		show(show);
	}

	public boolean isShow() {
		if (delegator != null) {
			return delegator.isShow();
		}
		return show;
	}

	/***************************************************
	 * Location�֘A
	 ****************************************************/

	public Point2D location() {
		if (delegator != null) {
			return delegator.location();
		}
		return new Point2D.Double(x(), y());
	}

	public void location(Point2D location) {
		if (delegator != null) {
			delegator.location(location);
			return;
		}
		location(location.getX(), location.getY());
	}

	public void location(double x, double y) {
		if (delegator != null) {
			delegator.location(x, y);
			return;
		}

		if (penDown) {
			Point2D newLocation = new Point2D.Double(x, y);
			locus.add(new Line(location, newLocation, penColor));
		}

		double moveX = x - x();
		double moveY = y - y();

		location.setLocation(x, y);
		balance.setLocation(balance.getX() + moveX, balance.getY() + moveY);

		dirty = true;

	}

	public double x() {
		if (delegator != null) {
			return delegator.x();
		}
		return location.getX();
	}

	public double y() {
		if (delegator != null) {
			return delegator.y();
		}
		return location.getY();
	}

	public int getX() {
		if (delegator != null) {
			return delegator.getX();
		}
		return (int) x();
	}

	public int getY() {
		if (delegator != null) {
			return delegator.getY();
		}
		return (int) y();
	}

	public void x(double x) {
		if (delegator != null) {
			delegator.x(x);
			return;
		}
		location(x, y());
	}

	public void y(double y) {
		if (delegator != null) {
			delegator.y(y);
			return;
		}
		location(x(), y);
	}

	public double minX() {
		if (delegator != null) {
			return delegator.minX();
		}
		return x() - width() / 2;
	}

	public double minY() {
		if (delegator != null) {
			return delegator.minY();
		}
		return y() - height() / 2;
	}

	public double maxX() {
		if (delegator != null) {
			return delegator.maxX();
		}
		return x() + width() / 2;
	}

	public double maxY() {
		if (delegator != null) {
			return delegator.maxY();
		}
		return y() + height() / 2;
	}

	/***************************************************
	 * Rotated(��]��)Position�֘A
	 ****************************************************/

	public Point2D rotatedLocation() {
		if (delegator != null) {
			return delegator.rotatedLocation();
		}
		return new Point2D.Double(rotatedX(), rotatedY());
	}

	public double rotatedX() {
		if (delegator != null) {
			return delegator.rotatedX();
		}
		return shape.getBounds().getCenterX();
	}

	public double rotatedY() {
		if (delegator != null) {
			return delegator.rotatedY();
		}
		return shape.getBounds().getCenterY();
	}

	public double rotatedMinX() {
		if (delegator != null) {
			return delegator.rotatedMinX();
		}
		return rotatedX() - rotatedWidth() / 2;
	}

	public double rotatedMinY() {
		if (delegator != null) {
			return delegator.rotatedMinY();
		}
		return rotatedY() - rotatedHeight() / 2;
	}

	public double rotatedMaxX() {
		if (delegator != null) {
			return delegator.rotatedMaxX();
		}
		return rotatedX() + rotatedWidth() / 2;
	}

	public double rotatedMaxY() {
		if (delegator != null) {
			return delegator.rotatedMaxY();
		}
		return rotatedY() + rotatedHeight() / 2;
	}

	/***************************************************
	 * Size�֘A
	 ****************************************************/

	public Dimension2D size() {
		if (delegator != null) {
			return delegator.size();
		}
		return new DoubleDimension(size);
	}

	public void size(Dimension2D dimension) {
		if (delegator != null) {
			delegator.size(dimension);
			return;
		}
		size(dimension.getWidth(), dimension.getHeight());
	}

	public void size(double width, double height) {
		if (delegator != null) {
			delegator.size(width, height);
			return;
		}

		if (width <= 0.1) {
			width = 0.1;
		}
		if (height <= 0.1) {
			height = 0.1;
		}

		size.setSize(width, height);
		dirty = true;

	}

	public double width() {
		if (delegator != null) {
			return delegator.width();
		}
		return size().getWidth();
	}

	public double height() {
		if (delegator != null) {
			return delegator.height();
		}
		return size().getHeight();
	}

	public int getWidth() {
		if (delegator != null) {
			return delegator.getWidth();
		}
		return (int) width();
	}

	public int getHeight() {
		if (delegator != null) {
			return delegator.getHeight();
		}
		return (int) height();
	}

	public void width(double width) {
		if (delegator != null) {
			delegator.width(width);
			return;
		}
		size(width, height());
	}

	public void height(double height) {
		if (delegator != null) {
			delegator.height(height);
			return;
		}
		size(width(), height);
	}

	public void large(double width, double height) {
		if (delegator != null) {
			delegator.large(width, height);
			return;
		}
		size(width() + width, height() + height);
	}

	public void large(double length) {
		if (delegator != null) {
			delegator.large(length);
			return;
		}
		large(length, length);
	}

	public void wide(double length) {
		if (delegator != null) {
			delegator.wide(length);
			return;
		}
		large(length, 0);
	}

	public void tall(double length) {
		if (delegator != null) {
			delegator.tall(length);
			return;
		}
		large(0, length);
	}

	public void small(double width, double height) {
		if (delegator != null) {
			delegator.small(width, height);
			return;
		}
		large(-width, -height);
	}

	public void small(double length) {
		if (delegator != null) {
			delegator.small(length);
			return;
		}
		large(-length);
	}

	public void narrow(double length) {
		if (delegator != null) {
			delegator.narrow(length);
			return;
		}
		wide(-length);
	}

	public void little(double length) {
		if (delegator != null) {
			delegator.little(length);
			return;
		}
		tall(-length);
	}

	/***************************************************
	 * Rotated(��]��)Size�֘A
	 ****************************************************/

	public double rotatedWidth() {
		if (delegator != null) {
			return delegator.rotatedWidth();
		}
		return shape.getBounds().getWidth();
	}

	public double rotatedHeight() {
		if (delegator != null) {
			return delegator.rotatedHeight();
		}
		return shape.getBounds().getHeight();
	}

	/***************************************************
	 * Bounds�֘A
	 ****************************************************/

	public Rectangle2D bounds() {
		if (delegator != null) {
			return delegator.bounds();
		}
		return new Rectangle2D.Double(minX(), minY(), width(), height());
	}

	public void bounds(Rectangle2D r) {
		if (delegator != null) {
			delegator.bounds(r);
			return;
		}
		location(r.getCenterX(), r.getCenterY());
		size(r.getWidth(), r.getHeight());
	}

	public void bounds(double x, double y, double width, double height) {
		if (delegator != null) {
			delegator.bounds(x, y, width, height);
			return;
		}
		bounds(new Rectangle2D.Double(x, y, width, height));
	}

	public Rectangle2D rotatedBounds() {
		if (delegator != null) {
			return delegator.rotatedBounds();
		}
		return shape.getBounds();
	}

	/***************************************************
	 * RotatePoint�֘A
	 ****************************************************/

	public Point2D balance() {
		if (delegator != null) {
			return delegator.balance();
		}
		return balance;
	}

	public double balanceX() {
		if (delegator != null) {
			return delegator.balanceX();
		}
		return balance().getX();
	}

	public double balanceY() {
		if (delegator != null) {
			return delegator.balanceY();
		}
		return balance().getY();
	}

	public int getBalanceX() {
		if (delegator != null) {
			return delegator.getBalanceX();
		}
		return (int) balanceX();
	}

	public int getBalanceY() {
		if (delegator != null) {
			return delegator.getBalanceY();
		}
		return (int) balanceY();
	}

	public void balance(double percentX, double percentY) {
		if (delegator != null) {
			delegator.balance(percentX, percentY);
			return;
		}
		double xMag = percentX / 100d;
		double yMag = percentY / 100d;

		RelativePoint rp = new RelativePoint(xMag, yMag);
		balance = rp.getPoint(bounds());
	}

	/***************************************************
	 * move�֘A
	 ****************************************************/

	public void fd(double length) {
		if (delegator != null) {
			delegator.fd(length);
		} else {
			move(length, 0);
		}
	}

	public void bk(double length) {
		if (delegator != null) {
			delegator.bk(length);
		} else {
			move(length, 180);
		}
	}

	public void right(double length) {
		if (delegator != null) {
			delegator.right(length);
		} else {
			move(length, 90);
		}
	}

	public void left(double length) {
		if (delegator != null) {
			delegator.right(length);
		} else {
			move(length, -90);
		}
	}

	public void move(double length, double direction) {
		if (delegator != null) {
			delegator.move(length, direction);
			return;
		}
		double theta = theta(angle() + direction() + direction);
		double xLength = Math.sin(theta) * length;
		double yLength = -Math.cos(theta) * length;

		double newX = x() + xLength;
		double newY = y() + yLength;

		warp(newX, newY);
	}

	public void warp(double x, double y) {
		if (delegator != null) {
			delegator.location(x, y);
			return;
		}

		location(x, y);
	}

	public void warpByTopLeft(double x, double y) {
		if (delegator != null) {
			delegator.warpByTopLeft(x, y);
			return;
		}
		double centerX = x + width() / 2;
		double centerY = y + height() / 2;
		warp(centerX, centerY);
	}

	/***************************************************
	 * scale�֘A
	 ****************************************************/

	public void scale(double scale) {
		if (delegator != null) {
			delegator.scale(scale);
			return;
		}
		scale(scale, scale);
	}

	public void scale(double scaleX, double scaleY) {
		if (delegator != null) {
			delegator.scale(scaleX, scaleY);
			return;
		}
		size(width() * scaleX, height() * scaleY);
	}

	public void resetScale(double scale) {
		if (delegator != null) {
			delegator.resetScale(scale);
			return;
		}
		Rectangle2D org = originalBounds();
		size(org.getWidth() * scale, org.getHeight() * scale);
	}

	public void resetScale() {
		if (delegator != null) {
			delegator.resetScale();
			return;
		}
		resetScale(1d);
	}

	public Scale scale() {
		if (delegator != null) {
			return delegator.scale();
		}
		Rectangle2D org = originalBounds();
		return new Scale(org.getWidth(), org.getHeight(), width(), height());
	}

	public double scaleX() {
		if (delegator != null) {
			return delegator.scaleX();
		}
		return scale().x();
	}

	public double scaleY() {
		if (delegator != null) {
			return delegator.scaleY();
		}
		return scale().y();
	}

	/***************************************************
	 * angle�֘A
	 ****************************************************/

	public void angle(double angle) {
		if (delegator != null) {
			delegator.scale(angle);
			return;
		}
		this.angle = angle;
		dirty = true;
	}

	public double angle() {
		if (delegator != null) {
			return delegator.angle();
		}
		return angle;
	}

	public void rt(double angle) {
		if (delegator != null) {
			delegator.rt(angle);
			return;
		}
		rotate(angle);
	}

	public void lt(double angle) {
		if (delegator != null) {
			delegator.lt(angle);
			return;
		}
		rotate(-angle);
	}

	public void rotate(double angle) {
		if (delegator != null) {
			delegator.rotate(angle);
			return;
		}
		angle(this.angle + angle);
	}

	/***************************************************
	 * direction�֘A
	 ****************************************************/

	public void direction(double direction) {
		if (delegator != null) {
			delegator.direction(direction);
			return;
		}
		this.direction = direction;
	}

	public double direction() {
		if (delegator != null) {
			return delegator.direction();
		}
		return direction;
	}

	public void directionRt(double direction) {
		if (delegator != null) {
			delegator.directionRt(direction);
			return;
		}
		rotateDirection(direction);
	}

	public void directionLt(double direction) {
		if (delegator != null) {
			delegator.directionLt(direction);
			return;
		}
		rotateDirection(-direction);
	}

	public void rotateDirection(double direction) {
		if (delegator != null) {
			delegator.rotateDirection(direction);
			return;
		}
		direction(this.direction + direction);
	}

	/***************************************************
	 * theta�����߂�Utility
	 ****************************************************/

	protected double theta() {
		if (delegator != null) {
			return delegator.theta();
		}
		return theta(angle());
	}

	protected double theta(double angle) {
		if (delegator != null) {
			return delegator.theta(angle);
		}
		// return angle / 360d * 2d * Math.PI;
		return Math.toRadians(angle);
	}

	/***************************************************
	 * pen�֘A
	 ****************************************************/

	public void up() {
		if (delegator != null) {
			delegator.up();
			return;
		}

		penDown = false;
	}

	public void down() {
		if (delegator != null) {
			delegator.down();
			return;
		}

		penDown = true;
	}

	/***************************************************
	 * color�֘A
	 ****************************************************/

	public void color(Color penColor) {
		if (delegator != null) {
			delegator.color(penColor);
			return;
		}
		this.penColor = penColor;
	}

	public Color color() {
		if (delegator != null) {
			return delegator.color();
		}
		return penColor;
	}

	/***************************************************
	 * �����蔻��֘A
	 ****************************************************/

	public boolean intersects(Turtle target) {
		if (delegator != null) {
			return delegator.intersects(target);
		}

		if (!isShow() || !target.isShow()) {
			return false;
		}

		this.reshape();
		target.reshape();

		Rectangle2D r1 = this.rotatedBounds();
		Rectangle2D r2 = target.rotatedBounds();
		// return r1.intersects(r2);

		Rectangle2D intersection = r1.createIntersection(r2);
		if (intersection.isEmpty()) {
			return false;
		}
		return this.intersectsBounds(intersection) && target.intersectsBounds(intersection);
	}

	public boolean contains(double x, double y) {
		if (delegator != null) {
			return delegator.contains(x, y);
		}

		if (!isShow()) {
			return false;
		}

		reshape();
		Rectangle2D rect = shape().getBounds();
		return rect.contains(x, y);
	}

	protected boolean intersectsBounds(Rectangle2D bounds) {
		if (looks() instanceof ImageTurtle) {
			return true;
		}
		return shape().intersects(bounds);
	}

	protected Shape rotatedShape() {
		AffineTransform transform = new AffineTransform();
		transform.rotate(theta(), balanceX(), balanceY());
		return transform.createTransformedShape(bounds());
	}

	/***************************************************
	 * �L�[���͊֘A
	 ****************************************************/

	private Map<Integer, Boolean> pressing = new HashMap<Integer, Boolean>();

	public void keyPressed(KeyEvent e) {
		keyEvent = e;
		inputCaptured = false;
		keyPressed(e.getKeyCode());
		pressing.put(e.getKeyCode(), true);
		for (InputTurtle input : inputTurtles) {
			input.captureText(e);
		}
	}

	void keyPressed(int keyCode) {
	}

	public void keyReleased(KeyEvent e) {
		pressing.put(e.getKeyCode(), false);
		keyReleased(e.getKeyCode());
	}

	void keyReleased(int keyCode) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public int key() {
		inputCaptured = true;
		if (keyEvent != null) {
			return keyEvent.getKeyCode();
		} else {
			return -1;
		}
	}

	/**
	 * use keyDown(int)
	 * 
	 * @deprecated
	 */
	public boolean keyDown() {
		inputCaptured = true;
		return keyEvent != null;
	}

	private void resetKey() {
		if (inputCaptured) {
			keyEvent = null;
		}
	}

	public boolean keyDown(int keycode) {
		if (pressing.containsKey(keycode)) {
			return pressing.get(keycode);
		}
		return false;
	}

	/***************************************************
	 * �}�E�X���͊֘A
	 ****************************************************/

	// --- implements MouseListener ---
	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	private boolean leftMousePressing = false;
	private boolean rightMousePressing = false;

	public void mousePressed(MouseEvent e) {
		mouseEvent = e;
		inputCaptured = false;
		if (e.getButton() == MouseEvent.BUTTON1) {
			leftMousePressing = true;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			rightMousePressing = true;
		}
		mousePressed(e.getX(), e.getY());
	}

	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			leftMousePressing = false;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			rightMousePressing = false;
		}
		mouseReleased(e.getX(), e.getY());
	}

	// --- implements MouseMotionListener ---

	public void mouseDragged(MouseEvent e) {
		mouseMotionEvent = e;
	}

	public void mouseMoved(MouseEvent e) {
		mouseMotionEvent = e;
	}

	// --- original method ---

	void mousePressed(int x, int y) {
	}

	void mouseReleased(int x, int y) {
	}

	public int mouseX() {
		inputCaptured = true;
		if (mouseEvent != null) {
			return mouseEvent.getX();
		} else if (mouseMotionEvent != null) {
			return mouseMotionEvent.getX();
		} else {
			return 0;
		}
	}

	public int mouseY() {
		inputCaptured = true;
		if (mouseEvent != null) {
			return mouseEvent.getY();
		} else if (mouseMotionEvent != null) {
			return mouseMotionEvent.getY();
		} else {
			return 0;
		}
	}

	public boolean mouseClicked() {
		return leftMouseClicked() || rightMouseClicked();
	}

	public boolean mouseDown() {
		return leftMouseDown() || rightMouseDown();
	}

	public boolean leftMouseClicked() {
		inputCaptured = true;
		return mouseEvent == null ? false : mouseEvent.getButton() == MouseEvent.BUTTON1;
	}

	public boolean leftMouseDown() {
		return leftMousePressing;
	}

	public boolean rightMouseClicked() {
		inputCaptured = true;
		return mouseEvent == null ? false : mouseEvent.getButton() == MouseEvent.BUTTON3;
	}

	public boolean rightMouseDown() {
		return rightMousePressing;
	}

	public boolean doubleClick() {
		inputCaptured = true;
		return mouseEvent == null ? false : mouseEvent.getClickCount() == 2;
	}

	private void resetMouse() {
		if (inputCaptured) {
			mouseEvent = null;
		}
	}

	/***************************************************
	 * �W�����o�͊֘A
	 ****************************************************/

	/**
	 * @param console
	 *            the console to set
	 */
	public static void setConsole(ConsoleTextArea console) {
		Turtle.console = console;
	}

	private PrintStream getOut() {
		if (console != null) {
			return console.out;
		} else {
			return System.out;
		}
	}

	private InputStream getIn() {
		if (console != null) {
			return console.in;
		} else {
			return System.in;
		}
	}

	public String inputString() {
		String returnString = null;
		try {
			InputStreamReader isr = new InputStreamReader(getIn());
			BufferedReader br = new BufferedReader(isr);
			returnString = br.readLine();
			return returnString;
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// added by macchan 10/8
	public double inputDouble() {
		double returnDouble = 0;
		returnDouble = Double.parseDouble(inputString());
		return returnDouble;
	}

	public int input() {
		int returnInt = 0;
		returnInt = Integer.parseInt(inputString());
		return returnInt;
	}

	public void print(Object o) {
		printlnInternal(o);
	}

	public void print(int x) {
		printlnInternal(new Integer(x));
	}

	public void print(double x) {
		printlnInternal(new Double(x));
	}

	public void print(boolean x) {
		printlnInternal(new Boolean(x));
	}

	private void printlnInternal(Object o) {
		getOut().println(o);
	}

	public void printNoln(Object o) {
		printInternal(o);
	}

	public void printNoln(int x) {
		printInternal(new Integer(x));
	}

	public void printNoln(double x) {
		printInternal(new Double(x));
	}

	public void printNoln(boolean x) {
		printInternal(new Boolean(x));
	}

	private void printInternal(Object o) {
		getOut().print(o);
	}

	/***************************************************
	 * �t�@�C���̃Z�[�u�C���[�h
	 ****************************************************/

	public void saveToFile(String filename, String text) {
		try {
			FileWriter fw = new FileWriter(filename);
			fw.append(text);
			fw.close();
		} catch (Exception ex) {
			// throw new RuntimeException(ex);
			print("�������݂Ɏ��s���܂��� " + ex.getMessage());
		}
	}

	public String loadFromFile(String filename) {
		return loadFromFile(filename, null);
	}

	public String loadFromFile(String filename, String enc) {
		try {
			URL url = CResourceFinder.getResource(filename, getCaller());

			BufferedReader br;
			if (enc == null) {
				br = new BufferedReader(new InputStreamReader(url.openStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(url.openStream(), enc));
			}
			StringBuffer buf = new StringBuffer();
			String line = "";
			while ((line = br.readLine()) != null) {
				if (buf.length() > 0) {
					buf.append("\n");
				}
				buf.append(line);
			}
			br.close();
			return buf.toString();
		} catch (Exception ex) {
			// throw new RuntimeException(ex);
			print("�ǂݍ��݂Ɏ��s���܂��� " + ex.getMessage());
			return "";
		}
	}

	private Class<?> getCaller() {
		try {
			StackTraceElement[] elements = new Exception().getStackTrace();
			if (elements.length < 3) {
				throw new RuntimeException();
			}
			return Class.forName(elements[2].getClassName());
		} catch (Exception ex) {
			return Class.class;
		}
	}

	/***************************************************************************
	 * �����_���֘A
	 **************************************************************************/

	private Random random = new Random();

	public void setRandomSeed(long seed) {
		random = new Random(seed);
	}

	/**
	 * 0����max(�͊܂܂Ȃ��j�܂ł̃����_���Ȓl��Ԃ��܂��D
	 */
	public int random(int max) {
		return random.nextInt(max);
	}

	/***************************************************
	 * Sleep�֘A
	 ****************************************************/

	public void sleep(double second) {
		synchronized (instanciationLock) {
			if (isinstanciation) {
				throw new RuntimeException("�^�[�g���������ɃA�j���[�V�������Ă΂�܂���");
			}

			try {
				Thread.sleep((long) (second * 1000d));
			} catch (InterruptedException ex) {
				throw new RuntimeException("Interrupted By User");
			}
		}
	}

	/***************************************************
	 * paint�֘A
	 ****************************************************/

	public void update() {
		window.canvas().repaint();
		resetKey();
		resetMouse();
	}

	public void paint(Graphics2D g) {
		if (show) {
			reshape();
			draw(g);
		}
	}

	public void draw(Graphics2D g) {
		locus.paint(g);

		if (looks instanceof ImageTurtle) {
			ImageTurtle it = (ImageTurtle) looks;
			g.drawImage(it.image(), it.createTransformOp(theta(), width(), height(), rotatedWidth(), rotatedHeight()),
					(int) rotatedMinX(), (int) rotatedMinY());
			return;
		}

		shape.paint(g);
	}

	/***************************************************
	 * Start�֘A
	 ****************************************************/

	public void start() {
	}

	/***************************************************
	 * Transform �`��ϊ��֘A
	 ****************************************************/

	protected boolean dirty() {
		return dirty;
	}

	protected void dirty(boolean dirty) {
		this.dirty = dirty;
	}

	private Rectangle2D originalBounds() {
		return originalShape.getBounds();
	}

	protected void reshape() {
		if (shape() != null && dirty) {
			doTransform();
			dirty = false;
		}
	}

	protected synchronized void doTransform() {

		// ���Ɍ��ݕϊ��ς݂Ȃ��U���ɖ߂�
		if (currentTransform != null) {
			try {
				AffineTransform reverse = currentTransform.createInverse();
				shape.transform(reverse);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// ����
		AffineTransform transform = createTransform();
		shape.transform(transform);
		currentTransform = transform;
	}

	protected AffineTransform createTransform() {
		AffineTransform transform = new AffineTransform();

		Rectangle2D r = shape.getBounds();
		Scale scale = new Scale(r.getWidth(), r.getHeight(), width(), height());

		// �t���ɂȂ��Ă��܂�
		transform.rotate(theta(), balanceX(), balanceY());
		transform.translate(balanceX(), balanceY());
		transform.scale(scale.x(), scale.y());
		transform.translate(-balanceX(), -balanceY());
		transform.translate(x(), y());
		transform.translate(-r.getCenterX(), -r.getCenterY());

		return transform;
	}

	/***************************************************
	 * List�ɓ������悤�ɁCimage������Ă����悤�ɂ���D(2012/01/08)
	 ****************************************************/

	private static final BufferedImage nullImage = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);

	public BufferedImage image() {
		if (delegator != null) {
			return delegator.image();
		} else {
			return nullImage;
		}
	}
}

/***************************************************
 * 
 * Class DefaultTurtle.
 * 
 ****************************************************/

class DefaultTurtle extends Turtle {
	public DefaultTurtle() {
		down();
	}
}

/***************************************************
 * 
 * Class Scale.
 * 
 ****************************************************/

class Scale {

	private double x = 1.0;

	private double y = 1.0;

	public Scale(Dimension2D d1, Dimension2D d2) {
		this(d1.getWidth(), d1.getHeight(), d2.getWidth(), d2.getHeight());
	}

	public Scale(double w1, double h1, double w2, double h2) {
		this(w2 / w1, h2 / h1);
	}

	public Scale(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double x() {
		return x;
	}

	public void x(double x) {
		this.x = x;
	}

	public double y() {
		return y;
	}

	public void y(double y) {
		this.y = y;
	}

	public void add(Scale scale) {
		x = x * scale.x;
		y = y * scale.y;
	}

}

/***************************************************
 * 
 * Class DoubleDimension.
 * 
 ****************************************************/

class DoubleDimension extends Dimension2D {

	private double width = 0d;

	private double height = 0d;

	/**
	 * Constructor.
	 */
	public DoubleDimension(double width, double height) {
		setSize(width, height);
	}

	/**
	 * Constructor for copy.
	 */
	public DoubleDimension(Dimension2D dimension) {
		this(dimension.getWidth(), dimension.getHeight());
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
	}

	public Object clone() {
		return new DoubleDimension(width, height);
	}

}

/***************************************************
 * 
 * Class RelativePoint.
 * 
 ****************************************************/

class RelativePoint {

	private double xMagnification = 0.5d;
	private double yMagnification = 0.5d;

	/**
	 * Constructor.
	 */
	public RelativePoint(double xMagnification, double yMagnification) {
		setMagnification(xMagnification, yMagnification);
	}

	public Point2D getPoint(Rectangle2D rectangle) {
		double x = rectangle.getX();
		double y = rectangle.getY();
		double centerX = x + rectangle.getWidth() * xMagnification;
		double centerY = y + rectangle.getHeight() * yMagnification;
		return new Point2D.Double(centerX, centerY);
	}

	public void setMagnification(double xMagnification, double yMagnification) {
		this.xMagnification = xMagnification;
		this.yMagnification = yMagnification;
	}

}

/***************************************************
 * 
 * Class Line.
 * 
 ****************************************************/

class Line {

	private Point2D p1 = null;

	private Point2D p2 = null;

	private Color color = null;

	/**
	 * Constructor.
	 */
	public Line(Point2D p1, Point2D p2, Color color) {
		this(p1.getX(), p1.getY(), p2.getX(), p2.getY(), color);
	}

	/**
	 * Constructor.
	 */
	public Line(double p1X, double p1Y, double p2X, double p2Y, Color color) {
		this.p1 = new Point2D.Double(p1X, p1Y);
		this.p2 = new Point2D.Double(p2X, p2Y);
		this.color = color;
	}

	/**
	 * Constructor for copy.
	 */
	public Line(Line line) {
		p1 = new Point2D.Double(line.p1.getX(), line.p1.getY());
		p2 = new Point2D.Double(line.p2.getX(), line.p2.getY());
		color = line.color;
	}

	public void transform(AffineTransform transform) {
		transform.transform(p1, p1);
		transform.transform(p2, p2);
	}

	public Line2D getLine2D() {
		return new Line2D.Double(p1, p2);
	}

	public void paint(Graphics2D g) {
		Color originalColor = g.getColor();
		g.setColor(color);
		g.draw(getLine2D());
		g.setColor(originalColor);
	}

}

/***************************************************
 * 
 * Class LineList.
 * 
 ****************************************************/

class LineList {

	private boolean dirty = false;

	private Rectangle2D bounds = new Rectangle2D.Double(0, 0, 0, 0);

	private List lines = new ArrayList();

	/**
	 * Constructor.
	 */
	public LineList() {
	}

	/**
	 * Constructor for copy.
	 */
	public LineList(LineList lines) {
		Iterator i = lines.lines.iterator();
		while (i.hasNext()) {
			Line line = (Line) i.next();
			this.lines.add(new Line(line));
			dirty = true;
		}
	}

	public synchronized void add(Line line) {
		lines.add(line);
		dirty = true;
	}

	public synchronized void remove(Line line) {
		lines.remove(line);
		dirty = true;
	}

	public synchronized void transform(AffineTransform transform) {
		Iterator i = lines.iterator();
		while (i.hasNext()) {
			Line line = (Line) i.next();
			line.transform(transform);
		}
		dirty = true;
	}

	public synchronized void paint(Graphics2D g) {
		Iterator i = lines.iterator();
		while (i.hasNext()) {
			Line line = (Line) i.next();
			line.paint(g);
		}
	}

	public synchronized Rectangle2D getBounds() {

		if (dirty) {
			synchronized (this) {
				Rectangle2D allBounds = null;
				Iterator i = lines.iterator();
				while (i.hasNext()) {
					Line line = (Line) i.next();
					Rectangle2D bounds = line.getLine2D().getBounds2D();
					if (allBounds == null) {
						allBounds = bounds;
					} else {
						allBounds = allBounds.createUnion(bounds);
					}
				}
				if (allBounds == null) {
					allBounds = new Rectangle2D.Double(0, 0, 0, 0);
				}

				bounds = allBounds;
				dirty = false;
			}
		}

		return bounds;

	}

	public synchronized boolean intersects(Rectangle2D rectangle) {
		Iterator i = lines.iterator();
		while (i.hasNext()) {
			Line line = (Line) i.next();
			if (rectangle.intersectsLine(line.getLine2D())) {
				return true;
			}
		}
		return false;
	}

}
