package hu.bme.tmit.agile.logfilereader.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.Properties;
import java.util.TreeSet;

import hu.bme.tmit.agile.logfilereader.model.ComponentEvent;
import hu.bme.tmit.agile.logfilereader.model.LogTimestamp;
import hu.bme.tmit.agile.logfilereader.model.Message;
import hu.bme.tmit.agile.logfilereader.model.TimerOperation;
import hu.bme.tmit.agile.logfilereader.model.TtcnEvent;
import hu.bme.tmit.agile.logfilereader.model.VerdictOperation;
import util.EventIdentifier;
import util.PropertyHandler;
import util.RegexpPatterns;

public class Parser {

	private static final String REGEXP_PATTERNS_PROPERTIES = "regexp_patterns.properties";

	private static final String DATE_PROPERTY = "date";
	private static final String TIME_PROPERTY = "time";
	private static final String VERDICT_PROPERTY = "verdict";

	private static final String SENT_MESSAGE_PROPERTY = "sent";
	private static final String RECEIVED_MESSAGE_PROPERTY = "recive";

	private boolean isMessageParam = false;
	private String messageParam = "";
	private Message message;

	private TreeSet<TtcnEvent> eventSet = new TreeSet<TtcnEvent>();

	private void applyParsingRules() {
		PropertyHandler ph = new PropertyHandler();
		Properties properties = ph.getProperties(REGEXP_PATTERNS_PROPERTIES);

		RegexpPatterns.datePattern = properties.getProperty(DATE_PROPERTY);
		RegexpPatterns.timePattern = properties.getProperty(TIME_PROPERTY);
		RegexpPatterns.verdictPattern = properties.getProperty(VERDICT_PROPERTY);
		RegexpPatterns.sentMessagePattern = properties.getProperty(SENT_MESSAGE_PROPERTY);
		RegexpPatterns.receivedMessagePattern = properties.getProperty(RECEIVED_MESSAGE_PROPERTY);
	}

	public void parse(String relativePath) {
		applyParsingRules();
		File file = new File(relativePath);
		String fileName = file.getName();

		try {
			for (String line : Files.readAllLines(file.toPath())) {
				String parts[] = line.split(" ");
				if (EventIdentifier.matchesDate(parts[0])) {
					if (isMessageParam) {
						message.setParam(messageParam);
						isMessageParam = false;
						messageParam = "";
					}
					LogTimestamp timestamp = TimestampParser.parse(parts);
					String sender = parts[2];
					if (EventIdentifier.isTimerOperation(parts[3])) {
						TimerOperation to = TimerParser.parseTimer(parts);
						to = (TimerOperation) setTtcnEventParams(fileName, timestamp, sender, to);
						eventSet.add(to);
					} else if (parts.length >= 7 && EventIdentifier.isVerdictOperation(parts[3], parts[5])) {
						String backOfLine = "";
						for (int i = 4; i < parts.length; i++) {
							backOfLine += (parts[i] + " ");

						}
						VerdictOperation vo = VerdictParser.parseVerdict(backOfLine);
						vo = (VerdictOperation) setTtcnEventParams(fileName, timestamp, sender, vo);
						eventSet.add(vo);
					} else if (parts.length >= 20 && EventIdentifier.isCreatedComponent(parts[6], parts[7])) {
						if (EventIdentifier.isComponentType(parts[13])) {
							ComponentEvent ce = CreatedComponentParser.parseCreatedComponent(parts);
							ce = (ComponentEvent) setTtcnEventParams(fileName, timestamp, sender, ce);
							eventSet.add(ce);
						}
					} else if (parts.length >= 9 && EventIdentifier.isTerminatedComponent(parts[5], parts[6])) {
						ComponentEvent ce = TerminatedComponentParser.parseTerminatedComponent(parts);
						ce = (ComponentEvent) setTtcnEventParams(fileName, timestamp, sender, ce);
						eventSet.add(ce);
					} else if (parts.length >= 13 && EventIdentifier.isSentMessage(parts[5], parts[6])) {
						message = MessageParser.parseSentMessage(parts);
						setMessageParams(fileName, timestamp);
						eventSet.add(message);

					} else if (parts.length >= 17 && EventIdentifier.isReceivedMessage(parts[5], parts[6], parts[7])) {
						message = MessageParser.parseReceivedMessage(parts);
						setMessageParams(fileName, timestamp);
						eventSet.add(message);

					}
				} else if (isMessageParam) {
					messageParam += line;
					messageParam += "\n";
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private TtcnEvent setTtcnEventParams(String fileName, LogTimestamp timestamp, String sender, TtcnEvent event) {
		event.setFileName(fileName);
		event.setSender(sender);
		event.setTimestamp(timestamp);
		return event;
	}

	private void setMessageParams(String fileName, LogTimestamp timestamp) {
		message.setTimestamp(timestamp);
		message.setFileName(fileName);
		isMessageParam = true;
	}

	public TreeSet<TtcnEvent> getEventSet() {
		return eventSet;
	}
}
