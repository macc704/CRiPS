package drawingobjects;

import java.awt.Component;
import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

	private static Map<Long, ArrowObject> arrows = new HashMap<Long, ArrowObject>();
	private static boolean isActive = true;
	public static int ARROW_GAP = 15;
	
	
	public static void addArrow(Long id, ArrowObject arrow) {
		arrows.put(id, arrow);
	}

	public static void removArrow(Long id) {
		arrows.remove(id);
	}
	
	/*
	 * MeRVのON/FFを設定
	 */
	public static void setActive(boolean isActive) {
		DrawingArrowManager.isActive = isActive;
		//再描画
		for(Long id : arrows.keySet()){
			arrows.get(id).setVisible(isActive);
		}
	}

	public static boolean isActive() {
		return isActive;
	}

	public static void clearPossessers() {
		arrows.clear();
	}

	public static void updatePossessers() {

	}

	public static void clearPosesser(ArrowObject arrow) {
		arrows.remove(arrow);
	}

	public static void setVisible(Long id, boolean visible) {
		arrows.get(id).setVisible(visible);
	}
	
	public static void toggleVisible(Long id) {
		ArrowObject arrow = arrows.get(id);
		arrow.toggleVisible();			
		arrow.toggleCollapsed();
	}

	public static void thinArrows(RenderableBlock rBlock) {
//		if(rBlock != null){
//			boolean isThin = calcConcentration(rBlock);
//			if (rBlock.getEndArrows().size() > 0) {
//				Point p = rBlock.getLocation();
//				p.x += rBlock.getWidth();
//				p.y += rBlock.getHeight() / 2;
//
//				for (ArrowObject endArrow : rBlock.getEndArrows()) {
//					endArrow.setStartPoint(p);
//					endArrow.chengeColor(isThin);
//				}
//			}	
//		}
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
				while (rBlock != null && rBlock.getBlock().getPlug()!= null) {
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

	public static boolean hasNoAfterBlock(Block block){
		if(block != null){
			if (block.getAfterBlockID() != -1 || block.getAfterBlockID() != null) {
				return true;
			}	
		}
		return false;
	}
	
	public static boolean isRecursiveFunction(Block topBlock, Block callerBlock){
		if(callerBlock instanceof BlockStub && topBlock.getBlockID().equals(((BlockStub)callerBlock).getParent().getBlockID())){
			return true;
		}
		return false;
	}
	
	public void workspaceEventOccurred(WorkspaceEvent event) {
		if(event.getEventType() == WorkspaceEvent.CALLERBLOCK_CREATED || event.getEventType() == WorkspaceEvent.BLOCK_ADDED){
			//callerかつ表示状態ならcalleeとの矢印を作成
			RenderableBlock sourceBlock = RenderableBlock.getRenderableBlock(event.getSourceBlockID());
			if(sourceBlock.getBlock().getGenusName().equals("callerprocedure")){
				//呼び出し元を取ってくる
				RenderableBlock calleeBlock = RenderableBlock.getRenderableBlock(((BlockStub)sourceBlock.getBlock()).getParent().getBlockID());
				ArrowObject arrow = new ArrowObject(sourceBlock, calleeBlock, sourceBlock.isVisible());
				arrows.put(sourceBlock.getBlockID(), arrow);
				Workspace.getInstance().getPageNamed(Workspace.getInstance().getWorkSpaceController().calcClassName()).addArrow(arrow);
				if(hasEmptySocket(Block.getBlock(sourceBlock.getBlockID()))){
					arrow.chengeColor(true);
				}
			}
		}
		
		if(event.getEventType() == WorkspaceEvent.BLOCK_COLLAPSED){
			RenderableBlock sourceBlock = RenderableBlock.getRenderableBlock(event.getSourceBlockID());
			if(sourceBlock.getBlock().getGenusName().equals("callerprocedure")){
				toggleVisible(sourceBlock.getBlockID());
			}
		}
		
		if(event.getEventType() == WorkspaceEvent.BLOCK_REMOVED){
			Workspace.getInstance().getPageNamed(Workspace.getInstance().getWorkSpaceController().calcClassName()).removeArrow(arrows.get(event.getSourceBlockID()));
			arrows.remove(event.getSourceBlockID());
		}
//		if (event.getEventType() == WorkspaceEvent.BLOCKS_DISCONNECTED || event.getEventType() == WorkspaceEvent.BLOCKS_CONNECTED || event.getEventType() == WorkspaceEvent.BLOCK_MOVED || event.getEventType() == WorkspaceEvent.BLOCK_COLLAPSED) {
//			updatePossessers();
//		}
		
//		if (event.getEventType() == WorkspaceEvent.BLOCK_REMOVED) {
//			removePossesser(RenderableBlock.getRenderableBlock(event.getSourceBlockID()));
//		}
	}

}
