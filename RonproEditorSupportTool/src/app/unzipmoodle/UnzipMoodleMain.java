package app.unzipmoodle;

import framework.DnDFramework;

public class UnzipMoodleMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DnDFramework.open("UnzipMoodle", "zipファイルをドロップしてください",
				new UnzipMoodleStrategy());

	}

}
