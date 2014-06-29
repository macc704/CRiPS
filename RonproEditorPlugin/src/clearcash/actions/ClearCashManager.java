package clearcash.actions;

import javax.swing.JOptionPane;

import org.eclipse.ui.IWorkbenchWindow;

import ronproeditorplugin.Activator;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileSystem;
import createcocodata.actions.PPDataManager2;

// TODO libのフォルダ設定
// TODO ZIPのフォルダ構成
// TODO バックグラウンド処理
public class ClearCashManager {

	private String PPV_ROOT_DIR = CFileSystem.getHomeDirectory()
			.findOrCreateDirectory(".ppvdata").getAbsolutePath().toString();

	private PPDataManager2 ppDataManager2;

	public ClearCashManager(IWorkbenchWindow window) {
		if (Activator.getDefault().getcompileErrorCashCreating()) {
			JOptionPane.showMessageDialog(null, "Compile Cash作成・削除中です");
			return;
		} else {
			Activator.getDefault().setcompileErrorCashCreating(true);
		}

		Thread thread = new Thread() {
			public void run() {
				clearCash();
			}
		};

		thread.start();
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

		this.ppDataManager2 = new PPDataManager2(ppvRoot);
		try {
			ppDataManager2.clearCompileCash();
		} catch (Exception ex) {
			throw new RuntimeException("cashが削除できませんでした．");
		} finally {
			Activator.getDefault().setcompileErrorCashCreating(false);
		}
	}
}
