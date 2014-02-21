package app.comentcrawler;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import au.com.bytecode.opencsv.CSVWriter;
import framework.DropStrategy;

public class CommentCrawleStrategy implements DropStrategy {

	@Override
	public void dropPerformed(List<File> files) throws Exception {
		if (files.size() != 1) {
			throw new RuntimeException("ドロップできるディレクトリは1つのみです");
		}
		File dir = files.get(0);
		if (!dir.isDirectory()) {
			throw new RuntimeException("ファイルはドロップできません");
		}
		Map<String, String> comments = crawleComment(dir);
		File csvFile = new File(dir.getParent(), dir.getName() + ".csv");
		saveCSV2(comments, csvFile);
	}

	public Map<String, String> crawleComment(File dir) throws Exception {
		Map<String, String> comments = new LinkedHashMap<String, String>();
		for (File file : dir.listFiles()) {
			if (!file.isFile() || !file.getName().endsWith(".zip")) {
				continue;
			}
			String comment = getComment(file);
			String name = file.getName();
			comments.put(name, comment);
		}
		return comments;
	}

	private String getComment(File file) throws Exception {
		ZipFile zipFile = new ZipFile(file);
		ZipEntry commentEntry = zipFile.getEntry(".comment.txt");
		if (commentEntry == null) {
			return "N/A";
		}
		InputStream is = zipFile.getInputStream(commentEntry);
		InputStreamReader in = new InputStreamReader(is);
		StringBuffer stringBuffer = new StringBuffer();
		int c;
		while ((c = in.read()) > -1) {
			stringBuffer.append((char) c);
		}
		in.close();
		return stringBuffer.toString();
	}

	private void saveCSV2(Map<String, String> comments, File outFile)
			throws Exception {
		CSVWriter writer = new CSVWriter(new FileWriter(outFile),
				CSVWriter.DEFAULT_SEPARATOR, CSVWriter.DEFAULT_QUOTE_CHARACTER,
				System.getProperty("line.separator"));
		for (String name : comments.keySet()) {
			String comment = comments.get(name);
			writer.writeNext(new String[] { name, comment });
		}
		writer.close();
	}

}
