package controller;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringReader;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import renderable.RenderableBlock;
import slcodeblocks.ParamRule;
import slcodeblocks.PolyRule;
import util.ChangeExtension;
import workspace.BlockCanvas;
import workspace.SearchBar;
import workspace.SearchableContainer;
import workspace.TrashCan;
import workspace.Workspace;
import workspace.WorkspaceEvent;
import workspace.WorkspaceListener;
import a.slab.blockeditor.SBlockEditor;
import a.slab.blockeditor.SBlockEditorListener;
import bc.BCSystem;
import bc.apps.BlockToJavaMain;
import bc.apps.JavaToBlockMain;
import clib.common.filesystem.CFilename;
import clib.view.dialogs.CErrorDialog;
import clib.view.screenshot.CScreenShotTaker;
import codeblocks.BlockConnectorShape;
import codeblocks.BlockGenus;
import codeblocks.BlockLinkChecker;
import codeblocks.CommandRule;
import codeblocks.InfixRule;
import codeblocks.SocketRule;

/**
 * 
 * The WorkspaceController is the starting point for any program using Open
 * Blocks. It contains a Workspace (the block programming area) as well as the
 * Factories (the palettes of blocks), and is responsible for setting up and
 * laying out the overall window including loading some WorkspaceWidgets like
 * the TrashCan.
 * 
 * @author Ricarose Roque
 */

public class WorkspaceController {

	private static String LANG_DEF_FILEPATH;

	private static Element langDefRoot;
	private String selectedJavaFile;

	// flags
	private boolean isWorkspacePanelInitialized = false;

	/** The single instance of the Workspace Controller **/
	protected static Workspace workspace;
	private SBlockEditorListener ronproEditor;

	protected JPanel workspacePanel;
	protected SearchBar searchBar;

	// flag to indicate if a new lang definition file has been set
	private boolean langDefDirty = true;

	// flag to indicate if a workspace has been loaded/initialized
	private static boolean workspaceLoaded = false;

	//
	private JFrame frame;

	private String imagePath = "support/images/";// added by macchan

	public static final int BLOCK_SHOWING = 1;
	public static final int PROJECT_SELECTED = 2;
	public static final int COMPILE_ERROR = 3;
	private int state = PROJECT_SELECTED;

