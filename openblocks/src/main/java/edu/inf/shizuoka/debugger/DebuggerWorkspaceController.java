package edu.inf.shizuoka.debugger;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.unicoen.node.UniClassDec;
import net.unicoen.node.UniMemberDec;
import net.unicoen.parser.blockeditor.BlockGenerator;
import net.unicoen.parser.blockeditor.BlockMapper;
import edu.mit.blocks.controller.WorkspaceController;

public class DebuggerWorkspaceController extends WorkspaceController{

	private File selectedFile;
	// Reference kept to be able to update frame title with current loaded file
	private JFrame frame;

	public DebuggerWorkspaceController(String langDefRootPath, File selectedFile) throws IOException{

		setLangDefFilePath(langDefRootPath);
		loadFreshWorkspace();
		this.selectedFile = selectedFile;

		UniClassDec exeClass = parse();

		loadProjectFromPath(selectedFile.getPath());

		createDebugGUI();
		runProgram(exeClass);
	}

	public UniClassDec parse() throws IOException{
		BlockMapper mapper = new BlockMapper();
		UniClassDec dec = mapper.parse(selectedFile);
		dec.members = new ArrayList<UniMemberDec>();
		dec.className = selectedFile.getName().substring(0, selectedFile.getName().indexOf(".xml"));


		PrintStream out = new PrintStream(selectedFile);

		BlockGenerator parser = new BlockGenerator(out);

		parser.parse(dec);

		out.close();

		return dec;
	}

	private void createDebugGUI() {

		frame = new JFrame("BlockEditor Debugger");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBounds(100, 100, 800, 600);

		frame.add(getWorkspacePanel(), BorderLayout.CENTER);

		frame.add(getDebugButtonPanel(), BorderLayout.PAGE_START);

		frame.setVisible(true);
	}

	protected JComponent getDebugButtonPanel() {
		JPanel buttonPanel = new JPanel();

		JButton button = new JButton("|▶");
		button.addActionListener(new NextAction());

		buttonPanel.add(button);
		return buttonPanel;
	}


	public void runProgram(UniClassDec exeClass){
		ProgramRunnner runnner = new ProgramRunnner(exeClass, this);
		runnner.start();
	}

}

class NextAction implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		BlockEditorDebbugger.setFlag(true);
	}

}