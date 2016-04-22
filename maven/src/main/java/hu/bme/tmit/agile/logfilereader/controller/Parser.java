package hu.bme.tmit.agile.logfilereader.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.Properties;

import hu.bme.tmit.agile.logfilereader.model.CreatedComponent;
import hu.bme.tmit.agile.logfilereader.model.LogTimestamp;
import hu.bme.tmit.agile.logfilereader.model.TerminatingComponent;
import hu.bme.tmit.agile.logfilereader.model.TimerOperation;
import hu.bme.tmit.agile.logfilereader.model.VerdictOperation;
import util.PropertyHandler;
import util.RegexpPatterns;

public class Parser {

	private static final String DATE_PROPERTY = "date";
	private static final String TIME_PROPERTY = "time";
	private static final String COMPONENT_AND_PORT_PROPERTY = "componentAndPort";

	private static final String REGEXP_PATTERNS_PROPERTIES = "regexp_patterns.properties";

	private void applyParsingRules() {
		PropertyHandler ph = new PropertyHandler();
		Properties properties = ph.getProperties(REGEXP_PATTERNS_PROPERTIES);
		RegexpPatterns.datePattern = properties.getProperty(DATE_PROPERTY);
		RegexpPatterns.timePattern = properties.getProperty(TIME_PROPERTY);
		RegexpPatterns.componentAndPortPattern = properties.getProperty(COMPONENT_AND_PORT_PROPERTY);
	}

	public void parse(String relativePath) {
		applyParsingRules();

		try {
			for (String line : Files.readAllLines((new File(relativePath).toPath()))) {
				String parts[] = line.split(" ");
				if (matchesDate(parts[0])) {
					LogTimestamp timestamp = TimestampParser.parse(parts);
					String sender = parts[2];
					if (isTimerOperation(parts[3])) {
						TimerOperation to = TimerParser.parseTimer(parts);
						to.setSender(sender);
						to.setTimestamp(timestamp);
					} else if (parts.length >= 8 && isVerdictOperation(parts[3])) {
						VerdictOperation vo = VerdictParser.parseVerdict(parts);
					} else if (parts.length >= 20 && isCreatedComponent(parts[6], parts[7])) {
						if (isComponentType(parts[13])) {
							CreatedComponent cc = CreatedComponentParser.parseCreated(parts);
							cc.setTimestamp(timestamp);
							cc.setSender(sender);
						}
					} else if (parts.length >= 9 && isTerminatingComponent(parts[5], parts[6])) {
						TerminatingComponent tc = TerminatingComponentParser.parseTerminating(parts);
						tc.setTimestamp(timestamp);
						tc.setSender(sender);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private boolean isTimerOperation(String words) {
		return words.equals("TIMEROP");
	}

	public boolean matchesDate(String date) {
		return date.matches(RegexpPatterns.datePattern);
	}

	private boolean isVerdictOperation(String words) {
		return words.equals("VERDICTOP");
	}

	private boolean isCreatedComponent(String words1, String words2) {
		return (words1.equals("was") && words2.equals("created."));
	}

	private boolean isTerminatingComponent(String words1, String words2) {
		return (words1.equals("Terminating") && words2.equals("component"));
	}

	private boolean isComponentType(String words) {
		if (words.equals("type:"))
			return false;
		else
			return true;
	}
}
