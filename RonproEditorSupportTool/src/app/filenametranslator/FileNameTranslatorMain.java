package app.filenametranslator;

import framework.DnDFramework;

public class FileNameTranslatorMain {

	public static void main(String[] args) {
		DnDFramework.open("FileNameTranslator", "zipファイルとcsvファイルをドロップしてください",
				new FileNameTranslatorStrategy());
	}

}
