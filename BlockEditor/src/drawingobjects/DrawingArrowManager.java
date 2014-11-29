package drawingobjects;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import renderable.RenderableBlock;
import renderable.ScopeChecker;
import workspace.Workspace;
import workspace.WorkspaceEvent;
import workspace.WorkspaceListener;
import codeblocks.Block;
import codeblocks.BlockConnector;
import codeblocks.BlockStub;
import controller.WorkspaceController;

public class DrawingArrowManager implements WorkspaceListener {

	private static List<RenderableBlock> arrowPossesser = new ArrayList<RenderableBlock>();
	private static boolean isActive = true;

	public static void addPossesser(RenderableBlock possesser) {
		arrowPossesser.add(possesser);
	}

	public static void removePossesser(RenderableBlock possesser) {
		arrowPossesser.remove(possesser);
	}

	public static void setActive(boolean isActive) {
		DrawingArrowManager.isActive = isActive;
	}

	public static boolean isActive() {
		return isActive;
	}

	public static void clearPossessers() {
		for (RenderableBlock block : arrowPossesser) {
			block.clearArrows();
		}
		arrowPossesser.clear();
	}

	public static void updatePossessers() {
		for (RenderableBlock rb : arrowPossesser) {
			rb.updateEndArrowPoint();
		}
	}

	public static void clearPosesser(RenderableBlock posesser) {
		posesser.clearArrows();
		arrowPossesser.remove(posesser);
	}

	public static void setVisible(boolean active) {
		if (active) {
			Workspace.getInstance().getWorkSpaceController().showAllTraceLine();
		} else {
			Workspace.getInstance().getWorkSpaceController().disposeTraceLine();
		}
	}

	public static void resetArrowsPosition() {
		for (RenderableBlock rb : arrowPossesser) {
			rb.resetArrowPosition();
		}
	}

	public static void thinArrows(RenderableBlock rBlock) {
		if(rBlock != null){
			boolean isThin = calcConcentration(rBlock);
			if (rBlock.getEndArrows().size() > 0) {
				Point p = rBlock.getLocation();
				p.x += rBlock.getWidth();
				p.y += rBlock.getHeight() / 2;

				for (ArrowObject endArrow : rBlock.getEndArrows()) {
					endArrow.setStartPoint(p);
					endArrow.chengeColor(isThin);
				}
			}	
		}
	}

	public static boolean calcConcentration(RenderableBlock rBlock) {
		boolean isThin = false;

		//stub
		if (rBlock.getBlock() instanceof BlockStub) {
			//引数がない
			if (hasEmptySocket(rBlock.getBlock())) {
				isThin = true;
			}
			{//孤島かどうか
				while (rBlock != null && rBlock.getBlock().getPlug()!= null  && "SINGLE".equals(rBlock.getBlock().getPlug().getPositionType().toString())) {
					rBlock = RenderableBlock.getRenderableBlock(rBlock.getBlock().getPlugBlockID());
				}
				//rblock == null は独立した引数ブロック 
				if (rBlock == null || ScopeChecker.isIndependentBlock(rBlock.getBlock())) {
					isThin = true;
				}
			}
		}
		return isThin;
	}

	public static boolean hasEmptySocket(Block block) {
		for (Iterator<BlockConnector> itarator = block.getSockets().iterator(); itarator
				.hasNext();) {
			if (!itarator.next().hasBlock()) {
				return true;
			}
		}
		return false;
	}

	public static Point calcCallerBlockPoint(RenderableBlock callerblock) {
		Point p1 = callerblock.getLocation();
		p1.x += callerblock.getWidth();
		p1.y += callerblock.getHeight() / 2;

		return p1;
	}

	public static Point calcDefinisionBlockPoint(RenderableBlock parentBlock) {
		Point p2 = new Point(parentBlock.getLocation());
		p2.y += parentBlock.getHeight() / 2;

		return p2;
	}

	public static void removeArrows(RenderableBlock block) {
		Workspace ws = Workspace.getInstance();
		WorkspaceController wc = ws.getWorkSpaceController();
		removeArrow(block, wc, ws);
		wc.getWorkspace().getPageNamed(wc.calcClassName()).getJComponent()
				.repaint();
	}

	public static void removeArrow(RenderableBlock block,
			WorkspaceController wc, Workspace ws) {
		if (block != null) {
			for (ArrowObject arrow : block.getEndArrows()) {
				ws.getPageNamed(wc.calcClassName()).clearArrow((Object) arrow);
			}
			for (ArrowObject arrow : block.getStartArrows()) {
				ws.getPageNamed(wc.calcClassName()).clearArrow((Object) arrow);
			}

			DrawingArrowManager.clearPosesser(block);

			Iterable<BlockConnector> sockets = block.getBlock().getSockets();
			if (sockets != null) {
				Iterator<BlockConnector> socketConnectors = sockets.iterator();
				while (socketConnectors.hasNext()) {
					removeArrow(
							RenderableBlock.getRenderableBlock(socketConnectors
									.next().getBlockID()), wc, ws);
				}
			}

			if (block.getBlock().getAfterBlockID() != -1) {
				removeArrow(RenderableBlock.getRenderableBlock(block.getBlock()
						.getAfterBlockID()), wc, ws);
			}
		}
	}

	public static boolean isRecursiveFunction(Block topBlock, Block callerBlock){
		if(callerBlock instanceof BlockStub && topBlock.getBlockID().equals(((BlockStub)callerBlock).getParentBlockID())){
				System.out.println(true);
			return true;
		}
		System.out.println(false);
		return false;
	}
	
	public void workspaceEventOccurred(WorkspaceEvent event) {
		if (event.getEventType() == WorkspaceEvent.BLOCKS_DISCONNECTED || event.getEventType() == WorkspaceEvent.BLOCKS_CONNECTED || event.getEventType() == WorkspaceEvent.BLOCK_MOVED) {
			updatePossessers();
		}
		
		if (event.getEventType() == WorkspaceEvent.BLOCK_REMOVED) {
			removePossesser(RenderableBlock.getRenderableBlock(event.getSourceBlockID()));
		}
	}

}
