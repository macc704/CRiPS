package ronproeditor.ext;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import clib.common.compiler.CCompileResult;
import clib.common.compiler.CDiagnostic;
import clib.preference.model.CAbstractPreferenceCategory;
import generef.compileerror.RSCompileHistory;
import generef.compileerror.RSCompileHistoryList;
import generef.compileerror.RSErrorMessage;
import generef.knowledge.RSFKWritingPoint;
import generef.knowledge.RSFailureKnowledge;
import generef.knowledge.RSFailureKnowledgeRepository;
import generef.knowledge.RSFailureKnowledgeRepositoryDAO;
import ronproeditor.REApplication;
import ronproeditor.ext.rss.RSFailureKnowledgeHistoryBrowser;
import ronproeditor.ext.rss.RSReflectionDialog;
import tea.analytics.model.TCompileErrorHistory;

/*
 * GeneRef
 * 
 * 2012/09/28 version 1.0.0 リリース
 * 2012/09/28 version 1.0.1 エラー減少パターンの際に前回修正されたものも修正されたと判定されるバグを修正
 * 2012/09/29 version 1.0.2 複数ファイルのコンパイル時の動作に対応
 * 2012/10/01 version 1.0.3 Export時にdatファイルをzipに含めるよう変更
 * 2012/10/03 version 1.0.4 コンパイルしてない状態で履歴画面を開くとNullPointerExceptionが出る問題を修正
 * 2012/10/04 version 1.0.5 コンパイルエラーの修正時間に記述時間を含めないように変更
 * 2012/10/04 version 1.0.6 内省ダイアログ表示の閾値をシステム上で変更できるようにした
 * 2012/10/14 version 1.0.7 GeneRefのインタフェースを変更
 * 2012/10/14 version 1.0.8 絶対パスで保存していたものを相対パスに変更
 * 2012/10/15 version 1.0.9 CT値スレッショルドのデフォルトを0に変更
 * 							失敗知識選択にデフォルトで選択肢を追加
 * 2012/10/19 version 1.0.10 エラー選択パネルに「全て選択」ボタンを追加
 * 2012/10/24 version 1.1.0 エラー修正の検出におけるバグを修正
 * 2012/10/30 version 1.1.1 WorkingTimeでNullPointerExceptionが出る問題を修正
 * 2012/11/09 version 1.1.2 CT値スレッショルドの不具合を修正
 * 2012/11/21 version 1.1.3 HistoryBrowserで失敗知識を見た時にopenWindowとcloseWindowの時間が書き変わる問題を修正
 * 2012/11/26 version 1.1.4 スレッショルドをログで出力するように変更
 * 							内省ダイアログに修正時間を表示するように変更
 * 2012/12/05 version 1.1.5 モーダルダイアログになっていなかったのを修正
 * 
 */

public class REGeneRefManager {

	public static final String APP_NAME = "GeneRef -ジェネリフ-";
	public static final String VERSION = "1.1.5";

	private static final String FILE_NAME = "repository.dat";
	private static final String DIR_PATH = "MyProjects/.GeneRef/";
	private static final String FILE_PATH = DIR_PATH + FILE_NAME;

	private REApplication application;

	// Dialog size
	private int width = 1120;
	private int height = 720;

	// Model
	private RSCompileHistoryList historyList = new RSCompileHistoryList();
	private RSFailureKnowledgeRepository fkRepository;
	private RSFailureKnowledgeRepositoryDAO fkDAO = new RSFailureKnowledgeRepositoryDAO();

	// Compile data
	private CCompileResult result;
	private List<String> compileSourceNames;

	private long writingReflectionTime = 0;

	private long correctionTime = 0;

	public REGeneRefManager(REApplication application) {
		this.application = application;
		this.height = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height - 25;
		initialize();
	}

