package ronproeditor.ext;

import javax.swing.JFrame;

import ronproeditor.REApplication;

public class RECheCoProViewer {

	public static final String APP_NAME = "CheCoProViewer";
	public static final String CH_DIR_PATH = "MyProjects/.CH";
	
	private REApplication application;
	private String user;
	
	public RECheCoProViewer(String user) {
		this.user = user;
	}

	public void init() {
		initCHFrame();
	}
	
	private void initCHFrame() {
		application.getFrame().setTitle(user + "-" + APP_NAME);
		application.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	public REApplication doOpenNewCH(REApplication application) {
		this.application = application.doOpenNewRE(CH_DIR_PATH + "/" + user);
		init();
		return this.application;
	}

	public REApplication getApplication() {
		return application;
	}

	public void setApplication(REApplication application) {
		this.application = application;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	
}
