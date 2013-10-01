package codeblocks;

import renderable.RenderableBlock;

public class BlockAnimationThread extends Thread {

	private int endX;
	private RenderableBlock socket;
	private static boolean isRun = false;

	public BlockAnimationThread(int x, RenderableBlock sock) {
		endX = x;
		socket = sock;
	}

	public static boolean isRun() {
		return isRun;
	}

	public void run() {
		isRun = true;

		int initX = socket.getX();
		int x = socket.getX() + 1;
		int y = socket.getY();
		int distance = endX - socket.getX();
		double realWaitTime = 1.0;

		long waitTime = 1;
		try {
			while (socket.getX() < endX) {
				socket.setLocation(x, y);
				if ((int) (((socket.getX() - initX) * 100 / distance)) % 25 == 24) {
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
