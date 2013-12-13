package app.jws;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.comentcrawler.CommentCrawleStrategy;
import framework.DropStrategy;

public class JWSCreaterStrategy implements DropStrategy {

	@Override
	public void dropPerformed(List<File> files) throws Exception {
		if (files.size() != 1) {
			throw new RuntimeException("ドロップできるディレクトリは1つのみです");
		}
		File dir = files.get(0);
		if (!dir.isDirectory()) {
			throw new RuntimeException("ファイルはドロップできません");
		}

		File[] zipFiles = dir.listFiles();
		List<File> zipList = transList(zipFiles);

		CommentCrawleStrategy commentCrawle = new CommentCrawleStrategy();

		JWSCreater creater = new JWSCreater(zipList, "./jws/",
				commentCrawle.crawleComment(dir));
		creater.createJWS();
	}

	private List<File> transList(File[] files) {
		List<File> list = new ArrayList<File>();
		for (int i = 0; i < files.length; i++) {
			list.add(files[i]);
		}
		return list;
	}

}
