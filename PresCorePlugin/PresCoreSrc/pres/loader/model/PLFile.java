/*
 * PresFile.java
 * Created on Jul 6, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package pres.loader.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.DefaultStyledDocument;

import pres.core.model.PRCommandLog;
import pres.core.model.PRTextEditLog;
import pres.loader.logmodel.PLLog;
import pres.loader.logmodel.PLTextEditLog;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFile;
import clib.common.filesystem.CFilename;
import clib.common.filesystem.CPath;
import clib.common.time.CTime;
import clib.common.time.CTimeOrderedList;
import clib.common.time.CTimeRange;
import clib.common.utils.ICChecker;

/**
 * @author macchan
 */
public class PLFile extends PLAbstractUnit {

	private CTimeOrderedList<IPLFileStamp> stamps = new CTimeOrderedList<IPLFileStamp>();

	private PLNullFileStamp NULLSTAMP;

	private int cashMaxLineCount = 0;

	public PLFile(PLProject project, CDirectory dir, CPath path) {
		super(project, dir, path);
		initialize();
	}

	private void initialize() {
		List<CFile> children = getDir().getFileChildren();
		for (CFile child : children) {
			try {
				PLFileStamp stamp = new PLFileStamp(child);
				cashMaxLineCount = Math.max(cashMaxLineCount,
						stamp.getLineCount());
				stamps.add(stamp);
			} catch (NumberFormatException ex) {
				//ex.printStackTrace();
				System.err.println("Stamp file format error: "
						+ ex.getMessage());
			}
		}
	}

	public CFilename getFileName() {
		return getPath().getName();
	}

	public CTimeOrderedList<IPLFileStamp> getStamps() {
		return stamps;
	}

	public boolean hasStamp(CTime time) {
		return stamps.hasElement(time);
	}

	public IPLFileStamp getStamp(CTime time) {
		IPLFileStamp stamp = stamps.searchElementBefore(time);
		if (stamp == null) {
			return getNULLSTAMP();
		}
		return stamp;
	}

	private PLNullFileStamp getNULLSTAMP() {
		if (this.NULLSTAMP == null) {
			if (hasRange()) {
				this.NULLSTAMP = new PLNullFileStamp(getStart());
			} else if (getProject().getRootPackage().hasRange()) {
				this.NULLSTAMP = new PLNullFileStamp(getProject()
						.getRootPackage().getStart());
			} else {
				throw new RuntimeException("cannot recover range problem");
			}
		}
		return this.NULLSTAMP;
	}

	/* (non-Javadoc)
	* @see pres.loader.model.IPLFileProvider#getFile(clib.common.time.CTime)
	*/
	public PLFile getFile(CTime time) {
		return this;
	}

	public String getSource(CTime time) {
		CTimeOrderedList<CTime> savepoints = new CTimeOrderedList<CTime>(
				getSavePoints());
		if (savepoints.size() <= 0) {
			//補完した場合は，savepoint0でもファイルがあることはあり得る．
			CTimeOrderedList<IPLFileStamp> stamps = getStamps();
			if (stamps.size() <= 0) {
				return "";
			}

			return stamps.searchElementBefore(time).getSource();
		}

		try {
			{//before->after
				CTime savePointBefore = savepoints.searchElementBefore(time);
				if (savePointBefore != null) {
					return createSourceByForwarding(savePointBefore, time);
				} else {
					CTime savePointAfter = savepoints.searchElementAfter(time);
					if (savePointAfter == null) {
						throw new RuntimeException();//ありえない
					}
					return createSourceByBackwarding(savePointAfter, time);
				}
			}
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			//ex.printStackTrace();
			return "";
		}
	}

	public static ICChecker<PLLog> LOG_TEXTEDIT_CHECKER = new ICChecker<PLLog>() {
		public boolean check(PLLog t) {
			//			if (t.getType().equals(
			//					PRTextEditLog.Type.TEXTEDIT_RECORD.toString())) {
			//				return true;
			//			}
			if (t.getSubType().equals(PRTextEditLog.SubType.ECLIPSE.toString())) {
				return true;
			}
			return false;
		}
	};

