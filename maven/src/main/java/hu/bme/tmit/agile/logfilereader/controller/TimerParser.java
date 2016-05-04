package hu.bme.tmit.agile.logfilereader.controller;

import hu.bme.tmit.agile.logfilereader.model.TimerOperation;
import hu.bme.tmit.agile.logfilereader.model.TimerOperation.EventType;
import util.Utils;

public class TimerParser {

	private static final String TIMER_STARTED = "Start";
	private static final String TIMER_STOPPED = "Stop";
	private static final String TIMEOUT = "Timeout";

	public static TimerOperation parseTimer(String[] words) {
		String name = null;
		double duration = 0;
		TimerOperation to = new TimerOperation();
		if (isTimerStarted(words[5])) {
			name = Utils.removeLastCharacter(words[7]);
			duration = Double.parseDouble(words[8]);
			to.setEventType(EventType.Start);
		} else if (isTimerStopped(words[5])) {
			name = Utils.removeLastCharacter(words[7]);
			duration = Double.parseDouble(words[8]);
			to.setEventType(EventType.Stop);
		} else if (isTimeout(words[5])) {
			name = Utils.removeLastCharacter(words[6]);
			duration = Double.parseDouble(words[7]);
			to.setEventType(EventType.Timeout);
		}
		to.setName(name);
		to.setDuration(duration);
		return to;
	}

	private static boolean isTimerStarted(String word) {
		return word.equals(TIMER_STARTED);
	}

	private static boolean isTimerStopped(String word) {
		return word.equals(TIMER_STOPPED);
	}

	private static boolean isTimeout(String word) {
		return word.equals(TIMEOUT);
	}
}
