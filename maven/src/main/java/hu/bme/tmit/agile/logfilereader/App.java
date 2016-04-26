package hu.bme.tmit.agile.logfilereader;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import hu.bme.tmit.agile.logfilereader.controller.Parser;
import hu.bme.tmit.agile.logfilereader.dao.Connector;
import hu.bme.tmit.agile.logfilereader.dao.ParserDAO;
import hu.bme.tmit.agile.logfilereader.model.CreatedTerminatedComponent;
import hu.bme.tmit.agile.logfilereader.model.CreatedTerminatedComponent.ComponentType;
import hu.bme.tmit.agile.logfilereader.model.LogTimestamp;
import hu.bme.tmit.agile.logfilereader.model.Message;
import hu.bme.tmit.agile.logfilereader.model.Message.MessageType;
import hu.bme.tmit.agile.logfilereader.model.TimerOperation;
import hu.bme.tmit.agile.logfilereader.model.TtcnEvent;
import hu.bme.tmit.agile.logfilereader.model.TimerOperation.EventType;
import hu.bme.tmit.agile.logfilereader.model.VerdictOperation;
import hu.bme.tmit.agile.logfilereader.model.VerdictOperation.VerdictType;

public class App {
	public static void main(String[] args) {
//		final String QUERY = "SELECT name FROM message";

//		Connector conn = new Connector();
//		conn.executeQuery(QUERY);
		
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
		
//		dao.saveTtcnEvent(m1);
		
		TimerOperation to = new TimerOperation();
		to.setDuration(333);
		to.setEventType(EventType.Start);
		to.setFileName("test1.txt");
		to.setName("valamitimer");
		to.setSender("mtc");
		to.setTimestamp(new LogTimestamp(2005, 07, 2, 4, 53, 53, 55555));
		
		//dao.saveTtcnEvent(to);
		
		VerdictOperation vo = new VerdictOperation();
		vo.setSender("mtc");
		vo.setVerdictType(VerdictType.Pass);
		
		//dao.saveTtcnEvent(vo);
		
		
		CreatedTerminatedComponent cc1 = new CreatedTerminatedComponent();
		cc1.setComponentReference(123123);
		cc1.setComponentType("asdasd");
		cc1.setCompType(ComponentType.Create);
		cc1.setFileName("test1.txt");
		cc1.setSender("mtc");
		cc1.setTestcaseName("random");
		cc1.setTimestamp(new LogTimestamp(2005, 07, 2, 4, 53, 53, 5444));
		
		//dao.saveTtcnEvent(cc1);

		parser.parse("logs/WCG100200010.txt");
		for (TtcnEvent event : parser.getEventList()) {
			dao.saveTtcnEvent(event);
		}
		
	}
}
