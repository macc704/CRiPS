/*
 * @(#)GUI.java	1.13 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright (c) 1997-1999 by Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

package nd.com.sun.tools.example.debug.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import nd.com.sun.tools.example.debug.bdi.ExecutionManager;
// import nd.novicedebugger.NBlockViewTool;
import nd.novicedebugger.NDebuggerManager;
import nd.novicedebugger.NNoviceDebugger;
import nd.novicedebugger.NVariableTool;
import clib.common.system.CJavaSystem;
import clib.common.thread.ICTask;
import clib.view.actions.CAction;
import clib.view.actions.CActionUtils;
import com.sun.jdi.VirtualMachine;

/*
 * コメントはNoviceDebuggerクラスへ移動しました（松）
 */
public class GUI extends JPanel {

	private Font fixedFont = new Font("MS UI Gothic", Font.PLAIN, 12);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static int CTRL_MASK = KeyEvent.CTRL_MASK;
	static {
		if (CJavaSystem.getInstance().isMac()) {
			CTRL_MASK = KeyEvent.META_MASK;
		}
	}

	private static JFrame frame;

	// コマンドライン
	private CommandTool cmdTool;
	// コンソール
	private ApplicationTool appTool;
	// ソースビュー
	private SourceTool srcTool;
	// 変数ビュー
	private NVariableTool varTool;
	// ブロックビュー
	// private NBlockViewTool blockTool;
	
	// 実行環境
	private final Environment env = new Environment();
	
	// ブロックビューとソースビューの切り替え表示用
	// private JPanel executiionView;
	// private CardLayout cardLayout;

	// ディレクトリツリー
	// private SourceTreeTool sourceTreeTool;
	// クラスツリー
	// private ClassTreeTool classTreeTool;
	// private ThreadTreeTool threadTreeTool;
	// スタックツール
	// private StackTraceTool stackTool;
	// private MonitorTool monitorTool;


	public GUI() {
		
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(5, 5, 5, 5));

		JDBToolBar toolbar = new JDBToolBar(env);
		add(toolbar, BorderLayout.NORTH);
		// toolbar.setOrientation(JToolBar.VERTICAL);
		// add(toolbar, BorderLayout.EAST);

		JPanel srcPanel = new JPanel();
		srcPanel.setLayout(new BorderLayout());
		srcPanel.setBorder(BorderFactory.createTitledBorder("ソースビュー"));
		srcTool = new SourceTool(env);
		env.setSrcTool(srcTool);
		srcTool.setTextFont(fixedFont);
		srcPanel.add(srcTool);
		srcPanel.setPreferredSize(new java.awt.Dimension(500, 300));
		
//		blockTool = new NBlockViewTool(env);
//		blockTool.setPreferredSize(new java.awt.Dimension(500, 300));
//		env.setBlockTool(blockTool);

//		executiionView = new JPanel();
//		cardLayout = new CardLayout();
//		executiionView.setLayout(cardLayout);
//		executiionView.setPreferredSize(new java.awt.Dimension(500,300));
//		executiionView.add(srcTool, "src");
//		executiionView.add(blockTool, "block");
//		cardLayout.first(executiionView);
		
		// stackTool = new StackTraceTool(env);
		// stackTool.setPreferredSize(new java.awt.Dimension(500, 100));

		// monitorTool = new MonitorTool(env);
		// monitorTool.setPreferredSize(new java.awt.Dimension(500, 50));

		// JSplitPane right = new JSplitPane(JSplitPane.VERTICAL_SPLIT, srcTool,
		// new JSplitPane(JSplitPane.VERTICAL_SPLIT, stackTool, monitorTool));
		// JSplitPane right = new JSplitPane(JSplitPane.VERTICAL_SPLIT, srcTool,
		// stackTool);

		// sourceTreeTool = new SourceTreeTool(env);
		// sourceTreeTool.setPreferredSize(new java.awt.Dimension(200, 450));

		// classTreeTool = new ClassTreeTool(env);
		// classTreeTool.setPreferredSize(new java.awt.Dimension(200, 450));

		// threadTreeTool = new ThreadTreeTool(env);
		// threadTreeTool.setPreferredSize(new java.awt.Dimension(200, 450));

		// JTabbedPane treePane = new JTabbedPane(JTabbedPane.BOTTOM);
		// treePane.addTab("Source", null, sourceTreeTool);
		// treePane.addTab("Classes", null, classTreeTool);
		// treePane.addTab("Threads", null, threadTreeTool);
		// JSplitPane centerTop = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		// treePane, right);

