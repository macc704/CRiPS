package edu.inf.shizuoka.blocks.syntaxchecker;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblocks.BlockConnector;
import net.unicoen.parser.blockeditor.blockmodel.BlockExCallerModel;

public class SocketChecker implements SyntaxChecker {

	private static String ERR_CONTEXT = "のソケットにブロックが結合されていません！";
	
	/**
	 * ブロックに空のソケットがないかどうかチェックする．
	 * @return エラーが有る時はtrue,無いときはfalseを返す
	 */
	@Override
	public boolean check(Block block) {
		boolean hasError = false;
		if(!block.isProcedureDeclBlock() && !block.isVariableDeclBlock() && !block.getGenusName().equals("return")){
			//check has empty socket
			for(BlockConnector connector : block.getSockets()){
				if(connector.getBlockID() == Block.NULL){
					hasError |= enableEmptySocket(connector.getKind(), block);
				}
			}
		}
		return hasError;
	}

	public boolean enableEmptySocket(String connectorKind, Block block){
		if(connectorKind.equals("cmd")){
			String genusName = block.getGenusName();
			if(genusName.equals(BlockExCallerModel.GENUS_NAME)){
				return true;
			}else{
				return false;
			}
		}else{
			return true;			
		}
	}
	
	@Override
	public String getErrContext() {
		return ERR_CONTEXT;
	}

}
