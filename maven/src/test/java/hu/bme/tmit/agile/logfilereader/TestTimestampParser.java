package hu.bme.tmit.agile.logfilereader;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import hu.bme.tmit.agile.logfilereader.controller.Parser;
import util.EventIdentifier;
import util.PropertyHandler;
import util.RegexpPatterns;

public class TestTimestampParser {

	private Parser parser = new Parser();
	private final String date = "2014/Oct/24";
	private String dateParts[], yearPattern, monthPattern, dayPattern;

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
		String datePatternParts[] = RegexpPatterns.datePattern.split("/");
		yearPattern = datePatternParts[0];
		monthPattern = datePatternParts[1];
		dayPattern = datePatternParts[2];
	}

	@Test
	public void matchesYear() {
		assertTrue(dateParts[0].matches(yearPattern));
	}

	@Test
	public void matchesMonth() {
		assertTrue(dateParts[1].matches(monthPattern));
	}

	@Test
	public void matchesDay() {
		assertTrue(dateParts[2].matches(dayPattern));
	}

	@Test
	public void matchesDateFromProperties() {
		assertTrue(EventIdentifier.matchesDate(date));
	}
}
