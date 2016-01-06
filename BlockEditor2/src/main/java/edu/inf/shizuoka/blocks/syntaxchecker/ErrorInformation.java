package edu.inf.shizuoka.blocks.syntaxchecker;

import edu.mit.blocks.codeblocks.Block;

public class ErrorInformation {

	private String errContext = "";
	private Block errBlock;

	public ErrorInformation(String errContext, Block errBlock){
		this.errContext = errContext;
		this.errBlock = errBlock;
	}

	public Block geterrBlock(){
		return this.errBlock;
	}
	
	public String getErrContext(){
		return errBlock.getBlockLabel() + this.errContext;
	}
	
}
