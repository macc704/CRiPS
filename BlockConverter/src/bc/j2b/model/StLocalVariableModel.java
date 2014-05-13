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
					return "proc-param-object-" + getJavaVariableType() + "-arrayobject";
				}else{
					return "proc-param-object-" + getJavaVariableType();	
				}
			}else{
				if(isArray()){
					return "proc-param-" + convertArrayVariableTypeToBlockVariableType(getJavaVariableType()) + "-arrayobject";
				}else{
					return "proc-param-" + getBlockType();		
				}
			}
			
		}else{
			String genusName;
			if (isProjectObject()) {
				genusName = "object-" + getType();
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
