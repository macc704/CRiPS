package ppv.analytics.metrics;

import pres.loader.model.IPLUnit;
import pres.loader.utils.PLMetricsCalculator;
import clib.common.time.CTimeInterval;

public class PPMetrics {

	private static final String CAMMA = ",";

	private String fileName;

	private long workingTime = 0;
	private long beWorkingTime = 0;
	private long lineCount = 0;
	private int compileCount = 0;
	private int runCount = 0;
	// for debugger by hakamata
	private int debugCount = 0;
	private int stepCount = 0;
	private int playCount = 0;
	private int aveSpeed = 0;
	private long ndWorkingTime = 0;

	public PPMetrics(IPLUnit unit) {
		this.fileName = unit.getName();
		PLMetricsCalculator metrics = new PLMetricsCalculator(unit);
		setMetricsElements(metrics);
	}

	private void setMetricsElements(PLMetricsCalculator metrics) {
		this.workingTime = metrics.getWorkingTime().getTime();
		this.beWorkingTime = metrics.getBEWorkingTime().getTime();
		this.lineCount = metrics.getLineCount();
		this.compileCount = metrics.getCompileCount();
		this.runCount = metrics.getRunCount();
		
		// for debugger by hakamata
		this.debugCount = metrics.getDebugCount();
		this.stepCount = metrics.getStepCount();
		this.playCount = metrics.getPlayCount();
		this.aveSpeed = metrics.getAverageSpeed();
		this.ndWorkingTime = metrics.getNDWorkingTime().getTime();
	}

	public void addMetrics(PPMetrics another) {
		this.fileName = this.fileName + "," + another.fileName;
		this.workingTime += another.workingTime;
		this.beWorkingTime += another.beWorkingTime;
		this.lineCount += another.lineCount;
		this.compileCount += another.compileCount;
		this.runCount += another.runCount;
		
		// for debugger by hakamata
		this.debugCount += another.debugCount;
		this.stepCount += another.stepCount;
		this.playCount += another.playCount;
		this.aveSpeed += another.aveSpeed;
		this.ndWorkingTime += another.ndWorkingTime;
	}

	/********************************************************
	 * getter
	 ********************************************************/

	public String getFileName() {
		return fileName;
	}

	/********************************************************
	 * print
	 ********************************************************/

	public String getMetricsPrintString() {
		StringBuffer buf = new StringBuffer();
		long ndTime, wTime;

		buf.append("\"" + fileName + "\"");
		buf.append(CAMMA);

		// working time
		buf.append(new CTimeInterval(workingTime).getMajorString());
		buf.append(CAMMA);

		// working time(min)
		{
			CTimeInterval interval = new CTimeInterval(workingTime);
			wTime = interval.getDay() * 24 * 60 + interval.getHour() * 60 + interval.getMinute();
			buf.append(wTime);
			buf.append(CAMMA);
		}

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
			CTimeInterval interval = new CTimeInterval(beWorkingTime);
			buf.append(interval.getDay() * 24 * 60 + interval.getHour() * 60
					+ interval.getMinute());
		} else {
			buf.append("0");
		}
		buf.append(CAMMA);

		
		// for debugger by hakamata
		// debugCount
		buf.append(debugCount);
		buf.append(CAMMA);
		
		// stepCount
		buf.append(stepCount);
		buf.append(CAMMA);
		
		// playCount
		buf.append(playCount);
		buf.append(CAMMA);
		
		// average speed
		buf.append(aveSpeed);
		buf.append(CAMMA);
		
		// NDWorkingTime(min)
		if (ndWorkingTime > 0) {
			CTimeInterval interval = new CTimeInterval(ndWorkingTime);
			ndTime = interval.getDay() * 24 * 60 + interval.getHour() * 60 + interval.getMinute();
			
		} else {
			ndTime = 0;
		}
		buf.append(ndTime);
		buf.append(CAMMA);
		
		// NDWorkingTime / WorkingTime
		if (wTime > 0) {
			double percent = (int)((double)ndTime / wTime * 10000 + 0.5) / 100d;
			buf.append(percent + "%");
		} else {
			buf.append("");
		}
		buf.append(CAMMA);
		
		// NDWorkingTime / DebugCount
		if (debugCount > 0) {
			buf.append(new CTimeInterval(ndWorkingTime / debugCount)
					.getMajorString());
		} else {
			buf.append("");
		}
		buf.append(CAMMA);

		return buf.toString();
	}

	public static String createHeader() {
		StringBuffer buf = new StringBuffer();
		buf.append("ProjectName");
		buf.append(CAMMA);
		buf.append("FileName");
		buf.append(CAMMA);
		buf.append("WorkingTime");
		buf.append(CAMMA);
		buf.append("WorkingTime(min)");
		buf.append(CAMMA);
		buf.append("LineCount");
		buf.append(CAMMA);
		buf.append("CompileCount");
		buf.append(CAMMA);
		buf.append("RunCount");
		buf.append(CAMMA);
		buf.append("Time/Compile");
		buf.append(CAMMA);
		buf.append("Time/Run");
		buf.append(CAMMA);
		buf.append("BEWorkingTime(min)");
		buf.append(CAMMA);
		// for debugger by hakamata
		buf.append("DebugCount");
		buf.append(CAMMA);
		buf.append("StepCount");
		buf.append(CAMMA);
		buf.append("DebugPlayCount");
		buf.append(CAMMA);
		buf.append("DebugPlayAverageSpeed");
		buf.append(CAMMA);
		buf.append("NDWorkingTime(min)");
		buf.append(CAMMA);
		buf.append("NDWorkingTime/WorkingTime");
		buf.append(CAMMA);
		buf.append("NDWorkingTime/DebugCount");
		buf.append(CAMMA);
		return buf.toString();
	}

	public String toString() {
		return fileName;
	}

}
