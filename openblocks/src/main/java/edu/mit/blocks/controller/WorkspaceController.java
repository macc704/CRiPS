package edu.mit.blocks.controller;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import bc.apps.BlockToJavaMain;
import bc.apps.JavaToBlockMain;
import clib.view.app.javainfo.CJavaInfoPanels;
import edu.inf.shizuoka.debugger.DebuggerWorkspaceController;
import edu.inf.shizuoka.drawingobjects.ArrowObject;
import edu.inf.shizuoka.drawingobjects.DrawingArrowManager;
import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblocks.BlockConnector;
import edu.mit.blocks.codeblocks.BlockConnectorShape;
import edu.mit.blocks.codeblocks.BlockGenus;
import edu.mit.blocks.codeblocks.BlockLinkChecker;
import edu.mit.blocks.codeblocks.BlockStub;
import edu.mit.blocks.codeblocks.CommandRule;
import edu.mit.blocks.codeblocks.Constants;
import edu.mit.blocks.codeblocks.InfixRule;
import edu.mit.blocks.codeblocks.ParamRule;
import edu.mit.blocks.codeblocks.PolyRule;
import edu.mit.blocks.codeblocks.SocketRule;
import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.Page;
import edu.mit.blocks.workspace.SearchBar;
import edu.mit.blocks.workspace.SearchableContainer;
import edu.mit.blocks.workspace.Workspace;

/**
 * Example entry point to OpenBlock application creation.
 *
 * @author Ricarose Roque
 */
public class WorkspaceController {

	private Element langDefRoot;
	private boolean isWorkspacePanelInitialized = false;
	protected JPanel workspacePanel;
	protected final Workspace workspace;
	protected SearchBar searchBar;
//	private static String LANG_DEF_PATH;
	
	private String enc = "SJIS";

	public Workspace getWorkspace() {
		return this.workspace;
	}

	// flag to indicate if a new lang definition file has been set
	private boolean langDefDirty = true;

	// flag to indicate if a workspace has been loaded/initialized
	private boolean workspaceLoaded = false;
	// last directory that was selected with open or save action
	private File lastDirectory;
	// file currently loaded in workspace
	private File selectedFile;
	// Reference kept to be able to update frame title with current loaded file
	private JFrame frame;
	
	private DebuggerWorkspaceController debugger;
	
	// for CheCoPro
	private boolean openedFromCH = false;
	private String user = "";

	/**
	 * Constructs a WorkspaceController instance that manages the interaction
	 * with the codeblocks.Workspace
	 *
	 */
	public WorkspaceController() {
		this.workspace = new Workspace();
	}
	
	public WorkspaceController(String user, boolean openedFromCH) {
		this.openedFromCH = openedFromCH;
		this.user = user;
		this.workspace = new Workspace();
	}