	/**
	 * Constructs a WorkspaceController instance that manages the interaction
	 * with the codeblocks.Workspace
	 * 
	 */
	public WorkspaceController(String imagePath) {
		workspace = Workspace.getInstance();
		this.imagePath = imagePath;// added by macchan
		// workspace.setWorkSpaceController(this);
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(int state) {
		this.state = state;
	}

	/**
	 * return frame
	 * 
	 * @return
	 */
	public JFrame getFrame() {
		return frame;
	}

	public String getSelectedJavaFile() {
		return selectedJavaFile;
	}

	// //////////////////
	// LANG DEF FILE //
	// //////////////////

	/**
	 * Sets the file path for the language definition file, if the language
	 * definition file is located in
	 */
	public void setLangDefFilePath(String filePath) {

		LANG_DEF_FILEPATH = filePath; // do we really need to save the file
		// path?

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc;
		try {
			builder = factory.newDocumentBuilder();

			String langDefLocation = /* workingDirectory + */LANG_DEF_FILEPATH;
			doc = builder.parse(new File(langDefLocation));

			BCSystem.out.println("langDefLocation:" + langDefLocation);

			langDefRoot = doc.getDocumentElement();

			// set the dirty flag for the language definition file
			// to true now that a new file has been set
			langDefDirty = true;

			BCSystem.out.println("langdeffile:" + langDefLocation);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	//added macchan
	public void setLangDefDirty(boolean langDefDirty) {
		this.langDefDirty = langDefDirty;
	}

	/**
	 * Sets the contents of the Lang Def File to the specified String
	 * langDefContents
	 * 
	 * @param langDefContents
	 *            String contains the specification of a language definition
	 *            file
	 */
	public void setLangDefFileString(String langDefContents) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc;
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(new InputSource(new StringReader(
					langDefContents)));
			langDefRoot = doc.getDocumentElement();

			// set the dirty flag for the language definition file
			// to true now that a new file has been set
			langDefDirty = true;

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the Lang Def File to the specified File langDefFile.
	 * 
	 * @param langDefFile
	 *            File contains the specification of the a language definition
	 *            file.
	 */
	public void setLangDefFile(File langDefFile) {
		// LANG_DEF_FILEPATH = langDefFile.getCanonicalPath();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc;
		try {
			builder = factory.newDocumentBuilder();

			doc = builder.parse(langDefFile);

			langDefRoot = doc.getDocumentElement();

			// set the dirty flag for the language definition file
			// to true now that a new file has been set
			langDefDirty = true;

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads all the block genuses, properties, and link rules of a language
	 * specified in the pre-defined language def file.
	 * 
	 * @param root
	 *            Loads the language specified in the Element root
	 */
	public void loadBlockLanguage(Element root) {
		//#matsuzawa 二回WorkspaceListenerが登録されないようにする．
		workspace.clearWorkspaceListenersForBlockLanguage();
		workspace.setLoadingBlockLanguage(true);

		// load connector shapes
		// MUST load shapes before genuses in order to initialize connectors
		// within
		// each block correctly
		BlockConnectorShape.loadBlockConnectorShapes(root);

		// load genuses
		BlockGenus.loadBlockGenera(root);

		// load rules
		BlockLinkChecker.addRule(new CommandRule());
		BlockLinkChecker.addRule(new SocketRule());
		BlockLinkChecker.addRule(new PolyRule());
		// BlockLinkChecker.addRule(new StackRule()); 
		BlockLinkChecker.addRule(new ParamRule());
		BlockLinkChecker.addRule(new InfixRule());
		// arranged by sakai lab 2011/11/21
		// BlockLinkChecker.addRule(new CallMethodRule());

		// set the dirty flag for the language definition file
		// to false now that the lang file has been loaded
		langDefDirty = false;

		workspace.setLoadingBlockLanguage(false);
	}

	/**
	 * Resets the current language within the active Workspace.
	 * 
	 */
	public void resetLanguage() {
		// clear shape mappings
		BlockConnectorShape.resetConnectorShapeMappings();
		// clear block genuses
		BlockGenus.resetAllGenuses();
		// clear all link rules
		BlockLinkChecker.reset();
	}

	// //////////////////////
	// SAVING AND LOADING //
	// //////////////////////

	/**
	 * Returns the save string for the entire workspace. This includes the block
	 * workspace, any custom factories, canvas view state and position, pages
	 * 
	 * @return the save string for the entire workspace.
	 */
	public String getSaveString() {
		StringBuffer saveString = new StringBuffer();
		// append the save data
		//saveString.append("<?xml version=\"1.0\" encoding=\"Shift_JIS\"?>");
		saveString.append("<?xml version=\"1.0\" encoding=\""
				+ SBlockEditor.ENCODING_BLOCK_XML + "\"?>");
		saveString.append("\r\n");
		// dtd file path may not be correct...
		// saveString.append("<!DOCTYPE StarLogo-TNG SYSTEM \""+SAVE_FORMAT_DTD_FILEPATH+"\">");
		// append root node
		saveString.append("<CODEBLOCKS>");
		saveString.append(workspace.getSaveString());
		saveString.append("</CODEBLOCKS>");
		return saveString.toString();
	}

	/**
	 * Loads a fresh workspace based on the default specifications in the
	 * language definition file. The block canvas will have no live blocks.
	 */
	public void loadFreshWorkspace() {
		// need to just reset workspace (no need to reset language) unless
		// language was never loaded
		// reset only if workspace actually exists
		if (workspaceLoaded) {
			resetWorkspace();
		}

		if (langDefDirty) {
			loadBlockLanguage(langDefRoot);
		}

		workspace.loadWorkspaceFrom(null, langDefRoot);

		workspaceLoaded = true;
	}

	/**
	 * Loads the programming project from the specified file path. This method
	 * assumes that a Language Definition File has already been specified for
	 * this programming project.
	 * 
	 * @param path
	 *            String file path of the programming project to load
	 */
	public void loadProjectFromPath(String path) {

		selectedJavaFile = path;

		// reset only if workspace actually exists
		if (workspaceLoaded) {
			resetWorkspace();
		}

		if (langDefDirty) {
			loadBlockLanguage(langDefRoot);
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc;
		try {
			synchronized (frame.getTreeLock()) {
				builder = factory.newDocumentBuilder();

				doc = builder.parse(new File(path));

				Element projectRoot = doc.getDocumentElement();

				// load the canvas (or pages and page blocks if any) blocks from
				// the
				// save file
				// also load drawers, or any custom drawers from file. if no
				// custom
				// drawers
				// are present in root, then the default set of drawers is
				// loaded
				// from
				// langDefRoot
				workspace.loadWorkspaceFrom(projectRoot, langDefRoot);//左のブロック読み込み

				workspaceLoaded = true;

				setFrameTitle(path);

				setDirty(false);
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String createWindowTitle() {
		return SBlockEditor.APP_NAME + " " + SBlockEditor.VERSION;
	}

	private void setFrameTitle(String path) {
		String defaultTitle = createWindowTitle();

		if (path == null) {
			frame.setTitle(defaultTitle);
			return;
		}

		String name = new File(path).getName();
		String javaName = ChangeExtension.changeToJavaExtension(name);

		String title = defaultTitle + "-" + javaName;

		frame.setTitle(title);
	}

	public void setCompileErrorTitle(String targetName) {
		String title = createWindowTitle()
				+ " - Javaのソースコードのコンパイルに失敗したため、ブロック化できません。";
		frame.setTitle(title);

	}

	/**
	 * Loads the programming project specified in the projectContents. This
	 * method assumes that a Language Definition File has already been specified
	 * for this programming project.
	 * 
	 * @param projectContents
	 */
	public void loadProject(String projectContents) {
		// need to reset workspace and language (only if new language has been
		// set)

		// reset only if workspace actually exists
		if (workspaceLoaded) {
			resetWorkspace();
		}

		if (langDefDirty) {
			loadBlockLanguage(langDefRoot);
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc;
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(new InputSource(new StringReader(
					projectContents)));
			Element root = doc.getDocumentElement();
			workspace.loadWorkspaceFrom(root, langDefRoot);

			workspaceLoaded = true;

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Loads the programming project specified in the projectContents String,
	 * which is associated with the language definition file contained in the
	 * specified langDefContents. All the blocks contained in projectContents
	 * must have an associted block genus defined in langDefContents.
	 * 
	 * If the langDefContents have any workspace settings such as pages or
	 * drawers and projectContents has workspace settings as well, the workspace
	 * settings within the projectContents will override the workspace settings
	 * in langDefContents.
	 * 
	 * NOTE: The language definition contained in langDefContents does not
	 * replace the default language definition file set by: setLangDefFilePath()
	 * or setLangDefFile().
	 * 
	 * @param projectContents
	 * @param langDefContents
	 *            String XML that defines the language of projectContents
	 */
	public void loadProject(String projectContents, String langDefContents) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document projectDoc;
		Document langDoc;
		try {
			synchronized (frame.getTreeLock()) {
				builder = factory.newDocumentBuilder();
				projectDoc = builder.parse(new InputSource(new StringReader(
						projectContents)));
				Element projectRoot = projectDoc.getDocumentElement();
				langDoc = builder.parse(new InputSource(new StringReader(
						langDefContents)));
				Element langRoot = langDoc.getDocumentElement();

				// need to reset workspace and language (if langDefContents !=
				// null)
				// reset only if workspace actually exists
				if (workspaceLoaded) {
					resetWorkspace();
				}

				if (langDefContents == null) {
					loadBlockLanguage(langDefRoot);
				} else {
					loadBlockLanguage(langRoot);
				}
				// should verify that the roots of the two XML strings are
				// valid
				workspace.loadWorkspaceFrom(projectRoot, langRoot);

				workspaceLoaded = true;
				frame.setTitle(createWindowTitle());
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Resets the entire workspace. This includes all blocks, pages, drawers,
	 * and trashed blocks. Also resets the undo/redo stack. The language (i.e.
	 * genuses and shapes) is not reset.
	 */
	public void resetWorkspace() {
		// clear all pages and their drawers
		// clear all drawers and their content
		// clear all block and renderable block instances
		workspace.reset();
		// clear action history
		// rum.reset();
		// clear runblock manager data
		// rbm.reset();
	}

	/**
	 * This method creates and lays out the entire workspace panel with its
	 * different components. Workspace and language data not loaded in this
	 * function. Should be call only once at application startup.
	 */
	private void initWorkspacePanel() {

		// add trashcan and prepare trashcan images
		ImageIcon tc = new ImageIcon(imagePath + "trash.png");
		ImageIcon openedtc = new ImageIcon(imagePath + "trash_open.png");
		TrashCan trash = new TrashCan(tc.getImage(), openedtc.getImage());
		workspace.addWidget(trash, true, true);

		workspacePanel = new JPanel();
		workspacePanel.setLayout(new BorderLayout());
		workspacePanel.add(workspace, BorderLayout.CENTER);

		isWorkspacePanelInitialized = true;
	}

	/**
	 * Returns the JComponent of the entire workspace.
	 * 
	 * @return the JComponent of the entire workspace.
	 */
	public JComponent getWorkspacePanel() {
		if (!isWorkspacePanelInitialized)
			initWorkspacePanel();
		return workspacePanel;
	}

	/**
	 * Returns a SearchBar instance capable of searching for blocks within the
	 * BlockCanvas and block drawers
	 */
	public JComponent getSearchBar() {
		SearchBar searchBar = new SearchBar("Search blocks",
				"Search for blocks in the drawers and workspace", workspace);
		for (SearchableContainer con : getAllSearchableContainers()) {
			searchBar.addSearchableContainer(con);
		}

		return searchBar.getComponent();
	}

	/**
	 * Returns an unmodifiable Iterable of SearchableContainers
	 * 
	 * @return an unmodifiable Iterable of SearchableContainers
	 */
	public Iterable<SearchableContainer> getAllSearchableContainers() {
		return workspace.getAllSearchableContainers();
	}

	private boolean dirty = false;

	// ///////////////////////////////////
	// TESTING CODEBLOCKS SEPARATELY //
	// ///////////////////////////////////
	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	public void createAndShowGUI(final WorkspaceController wc,
			final SBlockEditorListener ronproEditor, final String enc) {
		this.ronproEditor = ronproEditor;

		Workspace.getInstance().addWorkspaceListener(new WorkspaceListener() {
			public void workspaceEventOccurred(WorkspaceEvent event) {
				//System.out.println(event);
				setDirty(true);
			}
		});

		// Create and set up the window.
		frame = new JFrame(createWindowTitle());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// int inset = 50;
		// Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		frame.setBounds(100, 100, 800, 500);

		JPanel topPane = new JPanel();

		{// create save button
			JButton saveButton = new JButton("Save as Java");
			saveButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					wc.convertToJava(wc.getSaveString(), enc);
				}
			});
			topPane.add(saveButton);
		}

		//		{// create run button
		//			JButton runButton = new JButton("Java出力して実行");
		//			runButton.addActionListener(new ActionListener() {
		//				@Override
		//				public void actionPerformed(ActionEvent e) {
		//					wc.convertToJavaAndRun(
		//							wc.getSaveString(), enc);
		//				}
		//			});
		//			topPane.add(runButton);
		//		}

		{// create compile button
			JButton runButton = new JButton("Compile");
			runButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (dirty) {
						JOptionPane.showMessageDialog(frame, "ソースがセーブされていません",
								"コンパイルできません", JOptionPane.ERROR_MESSAGE);
						return;
					}
					ronproEditor.blockCompile();
				}
			});
			topPane.add(runButton);
		}

		{// create run button
			JButton runButton = new JButton("Run");
			runButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (dirty) {
						JOptionPane.showMessageDialog(frame, "コンパイルが成功していません",
								"実行できません", JOptionPane.ERROR_MESSAGE);
						return;
					}
					ronproEditor.blockRun();
				}
			});
			topPane.add(runButton);
		}

		{// create debug run button
			JButton runButton = new JButton("DebugRun");
			runButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (dirty) {
						JOptionPane.showMessageDialog(frame, "コンパイルが成功していません",
								"実行できません", JOptionPane.ERROR_MESSAGE);
						return;
					}
					ronproEditor.blockDebugRun();
				}
			});
			//topPane.add(runButton);
		}

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		{
			JMenu menu = new JMenu("Menu");
			menuBar.add(menu);

			{
				//JButton b = new JButton("SS");
				JMenuItem item = new JMenuItem("SS");
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						CScreenShotTaker taker = createSSTaker();
						String name = new CFilename(wc.getSelectedJavaFile())
								.getName();
						taker.getChooser().setSelectedFile(new File(name));
						taker.takeToFile();
					}
				});
				//topPane.add(b);
				menu.add(item);
			}
		}

		WindowListener closeManagement = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame = null;
			}
		};

		frame.add(topPane, BorderLayout.PAGE_START);
		frame.add(wc.getWorkspacePanel(), BorderLayout.CENTER);
		frame.addWindowListener(closeManagement);
		frame.setVisible(true);
	}

	public void createAndShowGUIForTesting(final WorkspaceController wc,
			final String enc) {

		// Create and set up the window.
		frame = new JFrame(createWindowTitle());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// int inset = 50;
		// Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		frame.setBounds(100, 100, 800, 550);

		// create save button
		JButton saveButton = new JButton("Java出力");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				wc.convertToJava(wc.getSaveString(), enc);
			}
		});

		JPanel topPane = new JPanel();
		topPane.add(saveButton);

		// create load button
		JButton loadButton = new JButton("ソースをBlock化");
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File dir = new File("..\\");
				JFileChooser filechooser = new JFileChooser(dir);
				FileFilter filterXmlExtention = new FileNameExtensionFilter(
						"XMLファイル(*.xml)", "xml");
				filechooser.addChoosableFileFilter(filterXmlExtention);
				int selected = filechooser.showOpenDialog(wc
						.getWorkspacePanel());
				if (selected != JFileChooser.APPROVE_OPTION) {
					return;
				}

				// JavaFile
				File xmlFile = filechooser.getSelectedFile();

				wc.loadProjectFromPath(xmlFile.getPath());
			}
		});
		topPane.add(loadButton);

		// added by matsuzawa
		JButton button = new JButton("Compile And Load");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					File dir = new File("..\\");
					JFileChooser filechooser = new JFileChooser(dir);
					FileFilter filterXmlExtention = new FileNameExtensionFilter(
							"Javaファイル(*.java)", "java");
					filechooser.addChoosableFileFilter(filterXmlExtention);
					int selected = filechooser.showOpenDialog(wc
							.getWorkspacePanel());
					if (selected != JFileChooser.APPROVE_OPTION) {
						return;
					}

					// JavaFile
					File javaFile = filechooser.getSelectedFile();

					// XmlFile
					String xmlpath = javaFile.getAbsolutePath();
					xmlpath = xmlpath.substring(0, xmlpath.lastIndexOf("."));
					xmlpath = xmlpath += ".xml";
					File xmlFile = new File(xmlpath);

					// compile
					JavaToBlockMain javaToBlock = new JavaToBlockMain();
					javaToBlock.process(javaFile, enc,
							new PrintStream(xmlFile), new String[] {});

					// load
					wc.loadProjectFromPath(xmlFile.getPath());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		topPane.add(button);

		{
			JButton b = new JButton("Save");
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						String saveString = wc.getSaveString();
						BufferedWriter bw = new BufferedWriter(
								new OutputStreamWriter(new FileOutputStream(
										"test.xml"), enc));
						bw.write(saveString);
						bw.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
			topPane.add(b);
		}

		{
			JButton b = new JButton("Load");
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					wc.loadProjectFromPath(new File("test.xml")
							.getAbsolutePath());
				}
			});
			topPane.add(b);
		}

		{
			JButton b = new JButton("SS");
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					createSSTaker().takeToClipboard();
				}
			});
			topPane.add(b);
		}

		WindowListener closeManagement = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame = null;
			}
		};

		frame.add(topPane, BorderLayout.PAGE_START);
		frame.add(wc.getWorkspacePanel(), BorderLayout.CENTER);
		frame.addWindowListener(closeManagement);
		frame.setVisible(true);

	}

	private CScreenShotTaker createSSTaker() {
		Workspace ws = WorkspaceController.workspace;
		BlockCanvas canvas = ws.getBlockCanvas();
		JComponent comp = canvas.getCanvas();

		Rectangle r = new Rectangle(0, 0, 100, 100);
		int i = 0;
		for (RenderableBlock block : canvas.getBlocks()) {
			if (!block.isVisible()) {
				continue;
			}
			if (i == 0) {
				r = block.getBounds();
			} else {
				r.add(block.getBounds());
			}
			i++;
		}
		r.grow(10, 10);//margin
		r = r.intersection(comp.getBounds());//マイナスにはみ出さない

		CScreenShotTaker taker = new CScreenShotTaker(comp);
		taker.setClipbounds(r);
		return taker;
	}

	protected void convertToJava(String saveString, String enc) {
		try {
			convertToJava0(saveString, enc);
			setDirty(false);
		} catch (Exception ex) {
			//ex.printStackTrace();
			//JOptionPane.showMessageDialog(null, ex.getMessage(), "エラーメッセージ",
			//		JOptionPane.ERROR_MESSAGE);
			CErrorDialog.show(null, "エラーが発生しました．", ex);
		}
	}

	private void convertToJava0(String saveString, String enc) throws Exception {

		if (state == COMPILE_ERROR) {
			throw new RuntimeException("Javaファイルにコンパイルエラーがあり、ブロック構築できません。");
		}
		if (state == PROJECT_SELECTED) {
			throw new RuntimeException("論プロエディタで、Javaファイルが選択されていません。");
		}
		String xmlFileName = ChangeExtension
				.changeToXmlExtension(selectedJavaFile);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(xmlFileName),
				SBlockEditor.ENCODING_BLOCK_XML));

		bw.write(saveString);
		bw.flush();
		bw.close();

		BlockToJavaMain.convert(new File(xmlFileName), enc, new String[] {});
		ronproEditor.blockConverted(new File(selectedJavaFile));
	}

	@Deprecated
	protected void convertToJavaAndRun(String saveString, String enc) {
		try {
			convertToJava0(saveString, enc);
			ronproEditor.blockRun();
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, ex.getMessage(), "エラーメッセージ",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public String fileToString(File file) throws IOException {
		selectedJavaFile = ChangeExtension
				.changeToJavaExtension(file.getPath());
		BufferedReader br = null;
		try {

			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file)));
			//
			StringBuffer sb = new StringBuffer();
			//
			int c;
			//
			while ((c = br.read()) != -1) {
				sb.append((char) c);
			}
			//
			return sb.toString();
		} finally {
			br.close();
		}
	}

	public static void initWithLangDefFilePath(final String langDefFilePath,
			final String imagePath) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Create a new WorkspaceController
				WorkspaceController wc = new WorkspaceController(imagePath);

				wc.setLangDefFilePath(langDefFilePath);

				wc.loadFreshWorkspace();
				//wc.createAndShowGUIForTesting(wc);
			}
		});
	}

	public void setDirty(boolean dirty) {
		if (this.dirty == dirty) {
			return;
		}

		this.dirty = dirty;

		if (frame != null) {
			String title = frame.getTitle();
			if (!title.endsWith("*") && dirty) {
				frame.setTitle(title + "*");
			} else if (title.endsWith("*") && !dirty) {
				frame.setTitle(title.substring(0, title.length() - 1));
			}
		}
	}

	public Workspace getWorkspace() {
		return workspace;
	}

}
