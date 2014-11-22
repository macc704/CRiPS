package edu.inf.shizuoka.debugger;

import java.awt.Color;

import net.unicoen.interpreter.ExecutionListener;
import net.unicoen.interpreter.Scope;
import net.unicoen.node.UniExpr;
import net.unicoen.node.UniFuncDec;
import net.unicoen.node.UniMethodCall;
import net.unicoen.node.UniNode;
import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.Workspace;

public class BlockEditorDebbugger implements ExecutionListener {

	private static boolean flag = false;
	private RenderableBlock executingBlock;
	private Workspace ws;

	public BlockEditorDebbugger(RenderableBlock startBlock, Workspace ws) {
		this.executingBlock = startBlock;
		executingBlock.getHilightHandler().setHighlightColor(Color.YELLOW);
		this.ws = ws;
	}

	public void setExeCutingBlock(RenderableBlock block) {
		executingBlock = block;
	}

	@Override
	public void preExecute(UniNode node, Scope scope) {
		// TODO Auto-generated method stub
//		executingBlock.getHilightHandler().setHighlightColor(Color.YELLOW);

		while (!flag) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(node);
		flag = false;
	}

	public static void setFlag(boolean state) {
		flag = state;
	}

	@Override
	public void postExecute(UniNode node, Scope scope, Object value) {
		// TODO Auto-generated method stub
		System.out.println("post" + node);
//		executingBlock.getHilightHandler().resetHighlight();
//		if (node instanceof UniExpr) {
//			executingBlock = getNextBlock(node, executingBlock);
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

}
