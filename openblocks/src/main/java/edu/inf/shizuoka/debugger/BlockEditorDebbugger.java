package edu.inf.shizuoka.debugger;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import net.unicoen.interpreter.ExecutionListener;
import net.unicoen.interpreter.Scope;
import net.unicoen.node.UniExpr;
import net.unicoen.node.UniFuncDec;
import net.unicoen.node.UniMethodCall;
import net.unicoen.node.UniNode;
import net.unicoen.parser.blockeditor.UniToBlockParser;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.Workspace;

public class BlockEditorDebbugger implements ExecutionListener {

	private static boolean flag = false;
	private RenderableBlock executingBlock;
	private Workspace ws;
	private static Map<String, Element> map;
//	private Map<UniNode, Node> uniMap = new HashMap<UniNode,Node>();
	
	public BlockEditorDebbugger(RenderableBlock startBlock, Workspace ws) {
		this.executingBlock = startBlock;
		executingBlock.getHilightHandler().setHighlightColor(Color.YELLOW);
		this.ws = ws;
		map = UniToBlockParser.getAddedModels();
	}

	public void setExeCutingBlock(RenderableBlock block) {
		executingBlock = block;
	}

	@Override
	public void preExecute(UniNode node, Scope scope) {
		System.out.println(node);
		while (!flag) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//nodeに対応したRenderableBlockをハイライトする
		executingBlock.getHilightHandler().resetHighlight();
		Element element = UniToBlockParser.getAddedModels().get(Integer.toString(node.hashCode()));
		if(element != null){
			System.out.println(Integer.valueOf(element.getAttributes().getNamedItem("id").getNodeValue()));
			int value = Integer.valueOf(element.getAttributes().getNamedItem("id").getNodeValue());
			executingBlock = ws.getEnv().getRenderableBlock(Integer.toUnsignedLong(value));
			executingBlock.getHilightHandler().setHighlightColor(Color.YELLOW);
		}
		
		flag = false;
	}

	public static void setFlag(boolean state) {
		flag = state;
	}

	@Override
	public void postExecute(UniNode node, Scope scope, Object value) {

		System.out.println("post" + node);
//		if (node instanceof UniExpr) {
//		}
	}

	public RenderableBlock getNextBlock(UniNode node,
			RenderableBlock executingBlock) {
		if (node instanceof UniFuncDec) {
			throw new RuntimeException("ファンクションのパースは未実装");
		} else if (node instanceof UniExpr) {
			parseExpr((UniExpr) node, executingBlock);
		} else {
			throw new RuntimeException("未実装のUNINODEをパース");
		}

		return null;
	}

	public RenderableBlock parseExpr(UniExpr expr,
			RenderableBlock executingBlock) {
		if (expr instanceof UniMethodCall) {
			UniMethodCall caller = (UniMethodCall) expr;
			return calcMethod(executingBlock, caller.methodName);
		}
		return null;
	}

	public RenderableBlock calcMethod(RenderableBlock block, String name) {
		RenderableBlock afterBlock = ws.getEnv().getRenderableBlock(
				block.getBlock().getAfterBlockID());
		if (afterBlock != null
				&& afterBlock.getBlock().getBlockLabel().equals(name)) {
			return afterBlock;
		}

		return block;
	}

	@Override
	public void preExecuteAll(Scope global) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postExecuteAll(Scope global) {
		// TODO Auto-generated method stub
		
	}

}
