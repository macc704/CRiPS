package bc.classblockfilewriters;

import java.io.PrintStream;
import java.util.List;

public interface MethodInfo {
	public void setJavaType(String returnType);

	public String getJavaType();

	public void setName(String name);

	public void setModifier(String modifier);

	public void setFuLLName(String fullName);

	public void setParameters(List<String> parameters);

	public void setReturnType(String returnType);

	public List<String> getParameters();

	public String getReturnType();

	public String getFullName();

	public String getModifier();

	public String getName();

	public void print(PrintStream out, int lineNum);

	public void printMethods(PrintStream out, int lineNum);

}
