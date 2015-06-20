package bc.classblockfilewriters;

import java.io.PrintStream;

public class PublicMethodCommandWriter extends BasicModel {

	private PublicMethodInfo method;

	public void setMethods(PublicMethodInfo method) {
		this.method = method;
	}

	public void printCommand(PrintStream out) {
		int lineNum = 0;
		makeIndent(out, lineNum);
		if (method.getName().startsWith("new-")) {
			printConstructor(lineNum, out);
		} else {
			printMethod(lineNum, out);
		}
	}

	private void printMethod(int lineNum, PrintStream out) {
		String kind = "command";
		if (method.getReturnType() != null && !method.getReturnType().equals("void")) {
			kind = "data";
		}
		// genusの出力
		out.println("<BlockGenus name=\"" + method.getFullName() + "\" kind=\"" + kind + "\" initlabel=\"" + method.getName() + "\" color=\"255 0 0\">");

		makeIndent(out, lineNum + 1);
		out.println("<JavaLabel>" + method.getName() + "</JavaLabel>");
		makeIndent(out, lineNum + 1);
		out.println("<JavaType>" + method.getJavaType() + "</JavaType>");

		makeIndent(out, ++lineNum);
		if (method.getParameters() != null) {
			out.println("<BlockConnectors>");

			++lineNum;

			if (method.getReturnType() != null && !method.getReturnType().equals("void")) {
				makeIndent(out, lineNum);
				out.println("<BlockConnector label=\"" + method.getReturnType() + "\" " + "connector-kind=\"plug\" connector-type=\"" + method.getReturnType() + "\" connector-javatype=\"" + method.getJavaType() + "\"></BlockConnector>");
			}

			for (int i = 0; i < method.getParameters().size(); i++) {
				String parameter = method.getParameters().get(i);
				String paramType = method.getParameterJavaTypes().get(i);
				// xxのような型＋変数名の形で保持されていることに注意
				String parameterType = convertParameterType(parameter.substring(0, parameter.indexOf(" ")));
				String parameterName = parameter.substring(parameter.substring(0, parameter.indexOf(" ")).length() + 1, parameter.length());
				makeIndent(out, lineNum);
				if("object".equals(parameterType)){
					out.println("<BlockConnector label=\"" + parameterName + "(" + paramType + "型)"+ "\" connector-kind=\"socket\" connector-type=\"" + parameterType + "\" connector-javatype=\"" + parameter.substring(0, parameter.indexOf(" ")) + "\">");
				}else{
					out.println("<BlockConnector label=\"" + parameterName +  "\" connector-kind=\"socket\" connector-type=\"" + parameterType + "\" connector-javatype=\"" + parameter.substring(0, parameter.indexOf(" ")) + "\">");
				}

				makeIndent(out, lineNum);
				out.println("</BlockConnector>");
				// 引数の設定
			}
			makeIndent(out, --lineNum);
			out.println("</BlockConnectors>");
		}

		makeIndent(out, lineNum);
		out.println("<LangSpecProperties>");
		makeIndent(out, ++lineNum);
		out.println("<LangSpecProperty key=\"vm-cmd-name\" value=\"" + method.getName() + "\"></LangSpecProperty>");
		makeIndent(out, lineNum);
		out.println("<LangSpecProperty key=\"stack-type\" value=\"breed\"></LangSpecProperty>");

		makeIndent(out, --lineNum);
		out.println("</LangSpecProperties>");

		makeIndent(out, --lineNum);
		out.println("</BlockGenus>");
		out.println();
	}

	private void printConstructor(int lineNum, PrintStream out) {
		// コンストラクターを出力する
		String kind = "function";
		out.println("<BlockGenus name=\"" + method.getFullName() + "\" editable-label=\"yes\"" + " kind=\"" + kind + "\" initlabel=\"" + method.getInitialLabel() + "\" header-label=\"新しく\"  footer-label=\"を作る\"" + " color=\"16 240 27\">");
		++lineNum;
		if (method.getParameters() != null) {
			makeIndent(out, lineNum++);
			out.println("<BlockConnectors>");
			makeIndent(out, lineNum);
			out.println("<BlockConnector connector-kind=\"plug\" connector-type=\"" + method.getReturnType() + "\"></BlockConnector>");

			for (String parameter : method.getParameters()) {
				// xxのような型＋変数名の形で保持されていることに注意されたし
				String parameterType = convertParameterType(parameter.substring(0, parameter.indexOf(" ")));
				String parameterName = parameter.substring(parameter.substring(0, parameter.indexOf(" ")).length() + 1, parameter.length());
				makeIndent(out, lineNum);

				out.println("<BlockConnector label=\"" + parameterName + "\" connector-kind=\"socket\" connector-type=\"" + parameterType + "\" connector-javatype=\"" + parameter.substring(0, parameter.indexOf(" ")) + "\">");

				makeIndent(out, lineNum);
				out.println("</BlockConnector>");
				// 引数の設定
			}
			makeIndent(out, --lineNum);
			out.println("</BlockConnectors>");
		}

		makeIndent(out, lineNum);
		out.println("<LangSpecProperties>");
		makeIndent(out, ++lineNum);
		out.println("<LangSpecProperty key=\"vm-cmd-name\" value=\"" + "new-object" + "\"></LangSpecProperty>");
		makeIndent(out, lineNum);
		out.println("<LangSpecProperty key=\"stack-type\" value=\"breed\"></LangSpecProperty>");

		makeIndent(out, --lineNum);
		out.println("</LangSpecProperties>");

		makeIndent(out, --lineNum);
		out.println("</BlockGenus>");
	}

	public void printMenuItem(PrintStream out, int lineNumber) {
		makeIndent(out, lineNumber);
		out.println("<BlockGenusMember>" + method.getFullName() + "</BlockGenusMember>");
	}

	private String convertParameterType(String parameterType) {
		String connectorType = "";
		if (parameterType.equals("int")) {
			connectorType = "number";
		} else if (parameterType.equals("String")) {
			connectorType = "string";
		} else if (parameterType.equals("boolean")) {
			connectorType = "boolean";
		} else if (parameterType.equals("double")) {
			return "double-number";
		} else {
			connectorType = "object";
		}
		return connectorType;
	}

}
