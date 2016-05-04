package hu.bme.tmit.agile.logfilereader;

import hu.bme.tmit.agile.logfilereader.controller.Parser;
import hu.bme.tmit.agile.logfilereader.dao.ParserDAO;
import hu.bme.tmit.agile.logfilereader.model.ComponentEvent;
import hu.bme.tmit.agile.logfilereader.model.ComponentEvent.ComponentType;
import hu.bme.tmit.agile.logfilereader.model.LogTimestamp;
import hu.bme.tmit.agile.logfilereader.model.Message;
import hu.bme.tmit.agile.logfilereader.model.Message.MessageType;
import hu.bme.tmit.agile.logfilereader.model.TimerOperation;
import hu.bme.tmit.agile.logfilereader.model.TimerOperation.EventType;
import hu.bme.tmit.agile.logfilereader.model.VerdictOperation;
import hu.bme.tmit.agile.logfilereader.model.VerdictOperation.VerdictType;

public class App {
	public static void main(String[] args) {
		// final String QUERY = "SELECT name FROM message";

		// Connector conn = new Connector();
		// conn.executeQuery(QUERY);

		Parser parser = new Parser();
		ParserDAO dao = new ParserDAO();

		Message m1 = new Message();
		m1.setDestination("hc");
		m1.setEventType(MessageType.Send);
		m1.setFileName("test1.txt");
		m1.setName("variables.internalPortMessageWithAspsSip");
		m1.setParam("asafsfewfewfwfq");
		m1.setPort("sipInternalPort");
		m1.setSender("mtc");
		m1.setTimestamp(new LogTimestamp(2005, 07, 2, 4, 53, 53, 5444));

		// dao.saveTtcnEvent(m1);

		TimerOperation to = new TimerOperation();
		to.setDuration(0.56);
		to.setEventType(EventType.Start);
		to.setFileName("test1.txt");
		to.setName("valamitimer");
		to.setSender("mtc");
		to.setTimestamp(new LogTimestamp(2005, 07, 2, 4, 53, 53, 55555));

		// dao.saveTtcnEvent(to);

		VerdictOperation vo = new VerdictOperation();
		vo.setSender("mtc");
		vo.setVerdictType(VerdictType.Pass);

		// dao.saveTtcnEvent(vo);

		ComponentEvent ce = new ComponentEvent();
		ce.setComponentReference(123123);
		ce.setComponentType("asdasd");
		ce.setCompType(ComponentType.Create);
		ce.setFileName("test1.txt");
		ce.setSender("mtc");
		ce.setTestcaseName("random");
		ce.setTimestamp(new LogTimestamp(2005, 07, 2, 4, 53, 53, 5444));

		// dao.saveTtcnEvent(cc1);

		parser.parse("logs/WCG100200010.txt");
		// for (TtcnEvent event : parser.getEventList()) {
		// dao.saveTtcnEvent(event);
		// }

	}
}