	/**
	 * Sets the file path for the language definition file, if the language
	 * definition file is located in
	 */
	public void setLangDefFilePath(final String filePath) {
		InputStream in = null;
		try {
			in = new FileInputStream(filePath);
			setLangDefStream(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	/**
	 * Sets language definition file from the given input stream
	 * 
	 * @param in
	 *            input stream to read
	 */
	public void setLangDefStream(InputStream in) {
		final DocumentBuilderFactory factory = DocumentBuilderFactory
				.newInstance();
		final DocumentBuilder builder;
		final Document doc;
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(new File(langDefRootPath));
			langDefRoot = doc.getDocumentElement();
			langDefDirty = true;
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Loads all the block genuses, properties, and link rules of a language
	 * specified in the pre-defined language def file.
	 * 
	 * @param root
	 *            Loads the language specified in the Element root
	 */
	public void loadBlockLanguage(final Element root) {
		/*
		 * MUST load shapes before genuses in order to initialize connectors
		 * within each block correctly
		 */
		BlockConnectorShape.loadBlockConnectorShapes(root);

		// load genuses
		BlockGenus.loadBlockGenera(workspace, root);

		// load rules
		BlockLinkChecker.addRule(workspace, new CommandRule(workspace));
		BlockLinkChecker.addRule(workspace, new SocketRule());

		BlockLinkChecker.addRule(workspace, new PolyRule());

		BlockLinkChecker.addRule(workspace, new ParamRule());
		BlockLinkChecker.addRule(workspace, new InfixRule());

		// set the dirty flag for the language definition file
		// to false now that the lang file has been loaded
		langDefDirty = false;
	}

	/**
	 * Resets the current language within the active Workspace.
	 *
	 */
	public void resetLanguage() {
		BlockConnectorShape.resetConnectorShapeMappings();
		getWorkspace().getEnv().resetAllGenuses();
		BlockLinkChecker.reset();
	}

	/**
	 * Returns the save string for the entire workspace. This includes the block
	 * workspace, any custom factories, canvas view state and position, pages
	 * 
	 * @return the save string for the entire workspace.
	 */
	public String getSaveString() {
		try {
			Node node = getSaveNode();

			StringWriter writer = new StringWriter();
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer
					.transform(new DOMSource(node), new StreamResult(writer));
			return writer.toString();
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns a DOM node for the entire workspace. This includes the block
	 * workspace, any custom factories, canvas view state and position, pages
	 * 
	 * @return the DOM node for the entire workspace.
	 */
	public Node getSaveNode() {
		return getSaveNode(true);
	}

	/**
	 * Returns a DOM node for the entire workspace. This includes the block
	 * workspace, any custom factories, canvas view state and position, pages
	 *
	 * @param validate
	 *            If {@code true}, perform a validation of the output against
	 *            the code blocks schema
	 * @return the DOM node for the entire workspace.
	 */
	public Node getSaveNode(final boolean validate) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);

			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();

			Element documentElement = document.createElementNS(
					Constants.XML_CODEBLOCKS_NS, "cb:CODEBLOCKS");

			// schema reference
			documentElement.setAttributeNS(
					XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
					"xsi:schemaLocation", Constants.XML_CODEBLOCKS_NS + " "
							+ Constants.XML_CODEBLOCKS_SCHEMA_URI);

			Node workspaceNode = workspace.getSaveNode(document);
			if (workspaceNode != null) {
				documentElement.appendChild(workspaceNode);
			}

			document.appendChild(documentElement);

			// if (validate) {
			// validate(document);
			// }

			return document;
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Validates the code blocks document against the schema
	 * 
	 * @param document
	 *            The document to check
	 * @throws RuntimeException
	 *             If the validation failed
	 */
//	private void validate(Document document) {
//		try {
//
//			SchemaFactory schemaFactory = SchemaFactory
//					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//			URL schemaUrl = ClassLoader
//					.getSystemResource("edu/mit/blocks/codeblocks/codeblocks.xsd");
//			File file = new File(schemaUrl.getPath());
//			Schema schema = schemaFactory.newSchema(schemaUrl);
//			Validator validator = schema.newValidator();
//			validator.validate(new DOMSource(document));
//		} catch (MalformedURLException e) {
//			throw new RuntimeException(e);
//		} catch (SAXException e) {
//			throw new RuntimeException(e);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}

	/**
	 * Loads a fresh workspace based on the default specifications in the
	 * language definition file. The block canvas will have no live blocks.
	 */
	public void loadFreshWorkspace() {
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
	public void loadProjectFromPath(final String path) {
		final DocumentBuilderFactory factory = DocumentBuilderFactory
				.newInstance();
		factory.setNamespaceAware(true);
		final DocumentBuilder builder;
		final Document doc;
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(new File(path));

			// XXX here, we could be strict and only allow valid documents...
			// validate(doc);
			final Element projectRoot = doc.getDocumentElement();
			// load the canvas (or pages and page blocks if any) blocks from the
			// save file
			// also load drawers, or any custom drawers from file. if no custom
			// drawers
			// are present in root, then the default set of drawers is loaded
			// from
			// langDefRoot
			workspace.loadWorkspaceFrom(projectRoot, langDefRoot);
			workspaceLoaded = true;
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Loads the programming project from the specified element. This method
	 * assumes that a Language Definition File has already been specified for
	 * this programming project.
	 *
	 * @param element
	 *            element of the programming project to load
	 */
	public void loadProjectFromElement(Element elementToLoad) {
		workspace.loadWorkspaceFrom(elementToLoad, langDefRoot);
		workspaceLoaded = true;
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
		final DocumentBuilderFactory factory = DocumentBuilderFactory
				.newInstance();
		final DocumentBuilder builder;
		final Document projectDoc;
		final Document langDoc;
		try {
			builder = factory.newDocumentBuilder();
			projectDoc = builder.parse(new InputSource(new StringReader(
					projectContents)));
			final Element projectRoot = projectDoc.getDocumentElement();
			langDoc = builder.parse(new InputSource(new StringReader(
					projectContents)));
			final Element langRoot = langDoc.getDocumentElement();
			if (workspaceLoaded) {
				resetWorkspace();
			}
			if (langDefContents == null) {
				loadBlockLanguage(langDefRoot);
			} else {
				loadBlockLanguage(langRoot);
			}
			workspace.loadWorkspaceFrom(projectRoot, langRoot);
			workspaceLoaded = true;
			
			showAllTraceLine(workspace);
			
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
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
	}

	/**
	 * This method creates and lays out the entire workspace panel with its
	 * different components. Workspace and language data not loaded in this
	 * function. Should be call only once at application startup.
	 */
	private void initWorkspacePanel() {
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
		if (!isWorkspacePanelInitialized) {
			initWorkspacePanel();
		}
		return workspacePanel;
	}

	/**
	 * Action bound to "Open" action.
	 */
	private class OpenAction extends AbstractAction {

		private static final long serialVersionUID = -2119679269613495704L;

		OpenAction() {
			super("Open");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser(lastDirectory);
			if (fileChooser.showOpenDialog((Component) e.getSource()) == JFileChooser.APPROVE_OPTION) {
				setSelectedFile(fileChooser.getSelectedFile());
				lastDirectory = selectedFile.getParentFile();
				String selectedPath = selectedFile.getPath();
				loadFreshWorkspace();
				loadProjectFromPath(selectedPath);
			}
		}
	}

	/**
	 * Action bound to "Save" button.
	 */
	private class SaveAction extends AbstractAction {
		private static final long serialVersionUID = -5540588250535739852L;

		SaveAction() {
			super("Save");
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			if (selectedFile == null) {
				JFileChooser fileChooser = new JFileChooser(lastDirectory);
				if (fileChooser.showSaveDialog((Component) evt.getSource()) == JFileChooser.APPROVE_OPTION) {
					setSelectedFile(fileChooser.getSelectedFile());
					lastDirectory = selectedFile.getParentFile();
				}
			}
			try {
				saveToFile(selectedFile);
			} catch (IOException e) {
				JOptionPane.showMessageDialog((Component) evt.getSource(),
						e.getMessage());
			}
		}
	}

	/**
	 * Action bound to "Save As..." button.
	 */
	private class SaveAsAction extends AbstractAction {
		private static final long serialVersionUID = 3981294764824307472L;
		private final SaveAction saveAction;

		SaveAsAction(SaveAction saveAction) {
			super("Save As...");
			this.saveAction = saveAction;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			selectedFile = null;
			// delegate to save action
			saveAction.actionPerformed(e);
		}
	}
	
	/**
	 * Action bound to "Save As..." button.
	 */
	private class ConvertAction extends AbstractAction {
		
		private static final long serialVersionUID = 4649159219713654455L;

		ConvertAction() {
			super("Convert");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				BlockToJavaMain.convert(selectedFile, enc , new String[] {});
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Saves the content of the workspace to the given file
	 * 
	 * @param file
	 *            Destination file
	 * @throws IOException
	 *             If save failed
	 */
	private void saveToFile(File file) throws IOException {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(file);
			fileWriter.write(getSaveString());
		} finally {
			if (fileWriter != null) {
				fileWriter.close();
			}
		}
	}
	

	public void setSelectedFile(File selectedFile) {
		this.selectedFile = selectedFile;
		frame.setTitle("BlockEditor - " + selectedFile.getPath() + " - " + user);
	}

	/**
	 * Return the lower button panel.
	 */
	private JComponent getButtonPanel() {
		JPanel buttonPanel = new JPanel();
		// Open
		OpenAction openAction = new OpenAction();
		buttonPanel.add(new JButton(openAction));
		// Save
		SaveAction saveAction = new SaveAction();
		buttonPanel.add(new JButton(saveAction));
		// Save as
		SaveAsAction saveAsAction = new SaveAsAction(saveAction);
		buttonPanel.add(new JButton(saveAsAction));
		
		ConvertAction convertAction = new  ConvertAction();
		buttonPanel.add(new JButton(convertAction));
		
		{// create showing method trace line bottun
			final JToggleButton showTraceLineButton = new JToggleButton(
					"hide trace line");
			showTraceLineButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (showTraceLineButton.isSelected()) {
						// 関数呼び出しをトレースするラインを非表示にする
						disposeTraceLine(workspace);
					} else {
						// 関数呼び出しをトレースするラインを表示する
						showAllTraceLine(workspace);
					}
				}
			});
			buttonPanel.add(showTraceLineButton);
		}

		
		return buttonPanel;
	}
	
	/**
	 * Returns a SearchBar instance capable of searching for blocks within the
	 * BlockCanvas and block drawers
	 */
	public JComponent getSearchBar() {
		final SearchBar sb = new SearchBar("Search blocks",
				"Search for blocks in the drawers and workspace", workspace);
		for (SearchableContainer con : getAllSearchableContainers()) {
			sb.addSearchableContainer(con);
		}
		return sb.getComponent();
	}

	/**
	 * Returns an unmodifiable Iterable of SearchableContainers
	 * 
	 * @return an unmodifiable Iterable of SearchableContainers
	 */
	public Iterable<SearchableContainer> getAllSearchableContainers() {
		return workspace.getAllSearchableContainers();
	}
	
	protected JMenuBar getMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		
		//open other blockeditor
		JMenu menu = new JMenu("Tools");
		JMenuItem debugItem = new JMenuItem("Open other BlockEditor");
		debugItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				runDebbuger();
			}
		});
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// 
				frame.dispose();
			}
		});
		
