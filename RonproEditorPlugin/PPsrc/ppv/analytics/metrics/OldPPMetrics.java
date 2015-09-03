package ppv.analytics.metrics;

import generef.analytics.FailureKnowledgeAnalyzer;
import generef.analytics.FailureKnowledgeRepositoryAnalyzer;
import pres.loader.model.IPLUnit;
import pres.loader.utils.PLMetricsCalculator;
import tea.analytics.CompileErrorAnalyzer;
import clib.common.time.CTimeInterval;

@SuppressWarnings("unused")
public class OldPPMetrics {

	private static final String CAMMA = ",";

	private boolean existCE = true;
	private boolean existFK = true;

	private String fileName;

	private long workingTime = 0;
	private long beWorkingTime = 0;
	private long lineCount = 0;
	private int compileCount = 0;
	private int runCount = 0;

	private int compileErrorCount = 0;
	private long correctionTime = 0;

	private int failureKnowledgeCount = 0;

	// private long failureKnowledgeTime = 0;

	public OldPPMetrics(IPLUnit unit/* , CompileErrorAnalyzerList ceAnalyzerList */) {
		this.fileName = unit.getName();
		PLMetricsCalculator metrics = new PLMetricsCalculator(unit);
		// CompileErrorAnalyzer ceAnalyzer = ceAnalyzerList.getChild(unit
		// .getName());
		// FailureKnowledgeAnalyzer fkAnalyzer =
		// loadFailureKnowledgeAnalyzer(unit);

		setMetricsElements(metrics);
		// if (ceAnalyzer != null) {
		// setCompileElements(ceAnalyzer);
		// } else {
		// existCE = false;
		// }
		//
		// if (fkAnalyzer != null) {
		// setFKElements(fkAnalyzer);
		// } else {
		// existFK = false;
		// }
	}

	private void setMetricsElements(PLMetricsCalculator metrics) {
		this.workingTime = metrics.getWorkingTime().getTime();
		this.beWorkingTime = metrics.getBEWorkingTime().getHour() * 60
				+ metrics.getBEWorkingTime().getMinute();
		this.lineCount = metrics.getLineCount();
		this.compileCount = metrics.getCompileCount();
		this.runCount = metrics.getRunCount();
	}

	private void setCompileElements(CompileErrorAnalyzer analyzer) {
		this.compileErrorCount = analyzer.getHistories().size();
		this.correctionTime = analyzer.getCorrectionTime().getTime();
	}

	private void setFKElements(FailureKnowledgeAnalyzer analyzer) {
		this.failureKnowledgeCount = analyzer.getKnowledges().size();
		// this.failureKnowledgeTime =
		// analyzer.getWrintingTimeWithoutIsWorking();
	}

	private FailureKnowledgeAnalyzer loadFailureKnowledgeAnalyzer(IPLUnit unit) {
		FailureKnowledgeRepositoryAnalyzer analyzer = new FailureKnowledgeRepositoryAnalyzer(
				unit.getProject());
		if (analyzer.existRepositoryFile()) {
			if (analyzer.getFailureKnowledgeAnalyzer(unit.getName()) != null) {
				return analyzer.getFailureKnowledgeAnalyzer(unit.getName());
			}
		}
		return null;
	}

	public void addMetrics(OldPPMetrics another) {
		setFileName(this.fileName + "," + another.getFileName());

		setWorkingTime(this.workingTime + another.getWorkingTime());
		setBeWorkingTime(this.beWorkingTime + another.getBEWorkingTime());
		setLineCount(another.getLineCount());
		setCompileCount(this.compileCount + another.getCompileCount());
		setRunCount(this.runCount + another.getRunCount());

		setCompileErrorCount(this.getCompileErrorCount()
				+ another.getCompileErrorCount());
		setCorrectionTime(this.correctionTime + another.getCorrectionTime());

		setFailureKnowledgeCount(this.failureKnowledgeCount
				+ another.getFailureKnowledgeCount());
		// setFailureKnowledgeTime(this.failureKnowledgeTime
		// + another.getFailureKnowledgeTime());
	}

	/********************************************************
	 * getter
	 ********************************************************/

	public String getFileName() {
		return fileName;
	}

	public long getWorkingTime() {
		return workingTime;
	}

