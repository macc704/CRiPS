package ch.perspective;


import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class CHPerspective implements IPerspectiveFactory{

	@Override
	public void createInitialLayout(IPageLayout layout) {
		// エディタ領域を取得
		String editorArea = layout.getEditorArea();

		// ショートカット追加
		layout.addPerspectiveShortcut("ch.perspective");

		// エディタ領域の左側にフォルダを追加
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.15f, editorArea);
		left.addView(JavaUI.ID_PACKAGES);

		// エディタ領域の下側にコンソールを表示
		IFolderLayout console = layout.createFolder("conlose", IPageLayout.BOTTOM, 0.80f, editorArea);
		console.addView("org.eclipse.ui.console.ConsoleView");

		IFolderLayout membersEditor = layout.createFolder("membersEditor", IPageLayout.RIGHT, 0.60f, editorArea);
		membersEditor.addView(IPageLayout.ID_BOOKMARKS);

		// メンバエディタ領域の右側にフォルダを追加
		left.addView("ch.memberDirectoryView");

		// コンソールの左にメンバの状態を表示
		IFolderLayout memberState = layout.createFolder("memberState", IPageLayout.BOTTOM, 0.70f, "left");
		memberState.addView("ch.memberStateView");
		
		// メンバ状態ビューにPreferenceをスタック
		memberState.addView("ch.preferenceView");
	}

}
