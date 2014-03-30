package bc.b2j.model;


public class ResolveSyntaxError {

	public ResolveSyntaxError() {
	}

	public void resolveError(ProgramModel root) {
		
		for (PageModel page : root.getPages()) {
			page.checkError();

		}

	}
}
