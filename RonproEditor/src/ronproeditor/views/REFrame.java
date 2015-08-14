/*
 * REFrame.java
 * Created on 2007/09/14 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor.views;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultEditorKit;

import ronproeditor.REApplication;
import ronproeditor.RESourceManager;
import ronproeditor.helpers.ConsoleTextPane;
import clib.common.system.CJavaSystem;
import clib.preference.model.CAbstractPreferenceCategory;
import clib.view.app.javainfo.CJavaInfoPanels;

/**
 * REFrame
 */
@SuppressWarnings("serial")
public class REFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	protected REApplication application = null;

	private JMenuBar menuBar = new JMenuBar();

	private JPanel editorContainer = new JPanel();
	private ConsoleTextPane console = new ConsoleTextPane();
	private RESourceEditor editor;

	private RESourceEditorCategory category = new RESourceEditorCategory();

	private static int CTRL_MASK = KeyEvent.CTRL_MASK;
	static {
		if (CJavaSystem.getInstance().isMac()) {
			CTRL_MASK = KeyEvent.META_MASK;
		}
	}

	/**
	 * �R���X�g���N�^ ������initialize()���Ă΂Ȃ�����
	 */
	public REFrame(REApplication application) {
		assert application != null;
		this.application = application;
	}

	/**
	 * �t���[���̏��������s���܂��B
	 */
	public void initialize() {
		initializeWindow();
		initializeAction();
		initializeMenu();
		initializeView(); // Action�����ׂĐ�������Ă���View������������K�v�A��
		initializeListeners();

		application.getPreferenceManager().putCategory(category);
		editorStateChanged();
	}

	protected void initializeWindow() {
		// this.setTitle(TITLE);
		// fwSetWindowIcon("image/LogoIcon16.gif"); // �A�C�R��
		// fwSetWindowSizeToMaximum(); // �E�B���h�E�T�C�Y
		this.setSize(800, 600);
		fwSetWindowAtCenter();

		// �����Ƃ��ɏI������悤�ɂ���
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				application.doExit();
			}
		});
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}

	protected void initializeView() {

		JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		getContentPane().add(pane);

		JSplitPane verticalSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		verticalSplitter.setDividerLocation(400);
		// getContentPane().add(verticalSplitter);

		JSplitPane horizontalSplitter = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT);
		horizontalSplitter.setDividerLocation(200);
		verticalSplitter.add(horizontalSplitter, JSplitPane.LEFT);
		JScrollPane scrollconsole = new JScrollPane(console);
		verticalSplitter.add(scrollconsole, JSplitPane.RIGHT);

		REFileTreeNavigator treeNavigator = new REFileTreeNavigator(application);
		JScrollPane scrollTree = new JScrollPane(treeNavigator);
		horizontalSplitter.add(scrollTree, JSplitPane.LEFT);
		editorContainer.setLayout(new BorderLayout());
		horizontalSplitter.add(editorContainer, JSplitPane.RIGHT);

		// debugger = new REDebugNavigator( application );

		// pane.setDividerLocation( 700 );
		pane.add(verticalSplitter);
		// pane.add( debugger );

		// new REDebugNavigator( application );

		// ToolBar ����
		// JToolBar toolBar = new JToolBar();
		// getContentPane().add(toolBar, BorderLayout.NORTH);
		// CFontComboBox font = new CFontComboBox();
		// toolBar.add(new JButton("A"));
		// toolBar.add(font);

		this.pack();
		this.setSize(800, 600);
	}

	protected void initializeListeners() {
		application.getSourceManager().addPropertyChangeListener(
				new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						if (evt.getPropertyName().equals(
								RESourceManager.DOCUMENT_CLOSED)) {
							deleteEditor();
						} else if (evt.getPropertyName().equals(
								RESourceManager.DOCUMENT_OPENED)) {
							createNewEditor();
						}
					}
				});
	}

	/***********************
	 * ���j���[�E�A�N�V������
	 **********************/

	// �u�t�@�C���v
	private JMenu menuFile;
	private Action actionCreateProject;
	private Action actionCreateFile;
	private Action actionRefactoring;
	private Action actionDelete;
	private Action actionSave;
	private Action actionFileCopy;
	private Action actionExport;
	private Action actionRefresh;
	private Action actionExit;

	// �u�ҏW�v
	private JMenu menuEdit;
	private Action actionUndo;
	private Action actionRedo;
	private Action actionCut = new DefaultEditorKit.CutAction();
	private Action actionCopy = new DefaultEditorKit.CopyAction();
	private Action actionPaste = new DefaultEditorKit.PasteAction();

	// �uJava�v
	private JMenu menuJava;
	private Action actionCompile;
	private Action actionRun;
	private Action actionDebugRun; // add hakamata
	private Action actionKill;
	private Action actionFormat;

	// �uTools�v
	private JMenu menuTools;
	private Action actionOpenBlockEditor;
	private Action actionOpenFlowViewer;
	private Action actionOpenGeneRefBrowser;
	// private JCheckBoxMenuItem useRSSystem;
	private Action actionOpenPPV;
	private Action actionOpenCocoViewer; // add hirao
	private Action actionCreateCocoData; // add hirao
	private Action actionClearCash; // add hirao
	private Action actionBytecode;
	private Action actionStartCheCoPro; // CheCoPro(kato)
	

	// �uHelp�v
	private JMenu menuHelp;
	private Action actionOpenPreference;
	private Action actionAbout;

	// private Action actionMakeLog;

	/*******************
	 * ���j���[�̏�����
	 *******************/

	private void initializeMenu() {
		// -- ���j���[�o�[�̐ݒ� --
		setJMenuBar(menuBar);

		initializeFileMenu();
		initializeEditMenu();
		initializeJavaMenu();
		initializeToolsMenu();
		initializeHelpMenu();
	}

	/**
	 * �t�@�C�����j���[�̏�����
	 */
	private void initializeFileMenu() {
		// ���j���[�̒ǉ�
		menuFile = new JMenu("File");
		menuBar.add(menuFile);

		// �A�N�V�����̒ǉ�
		menuFile.add(actionCreateProject);
		menuFile.add(actionCreateFile);
		menuFile.addSeparator();
		menuFile.add(actionFileCopy);
		menuFile.add(actionRefactoring);
		menuFile.addSeparator();
		menuFile.add(actionDelete);
		menuFile.addSeparator();
		menuFile.add(actionSave);
		menuFile.addSeparator();
		menuFile.add(actionExport);
		menuFile.addSeparator();
		menuFile.add(actionRefresh);
		menuFile.addSeparator();
		menuFile.add(actionExit);
	}

	/**
	 * �ҏW���j���[�̏�����
	 */
	private void initializeEditMenu() {
		// ���j���[�̒ǉ�
		menuEdit = new JMenu("Edit");
		menuBar.add(menuEdit);

		// �A�N�V�����̒ǉ�
		menuEdit.add(actionUndo);
		menuEdit.add(actionRedo);
		menuEdit.addSeparator();

		menuEdit.add(actionCut);
		menuEdit.add(actionCopy);
		menuEdit.add(actionPaste);
	}

	/**
	 * Java���j���[�̏�����
	 */
	private void initializeJavaMenu() {
		// ���j���[�̒ǉ�
		menuJava = new JMenu("Java");
		menuBar.add(menuJava);

		// �A�N�V�����̒ǉ�
		menuJava.add(actionCompile);
		menuJava.add(actionRun);
		menuJava.add(actionDebugRun);
		menuJava.add(actionKill);
		menuJava.addSeparator();
		menuJava.add(actionFormat);
	}

	/**
	 * Tools���j���[�̏�����
	 */
	private void initializeToolsMenu() {
		// ���j���[�̒ǉ�
		menuTools = new JMenu("Tools");
		menuBar.add(menuTools);

		// �A�N�V�����̒ǉ�
		menuTools.add(actionOpenBlockEditor);
		menuTools.add(actionOpenFlowViewer);
		menuTools.add(actionOpenGeneRefBrowser);
		// menuTools.add(useRSSystem);
		menuTools.add(actionOpenPPV);
		menuTools.add(actionOpenCocoViewer);
		menuTools.add(actionCreateCocoData);
		menuTools.add(actionClearCash);
		menuTools.add(actionStartCheCoPro);

		if (CJavaSystem.getInstance().isWindows()) {
			menuTools.addSeparator();
			menuTools.add(actionBytecode);
		}
	}

	private void initializeHelpMenu() {
		// ���j���[�̒ǉ�
		menuHelp = new JMenu("Help");
		menuBar.add(menuHelp);
		// menuHelp.add(actionMakeLog);
		menuHelp.add(actionOpenPreference);
		menuHelp.add(CJavaInfoPanels.createJavaInformationAction());
		menuHelp.addSeparator();
		menuHelp.add(actionAbout);

	}

	/************************
	 * �A�N�V�����̏�����
	 ************************/

	private void initializeAction() {
		initializeFileAction();
		initializeEditAction();
		initializeJavaAction();
		initializeToolsAction();
		initializeHelpAction();
	}

	/**
	 * �u�t�@�C���v�A�N�V�����̏�����
	 */
	private void initializeFileAction() {
		// -- �V�K�v���W�F�N�g
		actionCreateProject = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				application.doCreateProject();
			}
		};
		actionCreateProject.putValue(Action.NAME, "New Project");
		actionCreateProject.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_P, CTRL_MASK));

		// -- �V�K����
		actionCreateFile = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				application.doCreateFile();
			}
		};
		actionCreateFile.putValue(Action.NAME, "New File(Class)");
		actionCreateFile.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_N, CTRL_MASK));

		// //-- Refactor�v���W�F�N�g
		// actionRefactoringProject = new AbstractAction() {
		// public void actionPerformed(ActionEvent e) {
		// application.doRefactorProjectName();
		// }
		// };
		// actionRefactoringProject.putValue(Action.NAME, "Remane Project");
		// // actionRefactoringProject.putValue(Action.ACCELERATOR_KEY,
		// KeyStroke
		// // .getKeyStroke(KeyEvent.VK_P, CTRL_MASK));
		//
		// // -- Refactor�t�@�C��
		// actionRefactoringFile = new AbstractAction() {
		// public void actionPerformed(ActionEvent e) {
		// application.doRefactorFileName();
		// }
		// };
		// actionRefactoringFile.putValue(Action.NAME, "Rename File(Class)");
		// // actionRefactoringFile.putValue(Action.ACCELERATOR_KEY, KeyStroke
		// // .getKeyStroke(KeyEvent.VK_N, CTRL_MASK));

		// -- Refactor�v���W�F�N�g
		actionRefactoring = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				application.doRefactoring();
			}
		};
		actionRefactoring.putValue(Action.NAME, "Rename");
		actionRefactoring.putValue(
				Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_R, CTRL_MASK
						| KeyEvent.SHIFT_MASK));

		// -- File Copy
		actionFileCopy = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				application.doFileCopy();
			}
		};
		actionFileCopy.putValue(Action.NAME, "File Copy");
		actionFileCopy.putValue(
				Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_C, CTRL_MASK
						| KeyEvent.SHIFT_MASK));
		actionFileCopy.setEnabled(false);

		// -- Delete
		actionDelete = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				application.doDelete();
			}
		};
		actionDelete.putValue(Action.NAME, "Delete");
		actionDelete.putValue(
				Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_D, CTRL_MASK
						| KeyEvent.SHIFT_MASK));

		// -- �ۑ�
		actionSave = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				application.doSave();
			}
		};
		actionSave.putValue(Action.NAME, "Save...");
		actionSave.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, CTRL_MASK));
		actionSave.setEnabled(false);

		// -- Export
		actionExport = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				application.doExport();
			}
		};
		actionExport.putValue(Action.NAME, "Export Project");
		// actionExport.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		// KeyEvent.VK_S, CTRL_MASK));
		actionExport.setEnabled(false);

		// -- �X�V
		actionRefresh = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				application.doRefresh();
			}
		};
		actionRefresh.putValue(Action.NAME, "Refresh");
		actionRefresh.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));

		// -- �I��
		actionExit = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				application.doExit();
			}
		};
		actionExit.putValue(Action.NAME, "Exit");
		// actionExit.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		// KeyEvent.VK_Q, CTRL_MASK));
	}

	/**
	 * �u�ҏW�v�A�N�V�����̏�����
	 */
	private void initializeEditAction() {

		// -- Undo
		actionUndo = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				// application.doUndo();
				editor.doUndo();
			}
		};
		actionUndo.putValue(Action.NAME, "Undo");
		actionUndo.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_Z, CTRL_MASK));
		actionUndo.setEnabled(false);

		// -- Redo
		actionRedo = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				// application.doRedo();
				editor.doRedo();
			}
		};
		actionRedo.putValue(Action.NAME, "Redo");
		actionRedo.putValue(
				Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_Z, CTRL_MASK
						| KeyEvent.SHIFT_MASK));
		actionRedo.setEnabled(false);

		// -- Cut
		actionCut.putValue(Action.NAME, "Cut");
		actionCut.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_X, CTRL_MASK));

		// -- Copy
		actionCopy.putValue(Action.NAME, "Copy");
		actionCopy.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_C, CTRL_MASK));

		// -- Paste
		actionPaste.putValue(Action.NAME, "Paste");
		actionPaste.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_V, CTRL_MASK));

	}

	private void initializeJavaAction() {
		// -- Compile
		actionCompile = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				application.doCompile();
			}
		};
		actionCompile.putValue(Action.NAME, "Compile");
		actionCompile.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_E, CTRL_MASK));
		actionCompile.setEnabled(false);

		// -- Run
		actionRun = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				application.doRun();
			}
		};
		actionRun.putValue(Action.NAME, "Run");
		actionRun.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_R, CTRL_MASK));
		actionRun.setEnabled(false);

		// -- DebugRun (add hakamata)
		actionDebugRun = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				application.doDebugRun();
			}
		};
		actionDebugRun.putValue(Action.NAME, "DebugRun");
		actionDebugRun.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_T, CTRL_MASK));
		actionDebugRun.setEnabled(true);

		// -- Kill
		actionKill = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				application.doKillAll();
			}
		};
		actionKill.putValue(Action.NAME, "Kill");
		actionKill.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_SEMICOLON, CTRL_MASK));
		actionKill.setEnabled(true);

		// -- Format
		actionFormat = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				application.doFormat();
			}
		};
		actionFormat.putValue(Action.NAME, "Format");
		actionFormat.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_W, CTRL_MASK));
		actionFormat.setEnabled(false);
	}

	private void initializeToolsAction() {
		// --BlockEditor
		actionOpenBlockEditor = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				application.doOpenBlockEditor();
			}
		};
		actionOpenBlockEditor.putValue(Action.NAME, "Open BlockEditor");
		actionOpenBlockEditor.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_O, CTRL_MASK));
		actionOpenBlockEditor.setEnabled(true);

		// --Flowchart
		actionOpenFlowViewer = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				application.doOpenFlowViewer();
			}
		};
		actionOpenFlowViewer.putValue(Action.NAME, "Open FlowViewer");
		actionOpenFlowViewer.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_P, CTRL_MASK));
		actionOpenFlowViewer.setEnabled(true);

		// --GeneRef
		actionOpenGeneRefBrowser = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				application.doOpenGeneRefBrowser();
			}
		};
		actionOpenGeneRefBrowser.putValue(Action.NAME, "Open GeneRefBrowser");
		actionOpenGeneRefBrowser.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_B, CTRL_MASK));
		actionOpenGeneRefBrowser.setEnabled(true);

		// useRSSystem = new JCheckBoxMenuItem("Use ReflectionTool");
		// useRSSystem.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// application.doChangeUseReflectionToolState();
		// }
		// });
		// useRSSystem.setEnabled(true);

		{
			Action action = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					application.doOpenPPV();
				}
			};
			action.putValue(Action.NAME, "Open PPV");
			// action.putValue(Action.ACCELERATOR_KEY,
			// KeyStroke.getKeyStroke(KeyEvent.VK_B, CTRL_MASK));
			action.setEnabled(true);
			actionOpenPPV = action;
		}

		// add hirao
		{
			Action action = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					application.doCreateCocoData();
				}
			};
			action.putValue(Action.NAME, "Create CocoData");
			action.setEnabled(true);
			actionCreateCocoData = action;
		}

		{
			Action action = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					application.doOpenClearCash();
				}
			};
			action.putValue(Action.NAME, "Clear Cash");
			action.setEnabled(true);
			actionClearCash = action;
		}

		{
			Action action = new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					application.doOpenCocoViewer();
				}
			};
			action.putValue(Action.NAME, "Open CocoViewer");
			action.setEnabled(true);
			actionOpenCocoViewer = action;
		}

		// --ByteCode
		actionBytecode = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				application.doShowBytecode();
			}
		};
		actionBytecode.putValue(Action.NAME, "Lesson Bytecode");
		actionBytecode.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_J, CTRL_MASK));
		actionBytecode.setEnabled(false);
		
		// --CheCoPro
		actionStartCheCoPro = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				application.doStartCheCoPro();
				// actionStartCheCoPro.setEnabled(false);
			}
		};
		actionStartCheCoPro.putValue(Action.NAME, "Start CheCoPro");
		actionStartCheCoPro.setEnabled(true);
	}

	private void initializeHelpAction() {
		// -- Help

		actionOpenPreference = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				application.doOpenPreferencePage();
			}
		};
		actionOpenPreference.putValue(Action.NAME, "Preference");

		actionAbout = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				showApplicationInformationDialog();
			}
		};
		actionAbout.putValue(Action.NAME, "About");
		// actionAbout.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
		// KeyEvent.VK_R, CTRL_MASK));

		// actionMakeLog = new AbstractAction() {
		// public void actionPerformed(ActionEvent arg0) {
		// application.doMakeLogArchive();
		// }
		// };
		// actionMakeLog.putValue(Action.NAME, "MakeLog");

	}

	/*******************
	 * �G�f�B�^�֌W
	 ******************/

	public RESourceEditor getEditor() {
		return editor;
	}

	private void deleteEditor() {
		this.editorContainer.removeAll();
		this.editor = null;
		this.editorContainer.validate();
		this.editorContainer.repaint();
		editorStateChanged();
	}

	public void createNewEditor() {
		this.editor = new RESourceEditor(application);
		this.editor.getViewer().changeFont(category.getSelectedFont());
		this.console.setFont(category.getSelectedFont());
		// menuEdit.addSeparator();
		// for (Action action : this.editor.getActions()) {
		// menuEdit.add(action);
		// }
		this.editorContainer.add(editor.getViewer());
		this.editorContainer.validate();
		editorStateChanged();
	}

	public void editorStateChanged() {
		refreshTitle();
		refreshFileState();
		refreshEditState();
		refreshUndoState();
		refreshJavaState();
	}

	private void refreshTitle() {
		String title = REApplication.APP_NAME + " " + REApplication.VERSION;
		if (application.getSourceManager().hasCurrentFile()) {
			title += " - "
					+ application.getSourceManager().getCurrentFile().getName();
			if (editor.isDirty()) {
				title += "*";
				actionSave.setEnabled(true);
			}
		}
		if (!getTitle().equals(title)) {
			this.setTitle(title);
		}
	}

	private void refreshFileState() {
		actionSave.setEnabled(false);
		if (editor != null && editor.isDirty()) {
			actionSave.setEnabled(true);
		}
		if (application.getSourceManager().getCCurrentProject() != null) {
			actionExport.setEnabled(true);
		}
		// actionRefactoring.setEnabled(editor != null);
		actionFileCopy.setEnabled(editor != null);
	}

	private void refreshEditState() {
		actionCut.setEnabled(editor != null);
		actionCopy.setEnabled(editor != null);
		actionPaste.setEnabled(editor != null);
		actionBytecode.setEnabled(editor != null);
	}

	private void refreshJavaState() {
		actionCompile.setEnabled(editor != null);
		actionRun.setEnabled(editor != null);
		actionFormat.setEnabled(editor != null);
		actionBytecode.setEnabled(editor != null);
	}

	private void refreshUndoState() {
		if (editor != null && editor.undoableEdit() != null) {
			actionUndo.putValue(Action.NAME, "Undo - "
					+ editor.undoableEdit().getUndoPresentationName());
			actionUndo.setEnabled(true);
		} else {
			actionUndo.putValue(Action.NAME, "Undo");
			actionUndo.setEnabled(false);
		}

		if (editor != null && editor.redoableEdit() != null) {
			actionRedo.putValue(Action.NAME, "Redo - "
					+ editor.redoableEdit().getRedoPresentationName());
			actionRedo.setEnabled(true);
		} else {
			actionRedo.putValue(Action.NAME, "Redo");
			actionRedo.setEnabled(false);
		}
	}

	/******************/
	/* ���[�e�B���e�B */
	/******************/

	public ConsoleTextPane getConsole() {
		return console;
	}

	public void showApplicationInformationDialog() {
		String[] message = {
				REApplication.APP_NAME + " " + REApplication.VERSION,
				REApplication.DEVELOPERS, REApplication.COPYRIGHT };
		// Icon icon = new ImageIcon(getIconImage());
		showDialog(REApplication.APP_NAME, message,
				JOptionPane.INFORMATION_MESSAGE, null);
	}

	private void showDialog(String title, Object message, int type, Icon icon) {
		JOptionPane op = new JOptionPane();
		op.setMessage(message);
		op.setMessageType(type);
		op.setIcon(icon);
		JDialog dialog = op.createDialog(this, title);
		dialog.setVisible(true);
	}

	/**
	 * �ΏۂƂȂ�E�C���h�E���ő�̑傫���ɂ��܂�
	 */
	protected final void fwSetWindowSizeToMaximum() {
		int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenH = Toolkit.getDefaultToolkit().getScreenSize().height;

		// �^�X�N�o�[�̂���
		screenH = screenH - 50;

		this.setSize(screenW, screenH);
		this.setLocation(0, 0);
	}

	/**
	 * �ΏۂƂȂ�E�C���h�E����ʂ̒����ɐݒ肵�܂��B
	 */
	protected final void fwSetWindowAtCenter() {
		int width = this.getSize().width;
		int height = this.getSize().height;

		int screenW = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenH = Toolkit.getDefaultToolkit().getScreenSize().height;

		int x = screenW / 2 - width / 2;
		int y = screenH / 2 - height / 2;

		this.setLocation(x, y);
	}

	/**
	 * �E�C���h�E�ɃA�C�R����ݒ肵�܂�
	 */
	protected final void fwSetWindowIcon(String iconPath) {
		URL url = ClassLoader.getSystemClassLoader().getResource(iconPath);
		Image img = Toolkit.getDefaultToolkit().getImage(url);
		this.setIconImage(img);
	}

	/**
	 * path����A�C�R�����擾����
	 */
	protected final ImageIcon fwGetImageIcon(String iconPath) {
		URL url = ClassLoader.getSystemClassLoader().getResource(iconPath);
		Image img = Toolkit.getDefaultToolkit().getImage(url);
		ImageIcon icon = new ImageIcon(img);
		return icon;
	}

	class RESourceEditorCategory extends CAbstractPreferenceCategory {

		private static final long serialVersionUID = 1L;

		private CFontComboBox checkbox = new CFontComboBox();
		private JTextField field = new JTextField("12");

		private JPanel panel = new SourceEditorPreferencePanel();

		public String getName() {
			return "Editor";
		}

		public JPanel getPage() {
			return panel;
		}

		private static final String FONT_LABEL = "editor.font";
		private static final String FONT_SIZE = "editor.size";

		public void load() {

			String fontName;
			if (getRepository().exists(FONT_LABEL)) {
				fontName = getRepository().get(FONT_LABEL);
			} else {
				if (CJavaSystem.getInstance().isMac()) {
					fontName = "Osaka";
				} else {
					if (panel.getFont() != null) {
						fontName = panel.getFont().getName();
					} else {
						fontName = "Dialog";
					}
				}
			}
			checkbox.setSelectedFontByName(fontName);

			int size = 12;
			try {
				if (getRepository().exists(FONT_SIZE)) {
					size = Integer.parseInt(getRepository().get(FONT_SIZE));
				}
			} catch (Exception ex) {
				// do nothing
			}
			field.setText(Integer.toString(size));
			console.setFont(category.getSelectedFont());
			if (editor != null) {
				editor.getViewer().changeFont(getSelectedFont());
			}
		}

		public void save() {
			getRepository().put(FONT_LABEL, getSelectedFont().getName());
			getRepository().put(FONT_SIZE,
					Integer.toString(getSelectedFontSize()));
			if (editor != null) {
				editor.getViewer().changeFont(getSelectedFont());
				console.setFont(getSelectedFont());
			}
		}

		public Font getSelectedFont() {
			// return
			// checkbox.getSelectedFont().deriveFont(getSelectedFontSize());
			return new Font(checkbox.getSelectedFont().getFontName(), checkbox
					.getSelectedFont().getStyle(), getSelectedFontSize());
		}

		private int getSelectedFontSize() {
			try {
				return Integer.parseInt(field.getText());
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
				return 12;
			}
		}

		class SourceEditorPreferencePanel extends JPanel {
			private static final long serialVersionUID = 1L;

			SourceEditorPreferencePanel() {
				// setLayout(new CVerticalFlowLayout());
				setLayout(null);

				checkbox.setBounds(20, 20, 200, 27);
				add(checkbox);

				field.setBounds(20, 50, 70, 27);
				field.setColumns(5);
				add(field);
			}
		}
	}

}
