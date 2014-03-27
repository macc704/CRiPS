package bc.b2j.model;

import java.io.PrintStream;

public class ProcedureParamBlockModel extends DataBlockModel {

	@Override
	public void print(PrintStream out, int indent) {
		
		if (getGenusName().equals("proc-param-number")) {
			out.print("int");
		} else if (getGenusName().equals("proc-param-double-number")) {
			out.print("double");
		} else if (getGenusName().equals("proc-param-string")) {
			out.print("String");
		} else if (getGenusName().equals("proc-param-boolean")) {
			out.print("boolean");
		} else {
			out.print("Object");
		}

		if (getGenusName().indexOf("list") != -1) {
			out.print("[]");
		}

		out.print(" " + getLabel());
	}

}
