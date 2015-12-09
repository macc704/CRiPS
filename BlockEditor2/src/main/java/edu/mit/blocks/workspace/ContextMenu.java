package edu.mit.blocks.workspace;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
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
public class ContextMenu extends JPopupMenu implements ActionListener {

    private static final long serialVersionUID = 328149080421L;


	// context menu renderableblocks plus
	// menu items for renderableblock context menu
	private static ContextMenu rndBlockMenu = new ContextMenu();
	private static ContextMenu addCommentMenu = new ContextMenu();
	private static JMenuItem addCommentItem;
	private static boolean addCommentMenuInit = false;
	private static ContextMenu removeCommentMenu = new ContextMenu();
	private static JMenuItem removeCommentItem;
	private static boolean removeCommentMenuInit = false;


	private final static String ADD_COMMENT_BLOCK = "ADDCOMMENT";
	private final static String REMOVE_COMMENT_BLOCK = "REMOVECOMMENT";

    private static Object activeComponent = null;

    //privatize the constructor
    public ContextMenu() {
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
			JPopupMenu menu = new SContextMenuProvider((RenderableBlock) o).getPopupMenu();
			activeComponent = o;
			if (((RenderableBlock) o).hasComment()) {
                if (!removeCommentMenuInit) {
                    initRemoveCommentMenu();
                }
                menu.add(removeCommentItem);
            } else {
                if (!addCommentMenuInit) {
                    initAddCommentMenu();
                }
                menu.add(addCommentItem);
            }
			return menu;
		}

        return null;
    }

    public void actionPerformed(ActionEvent a) {
    	if (a.getActionCommand() == ADD_COMMENT_BLOCK) {
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


	/**
	 * Initializes the context menu for adding Comments.
	 */
	private static void initAddCommentMenu() {
		addCommentItem = new JMenuItem("Add Comment");
		addCommentItem.setActionCommand(ADD_COMMENT_BLOCK);
		addCommentItem.addActionListener(rndBlockMenu);
		addCommentMenu.add(addCommentItem);
		addCommentMenuInit = true;
	}

	/**
	 * Initializes the context menu for deleting Comments.
	 */
	private static void initRemoveCommentMenu() {

		removeCommentItem = new JMenuItem("Delete Comment");
		removeCommentItem.setActionCommand(REMOVE_COMMENT_BLOCK);
		removeCommentItem.addActionListener(rndBlockMenu);

		removeCommentMenu.add(removeCommentItem);
		// rndBlockMenu.add(runBlockItem);

		removeCommentMenuInit = true;
	}

}