		cmdTool = new CommandTool(env);
		cmdTool.setPreferredSize(new java.awt.Dimension(700, 150));

		JPanel appPanel = new JPanel();
		appPanel.setLayout(new BorderLayout());
		appPanel.setBorder(BorderFactory.createTitledBorder("コンソール"));
		appTool = new ApplicationTool(env);
		appPanel.setPreferredSize(new java.awt.Dimension(500, 200));
		appPanel.add(appTool);

		JPanel varPanel = new JPanel();
		varPanel.setLayout(new BorderLayout());
		varPanel.setBorder(BorderFactory.createTitledBorder("変数ビュー"));
		varTool = new NVariableTool(env);
		env.setVarTool(varTool);
		varPanel.add(varTool);

		JSplitPane views = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, srcPanel, varPanel);
		views.setContinuousLayout(true);
		views.setResizeWeight(0.4d);
		JSplitPane main = new JSplitPane(JSplitPane.VERTICAL_SPLIT, views, appPanel);
		main.setContinuousLayout(true);
		
		add(main, BorderLayout.CENTER);

	}

	// 使用方法
	private static void usage() {
		String separator = File.pathSeparator;
		System.out.println("Usage: " + NNoviceDebugger.NAME
				+ " <options> <class> <arguments>");
		System.out.println();
		System.out.println("where options include:");
		System.out
				.println("    -help             print out this message and exit");
		System.out.println("    -sourcepath <directories separated by \""
				+ separator + "\">");
		System.out
				.println("                      list directories in which to look for source files");
		System.out.println("    -remote <hostname>:<port-number>");
		System.out
				.println("                      host machine and port number of interpreter to attach to");
		System.out.println("    -dbgtrace [flags] print info for debugging "
				+ NNoviceDebugger.NAME);
		System.out.println();
		System.out.println("options forwarded to debuggee process:");
		System.out.println("    -v -verbose[:class|gc|jni]");
		System.out.println("                      turn on verbose mode");
		System.out.println("    -D<name>=<value>  set a system property");
		System.out.println("    -classpath <directories separated by \""
				+ separator + "\">");
		System.out
				.println("                      list directories in which to look for classes");
		System.out
				.println("    -X<option>        non-standard debuggee VM option");
		System.out.println();
		System.out
				.println("<class> is the name of the class to begin debugging");
		System.out
				.println("<arguments> are the arguments passed to the main() method of <class>");
		System.out.println();
		System.out.println("For command help type 'help' at "
				+ NNoviceDebugger.NAME + " prompt");
	}

	/**
	 * @return the varTool
	 */
	public NVariableTool getVarTool() {
		return varTool;
	}

	// メインメソッド
	public void run(String args[]) {
		// String remote = null;
		String clsName = "";
		String progArgs = "";
		String javaArgs = "";
		// boolean verbose = false; //### Not implemented.

		// コンテキストマネージャ取得
		ContextManager context = env.getContextManager();
		// 実行マネージャ取得
		ExecutionManager runtime = env.getExecutionManager();

		// コマンドライン引数解析
		for (int i = 0; i < args.length; i++) {
			String token = args[i];
			// dbgtrace引数
			if (token.equals("-dbgtrace")) {
				if ((i == args.length - 1)
						|| !Character.isDigit(args[i + 1].charAt(0))) {
					runtime.setTraceMode(VirtualMachine.TRACE_ALL);
				} else {
					String flagStr = args[++i];
					runtime.setTraceMode(Integer.decode(flagStr).intValue());
				}
				// X引数
			} else if (token.equals("-X")) {
				System.out
						.println("Use 'java -X' to see the available non-standard options");
				System.out.println();
				usage();
				System.exit(1);
				// それ以外
			} else if (
			// 標準VMオプション
			token.equals("-v")
					|| token.startsWith("-v:")
					|| // -v[:...]
					token.startsWith("-verbose")
					|| // -verbose[:...]
					token.startsWith("-D")
					||
					// 非標準オプション
					token.startsWith("-X")
					||
					// 旧式オプション
					token.equals("-noasyncgc") || token.equals("-prof")
					|| token.equals("-verify") || token.equals("-noverify")
					|| token.equals("-verifyremote")
					|| token.equals("-verbosegc") || token.startsWith("-ms")
					|| token.startsWith("-mx") || token.startsWith("-ss")
					|| token.startsWith("-oss")) {
				javaArgs += token + " ";
				// sourcepath引数
			} else if (token.equals("-sourcepath")) {
				if (i == (args.length - 1)) {
					System.out.println("No sourcepath specified.");
					usage();
					System.exit(1);
				}
				env.getSourceManager().setSourcePath(new SearchPath(args[++i]));
				// classpath引数
			} else if (token.equals("-classpath")) {
				if (i == (args.length - 1)) {
					System.out.println("No classpath specified.");
					usage();
					System.exit(1);
				}
				env.getClassManager().setClassPath(new SearchPath(args[++i]));
				// remote引数
			} else if (token.equals("-remote")) {
				if (i == (args.length - 1)) {
					System.out.println("No remote specified.");
					usage();
					System.exit(1);
				}
				env.getContextManager().setRemotePort(args[++i]);
				// help引数
			} else if (token.equals("-help")) {
				usage();
				System.exit(0);
				// version引数
			} else if (token.equals("-version")) {
				System.out.println(NNoviceDebugger.NAME + " version "
						+ NNoviceDebugger.VERSION);
				System.exit(0);
				// 不正なオプション
			} else if (token.startsWith("-")) {
				System.out.println("invalid option: " + token);
				usage();
				System.exit(1);
				// 引数なし
			} else {
				clsName = token;
				for (i++; i < args.length; i++) {
					progArgs += args[i] + " ";
				}
				break;
			}
		}

		context.setMainClassName(clsName);
		context.setProgramArguments(progArgs);
		context.setVmArguments(javaArgs);

		// ソースファイル指定
		String sourcename = clsName + ".java";
		env.viewSource(sourcename);
		
		// Force Cross Platform L&F
		try {
			// Matsuzawa
			// UIManager.setLookAndFeel(UIManager
			// .getCrossPlatformLookAndFeelClassName());
			// If you want the System L&F instead, comment out the above line
			// and
			// uncomment the following:
			// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception exc) {
			System.err.println("Error loading L&F: " + exc);
		}

		// ウィンドウ生成
		frame = new JFrame();

		// added by matsuzawa
		JMenuBar menubar = new JMenuBar();
		{
			JMenu menu = new JMenu("操作");
			menubar.add(menu);

			{
				CAction action = CActionUtils.createAction("ステップ",
						new ICTask() {
							public void doTask() {
								NDebuggerManager.fireStepPressed();
								new CommandInterpreter(env, true)
										.executeCommand("step");
							}
						});
				action.putValue(Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(KeyEvent.VK_S, CTRL_MASK));
				menu.add(action);
			}

			{
				CAction action = CActionUtils.createAction("変数順序反転",
						new ICTask() {
							public void doTask() {
								getVarTool().setReversed(
										!getVarTool().isReversed());
							}
						});
				action.putValue(Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(KeyEvent.VK_R, CTRL_MASK));
				menu.add(action);
			}
			
//			{
//				CAction action = CActionUtils.createAction("ビュー切り替え", 
//						new ICTask() {
//							public void doTask() {
//								// ログ取る
//								cardLayout.next(executiionView);
//							}
//						});
//				action.putValue(Action.ACCELERATOR_KEY,
//						KeyStroke.getKeyStroke(KeyEvent.VK_Q, CTRL_MASK));
//				menu.add(action);
//			}
		}
		frame.setJMenuBar(menubar);

		// 背景色
		frame.setBackground(Color.lightGray);
		// ウィンドウタイトル
		// frame.setTitle(windowBanner);
		frame.setTitle(NNoviceDebugger.WINDOWTITLE + NNoviceDebugger.VERSION
				+ " デバッグ中 - " + sourcename);// changed
		// by
		// matsuzawa

		// メニューバー
		// frame.setJMenuBar(new JDBMenuBar(env));
		// メインパネル設置
		frame.setContentPane(this);

		// ウィンドウリスナー
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// env.terminate();
				env.getExecutionManager().endSession();
			}
		});

		// 内部領域からサイズ設定
		frame.pack();
		// 表示位置設定
		frame.setBounds(305, frame.getY(), frame.getWidth(), frame.getHeight());
		// 表示
		// frame.show();
		NDebuggerManager.fireDebugStarted();
		frame.setVisible(true);

		// env.getBlockTool().loadXml(env.getSourceManager().getSourcePath().resolve(clsName + ".xml").getPath().toString());
	}

	public Environment getEnv() {
		return env;
	}

	public static void dead() {
		NDebuggerManager.fireDebugFinished();
		
		frame.dispose();
	}

	public boolean isRunning() {
		return frame != null && frame.isVisible();
	}

	public JFrame getFrame() {
		return frame;
	}
}
