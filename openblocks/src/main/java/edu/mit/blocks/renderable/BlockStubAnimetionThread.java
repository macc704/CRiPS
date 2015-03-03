package edu.mit.blocks.renderable;

import java.awt.Color;

import edu.mit.blocks.codeblocks.BlockConnector;
import edu.mit.blocks.codeblocks.BlockLinkChecker;
import edu.mit.blocks.codeblocks.BlockStub;

public class BlockStubAnimetionThread extends BlockAnimationThread {

	private RenderableBlock parentBlock;
	
	public BlockStubAnimetionThread(BlockStub targetStubBlock, String direction ) {
		super(targetStubBlock.getWorkspace().getEnv().getRenderableBlock(targetStubBlock.getBlockID()), direction);
		this.parentBlock = targetStubBlock.getWorkspace().getEnv().getRenderableBlock(targetStubBlock.getParent().getBlockID());
	}
	
	public void run() {
		isRun = true;

		if (animationDirection.equals("right")) {
		} else if (animationDirection.equals("down")) {
			downSlideAnimation();
		}
		isRun = false;
	}
	
	private void downSlideAnimation() {
		int initY = block.getY();
		int x = block.getX();
		int y = block.getY() + 1;

		double realWaitTime = 1.0;

		for (BlockConnector socket : BlockLinkChecker
				.getSocketEquivalents(block.getBlock())) {
			if (socket.hasBlock()) {
				RenderableBlock rb = block.getWorkspace().getEnv().getRenderableBlock(socket
						.getBlockID());
				BlockAnimationThread t1 = new BlockAnimationThread(rb,
						animationDirection);
				t1.start();
			}
		}

		long waitTime = 1;

		try {
			parentBlock.getHilightHandler().setHighlightColor(Color.RED);
			while (block.getY() < initY + heightDistance) {
				block.setLocation(x, y);
				if ((int) (((block.getY() - initY) * 100 / heightDistance)) % 25 == 24) {
					realWaitTime *= 1.7;
					waitTime *= realWaitTime;
				}
				Thread.sleep(waitTime);
				y++;
			}
			block.getHilightHandler().resetHighlight();
			parentBlock.getHilightHandler().resetHighlight();
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
