package edu.inf.shizuoka.debugger;

import net.unicoen.interpreter.ExecutionListener;
import net.unicoen.interpreter.Scope;
import net.unicoen.node.UniNode;

public class BlockEditorDebbugger implements ExecutionListener{

	private static boolean flag = false;
	
	
	@Override
	public void preExecute(UniNode node, Scope scope) {
		// TODO Auto-generated method stub
		while(!flag){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//ワークスペースをハイライトする
		System.out.println(node);
		flag = false;
	}
	
	public static void setFlag(boolean state){
		flag = state;
	}

	@Override
	public void postExecute(UniNode node, Scope scope, Object value) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
