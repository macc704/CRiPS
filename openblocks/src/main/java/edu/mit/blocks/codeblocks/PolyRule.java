package edu.mit.blocks.codeblocks;

import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.WorkspaceEnvironment;
import edu.mit.blocks.workspace.WorkspaceEvent;
import edu.mit.blocks.workspace.WorkspaceListener;

public class PolyRule implements LinkRule,WorkspaceListener {

	@Override
	public boolean canLink(Block block1, Block block2, BlockConnector socket1,
			BlockConnector socket2) {
		WorkspaceEnvironment ws = block1.getWorkspace().getEnv();
		//既にブロックがくっついている場合は，結合しない
		if(ws.getRenderableBlock(socket1.getBlockID()) != null || ws.getRenderableBlock(socket2.getBlockID()) != null){
			return false;
		}
		
		//ソケットの形を変更
		if("poly".equals(socket1.getKind())){
			socket1.setKind(socket2.getKind());
		}else{
			socket2.setKind(socket1.getKind());
		}
		return true;
	}

	@Override
	public boolean isMandatory() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void workspaceEventOccurred(WorkspaceEvent event) {
		//ohata added
		// TODO Auto-generated method stub
		if(event.getEventType() == WorkspaceEvent.BLOCKS_DISCONNECTED && "poly".equals(event.getSourceLink().getSocket().initKind())){
			BlockLink link = event.getSourceLink();
			link.getSocket().setKind("poly");
		}
	}

	
}
