/*
 * BStopWatch.java
 * Created on 2007/05/24 by macchan
 * Copyright(c) 2007 CreW Project
 */
package obpro.common;

/**
 * BStopWatch
 */
public class BStopWatch {

	private long startTime = 0;
	private long stopTime = 0;

	/**
	 * スタートする
	 */
	public void start() {
		startTime = System.nanoTime();
	}

	/**
	 * ストップする
	 */
	public void stop() {
		stopTime = System.nanoTime();
	}

	/**
	 * かかった時間を取得する
	 */
	public long getTimeByMiliseconds() {
		return (stopTime - startTime) / 1000000;
	}
}