	/**
	 * @param savePointBefore
	 * @param time
	 * @return
	 */
	private String createSourceByForwarding(CTime savePoint, CTime time)
			throws Exception {
		assert savePoint != null;
		assert time != null;

		IPLFileStamp stamp = getStamp(savePoint);

		CTimeOrderedList<PLLog> logs = getOrderedLogs().searchElements(
				new CTimeRange(savePoint, time), LOG_TEXTEDIT_CHECKER);

		if (logs.size() <= 0) {
			return stamp.getSource();
		}

		return apply(stamp.getSource(), logs);
	}

	/**
	 * @param savePointAfter
	 * @param time
	 * @return
	 */
	private String createSourceByBackwarding(CTime savePoint, CTime time)
			throws Exception {
		assert savePoint != null;
		assert time != null;

		CTime baseTime = new CTime(0);

		IPLFileStamp stamp = getStamp(savePoint);

		CTimeOrderedList<PLLog> backlogs = getOrderedLogs().searchElements(
				new CTimeRange(baseTime, savePoint), LOG_TEXTEDIT_CHECKER);

		CTimeOrderedList<PLLog> logs = getOrderedLogs().searchElements(
				new CTimeRange(baseTime, time), LOG_TEXTEDIT_CHECKER);

		if (backlogs.size() <= 0) {
			return stamp.getSource();
		}

		String baseText = stamp.getSource();
		baseText = applyBackward(baseText, backlogs);
		return apply(baseText, logs);
	}

	private String apply(String source, CTimeOrderedList<PLLog> logs)
			throws Exception {
		DefaultStyledDocument doc = new DefaultStyledDocument();
		doc.insertString(0, source, null);
		int len = logs.size();
		for (int i = 0; i < len; i++) {
			PLTextEditLog edit = (PLTextEditLog) logs.get(i);
			String text = edit.getText();
			int offset = edit.getOffset();
			int delLen = edit.getLength();
			doc.replace(offset, delLen, text, null);
		}
		return doc.getText(0, doc.getLength());
	}

	private String applyBackward(String source, CTimeOrderedList<PLLog> logs)
			throws Exception {
		DefaultStyledDocument doc = new DefaultStyledDocument();
		doc.insertString(0, source, null);
		int len = logs.size();
		for (int i = len - 1; i >= 0; i--) {
			PLTextEditLog edit = (PLTextEditLog) logs.get(i);
			String text = edit.getText();
			int offset = edit.getOffset();
			int addLen = text.length();
			int delLen = edit.getLength();
			String dummy = createDummy(delLen);
			doc.replace(offset, addLen, dummy, null);
		}
		return doc.getText(0, doc.getLength());
	}

	private String createDummy(int n) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < n; i++) {
			buf.append('?');
		}
		return buf.toString();
	}

	public int getMaxLineCount() {
		return cashMaxLineCount;
	}

	public int getLineCount(CTime time) {
		return getStamp(time).getLineCount();
	}

	public List<CTime> getSavePoints() {
		//2012/07/07 以前のコード
		//		List<CTime> points = new ArrayList<CTime>();
		//		for (IPLFileStamp stamp : getStamps()) {
		//			points.add(stamp.getTime());
		//		}
		//		return points;
		CTimeOrderedList<PLLog> list = getOrderedLogs().select(
				new ICChecker<PLLog>() {
					public boolean check(PLLog t) {
						return t.getType().equals(
								PRCommandLog.Type.COMMAND_RECORD.toString())
								&& t.getSubType().equals(
										PRCommandLog.SubType.SAVE.toString());
					}
				});
		List<CTime> points = new ArrayList<CTime>();
		for (PLLog log : list) {
			points.add(log.getTime());
		}
		return points;
	}

//	/* (non-Javadoc)
//	 * @see pres.loader.model.IPLUnit#hasSource(java.lang.String)
//	 */
//	public boolean hasSource(String sourceName) {
//		// TODO 仮の実装 	getName()でパスがとれる
//		return getFileName().toString().equals(sourceName);
//	}
	
}
