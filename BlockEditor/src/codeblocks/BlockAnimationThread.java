package codeblocks;

import renderable.RenderableBlock;

public class BlockAnimationThread extends Thread {

	private int endX;
	private RenderableBlock block;
	private static boolean isRun = false;
	private int distance;

	public BlockAnimationThread(int end, RenderableBlock targetBlock) {
		endX = end;
		block = targetBlock;
		distance = endX - block.getX();
	}

	public static boolean isRun() {
		return isRun;
	}

	public void run() {
		isRun = true;

		int initX = block.getX();
		int x = block.getX() + 1;
		int y = block.getY();

		double realWaitTime = 1.0;

		for (BlockConnector socket : BlockLinkChecker
				.getSocketEquivalents(block.getBlock())) {
			if (socket.hasBlock()) {
				RenderableBlock rb = RenderableBlock.getRenderableBlock(socket
						.getBlockID());
				BlockAnimationThread t1 = new BlockAnimationThread(rb.getX()
						+ distance, rb);
				t1.start();
			}
		}

		long waitTime = 1;
		try {
			while (block.getX() < endX) {
				block.setLocation(x, y);
				if ((int) (((block.getX() - initX) * 100 / distance)) % 25 == 24) {
					realWaitTime *= 1.7;
					waitTime *= realWaitTime;
				}
				//decWaitTime(initX, distance, realWaitTime, waitTime);

				//addWaitTime(initX, distance, realWaitTime, waitTime);
				Thread.sleep(waitTime);
				x++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		isRun = false;
	}

}
