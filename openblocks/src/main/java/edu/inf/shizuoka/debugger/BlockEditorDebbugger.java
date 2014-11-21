package edu.inf.shizuoka.debugger;

import net.unicoen.interpreter.ExecutionListener;
import net.unicoen.interpreter.Scope;
import net.unicoen.node.UniNode;

public class BlockEditorDebbugger implements ExecutionListener{

	@Override
	public void preExecute(UniNode node, Scope scope) {
		// TODO Auto-generated method stub
		System.out.println(node);
	}

	@Override
	public void postExecute(UniNode node, Scope scope, Object value) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
