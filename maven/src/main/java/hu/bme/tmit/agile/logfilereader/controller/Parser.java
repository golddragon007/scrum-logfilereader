package hu.bme.tmit.agile.logfilereader.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.PropertyHandler;

public class Parser {

	private static final String DATE_PROPERTY = "date";
	private static final String COMPONENTANDPORT_PROPERTY = "componentAndPort";
	private static final String REGEXP_PATTERNS_PROPERTIES = "regexp_patterns.properties";
	
	private void applyRules() {
		PropertyHandler ph = new PropertyHandler();
		Properties properties = ph.getProperties(REGEXP_PATTERNS_PROPERTIES);
		RegexpPatterns.datePattern = properties.getProperty(DATE_PROPERTY);
		RegexpPatterns.componentAndPortPattern=properties.getProperty(COMPONENTANDPORT_PROPERTY);
	}
	
	public void parse(String relativePath) {
		applyRules();
		try {
			for (String line : Files.readAllLines((new File(relativePath).toPath()))) {
				String parts[] = line.split(" ");
				if(matchesDate(parts[0])) {
					String dateString = parts[0];
					String timeString = parts[1];
//					Date date = parseDate(dateString, timeString);
					String sender = parts[2];
					if(isTimerOperation(parts[3])) {
						parseTimer(parts);
					}
					// úgyis akkor kell csekkolni csak, ha az eleje dátum, és egyszerre nem állhat fent max egy
					else if(parts.length >= 20 && isCreatedComponent(parts[6], parts[7])) {
						String componentReference = parts[10].substring(0, parts[10].length()-1);
						if(isComponentType(parts[13])) {
							String componentType = parts[13];
							String testcaseName = parts[16].substring(0, parts[16].length()-1);
							String processID = parts[19].substring(0, parts[19].length()-1);
						}
					}
//					ez így szar pl.
//					2014/Oct/24 19:53:07.322255 2464 VERDICTOP - Final verdict of PTC: pass reason: "Life is beautiful!"
//					miatt
					else if(parts.length >= 8 && isVerdictOperation(parts[3]))
					{
						String sourceOfOperation = parts[2];	// így már ez sem kell
						String operationType = parts[3];
						String miscText = parts[5];
						String verdict = parts[7];
//						parseComponentAndPort(parts[4]);
						
					}
					else if(parts.length >= 9 && isTerminatingComponent(parts[5], parts[6])) {
						String componentTye = parts[8].substring(0, parts[8].length()-1);		
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
	
	private void parseComponentAndPort(String word){
		String componentName = null, portNumber = null;
		Pattern p = Pattern.compile(RegexpPatterns.componentAndPortPattern);
		Matcher m = p.matcher(word);
		componentName=m.group(0);
		portNumber=m.group(1);
		
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
	
	private boolean isVerdictOperation(String words){
		return words.equals("VERDICTOP");
	} 
	
	private boolean isCreatedComponent(String words1, String words2) {
		return (words1.equals("was") && words2.equals("created."));
	}
	
	private boolean isTerminatingComponent(String words1, String words2) {
		return (words1.equals("Terminating") && words2.equals("component"));
	}
	
	private boolean isComponentType(String words) {
		if(words.equals("type:"))
			return false;
		else
			return true;
	}
}
