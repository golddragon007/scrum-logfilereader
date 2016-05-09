package hu.bme.tmit.agile.logfilereader.controller;

import hu.bme.tmit.agile.logfilereader.model.Message;
import hu.bme.tmit.agile.logfilereader.model.Message.MessageType;
import util.Utils;

public class MessageParser {

	public static Message parseSentMessage(String[] parts) {
		Message m = new Message();

		m.setSender(parts[2]);
		m.setPort(parts[7]);
		m.setDestination(parts[9]);
		m.setName(parts[10]);
		m.setEventType(MessageType.Send);

		return m;
	}

	public static Message parseReceivedMessage(String[] parts) {
		Message m = new Message();

		m.setDestination(parts[2]);
		m.setPort(parts[9]);
		if (parts[13].equals("system():"))
		{
			m.setSender(Utils.removeLastThreeCharacter(parts[13]));
		}
		else
		{
			m.setSender(parts[13]);
		}
		m.setName(parts[14]);
		m.setEventType(MessageType.Receive);
		
		return m;
	}
}
