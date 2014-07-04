/*
 * REDialog.java
 * Created on 2007/09/18 by macchan
 * Copyright(c) 2007 CreW Project
 */
package ronproeditor.dialogs;

import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;

import ronproeditor.REApplication;

/**
 * REDialog
 */
public class REDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private REApplication application;

	public REDialog(JFrame owner, REApplication application) {
		super(owner, true);
		this.application = application;
	}

	public REApplication getApplication() {
		return application;
	}

	protected final void setWindowAtCenter() {
		Window window = getOwner();
		int selfW = this.getSize().width;
		int selfH = this.getSize().height;

		int ownerW = window.getWidth();
		int ownerH = window.getHeight();

		int x = window.getX() + ownerW / 2 - selfW / 2;
		int y = window.getY() + ownerH / 2 - selfH / 2;

		this.setLocation(x, y);
	}

}
