package ppv.view.parts;

import java.awt.Color;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pres.loader.model.IPLFileProvider;
import clib.common.model.ICModelChangeListener;
import clib.common.time.CTime;
import clib.view.timeline.model.CTimeModel;

public class PPBlockPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IPLFileProvider model;
	private CTimeModel timeModel;
	private CTime current;
	private File blockPrintDir;
	private JLabel imgLabel = new JLabel();
	private long[] blockImages;
	private long currentImgStamp = 0;

	boolean canUpDownf = true;

	private static String BLOCKPRINT_DIR = "BlockPrint";

	public PPBlockPane(IPLFileProvider model, CTimeModel timeModel) {
		this.model = model;
		this.timeModel = timeModel;
		initialize();
	}

	private void initialize() {
		timeModel.addModelListener(new ICModelChangeListener() {
			public void modelUpdated(Object... args) {
				refresh();
			}
		});
		setBackground(Color.BLACK);
		current = timeModel.getTime();
		blockPrintDir = new File(new File(model.getFile(current).getDir()
				.getAbsolutePath().toString()).getParentFile(), BLOCKPRINT_DIR);
		blockImages = new SearchBlockImage().searchBlockImage(blockPrintDir);

		imgLabel.setIcon(new ImageIcon(new File(blockPrintDir, current
				.getAsLong() + ".jpg").getAbsolutePath()));
		imgLabel.setVerticalAlignment(JLabel.TOP);
		imgLabel.setHorizontalAlignment(JLabel.LEFT);

		add(imgLabel);

	}

	public void refresh() {
		current = timeModel.getTime();

		int imgIndex = 0;
		for (int i = 0; i < blockImages.length; ++i) {
			if (blockImages[i] <= current.getAsLong()) {
				imgIndex++;
			}
		}

		if (imgIndex >= blockImages.length) {
			imgIndex = blockImages.length - 1;
		}
		if (blockImages[0] <= current.getAsLong()) {
			currentImgStamp = blockImages[imgIndex];
		} else {
			currentImgStamp = 0;
		}
		File path = new File(blockPrintDir, blockImages[imgIndex] + ".jpg");

		if (path.exists()) {// いる？
			imgLabel.setIcon(new ImageIcon(path.getAbsolutePath()));
		}

	}

	public long getCurrentImgStamp() {
		return currentImgStamp;
	}
}

class SearchBlockImage {

	public long[] searchBlockImage(File searchDir) {
		String[] files = searchDir.list(new MyFilter());// MyFilterでjpgとpng以外はじく
		Arrays.sort(files);// ソート
		long[] fileNames = new long[files.length];
		for (int i = 0; i < files.length; ++i) {
			int index = files[i].indexOf(".");
			fileNames[i] = Long.valueOf(files[i].substring(0, index))
					.longValue();
		}
		return fileNames;
	}
}

class MyFilter implements FilenameFilter {
	public boolean accept(File dir, String name) {
		int index = name.lastIndexOf(".");
		String ext = name.substring(index + 1).toLowerCase();
		return ext.equals("jpg") || ext.equals("png");
	}
}
