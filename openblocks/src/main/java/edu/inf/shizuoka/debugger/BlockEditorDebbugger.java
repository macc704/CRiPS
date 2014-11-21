package edu.inf.shizuoka.debugger;

import java.util.ArrayList;
import java.util.List;

import net.unicoen.interpreter.Engine;
import net.unicoen.node.UniClassDec;
import net.unicoen.node.UniExpr;
import net.unicoen.node.UniFuncDec;
import net.unicoen.node.UniMemberDec;
import net.unicoen.node.UniMethodCall;
import net.unicoen.node.UniNode;

public class BlockEditorDebbugger {
	
	private static List<UniNode> exeNodes;
	private static Engine engine = new Engine();
	
	
	public static void setClass(UniClassDec exeClass){
		exeNodes = new ArrayList<UniNode>();
		UniFuncDec start = null;
		for(UniMemberDec member : exeClass.members){
			//startメソッドなら実行メソッド
			if(member instanceof UniFuncDec && "starts".equals(((UniFuncDec) member).funcName) && (((UniFuncDec) member).args) == null){
				start = (UniFuncDec)member;
			}
		}
		if(start != null){
			addFunctionNodes(start);
			
		}else{
			throw new RuntimeException(""); 
		}
		
	}
	
	private static void addFunctionNodes(UniFuncDec function){
		for(UniExpr expr : function.body){
			exeNodes.add(expr);
			if(expr instanceof UniMethodCall){
//				UniMethodCall caller = (UniMethodCall)expr;
				//ユーザー定義メソッドであれば，exeNodesに追加する
			}
		}
	}
	
	public static void stepExecute(){
		
		
	}
	
	
	
}
