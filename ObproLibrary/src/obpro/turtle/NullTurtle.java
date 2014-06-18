package obpro.turtle;
/*
 * プログラム名：
 * 作成者： 
 * バージョン： 1.0 (日付)
 */
public class NullTurtle extends CardTurtle {

	public static final NullTurtle NULL_TURTLE = new NullTurtle();

	private NullTurtle() {
		super("Null");
	}

	public void text(Object text) {
		// 何もしない
		System.err.println("NullTurtle#text() 不正な操作です．");
	}

	public void hide() {
		// 何もしない
		System.err.println("NullTurtle#hide() 不正な操作です．");
	}
}
