package app.filenametranslator;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import framework.DropStrategy;

public class FileNameTranslatorStrategy implements DropStrategy {

	// private static String PATH = "./lecture/";
	private static String path = "./lecture/";

	private File zipFile = null;
	private File csvFile = null;

	// private String lectureNumber = "";

	@Override
	public void dropPerformed(List<File> files) throws Exception {
		if (files.size() < 2) {
			throw new RuntimeException("ドロップするファイルが足りません");
		} else if (files.size() == 2) {
			for (File f : files) {
				if (f.getName().endsWith(".zip")) {
					zipFile = f;
				} else if (f.getName().endsWith(".csv")) {
					csvFile = f;
				}
			}

			if (zipFile == null) {
				throw new RuntimeException("zipファイルがドロップされていません");
			} else if (csvFile == null) {
				throw new RuntimeException("csvファイルがドロップされていません");
			}

		} else {
			throw new RuntimeException("ドロップできるファイルは2つです");
		}

		// InputDataFrame frame = new InputDataFrame();
		// frame.open();
		// lectureNumber = frame.getLectureNumber();

		transFileName();
	}

	public void transFileName() throws Exception {
		NameList nameList = new NameList(csvFile);
		SourceFile sourceFile = new SourceFile(zipFile);
		
		// zipファイルと同じフォルダにリネームされたzipファイルを収めたフォルダを作る
		if(zipFile.getParent() != null) {
			path = zipFile.getParent() + File.separator + "lecture" + File.separator;
		} else {
			path = "./lecture/";
		}

		System.out.println(path);
		// dir作成
		File dir = new File(path);
		dir.mkdir();

		for (@SuppressWarnings("unchecked")
		Iterator<File> it = sourceFile.iterator(); it.hasNext();) {
			File file = it.next();
			// 氏名取得
			String name = file.getName().substring(0,
					file.getName().indexOf('_'));
			// 学籍番号取得
			String studentId = nameList.getStudentId(name);
			if (studentId == null) {
				JOptionPane.showMessageDialog(null, "名簿に名前がありません: " + name);
				break;
			}
			// 課題番号取得
			// ファイル名変換
			File after = new File(path + File.separator + studentId + ".zip");
			// 既に存在している場合は削除して置き換え
			if (after.exists()) {
				after.delete();
			}
			if (!file.renameTo(after)) {
				JOptionPane.showMessageDialog(null, "変換失敗: " + file.getName());
			}
		}
	}
}
