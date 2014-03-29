package util;

public class ChangeExtension {

	public static String changeToXmlExtension(String filePath){
		String newFileName = filePath;
		
	    if (newFileName == null)
	        return null;
	    int point = newFileName.lastIndexOf(".");
	    if (point != -1) {
	        return newFileName.substring(0, point) + ".xml";
	    }
	    return newFileName + ".xml";
	}

	public static String changeToJavaExtension(String filePath){
		String newFileName = filePath;
		
	    if (newFileName == null)
	        return null;
	    int point = newFileName.lastIndexOf(".");
	    if (point != -1) {
	        return newFileName.substring(0, point) + ".java";
	    }
	    return newFileName + ".java";
	}
}
