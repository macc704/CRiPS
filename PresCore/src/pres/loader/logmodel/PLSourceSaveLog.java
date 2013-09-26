package pres.loader.logmodel;

import java.io.File;

public class PLSourceSaveLog extends PLLog {

	private String sourceName;
	private File targetSource;

	public PLSourceSaveLog(long timestamp, String sourceName, File target) {
		super(timestamp, "SourceSave", "SourceSave");
		this.sourceName = sourceName;
		this.targetSource = target;
	}

	public File getTarget() {
		return this.targetSource;
	}

	public String getExplanation() {
		String explanation = this.getTypeExplanation();
		explanation += "保存ファイル：" + this.sourceName + "\n";
		explanation += "保存時間：" + this.getTimeStampString() + "\n";
		return explanation;
	}

	@Override
	public String getExplanationPhrase() {
		return "ソース(" + this.sourceName + ")のセーブ";
	}

}
