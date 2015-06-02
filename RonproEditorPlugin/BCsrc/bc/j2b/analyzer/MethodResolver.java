package bc.j2b.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

import bc.j2b.model.ExpressionModel;

/*
 *  戻り値の型を返す（現在，ASTには呼出し先情報がないため，以下に登録する仮の処理）
 */
public class MethodResolver {

	private static Map<String, String> methodToReturnType = new HashMap<String, String>();

	static {
		MethodsCollector collector = new MethodsCollector();
		collector.main();
		methodToReturnType = collector.getCalcReturnType();
	}

	public boolean isRegistered(MethodInvocation method) {
		return isRegisteredAsReserved(method)
				|| isRegisteredAsUserMethod(method);
	}

	public boolean isRegisteredAsReserved(MethodInvocation method) {
		return methodToReturnType.containsKey(toSignature(method));
	}

	public boolean isRegisteredAsReserved(SuperMethodInvocation method) {
		return methodToReturnType.containsKey(toSignature(method));
	}

	public String getReturnType(MethodInvocation method) {
		if (isRegisteredAsReserved(method)) {
			return getReservedReturnType(toSignature(method));
		} else if (isRegisteredAsUserMethod(method)) {
			return getUserMethodType(method);
		} else {
			// throw new RuntimeException();
			return null;
		}
	}

	public String getReturnType(SuperMethodInvocation method) {
		if (isRegisteredAsReserved(method)) {
			return getReservedReturnType(toSignature(method));
		} else if (isRegisteredAsUserMethod(method)) {
			return getUserMethodType(method);
		} else {
			// throw new RuntimeException();
			return null;
		}
	}

	private String getReservedReturnType(String signature) {
		if (!methodToReturnType.containsKey(signature)) {
			return ExpressionModel.VOID;
		}
		return methodToReturnType.get(signature);
	}

	private String toSignature(MethodInvocation method) {
		return toSignature(method.getName().toString(), method.arguments());
	}

	private String toSignature(SuperMethodInvocation method) {
		return toSignature(method.getName().toString(), method.arguments());
	}

	private String toSignature(String name, List<?> arguments) {
		StringBuffer buf = new StringBuffer();
		buf.append(name);
		buf.append("(");
		int len = arguments.size();// この仕様は仮
		if (len > 0) {
			buf.append(len);
		}
		buf.append(")");
		return buf.toString();
	}

	private Map<String, String> userMethods = new HashMap<String, String>();
	private Map<String, String> projectMethods = new HashMap<String, String>();
	private Map<String, String> projectMethodsJavaTypes = new HashMap<String, String>();// 返り値付きメソッドの本当の返り値をここに保存する
	private List<String> userConstructor = new ArrayList<String>();

	public boolean isRegisteredAsUserMethod(MethodInvocation method) {
		String signature = toSignature(method);
		return userMethods.containsKey(signature);
	}

	public boolean isRegisteredAsProjectMethod(MethodInvocation method) {
		String signature = toSignature(method);
		return projectMethods.containsKey(signature);
	}

	public boolean isRegisteredAsProjectMethod(SuperMethodInvocation method) {
		String signature = toSignature(method);
		return projectMethods.containsKey(signature);
	}

	public String getUserMethodType(MethodInvocation method) {
		if (isRegisteredAsUserMethod(method)) {
			String signature = toSignature(method);
			return userMethods.get(signature);
		}
		return null;
	}

	public boolean isRegisteredAsUserMethod(SuperMethodInvocation method) {
		String signature = toSignature(method);
		return userMethods.containsKey(signature);
	}

	public String getUserMethodType(SuperMethodInvocation method) {
		if (isRegisteredAsUserMethod(method)) {
			String signature = toSignature(method);
			return userMethods.get(signature);
		}
		return null;
	}

	public void putUserMethod(String name,
			List<SingleVariableDeclaration> arguments, String returnType) {
		String signature = toSignature(name, arguments);
		userMethods.put(signature, returnType);
		putArgumentLabels(signature, arguments);
	}

	public void putUserConstructor(String name,
			List<SingleVariableDeclaration> arguments) {
		String signature = toSignature(name, arguments);
		userConstructor.add(signature);
		putArgumentLabels(signature, arguments);
	}

	public boolean registeredAsUserConstructor(String name){
		if(userConstructor.contains(name)){
			return true;
		}
		return false;
	}

	public void reset() {
		userMethods.clear();
	}

	private Map<String, List<String>> argumentLabels = new HashMap<String, List<String>>();

	/**
	 */
	public List<String> getArgumentLabels(MethodInvocation node) {
		String signature = toSignature(node);
		return argumentLabels.get(signature);
	}

	public List<String> getArgumentLabels(SuperMethodInvocation node) {
		String signature = toSignature(node);
		return argumentLabels.get(signature);
	}

	/**
	 */
	private void putArgumentLabels(String signature,
			List<SingleVariableDeclaration> arguments) {
		List<String> labels = new ArrayList<String>();
		for (SingleVariableDeclaration argument : arguments) {
			labels.add(argument.getName().toString());
		}
		argumentLabels.put(signature, labels);
	}

	public void addMethodReturnType(String name, String returnType) {
		methodToReturnType.put(name, returnType);
		projectMethods.put(name, returnType);
	}

	public void addMethodJavaReturnType(String name, String returnType) {
		projectMethodsJavaTypes.put(name, returnType);
	}

	public String getMethodJavaReturnType(String name) {
		return projectMethodsJavaTypes.get(name);
	}

	public void addArgumentLabels(String signature, List<String> arguments) {
		argumentLabels.put(signature, arguments);
	}
}
