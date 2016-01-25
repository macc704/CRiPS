package edu.mit.blocks.renderable;

import java.awt.Color;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblocks.BlockConnectorShape;
import edu.mit.blocks.workspace.Workspace;


/**
 * created by sakai lab 2014/12/13
 *
 * @author yasui
 *
 *         AbstractCollapseLabel is a label that can be added to a renderable
 *         block that will cause all blocks after this block to be hidden from
 *         view when the isCollapsed parameter is true.
 *
 *         2012.10.06 update()関数にFigure変更の処理があると，冗長でパフォーマンスに影響するので
 *         （オリジナルではそう処理しているけど）．updateCollapse()の時だけ書き換えるようにする．
 */
class AbstractionBlockCollapseLabel extends CollapseLabel {

	private static final long serialVersionUID = 1L;

	AbstractionBlockCollapseLabel(Workspace workspace,long blockID) {
		super(workspace, blockID);
		this.setForeground(new Color(0, 0, 0));
		updateCollapse(true);
	}

	/**
	 * setup current visual state of button
	 */
	@Override
	public void update() {
		//refreshFigure();//original
	}

	@Override
	public void updateCollapse() {
		updateCollapse(false);
	}
	
	private void updateCollapse(boolean init) {
		originalUpdateCollapse();
		refreshFigure();
		if (!init) {
			reformRelatedBlocks();
		}
	}

	// 抽象化ブロックが開閉されたとき関係するブロックをリフォームする
	// 2012.09.25 ループしていたのでひとまず消去（なくてもなんとかなりそう） #matsuzawa
	// 2012.10.05 これがないと抽象化ブロックを閉めたときに親の大きさが変わらない．
	// 2013.09.27 抽象化ブロックが2つ並んだ際に、2つ目の抽象化ブロックがclose状態である場合にNullPointerException発生

	private void reformRelatedBlocks() {
		try {
			Block topBlock = getTopBlock();
			if (topBlock == null) {
				return;
			}
			RenderableBlock topFigure = workspace.getEnv().getRenderableBlock(topBlock.getBlockID());
			//RenderableBlock.getRenderableBlock()
			topFigure.redrawFromTop();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private Block getTopBlock() {
		Block topBlock = workspace.getEnv().getBlock(getBlockID());
		while (true) {
			Block beforeBlock = workspace.getEnv().getBlock(topBlock.getBeforeBlockID());
			if (beforeBlock == null) {
				break;
			}
			topBlock = beforeBlock;
		}

		if (topBlock == null) {
			throw new RuntimeException("ari e nai!");
		}
		if (topBlock.getBlockID().equals(getBlockID())) {
			return null;
		}
		return topBlock;
	}

	//	// 抽象化ブロックが開閉されたとき関係するブロックをリフォームする (保井バージョン)
	//	private void reformRelatedBlocks() {
	//		RenderableBlock beforeRBlock = RenderableBlock
	//				.getRenderableBlock(getBlockID());
	//		while (true) {
	//			long blockId = Block.getBlock(beforeRBlock.getBlockID())
	//					.getBeforeBlockID();
	//			if (blockId == Block.NULL) {
	//				break;
	//			}
	//			Block beforeBlock = Block.getBlock(blockId);
	//			reformBlock(beforeBlock);
	//			beforeRBlock = RenderableBlock.getRenderableBlock(blockId);
	//		}
	//	}
	//
	//	// 抽象化ブロックが開閉されたとき関係するブロックをリフォームする (保井バージョン)
	//	private void reformBlock(Block block) {
	//		if (block.isCommandBlock() || block.isAbstractionBlock()) {
	//			RenderableBlock rb = RenderableBlock
	//					.getRenderableBlock(getBlockID());
	//			rb.repaintBlock();
	//		}
	//	}

	//sakailab. original code was update()
	private void refreshFigure() {
		RenderableBlock rb = workspace.getEnv().getRenderableBlock(getBlockID());

		if (rb != null) {
			int x = 0;
			int y = 0;

			y += 10 + (isActive() ? BlockConnectorShape.CONTROL_PLUG_HEIGHT : 0);
			x += 12;
			x = rb.rescale(x);
			y = rb.rescale(y);

			if (isActive()) {
				y -= 4;
			}

			setLocation(x, y);
			setSize(rb.rescale(14), rb.rescale(14));

			if (isActive()) {
				setText("+");
			} else {
				setText("-");
			}
		}
	}

	/**
	 * Sets visibility of connectorBlocks
	 */
	private void originalUpdateCollapse() {
		RenderableBlock rb = workspace.getEnv().getRenderableBlock(getBlockID());
		if (rb != null) {
			//collapseAfterBlocks(rb.getBlockID());//original
			collapseInsideBlocks();//sakailab
			rb.repaintBlock();
			if (rb.getHighlightHandler() != null) {
				rb.getHighlightHandler().updateImage();
				if (rb.getHighlightHandler().getParent() != null
						&& rb.getHighlightHandler().getParent().getParent() != null)
					// force redraw to erase highlight
					rb.getHighlightHandler().getParent().getParent().repaint();
			}
		}
	}

	// sakailab 2012.10.06
	private void collapseInsideBlocks() {
		Block block = workspace.getEnv().getBlock(getBlockID());
		Long insideFirstBlockId = block.getSocketAt(0).getBlockID();
		if (insideFirstBlockId != Block.NULL) {
			collapseBlock(insideFirstBlockId);
			collapseAfterBlocks(insideFirstBlockId);
		}
	}
	
	@Override
	public void initialCollapseAfterBlocks(Long blockID, Workspace workspace){
		collapseInsideBlocks();
	}
	
	@Override
	public void initialBlockCollapse(Workspace workspace){
		initcallapseBlockAndStack(workspace);
		update();
	}
	
	@Override
	public void initialCollapseBlock(long blockID, Workspace workspace){
		RenderableBlock rBlock;
		
		rBlock = workspace.getEnv().getRenderableBlock(blockID);
		rBlock.setVisible(!isActive());
		
		if (rBlock.hasComment() && rBlock.getComment().getCommentLabel().isActive()) {
			rBlock.getComment().setVisible(!isActive());
		}

		rBlock.getHighlightHandler().updateImage();
		rBlock.repaintBlock();
		
		if (rBlock.isCollapsed()) {
			return;
		}
		
		collapseSockets(blockID);
	}
}
