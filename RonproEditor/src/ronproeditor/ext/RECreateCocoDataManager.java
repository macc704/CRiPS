package ronproeditor.ext;

import javax.swing.JOptionPane;

import coco.controller.CCCompileErrorConverter;
import coco.controller.CCCompileErrorKindLoader;
import coco.model.CCCompileErrorManager;
import ppv.app.datamanager.PPDataManager;
import ppv.app.datamanager.PPProjectSet;
import ronproeditor.REApplication;

public class RECreateCocoDataManager {
	private REApplication application;

	private static String PPV_ROOT_DIR = ".ppv";// MyProjects/.ppv�t�H���_�ɓW�J����
	private static String KINDS_FILE = "ext/cocoviewer/ErrorKinds.csv"; // ext����ErrorKinds
	private static String ORIGINAL_DATA_FILE = "CompileError.csv"; // ppv����o�͂����csv�t�@�C��
	private static String DATA_FILE = "CompileErrorLog.csv"; // Coco�p�̃R���p�C���G���[�f�[�^

	private PPProjectSet ppProjectSet;

	public RECreateCocoDataManager(REApplication application) {
		this.application = application;
	}

	public void createCocoData() {
		int res = JOptionPane.showConfirmDialog(null,
				"�f�[�^�̍쐬�ɂ͎��Ԃ�������܂����C��낵���ł����H", "�f�[�^�̍쐬",
				JOptionPane.OK_CANCEL_OPTION);
		if (res != JOptionPane.OK_OPTION) {
			return;
		}

		// CompileError.csv�������I�ɃG�N�X�|�[�g����
		autoExportCompileErrorCSV();

		// �����I�ɃG�N�X�|�[�g�����t�@�C����Coco�p�f�[�^�ɕϊ�����
		convertCompileErrorData();
	}

	private void autoExportCompileErrorCSV() {
		REPresVisualizerManager ppvManager = new REPresVisualizerManager(
				application);
		ppvManager.exportAndImportAll();
		PPDataManager ppDataManager = ppvManager.getPPDataManager();
		ppDataManager.setLibDir(application.getLibraryManager().getDir());
		// TODO Hardcoding
		ppProjectSet = ppDataManager.openProjectSet("hoge", true, true, true);
	}

	private void convertCompileErrorData() {
		CCCompileErrorManager manager = new CCCompileErrorManager();
		String ppvRootPath = application.getSourceManager().getCRootDirectory()
				.findOrCreateDirectory(PPV_ROOT_DIR).getAbsolutePath()
				.toString()
				+ "/";

		checkAllFileExist();

		// �G���[�̎�ރf�[�^�����[�h
		CCCompileErrorKindLoader kindloader = new CCCompileErrorKindLoader(
				manager);
		kindloader.load(KINDS_FILE);

		// CompileError�f�[�^��Coco�p�ɃR���o�[�g
		try {
			CCCompileErrorConverter errorConverter = new CCCompileErrorConverter(
					manager);
			errorConverter.convertData(ppvRootPath + ORIGINAL_DATA_FILE,
					ppvRootPath + DATA_FILE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkAllFileExist() {
		checkOneFileExist(DATA_FILE);
		checkOneFileExist(ORIGINAL_DATA_FILE);
	}

	private void checkOneFileExist(String filename) {
		application.getSourceManager().getCRootDirectory()
				.findOrCreateDirectory(PPV_ROOT_DIR).findOrCreateFile(filename);
	}

	public PPProjectSet getPPProjectSet() {
		return ppProjectSet;
	}
}
