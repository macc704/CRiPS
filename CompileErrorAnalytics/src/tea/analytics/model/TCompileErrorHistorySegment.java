package tea.analytics.model;

import pres.loader.model.IPLUnit;
import pres.loader.utils.PLWorkingTime;
import pres.loader.utils.PLWorkingTimeCalculator;
import clib.common.compiler.CDiagnostic;
import clib.common.time.CTime;
import clib.common.time.CTimeInterval;
import clib.common.time.CTimeRange;
import clib.common.time.ICTimeOrderable;

public class TCompileErrorHistorySegment implements ICTimeOrderable {
	private CDiagnostic compileError;

	private TCompileErrorHistory history;

	private TCompilePoint start;
	private TCompilePoint end; // エラーが修正されていなかったらnull

	private boolean fixed = false;
	private boolean working = false;

	private double a = 0d; // 係数

	private long startGeneRefTime = 0; // open dialog
	private long endGeneRefTime = 0; // close dialog

	public TCompileErrorHistorySegment(CDiagnostic compileError,
			TCompilePoint start, TCompilePoint end) {
		this.compileError = compileError;
		this.start = start;
		this.end = end;
	}

	public CDiagnostic getCompileError() {
		return compileError;
	}

	public TCompileErrorHistory getHistory() {
		return history;
	}

	public void setHistory(TCompileErrorHistory history) {
		this.history = history;
	}

	public TCompilePoint getStart() {
		return start;
	}

	public TCompilePoint getEnd() {
		return end;
	}

	public CTimeInterval getCorrectionTime() {
		if (end == null) {
			return null;
		} else {
			if (containsGeneRefTime()) {
				return new CTimeRange(endGeneRefTime, end.getTime().getAsLong())
						.getLength().multiply(a);
			} else {
				return new CTimeRange(start.getTime(), end.getTime())
						.getLength().multiply(a);
			}
		}
	}

	public CTime getTime() {
		return getStart().getTime();
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

	public boolean isFixed() {
		return fixed;
	}

	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a;
	}

	public void setWorking(boolean working) {
		this.working = working;
	}

	public void setWorking(PLWorkingTime workingTime) {

		// コンパイルエラーの修正ができてない場合はfalse
		if (getEnd() == null || getStart() == null) {
			this.working = false;
			return;
		}

		// GeneRefの分析時はunit=nullになる
		if (workingTime == null) {
			this.working = true;
			return;
		}

		long startTime = getStart().getTime().getAsLong();
		long millis = (getEnd().getTime().getAsLong() - startTime) / 5;

		for (int i = 0; i < 5; i++) {
			CTime time = new CTime(startTime + millis * i);
			if (time.afterAndEqual(end.getTime())) {
				this.working = true;
				return;
			}
			if (!workingTime.isWorking(time)) {
				this.working = false;
				return;
			}
		}
		this.working = true;
	}

	public boolean isWorking() {
		return working;
	}

	public boolean isWorking(IPLUnit unit) {

		try {
			if (unit == null) {
				throw new IllegalArgumentException();
			}
			// コンパイルエラーの修正ができてない場合はfalse
			if (getEnd() == null || getStart() == null) {
				return false;
			}

			PLWorkingTime workingTime = new PLWorkingTimeCalculator()
					.calculate(unit);
			long millis = (getEnd().getTime().getAsLong() - getStart()
					.getTime().getAsLong()) / 5;

			for (int i = 0; i < 5; i++) {
				CTime time = new CTime(start.getTime().getAsLong() + millis * i);
				if (time.afterAndEqual(end.getTime())) {
					return true;
				}
				if (!workingTime.isWorking(time)) {
					return false;
				}
			}

			return true;
		} catch (Exception ex) {
			System.err.println("Error@TCompileErrorHistory unit:" + unit);
			ex.printStackTrace();
			// ex.getMessage();
			return false;
		}
	}

	public void setGeneRefTime(long startTime, long endTime) {
		this.startGeneRefTime = startTime;
		this.endGeneRefTime = endTime;
	}

	public long getGeneRefTime() {
		if (!containsGeneRefTime()) {
			return 0;
		} else {
			return endGeneRefTime - start.getTime().getAsLong();
		}
	}

	public boolean containsGeneRefTime() {
		return startGeneRefTime != 0 && endGeneRefTime != 0;
	}

}
