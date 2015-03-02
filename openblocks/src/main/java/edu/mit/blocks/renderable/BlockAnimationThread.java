package edu.mit.blocks.renderable;

import edu.mit.blocks.codeblocks.BlockConnector;
import edu.mit.blocks.codeblocks.BlockLinkChecker;


public class BlockAnimationThread extends Thread {

	protected RenderableBlock block;
	protected RenderableBlock parentBlock;
	protected static boolean isRun = false;
	protected static int distance = 192;
	protected String animationDirection;
	protected static int heightDistance = 100;

	public BlockAnimationThread(RenderableBlock targetBlock, String direction) {
		block = targetBlock;
		animationDirection = direction;
	}
	

	public static boolean isRun() {
		return isRun;
	}

	public void run() {
		isRun = true;

		if (animationDirection.equals("right")) {
			rightSlideAnimation();
		} else if (animationDirection.equals("down")) {
			downSlideAnimation();
		}
		isRun = false;
	}

	private void rightSlideAnimation() {
		int initX = block.getX();
		int x = block.getX() + 1;
		int y = block.getY();

		double realWaitTime = 1.0;

		long waitTime = 1;

		try {
			while (block.getX() < initX + distance) {
				block.setLocation(x, y);
				if ((int) (((block.getX() - initX) * 100 / distance)) % 25 == 24) {
					if (realWaitTime * 1.7 < 10) {
						realWaitTime *= 1.7;
						waitTime *= realWaitTime;
					}
				}
				Thread.sleep(waitTime);
				x++;
			}
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
