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

		IFolderLayout membersEditor = layout.createFolder("membersEditor", IPageLayout.RIGHT, 0.50f, editorArea);
		membersEditor.addView(IPageLayout.ID_BOOKMARKS);

		// メンバエディタ領域の右側にフォルダを追加
		IFolderLayout memberProjects = layout.createFolder("memberProjects", IPageLayout.RIGHT, 0.75f, "membersEditor");
		memberProjects.addView(IPageLayout.ID_TASK_LIST);

		// コンソールの右にPreferenceを表示
		layout.addView("ch.preferenceView", IPageLayout.RIGHT, 0.80f, "console");
	}

}
