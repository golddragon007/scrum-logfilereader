package util;

public class Utils {
	public static String removeLastCharacter(String word) {
		return word.substring(0, word.length() - 1);
	}

	public static String removeLastThreeCharacter(String word) {
		return word.substring(0, word.length() - 3);
	}
}
