package edu.inf.shizuoka.blocks.syntaxchecker;

import java.util.ArrayList;
import java.util.List;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.codeblocks.BlockConnector;

public class BlockSyntaxErrorChecker {

	public List<SyntaxChecker> checkers = new ArrayList<>();
	private List<ErrorInformation> errors = new ArrayList<>();
	
	public void addChecker(SyntaxChecker checker){
		this.checkers.add(checker);
	}
	/**
	 * キャンバス上の全ブロックの構文をチェックする
	 * @param iterable キャンバス上の全ブロック
	 */
	public List<ErrorInformation> checkSyntax(Iterable<Block> iterable){
		errors.clear();
		List<Block> procedures = new ArrayList<>();
		for(Block block : iterable){
			if(block.isProcedureDeclBlock()){
				procedures.add(block);	
			}
		}
		
		for(Block procedureBlock : procedures){
			checkAllBlocks(procedureBlock);
		}
		
		return errors;
	}
	
	public List<ErrorInformation> getErrors(){
		return this.errors;
	}
	
	public void removeError(Block block){
		for(ErrorInformation err : errors){
			if(block.getBlockID().equals(err.geterrBlock().getBlockID())){
				for(SyntaxChecker checker : checkers){
					if(!checker.check(block)){
						errors.remove(err);
						block.getWorkspace().getEnv().getRenderableBlock(block.getBlockID()).resetHighlight();
						return;
					}
				}
			}
		}
	}
	
	/**
	 * 全てのブロックをチェックする
	 * @param block
	 */
	public void checkAllBlocks(Block block){
		if(block == null){
			return;
		}		
		Block target = block;
		checkBlock(target);
		while(target != null && target.getAfterBlockID() != Block.NULL){
			target = block.getWorkspace().getEnv().getBlock(target.getAfterBlockID());
			checkBlock(target);
		}
	}
	
	public void checkBlock(Block block){
		if(block == null){
			return;
		}		
		Block tmp = block;
		for(SyntaxChecker checker : checkers){
			if(checker.check(block)){
				this.errors.add(new ErrorInformation(checker.getErrContext(), block));
			}
		}
		for(BlockConnector socket : tmp.getSockets()){
			Block socketBlock = tmp.getWorkspace().getEnv().getBlock(socket.getBlockID());
			checkAllBlocks(socketBlock);
		}		
	}
	
}
