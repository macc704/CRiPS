package bc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockConverter {

	public static final String ENCODING_BLOCK_XML = "UTF-8";

	public static final String COLLAPSED_BLOCK_LABEL = "c//";
	public static final String COLLAPSED_BLOCK_LABEL_PLUGIN = " c//";
	
	public static Map<String, String> projectMethods = new HashMap<String, String>();
	// 色
	public static final String[] COLOR_NAMES = { "blue", "cyan", "green",
			"magenta", "orange", "pink", "red", "white", "yellow", "gray",
			"black", "lightGray", "darkGray" };

	public static final String[] DATA_BLOCKNAMES = { "toIntFromDouble",
			"toIntFromString", "toDoubleFromInt", "toDoubleFromString",
			"toStringFromInt", "toStringFromDouble", "toStringFromObject",
			"toStringFromString", "double-number", "number", "string", "true",
			"false", "pi", "e", "gettermember-ver-int-number",
			"gettermember-ver-double-number", "gettermember-var-string",
			"gettermember-var-boolean", "getterlocal-var-int-number",
			"getterlocal-var-double-number", "getterlocal-var-string",
			"getterlocal-var-boolean", "new-object", /* ! */
			"getterlocal-var-object", "null" };

	// 演算子はココに登録
	public static final String[] INFIX_COMMAND_BLOCKS = { "lessthan",
			"lessthanorequalto", "greaterthan", "greaterthanorequalto",
			"equals-number", "not-equals-number", "and", "or", "sum",
			"difference", "product", "quotient", "remainder", "string-append",
			"lessthan-double", "lessthanorequalto-double",
			"greaterthan-double", "greaterthanorequalto-double",
			"equals-number-double", "not-equals-number-double", "sum-double",
			"difference-double", "product-double", "quotient-double",
			"remainder-double", "equals-boolean", "not-equals-boolean",
			"equals-string", "instanceof" };

	// 戻り値のないメソッドはここに登録
	public static final String[] METHOD_CALL_BLOCKS = { "fd", "bk", "lt", "rt",
			"input", "print", "color", "up", "down", "atan", "random", "round",
			"min", "max", "pow", "abs", "sqrt", "sin", "cos", "tan", "asin",
			"acos", "log", "ln", "sleep", "hide", "update", "warp",
			"warpByTopLeft", "size", "scale", "large", "small", "wide",
			"narrow", "tall", "little", "show", "getX", "getY", "getWidth",
			"getHeight", "int", "double", "toString", /* ! */
			"x", "y", "width", "height", "setShow", "isShow", "key", "keyDown",
			"mouseX", "mouseY", "mouseClicked", "leftMouseClicked",
			"rightMouseClicked", "doubleClick", "mouseDown", "leftMouseDown",
			"rightMouseDown", "intersects", "contains", "image",
			"text"/* !! */, "looks","length"//string
			/* !sound! */, "file", "setFile", "play", "loop", "stop",
			"isPlaying", "getVolume", "setVolume"
			/* text */, "getText", "loadOnMemory"
			/* cui */, "cui-print", "cui-println", "cui-random", "next",
			"nextInt", "nextDouble", "hashCode", "input-getInt", "input-getDouble", "input-getString", "input-isInteger", "input-isDouble"
			/* empty! */, "empty"
			/* math */, "sqrt", "sin", "cos", "tan", "log", "toRadians",
			/* list */"get", "getSize", "add", "addFirst", "addLast", "addAll",
			"moveAllTo", "removeFirst", "removeLast", "removeAll", "getCursor",
			"setCursor", "moveCursorToNext", "moveCursorToPrevious",
			"getObjectAtCursor", "addToBeforeCursor", "addToAfterCursor",
			"removeAtCursor", "shuffle", "setBgColor"
			/* card */, "getNumber"
			/* button */, "isClicked"
			/* input *//*
						 * ,"getText" , "text"
						 */, "clearText", "setActive", "isActive",
			"toJapaneseMode", "toEnglishMode", "fontsize", 
			//BCanvas
			"drawArc",
			"drawFillTriangle",
			"drawLine", 
			"drawFillArc",
			"drawText",
			 "drawImage",
			 "isClick",
			 "getImageWidth", "getImageHeight", "clear", "update",
			 "isKeyCode", "isKeyDown", "getKeyCode", "isKeyPressing",
			"isSingleClick", "isDoubleClick", "isDragging", "isRightMouseDown",
			"isLeftMouseDown", "getMouseX", "getMouseY",
			"getCanvasWidth", "getCanvasHeight",
			/*BWindow*/"setLocation", "setSize", "getCanvas",  
			/*List*/"remove", "remove[@object]", "get","get[@number]",
			/*Scanner*/"isMouseDown", "hasNextInt", "hasNextDouble" ,/*Bsound*/"getVolume", "setVolume",
			"getDefaultVolume","play[@string]","loadOnMemory[@string]"};

	// 戻り値のあるメソッドはここに登録 (上にも登録しないとダメ)
	public final static String[] FUNCTION_METHODCALL_BLOCKS = { "input",
			"atan", "random", "round", "min", "max", "pow", "abs", "sqrt",
			"sin", "cos", "tan", "asin", "acos", "log", "ln", "getX", "getY",
			"getWidth", "getHeight", "int", "double", "toString", /* ! */"x",
			"y", "width", "height", "isShow", "key", "keyDown", "mouseX",
			"mouseY", "mouseClicked", "leftMouseClicked", "rightMouseClicked",
			"doubleClick", "mouseDown", "leftMouseDown", "rightMouseDown",
			"intersects", "contains", /* "image", "text", *//* !sound! */
			"isPlaying", "getVolume", "getText" /* cui */, "cui-random",
			"next", "nextInt", "nextDouble", "hashCode"/* math */, "sqrt",
			"sin", "cos", "tan", "log", "toRadians",/* list */"get", "getSize",
			"getCursor", "getObjectAtCursor", /* card */"getNumber",/* button */
			"isClicked"/* input *//* ,"getText" */, "isActive", "isClick",
			"isSingleClick", "isDoubleClick", "isDragging", "isRightMouseDown",
			"isLeftMouseDown", "getMouseX", "getMouseY", "isKeyPressing",
			"isKeyCode", "isKeyDown", "getKeyCode", "getImageWidth",
			"getImageHeight", "getCanvas", "getVolume", "getDefaultVolume",
			 "get", "Math.random", "Math.sqrt", "Math.sin", "Math.cos",
			"Math.tan", "Math.log", "Math.toRadians", "getCanvasWidth",
			"getCanvasHeight", "removeFirst", "isMouseDown", "get[@number]",
			"hasNextInt", "hasNextDouble" , "Input.getInt", "Input.getDouble", "Input.getString", "Input.isInteger", "Input.isDouble","length" };

	public static final String[] ALL_DATA_BLOCKNAMES;

	static {
		List<String> all = new ArrayList<String>();
		all.addAll(Arrays.asList(COLOR_NAMES));
		all.addAll(Arrays.asList(DATA_BLOCKNAMES));
		ALL_DATA_BLOCKNAMES = (String[]) all.toArray(new String[all.size()]);
	}

}
