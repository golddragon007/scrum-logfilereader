package hu.bme.tmit.agile.logfilereader;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import hu.bme.tmit.agile.logfilereader.controller.TerminatedComponentParser;
import hu.bme.tmit.agile.logfilereader.model.ComponentEvent.ComponentEventType;

public class TerminatedComponentParserTest {

	@Test
	public void testTerminatedComponent() {
		String testString = "2014/Oct/24 19:53:07.338123 2456 PARALLEL - Terminating component type SipClientComponent.sipClientComponent.";
		String testParts[] = testString.split(" ");
		assertEquals(TerminatedComponentParser.parseTerminatedComponent(testParts).getComponentType(),
				"SipClientComponent.sipClientComponent");
		assertEquals(TerminatedComponentParser.parseTerminatedComponent(testParts).getComponentEventType(),
				ComponentEventType.Terminate);
	}
}