	private void initialize() {
		application.getPreferenceManager().putCategory(new GeneRefPreferenceCategory());

		// create directory
		File dir = new File(DIR_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		this.fkRepository = fkDAO.load(new File(FILE_PATH));
	}

	/***************************************************************************
	 * getters
	 **************************************************************************/

	public REApplication getApplication() {
		return application;
	}

	public RSFailureKnowledgeRepository getFKRepository() {
		return fkRepository;
	}

	/***************************************************************************
	 * 公開インターフェイス
	 **************************************************************************/

	public void openReflectionDialog(RSFKWritingPoint point) {
		new RSReflectionDialog(this, width, height).open(point.getKnowledgeList(), fkRepository);
	}

	public void openGeneRefBrowser() {
		RSFailureKnowledgeHistoryBrowser historyBrowser = new RSFailureKnowledgeHistoryBrowser(fkRepository, this);
		historyBrowser.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		historyBrowser.setBounds(100, 100, 200, 200);
		historyBrowser.setVisible(true);
	}

	public void handleCompileDone() {
		try {
			if (active) {
				doReflectionProcess();
			}
		} catch (Exception ex) {
			ex.printStackTrace(application.getFrame().getConsole().getErr());
		}
	}

	public void saveFailureKnowledge() {

		// create file
		File file = new File(FILE_PATH);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		fkDAO.save(fkRepository, file);
	}

	public void refreshCompileHistory() {
		// Historyの修正済みエラーリストをクリア
		historyList.clearCompileSuccessHistorys(result, compileSourceNames);
	}

	@SuppressWarnings("resource")
	public void copyDatFileToProject() {
		String targetFilePath = application.getSourceManager().getProjectDirectory().toString() + "/" + FILE_NAME;

		File sourceFile = new File(FILE_PATH);
		File targetFile = new File(targetFilePath);
		if (sourceFile.exists()) {
			try {
				if (!targetFile.exists()) {
					targetFile.createNewFile();
				}
				FileChannel sourceChannel = new FileInputStream(sourceFile).getChannel();
				FileChannel targetChannel = new FileOutputStream(targetFile).getChannel();
				sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
				sourceChannel.close();
				targetChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void deleteDatFileFromProject() {
		String targetFilePath = application.getSourceManager().getProjectDirectory().toString() + "/" + FILE_NAME;
		File file = new File(targetFilePath);
		if (file.exists()) {
			file.delete();
		}
	}

	public CCompileResult getCompileResult() {
		return result;
	}

	public void setWritingReflectionTime(long millis) {
		this.writingReflectionTime = millis;
	}

	public int getThreshold() {
		return threshold;
	}

	public long getCorrectionTime() {
		return correctionTime;
	}

	/***************************************************************************
	 * 非公開
	 **************************************************************************/

	private void doReflectionProcess() throws Exception {

		RSErrorMessage message = new RSErrorMessage(application.doCompileInternally(true));
		this.result = message.getCompileResult();
		this.compileSourceNames = message.getCompileFileNames();

		addNewCompileHistory(result);
		historyList.addCompileResult(message);

		refreshPrevCompileFile(compileSourceNames);

		if (historyList.isFixed()) {
			List<RSFailureKnowledge> failureKnowledges = getInputFailureKnowledges(getRecordCompileErrorHistorys());
			fkRepository.addAll(failureKnowledges);
			openReflectionDialog(new RSFKWritingPoint(failureKnowledges));
		}

	}

	private void addNewCompileHistory(CCompileResult result) {
		String projectName = application.getSourceManager().getCCurrentProject().toString();
		for (CDiagnostic error : result.getDiagnostics()) {
			if (!historyList.containsHistory(error.getNoPathSourceName())) {
				historyList.addHistory(new RSCompileHistory(projectName, error.getNoPathSourceName()));
			}
		}
	}

	private void refreshPrevCompileFile(List<String> sourceNames) {
		String projectPath = application.getSourceManager().getCCurrentProject().getAbsolutePath().toString();
		historyList.setCurrentCompileFiles(projectPath, sourceNames);
	}

	private List<TCompileErrorHistory> getRecordCompileErrorHistorys() {
		List<TCompileErrorHistory> historys = new ArrayList<TCompileErrorHistory>();
		int sec = (int) writingReflectionTime / 1000;
		int num = historyList.getFixedCompileErrorHistory().size();

		for (TCompileErrorHistory history : this.historyList.getFixedCompileErrorHistory()) {
			correctionTime = (long) ((double) (history.getCorrectionTime().getTime() / 1000) - (double) sec / num);
			if (correctionTime > threshold - 1) {
				historys.add(history);
			} else {
				this.writingReflectionTime = 0;
			}
		}
		return historys;
	}

	private List<RSFailureKnowledge> getInputFailureKnowledges(List<TCompileErrorHistory> historys) {
		List<RSFailureKnowledge> knowledges = new ArrayList<RSFailureKnowledge>();

		for (TCompileErrorHistory history : historys) {
			CDiagnostic error = history.getSegments().getLast().getCompileError();
			RSCompileHistory rsHistory = historyList.getRSHistory(history);
			File unFixedFile = rsHistory.getPrevCompileFile();
			File fixedFile = rsHistory.getCurrentCompileFile();

			String currentProject = application.getSourceManager().getCCurrentProject().toString();
			String unFixedFilePath = unFixedFile.getAbsolutePath()
					.substring(unFixedFile.getAbsolutePath().indexOf(currentProject));
			String fixedFilePath = fixedFile.getAbsolutePath()
					.substring(fixedFile.getAbsolutePath().indexOf(currentProject));

			knowledges.add(new RSFailureKnowledge(error, "/" + unFixedFilePath, "/" + fixedFilePath, threshold));
		}
		return knowledges;
	}

	/***************************************************************************
	 * Preference
	 **************************************************************************/

	private boolean active = false;
	private static final String ACTIVE_LABEL = "GeneRef.active";

	private int threshold = 0;
	private static final String THRESHOLD_LABEL = "GeneRef.sleshold";

	class GeneRefPreferenceCategory extends CAbstractPreferenceCategory {

		private static final long serialVersionUID = 1L;

		private JCheckBox checkbox = new JCheckBox("GeneRef有効");
		private JSlider slider = new JSlider(0, 60);

		private JPanel panel = new GeneRefPreferencePanel();

		public String getName() {
			return "GeneRef";
		}

		public JPanel getPage() {
			return panel;
		}

		public void load() {
			if (getRepository().exists(ACTIVE_LABEL)) {
				active = Boolean.parseBoolean(getRepository().get(ACTIVE_LABEL));
				checkbox.setSelected(active);
			}
			if (getRepository().exists(THRESHOLD_LABEL)) {
				threshold = Integer.parseInt(getRepository().get(THRESHOLD_LABEL));
			}
			slider.setValue(threshold);
		}

		public void save() {
			active = checkbox.isSelected();
			getRepository().put(ACTIVE_LABEL, Boolean.toString(active));

			threshold = slider.getValue();
			getRepository().put(THRESHOLD_LABEL, Integer.toString(threshold));
		}

		class GeneRefPreferencePanel extends JPanel {
			private static final long serialVersionUID = 1L;

			JLabel label = new JLabel();

			GeneRefPreferencePanel() {
				setLayout(new FlowLayout());
				add(checkbox);
				add(getSliderPanel());
			}

			private JPanel getSliderPanel() {

				// set slider
				slider.setLabelTable(slider.createStandardLabels(10));
				slider.setPaintLabels(true);
				slider.setMajorTickSpacing(5);
				slider.setPaintTicks(true);
				slider.setSnapToTicks(true);
				slider.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						if (!slider.getValueIsAdjusting()) {
							label.setText("CT値 ：" + slider.getValue());
						}
					}
				});

				// set panel
				JPanel sliderPanel = new JPanel();
				sliderPanel.setLayout(new BorderLayout());
				sliderPanel.add(slider, BorderLayout.CENTER);
				label.setText("CT値 ：" + slider.getValue());
				label.setHorizontalAlignment(JLabel.CENTER);
				sliderPanel.add(label, BorderLayout.NORTH);

				return sliderPanel;
			}

		}

	}
}
