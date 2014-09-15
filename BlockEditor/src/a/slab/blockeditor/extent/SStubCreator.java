/*
 * SValueCreator.java
 * Created on 2011/11/17
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package a.slab.blockeditor.extent;

import java.awt.event.ActionEvent;

import renderable.RenderableBlock;
import workspace.Workspace;
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
		createStub(stubName, rb);
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
				
				rb.getParentWidget().addBlock(newRB);
				
				Workspace.getInstance().getWorkSpaceController().disposeTraceLine();
				Workspace.getInstance().getWorkSpaceController().showTraceLine();
				
				return newRB;
			}
		}
		throw new RuntimeException("stub not found: " + stubName);
	}
}
