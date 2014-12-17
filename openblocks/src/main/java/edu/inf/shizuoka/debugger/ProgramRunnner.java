package edu.inf.shizuoka.debugger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.unicoen.interpreter.Engine;
import net.unicoen.interpreter.ExecutionListener;
import net.unicoen.node.UniClassDec;
import edu.mit.blocks.codeblocks.BlockConnector;
import edu.mit.blocks.renderable.RenderableBlock;

public class ProgramRunnner extends Thread{
	private static UniClassDec classDec;
	
	private static BlockEditorDebbugger stepDebugger;
	
	private DebuggerWorkspaceController wc;
	
	public ProgramRunnner(UniClassDec dec, DebuggerWorkspaceController wc){
		classDec = dec;
		this.wc = wc;
	}
	
	
	public void run(){
		if(classDec == null){
			throw new RuntimeException("null class exe");
		}
		Engine engine = new Engine();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		engine.out = new PrintStream(baos);
		
		List<ExecutionListener> debugger = new ArrayList<ExecutionListener>();
		stepDebugger = new BlockEditorDebbugger(wc.getWorkspace());
		debugger.add(stepDebugger);
		
		engine.listeners = debugger;
		engine.execute(classDec);
		engine.listeners.clear();
	}
	
	public RenderableBlock getStartMethodBlock(){
		Iterable<RenderableBlock> blocks = wc.getWorkspace().getBlockCanvas().getBlocks();
		if(blocks == null){
			throw new RuntimeException("cant find any blocks");
		}
		Iterator<RenderableBlock> renderableBlocks = blocks.iterator();
		
		while(renderableBlocks.hasNext()){
			RenderableBlock rb = renderableBlocks.next();
			if(isStartMethod(rb)){
				return rb;
			}
		}
		return null;
	}
	
	private boolean isStartMethod(RenderableBlock rb){
		return "procedure".equals(rb.getBlock().getGenusName()) && "start".equals(rb.getBlock().getBlockLabel()) && !hasParam(rb); 
	}
	
	public boolean hasParam(RenderableBlock block){
		Iterable<BlockConnector> params = block.getBlock().getSockets();
		if(params == null){
			return false;
		}
		
		Iterator<BlockConnector> paramsConnectors = params.iterator();
		
		if(paramsConnectors.hasNext()){
			paramsConnectors.next();
			if(paramsConnectors.hasNext()){
				return true;
			}
		}
		
		return false;
	}

}
