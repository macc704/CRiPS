/*
 * PPMetricsPane.java
 * Created on 2011/06/04
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.view.parts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import pres.loader.model.IPLUnit;
import clib.common.compiler.CCompileResult;
import clib.common.compiler.CDiagnostic;
import clib.common.model.ICModelChangeListener;
import clib.view.list.CListPanel;
import clib.view.timeline.model.CTimeModel;

/**
 * @author macchan
 */
public class PPCompileResultPane extends JPanel {

	private static final long serialVersionUID = 1L;

	private CTimeModel timeModel;
	private IPLUnit unit;

	private CListPanel<String> list = new CListPanel<String>();
	private JLabel label = new JLabel();

	/**
	 * @param timePane
	 * @param project
	 */
	public PPCompileResultPane(CTimeModel timeModel, IPLUnit unit) {
		this.timeModel = timeModel;
		this.unit = unit;
		initialize();
	}

	public void initialize() {
		timeModel.addModelListener(new ICModelChangeListener() {
			public void modelUpdated(Object... args) {
				refresh();
			}
		});
		setLayout(new BorderLayout());

		add(label, BorderLayout.NORTH);

		JScrollPane scroll = new JScrollPane(list);
		add(scroll, BorderLayout.CENTER);

	}

	private CCompileResult resultCash;

	private void refresh() {
		CCompileResult result = unit.getProject().getCompileResult(
				timeModel.getTime());

		if (resultCash == result) {
			return;
		}

		refresh(result);

		resultCash = result;
	}

	@SuppressWarnings("unchecked")
	private void refresh(CCompileResult result) {
		// result
		if (result == null) {
			label.setForeground(Color.BLACK);
			label.setText("");
		} else if (result.isSuccess()) {
			label.setForeground(Color.BLACK);
			label.setText("Success");
		} else {
			label.setForeground(Color.RED);
			label.setText("Failure " + result.getDiagnostics().size()
					+ "errors");
		}

		// error
		if (result == null) {
			list.setList(Collections.EMPTY_LIST);
		} else if (result.isSuccess()) {
			list.setList(Collections.EMPTY_LIST);
		} else {
			List<CDiagnostic> diags = result.getDiagnostics();
			List<String> results = new ArrayList<String>();
			for (CDiagnostic diag : diags) {
				String x = "";
				// sakakibara
				// x += diag.getNoPathSourceName();
				// x += "(Line:" + diag.getLineNumber() + ") ";
				// x += getMessage(diag);
				
				// matsuzawa
				x += getMessage(diag);
				x += " (Line:" + diag.getLineNumber() + ") ";
				results.add(x);
			}
			list.setList(results);
		}
	}

	private String getMessage(CDiagnostic diag) {
		// sakakibara
		return diag.getMessageParser().getErrorMessage();

		// matsuzawa
		// String original = diag.getErrorMessage();
		// String[] splitted = original.split(": ");
		// if (splitted.length > 1) {
		// return splitted[1];
		// } else {
		// return original;
		// }
	}

}
