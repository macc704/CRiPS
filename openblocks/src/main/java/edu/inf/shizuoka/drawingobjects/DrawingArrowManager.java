package edu.inf.shizuoka.drawingobjects;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblocks.BlockConnector;
import edu.mit.blocks.codeblocks.BlockStub;
import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.Page;
import edu.mit.blocks.workspace.Workspace;
import edu.mit.blocks.workspace.WorkspaceEvent;
import edu.mit.blocks.workspace.WorkspaceListener;

public class DrawingArrowManager implements WorkspaceListener {

	private Map<Long, ArrowObject> arrows;
	private boolean isActive = true;
	public static int ARROW_GAP = 15;

	public DrawingArrowManager(){
		arrows = new HashMap<Long, ArrowObject>();
	}

	public  void addArrow(Long id, ArrowObject arrow) {
		arrows.put(id, arrow);
	}

	public  void removArrow(Long id) {
		arrows.remove(id);
	}

	/*
	 * MeRVのON/FFを設定
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
		//再描画
		for(Long id : arrows.keySet()){
			arrows.get(id).setVisible(isActive);
		}
	}

	public boolean isActive() {
		return isActive;
	}

	public  void clearPossessers() {
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

	public void updateArrowColor(Block block){
		if(arrows.get(block.getBlockID()) != null){
			if(hasEmptySocket(block)){
				arrows.get(block.getBlockID()).changeColor(true);
			}else{
				arrows.get(block.getBlockID()).changeColor(false);
			}
		}
	}

	public void workspaceEventOccurred(WorkspaceEvent event) {
		if(event.getEventType() == WorkspaceEvent.BLOCK_ADDED && event.getWorkspace().getEnv().getBlock(event.getSourceBlockID()).getGenusName().equals("callerprocedure")){
			//callerかつ表示状態ならcalleeとの矢印を作成
			RenderableBlock sourceBlock = event.getWorkspace().getEnv().getRenderableBlock(event.getSourceBlockID());
			RenderableBlock calleeBlock = event.getWorkspace().getEnv().getRenderableBlock(((BlockStub)sourceBlock.getBlock()).getParent().getBlockID());
			ArrowObject arrow = new ArrowObject(sourceBlock, calleeBlock, sourceBlock.isVisible(), isActive(), event.getWorkspace().getBlockCanvas().getCanvas().getBounds());
			arrows.put(sourceBlock.getBlockID(), arrow);
			getActivePage(event.getWorkspace()).addArrow(arrow);

			if(hasEmptySocket(sourceBlock.getBlock())){
				arrow.changeColor(true);
			}
		}

		if(event.getEventType() == WorkspaceEvent.BLOCK_COLLAPSED){
			//可視状態をトグル
			Block sourceBlock = event.getWorkspace().getEnv().getBlock(event.getSourceBlockID());
			toggleArrow(sourceBlock, event.getWorkspace());
		}

		if(event.getEventType() == WorkspaceEvent.BLOCK_REMOVED){
			//矢印削除
			Block sourceBlock = event.getWorkspace().getEnv().getBlock(event.getSourceBlockID());
			Page page = getActivePage(event.getWorkspace());
			if(isCaller(sourceBlock) && hasArrow(sourceBlock.getBlockID())){
				page.removeArrow(arrows.get(event.getSourceBlockID()));
				arrows.remove(event.getSourceBlockID());
			}else if(sourceBlock.isProcedureDeclBlock()){
				//子のarrowを全て削除
				for(long stubID : BlockStub.getStubsOfParent(event.getWorkspace(), sourceBlock)){
					page.removeArrow(arrows.get(stubID));
					arrows.remove(stubID);
				}
			}
			page.getJComponent().repaint();
		}

		if(event.getEventType() == WorkspaceEvent.BLOCKS_CONNECTED || event.getEventType() == WorkspaceEvent.BLOCKS_DISCONNECTED){
			//矢印の濃度を変更
			Block socketBlock = event.getWorkspace().getEnv().getBlock(event.getSourceLink().getSocketBlockID());
			Block plugBlock = event.getWorkspace().getEnv().getBlock(event.getSourceLink().getPlugBlockID());

			if(socketBlock.getGenusName().equals("callerprocedure") && hasArrow(socketBlock.getBlockID())){
				if(hasEmptySocket(socketBlock)){
					arrows.get(socketBlock.getBlockID()).changeColor(true);
				}else{
					arrows.get(socketBlock.getBlockID()).changeColor(false);
				}
			}
			if(plugBlock.getGenusName().equals("callerprocedure") && hasArrow(plugBlock.getBlockID())){
				if(hasEmptySocket(plugBlock)){
					arrows.get(plugBlock.getBlockID()).changeColor(true);
				}else{
					arrows.get(plugBlock.getBlockID()).changeColor(false);
				}
			}
		}


	}

	public boolean isCaller(Block block){
		if(block == null){
			return false;
		}
		return block.getGenusName().equals("callerprocedure");
	}

	public boolean hasArrow(long blockID){
		return arrows.get(blockID) != null;
	}

	public void toggleArrow(Block block, Workspace ws){
		while(block != null){
			for(BlockConnector socket : block.getSockets()){
				Block socketBlock = ws.getEnv().getBlock(socket.getBlockID());
				toggleArrow(socketBlock, ws);
			}

			if(isCaller(block) && hasArrow(block.getBlockID())){
				toggleVisible(block.getBlockID(), ws.getEnv().getRenderableBlock(block.getBlockID()).isVisible());
			}
			block = ws.getEnv().getBlock(block.getAfterBlockID());
		}
	}

	public Page getActivePage(Workspace ws){
		for(Page page : ws.getBlockCanvas().getPages()){
			if(page.isInFullview()){
				return page;
			}
		}
		return null;
	}

}
