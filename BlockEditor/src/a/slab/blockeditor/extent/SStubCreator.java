/*
 * SValueCreator.java
 * Created on 2011/11/17
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package a.slab.blockeditor.extent;

import java.awt.event.ActionEvent;

import renderable.RenderableBlock;
import codeblocks.Block;

/**
 * @author macchan
 */
public class SStubCreator {

	private RenderableBlock rb;
	private String stubName;

	public SStubCreator(String stubName, RenderableBlock rb) {
		this.stubName = stubName;
		this.rb = rb;
	}

	public void doWork(ActionEvent e) {
		create(e);
	}

	private void create(ActionEvent e) {
		RenderableBlock newRB = createStub(stubName, rb);
	}

	public static RenderableBlock createStub(String stubName, RenderableBlock rb) {
		Block b = rb.getBlock();
		for (String stubGenus : b.getStubList()) {
			if (stubGenus.startsWith(stubName)) {
				Block createB = b.createFreshStub(stubGenus);
				RenderableBlock newRB = new RenderableBlock(null,
						createB.getBlockID());

				newRB.setLocation(rb.getX() + 20, rb.getY() + 20); // 新しく生成するブロックのポジション

				newRB.setParentWidget(rb.getParentWidget());
				//所属するprocedureブロックのid
				newRB.setParentProcedureID(rb.getParentProcedureID());
				//書いてあるブロックコネクターを読み込む仕組み
				//newRB.getBlock().getSocketAt
				/*if(stubGenus.startsWith("setter")){//#ohata とりあえず
					if(stubGenus.endsWith("int-number")){
						RenderableBlock initRB = SContextMenuProvider.createNewBlock(newRB.getParentWidget(), "number");
						initRB.setLocation(rb.getX()  + newRB.getBlockWidth(),rb.getY() + 20);
						initRB.setParentWidget(rb.getParentWidget());
						SContextMenuProvider.connectByPlug(newRB, 0, initRB);
					}else if(stubGenus.endsWith("string")) {
						RenderableBlock initRB = SContextMenuProvider.createNewBlock(newRB.getParentWidget(), "string");
						initRB.setLocation(rb.getX()  + newRB.getBlockWidth(),rb.getY() + 20);
						initRB.setParentWidget(rb.getParentWidget());
						SContextMenuProvider.connectByPlug(newRB, 0, initRB);
					}else if(stubGenus.endsWith("double-number")){
						RenderableBlock initRB = SContextMenuProvider.createNewBlock(newRB.getParentWidget(), "double-number");
						initRB.setLocation(rb.getX()  + newRB.getBlockWidth(),rb.getY() + 20);
						initRB.setParentWidget(rb.getParentWidget());
						SContextMenuProvider.connectByPlug(newRB, 0, initRB);
					}else if(stubGenus.endsWith("boolean")){
						RenderableBlock initRB = SContextMenuProvider.createNewBlock(newRB.getParentWidget(), "true");
						initRB.setLocation(rb.getX()  + newRB.getBlockWidth(),rb.getY() + 20);
						initRB.setParentWidget(rb.getParentWidget());
						SContextMenuProvider.connectByPlug(newRB, 0, initRB);
					}
					
					
				//	newRB.getNearbyLink().connect();
				}*/

				rb.getParentWidget().addBlock(newRB);
				return newRB;
			}
		}
		throw new RuntimeException("stub not found: " + stubName);
	}
}
