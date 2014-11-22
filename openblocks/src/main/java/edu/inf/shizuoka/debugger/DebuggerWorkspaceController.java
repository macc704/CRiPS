package edu.inf.shizuoka.debugger;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.mit.blocks.controller.WorkspaceController;
import edu.mit.blocks.workspace.Workspace;

public class DebuggerWorkspaceController extends WorkspaceController{

	private File selectedFile;
	// Reference kept to be able to update frame title with current loaded file
	private JFrame frame;
	
	
	public DebuggerWorkspaceController(String langDefRootPath, File selectedFile){
		
		setLangDefFilePath(langDefRootPath);
		loadFreshWorkspace();
		this.selectedFile = selectedFile;
		
		loadProjectFromPath(selectedFile.getPath());
		
		createDebugGUI();
		
		runProgram();
		
	}
	
	
	private void createDebugGUI() {

		frame = new JFrame("BlockEditor");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBounds(100, 100, 800, 600);

		frame.setJMenuBar(getMenuBar());
		
		frame.add(getWorkspacePanel(), BorderLayout.CENTER);

		frame.add(getDebugButtonPanel(), BorderLayout.PAGE_START);
		
		frame.setVisible(true);
		
	}
	
	protected JComponent getDebugButtonPanel() {
		JPanel buttonPanel = new JPanel();

		buttonPanel.add(new JButton("◀|"));

		JButton button = new JButton("|▶");
		button.addActionListener(new NextAction());
		
		buttonPanel.add(button);
		return buttonPanel;
	}
	
	public void runProgram(){
		ProgramRunnner runnner = new ProgramRunnner(selectedFile, this);
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