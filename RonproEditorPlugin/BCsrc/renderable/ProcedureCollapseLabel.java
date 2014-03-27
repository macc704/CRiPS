package renderable;

import codeblocks.BlockConnectorShape;

/**
 * ProcedureCollapseLabel is a label that can be added to a renderable block that 
 * will cause all blocks after this block to be hidden from view when 
 * the isCollapsed parameter is true.
 * 
 *
 */
class ProcedureCollapseLabel extends CollapseLabel {

	private static final long serialVersionUID = 1L;

	ProcedureCollapseLabel(long blockID) {
		super(blockID);
	}

	/**
	 * setup current visual state of button 
	 */
	public void update() {
		RenderableBlock rb = RenderableBlock.getRenderableBlock(getBlockID());

		if (rb != null) {
			int x = 0;
			int y = 0;

			y += rb.getBlockHeight() / rb.getZoom() - 22 + (isActive() ? BlockConnectorShape.CONTROL_PLUG_HEIGHT : 0);
			x += 12;
			x = rb.rescale(x);
			y = rb.rescale(y);

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
	 * Sets visibility of afterBlocks and sockets of a procedure block
	 */
	public void updateCollapse() {
		RenderableBlock rb = RenderableBlock.getRenderableBlock(getBlockID());

		if (rb != null) {
			collapseAfterBlocks(rb.getBlockID());
			rb.repaintBlock();
			if (rb.getHighlightHandler() != null) {
				rb.getHighlightHandler().updateImage();
				if (rb.getHighlightHandler().getParent() != null
						&& rb.getHighlightHandler().getParent().getParent() != null)
					rb.getHighlightHandler().getParent().getParent().repaint(); //force redraw to erase highlight
			}

		}
	}
}
