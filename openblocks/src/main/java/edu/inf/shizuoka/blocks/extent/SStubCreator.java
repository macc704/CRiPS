/*
 * SValueCreator.java
 * Created on 2011/11/17
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package edu.inf.shizuoka.blocks.extent;

import java.awt.event.ActionEvent;

import edu.mit.blocks.codeblocks.Block;
import edu.mit.blocks.renderable.RenderableBlock;
import edu.mit.blocks.workspace.WorkspaceEvent;



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

	public RenderableBlock doWork(ActionEvent e) {
		return create(e);
	}
	
	private RenderableBlock create(ActionEvent e) {
		return createStub(stubName, rb);
	}

	public static RenderableBlock createStub(String stubName, RenderableBlock rb) {
		Block b = rb.getBlock();
		for (String stubGenus : b.getStubList()) {
			if (stubGenus.startsWith(stubName)) {
				Block createB = b.createFreshStub(stubGenus);
				RenderableBlock newRB = new RenderableBlock(createB.getWorkspace(), null,
						createB.getBlockID());
				newRB.setLocation(rb.getX() + 20, rb.getY() + 20); // 新しく生成するブロックのポジション

				newRB.setParentWidget(rb.getParentWidget());

				rb.getParentWidget().addBlock(newRB);

				//イベント通知
				newRB.getWorkspace().notifyListeners(new WorkspaceEvent(newRB.getWorkspace(), newRB.getParentWidget(), newRB.getBlockID(), WorkspaceEvent.BLOCK_ADDED));
				
				return newRB;
			}
		}
		throw new RuntimeException("stub not found: " + stubName);
	}
}
