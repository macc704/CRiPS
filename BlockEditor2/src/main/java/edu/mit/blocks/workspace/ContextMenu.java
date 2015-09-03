package edu.mit.blocks.workspace;

import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPopupMenu;

import edu.inf.shizuoka.blocks.extent.SContextMenuProvider;
import edu.mit.blocks.renderable.RenderableBlock;

/**
 * ContextMenu handles all the right-click menus within the Workspace.
 * TODO ria enable customization of what menu items appear, fire events depending
 * on what items are clicked (if we enabled the first feature)
 *
 * TODO ria still haven't enabled the right click menu for blocks
 */
public class ContextMenu extends PopupMenu implements ActionListener {

    private static final long serialVersionUID = 328149080421L;
    private final static String ADD_COMMENT_BLOCK = "ADDCOMMENT";
    private final static String REMOVE_COMMENT_BLOCK = "REMOVECOMMENT";
    private final static String ARRANGE_ALL_BLOCKS = "ARRANGE_ALL_BLOCKS";
    /** The JComponent that launched the context menu in the first place */
    private static Object activeComponent = null;

    //privatize the constructor
    private ContextMenu() {
    }

    /**
     * Returns the right click context menu for the specified JComponent.  If there is
     * none, returns null.
     * @param o JComponent object seeking context menu
     * @return the right click context menu for the specified JComponent.  If there is
     * none, returns null.
     */
    public static JPopupMenu getContextMenuFor(Object o) {
		// arrenged by sakai lab 2011/11/17
		if (o instanceof RenderableBlock) {
			return new SContextMenuProvider((RenderableBlock) o).getPopupMenu();
		}

//        if (o instanceof RenderableBlock) {
//            if (((RenderableBlock) o).hasComment()) {
//                if (!removeCommentMenuInit) {
//                    initRemoveCommentMenu();
//                }
//                activeComponent = o;
//                return removeCommentMenu;
//            } else {
//                if (!addCommentMenuInit) {
//                    initAddCommentMenu();
//                }
//                activeComponent = o;
//                return addCommentMenu;
//            }
//        } else if (o instanceof BlockCanvas) {
//            if (!canvasMenuInit) {
//                initCanvasMenu();
//            }
//            activeComponent = o;
//            return canvasMenu;
//        }
        return null;
    }

    public void actionPerformed(ActionEvent a) {
        if (a.getActionCommand() == ARRANGE_ALL_BLOCKS) {
            //notify the component that launched the context menu in the first place
            if (activeComponent != null && activeComponent instanceof BlockCanvas) {
                ((BlockCanvas) activeComponent).arrangeAllBlocks();
            }
        } else if (a.getActionCommand() == ADD_COMMENT_BLOCK) {
            //notify the renderableblock componenet that lauched the conetxt menu
            if (activeComponent != null && activeComponent instanceof RenderableBlock) {
                ((RenderableBlock) activeComponent).addComment();
            }
        } else if (a.getActionCommand() == REMOVE_COMMENT_BLOCK) {
            //notify the renderableblock componenet that lauched the conetxt menu
            if (activeComponent != null && activeComponent instanceof RenderableBlock) {
                ((RenderableBlock) activeComponent).removeComment();
            }
        }
    }
}
