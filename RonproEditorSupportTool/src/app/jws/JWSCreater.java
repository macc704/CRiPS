package app.jws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JWSCreater {

	private String targetPath = "";

	private List<File> inputFiles;
	private List<File> outputFiles;

	private Map<String, String> commentMap;

	public JWSCreater(List<File> inputFiles, String targetPath,
			Map<String, String> commentMap) {
		this.inputFiles = inputFiles;
		this.targetPath = targetPath;
		this.commentMap = commentMap;
	}

	public void createJWS() {
		new File(targetPath).mkdir();
		try {
			copyFiles(inputFiles);
		} catch (IOException e) {
			e.printStackTrace();
		}

		File[] files = new File(targetPath).listFiles();
		this.outputFiles = transJarFile(files);

		// create
		createJNLPFiles();
		createHTMLFile();

	}

	private void copyFiles(List<File> files) throws IOException {
		for (File f : files) {
			String name = f.getName();
			copyFile(f.getAbsolutePath(), targetPath + name);
		}
	}

	private void copyFile(String sourcePath, String targetPath)
			throws IOException {
		FileInputStream sourceFs = new FileInputStream(sourcePath);
		FileChannel sourceChannel = sourceFs.getChannel();
		FileOutputStream targetFs = new FileOutputStream(targetPath);
		FileChannel targetChannel = targetFs.getChannel();
		sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
		sourceFs.close();
		targetFs.close();
	}

	private List<File> transJarFile(File[] files) {
		List<File> list = new ArrayList<File>();
		for (File f : files) {
			if (f.getName().endsWith("zip")) {
				String name = f.getName().substring(0,
						f.getName().indexOf("zip"))
						+ "jar";
				File file = new File(targetPath + name);
				f.renameTo(file);
				list.add(file);
			}
		}
		return list;
	}

	private void createJNLPFiles() {
		for (File file : outputFiles) {
			JNLPFile f = new JNLPFile(file.getName(), targetPath);
			f.outputFile();
		}
	}

	private void createHTMLFile() {
		HTMLFile f = new HTMLFile(outputFiles, targetPath, commentMap);
		f.output();
	}

}
