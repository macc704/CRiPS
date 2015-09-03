package app.unzipmoodle;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import framework.DropStrategy;


public class UnzipMoodleStrategy implements DropStrategy {

	@Override
	public void dropPerformed(List<File> files) throws Exception {
		for(File file: files){
            File outFile = createOutputDir(file);
			Unzip.unzip(file, outFile);
		}
	}

	private File createOutputDir(File inFile) {
		if(inFile.getParent() == null){
			throw new IllegalArgumentException("getParent is null");
		}
		String outDirName = inFile.getName().substring(0, inFile.getName().lastIndexOf('.'));
		File outDir = new File(inFile.getParent() + File.separator + outDirName);
		// 既にフォルダが作られていたら別名のフォルダを作成
		if(outDir.exists()){
			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			int hour = calendar.get(Calendar.HOUR);
			int minute = calendar.get(Calendar.MINUTE);
			int second = calendar.get(Calendar.SECOND);
			String newName = outDir.getName() + "_" + year + month + day + hour + minute + second;
			outDir = new File(outDir.getParent(), newName);
		}
		boolean result = outDir.mkdir(); 
		if(!result) {
			throw new RuntimeException("cannot make dir");
		}
		return outDir;
	}
}
