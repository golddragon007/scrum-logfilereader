package hu.bme.tmit.agile.logfilereader.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

import util.PropertyHandler;

public class Parser {

	private static final String DATE_PROPERTY = "date";
	private static final String REGEXP_PATTERNS_PROPERTIES = "regexp_patterns.properties";
	
	private void applyRules() {
		PropertyHandler ph = new PropertyHandler();
		Properties properties = ph.getProperties(REGEXP_PATTERNS_PROPERTIES);
		RegexpPatterns.datePattern = properties.getProperty(DATE_PROPERTY);
	}
	
	public void parse(String relativePath) {
		applyRules();
		try {
			for (String line : Files.readAllLines((new File(relativePath).toPath()))) {
				String words[] = line.split(" ");
				if(matchesDate(words[0])) {
					String dateString = words[0];
					String timeString = words[1];
//					Date date = parseDate(dateString, timeString);
					String sender = words[2];
					if(isTimerOperation(words[3])) {
						parseTimer(words);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseTimer(String[] words) {
		String name = null, duration = null;
		if(isTimerStarted(words[5])) {
			name = words[7].substring(0, words[7].length()-1);
			duration = words[8];
		}
		else if(isTimerStopped(words[5])) {
			name = words[7].substring(0, words[7].length()-1);
			duration = words[8];
		}
		else if(isTimeout(words[5])) {
			name = words[6].substring(0, words[6].length()-1);
			duration = words[7];
		}
	}

	private boolean isTimerStarted(String word) {
		return word.equals("Start");
	}
	
	private boolean isTimerStopped(String word) {
		return word.equals("Stop");
	}
	
	private boolean isTimeout(String word) {
		return word.equals("Timeout");
	}

	private boolean isTimerOperation(String words) {
		return words.equals("TIMEROP");
	}

	public boolean matchesDate(String date) {
		return date.matches(RegexpPatterns.datePattern);
	}
}
