package edu.mit.blocks.renderable;

import java.awt.Color;

import edu.mit.blocks.codeblocks.BlockConnectorShape;
import edu.mit.blocks.workspace.Workspace;

public class HeaderLabel extends BlockLabel {

	private boolean editableHeaderLabel = false;

	public HeaderLabel(Workspace workspace,String initLabelText, BlockLabel.Type labelType,
			boolean isEditable, long blockID) {
		super(workspace ,initLabelText, labelType, isEditable, blockID, true, new Color(
				255, 255, 225));
	}

	void update() {
		RenderableBlock rb = workspace.getEnv().getRenderableBlock(getBlockID());
		if (rb != null) {
//			RenameHeaderLabel(rb);
			int x = 0;
			int y = 0;
			if (rb.getBlock().isCommandBlock())
				x += 5;
			if (rb.getBlock().isDeclaration())
				x += 12;
			if (rb.getBlock().hasPlug())
				x += 4 + BlockConnectorShape.getConnectorDimensions(rb
						.getBlock().getPlug()).width;
			if (rb.getBlock().isInfix()) {
				if (!rb.getBlock().getSocketAt(0).hasBlock()) {
					x += 30;
				} else {
					x += rb.getSocketSpaceDimension(rb.getBlock()
							.getSocketAt(0)).width;
				}
			}

			if (rb.getBlockWidget() == null)
				y += rb.getAbstractBlockArea().getBounds().height / 2;
			else
				y += 12;

			if (rb.getBlock().isCommandBlock())
				y -= 2;
			if (rb.getBlock().hasPageLabel()
					&& rb.getBlock().hasAfterConnector())
				y -= BlockConnectorShape.CONTROL_PLUG_HEIGHT;
			if (!rb.getBlock().hasPageLabel())
				y -= getAbstractHeight() / 2;

			// Comment Label and Collapse Label take up some additional amount of space
			x += rb.getControlLabelsWidth();

			// if block is collapsed keep the name label from moving
			y += (rb.isCollapsed() ? BlockConnectorShape.CONTROL_PLUG_HEIGHT / 2
					: 0);

			x = rescale(x);
			y = rescale(y);
			setPixelLocation(x, y);
		}
	}

//	/**
//	 * Rename header label of this block
//	 * 
//	 * @param RenderableBlock
//	 */
//	private void RenameHeaderLabel(RenderableBlock rb) {
//		if (rb.getBlock().isObjectTypeVariableDeclBlock()
//				&& !editableHeaderLabel) {
//			for (BlockConnector socket : rb.getBlock().getSockets()) {
//				if (socket.getBlockID() == Block.NULL) {
//					BlockGenus blockGenus = BlockGenus.getGenusWithName(rb
//							.getGenus());
//
//					rb.getHeaderLabel()
//							.setText(blockGenus.getInitHeaderLabel());
//
//					continue;
//				}
//				RenderableBlock socketRBlock = RenderableBlock
//						.getRenderableBlock(socket.getBlockID());
//				//TODO 型に応じた変数の切り替えはココでしている．
//				if (socketRBlock.getGenus().equals(
//						"callObjectMethodlocal-var-object")) {
//					rb.getHeaderLabel().setText("Object型の変数つくり、");
//				} else {
//					if (!socketRBlock.getBlockLabel().getText().equals("null")) {
//						if (socketRBlock.getBlock().getGenusName()
//								.startsWith("new-")) {
//							rb.getHeaderLabel().setText(
//									socketRBlock.getBlockLabel().getText()
//											+ "型の変数をつくり、");
//						} else {
//							if (socketRBlock.getBlock().getGenusName().startsWith(
//									"getter-arrayelement")) {
//								if (Block.getBlock(socketRBlock.getBlockID())
//										.getJavaType() != null) {
//									String type = Block.getBlock(
//											socketRBlock.getBlockID())
//											.getJavaType();
//									type = type.substring(0, type.indexOf("[]"));
//									
//									rb.getHeaderLabel().setText(type + "型の変数をつくり、");
//
//								}
//							} else {
//								if (Block.getBlock(socketRBlock.getBlockID())
//										.getJavaType() != null) {
//									rb.getHeaderLabel().setText(
//											Block.getBlock(
//													socketRBlock.getBlockID())
//													.getJavaType()
//													+ "型の変数をつくり、");
//
//								}
//							}
//						}
//					}
//
//				}
//			}
//			textChanged(rb.getHeaderLabel().getText());
//		} else {
//			textChanged(rb.getHeaderLabel().getText());
//		}
//	}

//	@Override
//	protected void textChanged(String text) {
//		if (getBlockID() == Block.NULL) {
//			return;
//		}
//		Block b = RenderableBlock.getRenderableBlock(getBlockID()).getBlock();
//		b.setHeaderLabel(text);
//	}
}
