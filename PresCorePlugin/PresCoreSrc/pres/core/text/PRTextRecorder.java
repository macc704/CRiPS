/*
 * PRTextRecorder.java
 * Created on Apr 7, 2010 by macchan
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University
 */
package pres.core.text;

import java.util.Date;
import java.util.List;

import pres.core.model.PRLog;
import clib.common.filesystem.CFile;

/**
 * PRTextRecorder
 */
public class PRTextRecorder {

	public static final String DELIMITOR = "\t";

	private CFile file;

	/**
	 * Constructor 
	 */
	public PRTextRecorder(CFile file) {
		this.file = file;
	}

	/******************************
	 * 書込プログラム
	 ******************************/

	public void record(PRLog log) {
		if (file == null || file.deleted()) {/* is not writable */
			return;
		}

		String text = createText(log);

		file.appendText(text);
	}

	private String createText(PRLog log) {
		StringBuffer buf = new StringBuffer();

		//stamp time
		Date d = new Date(log.getTimestamp());
		buf.append(d.getTime());
		buf.append(DELIMITOR);
		buf.append(d.toString());

		//stamp type
		buf.append(DELIMITOR);
		buf.append(log.getType());

		//stamp subType
		buf.append(DELIMITOR);
		buf.append(log.getSubType());

		//stamp args
		List<Object> args = log.getArguments();
		for (Object arg : args) {
			buf.append(DELIMITOR);
			buf.append(PRTextEscaper.escape(arg));
		}

		return buf.toString();
	}

}
