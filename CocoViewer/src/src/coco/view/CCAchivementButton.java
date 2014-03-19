package src.coco.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

import src.coco.model.CCCompileErrorManager;

public class CCAchivementButton extends JButton implements ActionListener {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	private CCCompileErrorManager manager;
	private CCAchivementFrame achivementPanel;
	private String achiveFilename;

	public CCAchivementButton(CCCompileErrorManager manager,
			String achiveFilename, JLabel label) {
		super();
		this.manager = manager;
		this.achiveFilename = achiveFilename;
		add(label);
		addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		achivementPanel = new CCAchivementFrame(manager, achiveFilename);
		achivementPanel.setVisible(true);
	}
}