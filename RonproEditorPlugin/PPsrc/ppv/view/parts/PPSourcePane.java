/*
 * PPSourcePane.java
 * Created on 2011/05/29
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.view.parts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import pres.loader.logmodel.PLLog;
import pres.loader.logmodel.PLTextEditLog;
import pres.loader.model.IPLFileProvider;
import pres.loader.model.PLFile;
import clib.common.model.ICModelChangeListener;
import clib.common.time.CTime;
import clib.common.time.CTimeOrderedList;
import clib.view.scrollpane.CLineNumberView;
import clib.view.textpane.CJavaCodeKit;
import clib.view.textpane.CNonWrappingTextPane;
import clib.view.textpane.CTextPaneUtils;
import clib.view.timeline.model.CTimeModel;

/**
 * @author macchan
 * 
 */
public class PPSourcePane extends JPanel {

	private static final long serialVersionUID = 1L;

	private IPLFileProvider model;
	private CTimeModel timeModel;

	private JScrollPane textScroll = new JScrollPane();
	private CNonWrappingTextPane textPane = new CNonWrappingTextPane();

	/**
	 * @param timeModel
	 */
	public PPSourcePane(IPLFileProvider model, CTimeModel timeModel) {
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

		setLayout(new BorderLayout());

		textPane.setEditorKit(new CJavaCodeKit());

		textScroll.setViewportView(textPane);
		textScroll.setRowHeaderView(new CLineNumberView(textPane));
		add(textScroll);

		// JTextPane全体に行間を設定 ↓×
		// SimpleAttributeSet attr = new SimpleAttributeSet();
		// StyleConstants.setLineSpacing(attr, 0.1f);
		// textPane.setParagraphAttributes(attr, true);
	}

	private static MutableAttributeSet INS = new SimpleAttributeSet();
	private static MutableAttributeSet DEL = new SimpleAttributeSet();

	static {
		// StyleConstants.setFontFamily(attr, "Monaco");
		// StyleConstants.setFontSize(attr,12);
		StyleConstants.setBackground(INS, Color.YELLOW);
		StyleConstants.setBackground(DEL, Color.RED);
		StyleConstants.setBold(INS, true);
		StyleConstants.setBold(DEL, true);
	}

	public void refresh() {
		CTime current = timeModel.getTime();

		PLFile target = model.getFile(current);
		if (target == null) {
			return;
		}

		String sourceText = target.getSource(current);
		CTimeOrderedList<PLLog> logs = target.getOrderedLogs().select(
				PLFile.LOG_TEXTEDIT_CHECKER);

		List<PLTextEditLog> editLogs = searchLogs(logs);
		if (editLogs.isEmpty()) {
			CTextPaneUtils.setTextWithoutScrolling(sourceText, textPane);
		} else {
			try {
				// CJavaCodeDocument doc = new CJavaCodeDocument();
				DefaultStyledDocument doc = new DefaultStyledDocument();
				doc.insertString(0, sourceText, null);
				textPane.setDocument(doc);

				for (PLTextEditLog editlog : editLogs) {
					try {
						doHighlight(editlog, doc);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				CTextPaneUtils.setTextWithoutScrolling(sourceText, textPane);
			}
		}
	}

	private List<PLTextEditLog> searchLogs(CTimeOrderedList<PLLog> logs) {
		List<PLTextEditLog> editLogs = new ArrayList<PLTextEditLog>();
		PLLog before = null;
		while (true) {
			PLLog log = logs.searchElementBefore(timeModel.getTime());
			if (log == null) {
				break;
			}
			if (before != null && !log.getTime().equals(before.getTime())) {
				break;
			}
			editLogs.add((PLTextEditLog) log);
			before = log;
			logs.remove(log);
		}
		return editLogs;
	}

	private void doHighlight(PLTextEditLog editLog, DefaultStyledDocument doc)
			throws Exception {
		// System.out.println(editLog.getExplanation());

		int offset = editLog.getOffset();
		if (!(offset < doc.getLength())) {
			return;
		}

		int addLen = editLog.getText().length();
		if (addLen > 0) {
			doHighlightIns(editLog, doc);
		}

		int delLen = editLog.getLength();
		if (delLen > 0) {
			doHighlightDel(editLog, doc);
		}

		// if (offset < doc.getLength()) {
		textPane.setCaretPosition(offset);
	}

	private void doHighlightIns(PLTextEditLog editLog, DefaultStyledDocument doc)
			throws BadLocationException {
		// TODO 多数行追加に対応していない．
		int offset = editLog.getOffset();
		int addLen = editLog.getText().length();
		if (editLog.getText().equals("\n")) {
			doc.insertString(offset, "\\n", INS);
		}
		if (editLog.getText().equals("\r\n")) {
			doc.insertString(offset, "\\r\\n", INS);
		}
		doc.setCharacterAttributes(offset, addLen, INS, true);
	}

	private void doHighlightDel(PLTextEditLog editLog, DefaultStyledDocument doc)
			throws BadLocationException {
		int offset = editLog.getOffset();
		int delLen = editLog.getLength();
		String dummy = "";
		for (int i = 0; i < delLen; i++) {
			dummy += " ";
		}
		doc.insertString(offset, dummy, DEL);
	}

	// public void setSourceText(String sourceText) {
	// CTextPaneUtils.setTextWithoutScrolling(sourceText, textPane);
	// }

}
