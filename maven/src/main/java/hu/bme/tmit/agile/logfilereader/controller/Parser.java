package hu.bme.tmit.agile.logfilereader.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import hu.bme.tmit.agile.logfilereader.model.CreatedTerminatedComponent;
import hu.bme.tmit.agile.logfilereader.model.LogTimestamp;
import hu.bme.tmit.agile.logfilereader.model.Message;
import hu.bme.tmit.agile.logfilereader.model.TimerOperation;
import hu.bme.tmit.agile.logfilereader.model.TtcnEvent;
import hu.bme.tmit.agile.logfilereader.model.VerdictOperation;

import util.PropertyHandler;
import util.RegexpPatterns;

public class Parser {

	public static final String CREATED_COMPONENT = "Created";
	public static final String TERMINATED_COMPONENT = "Terminated";
	
	private static final String DATE_PROPERTY = "date";
	private static final String TIME_PROPERTY = "time";
	private static final String VERDICT_PROPERTY = "verdictData";
	private static final String SEND_MESSAGE_PROPERTY = "verdictData";
	private static final String RECEIVED_MESSAGE_PROPERTY = "verdictData";
	private static boolean isparam= false;
	private static String message=null;
	private Message m;

	private static final String REGEXP_PATTERNS_PROPERTIES = "regexp_patterns.properties";
	
	private List<TtcnEvent> eventList = new ArrayList<TtcnEvent>();

	private void applyParsingRules() {
		PropertyHandler ph = new PropertyHandler();
		Properties properties = ph.getProperties(REGEXP_PATTERNS_PROPERTIES);
		RegexpPatterns.datePattern = properties.getProperty(DATE_PROPERTY);
		RegexpPatterns.timePattern = properties.getProperty(TIME_PROPERTY);
		RegexpPatterns.verdictData = properties.getProperty(VERDICT_PROPERTY);
		RegexpPatterns.sentPattern = properties.getProperty(SEND_MESSAGE_PROPERTY);
		RegexpPatterns.receivePattern = properties.getProperty(RECEIVED_MESSAGE_PROPERTY);
		
	}

	public void parse(String relativePath) {
		applyParsingRules();

		try {
			for (String line : Files.readAllLines((new File(relativePath).toPath()))) {
				String parts[] = line.split(" ");
				if (matchesDate(parts[0])) {
					if(isparam){
						m.setParam(message);
						isparam=false;
					}
					LogTimestamp timestamp = TimestampParser.parse(parts);
					String sender = parts[2];
					if (isTimerOperation(parts[3])) {
						TimerOperation to = TimerParser.parseTimer(parts);
						to.setSender(sender);
						to.setTimestamp(timestamp);
						eventList.add(to);
					} else if (parts.length >= 7 && isVerdictOperation(parts[3])) {
						String backOfLine=null;
						for(int i=4; i<parts.length; i++)
						{
							backOfLine+=(parts[i]+" ");
							
						}
						
						VerdictOperation vo = VerdictParser.parseVerdict(backOfLine);
						vo.setTimestamp(timestamp);
						vo.setSender(sender);
						eventList.add(vo);
						if(vo.getComponentName()== null)
						{
							
						}
						
					} else if (parts.length >= 20 && isCreatedComponent(parts[6], parts[7])) {
						if (isComponentType(parts[13])) {
							CreatedTerminatedComponent ctc = CreatedTerminatedComponentParser.parse(parts, CREATED_COMPONENT);
							ctc.setTimestamp(timestamp);
							ctc.setSender(sender);
							eventList.add(ctc);
						}
					} else if (parts.length >= 9 && isTerminatingComponent(parts[5], parts[6])) {
						CreatedTerminatedComponent ctc = CreatedTerminatedComponentParser.parse(parts, TERMINATED_COMPONENT);
						ctc.setTimestamp(timestamp);
						ctc.setSender(sender);
						eventList.add(ctc);
					} else if (parts.length >=13 && isSentOnOperation(parts[5],parts[6])) {
						isparam=true;
						m = MessageParser.parseSent(parts);
						m.setTimestamp(timestamp); 
						eventList.add(m);

					} else if (parts.length >=17 && isReceiveOperationOn(parts[5],parts[6],parts[7])) {
						isparam=true;
						m = MessageParser.parseReceive(parts);
						m.setTimestamp(timestamp);
						eventList.add(m);

			}
				}else if(isparam){
					message.concat(line);
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
	
	private boolean isSentOnOperation(String words1, String words2) {
		return (words1.equals("Sent") && words2.equals("on"));
	}
	
	private boolean isReceiveOperationOn(String words1, String words2,String words3) {
		return (words1.equals("Receive") && words2.equals("operation") && words3.equals("on"));
	}

	public List<TtcnEvent> getEventList() {
		return eventList;
	}

	public void setEventList(List<TtcnEvent> eventList) {
		this.eventList = eventList;
	}
	
}
