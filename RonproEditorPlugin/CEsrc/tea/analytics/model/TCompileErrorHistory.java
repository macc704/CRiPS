package tea.analytics.model;

import clib.common.compiler.CDiagnostic;
import clib.common.time.CTimeInterval;
import clib.common.time.CTimeOrderedList;

public class TCompileErrorHistory {

	private CTimeOrderedList<TCompileErrorHistorySegment> segments = new CTimeOrderedList<TCompileErrorHistorySegment>();

	public void addSegment(TCompileErrorHistorySegment segment) {
		segments.add(segment);
	}

	public CTimeOrderedList<TCompileErrorHistorySegment> getSegments() {
		return segments;
	}

	public TCompilePoint getStart() {
		if (segments.size() == 0) {
			return null;
		} else {
			return segments.get(0).getStart();
		}
	}

	public TCompilePoint getEnd() {
		if (segments.size() == 0) {
			return null;
		} else {
			return segments.get(segments.size() - 1).getEnd();
		}
	}

	public CTimeInterval getCorrectionTime() {
		CTimeInterval correctionTime = new CTimeInterval(0);
		for (TCompileErrorHistorySegment segment : segments) {
			CTimeInterval interval = segment.getCorrectionTime();
			if (interval == null) {
				return null;
			} else {
				correctionTime = correctionTime.add(interval);
			}
		}
		return correctionTime;
	}

	public boolean isFixed() {
		if (segments.getLast().isFixed() == true) {
			return true;
		}
		return false;
	}

	public boolean containsCompileError(CDiagnostic compileError) {
		for (TCompileErrorHistorySegment segment : this.segments) {
			if (compileError == segment.getCompileError()) {
				return true;
			}
		}
		return false;
	}

	public boolean containsGeneRefTime() {
		for (TCompileErrorHistorySegment segment : this.segments) {
			if (segment.containsGeneRefTime()) {
				return true;
			}
		}
		return false;
	}

	public CTimeInterval getGeneRefTime() {
		long time = 0;
		for (TCompileErrorHistorySegment segment : this.segments) {
			time += segment.getGeneRefTime();
		}
		return new CTimeInterval(time);
	}

}
