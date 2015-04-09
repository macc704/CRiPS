package renderable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import workspace.WorkspaceWidget;
import codeblocks.Block;
import codeblocks.BlockStub;

public class BlockHIlighter{
	
	private static final List<Long> hilightBlocks = new ArrayList<Long>();
	
	public static void catchBlockSetHighlight(RenderableBlock catchedRBlock,
			WorkspaceWidget widget) {

		if(widget == null){
			return;
		}
		
		Block catchedBlock = catchedRBlock.getBlock();

		try {
			if (catchedBlock instanceof BlockStub ) {
				//親ブロックのハイライト
				Block parentBlock = ((BlockStub) catchedBlock).getParent();
				RenderableBlock.getRenderableBlock(parentBlock.getBlockID()).getHighlightHandler().setHighlightColor(Color.YELLOW);

				hilightBlocks.add(parentBlock.getBlockID());
				
				//子ブロックのハイライト
//				hilightAllStubBlocks(parentBlock, catchedBlock, widget);

			}else if(catchedBlock.isVariableDeclBlock() || catchedBlock.isProcedureDeclBlock()){
				hilightAllStubBlocks(catchedBlock, catchedBlock, widget);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void hilightAllStubBlocks(Block parentBlock, Block catchedBlock, WorkspaceWidget widget){
		//子ブロックのハイライト
		for(RenderableBlock rb : widget.getBlocks()){
			Block block = rb.getBlock();
			if(block instanceof BlockStub  && parentBlock.equals(((BlockStub) block).getParent())){
				if(isShouldHilightBlock(block.getGenusName())){
					rb.getHighlightHandler().setHighlightColor(Color.yellow);	
					hilightBlocks.add(rb.getBlockID());
				}
			}
		}
	}

	public static boolean isShouldHilightBlock(String genusName){
		if(genusName.startsWith("setter") || genusName.startsWith("getter") || genusName.startsWith("inc") || genusName.startsWith("caller")){
			return true;
		}else{
			return false;
		}
	}
	
	public static void resetAllHilightedStubBlocks(){
		//子ブロックのハイライトを消す 
		for(Long blockID : hilightBlocks){
			RenderableBlock.getRenderableBlock(blockID).getHighlightHandler().resetHighlight();
		}
		hilightBlocks.clear();
	}

}
