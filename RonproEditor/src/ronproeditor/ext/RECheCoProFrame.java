package ronproeditor.ext;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JToggleButton;

import ronproeditor.REApplication;
import ronproeditor.views.REFrame;

public class RECheCoProFrame extends REFrame {

	private static final long serialVersionUID = 1L;

	private REApplication application;
	private REApplication chApplication;
	private String user;

	public RECheCoProFrame(REApplication application, String user) {
		super(application);
		this.application = application;
		this.user = user;
	}

	public void doOpen(String dirPath) {
		chApplication = application.doOpenNewRE(dirPath);
		initializeCHFrame();
	}

	public void initializeCHFrame() {
		setTitle("CheCoPro Editor");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		initializeCHListeners();
		initializeCHMenu();
	}

	private void initializeCHListeners() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
			}
		});

		chApplication.getSourceManager().addPropertyChangeListener(
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {

					}
				});
	}

	private void initializeCHMenu() {
		JMenuBar menuBar = chApplication.getFrame().getJMenuBar();
		menuBar.getMenu(3).remove(4);

		JToggleButton connButton = new JToggleButton("同期中", true);
		JButton fileRequestButton = new JButton("ファイル要求");
		menuBar.add(connButton);
		menuBar.add(fileRequestButton);
	}

	public String getUser() {
		return user;
	}
}
