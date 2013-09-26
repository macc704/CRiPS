/*
 * PPSourceCountView.java
 * Created on 2011/06/07
 * Copyright(c) 2011 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */
package ppv.view.parts.timelineview;

import java.awt.Color;
import java.util.List;

import pres.core.model.PRCommandLog;
import pres.loader.logmodel.PLLog;
import pres.loader.model.IPLUnit;
import clib.common.compiler.CCompileResult;
import clib.common.compiler.CDiagnostic;
import clib.common.utils.ICChecker;
import clib.view.timeline.model.CTimeTransformationModel;

/**
 * @author macchan
 */
public class PPCompileErrorStateLineView extends PPAbstractStateLineView {

	private static final long serialVersionUID = 1L;

	public PPCompileErrorStateLineView(CTimeTransformationModel timeModel,
			IPLUnit unit, Color color) {
		super(timeModel, unit, color);
		parseLogs();
	}

	protected List<PLLog> getLogsForParse() {
		// PLUnit targetUnit =
		// getUnit();//ソースファイル全てを対象とし，本ファイルのコンパイルエラー状況を表示する必要がある．
		// 他のファイルのコンパイルでも本ファイルのコンパイルエラー状況が改善する場合がある．
		IPLUnit targetUnit = getUnit().getProject().getRootPackage();
		return targetUnit.getOrderedLogs().select(new ICChecker<PLLog>() {
			public boolean check(PLLog t) {
				return PRCommandLog.SubType.COMPILE.toString().equals(
						t.getSubType());
			}
		}).getElements();
	}

	protected boolean isEndTag(PLLog current, PLLog next) {
		// return getUnit().getProject().getCompileResult(current.getTime())
		// .isSuccess();
		return !hasCompileError(current);
	}

	protected boolean isStartTag(PLLog current, PLLog next) {
		return hasCompileError(current);
	}

	private boolean hasCompileError(PLLog current) {
		if (!getUnit().getProject().isReady(true)) {
			return false;
		}

		CCompileResult result = getUnit().getProject().getCompileResult(
				current.getTime());
		if (result.isSuccess()) {
			return false;
		} else {
			for (CDiagnostic diag : result.getDiagnostics()) {
				if (getUnit().hasSource(diag.getSourceName())) {
					return true;
				}
			}
		}
		return false;
	}
}
