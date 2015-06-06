package edu.inf.shizuoka.debugger;

import java.awt.Color;
import java.util.Map;

import net.unicoen.interpreter.ExecutionListener;
import net.unicoen.interpreter.Scope;
import net.unicoen.node.UniNode;

import org.w3c.dom.Element;

import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.Workspace;

public class BlockEditorDebbugger implements ExecutionListener {

	private static boolean flag = false;
	private RenderableBlock executingBlock;
	private Workspace ws;
	private Map<String, Element> blocks;

	public BlockEditorDebbugger(Workspace ws, Map<String, Element> blocks) {
		this.ws = ws;
		this.blocks = blocks;
	}

	public void setExeCutingBlock(RenderableBlock block) {
		executingBlock = block;
	}

	public void preExecute(UniNode node, Scope scope) {
		if (node != null) {
			// nodeに対応したRenderableBlockをハイライトする
			Element element = blocks.get(Integer.toString(node.hashCode()));

			if (element != null) {
				Long value = Long.valueOf(element.getAttributes().getNamedItem("id").getNodeValue());
//				String kind = element.getAttributes().getNamedItem("kind").getNodeValue();
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
			if (executingBlock != null) {
				executingBlock.getHilightHandler().resetHighlight();
			}
			flag = false;
		}
	}

	public static void setFlag(boolean state) {
		flag = state;
	}

	@Override
	public void postExecute(UniNode node, Scope scope, Object value) {
	}

	@Override
	public void preExecuteAll(Scope global) {

	}

	@Override
	public void postExecuteAll(Scope global) {

	}

}
