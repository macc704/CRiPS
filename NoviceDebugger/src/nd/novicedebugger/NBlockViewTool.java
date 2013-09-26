package nd.novicedebugger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nd.com.sun.tools.example.debug.gui.CommandInterpreter;
import nd.com.sun.tools.example.debug.gui.Environment;
import nd.com.sun.tools.example.debug.gui.SourceModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import renderable.RenderableBlock;
import controller.WorkspaceController;

public class NBlockViewTool extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String path = "D:/Users/Daiki/eclipse-workspace/RonproEditor/testbase/MyProjects/NewProject/NewClass.xml";
	private static final String LANG_DEF_PATH = "ext/block/lang_def.xml";
	private static final String LANG_DEF_TURTLE_PATH = "ext/block/lang_def_turtle.xml";
	private static final String IMAGES_PATH = "ext/block/images/";
	
	private String langPath;
	
	private WorkspaceController wc;
	private JComponent canvas;
	private ArrayList<RenderableBlock> rBlocks;
	
	private Environment env;
	
	private CommandInterpreter interpreter;
	
	public NBlockViewTool(Environment env){
		super(new BorderLayout());
		
		this.env = env;
		this.interpreter = new CommandInterpreter(env, true);
		
		rBlocks = new ArrayList<RenderableBlock>();
		wc = new WorkspaceController(IMAGES_PATH);
		wc.setLangDefFilePath(LANG_DEF_PATH);
		wc.loadFreshWorkspace();
		// wc.createAndShowGUIForTesting(wc, "SJIS");
		// wc.loadProjectFromPath(path);
		
		this.canvas = wc.getWorkspace().getBlockCanvas().getCanvas();
		add(new JScrollPane(canvas));
		
	}
	
	public static void main(String argv[]) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBounds(100, 100, 500, 300);
		frame.setPreferredSize(new java.awt.Dimension(500, 300));
		frame.add(new JScrollPane(new NBlockViewTool(null)));
		frame.setVisible(true);
	}
	
	public void loadXml(String path){
		
		langPath = LANG_DEF_PATH;
		if(env.isTurtle()){
			langPath = LANG_DEF_TURTLE_PATH;
		}
		wc.setLangDefFilePath(langPath);
		wc.resetWorkspace();
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document docLang, docXml;
		try {
			synchronized (this) {
				builder = factory.newDocumentBuilder();
				
				docLang = builder.parse(new File(langPath));
				Element langDefRoot = docLang.getDocumentElement();
				
				docXml = builder.parse(new File(path));
				Element projectRoot = docXml.getDocumentElement();

				wc.loadBlockLanguage(langDefRoot);
				wc.getWorkspace().loadWorkspaceFrom(projectRoot, langDefRoot);
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		
		for(RenderableBlock rBlock : wc.getWorkspace().getRenderableBlocks()){
			//System.out.println(rBlock.getBlockID());
			rBlocks.add(rBlock);
			for(MouseListener l : rBlock.getMouseListeners()){
				rBlock.removeMouseListener(l);
			}
			for(MouseMotionListener l : rBlock.getMouseMotionListeners()){
				rBlock.removeMouseMotionListener(l);
			}
			rBlock.addMouseListener(new MouseListener() {
				public void mouseReleased(MouseEvent e) {
					if (e.isPopupTrigger() || SwingUtilities.isRightMouseButton(e)
							|| e.isControlDown()) {
						showPopupMenu((Component) e.getSource(), e.getX(), e.getY());
					}
				}
				
				private void showPopupMenu(Component invoker, int x, int y) {
					RenderableBlock rBlock = (RenderableBlock) invoker;
					int ln = rBlock.getBlock().getLineNumber();
					SourceModel.Line line = (SourceModel.Line) env.getSourceTool().getList().getModel().getElementAt(ln - 1);
					JPopupMenu popup = new JPopupMenu();

					if (line == null) {
						popup.add(new JMenuItem("please select a line"));
					} else if (line.isExecutable()) {
						String className = line.refType.name();
					if (line.hasBreakpoint()) {
						popup.add(commandItem("Clear Breakpoint", "clear "
							+ className + ":" + ln));
					} else {
						popup.add(commandItem("Set Breakpoint", "stop at "
							+ className + ":" + ln));
					}
					} else {
						popup.add(new JMenuItem("not an executable line"));
					}

					popup.show(invoker, x + popup.getWidth() / 2, y + popup.getHeight() / 2);
				}

				private JMenuItem commandItem(String label, final String cmd) {
					JMenuItem item = new JMenuItem(label);
					item.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							interpreter.executeCommand(cmd);
						}
					});
					return item;
				}
				
				public void mousePressed(MouseEvent e) {
				}
				public void mouseExited(MouseEvent e) {
//					RenderableBlock rBlock = (RenderableBlock) e.getComponent();
//					rBlock.mouseExited(e);
				}
				public void mouseEntered(MouseEvent e) {
//					RenderableBlock rBlock = (RenderableBlock) e.getComponent();
//					rBlock.mouseEntered(e);
				}
				public void mouseClicked(MouseEvent e) {
				}
			});
		}
			
	}
	
	private ArrayList<RenderableBlock> bufrb = new ArrayList<RenderableBlock>();
	public void ExecutionPoint(int lineNumber){
		for(RenderableBlock rb : bufrb){
			if(rb != null){
				try{
					rb.resetHighlight();
				} catch(java.lang.NullPointerException e) {
					
				}
			}
		}
		bufrb.clear();
		for(RenderableBlock rb : rBlocks){
			if (rb.getBlock().getLineNumber() == lineNumber){
				rb.setBlockHighlightColor(Color.WHITE);
				bufrb.add(rb);
				RenderableBlock parentBlock = RenderableBlock.getRenderableBlock(rb.getBlock().getParentBlockID());
				while(!parentBlock.getGenus().equals("procedure")){
					parentBlock.setBlockHighlightColor(Color.WHITE);
					bufrb.add(parentBlock);
					parentBlock = RenderableBlock.getRenderableBlock(parentBlock.getBlock().getParentBlockID());
				}
			}
		}
	}

}
