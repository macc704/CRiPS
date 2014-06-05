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
       layout.addView("org.eclipse.ui.console.ConsoleView", IPageLayout.BOTTOM, 0.80f, editorArea);
       
       IFolderLayout editorRight = layout.createFolder("editorRight", IPageLayout.RIGHT, 0.50f, editorArea);
       editorRight.addView(IPageLayout.ID_BOOKMARKS);
       
       // メンバエディタ領域の右側にフォルダを追加
       IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.75f, "editorRight");
       right.addView(IPageLayout.ID_TASK_LIST);
	}

}
