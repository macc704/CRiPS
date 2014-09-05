package bc.j2b.model;

/**
 * @author yasui
 */
public class StLocalVariableModel extends StVariableDeclarationModel {

	private boolean argument;

	// public StLocalVariableModel() {
	// this(false);
	// }

	public StLocalVariableModel(boolean argument) {
		this.argument = argument;
	}

	@Override
	public String getGenusName() {
		if (argument) {
			if(isProjectObject()){
				if(isArray()){
					String name = removeSuperBrackets(ElementModel.addEscapeSequence(getJavaVariableType().toLowerCase()));
					return "proc-param-object-" + name + "-arrayobject";
				}else{
					return "proc-param-object-" + ElementModel.addEscapeSequence(getJavaVariableType().toLowerCase());	
				}
			}else{
				if(isArray()){
					return "proc-param-" + convertArrayVariableTypeToBlockVariableType(getJavaVariableType()).toLowerCase() + "-arrayobject";
				}else{
					return "proc-param-" + convertJavaTypeToBlockGenusName(getType());		
				}
			}
			
		}else{
			String genusName;
			if (isProjectObject()) {
				String type = getType();
				if(type.contains("[]")){
					type = type.substring(0, type.indexOf("[]"));
				}
				genusName = "object-" +type;
			} else {
				genusName = convertJavaTypeToBlockGenusName(getType());
			}

			if (genusName.equals("number")) {
				genusName = "int-number";
			}

			if (isArray()) {
				genusName += "-arrayobject";
			}

			return "local-var-" + genusName;
		}
	}

	private String convertArrayVariableTypeToBlockVariableType(String type) {

		String convertedType = type;
		// 配列引数の型を変換する
		if (type.contains("[")) {
			convertedType = convertedType.substring(0,
					convertedType.indexOf("["));
			convertedType = convertBasicJavaDataTypeToBlockType(convertedType);

			return convertedType;
		} else {
			return type;
		}
	}
	
	private String convertBasicJavaDataTypeToBlockType(String type) {
		// 基本的なデータ型のみ所定の名前に変更する
		if ("int".equals(type)) {
			return "int-number";
		} else if ("double".equals(type)) {
			return "double-number";
		} else if ("String".equals(type)) {
			return "string";
		} else {
			return type;
		}
	}

}
