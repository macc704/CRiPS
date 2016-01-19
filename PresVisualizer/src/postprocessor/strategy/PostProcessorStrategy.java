package postprocessor.strategy;

import java.io.File;
import java.util.List;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFileSystem;
import clib.common.filesystem.CFilename;
import clib.common.table.CCSVFileIO;
import clib.view.framework.DropStrategy;

public class PostProcessorStrategy implements DropStrategy {

	// private CFile absorption;

	@Override
	public void dropPerformed(List<File> files) throws Exception {

		// prepare directory
		if (files.size() != 1) {
			throw new RuntimeException("ドロップできるディレクトリは1つのみです");
		}
		if (!files.get(0).isDirectory()) {
			throw new RuntimeException("ファイルはドロップできません");
		}
		CDirectory dir = CFileSystem.findDirectory(files.get(0).getAbsolutePath());

		// 入力
		CFile in = dir.findOrCreateFile("FileMetrics.csv");
		String basename = in.getName().getName();
		List<List<String>> table = CCSVFileIO.loadAsListList(in);

		// 処理
		ProjectNameProcessor pjNameProcessor = new ProjectNameProcessor(dir);
		table = pjNameProcessor.process(table);
		save(dir, basename + "_1NameConverted", table);// debug
		NayosePreProcessor nayosePreProcessor = new NayosePreProcessor(dir);
		nayosePreProcessor.process(table);
		save(dir, basename + "_2NayosePreProcessed", table);// debug
		NayoseProcessor nayoseProcessor = new NayoseProcessor();
		table = nayoseProcessor.process(table);
		save(dir, basename + "_3NayoseProcessed", table);// debug

		// 出力
		BlockProcessor blockProcessor = new BlockProcessor(dir);
		List<List<String>> saveTable;
		// AllWorkingTime(min)
		saveTable = blockProcessor.process(table, "AllWorkingTime");
		save(dir, basename + "AllWorkingTime", saveTable);

		// BlockWorkingTimeRate
		saveTable = blockProcessor.process(table, "BlockWorkingTimeRate");
		save(dir, basename + "BlockWorkingTimeRate", saveTable);

		// CompileCorrectTime(min)
		saveTable = blockProcessor.process(table, "CompileCorrectTime");
		save(dir, basename + "CompileCorrectTime", saveTable);

		// CompileCorrectTimeRate
		saveTable = blockProcessor.process(table, "CompileCorrectTimeRate");
		save(dir, basename + "CompileCorrectTimeRate", saveTable);

	}

	void save(CDirectory dir, String name, List<List<String>> table) {
		CFile out = dir.getParentDirectory().findOrCreateFile(new CFilename(name, "csv"));
		CCSVFileIO.saveByListList(table, out);
	}

}
