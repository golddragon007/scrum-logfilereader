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
			for (String string : Files.readAllLines((new File(relativePath).toPath()))) {
				System.out.println(string);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
