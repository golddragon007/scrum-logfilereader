package hu.bme.tmit.agile.logfilereader.controller;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import util.EventIdentifier;
import util.PropertyHandler;
import util.RegexpPatterns;

public class ParserTest {

	Parser parser = new Parser();
	String date = new String("2014/Oct/24");
	String dateParts[];

	private static final String DATE_PROPERTY = "date";
	private static final String REGEXP_PATTERNS_PROPERTIES = "regexp_patterns.properties";

	private void applyRules() {
		PropertyHandler ph = new PropertyHandler();
		Properties properties = ph.getProperties(REGEXP_PATTERNS_PROPERTIES);
		RegexpPatterns.datePattern = properties.getProperty(DATE_PROPERTY);
	}

	@Before
	public void setUp() {
		dateParts = date.split("/");
		applyRules();
	}

	@Test
	public void matchesYear() {
		assertTrue(dateParts[0].matches("[1-2]\\d\\d\\d"));
	}

	@Test
	public void matchesMonth() {
		assertTrue(dateParts[1].matches("\\D\\D\\D"));
	}

	@Test
	public void matchesDay() {
		assertTrue(dateParts[2].matches("[0-3]\\d"));
	}

	@Test
	public void matchesDate() {
		assertTrue(date.matches("[1-2]\\d\\d\\d/\\D\\D\\D/[0-3]\\d"));
	}

	@Test
	public void matchesDateFromProperties() {
		assertTrue(EventIdentifier.matchesDate(date));
	}

}
