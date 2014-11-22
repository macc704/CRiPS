package edu.inf.shizuoka.debugger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import net.unicoen.interpreter.Engine;
import net.unicoen.interpreter.ExecutionListener;
import net.unicoen.node.UniClassDec;
import net.unicoen.node.UniFuncDec;
import net.unicoen.node.UniMemberDec;
import net.unicoen.node.UniNode;
import net.unicoen.parser.blockeditor.ToBlockEditorParser;

public class ProgramRunnner extends Thread{
	private File selectedFile;
	
	private static BlockEditorDebbugger stepDebugger;
	
	public ProgramRunnner(File selectedFile){
		this.selectedFile = selectedFile;
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
		stepDebugger = new BlockEditorDebbugger();
		debugger.add(stepDebugger);
		
		engine.listeners = debugger;
		engine.execute(dec);		
	}

}
