package hu.bme.tmit.agile.logfilereader;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import hu.bme.tmit.agile.logfilereader.controller.CreatedComponentParser;
import hu.bme.tmit.agile.logfilereader.controller.TerminatedComponentParser;
import hu.bme.tmit.agile.logfilereader.model.ComponentEvent.ComponentEventType;

public class testCreatedComponentParser {
	
	@Test
	public void testCreatedComponent() {
		String testString = "2014/Oct/24 19:53:04.996229 hc PARALLEL - PTC was created. Component reference: 2456, component type: SipClientComponent.sipClientComponent, testcase name: WCG100200010, process id: 17927.";
		String testParts[] = testString.split(" ");
		assertEquals(CreatedComponentParser.parseCreatedComponent(testParts).getComponentReference(), 2456);
		assertEquals(CreatedComponentParser.parseCreatedComponent(testParts).getComponentType(), "SipClientComponent.sipClientComponent");
		assertEquals(CreatedComponentParser.parseCreatedComponent(testParts).getTestcaseName(), "WCG100200010");
		assertEquals(CreatedComponentParser.parseCreatedComponent(testParts).getProcessID(), 17927);		
		assertEquals(CreatedComponentParser.parseCreatedComponent(testParts).getComponentEventType(), ComponentEventType.Create);
	}

}