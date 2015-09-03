package edu.mit.blocks.codeblocks;

import edu.mit.blocks.workspace.WorkspaceEnvironment;

public class ParamRule implements LinkRule{

	public boolean canLink(Block block1, Block block2, BlockConnector socket1,
			BlockConnector socket2) {
		WorkspaceEnvironment ws = block1.getWorkspace().getEnv();
		//既にブロックがくっついている場合は，結合しない
		if(ws.getRenderableBlock(socket1.getBlockID()) != null || ws.getRenderableBlock(socket2.getBlockID()) != null){
			return false;
		}

		if("procedure".equals(block1.getGenusName()) && block2.getGenusName().startsWith("proc-param")){
			socket1.setKind(socket2.getKind());
			return true;
		}

		if("procedure".equals(block2.getGenusName()) && block1.getGenusName().startsWith("proc-param")){
			socket2.setKind(socket1.getKind());
			return true;
		}

		return false;
	}

	public boolean isMandatory() {
		return false;
	}


}
