package renderable;

import java.awt.event.MouseEvent;

import codeblocks.Block;
import codeblocks.BlockConnector;

public class CollapseLabel extends BlockControlLabel {

	private static final long serialVersionUID = 1L;

	CollapseLabel(long blockID) {
		super(blockID);
	}

	/**
	 * setup current visual state of button
	 */
	public void update() {

	}

	/**
	 * Reverses visibility of afterBlocks and sockets of a procedure block (if
	 * present) when collapse label clicked
	 */
	protected void collapseBlockAndStack() {
		updateCollapse();
	}

	/**
	 * Sets visibility of afterBlocks and sockets of a procedure block
	 */
	public void updateCollapse() {

	}

	/**
	 * Toggles visibility of all afterBlocks and their sockets of the given
	 * blockID
	 */
	protected void collapseAfterBlocks(long blockID) {
		Block block = Block.getBlock(blockID);

		if (block.getAfterBlockID() != Block.NULL) {
			do {
				block = Block.getBlock(block.getAfterBlockID());
				collapseBlock(block.getBlockID());
			} while (block.getAfterBlockID() != Block.NULL);
		}
	}

	/**
	 * Collapse the block corresponding to @param blockID and the blocks connect
	 * to its sockets.
	 * 
	 * @param blockID
	 */
	protected void collapseBlock(long blockID) {
		RenderableBlock rBlock;
		
		rBlock = RenderableBlock.getRenderableBlock(blockID);
		rBlock.setVisible(!isActive());

		
		if (rBlock.hasComment()
				&& rBlock.getComment().getCommentLabel().isActive()) {
			rBlock.getComment().setVisible(!isActive());
		}
		rBlock.getHighlightHandler().updateImage();
		rBlock.repaintBlock();
		if (rBlock.isCollapsed()) {
			return;
		}
		collapseSockets(blockID);
	}
	
	protected void updatePoint(long blockID){
		RenderableBlock rBlock = RenderableBlock.getRenderableBlock(blockID);
		rBlock.updateEndArrowPoints(blockID, isActive());
	}

	/**
	 * Toggles visibility of all blocks connected to sockets NB Sockets on
	 * procedure blocks do not have afterBlocks
	 */
	protected void collapseSockets(Long blockID) {
		Block block = Block.getBlock(blockID);

		for (BlockConnector socket : block.getSockets()) {
			if (socket.getBlockID() != Block.NULL) {
				collapseBlock(socket.getBlockID());
				collapseAfterBlocks(socket.getBlockID());
			}
		}
	}

	/**
	 * Implement MouseListener interface toggle collapse state of block if
	 * button pressed
	 */
	public void mouseClicked(MouseEvent e) {
		toggle();
		collapseBlockAndStack();
		update();
	}

	/**
	 * マウスクリック以外でブロックを閉じるとき
	 */
	public void blockCollapse() {
		toggle();
		collapseBlockAndStack();
		update();
	}
}
