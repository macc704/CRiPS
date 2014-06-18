package obpro.turtle;




/**
 * JavaVertionChecker
 * TODO 次回公開時 パッケージリファクタリング
 */
public class JavaVertionChecker {

	public static void main(String[] args) {
		printArray(splitByDot("34.4.4"));
		printArray(getVMVersion());
	}

	public static int getMajorVersion() {
		return getVersion(0);
	}

	public static int getMinorVersion() {
		return getVersion(1);
	}

	public static int getVersion(int i) {
		try {
			return Integer.parseInt(getVMVersion()[i]);
		} catch (Exception ex) {
			return -1;
		}
	}

	public static String[] getVMVersion() {
		return splitByDot(System.getProperty("java.version"));
	}

	public static String[] splitByDot(String text) {
		String[] versionStrings = text.split("[.]");
		return versionStrings;
	}

	private static void printArray(Object[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.println(array[i]);
		}
	}

}
