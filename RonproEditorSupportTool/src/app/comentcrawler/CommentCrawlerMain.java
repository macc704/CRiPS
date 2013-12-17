package app.comentcrawler;

import framework.DnDFramework;

public class CommentCrawlerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DnDFramework.open("コメント収集", "フォルダをドロップしてください",
				new CommentCrawleStrategy());
	}

}
