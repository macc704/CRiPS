package presplugin.adapter;

import org.eclipse.core.commands.operations.IUndoableOperation;

public class ParsedTypingHistory {

	// private static final String TAG = "Typing";//Pleiades使用時"入力"となってしまうので却下

	public static final boolean isTypingOperation(IUndoableOperation op) {
		// return op.toString().startsWith(TAG)){//Pleiades使用時"入力"となってしまうので却下
		return op.toString().indexOf("$UndoableTextChange") != -1;
	}

	private int start;
	private int end;
	private String text;
	private String preservedText;

	public ParsedTypingHistory(IUndoableOperation op) {
		this(op.toString());
	}

	public ParsedTypingHistory(String paramString) {
		analyze(paramString);
	}

	private void analyze(String text) {
		int index;
		index = text.indexOf("start: ");
		text = text.substring(index + 7);

		index = text.indexOf(", end: ");
		String sStart = text.substring(0, index);
		text = text.substring(index + 7);

		index = text.indexOf(", text: ");
		String sEnd = text.substring(0, index);
		text = text.substring(index + 8);

		index = text.indexOf(", preservedText: ");
		String sText = text.substring(0, index);
		text = text.substring(index + 17);

		String sPreservedText = text;

		// toInt and insert to variable
		this.start = toInt(sStart);
		this.end = toInt(sEnd);

		// chop '' and insert to variable
		this.text = toText(sText);
		this.preservedText = toText(sPreservedText);
	}

	private String toText(String text) {
		try {
			int len = text.length();
			return text.substring(1, len - 1);
		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}

	private int toInt(String text) {
		try {
			return Integer.parseInt(text);
		} catch (Exception ex) {
			ex.printStackTrace();
			return -1;
		}
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public String getText() {
		return text;
	}

	public String getPreservedText() {
		return preservedText;
	}

	public String toString() {
		return "start:" + this.start + ", end:" + this.end + ", text:"
				+ this.text + ", pText:" + this.preservedText;
	}

	// Test
	public static void main(String[] args) {
		ParsedTypingHistory history = new ParsedTypingHistory(
				"op:Typing(org.eclipse.core.internal.filebuffers.SynchronizableDocument@5a41ec)\r\n"
						+ "org.eclipse.text.undo.DocumentUndoManager$UndoableTextChange undo modification stamp: 192 redo modification stamp: 193 start: 25, end: 25, text: 'h,'oge', preservedText: ',\r\n''");
		System.out.println(history);
	}
}
