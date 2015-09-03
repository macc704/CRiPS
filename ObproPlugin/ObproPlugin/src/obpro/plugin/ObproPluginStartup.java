package obpro.plugin;

import org.eclipse.ui.IStartup;

public class ObproPluginStartup implements IStartup {

	public void earlyStartup() {
		// このクラスがロードされた時点で ObproPlugin#start が呼ばれるので、
		// このメソッドの中身は空にしておいて、 ObproPlugin#start に updateLibrary を書いても
		// 問題はないが、意味的にここに記述しておく。
		try {
			ObproPlugin.getDefault().updateLibrary();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