		JMenu help = new JMenu("help");
		help.add(CJavaInfoPanels.createJavaInformationAction());

		menu.add(debugItem);
		menu.add(exit);	
		
		menuBar.add(menu);
		menuBar.add(help);

		return menuBar;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private void createAndShowGUI() {
		frame = new JFrame("BlockEditor");
		if(openedFromCH) { 
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		} else {
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		frame.setBounds(100, 100, 800, 600);
		// // final SearchBar sb = new SearchBar("Search blocks",
		// // "Search for blocks in the drawers and workspace", workspace);
		// // for (final SearchableContainer con : getAllSearchableContainers())
		// {
		// // sb.addSearchableContainer(con);
		// // }
		// // final JPanel topPane = new JPanel();
		// // sb.getComponent().setPreferredSize(new Dimension(130, 23));
		// // topPane.add(sb.getComponent());
		// frame.add(topPane, BorderLayout.PAGE_START);
		frame.setJMenuBar(getMenuBar());
		
		frame.add(getWorkspacePanel(), BorderLayout.CENTER);

		if(!openedFromCH) {
			frame.add(getButtonPanel(), BorderLayout.PAGE_START);
		}
		
		frame.setVisible(true);
	}
	
	/*
	 * メソッド呼び出し関係を表示するラインを描画します
	 */
	public static void showAllTraceLine(Workspace workspace) {
		
		List<Block> bodyBlocks = new ArrayList<Block>();
		for (Block block : workspace.getBlocks()) {
			// 呼び出しブロックにラインを表示する
			RenderableBlock callerblock = workspace.getEnv().getRenderableBlock(block
					.getBlockID());
			
			if (callerblock.getGenus().startsWith("caller")) {
				addTraceLine(callerblock, workspace);
			}
			
			if(callerblock.getGenus().equals("procedure") || callerblock.getGenus().equals("abstraction")){
				if(callerblock.isCollapsed()){
					bodyBlocks.add(block);	
				}
			}
		}
		
		//閉じてるブロックの全てのトレースラインを隠す
		
		for(Block parent : bodyBlocks){
			RenderableBlock rBlock;
			if(parent.getGenusName().equals("procedure")){
				rBlock = workspace.getEnv().getRenderableBlock(parent.getAfterBlockID());
			}else{
				rBlock = workspace.getEnv().getRenderableBlock(parent.getSocketAt(0).getBlockID());
			}
			if(rBlock != null){
				while(rBlock.getBlock().getAfterBlockID() != -1){
					if(rBlock.hasArrows()){
						rBlock.visibleArrows(false);
					}
					rBlock = workspace.getEnv().getRenderableBlock(rBlock.getBlock().getAfterBlockID());
				}
				
				if(rBlock.hasArrows()){
					rBlock.visibleArrows(false);
				}	
			}
		}
		
		
	}
	
	public static void addTraceLine(RenderableBlock callerBlock, Workspace workspace){
		
		//メソッド定義ブロックと，呼び出しブロックを直線で結ぶ
		BlockStub stub = (BlockStub) (callerBlock.getBlock());				
		RenderableBlock parentBlock = searchMethodDefinidionBlock(stub, workspace);
		if(parentBlock != null){
			//呼び出しブロックの座標
			Point p1 = DrawingArrowManager.calcCallerBlockPoint(callerBlock);
			
			//呼び出し関数の定義ファイル
			Point p2 = DrawingArrowManager.calcDefinisionBlockPoint(parentBlock);
			ArrowObject arrow = new ArrowObject(p1, p2, workspace);
			Page parentPage = (Page)callerBlock.getParentWidget();
			parentPage.addArrow(arrow);
							
			//定義ブロックへの矢印の追加
			parentBlock.addStartArrow(arrow);
			DrawingArrowManager.addPossesser(parentBlock);
			//callerブロックへの矢印の追加
			callerBlock.addEndArrow(arrow);
			DrawingArrowManager.addPossesser(callerBlock);
			
			
		}
	}
	
	
	
	public static boolean checkParameterType(Block block, List<String> params){
		int connectorSize = -1;//ソケットは必ず一つカウントされる
		int counterSize = 0;
		
		for(@SuppressWarnings("unused") BlockConnector connector : block.getSockets()){
			connectorSize++;
		}
		
		//引数の数をチェック
		if(connectorSize != params.size()){
			return false;
		}
		
		//引数無し同士
		if(params.size() == 0 && connectorSize == 0){
			return true;
		}
		
		for(int i = 0; i < counterSize; i++){
			if(checkIllegalParameter(block, params, connectorSize, i)){
				return false;
			}
		}
		
		return true;
	}
	
	private static boolean checkIllegalParameter(Block block, List<String> params, int connectorSize ,int i){
		//引数の数が合わない
		if(connectorSize < i || params.size() < i){
			return true;
		}
		//ソケットがどちらかnull
		if(block.getSocketAt(i) == null || params.get(i) == null){
			return true;
		}
		//型が不一致
		if(!block.getSocketAt(i).getKind().equals(params.get(i))){
			return true;
		}
		return false;
	}
	
	private static  List<String> calcParamTypes(BlockStub stub){
		List<String> params = new ArrayList<String>();
		for(BlockConnector connector : stub.getSockets()){
			params.add(connector.getKind());
		}
		return params;
	}
	
	private static  RenderableBlock searchMethodDefinidionBlock(BlockStub stub, Workspace workspace) {
		String name = stub.getBlockLabel();
		List<String> params = calcParamTypes(stub);
		
		for (Block block : workspace.getBlocks()) {
			RenderableBlock rb = workspace.getEnv().getRenderableBlock(block.getBlockID());
			
			if(rb.getGenus().equals("procedure") && rb.getBlock().getBlockLabel().equals(name) && checkParameterType(block, params)){
				return workspace.getEnv().getRenderableBlock(block.getBlockID());
			}
		}
		return null;
	}
	
	public static void disposeTraceLine(Workspace workspace){
		if(workspace.getBlockCanvas()!= null){
			workspace.getBlockCanvas().getPages().get(0).clearArrowLayer();
			workspace.getBlockCanvas().getPages().get(0).getJComponent().repaint();
			DrawingArrowManager.clearPossessers();
		}
	}
	
	
	private void runDebbuger(){
		try {
			debugger = new DebuggerWorkspaceController(langDefRootPath,selectedFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	private String langDefRootPath = "ext/block/lang_def_turtle.xml";

	public void setLangDefRootPath(String langDefRootPath) {
		this.langDefRootPath = langDefRootPath;
	}

	public static void main(final String[] args) {
		WorkspaceController wc = new WorkspaceController();
		wc.openBlockEditor();
	}
	
	public void openBlockEditor() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				loadFreshWorkspace();
				createAndShowGUI();
			}
		});
	}

	public void openBlockEditor(final String xmlFilePath) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				loadFreshWorkspace();
				createAndShowGUI();
				
				loadProjectFromPath(xmlFilePath);
				setSelectedFile(new File(xmlFilePath));
			}
		});
	}

}
