package app.jws;

import framework.DnDFramework;

public class JWSCreaterMain {

	public static void main(String[] args) {
		DnDFramework.open("JWSCreater", "番号化されたフォルダリストをドロップしてください",
				new JWSCreaterStrategy());
	}
}
