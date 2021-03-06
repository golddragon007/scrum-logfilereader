package hu.bme.tmit.agile.logfilereader;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import hu.bme.tmit.agile.logfilereader.controller.VerdictParser;
import hu.bme.tmit.agile.logfilereader.model.VerdictOperation.VerdictType;
import util.RegexpProperties;
import util.PropertyHandler;
import util.RegexpPatterns;

public class VerdictParserTest {

	private void applyRules() {
		PropertyHandler ph = new PropertyHandler();
		Properties properties = ph.getProperties(RegexpProperties.REGEXP_PATTERNS_PROPERTIES);
		RegexpPatterns.verdictPattern = properties.getProperty(RegexpProperties.VERDICT_PROPERTY);
	}

	@Before
	public void setUp() {
		applyRules();
	}

	@Test
	public void testParseVerdictCorrectInput() {
		String testString = "SipClientLayerComponent.ttcn:238(function:receiveInternalMessage) getverdict: pass";
		assertEquals(VerdictParser.parseVerdict(testString).getComponentName(), "SipClientLayerComponent");
		assertEquals(VerdictParser.parseVerdict(testString).getMiscText(), "(function:receiveInternalMessage)");
		assertEquals(VerdictParser.parseVerdict(testString).getPortNumber(), 238);
		assertEquals(VerdictParser.parseVerdict(testString).getVerdictType(), VerdictType.Pass);
	}

	@Test
	public void testParseVerdictEmptyInput() {
		String testString = "";
		assertEquals(VerdictParser.parseVerdict(testString).getComponentName(), "");
		assertEquals(VerdictParser.parseVerdict(testString).getMiscText(), "");
		assertEquals(VerdictParser.parseVerdict(testString).getPortNumber(), 0);
		assertEquals(VerdictParser.parseVerdict(testString).getVerdictType(), VerdictType.Inconclusive);
	}

	@Test
	public void testParseVerdictCorrectInputWithHeaderText() {
		String testString = "2014/Oct/24 19:53:25.444126 mtc VERDICTOP HttpNodeLayerComponent.ttcn:229(function:receiveInternalMessage) getverdict: pass";
		assertEquals(VerdictParser.parseVerdict(testString).getComponentName(), "");
		assertEquals(VerdictParser.parseVerdict(testString).getMiscText(), "");
		assertEquals(VerdictParser.parseVerdict(testString).getPortNumber(), 0);
		assertEquals(VerdictParser.parseVerdict(testString).getVerdictType(), VerdictType.Inconclusive);
	}

	@Test
	public void testParseVerdictIncorrectInput() {
		String testString = "HttpNodeLayerComponent.ttcn:236(function:receiveInternalMessage) Start timer TimerTestCaseInternalCommunicationGuard: 0.01 s";
		assertEquals(VerdictParser.parseVerdict(testString).getComponentName(), "");
		assertEquals(VerdictParser.parseVerdict(testString).getMiscText(), "");
		assertEquals(VerdictParser.parseVerdict(testString).getPortNumber(), 0);
		assertEquals(VerdictParser.parseVerdict(testString).getVerdictType(), VerdictType.Inconclusive);
	}
}
