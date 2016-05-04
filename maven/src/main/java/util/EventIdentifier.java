package util;

public class EventIdentifier {

	private static final String TIMEROP_IDENTIFIER = "TIMEROP";
	private static final String VERDICTOP_IDENTIFIER = "VERDICTOP";

	public static boolean isTimerOperation(String words) {
		return words.equals(TIMEROP_IDENTIFIER);
	}

	public static boolean matchesDate(String date) {
		return date.matches(RegexpPatterns.datePattern);
	}

	public static boolean isVerdictOperation(String word1, String word2) {
		return word1.equals(VERDICTOP_IDENTIFIER) && word2.equals("getverdict:");
	}

	public static boolean isCreatedComponent(String word1, String word2) {
		return (word1.equals("was") && word2.equals("created."));
	}

	public static boolean isTerminatedComponent(String word1, String word2) {
		return (word1.equals("Terminating") && word2.equals("component"));
	}

	public static boolean isComponentType(String words) {
		if (words.equals("type:"))
			return false;
		else
			return true;
	}

	public static boolean isSentMessage(String word1, String word2) {
		return (word1.equals("Sent") && word2.equals("on"));
	}

	public static boolean isReceivedMessage(String word1, String word2, String word3) {
		return (word1.equals("Receive") && word2.equals("operation") && word3.equals("on"));
	}

}
