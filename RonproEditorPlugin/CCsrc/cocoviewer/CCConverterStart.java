package cocoviewer;

import java.io.IOException;

import coco.controller.CCAddCompileErrorKinds;
import coco.controller.CCCompileErrorConverter;
import coco.controller.CCCompileErrorKindLoader;
import coco.model.CCCompileErrorManager;

public class CCConverterStart {

	/**
	 * Converter Start
	 * 
	 * @throws IOException
	 */

	public static void main(String[] args) throws IOException {
		new CCConverterStart().run();
	}

	public void run() {
		CCCompileErrorManager manager = new CCCompileErrorManager();
		CCCompileErrorKindLoader kindloader = new CCCompileErrorKindLoader(
				manager);
		kindloader.load("ErrorKinds.csv");

		try {
			CCCompileErrorConverter errorconverter = new CCCompileErrorConverter(
					manager);
			errorconverter.convertData("CCCompileError.csv", "CCCompileErrorLog.csv");
		} catch(Exception e) {
			System.err.println("Convert log failed...");
			e.printStackTrace();
		}
		
		// addKinds(manager, kindloader);
		System.out.println("Convert Success!");
	}

	// 文字化けのバグ．エンコーディングの問題．
	private void addKinds(CCCompileErrorManager manager, CCCompileErrorKindLoader kindloader) {
		CCAddCompileErrorKinds addcompileerrorkinds = new
				CCAddCompileErrorKinds(
						manager, kindloader.getLines());
		try {
			addcompileerrorkinds.addKinds("ErrorKinds.csv", "MyErrorKinds.csv");
		} catch(Exception e) {
			System.err.println("Add Compile errors failed...");
			e.printStackTrace();
		}
	}
}