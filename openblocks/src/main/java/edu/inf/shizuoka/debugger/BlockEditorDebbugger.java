package edu.inf.shizuoka.debugger;

import java.awt.Color;

import net.unicoen.interpreter.ExecutionListener;
import net.unicoen.interpreter.Scope;
import net.unicoen.node.UniNode;
import net.unicoen.parser.blockeditor.UniToBlockParser;

import org.w3c.dom.Element;

import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.Workspace;

public class BlockEditorDebbugger implements ExecutionListener {

	private static boolean flag = false;
	private RenderableBlock executingBlock;
	private Workspace ws;

	public BlockEditorDebbugger(Workspace ws) {
		this.ws = ws;
	}

	public void setExeCutingBlock(RenderableBlock block) {
		executingBlock = block;
	}

	@Override
	public void preExecute(UniNode node, Scope scope) {
		// nodeに対応したRenderableBlockをハイライトする
		Element element = UniToBlockParser.getAddedModels().get(Integer.toString(node.hashCode()));
		
		if (element != null) {
			long value = Long.valueOf(element.getAttributes().getNamedItem("id").getNodeValue());
			executingBlock = ws.getEnv().getRenderableBlock(value);
			executingBlock.getHilightHandler().setHighlightColor(Color.YELLOW);
		}

		while (!flag) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		executingBlock.getHilightHandler().resetHighlight();
		flag = false;
	}

	public static void setFlag(boolean state) {
		flag = state;
	}

	@Override
	public void postExecute(UniNode node, Scope scope, Object value) {
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
