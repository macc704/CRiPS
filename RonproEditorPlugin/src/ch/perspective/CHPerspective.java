package ch.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class CHPerspective implements IPerspectiveFactory{

	@Override
	public void createInitialLayout(IPageLayout layout) {
		// エディタ領域を取得
        String editorArea = layout.getEditorArea();

        // エディタ領域の左側にリソース・ナビゲータビューを表示
        layout.addView(IPageLayout.ID_RES_NAV
                    , IPageLayout.LEFT, 0.25f, editorArea);

        // エディタ領域の下側にブックマークビューを表示
        layout.addView(IPageLayout.ID_BOOKMARKS
                , IPageLayout.BOTTOM, 0.75f, editorArea);
	}

}
