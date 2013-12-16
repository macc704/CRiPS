package codeblocks;

import renderable.RenderableBlock;
import bc.BCSystem;

public class BlockAnimationThread extends Thread {

	private RenderableBlock block;
	private static boolean isRun = false;
	private static int distance = 192;
	private String animationDirection;
	private static int heightDistance = 100;

	public BlockAnimationThread(RenderableBlock targetBlock, String direction) {
		block = targetBlock;
		animationDirection = direction;
	}

	public static boolean isRun() {
		return isRun;
	}

	public void run() {
		BCSystem.out.println("animationthread start");
		isRun = true;

		if (animationDirection.equals("right")) {
			rightSlideAnimation();
		} else if (animationDirection.equals("down")) {
			downSlideAnimation();
		}

		block.resetHighlight();
		BCSystem.out.println("animation end");
		isRun = false;
	}

	private void rightSlideAnimation() {
		int initX = block.getX();
		int x = block.getX() + 1;
		int y = block.getY();

		BCSystem.out.println("distance:" + distance);
		double realWaitTime = 1.0;

		for (BlockConnector socket : BlockLinkChecker
				.getSocketEquivalents(block.getBlock())) {
			if (socket.hasBlock()) {
				RenderableBlock rb = RenderableBlock.getRenderableBlock(socket
						.getBlockID());
				BlockAnimationThread t1 = new BlockAnimationThread(rb,
						animationDirection);
				//t1.start();
			}
		}

		long waitTime = 1;

		try {
			while (block.getX() < initX + distance) {
				BCSystem.out.println("location:" + block.getLocation());
				BCSystem.out.println("waitTime:" + waitTime);
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
				RenderableBlock rb = RenderableBlock.getRenderableBlock(socket
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
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
