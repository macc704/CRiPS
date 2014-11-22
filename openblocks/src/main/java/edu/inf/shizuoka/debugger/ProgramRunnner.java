package edu.inf.shizuoka.debugger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.unicoen.interpreter.Engine;
import net.unicoen.interpreter.ExecutionListener;
import net.unicoen.node.UniClassDec;
import net.unicoen.node.UniFuncDec;
import net.unicoen.node.UniMemberDec;
import net.unicoen.node.UniNode;
import net.unicoen.parser.blockeditor.ToBlockEditorParser;
import edu.mit.blocks.codeblocks.BlockConnector;
import edu.mit.blocks.renderable.RenderableBlock;

public class ProgramRunnner extends Thread{
	private File selectedFile;
	
	private static BlockEditorDebbugger stepDebugger;
	
	private DebuggerWorkspaceController wc;
	
	public ProgramRunnner(File selectedFile, DebuggerWorkspaceController wc){
		this.selectedFile = selectedFile;
		this.wc = wc;
	}
	
	
	public void run(){
		if(selectedFile==null){
			throw new RuntimeException("ファイルが選択されていません");
		}
		List<UniNode> list = ToBlockEditorParser.parse(selectedFile);
		UniClassDec dec = new UniClassDec();
		dec.members = new ArrayList<UniMemberDec>();
		for (UniNode node : list) {
			dec.members.add((UniFuncDec) node);
		}

		Engine engine = new Engine();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		engine.out = new PrintStream(baos);
		
		List<ExecutionListener> debugger = new ArrayList<ExecutionListener>();
		stepDebugger = new BlockEditorDebbugger(getStartMethodBlock(), wc.getWorkspace());
		debugger.add(stepDebugger);
		
		engine.listeners = debugger;
		engine.execute(dec);		
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
