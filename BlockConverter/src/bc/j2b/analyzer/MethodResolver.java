package bc.j2b.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import bc.j2b.model.ExpressionModel;

/*
 *  戻り値の型を返す（現在，ASTには呼出し先情報がないため，以下に登録する仮の処理）
 */
public class MethodResolver {

	private static Map<String, String> methodToReturnType = new HashMap<String, String>();

	static {
		methodToReturnType.put("input()", "int");
		methodToReturnType.put("random(1)", "int");
		methodToReturnType.put("getX()", "int");
		methodToReturnType.put("getY()", "int");
		methodToReturnType.put("getWidth()", "int");
		methodToReturnType.put("getHeight()", "int");
		methodToReturnType.put("x()", "double");
		methodToReturnType.put("y()", "double");
		methodToReturnType.put("width()", "double");
		methodToReturnType.put("height()", "double");
		methodToReturnType.put("isShow()", "boolean");
		methodToReturnType.put("key()", "int");
		methodToReturnType.put("keyDown(1)", "boolean");
		methodToReturnType.put("mouseX()", "int");
		methodToReturnType.put("mouseY()", "int");
		methodToReturnType.put("mouseClicked()", "boolean");
		methodToReturnType.put("leftMouseClicked()", "boolean");
		methodToReturnType.put("rightMouseClicked()", "boolean");
		methodToReturnType.put("doubleClick()", "boolean");
		methodToReturnType.put("mouseDown()", "boolean");
		methodToReturnType.put("leftMouseDown()", "boolean");
		methodToReturnType.put("rightMouseDown()", "boolean");
		methodToReturnType.put("intersects(1)", "boolean");
		methodToReturnType.put("contains(2)", "boolean");
		methodToReturnType.put("image()", "ImageTurtle");
		methodToReturnType.put("getText()", "String");
		methodToReturnType.put("isPlaying()", "boolean");
		methodToReturnType.put("getVolume()", "int");

		methodToReturnType.put("next()", "String");
		methodToReturnType.put("nextInt()", "int");
		methodToReturnType.put("nextDouble()", "double");

		methodToReturnType.put("fd(1)", "void");
		methodToReturnType.put("bk(1)", "void");
		methodToReturnType.put("rt(1)", "void");
		methodToReturnType.put("lt(1)", "void");
		methodToReturnType.put("up()", "void");
		methodToReturnType.put("down()", "void");
		methodToReturnType.put("color(1)", "void");
		methodToReturnType.put("warp(2)", "void");
		methodToReturnType.put("warpByTopLeft(2)", "void");
		methodToReturnType.put("scale(1)", "void");
		methodToReturnType.put("size(2)", "void");
		methodToReturnType.put("large(1)", "void");
		methodToReturnType.put("small(1)", "void");
		methodToReturnType.put("narrow(1)", "void");
		methodToReturnType.put("tall(1)", "void");
		methodToReturnType.put("little(1)", "void");

		methodToReturnType.put("show()", "void");
		methodToReturnType.put("hide()", "void");
		methodToReturnType.put("looks(1)", "void");

		methodToReturnType.put("update()", "void");
		methodToReturnType.put("sleep(1)", "void");
		methodToReturnType.put("print(1)", "void");
		methodToReturnType.put("text(1)", "void");
		methodToReturnType.put("image(1)", "void");

		methodToReturnType.put("file(1)", "void");
		methodToReturnType.put("play()", "void");
		methodToReturnType.put("loop()", "void");
		methodToReturnType.put("stop()", "void");
		methodToReturnType.put("setVolume(1)", "void");
		methodToReturnType.put("loadOnMemory()", "void");

		// list
		methodToReturnType.put("get(1)", "Object");
		methodToReturnType.put("getSize()", "int");
		methodToReturnType.put("add(1)", "void");
		methodToReturnType.put("addFirst(1)", "void");
		methodToReturnType.put("addLast(1)", "void");
		methodToReturnType.put("addAll(1)", "void");
		methodToReturnType.put("moveAllTo(1)", "void");
		methodToReturnType.put("removeFirst()", "void");
		methodToReturnType.put("removeLast()", "void");
		methodToReturnType.put("removeAll()", "void");
		methodToReturnType.put("getCursor()", "int");
		methodToReturnType.put("setCursor(1)", "void");
		methodToReturnType.put("moveCursorToNext()", "void");
		methodToReturnType.put("moveCursorToPrevious()", "void");
		methodToReturnType.put("getObjectAtCursor()", "Object");
		methodToReturnType.put("addToBeforeCursor(1)", "void");
		methodToReturnType.put("addToAfterCursor(1)", "void");
		methodToReturnType.put("removeAtCursor()", "void");
		methodToReturnType.put("shuffle()", "void");
		methodToReturnType.put("setBgColor(1)", "void");

		// card
		methodToReturnType.put("getNumber()", "int");

		// button
		methodToReturnType.put("isClicked()", "boolean");

		// input
		// methodToReturnType.put("getText()", "String");
		// methodToReturnType.put("text(1)", "void");
		methodToReturnType.put("setActive(1)", "void");
		methodToReturnType.put("isActive()", "boolean");
		methodToReturnType.put("clearText()", "void");
		methodToReturnType.put("toJapaneseMode()", "void");
		methodToReturnType.put("toEnglishMode()", "void");
		methodToReturnType.put("fontsize(1)", "void");

		// BCanvas
		methodToReturnType.put("drawArc(7)", "void");
		methodToReturnType.put("drawFillTriangle(7)", "void");
		methodToReturnType.put("drawText(5)", "void");
		methodToReturnType.put("isClick()", "boolean");
		methodToReturnType.put("isSingleClick()", "boolean");
		methodToReturnType.put("isDoubleClick()", "boolean");
		methodToReturnType.put("isDragging()", "boolean");
		methodToReturnType.put("isRightMouseDown()", "boolean");
		methodToReturnType.put("isLefttMouseDown()", "boolean");
		methodToReturnType.put("getMouseX()", "int");
		methodToReturnType.put("getMouseY()", "int");
		methodToReturnType.put("isKeyPressing(1)", "boolean");
		methodToReturnType.put("isKeyCode()", "boolean");
		methodToReturnType.put("isKeyDown()", "boolean");
		methodToReturnType.put("getKeyCode()", "int");
		methodToReturnType.put("clear()", "void");
		methodToReturnType.put("update()", "void");
		methodToReturnType.put("getImageWidth()", "int");
		methodToReturnType.put("getImageHeight()", "int");
		methodToReturnType.put("drawLine(5)", "void");
		methodToReturnType.put("getCanvasWidth()", "int");
		methodToReturnType.put("getCanvasHeight()", "int");
		methodToReturnType.put("drawImage(5)", "void");
		methodToReturnType.put("drawFillArc(7)", "void");
		methodToReturnType.put("setLocation(2)", "void");
		methodToReturnType.put("setSize(2)", "void");
		methodToReturnType.put("getCanvas()", "Object");
		methodToReturnType.put("getVolume()", "int");
		methodToReturnType.put("setVolume(1)", "void");
		methodToReturnType.put("getDefaultVolume()", "number");
		// BSound
		// BWindow
	}

