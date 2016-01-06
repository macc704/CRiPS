package edu.inf.shizuoka.blocks.syntaxchecker;

import edu.mit.blocks.codeblocks.Block;

public interface SyntaxChecker {

	public boolean check(Block block);
	public String getErrContext();
	
}