	public long getBEWorkingTime() {
		return beWorkingTime;
	}

	public long getLineCount() {
		return lineCount;
	}

	public int getCompileCount() {
		return compileCount;
	}

	public int getRunCount() {
		return runCount;
	}

	public int getCompileErrorCount() {
		return compileErrorCount;
	}

	public long getCorrectionTime() {
		return correctionTime;
	}

	public int getFailureKnowledgeCount() {
		return failureKnowledgeCount;
	}

	// public long getFailureKnowledgeTime() {
	// return failureKnowledgeTime;
	// }

	/********************************************************
	 * setter
	 ********************************************************/

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setWorkingTime(long workingTime) {
		this.workingTime = workingTime;
	}

	public void setBeWorkingTime(long beWorkingTime) {
		this.beWorkingTime = beWorkingTime;
	}

	public void setLineCount(long lineCount) {
		this.lineCount = lineCount;
	}

	public void setCompileCount(int compileCount) {
		this.compileCount = compileCount;
	}

	public void setRunCount(int runCount) {
		this.runCount = runCount;
	}

	public void setCompileErrorCount(int compileErrorCount) {
		this.compileErrorCount = compileErrorCount;
	}

	public void setCorrectionTime(long correctionTime) {
		this.correctionTime = correctionTime;
	}

	public void setFailureKnowledgeCount(int failureKnowledgeCount) {
		this.failureKnowledgeCount = failureKnowledgeCount;
	}

	// public void setFailureKnowledgeTime(long failureKnowledgeTime) {
	// this.failureKnowledgeTime = failureKnowledgeTime;
	// }

	/********************************************************
	 * print
	 ********************************************************/

	public String getMetricsPrintString() {
		StringBuffer buf = new StringBuffer();

		buf.append("\"" + fileName + "\"");
		buf.append(CAMMA);

		// working time
		buf.append(new CTimeInterval(workingTime).getMajorString());
		buf.append(CAMMA);

		// working time(min)
		CTimeInterval interval = new CTimeInterval(workingTime);
		buf.append(interval.getDay() * 24 * 60 + interval.getHour() * 60
				+ interval.getMinute());
		buf.append(CAMMA);

		// Line Count
		buf.append(lineCount);
		buf.append(CAMMA);

		// Compile Count
		buf.append(compileCount);
		buf.append(CAMMA);

		// Run Count
		buf.append(runCount);
		buf.append(CAMMA);

		// Time/Compile
		if (compileCount > 0) {
			buf.append(new CTimeInterval(workingTime / compileCount)
					.getMajorString());
		} else {
			buf.append("");
		}
		buf.append(CAMMA);

		// Time/Run
		if (runCount > 0) {
			buf.append(new CTimeInterval(workingTime / runCount)
					.getMajorString());
		} else {
			buf.append("");
		}
		buf.append(CAMMA);

		if (beWorkingTime > 0) {
			buf.append(beWorkingTime);
		} else {
			buf.append("0");
		}
		buf.append(CAMMA);

		/*** CompileError ***/
		if (existCE) {
			// CompileErrorCount
			buf.append(compileErrorCount);
			buf.append(CAMMA);

			// Total Correction Time
			buf.append(new CTimeInterval(correctionTime));
			buf.append(CAMMA);

			// rate
			double rate = (double) correctionTime / workingTime * 100;
			buf.append(rate);
			buf.append(CAMMA);
		} else {
			for (int i = 0; i < 3; i++) {
				buf.append(CAMMA);
			}
		}

		// /*** Failure Knowledge ***/
		// if (existFK) {
		// // count
		// buf.append(failureKnowledgeCount);
		// buf.append(CAMMA);
		//
		// // time(sec)
		// double sec = (double) failureKnowledgeTime / 1000;
		// buf.append(sec);
		// buf.append(CAMMA);
		//
		// // overhead(%)
		// long time = workingTime - failureKnowledgeTime;
		// double overhead = (double) failureKnowledgeTime / time * 100;
		// buf.append(overhead);
		// } else {
		// for (int i = 0; i < 2; i++) {
		// buf.append(CAMMA);
		// }
		// }

		return buf.toString();
	}

	public String toString() {
		return fileName;
	}

}
