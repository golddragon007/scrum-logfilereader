package util;

import java.util.TreeSet;

import hu.bme.tmit.agile.logfilereader.model.Message;
import hu.bme.tmit.agile.logfilereader.model.TtcnEvent;
import hu.bme.tmit.agile.logfilereader.model.VerdictOperation;

public class PlantUmlConverter {

	public static String convert(TreeSet<TtcnEvent> eventSet) {
		String plantUmlString = "@startuml\n";
		int limit = 50;
		int i = 0;

		for (TtcnEvent event : eventSet) {
			if (event instanceof Message) {
				if (!event.getSender().contains(":")) {
					plantUmlString += event.getSender() + " -> " + ((Message) event).getDestination() + " : "
							+ "Simple message \n";
					i++;
				}
			}
			if (event instanceof VerdictOperation) {
				plantUmlString += event.getSender() + " -> " + event.getSender() + " : "
						+ ((VerdictOperation) event).getVerdictType().toString() + "\n";
				i++;
			}
			if (i > limit) {
				break;
			}
		}
		plantUmlString += "@enduml\n";

		return plantUmlString;
	}

}
