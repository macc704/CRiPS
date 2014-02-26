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

	CCCompileErrorManager manager;
	CCAchivementFrame achivementPanel;

	public CCAchivementButton(CCCompileErrorManager manager, JLabel label) {
		super();
		this.manager = manager;
		add(label);
		addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		achivementPanel = new CCAchivementFrame(manager);
		achivementPanel.openFrame();
		achivementPanel.setVisible(true);
	}
}