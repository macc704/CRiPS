package clearcash.actions;

import javax.swing.JOptionPane;

import org.eclipse.ui.IWorkbenchWindow;

import ppv.app.datamanager.PPDataManager;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileSystem;

// TODO libのフォルダ設定
// TODO ZIPのフォルダ構成
// TODO バックグラウンド処理
public class ClearCashManager {

	private String PPV_ROOT_DIR = CFileSystem.getHomeDirectory()
			.findOrCreateDirectory(".ppvdata").getAbsolutePath().toString();

	private PPDataManager ppDataManager;

	public ClearCashManager(IWorkbenchWindow window) {
		clearCash();
	}

	public void clearCash() {
		// 確認ダイアログ
		int res = JOptionPane.showConfirmDialog(null,
				"Cashの削除には時間がかかりますが，よろしいですか？", "cashの削除",
				JOptionPane.OK_CANCEL_OPTION);
		if (res != JOptionPane.OK_OPTION) {
			return;
		}

		// cashを削除している進捗ダイヤログを利用したいので，PPDataManagerの関数を呼ぶ
		CDirectory ppvRoot = CFileSystem.findDirectory(PPV_ROOT_DIR);

		this.ppDataManager = new PPDataManager(ppvRoot);
		try {
			ppDataManager.clearCompileCash();
		} catch (Exception ex) {
			throw new RuntimeException("cashが削除できませんでした．");
		}
	}
}
