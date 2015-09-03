package workspace;

import java.util.ArrayList;
import java.util.List;

import renderable.RenderableBlock;
import codeblocks.Block;
import codeblocks.BlockConnector;

public class BlockTypeSupporter implements WorkspaceListener{

	private List<RenderableBlock> hilightBlocks = new ArrayList<RenderableBlock>();

	public BlockTypeSupporter(){

	}

	public void workspaceEventOccurred(WorkspaceEvent event) {
		if(event.getEventType() == WorkspaceEvent.BLOCKS_PICKED_UP){

			Block sourceBlock = Block.getBlock(event.getSourceBlockID());
			if(sourceBlock.getPlug() != null && "object".equals(sourceBlock.getPlug().getKind())){
				String sourceBlockPlugType = getPlugType(sourceBlock);
				for(RenderableBlock rb : Workspace.getInstance().getBlockCanvas().getBlocks()){
					Block b = rb.getBlock();
					for(BlockConnector socket : b.getSockets()){
//						if(socket.getConnectorJavaType() != null && socket.getKind().equals("object") && Workspace.getInstance().getWorkSpaceController().getClassRelationMap().couldConnect(sourceBlockPlugType, socket.getConnectorJavaType())){
//							hilightBlocks.add(rb);
//							rb.getHighlightHandler().setHighlightColor(Color.GREEN);
//						}
					}
				}
			}
		}else if(event.getEventType() == WorkspaceEvent.BLOCK_MOVED){
			for(RenderableBlock block : hilightBlocks){
				block.getHighlightHandler().resetHighlight();
			}
			hilightBlocks.clear();
		}
	}

	public String getPlugType(Block block){
		if(block.getGenusName().equals("callGetterMethod2")){
			if(block.getSocketAt(1) != null){
				return getPlugType(Block.getBlock(block.getSocketAt(1).getBlockID()));
			}
		}else{
			if(block.getPlug() != null && block.getPlug().getConnectorJavaType() != null){
				return block.getPlug().getConnectorJavaType();
			}
		}
		return "Object";
	}

}
