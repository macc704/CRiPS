package tea.analytics.model;

import java.util.ArrayList;
import java.util.List;

import clib.common.compiler.CCompileResult;
import clib.common.time.CTime;

public class TCompilePoint {
	private CTime time;
	private CCompileResult result;

	private List<TCompileErrorHistorySegment> beginningSegments = new ArrayList<TCompileErrorHistorySegment>();
	private List<TCompileErrorHistorySegment> finishedSegments = new ArrayList<TCompileErrorHistorySegment>();

	private TCompilePoint previous;

	private List<Integer> pattern = new ArrayList<Integer>();

	private boolean check = false;

	public TCompilePoint(CTime time, CCompileResult result) {
		this.time = time;
		this.result = result;
	}

	public CTime getTime() {
		return time;
	}

	public CCompileResult getCompileResult() {
		return result;
	}

	public void setPrevious(TCompilePoint previous) {
		this.previous = previous;
	}

	public TCompilePoint getPrevious() {
		return previous;
	}

	public void addPattern(int pattern) {
		this.pattern.add(pattern);
	}

	public List<Integer> getPattern() {
		return pattern;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public boolean isCheck() {
		return check;
	}

	public void addBeginningSegment(TCompileErrorHistorySegment segment) {
		beginningSegments.add(segment);

	}

	public void addFinishedSegment(TCompileErrorHistorySegment segment) {
		finishedSegments.add(segment);
	}

	public List<TCompileErrorHistorySegment> getBeginningSegments() {
		return beginningSegments;
	}

	public List<TCompileErrorHistorySegment> getFinishedSegments() {
		return finishedSegments;
	}

	public boolean hasFixedSegments() {
		return getFixedSegments().size() > 0;
	}

	public List<TCompileErrorHistorySegment> getFixedSegments() {
		List<TCompileErrorHistorySegment> segments = new ArrayList<TCompileErrorHistorySegment>();

		for (TCompileErrorHistorySegment segment : getFinishedSegments()) {
			if (segment != null && segment.isFixed()) {
				segments.add(segment);
			}
		}

		return segments;
	}

	public List<TCompileErrorHistorySegment> getNonFixedSegments() {
		List<TCompileErrorHistorySegment> segments = new ArrayList<TCompileErrorHistorySegment>();

		for (TCompileErrorHistorySegment segment : getFinishedSegments()) {
			if (segment != null && !segment.isFixed()) {
				segments.add(segment);
			}
		}

		return segments;
	}

}