	public boolean isRegistered(MethodInvocation method) {
		return isRegisteredAsReserved(method)
				|| isRegisteredAsUserMethod(method);
	}

	public boolean isRegisteredAsReserved(MethodInvocation method) {
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

	private String getReservedReturnType(String signature) {
		if (!methodToReturnType.containsKey(signature)) {
			return ExpressionModel.VOID;
		}
		return methodToReturnType.get(signature);
	}

	private String toSignature(MethodInvocation method) {
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
	private List<String> userConstructor = new ArrayList<String>();

	public boolean isRegisteredAsUserMethod(MethodInvocation method) {
		String signature = toSignature(method);
		return userMethods.containsKey(signature);
	}

	public String getUserMethodType(MethodInvocation method) {
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
	}

	// private String getReturnType(MethodInvocation node) {
	// System.out.println(node.getClass().toString());
	// System.out.println("pa:" + node.getParent().getClass().toString());
	// if (node.getParent() instanceof Assignment) {
	// Assignment assign = ((Assignment) node.getParent());
	// String name = assign.getLeftHandSide().toString();
	// return resolver.resolve(name).getType();
	// } else if (node.getParent() instanceof IfStatement) {
	// return "boolean";
	// } else if (node.getParent() instanceof WhileStatement) {
	// return "boolean";
	// } else if (node.getParent() instanceof VariableDeclarationFragment) {
	// System.out.println("aa");
	// if (node.getParent().getParent() instanceof VariableDeclarationStatement)
	// {
	// VariableDeclarationStatement dec = ((VariableDeclarationStatement) node
	// .getParent().getParent());
	// return dec.getType().toString();
	// } else if (node.getParent().getParent() instanceof
	// VariableDeclarationStatement) {
	// VariableDeclarationExpression dec = ((VariableDeclarationExpression) node
	// .getParent().getParent());
	// System.out.println(dec.getType().toString());
	// return dec.getType().toString();
	// } else {
	// throw new RuntimeException(
	// "unknown variable declaration fragment");
	// }
	// } else if (node.getParent() instanceof MethodInvocation) {
	// MethodInvocation parent = ((MethodInvocation) node.getParent());
	// int index = parent.arguments().indexOf(node);
	// parent.arguments
	// }
	//
	// if (node.getParent() instanceof ExpressionStatement) {
	// return ExpressionModel.VOID;
	// }
	//
	// throw new RuntimeException("not supported method call:"
	// + node.getName());
	// }
}
