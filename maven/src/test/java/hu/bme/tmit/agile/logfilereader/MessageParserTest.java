package hu.bme.tmit.agile.logfilereader;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


import hu.bme.tmit.agile.logfilereader.controller.MessageParser;
import hu.bme.tmit.agile.logfilereader.model.Message.MessageType;


public class MessageParserTest {
	@Test
	public void testSentMessage() {
		String testString = "2014/Oct/24 19:53:52.299956 2507 PORTEVENT sipClientInterface.ttcn:152(function:sipInitialize) Sent on sipInternalPort to mtc @variables.internalPortMessageWithAspsSip : {";
		String testParts[] = testString.split(" ");
		assertEquals(MessageParser.parseSentMessage(testParts).getSender(), "2507");
		assertEquals(MessageParser.parseSentMessage(testParts).getPort(), "sipInternalPort");
		assertEquals(MessageParser.parseSentMessage(testParts).getDestination(), "mtc");
		assertEquals(MessageParser.parseSentMessage(testParts).getName(), "@variables.internalPortMessageWithAspsSip");
		assertEquals(MessageParser.parseSentMessage(testParts).getEventType(), MessageType.Send);
	}
	@Test
	public void testReceiveMessage() {
		String testString = "2014/Oct/24 19:53:52.300152 mtc PORTEVENT SipClientLayerComponent.ttcn:254(function:receiveInternalMessage) Receive operation on port sipInternalPort[0] succeeded, message from 2507: @variables.internalPortMessageWithAspsSip : {";
		String testParts[] = testString.split(" ");
		assertEquals(MessageParser.parseReceivedMessage(testParts).getDestination(), "mtc");
		assertEquals(MessageParser.parseReceivedMessage(testParts).getPort(), "sipInternalPort[0]");
		assertEquals(MessageParser.parseReceivedMessage(testParts).getSender(), "2507");
		assertEquals(MessageParser.parseReceivedMessage(testParts).getName(), "@variables.internalPortMessageWithAspsSip");
		assertEquals(MessageParser.parseReceivedMessage(testParts).getEventType(), MessageType.Receive);
	}

}
