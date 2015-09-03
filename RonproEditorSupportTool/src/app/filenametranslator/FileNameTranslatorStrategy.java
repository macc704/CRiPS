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
			throw new RuntimeException("�h���b�v����t�@�C��������܂���");
		} else if (files.size() == 2) {
			for (File f : files) {
				if (f.getName().endsWith(".zip")) {
					zipFile = f;
				} else if (f.getName().endsWith(".csv")) {
					csvFile = f;
				}
			}

			if (zipFile == null) {
				throw new RuntimeException("zip�t�@�C�����h���b�v����Ă��܂���");
			} else if (csvFile == null) {
				throw new RuntimeException("csv�t�@�C�����h���b�v����Ă��܂���");
			}

		} else {
			throw new RuntimeException("�h���b�v�ł���t�@�C����2�ł�");
		}

		// InputDataFrame frame = new InputDataFrame();
		// frame.open();
		// lectureNumber = frame.getLectureNumber();

		transFileName();
	}

	public void transFileName() throws Exception {
		NameList nameList = new NameList(csvFile);
		SourceFile sourceFile = new SourceFile(zipFile);
		
		// zip�t�@�C���Ɠ����t�H���_�Ƀ��l�[�����ꂽzip�t�@�C�������߂��t�H���_�����
		if(zipFile.getParent() != null) {
			path = zipFile.getParent() + File.separator + "lecture" + File.separator;
		} else {
			path = "./lecture/";
		}

		System.out.println(path);
		// dir�쐬
		File dir = new File(path);
		dir.mkdir();

		for (@SuppressWarnings("unchecked")
		Iterator<File> it = sourceFile.iterator(); it.hasNext();) {
			File file = it.next();
			// �����擾
			String name = file.getName().substring(0,
					file.getName().indexOf('_'));
			// �w�Дԍ��擾
			String studentId = nameList.getStudentId(name);
			if (studentId == null) {
				JOptionPane.showMessageDialog(null, "����ɖ��O������܂���: " + name);
				break;
			}
			// �ۑ�ԍ��擾
			// �t�@�C�����ϊ�
			File after = new File(path + File.separator + studentId + ".zip");
			// ���ɑ��݂��Ă���ꍇ�͍폜���Ēu������
			if (after.exists()) {
				after.delete();
			}
			if (!file.renameTo(after)) {
				JOptionPane.showMessageDialog(null, "�ϊ����s: " + file.getName());
			}
		}
	}
}
