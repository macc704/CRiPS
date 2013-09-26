package pres.application.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import pres.application.PRPollingRecorder;
import pres.core.PRRecordingProject;
import clib.common.filesystem.CFileFilter;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.CPath;

/**
 * This code was edited or generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
 * THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
 * LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class PRRecorderFrame extends javax.swing.JFrame {

	public static final long serialVersionUID = 1L;

	private PRPollingRecorder recorder = new PRPollingRecorder();

	private JTextField filenameField;
	private JButton selectFilenameButton;
	private JToggleButton recordingButton;

	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PRRecorderFrame inst = new PRRecorderFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public PRRecorderFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {
			this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			getContentPane().setLayout(null);
			{
				recordingButton = new JToggleButton();
				getContentPane().add(recordingButton);
				recordingButton.setText("Start");
				recordingButton.setBounds(89, 85, 183, 59);
				recordingButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						recordingButtonActionPerformed(evt);
					}
				});
			}
			{
				filenameField = new JTextField();
				getContentPane().add(filenameField);
				filenameField.setText("");
				filenameField.setBounds(50, 23, 222, 25);
			}
			{
				selectFilenameButton = new JButton();
				getContentPane().add(selectFilenameButton);
				selectFilenameButton.setText("Select");
				selectFilenameButton.setBounds(292, 24, 50, 25);
			}
			pack();
			setSize(400, 300);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void recordingButtonActionPerformed(ActionEvent evt) {
		if (recordingButton.isSelected()) {
			PRRecordingProject project = new PRRecordingProject(CFileSystem
					.getExecuteDirectory().findOrCreateDirectory(
							new CPath(this.filenameField.getText())));
			project.setDirFilter(CFileFilter.IGNORE_BY_NAME_FILTER(".*", "CVS",
					"bin"));
			project.setFileFilter(CFileFilter
					.ACCEPT_BY_EXTENSION_FILTER("java"));
			recorder.setProject(project);
			recorder.start();
			if (recorder.isRunning()) {
				recordingButton.setText("Now Recording");
			}
		} else {
			recorder.stop();
			recordingButton.setText("Start");
		}
	}
}
