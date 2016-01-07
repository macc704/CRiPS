package edu.inf.shizuoka.debugger;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import edu.mit.blocks.controller.WorkspaceController;
import net.unicoen.node.UniClassDec;
import net.unicoen.parser.blockeditor.BlockGenerator;
import net.unicoen.parser.blockeditor.BlockMapper;

public class DebuggerWorkspaceController extends WorkspaceController {

	private File selectedFile;
	// Reference kept to be able to update frame title with current loaded file
	private JFrame frame;

	public DebuggerWorkspaceController(UniClassDec dec, String langDefRootPath, File selectedFile) throws IOException, SAXException {
		setLangDefFilePath(langDefRootPath);
		loadFreshWorkspace();
		this.selectedFile = selectedFile;

		PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream(selectedFile)), false, "UTF-8");
		BlockGenerator generator = new BlockGenerator(out, WorkspaceController.langDefRootPath);
		generator.parse(dec);
		out.close();

		loadProjectFromPath(selectedFile.getPath());

		createDebugGUI();
	}

	public UniClassDec parse() throws IOException, SAXException {
		BlockMapper mapper = new BlockMapper(WorkspaceController.langDefRootPath);
		UniClassDec dec = mapper.parse(selectedFile);

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

	public void runProgram(UniClassDec exeClass, Map<String, Element> blocks) {
		ProgramRunnner runnner = new ProgramRunnner(exeClass, this, blocks);
		runnner.start();
	}

}

class NextAction implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		BlockEditorDebbugger.setFlag(true);
	}
}