package hu.bme.tmit.agile.logfilereader;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import util.EventIdentifier;
import util.RegexpProperties;
import util.PropertyHandler;
import util.RegexpPatterns;

public class TimestampParserTest {

	private final String date = "2014/Oct/24";
	private String dateParts[], yearPattern, monthPattern, dayPattern;

	private void applyRules() {
		PropertyHandler ph = new PropertyHandler();
		Properties properties = ph.getProperties(RegexpProperties.REGEXP_PATTERNS_PROPERTIES);
		RegexpPatterns.datePattern = properties.getProperty(RegexpProperties.DATE_PROPERTY);
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
