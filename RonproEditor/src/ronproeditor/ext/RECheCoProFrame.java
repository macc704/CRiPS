package ronproeditor.ext;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JToggleButton;

import ronproeditor.REApplication;
import ronproeditor.views.REFrame;

public class RECheCoProFrame extends REFrame {

	private static final long serialVersionUID = 1L;

	public static final String ROOT_PATH = "MyProjects/.CHProjects/";

	private REApplication application;
	private REApplication chApplication;
	private String user;

	public static void main(String[] args) {
		RECheCoProFrame chFrame = new RECheCoProFrame(new REApplication(), "");
		chFrame.doOpen();
	}

	public RECheCoProFrame(REApplication application, String user) {
		super(application);
		this.application = application;
		this.user = user;
	}

	public void doOpen() {
		chApplication = application.doOpenNewRE(ROOT_PATH + user);
		initializeCHFrame();
	}

	private void initializeCHFrame() {
		getFrame().setTitle("CheCoPro Editor");
		getFrame().setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		initializeCHListeners();
		initializeCHMenu();
	}

	private void initializeCHListeners() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// ボタン復元
			}
		});
	}

	private void initializeCHMenu() {
		JMenuBar menuBar = getFrame().getJMenuBar();
		menuBar.getMenu(3).remove(4);

		JToggleButton connButton = new JToggleButton("同期中", true);
		JButton fileRequestButton = new JButton("ファイル要求");

		fileRequestButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// FilelistReqest
			}
		});

		menuBar.add(connButton);
		menuBar.add(fileRequestButton);
	}

	public String getUser() {
		return user;
	}

	public REFrame getFrame() {
		return chApplication.getFrame();
	}

}
