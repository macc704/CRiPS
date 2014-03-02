package src.coco;

import java.io.IOException;

import src.coco.controller.CCCompileErrorConverter;
import src.coco.controller.CCCompileErrorKindLoader;
import src.coco.model.CCCompileErrorManager;

public class CCConverterStart {

	/**
	 * Converter Start
	 * 
	 * @throws IOException
	 */

	public static void main(String[] args) throws IOException {
		new CCConverterStart().run();
	}

	public void run() throws IOException {
		CCCompileErrorManager manager = new CCCompileErrorManager();
		CCCompileErrorKindLoader kindloader = new CCCompileErrorKindLoader(
				manager);
		kindloader.load("ErrorKinds.csv");

		CCCompileErrorConverter errorconverter = new CCCompileErrorConverter(
				manager);
		errorconverter.convertData("CCCompileError.csv", "CompileErrorLog.csv");

		System.out.println("Convert Success!");
		// CCAddCompileErrorKinds addcompileerrorkinds = new
		// CCAddCompileErrorKinds(
		// manager, kindloader.getLines());
		// addcompileerrorkinds.addKinds("ErrorKinds.csv", "MyErrorKinds.csv");
	}
}