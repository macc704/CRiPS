package drawingobjects;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import renderable.FactoryRenderableBlock;
import renderable.RenderableBlock;
import workspace.Workspace;
import workspace.WorkspaceEvent;
import workspace.WorkspaceListener;
import codeblocks.Block;
import codeblocks.BlockConnector;
import codeblocks.BlockStub;

public class DrawingArrowManager implements WorkspaceListener {

	private Map<Long, ArrowObject> arrows;
	private boolean isActive = true;
	public static int ARROW_GAP = 15;

	public DrawingArrowManager() {
		arrows = new HashMap<Long, ArrowObject>();
	}

	public void addArrow(Long id, ArrowObject arrow) {
		arrows.put(id, arrow);
	}

	public void removArrow(Long id) {
		arrows.remove(id);
	}

	/*
	 * MeRVのON/FFを設定
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
		//再描画
		for (Long id : arrows.keySet()) {
			arrows.get(id).setVisible(isActive);
		}
	}

	public boolean isActive() {
		return isActive;
	}

	public void clearPossessers() {
		arrows.clear();
	}

	public static void updatePossessers() {

	}

	public void clearPosesser(ArrowObject arrow) {
		arrows.remove(arrow);
	}

	public void setVisible(Long id, boolean visible) {
		arrows.get(id).setVisible(visible);
	}

	public void toggleVisible(Long id, boolean isVisible) {
		ArrowObject arrow = arrows.get(id);
		arrow.toggleVisible(isVisible, isActive);
		arrow.toggleCollapsed(!isVisible);
	}

	public static boolean hasEmptySocket(Block block) {
		for (Iterator<BlockConnector> itarator = block.getSockets().iterator(); itarator.hasNext();) {
			if (!itarator.next().hasBlock()) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasNoAfterBlock(Block block) {
		if (block != null) {
			if (block.getAfterBlockID() != -1|| block.getAfterBlockID() != null) {
				return true;
			}
		}
		return false;
	}

	public static boolean isRecursiveFunction(Block topBlock, Block callerBlock) {
		if (callerBlock instanceof BlockStub
				&& topBlock.getBlockID().equals(
						((BlockStub) callerBlock).getParent().getBlockID())) {
			return true;
		}
		return false;
	}

	public void updateArrowColor(Long blockID) {
		if (hasEmptySocket(Block.getBlock(blockID))) {
			arrows.get(blockID).changeColor(true);
		} else {
			arrows.get(blockID).changeColor(false);
		}
	}

	public void workspaceEventOccurred(WorkspaceEvent event) {
		if (event.getEventType() == WorkspaceEvent.CALLERBLOCK_CREATED || event.getEventType() == WorkspaceEvent.BLOCK_ADDED) {
			//callerかつ表示状態ならcalleeとの矢印を作成
			RenderableBlock sourceBlock = RenderableBlock
					.getRenderableBlock(event.getSourceBlockID());

			if (sourceBlock.getBlock().getGenusName().equals("callerprocedure")&& !(sourceBlock instanceof FactoryRenderableBlock)) {
				RenderableBlock calleeBlock = RenderableBlock.getRenderableBlock(((BlockStub) sourceBlock.getBlock()).getParent().getBlockID());

				ArrowObject arrow = new ArrowObject(sourceBlock, calleeBlock,sourceBlock.isVisible(), isActive());
				arrows.put(sourceBlock.getBlockID(), arrow);
				Workspace.getInstance().getPageNamed(Workspace.getInstance().getWorkSpaceController().calcClassName()).addArrow(arrow);
				changeColor(sourceBlock.getBlock());
			}
		}

		if (event.getEventType() == WorkspaceEvent.BLOCK_COLLAPSED) {
			//可視状態をトグル
			Block sourceBlock = Block.getBlock(event.getSourceBlockID());
			if (isCaller(sourceBlock) && hasArrow(sourceBlock.getBlockID())) {
				toggleVisible(sourceBlock.getBlockID(), RenderableBlock
						.getRenderableBlock(sourceBlock.getBlockID())
						.isVisible());
			}
		}

		if (event.getEventType() == WorkspaceEvent.BLOCK_REMOVED) {
			//矢印削除
			Block sourceBlock = Block.getBlock(event.getSourceBlockID());
			if (isCaller(sourceBlock) && hasArrow(sourceBlock.getBlockID())) {
				Workspace.getInstance().getPageNamed(Workspace.getInstance().getWorkSpaceController().calcClassName()).removeArrow(arrows.get(event.getSourceBlockID()));
				arrows.remove(event.getSourceBlockID());
			} else if (sourceBlock.isProcedureDeclBlock()) {
				//子のarrowを全て削除
				for (long stubID : BlockStub.getStubsOfParent(sourceBlock
						.getBlockID())) {
					if (hasArrow(stubID)) {
						Workspace.getInstance().getPageNamed(Workspace.getInstance().getWorkSpaceController().calcClassName()).removeArrow(arrows.get(stubID));
						arrows.remove(stubID);
					}
				}
			}
		}

		if (event.getEventType() == WorkspaceEvent.BLOCKS_CONNECTED
				|| event.getEventType() == WorkspaceEvent.BLOCKS_DISCONNECTED) {
			//矢印の濃度を変更
			for (long id : arrows.keySet()) {
				changeColor(Block.getBlock(id));
			}
		}

	}

	public void changeColor(Block block) {
		if (block != null) {
			if (!hasEmptySocket(block) && !isCallerBlockIsIndependent(block)) {
				arrows.get(block.getBlockID()).changeColor(false);
			} else {
				arrows.get(block.getBlockID()).changeColor(true);
			}
		}

	}

	public boolean isCallerBlockIsIndependent(Block block) {
		Block commandblock = RenderableBlock.getCommandBlock(block);
		return !(RenderableBlock.getTopBlock(commandblock).getBlock()
				.isProcedureDeclBlock());
	}

	public boolean isCaller(Block block) {
		return block.getGenusName().equals("callerprocedure");
	}

	public boolean hasArrow(long blockID) {
		return arrows.get(blockID) != null;
	}

	public void reset() {
		arrows.clear();
	}

}
